<template>
  <q-table
    :rows="rows"
    :columns="columns"
    :pagination="paginacion"
    :rows-per-page-options="[5, 10, 20, 50]"
    row-key="id"
    class="pv-table"
    flat
    :loading="loading"
    :visible-columns="visibleColumns"
  >
    <template v-slot:loading>
      <q-inner-loading showing color="primary" />
    </template>

    <template v-slot:body-cell-proveedor="props">
      <q-td :props="props">
        <div class="pv-prov">
          <q-avatar :color="props.row.color" text-color="white" size="38px" font-size="15px" class="pv-avatar">
            {{ initials(props.row.nombre) }}
          </q-avatar>
          <div class="pv-prov-meta">
            <div class="pv-prov-nombre">{{ props.row.nombre }}</div>
            <div class="pv-prov-sub">{{ props.row.categoria }}</div>
          </div>
        </div>
      </q-td>
    </template>

    <template v-slot:body-cell-tipo="props">
      <q-td :props="props">
        <q-chip :color="props.row.tipo === 'Empresa' ? 'blue-9' : 'purple-10'" text-color="white" dense size="12px" class="pv-chip-tipo">
          <q-icon :name="props.row.tipo === 'Empresa' ? 'apartment' : 'person'" size="14px" />
          {{ props.row.tipo }}
        </q-chip>
      </q-td>
    </template>

    <template v-slot:body-cell-documento="props">
      <q-td :props="props">
        <span class="pv-doc">{{ props.row.documento.tipo }}: </span>
        <span class="pv-doc-num">{{ props.row.documento.numero }}</span>
      </q-td>
    </template>

    <template v-slot:body-cell-productos="props">
      <q-td :props="props">
        <div class="pv-products">
          <q-chip v-for="(prod, i) in props.row.productos.slice(0, 2)" :key="prod" dense size="12px" class="pv-product-chip">
            {{ prod }}
          </q-chip>
          <q-chip v-if="props.row.productos.length > 2" dense size="12px" color="grey-8" text-color="white" class="pv-product-chip">
            +{{ props.row.productos.length - 2 }} más
          </q-chip>
        </div>
      </q-td>
    </template>

    <template v-slot:body-cell-contacto="props">
      <q-td :props="props">
        <div class="pv-contacto">
          <div class="pv-contacto-nombre">{{ props.row.contacto.nombre }}</div>
          <div class="pv-contacto-tel">{{ props.row.contacto.telefono }}</div>
        </div>
      </q-td>
    </template>

    <template v-slot:body-cell-calificacion="props">
      <q-td :props="props">
        <q-rating :model-value="props.row.calificacion" readonly size="15px" color="amber-7" icon="star" />
      </q-td>
    </template>

    <template v-slot:body-cell-estado="props">
      <q-td :props="props">
        <q-chip :color="props.row.estado === 'Activo' ? 'green-7' : 'grey-6'" text-color="white" dense size="12px">
          <q-icon :name="props.row.estado === 'Activo' ? 'check_circle' : 'cancel'" size="14px" />
          {{ props.row.estado }}
        </q-chip>
      </q-td>
    </template>

    <template v-slot:body-cell-acciones="props">
      <q-td :props="props" class="pv-acciones">
        <q-btn flat round dense icon="visibility" color="primary" @click="$emit('ver-detalle', props.row)">
          <q-tooltip>Ver detalle</q-tooltip>
        </q-btn>
        <q-btn flat round dense icon="edit" color="blue-7" @click="$emit('editar', props.row)">
          <q-tooltip>Editar proveedor</q-tooltip>
        </q-btn>
        <q-btn flat round dense icon="history" color="teal-7" @click="$emit('historial', props.row)">
          <q-tooltip>Historial de compras</q-tooltip>
        </q-btn>
        <q-btn
          flat
          round
          dense
          :icon="props.row.estado === 'Activo' ? 'pause_circle_outline' : 'play_circle_outline'"
          :color="props.row.estado === 'Activo' ? 'orange-8' : 'green-7'"
          @click="$emit('cambiar-estado', props.row)"
        >
          <q-tooltip>{{ props.row.estado === 'Activo' ? 'Desactivar' : 'Activar' }}</q-tooltip>
        </q-btn>
        <q-btn flat round dense icon="delete" color="red-6" @click="$emit('eliminar', props.row)">
          <q-tooltip>Eliminar</q-tooltip>
        </q-btn>
      </q-td>
    </template>

    <template v-slot:no-data>
      <div class="pv-no-data">
        <q-icon name="inbox" size="40px" color="grey-4" />
        <div>No se encontraron proveedores con los filtros aplicados.</div>
      </div>
    </template>
  </q-table>
</template>

<script setup>
defineProps({
  rows: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false }
})

defineEmits(['ver-detalle', 'editar', 'historial', 'cambiar-estado', 'eliminar'])

const paginacion = {
  sortBy: 'nombre',
  descending: false,
  page: 1,
  rowsPerPage: 10
}

const columns = [
  { name: 'proveedor', label: 'Proveedor', field: 'nombre', align: 'left', sortable: true },
  { name: 'tipo', label: 'Tipo', field: 'tipo', align: 'left', sortable: true },
  { name: 'documento', label: 'NIT / CI', field: 'documento', align: 'left' },
  { name: 'productos', label: 'Productos', field: 'productos', align: 'left' },
  { name: 'contacto', label: 'Contacto y teléfono', field: 'contacto', align: 'left' },
  { name: 'ciudad', label: 'Ciudad', field: 'ciudad', align: 'left', sortable: true },
  { name: 'calificacion', label: 'Calificación', field: 'calificacion', align: 'center', sortable: true },
  { name: 'estado', label: 'Estado', field: 'estado', align: 'center', sortable: true },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'right' }
]

const visibleColumns = [
  'proveedor',
  'tipo',
  'documento',
  'productos',
  'contacto',
  'ciudad',
  'calificacion',
  'estado',
  'acciones'
]

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
.pv-table {
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 1px 3px rgba(16, 24, 40, 0.05);
  border: 1px solid #e6ebf1;
}

.pv-prov {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pv-avatar {
  border-radius: 9px !important;
  font-family: 'DM Sans', 'Nunito', sans-serif;
  font-weight: 700;
}

.pv-prov-nombre {
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 13.5px;
  color: #16231c;
}

.pv-prov-sub {
  font-size: 11.5px;
  color: #7a8a80;
  font-family: 'Nunito', sans-serif;
}

.pv-chip-tipo {
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
}

.pv-doc {
  font-size: 11px;
  font-weight: 700;
  color: #8a9a90;
}

.pv-doc-num {
  font-family: 'DM Sans', 'Nunito', sans-serif;
  font-weight: 700;
  font-size: 12.5px;
  color: #2b3b30;
}

.pv-products {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-wrap: wrap;
  max-width: 240px;
}

.pv-product-chip {
  background: #eef3f0;
  color: #2f4438;
  font-family: 'Nunito', sans-serif;
  font-weight: 700;
  font-size: 11px;
}

.pv-contacto-nombre {
  font-family: 'Nunito', sans-serif;
  font-weight: 700;
  font-size: 12.5px;
  color: #33443a;
}

.pv-contacto-tel {
  font-size: 11.5px;
  color: #7a8a80;
  font-family: 'Nunito', sans-serif;
}

.pv-acciones {
  white-space: nowrap;
}

.pv-no-data {
  padding: 40px 0;
  text-align: center;
  color: #8a9a90;
  font-family: 'Nunito', sans-serif;
  font-weight: 600;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}
</style>
