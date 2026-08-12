<template>
  <q-drawer
    :model-value="modelValue"
    side="right"
    overlay
    :width="440"
    class="pv-drawer"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <q-scroll-area class="fit pv-scroll">
      <div v-if="proveedor" class="pv-drawer-body">
        <!-- Cabecera -->
        <div class="pv-hero">
          <div class="pv-hero-blur"></div>
          <q-btn flat round dense icon="close" size="sm" class="pv-close" @click="$emit('update:modelValue', false)" />
          <div class="pv-hero-top">
            <q-avatar :color="proveedor.color" text-color="white" size="72px" font-size="26px" class="pv-logo">
              {{ initials(proveedor.nombre) }}
            </q-avatar>
            <div class="pv-hero-chips">
              <q-chip
                :color="proveedor.tipo === 'Empresa' ? 'blue-8' : 'purple-9'"
                text-color="white"
                dense
                class="pv-tipo-chip"
              >
                <q-icon :name="proveedor.tipo === 'Empresa' ? 'apartment' : 'person'" size="13px" />
                {{ proveedor.tipo }}
              </q-chip>
              <q-chip
                :color="proveedor.estado === 'Activo' ? 'green-7' : 'grey-6'"
                text-color="white"
                dense
                class="pv-estado-chip"
              >
                <q-icon :name="proveedor.estado === 'Activo' ? 'check_circle' : 'block'" size="13px" />
                {{ proveedor.estado }}
              </q-chip>
            </div>
          </div>
          <div class="pv-hero-nombre">{{ proveedor.nombre }}</div>
          <div class="pv-hero-doc">
            <q-icon :name="proveedor.tipo === 'Empresa' ? 'badge' : 'credit_card'" size="13px" />
            {{ proveedor.documento.tipo }} · <strong>{{ proveedor.documento.numero || '—' }}</strong>
          </div>
        </div>

        <!-- Información general -->
        <div class="pv-card">
          <div class="pv-seccion-titulo"><q-icon name="info" size="15px" /> Información general</div>
          <div class="pv-grid">
            <div class="pv-grid-item">
              <div class="pv-grid-label">Tipo de proveedor</div>
              <div class="pv-grid-val">{{ proveedor.tipo }}</div>
            </div>
            <div class="pv-grid-item">
              <div class="pv-grid-label">Categoría</div>
              <div class="pv-grid-val">{{ proveedor.categoria }}</div>
            </div>
            <div class="pv-grid-item">
              <div class="pv-grid-label">Documento</div>
              <div class="pv-grid-val">{{ proveedor.documento.tipo }} {{ proveedor.documento.numero || '—' }}</div>
            </div>
            <div class="pv-grid-item">
              <div class="pv-grid-label">Ciudad / Zona</div>
              <div class="pv-grid-val">{{ proveedor.ciudad || '—' }}{{ proveedor.zona ? ' · ' + proveedor.zona : '' }}</div>
            </div>
          </div>
        </div>

        <!-- Contacto -->
        <div v-if="tieneContacto" class="pv-card">
          <div class="pv-seccion-titulo"><q-icon name="contact_phone" size="15px" /> Contacto</div>
          <div class="pv-contact-list">
            <div v-if="proveedor.contacto.nombre" class="pv-contact-row">
              <span class="pv-contact-ic" style="background:#e8f0fe;color:#1967d2;"><q-icon name="person" size="16px" /></span>
              <div>
                <div class="pv-contact-label">Persona de contacto</div>
                <div class="pv-contact-val">{{ proveedor.contacto.nombre }}</div>
              </div>
            </div>

            <template v-if="proveedor.contactos && proveedor.contactos.length">
              <div v-for="(c, i) in proveedor.contactos" :key="i" class="pv-contacto-bloque">
                <div v-if="c.telefono" class="pv-contact-row">
                  <span class="pv-contact-ic" style="background:#e6f7e9;color:#2E7D32;"><q-icon name="phone" size="16px" /></span>
                  <div>
                    <div class="pv-contact-label">Teléfono</div>
                    <div class="pv-contact-val">{{ c.telefono }}</div>
                  </div>
                </div>
                <div v-if="c.correo" class="pv-contact-row">
                  <span class="pv-contact-ic" style="background:#fef3e2;color:#d97b1a;"><q-icon name="email" size="16px" /></span>
                  <div>
                    <div class="pv-contact-label">Correo</div>
                    <div class="pv-contact-val">{{ c.correo }}</div>
                  </div>
                </div>
              </div>
            </template>
            <template v-else>
              <div v-if="proveedor.contacto.telefono" class="pv-contact-row">
                <span class="pv-contact-ic" style="background:#e6f7e9;color:#2E7D32;"><q-icon name="phone" size="16px" /></span>
                <div>
                  <div class="pv-contact-label">Teléfono</div>
                  <div class="pv-contact-val">{{ proveedor.contacto.telefono }}</div>
                </div>
              </div>
              <div v-if="proveedor.contacto.correo" class="pv-contact-row">
                <span class="pv-contact-ic" style="background:#fef3e2;color:#d97b1a;"><q-icon name="email" size="16px" /></span>
                <div>
                  <div class="pv-contact-label">Correo</div>
                  <div class="pv-contact-val">{{ proveedor.contacto.correo }}</div>
                </div>
              </div>
            </template>
          </div>
        </div>

        <!-- Condiciones comerciales -->
        <div v-if="tieneCondiciones" class="pv-card">
          <div class="pv-seccion-titulo"><q-icon name="payments" size="15px" /> Condiciones comerciales</div>
          <div class="pv-cond-grid">
            <div v-for="cond in condicionesLista" :key="cond.label" class="pv-cond-item">
              <span class="pv-contact-ic" style="background:#f1f8e9;color:#2E7D32;"><q-icon :name="cond.icon" size="16px" /></span>
              <div>
                <div class="pv-contact-label">{{ cond.label }}</div>
                <div class="pv-contact-val">{{ cond.valor }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Productos que provee -->
        <div v-if="proveedor.productos && proveedor.productos.length" class="pv-card">
          <div class="pv-seccion-titulo"><q-icon name="inventory_2" size="15px" /> Productos que provee</div>
          <div class="pv-prod-chips">
            <q-chip v-for="prod in proveedor.productos" :key="prod" dense size="12px" class="pv-prod-chip" icon="check_circle">
              {{ prod }}
            </q-chip>
          </div>
        </div>

        <!-- Compras -->
        <div class="pv-card">
          <div class="pv-seccion-titulo"><q-icon name="shopping_cart" size="15px" /> Compras</div>
          <div class="pv-mini-kpis">
            <div class="pv-mini">
              <div class="pv-mini-num">{{ proveedor.comprasMes }}</div>
              <div class="pv-mini-label">Compras del mes</div>
            </div>
            <div class="pv-mini">
              <div class="pv-mini-num">{{ proveedor.totalComprado }}</div>
              <div class="pv-mini-label">Total comprado</div>
            </div>
          </div>
        </div>

        <!-- Historial -->
        <div v-if="proveedor.historial && proveedor.historial.length" class="pv-card">
          <div class="pv-seccion-titulo"><q-icon name="history" size="15px" /> Historial de compras</div>
          <div class="pv-hist-list">
            <div v-for="(c, i) in proveedor.historial" :key="i" class="pv-hist-row">
              <div class="pv-hist-ic">
                <q-icon name="receipt_long" size="16px" />
              </div>
              <div class="pv-hist-meta">
                <div class="pv-hist-fecha">{{ c.fecha }}</div>
                <div class="pv-hist-det">{{ c.producto }} · {{ c.orden }}</div>
              </div>
              <div class="pv-hist-monto">{{ c.monto }}</div>
            </div>
          </div>
        </div>

        <!-- Acciones -->
        <div class="pv-footer-actions">
          <q-btn class="pv-btn-editar" no-caps label="Editar proveedor" icon="edit" @click="$emit('editar', proveedor)" />
        </div>
      </div>
    </q-scroll-area>
  </q-drawer>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  proveedor: { type: Object, default: null }
})

