<template>
  <q-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    :maximized="true"
    transition-show="slide-up"
    transition-hide="slide-down"
    class="pd-detail-dialog"
  >
    <q-card class="pd-card">
      <div class="accent-bar"></div>

      <q-card-section class="pd-header q-px-lg q-py-lg">
        <div class="row items-center justify-between no-wrap q-col-gutter-md">
          <div class="row items-center q-gutter-md no-wrap">
            <q-avatar size="60px" rounded class="pd-avatar">
              <q-icon name="shopping_cart" size="30px" color="#2E7D32" />
            </q-avatar>
            <div class="min-width-0">
              <div class="pd-title">Pedido #{{ pedido?.numero }}</div>
              <div class="pd-eyebrow">
                <q-icon name="storefront" size="13px" class="q-mr-xs" />
                {{ pedido?.proveedor }}
              </div>
            </div>
          </div>
          <div class="row items-center q-gutter-sm no-wrap">
            <q-chip
              :color="colorEstado(pedido?.estado)"
              text-color="white"
              size="12px"
              dense
              class="chip-header"
            >
              {{ (pedido?.estado || '').replace(/_/g, ' ') }}
            </q-chip>
            <q-btn icon="close" flat round dense class="close-btn" v-close-popup />
          </div>
        </div>
      </q-card-section>

      <q-card-section class="pd-body q-pa-lg q-pt-md">
        <!-- Información del pedido -->
        <div class="info-card">
          <div class="section-title-box">
            <div class="section-icon"><q-icon name="info_outline" size="16px" /></div>
            <span>Información del pedido</span>
          </div>
          <div class="info-grid">
            <div class="info-cell wide">
              <div class="cell-icon"><q-icon name="storefront" size="17px" /></div>
              <div class="cell-body">
                <div class="cell-label">Proveedor</div>
                <div class="cell-value">{{ pedido?.proveedor || '—' }}</div>
              </div>
            </div>
            <div class="info-cell">
              <div class="cell-icon"><q-icon name="event" size="17px" /></div>
              <div class="cell-body">
                <div class="cell-label">Fecha pedido</div>
                <div class="cell-value">{{ formatFecha(pedido?.fechaPedido) }}</div>
              </div>
            </div>
            <div class="info-cell">
              <div class="cell-icon"><q-icon name="event_available" size="17px" /></div>
              <div class="cell-body">
                <div class="cell-label">Entrega estimada</div>
                <div class="cell-value">{{ pedido?.fechaEntregaEstimada ? formatFecha(pedido.fechaEntregaEstimada) : '—' }}</div>
              </div>
            </div>
            <div class="info-cell">
              <div class="cell-icon"><q-icon name="person" size="17px" /></div>
              <div class="cell-body">
                <div class="cell-label">Usuario</div>
                <div class="cell-value">{{ pedido?.usuario || '—' }}</div>
              </div>
            </div>
            <div class="info-cell wide">
              <div class="cell-icon"><q-icon name="description" size="17px" /></div>
              <div class="cell-body">
                <div class="cell-label">Observación</div>
                <div class="cell-value">{{ pedido?.observacion || 'Sin observación' }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Productos solicitados -->
        <div class="info-card">
          <div class="section-title-box">
            <div class="section-icon"><q-icon name="inventory_2" size="16px" /></div>
            <span>Productos solicitados</span>
            <q-chip v-if="pedido?.detalles?.length" size="11px" color="green-8" text-color="white" dense class="q-ml-sm">
              {{ pedido.detalles.length }}
            </q-chip>
          </div>

          <template v-if="pedido?.detalles?.length">
            <div class="pres-list">
              <div v-for="d in pedido.detalles" :key="d.idDetalle" class="pres-row">
                <div class="pres-icon">
                  <q-icon name="layers" size="20px" />
                </div>
                <div class="pres-info">
                  <div class="pres-nombre">{{ d.producto }}</div>
                  <div class="pres-meta">
                    <span class="meta-tag">
                      <q-icon name="straighten" size="12px" /> {{ d.unidad || '—' }}
                    </span>
                    <span class="meta-tag">
                      <q-icon name="swap_vert" size="12px" /> factor x {{ d.factor ?? '—' }}
                    </span>
                    <span class="meta-tag">
                      <q-icon name="inventory_2" size="12px" /> {{ d.cantidad }}
                    </span>
                  </div>
                  <div class="pres-codigos">
                    <q-icon name="calculate" size="12px" /> {{ d.equivalencia || '—' }}
                  </div>
                </div>
              </div>
            </div>
          </template>
          <div v-else class="pd-empty">
            <q-icon name="inventory_2" size="34px" color="green-6" />
            <div>Este pedido no tiene productos registrados</div>
          </div>
        </div>

        <!-- Seguimiento -->
        <div class="info-card">
          <div class="section-title-box">
            <div class="section-icon"><q-icon name="timeline" size="16px" /></div>
            <span>Seguimiento</span>
          </div>
          <q-timeline color="green-7" side="left">
            <q-timeline-entry
              v-for="item in timelineItems"
              :key="item.label"
              :icon="item.icono"
              :color="item.color"
              :title="item.label"
              :subtitle="item.subtitle"
              :side="'right'"
            />
          </q-timeline>
        </div>
      </q-card-section>

      <q-separator />

      <q-card-actions align="right" class="q-pa-md pd-actions">
        <q-btn flat label="Cerrar" color="green-8" no-caps class="btn-cerrar" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  pedido: { type: Object, default: null }
})

