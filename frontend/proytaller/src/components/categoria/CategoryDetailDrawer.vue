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
    <template v-if="categoria">
      <div class="column no-wrap full-height">
        <!-- Cabecera fija -->
        <q-card flat class="cat-drawer-header no-shadow">
          <q-card-section class="q-pa-lg">
            <div class="row items-center justify-between">
              <q-btn flat round dense icon="close" size="sm" class="close-btn" @click="emit('update:modelValue', false)">
                <q-tooltip>Cerrar</q-tooltip>
              </q-btn>
              <div class="text-subtitle2 text-weight-bold">Detalle de Categoría</div>
              <q-btn flat round dense icon="edit" size="sm" class="close-btn" @click="emit('editar', categoria)">
                <q-tooltip>Editar categoría</q-tooltip>
              </q-btn>
            </div>

            <div class="text-center q-mt-md">
              <q-avatar size="72px" rounded class="cat-avatar" :style="{ background: 'rgba(255,255,255,0.6)', border: '1px solid rgba(255,255,255,0.9)' }">
                <q-icon :name="categoria.icono" :style="{ color: estadoColor }" size="40px" />
              </q-avatar>
              <div class="text-h6 text-weight-bold q-mt-sm">{{ categoria.nombre }}</div>
              <div class="text-caption q-mt-xs" style="color: #4E7A3A;">
                {{ categoria.descripcion }}
              </div>
              <q-chip
                size="sm"
                class="q-mt-sm"
                square
                :style="categoria.estado === 'activo'
                  ? 'background: #C8E6C9 !important; color: #2E7D32 !important;'
                  : 'background: #E0E0E0 !important; color: #616161 !important;'"
                :icon="categoria.estado === 'activo' ? 'check_circle' : 'cancel'"
              >
                {{ categoria.estado === 'activo' ? 'Activo' : 'Inactivo' }}
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
                <div class="text-body2 text-weight-semibold" style="color: #263238;">CAT-{{ String(categoria.id).padStart(4, '0') }}</div>
              </div>
              <div class="col-6">
                <div class="text-caption text-grey-6">Fecha creación</div>
                <div class="text-body2 text-weight-semibold" style="color: #263238;">
                  {{ new Date(categoria.fechaCreacion).toLocaleDateString('es-ES', { year: 'numeric', month: 'long', day: 'numeric' }) }}
                </div>
              </div>
              <div class="col-6 q-mt-sm">
                <div class="text-caption text-grey-6">Subcategorías</div>
                <div class="q-mt-xs">
                  <q-badge class="cat-badge" rounded>{{ categoria.subcategorias.length }}</q-badge>
                </div>
              </div>
              <div class="col-6 q-mt-sm">
                <div class="text-caption text-grey-6">Productos</div>
                <div class="q-mt-xs">
                  <q-badge class="cat-badge" rounded>{{ totalProductos(categoria) }}</q-badge>
                </div>
              </div>
            </div>
          </q-card>

          <!-- Subcategorías -->
          <div class="q-mt-sm">
            <div class="text-subtitle2 text-weight-bold q-mb-sm" style="color: #1B5E20;">Subcategorías</div>

            <q-card v-for="sub in categoria.subcategorias" :key="sub.id" flat class="cat-sub-card q-mb-sm">
              <q-card-section horizontal class="items-center">
                <q-card-section class="q-pr-none" style="min-width: 52px;">
                  <q-avatar size="38px" rounded class="cat-avatar" :style="{ background: estadoColor + '1A', border: '1px solid ' + estadoColor + '33' }">
                    <q-icon :name="categoria.icono" :style="{ color: estadoColor }" size="20px" />
                  </q-avatar>
                </q-card-section>
                <q-card-section class="col">
                  <div class="text-subtitle2 text-weight-semibold" style="color: #263238;">{{ sub.nombre }}</div>
                  <div class="text-caption text-grey-7">{{ sub.productos.length }} productos</div>
                </q-card-section>
                <q-card-section class="q-px-sm">
                  <q-btn flat round dense size="sm" icon="edit" style="color: #388E3C;" @click="emit('editar-subcategoria', sub, categoria)">
                    <q-tooltip>Editar subcategoría</q-tooltip>
                  </q-btn>
                </q-card-section>
              </q-card-section>
            </q-card>

            <q-card v-if="!categoria.subcategorias.length" flat class="cat-card q-pa-md">
              <div class="text-center text-caption text-grey-6">Esta categoría aún no tiene subcategorías.</div>
            </q-card>
          </div>

          <q-btn
            no-caps
            icon="add"
            label="+ Nueva Subcategoría"
            class="cat-btn cat-btn-subcategoria full-width q-mt-md"
            @click="emit('agregar-subcategoria', categoria)"
          />
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
  categoria: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'editar', 'editar-subcategoria', 'agregar-subcategoria'])

const estadoColor = computed(() => props.categoria?.estado === 'activo' ? '#2E7D32' : '#9E9E9E')

function totalProductos(cat) {
  return cat.subcategorias.reduce((acc, sub) => acc + (sub.productos ? sub.productos.length : 0), 0)
}
</script>

<style scoped src="../../assets/styles/categorias/categorias.css"></style>