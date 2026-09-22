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
    <template v-if="subcategoria">
      <div class="column no-wrap full-height">
        <!-- Cabecera fija -->
        <q-card flat class="cat-drawer-header no-shadow">
          <q-card-section class="q-pa-lg">
            <div class="row items-center justify-between">
              <q-btn flat round dense icon="close" size="sm" class="close-btn" @click="emit('update:modelValue', false)">
                <q-tooltip>Cerrar</q-tooltip>
              </q-btn>
              <div class="text-subtitle2 text-weight-bold">Detalle de Subcategoría</div>
              <q-btn v-permiso="'CATEGORIAS_EDITAR'" flat round dense icon="edit" size="sm" class="close-btn" @click="emit('editar-subcategoria', subcategoria, categoria)">
                <q-tooltip>Editar subcategoría</q-tooltip>
              </q-btn>
            </div>

            <div class="text-center q-mt-md">
              <q-avatar size="72px" rounded class="cat-avatar" :style="{ background: 'rgba(255,255,255,0.6)', border: '1px solid rgba(255,255,255,0.9)' }">
                <q-icon :name="iconoSub" :style="{ color: estadoColor }" size="40px" />
              </q-avatar>
              <div class="text-h6 text-weight-bold q-mt-sm">{{ subcategoria.nombre }}</div>
              <div class="text-caption q-mt-xs" style="color: #4E7A3A;">
                {{ subcategoria.descripcion }}
              </div>
              <q-chip
                size="sm"
                class="q-mt-sm"
                square
                :style="subcategoria.estado === 'activo'
                  ? 'background: #C8E6C9 !important; color: #2E7D32 !important;'
                  : 'background: #E0E0E0 !important; color: #616161 !important;'"
                :icon="subcategoria.estado === 'activo' ? 'check_circle' : 'cancel'"
              >
                {{ subcategoria.estado === 'activo' ? 'Activo' : 'Inactivo' }}
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
                <div class="text-body2 text-weight-semibold" style="color: #263238;">SUB-{{ String(subcategoria.id).padStart(4, '0') }}</div>
              </div>
              <div class="col-6">
                <div class="text-caption text-grey-6">Categoría padre</div>
                <div class="text-body2 text-weight-semibold" style="color: #263238;">{{ categoria ? categoria.nombre : '—' }}</div>
              </div>
              <div class="col-6 q-mt-sm">
                <div class="text-caption text-grey-6">Fecha creación</div>
                <div class="text-body2 text-weight-semibold" style="color: #263238;">
                  {{ new Date(subcategoria.fechaCreacion).toLocaleDateString('es-ES', { year: 'numeric', month: 'long', day: 'numeric' }) }}
                </div>
              </div>
              <div class="col-6 q-mt-sm">
                <div class="text-caption text-grey-6">Productos</div>
                <div class="q-mt-xs">
                  <q-badge class="cat-badge" rounded>{{ totalProductos }}</q-badge>
                </div>
              </div>
            </div>
          </q-card>

          <!-- Productos -->
          <div class="q-mt-sm">
            <div class="text-subtitle2 text-weight-bold q-mb-sm" style="color: #1B5E20;">Productos</div>

            <q-card v-if="productos.length" v-for="prod in productos" :key="prod" flat class="cat-sub-card q-mb-sm">
              <q-card-section horizontal class="items-center">
                <q-card-section class="q-pr-none" style="min-width: 52px;">
                  <q-avatar size="38px" rounded class="cat-avatar" :style="{ background: estadoColor + '1A', border: '1px solid ' + estadoColor + '33' }">
                    <q-icon :name="iconoSub" :style="{ color: estadoColor }" size="20px" />
                  </q-avatar>
                </q-card-section>
                <q-card-section class="col">
                  <div class="text-subtitle2 text-weight-semibold" style="color: #263238;">{{ prod }}</div>
                </q-card-section>
              </q-card-section>
            </q-card>

            <q-card v-else flat class="cat-card q-pa-md">
              <div class="text-center text-caption text-grey-6">Esta subcategoría aún no tiene productos.</div>
            </q-card>
          </div>
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
  subcategoria: {
    type: Object,
    default: null
  },
  categoria: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'editar-subcategoria'])

const estadoColor = computed(() => props.subcategoria?.estado === 'activo' ? '#2E7D32' : '#9E9E9E')

const iconoSub = computed(() => props.categoria?.icono || 'subdirectory_arrow_right')

const productos = computed(() => (props.subcategoria && Array.isArray(props.subcategoria.productos)) ? props.subcategoria.productos : [])

const totalProductos = computed(() => productos.value.length)
</script>

<style scoped src="../../assets/styles/categorias/categorias.css"></style>
