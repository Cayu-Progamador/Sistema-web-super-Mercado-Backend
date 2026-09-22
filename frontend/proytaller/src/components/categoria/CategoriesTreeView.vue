<template>
  <div class="q-col-gutter-md">
    <q-expansion-item
      v-for="cat in categorias"
      :key="cat.id"
      expand-icon="arrow_drop_down"
      expand-icon-class="cat-expand-icon"
      :header-class="'cat-expansion'"
      expand-separator
      switch-toggle-side
      class="q-mb-sm"
      :default-opened="false"
    >
      <template v-slot:header>
        <q-item-section avatar>
          <q-avatar size="38px" rounded class="cat-avatar" :style="{ background: cat.color + '1A', border: '1px solid ' + cat.color + '33' }">
            <q-icon :name="cat.icono" :style="{ color: cat.color }" size="20px" />
          </q-avatar>
        </q-item-section>

        <q-item-section>
          <div class="row items-center" style="gap: 10px;">
            <div class="text-subtitle1 text-weight-bold" style="color: #263238;">{{ cat.nombre }}</div>
            <q-chip
              size="sm"
              :class="cat.estado === 'activo' ? 'cat-chip-activo' : 'cat-chip-inactivo'"
              :icon="cat.estado === 'activo' ? 'check_circle' : 'cancel'"
              square
            >
              {{ cat.estado === 'activo' ? 'Activo' : 'Inactivo' }}
            </q-chip>
          </div>
          <div class="row items-center q-mt-xs" style="gap: 14px;">
            <div class="row items-center no-wrap" style="gap: 4px;">
              <q-icon name="category" size="14px" style="color: #2E7D32;" />
              <span class="text-caption text-weight-medium">{{ cat.subcategorias.length }} subcategorías</span>
            </div>
            <div class="row items-center no-wrap" style="gap: 4px;">
              <q-icon name="inventory_2" size="14px" style="color: #388E3C;" />
              <span class="text-caption text-weight-medium">{{ totalProductos(cat) }} productos</span>
            </div>
          </div>
        </q-item-section>

        <q-item-section side>
          <div class="row items-center" style="gap: 4px;">
            <q-btn flat round dense size="sm" icon="visibility" style="color: #2E7D32;" @click.stop="emit('ver-detalle', cat)">
              <q-tooltip>Ver detalle categoría</q-tooltip>
            </q-btn>
            <q-btn flat round dense size="sm" icon="playlist_add" style="color: #F57C00;" @click.stop="emit('agregar-subcategoria', cat)">
              <q-tooltip>Agregar subcategoría</q-tooltip>
            </q-btn>
          </div>
        </q-item-section>
      </template>

      <q-list dense padding>
        <template v-if="cat.subcategorias.length">
          <q-item v-for="sub in cat.subcategorias" :key="sub.id" class="cat-sub-item q-mb-xs">
            <q-item-section avatar style="min-width: 40px;">
              <q-icon name="subdirectory_arrow_right" size="18px" style="color: #66a35f;" />
            </q-item-section>

            <q-item-section>
              <div class="text-subtitle2 text-weight-semibold" style="color: #263238;">{{ sub.nombre }}</div>
              <div class="row items-center q-mt-xs" style="gap: 6px;">
                <q-icon name="inventory_2" size="13px" style="color: #388E3C;" />
                <span class="text-caption text-weight-medium text-grey-7">{{ sub.productos.length }} productos</span>
              </div>
              <div v-if="sub.productos.length" class="row q-mt-xs" style="gap: 6px;">
                <q-chip
                  v-for="prod in sub.productos"
                  :key="prod"
                  dense
                  size="sm"
                  square
                  class="cat-producto-tag"
                >
                  {{ prod }}
                </q-chip>
              </div>
            </q-item-section>

            <q-item-section side>
              <div class="row items-center" style="gap: 4px;">
                <q-btn flat round dense size="sm" icon="visibility" style="color: #2E7D32;" @click="emit('ver-detalle-subcategoria', sub, cat)">
                  <q-tooltip>Ver detalle subcategoría</q-tooltip>
                </q-btn>
                <q-btn v-permiso="'CATEGORIAS_EDITAR'" flat round dense size="sm" icon="edit" style="color: #388E3C;" @click="emit('editar-subcategoria', sub, cat)">
                  <q-tooltip>Editar subcategoría</q-tooltip>
                </q-btn>
              </div>
            </q-item-section>
          </q-item>
        </template>
        <q-item v-else>
          <q-item-section class="text-caption text-grey-5">Esta categoría aún no tiene subcategorías.</q-item-section>
        </q-item>
      </q-list>
    </q-expansion-item>

    <div v-if="!categorias.length" class="q-pa-lg">
      <div class="text-center text-grey-7">No se encontraron categorías con los filtros aplicados.</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  categorias: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['ver-detalle', 'agregar-subcategoria', 'editar-subcategoria', 'ver-detalle-subcategoria'])

function totalProductos(cat) {
  return cat.subcategorias.reduce((acc, sub) => acc + (sub.productos ? sub.productos.length : 0), 0)
}
</script>

<style scoped>
.cat-chip-activo {
  background: #E7F6E9 !important;
  color: #1B5E20 !important;
  font-weight: 700;
}

.cat-chip-activo :deep(.q-chip__icon) {
  color: #1B5E20 !important;
}

.cat-chip-inactivo {
  background: #EFEFEF !important;
  color: #5B6675 !important;
  font-weight: 700;
}

.cat-chip-inactivo :deep(.q-chip__icon) {
  color: #5B6675 !important;
}

.cat-expand-icon {
  color: #2E7D32;
}

.cat-expansion {
  background: #ffffff;
  border: 1px solid #e7eef2;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(46, 125, 50, 0.05);
}
</style>
