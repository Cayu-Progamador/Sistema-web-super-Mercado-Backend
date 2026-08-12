<template>
  <q-table
    :rows="rows"
    :columns="columns"
    :pagination="paginacion"
    :rows-per-page-options="[5, 10, 20, 50]"
    :rows-number="rowsNumber"
    row-key="id"
    class="prod-table"
    flat
    :grid="gridProp"
    :loading="loading"
    :visible-columns="visibleColumns"
    @request="onRequest"
  >
    <template v-slot:loading>
      <q-inner-loading showing color="primary" />
    </template>

    <!-- Imagen del producto -->
    <template v-slot:body-cell-imagen="props">
      <q-td :props="props">
        <q-avatar size="44px" rounded class="prod-imagen" :style="sinImagen(props.row)">
          <q-img v-if="props.row.imagen && !props.row.imagenRota" :src="urlImagen(props.row.imagen)" fit="contain" class="full-height full-width"
            @error="marcarImagenRota(props.row)" />
          <q-icon v-else name="inventory_2" size="22px" />
        </q-avatar>
      </q-td>
    </template>

    <!-- Código interno -->
    <template v-slot:body-cell-codigo="props">
      <q-td :props="props">
        <span class="prod-codigo">{{ props.row.codigo || '—' }}</span>
      </q-td>
    </template>

    <!-- Producto (nombre + descripción corta) -->
    <template v-slot:body-cell-producto="props">
      <q-td :props="props">
        <div class="prod-cell-producto">
          <div class="prod-nombre">{{ props.row.nombre }}</div>
          <div class="prod-descripcion">{{ props.row.descripcion }}</div>
        </div>
      </q-td>
    </template>

    <!-- Categoría -->
    <template v-slot:body-cell-categoria="props">
      <q-td :props="props">
        <span class="prod-cat">{{ props.row.categoria }}</span>
      </q-td>
    </template>

    <!-- Subcategoría -->
    <template v-slot:body-cell-subcategoria="props">
      <q-td :props="props">
        <span class="prod-cat">{{ props.row.subcategoria }}</span>
      </q-td>
    </template>

    <!-- Marca -->
    <template v-slot:body-cell-marca="props">
      <q-td :props="props">
        <span class="prod-marca">{{ props.row.marca || '—' }}</span>
      </q-td>
    </template>

    <!-- Unidad base -->
    <template v-slot:body-cell-unidadBase="props">
      <q-td :props="props">
        <q-chip dense outline color="green-8" class="prod-unidad">{{ props.row.unidadBase }}</q-chip>
      </q-td>
    </template>

    <!-- Presentaciones -->
    <template v-slot:body-cell-presentaciones="props">
      <q-td :props="props">
        <template v-if="props.row.nPresentaciones > 0">
          <q-badge color="green-7" class="prod-badge">
            <q-icon name="layers" size="13px" class="q-mr-xs" />
            {{ props.row.nPresentaciones }} {{ props.row.nPresentaciones === 1 ? 'presentación' : 'presentaciones' }}
          </q-badge>
        </template>
        <q-badge v-else color="grey-6" outline class="prod-badge">
          <q-icon name="check" size="13px" class="q-mr-xs" />
          Solo unidad base
        </q-badge>
      </q-td>
    </template>

    <!-- Estado -->
    <template v-slot:body-cell-estado="props">
      <q-td :props="props">
        <q-chip
          :class="props.row.estado === 'Activo' ? 'chip-estado-activo' : 'chip-estado-inactivo'"
          :label="props.row.estado"
          size="12px"
          dense
        />
      </q-td>
    </template>

    <!-- Acciones -->
    <template v-slot:body-cell-acciones="props">
      <q-td :props="props" class="prod-acciones">
        <q-btn flat round dense icon="visibility" size="md" @click="$emit('ver-detalle', props.row)" style="color: #2E7D32">
          <q-tooltip class="bg-primary text-white">Ver detalle</q-tooltip>
        </q-btn>

        <q-btn flat round dense icon="edit_outline" size="md" @click="$emit('editar', props.row)" style="color: #388E3C">
          <q-tooltip class="bg-positive text-white">Editar producto</q-tooltip>
        </q-btn>