defineEmits(['update:modelValue', 'editar', 'historial', 'descargar-doc'])

function initials(nombre) {
  return (nombre || '')
    .split(' ')
    .filter((p) => p.length > 0)
    .slice(0, 2)
    .map((p) => p[0].toUpperCase())
    .join('')
}

const tieneContacto = computed(() => {
  const c = props.proveedor || {}
  const contacto = c.contacto || {}
  const contactos = c.contactos || []
  return Boolean(contacto.nombre || contacto.telefono || contacto.correo) || contactos.some((x) => x.telefono || x.correo)
})

const tieneCondiciones = computed(() => {
  const cond = (props.proveedor && props.proveedor.condiciones) || {}
  return Object.values(cond).some((v) => v !== undefined && v !== null && v !== '')
})

const condicionesLista = computed(() => {
  const esEmpresa = props.proveedor && props.proveedor.tipo === 'Empresa'
  const cond = (props.proveedor && props.proveedor.condiciones) || {}
  const defs = esEmpresa
    ? [
        { label: 'Forma de pago', icon: 'payments', key: 'formaPago' },
        { label: 'Moneda', icon: 'account_balance_wallet', key: 'moneda' },
        { label: 'Días de entrega', icon: 'local_shipping', key: 'diasEntrega' }
      ]
    : [
        { label: 'Forma de pago', icon: 'attach_money', key: 'formaPago' },
        { label: 'Frecuencia de entrega', icon: 'schedule', key: 'frecuenciaEntrega' },
        { label: 'Zona de procedencia', icon: 'agriculture', key: 'zona' }
      ]
  return defs
    .map((d) => ({ ...d, valor: cond[d.key] }))
    .filter((d) => d.valor !== undefined && d.valor !== null && d.valor !== '')
})
</script>

<style scoped>
.pv-drawer {
  background: #f4f7f4;
}
.pv-scroll {
  background: #f4f7f4;
}
.pv-drawer-body {
  padding: 0 16px 20px;
}

