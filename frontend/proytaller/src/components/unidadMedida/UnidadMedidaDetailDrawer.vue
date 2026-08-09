<template>
  <q-drawer
    :model-value="modelValue"
    @update:model-value="emit('update:modelValue', $event)"
    side="right"
    overlay
    bordered
    :width="420"
    class="cat-drawer"
    style="background: #E8F5E9;"
  >
    <template v-if="unidad">
      <div class="column no-wrap full-height">
        <!-- Cabecera fija -->
        <q-card flat class="cat-drawer-header no-shadow">
          <q-card-section class="q-pa-lg">
            <div class="row items-center justify-between">
              <q-btn flat round dense icon="close" size="sm" class="close-btn" @click="emit('update:modelValue', false)">
                <q-tooltip>Cerrar</q-tooltip>
              </q-btn>
              <div class="text-subtitle2 text-weight-bold">Detalle de Unidad de Medida</div>
              <q-btn flat round dense icon="edit" size="sm" class="close-btn" @click="emit('editar', unidad)">
                <q-tooltip>Editar unidad</q-tooltip>
              </q-btn>
            </div>

            <div class="text-center q-mt-md">
              <q-avatar size="72px" rounded class="cat-avatar" :style="{ background: colorTipo + '1A', border: '1px solid ' + colorTipo + '40' }">
                <q-icon :name="iconoTipo" :style="{ color: colorTipo }" size="40px" />
              </q-avatar>
              <div class="text-h6 text-weight-bold q-mt-sm">{{ unidad.nombre }}</div>
              <div class="text-caption q-mt-xs" style="color: #4E7A3A;">
                Abreviatura: <strong>{{ unidad.abreviatura }}</strong>
              </div>
              <q-chip
                size="sm"
                class="q-mt-sm"
                square
                :style="unidad.estado === 'activo'
                  ? 'background: #C8E6C9 !important; color: #2E7D32 !important;'
                  : 'background: #E0E0E0 !important; color: #616161 !important;'"
                :icon="unidad.estado === 'activo' ? 'check_circle' : 'cancel'"
              >
                {{ unidad.estado === 'activo' ? 'Activo' : 'Inactivo' }}
              </q-chip>
            </div>
          </q-card-section>
        </q-card>

        <q-scroll-area class="col q-pa-md">
          <!-- Info general -->
          <q-card flat class="cat-card cat-info-card q-pa-md q-mb-md">
            <div class="text-subtitle2 text-weight-bold q-mb-sm" style="color: #1B5E20;">Información general</div>
            <div class="row q-col-gutter-sm">
              <div class="col-6">
                <div class="text-caption text-grey-6">ID</div>
                <div class="text-body2 text-weight-semibold" style="color: #263238;">UM-{{ String(unidad.id).padStart(4, '0') }}</div>
              </div>
              <div class="col-6">
                <div class="text-caption text-grey-6">Tipo de unidad</div>
                <div class="row items-center q-mt-xs no-wrap" style="gap: 6px;">
                  <q-avatar size="20px" square :style="{ background: colorTipo + '1A', borderRadius: '5px' }">
                    <q-icon :name="iconoTipo" :style="{ color: colorTipo }" size="13px" />
                  </q-avatar>
                  <span class="text-body2 text-weight-semibold" style="color: #263238;">{{ unidad.tipoNombre }}</span>
                </div>
              </div>
              <div class="col-6 q-mt-sm">
                <div class="text-caption text-grey-6">Abreviatura</div>
                <div class="text-body2 text-weight-semibold" style="color: #263238;">{{ unidad.abreviatura }}</div>
              </div>
              <div class="col-6 q-mt-sm">
                <div class="text-caption text-grey-6">Fecha creación</div>
                <div class="text-body2 text-weight-semibold" style="color: #263238;">
                  {{ new Date(unidad.fechaCreacion).toLocaleDateString('es-ES', { year: 'numeric', month: 'long', day: 'numeric' }) }}
                </div>
              </div>
            </div>
          </q-card>

          <!-- Nota de uso -->
          <q-card flat class="cat-card q-pa-md">
            <div class="row items-start" style="gap: 10px;">
              <q-icon name="lightbulb" size="20px" style="color: #F57C00;" />
              <div>
                <div class="text-subtitle2 text-weight-bold" style="color: #1B5E20;">Sobre esta unidad</div>
                <p class="text-caption q-mt-xs" style="color: #5B6675; margin: 0;">
                  Esta unidad se usa como unidad base o en las presentaciones de los productos.
                  El factor de conversión se define en cada presentación del producto.
                </p>
              </div>
            </div>
          </q-card>
        </q-scroll-area>
      </div>
    </template>
  </q-drawer>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  unidad: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'editar'])

const tipoIconos = { 'Peso': 'scale', 'Volumen': 'water_drop', 'Conteo': 'inventory_2', 'Lineal': 'straighten' }
const tipoColores = { 'Peso': '#2E7D32', 'Volumen': '#1565C0', 'Conteo': '#F57C00', 'Lineal': '#6A1B9A' }

const iconoTipo = computed(() => tipoIconos[props.unidad?.tipoNombre] || 'straighten')
const colorTipo = computed(() => tipoColores[props.unidad?.tipoNombre] || '#2E7D32')
</script>

<style scoped src="../../assets/styles/categorias/categorias.css"></style>
