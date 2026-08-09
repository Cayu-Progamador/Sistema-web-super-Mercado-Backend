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
    <template v-if="marca">
      <div class="column no-wrap full-height">
        <!-- Cabecera fija -->
        <q-card flat class="cat-drawer-header no-shadow">
          <q-card-section class="q-pa-lg">
            <div class="row items-center justify-between">
              <q-btn flat round dense icon="close" size="sm" class="close-btn" @click="emit('update:modelValue', false)">
                <q-tooltip>Cerrar</q-tooltip>
              </q-btn>
              <div class="text-subtitle2 text-weight-bold">Detalle de Marca</div>
              <q-btn flat round dense icon="edit" size="sm" class="close-btn" @click="emit('editar', marca)">
                <q-tooltip>Editar marca</q-tooltip>
              </q-btn>
            </div>

            <div class="text-center q-mt-md">
              <q-avatar size="72px" rounded class="cat-avatar" style="background: #2E7D321A; border: 1px solid #2E7D3240;">
                <q-icon name="branding_watermark" color="green-8" size="40px" />
              </q-avatar>
              <div class="text-h6 text-weight-bold q-mt-sm">{{ marca.nombre }}</div>
              <div class="text-caption q-mt-xs" style="color: #4E7A3A;">
                {{ marca.descripcion || 'Sin descripción' }}
              </div>
              <q-chip
                size="sm"
                class="q-mt-sm"
                square
                :style="marca.estado === 'activo'
                  ? 'background: #C8E6C9 !important; color: #2E7D32 !important;'
                  : 'background: #E0E0E0 !important; color: #616161 !important;'"
                :icon="marca.estado === 'activo' ? 'check_circle' : 'cancel'"
              >
                {{ marca.estado === 'activo' ? 'Activo' : 'Inactivo' }}
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
                <div class="text-body2 text-weight-semibold" style="color: #263238;">MAR-{{ String(marca.id).padStart(4, '0') }}</div>
              </div>
              <div class="col-6">
                <div class="text-caption text-grey-6">Productos</div>
                <div class="q-mt-xs">
                  <q-badge class="cat-badge" rounded>{{ marca.nProductos }}</q-badge>
                </div>
              </div>
              <div class="col-6 q-mt-sm">
                <div class="text-caption text-grey-6">Fecha creación</div>
                <div class="text-body2 text-weight-semibold" style="color: #263238;">
                  {{ formatearFecha(marca.fechaCreacion) }}
                </div>
              </div>
              <div class="col-6 q-mt-sm">
                <div class="text-caption text-grey-6">Fecha actualización</div>
                <div class="text-body2 text-weight-semibold" style="color: #263238;">
                  {{ formatearFecha(marca.fechaActualizacion) }}
                </div>
              </div>
            </div>
          </q-card>

          <!-- Nota de uso -->
          <q-card flat class="cat-card q-pa-md">
            <div class="row items-start" style="gap: 10px;">
              <q-icon name="lightbulb" size="20px" style="color: #F57C00;" />
              <div>
                <div class="text-subtitle2 text-weight-bold" style="color: #1B5E20;">Sobre esta marca</div>
                <p class="text-caption q-mt-xs" style="color: #5B6675; margin: 0;">
                  Las marcas se asignan a los productos del catálogo. Al desactivar una marca, esta deja de poder
                  usarse en productos nuevos pero conserva el historial de los existentes.
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
defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  marca: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'editar'])

function formatearFecha(fecha) {
  if (!fecha) return '—'
  return new Date(fecha).toLocaleDateString('es-ES', { year: 'numeric', month: 'long', day: 'numeric' })
}
</script>

<style scoped src="../../assets/styles/categorias/categorias.css"></style>