defineEmits(['update:modelValue'])

const estadosColor = {
  BORRADOR: 'grey-6',
  ENVIADO: 'blue-6',
  PARCIALMENTE_RECIBIDO: 'orange-6',
  COMPLETADO: 'positive',
  CANCELADO: 'negative'
}

const colorEstado = (estado) => estadosColor[estado] || 'grey-6'

function formatFecha(fecha) {
  if (!fecha) return '—'
  const d = new Date(fecha)
  if (isNaN(d.getTime())) return fecha
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}

const timelineItems = computed(() => {
  if (!props.pedido) return []
  const estado = props.pedido.estado
  const fecha = (dias) => {
    const d = new Date(props.pedido.fechaPedido)
    d.setDate(d.getDate() + dias)
    return formatFecha(d.toISOString().slice(0, 10))
  }

  if (estado === 'CANCELADO') {
    return [
      { label: 'PEDIDO CREADO', subtitle: fecha(0), icono: 'check_circle', color: 'green-6' },
      { label: 'CANCELADO', subtitle: fecha(1), icono: 'cancel', color: 'negative' }
    ]
  }

  if (estado === 'PARCIALMENTE_RECIBIDO') {
    return [
      { label: 'ENVIADO', subtitle: fecha(1), icono: 'send', color: 'green-6' },
      { label: 'PARCIALMENTE RECIBIDO', subtitle: fecha(3), icono: 'hourglass_bottom', color: 'orange-6' },
      { label: 'PENDIENTE', subtitle: 'A la espera de la recepción completa', icono: 'schedule', color: 'grey-6' }
    ]
  }

  const items = [
    { label: 'PEDIDO CREADO', subtitle: fecha(0), icono: 'check_circle', color: 'green-6' }
  ]
  if (estado === 'ENVIADO' || estado === 'COMPLETADO') {
    items.push({ label: 'PEDIDO CONFIRMADO', subtitle: fecha(0), icono: 'task_alt', color: 'green-6' })
    items.push({ label: 'ENVIADO', subtitle: fecha(1), icono: 'send', color: 'blue-6' })
  }
  if (estado === 'COMPLETADO') {
    items.push({ label: 'RECEPCIÓN', subtitle: fecha(3), icono: 'inventory_2', color: 'green-6' })
    items.push({ label: 'COMPLETADO', subtitle: fecha(4), icono: 'check_circle', color: 'positive' })
  }
  return items
})
</script>

<style scoped>
.pd-detail-dialog :deep(.q-dialog__backdrop) {
  background: rgba(16, 60, 22, 0.5) !important;
}

.pd-card {
  max-width: 820px;
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
.pd-header {
  background: linear-gradient(135deg, #14421a, #1B5E20 55%, #2E7D32);
  position: relative;
  flex: none;
}

.pd-header::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(600px 160px at 100% 0%, rgba(129, 199, 132, 0.22), transparent 60%);
  pointer-events: none;
}

.pd-avatar {
  background: #ffffff;
  border: 2px solid rgba(165, 214, 167, 0.6);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.18);
}

.min-width-0 {
  min-width: 0;
}

.pd-title {
  font-size: 20px;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: 0.2px;
  line-height: 1.2;
  word-break: break-word;
}

.pd-eyebrow {
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

/* ===== Cuerpo ===== */
.pd-body {
  background: #f4f8f2;
  overflow-y: auto;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 14px;
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

/* Lista de productos */
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

.pd-empty {
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
.pd-actions {
  flex: none;
  background: #ffffff;
}

.btn-cerrar {
  font-weight: 700;
}
</style>