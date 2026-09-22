<template>
  <q-card flat bordered class="table-card">
    <q-table flat :rows="rows" :columns="columns" row-key="id" :pagination="pagination" :loading="loading" :rows-per-page-options="[5, 10, 20]" @request="$emit('request', $event)">
      <template #body-cell-tipo="p"><q-td :props="p"><q-badge :color="p.row.tipo === 'ENTRADA' ? 'blue-7' : 'red-6'" :label="p.row.tipo" /><div class="muted">{{ p.row.motivo }}</div></q-td></template>
      <template #body-cell-ajuste="p"><q-td :props="p"><b>{{ p.row.codigo }}</b><div class="muted">{{ p.row.productos }} producto(s)</div></q-td></template>
      <template #body-cell-cantidad="p"><q-td :props="p"><b>{{ p.row.cantidad }}</b><div class="muted">{{ p.row.almacen }} · {{ p.row.ubicacion }}</div></q-td></template>
      <template #body-cell-estado="p"><q-td :props="p"><q-badge rounded :color="colorEstado(p.row.estado)" :label="textoEstado(p.row.estado)" /></q-td></template>
      <template #body-cell-acciones="p">
        <q-td :props="p" class="actions">
          <q-btn flat round dense color="green-8" icon="visibility" @click="$emit('ver', p.row)"><q-tooltip>Ver detalle</q-tooltip></q-btn>
          <q-btn v-if="p.row.estado === 'BORRADOR'" v-permiso="'AJUSTES_INVENTARIO_EDITAR'" flat round dense color="blue-7" icon="edit" @click="$emit('editar', p.row)"><q-tooltip>Editar borrador</q-tooltip></q-btn>
          <q-btn v-if="p.row.estado === 'BORRADOR'" v-permiso="'AJUSTES_INVENTARIO_EDITAR'" flat round dense color="negative" icon="delete" @click="$emit('eliminar', p.row)"><q-tooltip>Eliminar borrador</q-tooltip></q-btn>
          <q-btn v-if="p.row.estado === 'BORRADOR'" v-permiso="'AJUSTES_INVENTARIO_EDITAR'" flat round dense color="orange-8" icon="send" @click="$emit('enviar', p.row)"><q-tooltip>Enviar a aprobación</q-tooltip></q-btn>
          <q-btn v-if="p.row.estado === 'PENDIENTE_APROBACION'" v-permiso="'AJUSTES_INVENTARIO_APROBAR'" flat round dense color="green-8" icon="verified" @click="$emit('aprobar', p.row)"><q-tooltip>Aprobar ajuste</q-tooltip></q-btn>
          <q-btn v-if="p.row.estado === 'PENDIENTE_APROBACION'" v-permiso="'AJUSTES_INVENTARIO_APROBAR'" flat round dense color="red-7" icon="close" @click="$emit('rechazar', p.row)"><q-tooltip>Rechazar ajuste</q-tooltip></q-btn>
          <q-btn v-if="p.row.estado === 'APROBADO'" v-permiso="'AJUSTES_INVENTARIO_APROBAR'" flat round dense color="orange-8" icon="play_arrow" @click="$emit('aplicar', p.row)"><q-tooltip>Aplicar ajuste y actualizar stock</q-tooltip></q-btn>
          <q-btn v-if="p.row.estado === 'APLICADO'" flat round dense color="red-6" icon="picture_as_pdf" @click="$emit('pdf', p.row)"><q-tooltip>Descargar PDF</q-tooltip></q-btn>
        </q-td>
      </template>
      <template #no-data><div class="full-width text-center q-pa-xl text-grey-6">No hay ajustes que coincidan con los filtros.</div></template>
      <template #pagination="scope"><div class="row items-center q-gutter-xs"><q-btn round flat dense icon="chevron_left" :disable="scope.isFirstPage" @click="scope.prevPage" /><span class="text-caption">Página {{ pagination.page }}</span><q-btn round flat dense icon="chevron_right" :disable="scope.isLastPage" @click="scope.nextPage" /></div></template>
    </q-table>
  </q-card>
</template>

<script setup>
defineProps({ rows: { type: Array, default: () => [] }, pagination: { type: Object, required: true }, loading: Boolean })
defineEmits(['request', 'ver', 'editar', 'eliminar', 'enviar', 'aprobar', 'rechazar', 'aplicar', 'pdf'])
const columns = [
  { name: 'ajuste', label: 'Ajuste', field: 'codigo', align: 'left', sortable: true },
  { name: 'fecha', label: 'Fecha', field: 'fecha', align: 'left', sortable: true },
  { name: 'tipo', label: 'Tipo / motivo', field: 'tipo', align: 'left' },
  { name: 'cantidad', label: 'Cantidad / ubicación', field: 'cantidad', align: 'left' },
  { name: 'solicitante', label: 'Solicitado por', field: 'solicitante', align: 'left' },
  { name: 'estado', label: 'Estado', field: 'estado', align: 'center' },
  { name: 'acciones', label: 'Acciones', align: 'center' }
]
const colorEstado = (estado) => ({ BORRADOR: 'grey-7', PENDIENTE_APROBACION: 'amber-8', APROBADO: 'blue-7', APLICADO: 'green-7', RECHAZADO: 'red-7', CANCELADO: 'red-5' }[estado] || 'grey-6')
const textoEstado = (estado) => estado?.replaceAll('_', ' ') || '--'
</script>

<style scoped>
.table-card { border-radius: 16px; border-color: #dfe7dc; overflow: hidden }
.muted { font-size: 11px; color: #7b857c; margin-top: 3px }
.actions { white-space: nowrap }
.q-table :deep(thead tr) { background: #f0f8e9 }
.q-table :deep(th) { font-weight: 900; color: #3f5140 }
</style>
