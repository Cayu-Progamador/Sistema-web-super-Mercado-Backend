<template>
  <q-drawer
    :model-value="modelValue"
    side="right"
    overlay
    :width="420"
    class="pv-drawer"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <q-scroll-area class="fit pv-scroll">
      <div v-if="proveedor" class="pv-drawer-body">
        <div class="pv-header">
          <q-btn flat round dense icon="close" size="sm" @click="$emit('update:modelValue', false)" class="pv-close" />
          <q-avatar :color="proveedor.color" text-color="white" size="64px" font-size="24px" class="pv-logo">
            {{ initials(proveedor.nombre) }}
          </q-avatar>
          <div class="pv-header-nombre">{{ proveedor.nombre }}</div>
          <div class="pv-header-doc">
            {{ proveedor.documento.tipo }}: <strong>{{ proveedor.documento.numero }}</strong>
          </div>
          <q-chip
            :color="proveedor.tipo === 'Empresa' ? 'blue-9' : 'purple-10'"
            text-color="white"
            dense
            class="pv-tipo-chip"
          >
            <q-icon :name="proveedor.tipo === 'Empresa' ? 'apartment' : 'person'" size="14px" />
            {{ proveedor.tipo }}
          </q-chip>
        </div>

        <q-separator class="pv-sep" />

        <div class="pv-seccion">
          <div class="pv-seccion-titulo">Contacto</div>
          <div class="pv-contact-row"><q-icon name="person" size="15px" /><span>{{ proveedor.contacto.nombre }}</span></div>
          <div class="pv-contact-row"><q-icon name="phone" size="15px" /><span>{{ proveedor.contacto.telefono }}</span></div>
          <div class="pv-contact-row"><q-icon name="email" size="15px" /><span>{{ proveedor.contacto.correo }}</span></div>
          <div class="pv-contact-row"><q-icon name="place" size="15px" /><span>{{ proveedor.ciudad }}{{ proveedor.zona ? ' · ' + proveedor.zona : '' }}</span></div>
        </div>

        <div class="pv-calif-row">
          <q-rating :model-value="proveedor.calificacion" readonly size="16px" color="amber-7" icon="star" />
          <span class="pv-calif-num">{{ proveedor.calificacion.toFixed(1) }} / 5</span>
          <q-chip :color="proveedor.estado === 'Activo' ? 'green-7' : 'grey-6'" text-color="white" dense size="12px" class="pv-estado-chip">
            {{ proveedor.estado }}
          </q-chip>
        </div>

        <q-separator class="pv-sep" />

        <!-- Condiciones comerciales: Empresa -->
        <div v-if="proveedor.tipo === 'Empresa'" class="pv-seccion">
          <div class="pv-seccion-titulo">Condiciones comerciales</div>
          <div class="pv-cond-grid">
            <div class="pv-cond-item">
              <q-icon name="payments" size="18px" color="primary" />
              <div>
                <div class="pv-cond-label">Forma de pago</div>
                <div class="pv-cond-val">{{ proveedor.condiciones.formaPago }}</div>
              </div>
            </div>
            <div class="pv-cond-item">
              <q-icon name="account_balance_wallet" size="18px" color="primary" />
              <div>
                <div class="pv-cond-label">Moneda</div>
                <div class="pv-cond-val">{{ proveedor.condiciones.moneda }}</div>
              </div>
            </div>
            <div class="pv-cond-item">
              <q-icon name="local_shipping" size="18px" color="primary" />
              <div>
                <div class="pv-cond-label">Días de entrega</div>
                <div class="pv-cond-val">{{ proveedor.condiciones.diasEntrega }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Condiciones comerciales: Persona -->
        <div v-else class="pv-seccion">
          <div class="pv-seccion-titulo">Condiciones comerciales</div>
          <div class="pv-cond-grid">
            <div class="pv-cond-item">
              <q-icon name="attach_money" size="18px" color="primary" />
              <div>
                <div class="pv-cond-label">Forma de pago</div>
                <div class="pv-cond-val">{{ proveedor.condiciones.formaPago }}</div>
              </div>
            </div>
            <div class="pv-cond-item">
              <q-icon name="schedule" size="18px" color="primary" />
              <div>
                <div class="pv-cond-label">Frecuencia de entrega</div>
                <div class="pv-cond-val">{{ proveedor.condiciones.frecuenciaEntrega }}</div>
              </div>
            </div>
            <div class="pv-cond-item">
              <q-icon name="agriculture" size="18px" color="primary" />
              <div>
                <div class="pv-cond-label">Zona de procedencia</div>
                <div class="pv-cond-val">{{ proveedor.zona }}</div>
              </div>
            </div>
          </div>
        </div>

        <q-separator class="pv-sep" />

        <div class="pv-seccion">
          <div class="pv-seccion-titulo">Productos que provee</div>
          <div class="pv-prod-chips">
            <q-chip v-for="prod in proveedor.productos" :key="prod" dense size="12px" color="primary" text-color="white" class="pv-prod-chip">
              {{ prod }}
            </q-chip>
          </div>
        </div>

        <q-separator class="pv-sep" />

        <div class="pv-seccion">
          <div class="pv-seccion-titulo">Historial de compras</div>
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
          <div class="pv-hist-list">
            <div v-for="(c, i) in proveedor.historial" :key="i" class="pv-hist-row">
              <div>
                <div class="pv-hist-fecha">{{ c.fecha }}</div>
                <div class="pv-hist-det">{{ c.producto }} · {{ c.orden }}</div>
              </div>
              <div class="pv-hist-monto">{{ c.monto }}</div>
            </div>
          </div>
        </div>

        <q-separator class="pv-sep" />

        <div class="pv-seccion">
          <div class="pv-seccion-titulo">Documentos</div>
          <div v-if="proveedor.tipo === 'Empresa'">
            <div class="pv-doc-row">
              <q-icon name="description" size="18px" color="blue-8" />
              <div class="pv-doc-meta">
                <div class="pv-doc-nombre">NIT</div>
                <div class="pv-doc-tam">{{ proveedor.documento.numero }}</div>
              </div>
              <q-btn flat round dense icon="download" color="primary" @click="$emit('descargar-doc', 'nit')">
                <q-tooltip>Descargar NIT</q-tooltip>
              </q-btn>
            </div>
            <div class="pv-doc-row">
              <q-icon name="gavel" size="18px" color="blue-8" />
              <div class="pv-doc-meta">
                <div class="pv-doc-nombre">Contrato</div>
                <div class="pv-doc-tam">{{ proveedor.documento.contrato }}</div>
              </div>
              <q-btn flat round dense icon="download" color="primary" @click="$emit('descargar-doc', 'contrato')">
                <q-tooltip>Descargar contrato</q-tooltip>
              </q-btn>
            </div>
          </div>
          <div v-else>
            <div class="pv-doc-row">
              <q-icon name="badge" size="18px" color="purple-9" />
              <div class="pv-doc-meta">
                <div class="pv-doc-nombre">Cédula de Identidad</div>
                <div class="pv-doc-tam">{{ proveedor.documento.numero }}</div>
              </div>
              <q-btn flat round dense icon="download" color="purple-9" @click="$emit('descargar-doc', 'ci')">
                <q-tooltip>Descargar CI</q-tooltip>
              </q-btn>
            </div>
          </div>
        </div>

        <div class="pv-footer-actions">
          <q-btn outline color="primary" label="Editar proveedor" icon="edit" @click="$emit('editar', proveedor)" />
          <q-btn flat color="primary" label="Historial completo" icon="history" @click="$emit('historial', proveedor)" />
        </div>
      </div>
    </q-scroll-area>
  </q-drawer>
</template>

<script setup>
defineProps({
  modelValue: { type: Boolean, default: false },
  proveedor: { type: Object, default: null }
})

defineEmits(['update:modelValue', 'editar', 'historial', 'descargar-doc'])

function initials(nombre) {
  return nombre
    .split(' ')
    .filter((p) => p.length > 0)
    .slice(0, 2)
    .map((p) => p[0].toUpperCase())
    .join('')
}
</script>

<style scoped>
.pv-drawer {
  background: #f5f7fa;
}

.pv-drawer-body {
  padding: 16px;
}

.pv-header {
  text-align: center;
  padding-top: 8px;
  position: relative;
}

.pv-close {
  position: absolute;
  top: 0;
  right: 0;
}

.pv-logo {
  border-radius: 14px !important;
  font-family: 'DM Sans', 'Nunito', sans-serif;
  font-weight: 700;
  margin: 0 auto;
}

.pv-header-nombre {
  font-family: 'DM Sans', 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 18px;
  color: #16231c;
  margin-top: 10px;
}

.pv-header-doc {
  font-size: 12px;
  color: #6b7a72;
  font-family: 'Nunito', sans-serif;
  margin-top: 2px;
}

.pv-tipo-chip {
  margin-top: 8px;
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
}

.pv-sep {
  margin: 16px 0;
  color: #e6ebf1;
}

.pv-seccion-titulo {
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #7a8a80;
  margin-bottom: 10px;
}

.pv-contact-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-family: 'Nunito', sans-serif;
  font-size: 13px;
  color: #33443a;
  padding: 3px 0;
}

