<template>
  <q-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    :maximized="maximized"
    transition-show="slide-up"
    transition-hide="slide-down"
    class="prod-detail-dialog"
  >
    <q-card class="prod-card">
      <div class="accent-bar"></div>

      <q-card-section class="prod-header q-px-lg q-py-lg">
        <div class="row items-center justify-between no-wrap q-col-gutter-md">
          <div class="row items-center q-gutter-md no-wrap">
            <q-avatar size="60px" rounded class="prod-avatar">
              <q-icon name="inventory_2" size="30px" color="#2E7D32" />
            </q-avatar>
            <div class="min-width-0">
              <div class="prod-title">{{ producto.nombre }}</div>
              <div class="prod-eyebrow">
                <q-icon name="qr_code" size="13px" class="q-mr-xs" />
                {{ producto.codigo }}
              </div>
            </div>
          </div>
          <div class="row items-center q-gutter-sm no-wrap">
            <q-chip
              :class="producto.estado === 'Activo' ? 'chip-estado-activo' : 'chip-estado-inactivo'"
              :label="producto.estado"
              size="12px"
              dense
              class="chip-header"
            />
            <q-btn icon="close" flat round dense class="close-btn" v-close-popup />
          </div>
        </div>
      </q-card-section>

      <q-card-section class="prod-body q-pa-lg q-pt-md">
        <!-- Imagen completa del producto -->
        <div v-if="producto.imagen" class="prod-img-wrap">
          <q-img :src="urlImagen(producto.imagen)" fit="contain" class="prod-imagen" />
        </div>

        <!-- Información general -->
        <div class="info-card">
          <div class="section-title-box">
            <div class="section-icon"><q-icon name="info_outline" size="16px" /></div>
            <span>Información general</span>
          </div>
          <div class="info-grid">
            <div class="info-cell wide">
              <div class="cell-icon"><q-icon name="description" size="17px" /></div>
              <div class="cell-body">
                <div class="cell-label">Descripción</div>
                <div class="cell-value">{{ producto.descripcion || 'Sin descripción' }}</div>
              </div>
            </div>
            <div class="info-cell">
              <div class="cell-icon"><q-icon name="category" size="17px" /></div>
              <div class="cell-body">
                <div class="cell-label">Categoría</div>
                <div class="cell-value">{{ producto.categoria }}</div>
                <div class="cell-sub">{{ producto.subcategoria }}</div>
              </div>
            </div>
            <div class="info-cell">
              <div class="cell-icon"><q-icon name="factory" size="17px" /></div>
              <div class="cell-body">
                <div class="cell-label">Marca</div>
                <div class="cell-value">{{ producto.marca || '—' }}</div>
              </div>
            </div>
            <div class="info-cell">
              <div class="cell-icon"><q-icon name="straighten" size="17px" /></div>
              <div class="cell-body">
                <div class="cell-label">Unidad base</div>
                <div class="cell-value">{{ producto.unidadBase }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Configuración -->
        <div class="info-card">
          <div class="section-title-box">
            <div class="section-icon"><q-icon name="tune" size="16px" /></div>
            <span>Configuración</span>
          </div>
          <div class="info-grid two-cols">
            <div class="info-cell">
              <div class="cell-icon"><q-icon name="batch_prediction" size="17px" /></div>
              <div class="cell-body">
                <div class="cell-label">Controla lote</div>
                <div class="toggle-pill" :class="producto.controlaLote ? 'on' : 'off'">
                  <q-icon :name="producto.controlaLote ? 'check' : 'close'" size="13px" />
                  {{ producto.controlaLote ? 'Sí' : 'No' }}
                </div>
              </div>
            </div>
            <div class="info-cell">
              <div class="cell-icon"><q-icon name="event" size="17px" /></div>
              <div class="cell-body">
                <div class="cell-label">Controla vencimiento</div>
                <div class="toggle-pill" :class="producto.controlaVencimiento ? 'on' : 'off'">
                  <q-icon :name="producto.controlaVencimiento ? 'check' : 'close'" size="13px" />
                  {{ producto.controlaVencimiento ? 'Sí' : 'No' }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Presentaciones -->
        <div class="info-card">
          <div class="section-title-box">
            <div class="section-icon"><q-icon name="layers_outlined" size="16px" /></div>
            <span>Presentaciones de venta</span>
            <q-chip
              v-if="presentacionesVisibles.length"
              size="11px"
              color="green-8"
              text-color="white"
              dense
              class="q-ml-sm"
            >
              {{ presentacionesVisibles.length }}
            </q-chip>
          </div>

          <template v-if="presentacionesVisibles.length">
            <div class="pres-list">
              <div v-for="p in presentacionesVisibles" :key="p.key" class="pres-row" :class="{ principal: p.principal }">
                <div class="pres-icon">
                  <q-icon name="layers" size="20px" />
                </div>
                <div class="pres-info">
                  <div class="pres-nombre">
                    {{ p.nombre }}
                    <q-chip v-if="p.principal" size="10px" color="green-7" text-color="white" class="q-ml-sm">Principal</q-chip>
                  </div>
                  <div class="pres-meta">
                    <span v-if="p.unidad" class="meta-tag">
                      <q-icon name="straighten" size="12px" /> {{ p.unidad }}
                    </span>
                    <span v-if="p.factor" class="meta-tag">
                      <q-icon name="swap_vert" size="12px" /> factor x {{ p.factor }}
                    </span>
                  </div>
                  <div v-if="p.codigos.length" class="pres-codigos">
                    <q-icon name="barcode_reader" size="12px" /> {{ p.codigos.join('   ·   ') }}
                  </div>
                </div>
                <div class="pres-precio">
                  <span class="pres-precio-moneda">Bs</span>
                  <span class="pres-precio-valor">{{ formatearPrecio(p.precio) }}</span>
                </div>
              </div>
            </div>
          </template>
          <div v-else class="prod-empty">
            <q-icon name="check_circle_outline" size="34px" color="green-6" />
            <div>Este producto se vende únicamente por su unidad base ({{ producto.unidadBase }})</div>
          </div>
        </div>
      </q-card-section>

      <q-separator />

      <q-card-actions align="right" class="q-pa-md prod-actions">
        <q-btn flat label="Cerrar" color="green-8" no-caps class="btn-cerrar" v-close-popup />
        <q-btn
          unelevated
          color="green-8"
          icon="edit"
          label="Editar producto"
          no-caps
          class="btn-editar"
          v-close-popup
          @click="$emit('editar')"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  producto: { type: Object, default: null }
})