/* Hero */
.pv-hero {
  position: relative;
  margin: 0 -16px 14px;
  padding: 22px 20px 18px;
  background: linear-gradient(135deg, #1B5E20 0%, #2E7D32 55%, #43A047 100%);
  color: #ffffff;
  overflow: hidden;
}
.pv-hero-blur {
  position: absolute;
  width: 220px;
  height: 220px;
  right: -80px;
  top: -90px;
  background: radial-gradient(circle, rgba(255,255,255,0.16), transparent 70%);
  border-radius: 50%;
  pointer-events: none;
}
.pv-close {
  position: absolute;
  top: 10px;
  right: 10px;
  color: rgba(255,255,255,0.85) !important;
  background: rgba(255,255,255,0.14) !important;
  z-index: 2;
}
.pv-close:hover {
  background: rgba(255,255,255,0.28) !important;
}
.pv-hero-top {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  gap: 14px;
}
.pv-logo {
  border-radius: 16px !important;
  font-family: 'DM Sans', 'Nunito', sans-serif;
  font-weight: 700;
  box-shadow: 0 6px 18px rgba(0,0,0,0.22);
  border: 2px solid rgba(255,255,255,0.35);
}
.pv-hero-chips {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
  padding-top: 8px;
}
.pv-tipo-chip,
.pv-estado-chip {
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 11px;
  border-radius: 999px !important;
}
.pv-hero-nombre {
  text-align: center;
  font-family: 'DM Sans', 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 20px;
  margin-top: 12px;
  line-height: 1.25;
}
.pv-hero-doc {
  text-align: center;
  font-family: 'Nunito', sans-serif;
  font-size: 12.5px;
  color: rgba(255,255,255,0.9);
  margin-top: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

/* Cards */
.pv-card {
  background: #ffffff;
  border: 1px solid #e0ebe1;
  border-radius: 14px;
  padding: 14px 16px;
  margin-bottom: 12px;
  box-shadow: 0 1px 4px rgba(27,94,32,0.05);
}
.pv-seccion-titulo {
  display: flex;
  align-items: center;
  gap: 6px;
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: #2E7D32;
  margin-bottom: 12px;
}

.pv-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.pv-grid-item {
  background: #f7faf6;
  border: 1px solid #e4eee5;
  border-radius: 10px;
  padding: 9px 11px;
}
.pv-grid-label {
  font-size: 10.5px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.03em;
  color: #7f9a82;
  font-family: 'Nunito', sans-serif;
}
.pv-grid-val {
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 13px;
  color: #1B5E20;
  margin-top: 2px;
}

/* Contacto */
.pv-contact-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.pv-contact-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.pv-contact-ic {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.pv-contact-label {
  font-size: 10.5px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.03em;
  color: #8a9a90;
  font-family: 'Nunito', sans-serif;
}
.pv-contact-val {
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 13px;
  color: #16231c;
}
.pv-contacto-bloque {
  background: #f7faf6;
  border: 1px solid #e4eee5;
  border-radius: 10px;
  padding: 9px 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.pv-contacto-bloque + .pv-contacto-bloque {
  margin-top: 2px;
}

/* Condiciones */
.pv-cond-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.pv-cond-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* Productos */
.pv-prod-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.pv-prod-chip {
  font-family: 'Nunito', sans-serif;
  font-weight: 700;
  background: #E7F6E9 !important;
  color: #2E7D32 !important;
}

/* Mini KPI compras */
.pv-mini-kpis {
  display: flex;
  gap: 10px;
}
.pv-mini {
  flex: 1;
  background: #f7faf6;
  border: 1px solid #e4eee5;
  border-radius: 12px;
  padding: 12px 10px;
  text-align: center;
}
.pv-mini-num {
  font-family: 'DM Sans', 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 18px;
  color: #2E7D32;
}
.pv-mini-label {
  font-size: 10.5px;
  color: #7a8a80;
  font-family: 'Nunito', sans-serif;
  font-weight: 700;
  margin-top: 2px;
}

/* Historial */
.pv-hist-list {
  display: flex;
  flex-direction: column;
  gap: 7px;
}
.pv-hist-row {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #f7faf6;
  border: 1px solid #eef2f0;
  border-radius: 10px;
  padding: 9px 12px;
}
.pv-hist-ic {
  width: 32px;
  height: 32px;
  border-radius: 9px;
  background: #E7F6E9;
  color: #2E7D32;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.pv-hist-meta {
  flex: 1;
  min-width: 0;
}
.pv-hist-fecha {
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 12.5px;
  color: #33443a;
}
.pv-hist-det {
  font-size: 11px;
  color: #8a9a90;
  font-family: 'Nunito', sans-serif;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.pv-hist-monto {
  font-family: 'DM Sans', 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 13px;
  color: #2E7D32;
}

/* Acciones */
.pv-footer-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 4px;
}
.pv-btn-editar {
  width: 100%;
  background: #2E7D32 !important;
  color: #ffffff !important;
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  border-radius: 10px !important;
  padding: 10px !important;
}
</style>