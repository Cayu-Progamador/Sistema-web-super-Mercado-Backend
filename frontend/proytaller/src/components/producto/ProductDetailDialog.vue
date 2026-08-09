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

      <q-card-section class="prod-header q-px-lg q-py-md">
        <div class="row items-center justify-between no-wrap">
          <div class="row items-center q-gutter-sm no-wrap">
            <q-avatar size="56px" rounded class="prod-detail-imagen">
              <q-img v-if="producto.imagen" :src="producto.imagen" fit="cover" class="full-height full-width" />
              <q-icon v-else name="inventory_2" size="30px" />
            </q-avatar>
            <div>
              <div class="prod-title">{{ producto.nombre }}</div>
              <div class="prod-eyebrow">{{ producto.codigo }}</div>
            </div>
          </div>
          <q-btn icon="close" flat round dense class="close-btn" v-close-popup />
        </div>
      </q-card-section>

      <q-separator />

      <q-card-section class="q-pa-lg prod-body">
        <!-- Estado -->
        <div class="row items-center justify-between q-mb-md">
          <q-chip
            :class="producto.estado === 'Activo' ? 'chip-estado-activo' : 'chip-estado-inactivo'"
            :label="producto.estado"
            size="12px"
            dense
          />
        </div>

        <!-- Información general -->
        <div class="section-title">
          <q-icon name="info_outline" size="18px" class="q-mr-xs" />
          Información general
        </div>
        <div class="row q-col-gutter-md q-mb-lg">
          <div class="col-12 col-md-6">
            <div class="field-label">Descripción</div>
            <div class="field-value">{{ producto.descripcion || 'Sin descripción' }}</div>
          </div>
          <div class="col-12 col-md-6">
            <div class="field-label">Categoría</div>
            <div class="field-value">{{ producto.categoria }}</div>
            <div class="field-value sub">{{ producto.subcategoria }}</div>
          </div>
          <div class="col-6 col-md-3">
            <div class="field-label">Subcategoría</div>
            <div class="field-value">{{ producto.subcategoria }}</div>
          </div>
          <div class="col-6 col-md-3">
            <div class="field-label">Marca</div>
            <div class="field-value">{{ producto.marca || '—' }}</div>
          </div>
          <div class="col-12 col-md-3">
            <div class="field-label">Unidad base</div>
            <div class="field-value">{{ producto.unidadBase }}</div>
          </div>
        </div>

        <q-separator class="q-my-md" />

        <!-- Configuración -->
        <div class="section-title">
          <q-icon name="tune" size="18px" class="q-mr-xs" />
          Configuración
        </div>
        <div class="row q-col-gutter-md q-mb-lg">
          <div class="col-6">
            <div class="field-label">Controla lote</div>
            <q-chip class="prod-config-chip" :color="producto.controlaLote ? 'green-7' : 'grey-6'" text-color="white" dense>
              {{ producto.controlaLote ? 'Sí' : 'No' }}
            </q-chip>
          </div>
          <div class="col-6">
            <div class="field-label">Controla vencimiento</div>
            <q-chip class="prod-config-chip" :color="producto.controlaVencimiento ? 'green-7' : 'grey-6'" text-color="white" dense>
              {{ producto.controlaVencimiento ? 'Sí' : 'No' }}
            </q-chip>
          </div>
        </div>

        <q-separator class="q-mb-md" />

        <!-- Presentaciones -->
        <div class="section-title">
          <q-icon name="layers" size="18px" class="q-mr-xs" />
          Presentaciones de venta
        </div>
        <template v-if="producto.presentaciones && producto.presentaciones.length">
          <q-list bordered separator class="prod-list">
            <q-item v-for="p in producto.presentaciones" :key="p.id" class="prod-item">
              <q-item-section>
                <q-item-label class="item-nombre">
                  {{ p.nombre }}
                  <q-chip v-if="p.principal" size="10px" color="green-7" text-color="white" class="q-ml-sm">Principal</q-chip>
                </q-item-label>
                <q-item-label caption>{{ p.unidad }}</q-item-label>
              </q-item-section>
              <q-item-section side>
                <div class="row items-center q-gutter-sm">
                  <q-badge v-if="p.factor" color="green-3" text-color="green-9" outline class="prod-factor">x {{ p.factor }}</q-badge>
                  <div class="precio">{{ p.precio }}</div>
                </div>
              </q-item-section>
            </q-item>
          </q-list>
        </template>
        <div v-else class="prod-empty">
          <q-icon name="check_circle_outline" size="34px" color="green-6" />
          <div>Este producto se vende únicamente por su unidad base ({{ producto.unidadBase }})</div>
        </div>
      </q-card-section>

      <q-separator />

      <q-card-actions align="right" class="q-pa-md">
        <q-btn flat label="Cerrar" color="green-8" v-close-popup no-caps />
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
</script>

<style scoped>
.prod-card {
  border-radius: 16px;
  max-width: 720px;
  margin: 0 auto;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
}
.accent-bar {
  height: 4px;
  background: linear-gradient(90deg, #2E7D32, #388E3C, #81C784);
  border-radius: 16px 16px 0 0;
}
.prod-flex {
  background: linear-gradient(135deg, #f1f8e9, #e8f5e9);
}
.close-btn {
  color: #2E7D32;
}
.prod-title {
  font-size: 18px;
  font-weight: 700;
  color: #1B5E20;
}
.prod-eyebrow {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 1px;
  color: #2E7D32;
  text-transform: uppercase;
}
.prod-detail-imagen {
  background: #ffffff;
  border: 1px solid #c8e6c9;
}
.prod-body {
  background: #fafafa;
}
.section-title {
  font-size: 15px;
  font-weight: 700;
  color: #2E7D32;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
}
.field-label {
  font-size: 11px;
  font-weight: 700;
  color: #759375;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 2px;
}
.field-value {
  font-size: 14px;
  font-weight: 600;
  color: #1B5E20;
}
.field-value.sub {
  font-size: 12px;
  color: #66a35f;
}
.prod-config-chip {
  font-weight: 700;
}
.q-list {
  background: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
}
.q-item {
  border-bottom: 1px solid #f0f0f0;
}
.q-item:last-child {
  border-bottom: none;
}
.row-nombre {
  font-weight: 700;
  color: #1B5E20;
}
.prod-factor {
  font-weight: 700;
}
.precio {
  font-family: 'DM Sans', 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 14px;
  color: #2E7D32;
}
.prod-empty {
  padding: 30px;
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
.chip-estado-activo {
  background: #E7F6E9 !important;
  color: #2E7D32 !important;
}
.chip-estado-inactivo {
  background: #EFEFEF !important;
  color: #5B6675 !important;
}
</style>