.pv-contact-row .q-icon {
  color: #8a9a90;
}

.pv-calif-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
}

.pv-calif-num {
  font-family: 'DM Sans', 'Nunito', sans-serif;
  font-weight: 700;
  font-size: 13px;
  color: #33443a;
}

.pv-estado-chip {
  margin-left: auto;
}

.pv-cond-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.pv-cond-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pv-cond-label {
  font-size: 11px;
  color: #7a8a80;
  font-family: 'Nunito', sans-serif;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.03em;
}

.pv-cond-val {
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 13px;
  color: #16231c;
}

.pv-prod-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.pv-prod-chip {
  font-family: 'Nunito', sans-serif;
  font-weight: 700;
}

.pv-mini-kpis {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}

.pv-mini {
  flex: 1;
  background: #ffffff;
  border: 1px solid #e6ebf1;
  border-radius: 12px;
  padding: 10px;
  text-align: center;
}

.pv-mini-num {
  font-family: 'DM Sans', 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 16px;
  color: #2E7D32;
}

.pv-mini-label {
  font-size: 10.5px;
  color: #7a8a80;
  font-family: 'Nunito', sans-serif;
  font-weight: 700;
}

.pv-hist-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.pv-hist-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  border: 1px solid #eef2f0;
  border-radius: 10px;
  padding: 8px 12px;
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
}

.pv-hist-monto {
  font-family: 'DM Sans', 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 13px;
  color: #2E7D32;
}

.pv-doc-row {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #ffffff;
  border: 1px solid #eef2f0;
  border-radius: 10px;
  padding: 10px 12px;
  margin-bottom: 8px;
}

.pv-doc-meta {
  flex: 1;
}

.pv-doc-nombre {
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 12.5px;
  color: #33443a;
}

.pv-doc-tam {
  font-size: 11px;
  color: #8a9a90;
  font-family: 'Nunito', sans-serif;
}

.pv-footer-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 4px;
}

.pv-footer-actions .q-btn {
  width: 100%;
}
</style>