<q-btn
          v-if="props.row.estado === 'Activo'"
          flat round dense
          class="prod-accion-estado"
          icon="block"
          color="red"
          @click="$emit('cambiar-estado', props.row)"
        >
          <q-tooltip class="bg-grey-7 text-white">Desactivar</q-tooltip>
        </q-btn>
        <q-btn
          v-else
          flat round dense
          class="prod-accion-estado"
          icon="check"
          color="green"
          @click="$emit('cambiar-estado', props.row)"
        >
          <q-tooltip class="bg-grey-7 text-white">Activar</q-tooltip>
        </q-btn>
      </q-td>
    </template>

    <template v-slot:item="props">
      <div class="q-table__grid-item col-xs-12 col-sm-6 col-lg-4">
      <q-card class="prod-mobile-card">
        <div class="prod-mob-head">
          <q-avatar size="46px" rounded class="prod-imagen" :style="sinImagen(props.row)">
            <q-img v-if="props.row.imagen && !props.row.imagenRota" :src="urlImagen(props.row.imagen)" fit="contain" class="full-height full-width" @error="marcarImagenRota(props.row)" />
            <q-icon v-else name="inventory_2" size="22px" />
          </q-avatar>
          <div class="prod-mob-titulos">
            <div class="prod-nombre">{{ props.row.nombre }}</div>
            <div class="prod-mob-codigo">
              <q-icon name="qr_code" size="12px" />
              {{ props.row.codigo || '—' }}
            </div>
          </div>
          <q-chip
            :class="props.row.estado === 'Activo' ? 'chip-estado-activo' : 'chip-estado-inactivo'"
            :label="props.row.estado"
            size="11px"
            dense
          />
        </div>

        <q-separator />

        <div class="prod-mob-cuerpo">
          <div class="prod-mob-tags">
            <div v-if="props.row.categoria" class="prod-mob-tag">
              <q-icon name="category" size="12px" /> {{ props.row.categoria }}
            </div>
            <div v-if="props.row.subcategoria" class="prod-mob-tag">
              <q-icon name="folder" size="12px" /> {{ props.row.subcategoria }}
            </div>
            <div v-if="props.row.marca" class="prod-mob-tag">
              <q-icon name="factory" size="12px" /> {{ props.row.marca }}
            </div>
            <div v-if="props.row.unidadBase" class="prod-mob-tag">
              <q-icon name="straighten" size="12px" /> {{ props.row.unidadBase }}
            </div>
          </div>
          <div class="prod-mob-pres">
            <template v-if="props.row.nPresentaciones > 0">
              <q-badge color="green-7" class="prod-badge">
                <q-icon name="layers" size="13px" class="q-mr-xs" />
                {{ props.row.nPresentaciones }} {{ props.row.nPresentaciones === 1 ? 'presentación' : 'presentaciones' }}
              </q-badge>
            </template>
            <q-badge v-else color="grey-6" outline class="prod-badge">
              <q-icon name="check" size="13px" class="q-mr-xs" />
              Solo unidad base
            </q-badge>
          </div>
        </div>
<q-separator />

        <div class="prod-mob-acciones row no-wrap items-center">
          <q-btn flat round dense icon="visibility" @click="$emit('ver-detalle', props.row)">
            <q-tooltip class="bg-primary text-white">Ver detalle</q-tooltip>
          </q-btn>
          <q-btn flat round dense icon="edit_outline" @click="$emit('editar', props.row)">
            <q-tooltip class="bg-positive text-white">Editar producto</q-tooltip>
          </q-btn>
          <q-btn v-if="props.row.estado === 'Activo'" flat round dense icon="block" color="red" @click="$emit('cambiar-estado', props.row)">
            <q-tooltip class="bg-grey-7 text-white">Desactivar</q-tooltip>
          </q-btn>
          <q-btn v-else flat round dense icon="check" color="green" @click="$emit('cambiar-estado', props.row)">
            <q-tooltip class="bg-grey-7 text-white">Activar</q-tooltip>
          </q-btn>
        </div>
      </q-card>
      </div>
    </template>

    <template v-slot:no-data>
      <div class="prod-no-data">
        <q-icon name="inventory_2" size="40px" color="grey-4" />
        <div>No se encontraron productos con los filtros aplicados.</div>
      </div>
    </template>
  </q-table>
</template>

<script setup>
import { computed } from 'vue'
import { useQuasar } from 'quasar'

const $q = useQuasar()

const gridProp = computed(() => $q.screen.lt.md)

const props = defineProps({
  rows: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  rowsNumber: { type: Number, default: 0 }
})

const emit = defineEmits([
  'ver-detalle',
  'editar',
  'cambiar-estado',
  'eliminar',
  'importar',
  'exportar',
  'pagina-cambio'
])

const paginacion = {
  sortBy: 'nombre',
  descending: false,
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0
}

function onRequest(requestProp) {
  emit('pagina-cambio', {
    page: requestProp.pagination.page,
    rowsPerPage: requestProp.pagination.rowsPerPage,
    sortBy: requestProp.pagination.sortBy,
    descending: requestProp.pagination.descending
  })
}