const emit = defineEmits(['update:modelValue', 'editar'])

const maximized = computed(() => {
  const p = props.producto
  return p && p.presentaciones && p.presentaciones.length > 0
})

function urlImagen(ruta) {
  if (!ruta) return ''
  if (/^https?:\/\//i.test(ruta)) return ruta
  if (ruta.startsWith('/')) return import.meta.env.VITE_API_URL + ruta
  return import.meta.env.VITE_API_URL + '/uploads/' + ruta
}

const presentacionesVisibles = computed(() => {
  const p = props.producto
  if (!p || !Array.isArray(p.presentaciones)) return []
  return p.presentaciones.map((item, i) => {
    const codigos = (Array.isArray(item.codigosBarra) ? item.codigosBarra : [])
      .map((c) => c.codigo)
      .filter(Boolean)
    return {
      key: item.idPresentacion ?? i,
      nombre: item.nombrePresentacion || `Presentación ${i + 1}`,
      unidad: item.nombreUnidadMedida || '',
      factor: item.factorConversion,
      precio: item.precioVenta,
      principal: !!item.esPrincipal,
      codigos
    }
  })
})

function formatearPrecio(valor) {
  const n = Number(valor)
  return isFinite(n) ? n.toFixed(2) : ''
}
</script>

<style scoped>
.prod-detail-dialog :deep(.q-dialog__backdrop) {
  background: rgba(16, 60, 22, 0.5) !important;
}

.prod-card {
  max-width: 780px;
  width: 100%;
  max-height: 93vh;
  display: flex;
  flex-direction: column;
  border-radius: 18px;
  margin: 0 auto;
  box-shadow: 0 24px 60px rgba(15, 62, 22, 0.35), 0 4px 16px rgba(15, 62, 22, 0.18);
  overflow: hidden;
}

.accent-bar {
  height: 5px;
  background: linear-gradient(90deg, #1B5E20, #2E7D32, #81C784, #A5D6A7);
  flex: none;
}

/* ===== Encabezado ===== */
.prod-header {
  background: linear-gradient(135deg, #14421a, #1B5E20 55%, #2E7D32);
  position: relative;
  flex: none;
}

.prod-header::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(600px 160px at 100% 0%, rgba(129, 199, 132, 0.22), transparent 60%);
  pointer-events: none;
}

.prod-avatar {
  background: #ffffff;
  border: 2px solid rgba(165, 214, 167, 0.6);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.18);
}

.min-width-0 {
  min-width: 0;
}

.prod-title {
  font-size: 20px;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: 0.2px;
  line-height: 1.2;
  word-break: break-word;
}

.prod-eyebrow {
  font-size: 11.5px;
  font-weight: 700;
  letter-spacing: 1.2px;
  color: #C8E6C9;
  text-transform: uppercase;
  display: flex;
  align-items: center;
  margin-top: 3px;
}

.chip-header {
  position: relative;
  z-index: 1;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.close-btn {
  color: #E8F5E9;
  position: relative;
  z-index: 1;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.15);
}

.chip-estado-activo {
  background: #E7F6E9 !important;
  color: #2E7D32 !important;
  font-weight: 700;
}

.chip-estado-inactivo {
  background: #E6E6E6 !important;
  color: #5B6675 !important;
  font-weight: 700;
}

/* ===== Cuerpo ===== */
.prod-body {
  background: #f4f8f2;
  overflow-y: auto;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* Imagen */
.prod-img-wrap {
  background: #ffffff;
  border: 1px solid #dce9d6;
  border-radius: 14px;
  padding: 10px;
  box-shadow: 0 2px 8px rgba(27, 94, 32, 0.06);
}

.prod-imagen {
  height: 240px;
  width: 100%;
  border-radius: 10px;
  background: #f7fbf4;
}

/* Tarjetas de sección */
.info-card {
  background: #ffffff;
  border: 1px solid #e2ecdd;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 2px 10px rgba(27, 94, 32, 0.05);
}

.section-title-box {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 14px;
  font-size: 14.5px;
  font-weight: 800;
  color: #1B5E20;
  letter-spacing: 0.3px;
}

.section-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  background: linear-gradient(135deg, #E8F5E9, #C8E6C9);
  color: #2E7D32;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: none;
}

/* Grid info */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.info-grid.two-cols {
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}

.info-cell {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  background: #f8fbf6;
  border: 1px solid #edf4e8;
  border-radius: 12px;
  padding: 13px 14px;
}

.info-cell.wide {
  grid-column: 1 / -1;
}

.cell-icon {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: linear-gradient(135deg, #E8F5E9, #C8E6C9);
  color: #2E7D32;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: none;
}

.cell-body {
  min-width: 0;
}

.cell-label {
  font-size: 10.5px;
  font-weight: 800;
  color: #7d9c7d;
  text-transform: uppercase;
  letter-spacing: 0.8px;
  margin-bottom: 3px;
}

.cell-value {
  font-size: 14.5px;
  font-weight: 700;
  color: #1B5E20;
  line-height: 1.35;
  word-break: break-word;
}

.cell-sub {
  font-size: 12px;
  font-weight: 600;
  color: #66A35F;
  margin-top: 2px;
}

/* Toggle pill */
.toggle-pill {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 12.5px;
  font-weight: 800;
  border-radius: 999px;
  padding: 4px 12px;
  margin-top: 4px;
}

.toggle-pill.on {
  background: #E7F6E9;
  color: #2E7D32;
  border: 1px solid #A5D6A7;
}

.toggle-pill.off {
  background: #F0F0F0;
  color: #7A8280;
  border: 1px solid #D9DDD9;
}

/* Lista de presentaciones */
.pres-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.pres-row {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #FBFDF8;
  border: 1px solid #E4EEE0;
  border-radius: 12px;
  padding: 14px;
  transition: box-shadow 0.2s ease, border-color 0.2s ease;
}

.pres-row:hover {
  border-color: #A5D6A7;
  box-shadow: 0 3px 12px rgba(27, 94, 32, 0.08);
}

.pres-row.principal {
  border-color: #81C784;
  background: linear-gradient(135deg, #F1F8E9, #E8F5E9);
}

.pres-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: linear-gradient(135deg, #E8F5E9, #C8E6C9);
  color: #2E7D32;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: none;
}

.pres-info {
  min-width: 0;
  flex: 1;
}

.pres-nombre {
  font-size: 15px;
  font-weight: 800;
  color: #1B5E20;
  display: flex;
  align-items: center;
  word-break: break-word;
}

.pres-meta {
  display: flex;
  gap: 8px;
  margin-top: 6px;
  flex-wrap: wrap;
}

.meta-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 700;
  color: #55924F;
  background: #F1F8E9;
  border: 1px solid #D7E8D0;
  border-radius: 999px;
  padding: 2px 9px;
}

.pres-codigos {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 11.5px;
  font-weight: 600;
  color: #7C9B7C;
  margin-top: 7px;
  letter-spacing: 0.4px;
}

.pres-precio {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  flex: none;
  background: #E7F6E9;
  border: 1px solid #C8E6C9;
  border-radius: 12px;
  padding: 8px 14px;
}

.pres-precio-moneda {
  font-size: 10px;
  font-weight: 800;
  color: #2E7D32;
  letter-spacing: 1px;
}

.pres-precio-valor {
  font-family: 'DM Sans', 'Nunito', sans-serif;
  font-size: 18px;
  font-weight: 800;
  color: #1B5E20;
  line-height: 1.1;
}

.prod-empty {
  padding: 26px;
  text-align: center;
  color: #2E7D32;
  font-family: 'Nunito', sans-serif;
  font-weight: 600;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  background: #f1f8e9;
  border-radius: 12px;
  border: 1px dashed #c8e6c9;
}

/* ===== Pie ===== */
.prod-actions {
  flex: none;
  background: #ffffff;
}

.btn-cerrar {
  font-weight: 700;
}

.btn-editar {
  border-radius: 9px;
  font-weight: 700;
  box-shadow: 0 3px 10px rgba(46, 125, 50, 0.3);
}
</style>