const columns = computed(() => [
  { name: 'imagen', label: '', field: 'imagen', align: 'left' },
  { name: 'codigo', label: 'Código interno', field: 'codigo', align: 'left', sortable: true },
  { name: 'producto', label: 'Producto', field: 'nombre', align: 'left', sortable: true },
  { name: 'categoria', label: 'Categoría', field: 'categoria', align: 'left', sortable: true },
  { name: 'subcategoria', label: 'Subcategoría', field: 'subcategoria', align: 'left' },
  { name: 'marca', label: 'Marca', field: 'marca', align: 'left' },
  { name: 'unidadBase', label: 'Unidad base', field: 'unidadBase', align: 'left' },
  { name: 'presentaciones', label: 'Presentaciones', field: 'nPresentaciones', align: 'center' },
  { name: 'estado', label: 'Estado', field: 'estado', align: 'center', sortable: true },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' }
])

const visibleColumns = [
  'imagen',
  'codigo',
  'producto',
  'categoria',
  'subcategoria',
  'marca',
  'unidadBase',
  'presentaciones',
  'estado',
  'acciones'
]

function sinImagen(row) {
  return row.imagenRota || !row.imagen
    ? { background: 'linear-gradient(135deg, #e8f5e9, #c8e6c9)', color: '#2E7D32' }
    : {}
}

function marcarImagenRota(row) {
  row.imagenRota = true
}

function urlImagen(ruta) {
  if (!ruta) return ''
  if (ruta.startsWith('http')) return ruta
  if (ruta.startsWith('/')) return import.meta.env.VITE_API_URL + ruta
  return import.meta.env.VITE_API_URL + '/uploads/' + ruta
}
</script>

<style scoped>
.prod-table {
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 2px 12px rgba(46, 125, 50, 0.08);
  border: 1.5px solid #c8dfda;
}
.prod-table :deep(.q-table__top),
.prod-table :deep(.q-table__card) {
  background: #ffffff;
}
.prod-table :deep(.q-table thead tr th) {
  background: #E8F5E9 !important;
  color: #2E7D32 !important;
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 2px solid #c8dfda;
  padding: 12px 14px;
}
.prod-table :deep(.q-table tbody td) {
  border-bottom: 1px solid #e8f0ec;
  font-family: 'Nunito', sans-serif;
  font-size: 13px;
  color: #2E7D32;
  padding: 10px 14px;
}
.prod-table :deep(.q-table tbody tr:hover) {
  background: #F1F8E9 !important;
}
.prod-table :deep(.q-pagination .q-btn--active) {
  background: #2E7D32 !important;
  color: #ffffff !important;
}
@media (min-width: 1024px) {
  .prod-table,
  .prod-table :deep(.q-table__container),
  .prod-table :deep(.q-table__middle),
  .prod-table :deep(.q-table) {
    overflow-x: hidden !important;
  }
}
.prod-imagen {
  background: #f1f8e9;
  border: 1px solid #c8e6c9;
}
.prod-codigo {
  font-family: 'DM Sans', 'Nunito', sans-serif;
  font-weight: 700;
  font-size: 12px;
  color: #1B5E20;
  background: #f1f8e9;
  padding: 4px 8px;
  border-radius: 6px;
}
.prod-nombre {
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 13.5px;
  color: #1B5E20;
}
.prod-descripcion {
  font-size: 11px;
  color: #66a35f;
  font-family: 'Nunito', sans-serif;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 220px;
}
.prod-cat,
.prod-marca {
  font-family: 'Nunito', sans-serif;
  font-weight: 600;
  font-size: 13px;
  color: #2E7D32;
}
.prod-unidad {
  font-family: 'Nunito', sans-serif;
  font-weight: 700;
  font-size: 12px;
}
.prod-badge {
  font-family: 'Nunito', sans-serif;
  font-weight: 700;
  font-size: 11.5px;
  border-radius: 999px;
  padding: 4px 10px;
}
.prod-acciones {
  white-space: nowrap;
}
.prod-mobile-card {
  border: 1px solid #dce9d6;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(27, 94, 32, 0.06);
}
.prod-mob-head {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
}
.prod-mob-titulos {
  min-width: 0;
  flex: 1;
}
.prod-mob-codigo {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 1px;
  color: #66a35f;
  text-transform: uppercase;
  margin-top: 3px;
}
.prod-mob-cuerpo {
  padding: 12px 14px;
}
.prod-mob-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.prod-mob-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 11.5px;
  font-weight: 700;
  color: #55924F;
  background: #f1f8e9;
  border: 1px solid #d7e8d0;
  border-radius: 999px;
  padding: 3px 9px;
}
.prod-mob-pres {
  margin-top: 10px;
}
.prod-mob-acciones {
  padding: 10px 14px;
  gap: 4px;
}
.prod-accion-estado:hover {
  background: #E8F5E9 !important;
}
.chip-estado-activo {
  background: #E7F6E9 !important;
  color: #2E7D32 !important;
  font-weight: 700;
}
.chip-estado-inactivo {
  background: #EFEFEF !important;
  color: #5B6675 !important;
  font-weight: 700;
}
.prod-no-data {
  padding: 40px 0;
  text-align: center;
  color: #2E7D32;
  font-family: 'Nunito', sans-serif;
  font-weight: 600;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}
</style>