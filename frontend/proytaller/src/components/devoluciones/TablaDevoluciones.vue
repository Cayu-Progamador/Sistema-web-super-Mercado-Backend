<template>
  <q-card flat bordered class="dv-table-card">
    <q-table
      :rows="rows"
      :columns="columnas"
      :loading="loading"
      row-key="idDevolucion"
      :pagination="paginacion"
      :rows-per-page-options="[10, 20, 50]"
      :filter="filtro"
      binary-state-sort
      flat
      class="dv-table"
      @update:pagination="paginacion = $event"
    >
      <template v-slot:loading>
        <q-inner-loading showing color="primary" />
      </template>

      <template v-slot:no-data>
        <div class="text-center q-pa-lg text-grey-6">
          <q-icon name="assignment_return" size="48px" color="grey-4" />
          <div class="q-mt-sm">No se encontraron devoluciones</div>
          <div class="text-caption text-grey-5">Ajuste los filtros o cree una nueva devolución.</div>
        </div>
      </template>

      <template v-slot:body-cell-numero="props">
        <q-td :props="props">
          <span class="dv-num" @click="$emit('ver', props.row)">{{ props.value }}</span>
        </q-td>
      </template>

      <template v-slot:body-cell-numeroRecepcion="props">
        <q-td :props="props">
          <span class="dv-num">{{ props.value }}</span>
        </q-td>
      </template>

      <template v-slot:body-cell-proveedor="props">
        <q-td :props="props">
          <div class="row items-center no-wrap">
            <q-avatar class="dv-prod-avatar">
              {{ iniciales(props.value) }}
            </q-avatar>
            <div class="text-body2 text-weight-medium">{{ props.value }}</div>
          </div>
        </q-td>
      </template>

      <template v-slot:body-cell-fechaDevolucion="props">
        <q-td :props="props">{{ formatFecha(props.value) }}</q-td>
      </template>

      <template v-slot:body-cell-productos="props">
        <q-td :props="props">
          <q-badge outline color="green-7" class="dv-cant-chip">
            <q-icon name="inventory_2" size="14px" class="q-mr-xs" />
            {{ props.row.productos }}
          </q-badge>
        </q-td>
      </template>

      <template v-slot:body-cell-cantidadTotal="props">
        <q-td :props="props">
          <span class="text-weight-bold">{{ formatNum(props.value) }}</span>
        </q-td>
      </template>

      <template v-slot:body-cell-estado="props">
        <q-td :props="props">
          <q-chip
            dense
            size="11px"
            class="dv-chip-estado"
            text-color="white"
            :style="{ background: colorEstadoD(props.value) }"
          >
            {{ props.value.replace(/_/g, ' ') }}
          </q-chip>
        </q-td>
      </template>

      <template v-slot:body-cell-usuario="props">
        <q-td :props="props">
          <div class="text-body2">{{ props.value }}</div>
        </q-td>
      </template>

      <template v-slot:body-cell-acciones="props">
        <q-td :props="props" class="text-center">
          <q-btn
            flat
            round
            icon="visibility"
            size="md"
            style="color: #006051"
            @click="$emit('ver', props.row)"
          >
            <q-tooltip class="bg-primary text-white">Ver detalle</q-tooltip>
          </q-btn>

          <template v-if="props.row.estado === 'BORRADOR'">
            <q-btn
              flat
              round
              icon="edit"
              size="md"
              style="color: #F57C00"
              @click="$emit('editar', props.row)"
            >
              <q-tooltip class="bg-primary text-white">Editar</q-tooltip>
            </q-btn>
            <q-btn
              flat
              round
              icon="check_circle"
              size="md"
              style="color: #2E7D32"
              @click="$emit('confirmar', props.row)"
            >
              <q-tooltip class="bg-primary text-white">Confirmar</q-tooltip>
            </q-btn>
            <q-btn
              flat
              round
              icon="cancel"
              size="md"
              style="color: #D32F2F"
              @click="$emit('anular', props.row)"
            >
              <q-tooltip class="bg-primary text-white">Anular</q-tooltip>
            </q-btn>
          </template>

          <template v-else-if="props.row.estado === 'CONFIRMADA'">
            <q-btn
              flat
              round
              icon="send"
              size="md"
              style="color: #2E7D32"
              @click="$emit('enviar', props.row)"
            >
              <q-tooltip class="bg-primary text-white">Enviar</q-tooltip>
            </q-btn>
          </template>

          <template v-else-if="props.row.estado === 'ENVIADA'">
            <q-btn
              flat
              round
              icon="assignment_turned_in"
              size="md"
              style="color: #2E7D32"
              @click="$emit('registrarRecepcion', props.row)"
            >
              <q-tooltip class="bg-primary text-white">Completar devolución</q-tooltip>
            </q-btn>
          </template>

          <template v-else>
            <q-btn
              flat
              round
              icon="picture_as_pdf"
              size="md"
              style="color: #5B6675"
              @click="$emit('pdf', props.row)"
            >
              <q-tooltip class="bg-primary text-white">Generar PDF</q-tooltip>
            </q-btn>
          </template>
        </q-td>
      </template>
    </q-table>
  </q-card>
</template>

<script setup>
import { ref } from 'vue'
import { colorEstadoDevolucion } from '../../util/devoluciones'

const props = defineProps({
  rows: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  filtro: { type: String, default: '' }
})

const colorEstadoD = colorEstadoDevolucion

defineEmits(['ver', 'editar', 'confirmar', 'enviar', 'anular', 'registrarRecepcion', 'pdf'])

const paginacion = ref({ sortBy: 'fechaDevolucion', descending: true, page: 1, rowsPerPage: 10 })

const columnas = [
  { name: 'numero', label: 'N.º Devolución', field: 'numero', align: 'left', sortable: true, style: 'min-width: 130px' },
  { name: 'numeroRecepcion', label: 'Recepción', field: 'numeroRecepcion', align: 'left', sortable: true, style: 'min-width: 120px' },
  { name: 'proveedor', label: 'Proveedor', field: 'proveedor', align: 'left', sortable: true, style: 'min-width: 190px' },
  { name: 'fechaDevolucion', label: 'Fecha', field: 'fechaDevolucion', align: 'left', sortable: true, style: 'width: 110px' },
  { name: 'productos', label: 'Productos', field: 'productos', align: 'center', style: 'width: 100px' },
  { name: 'cantidadTotal', label: 'Cantidad', field: 'cantidadTotal', align: 'center', sortable: true, style: 'width: 90px' },
  { name: 'estado', label: 'Estado', field: 'estado', align: 'center', sortable: true, style: 'min-width: 120px' },
  { name: 'usuario', label: 'Usuario', field: 'usuario', align: 'left', sortable: true, style: 'min-width: 130px' },
  { name: 'acciones', label: 'Acciones', align: 'center', field: 'idDevolucion', sortable: false, style: 'width: 220px' }
]

function formatFecha(fecha) {
  if (!fecha) return '—'
  const d = new Date(fecha)
  if (isNaN(d.getTime())) return fecha
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}

function formatNum(n) {
  return Number(n || 0).toLocaleString('es-BO')
}

function iniciales(nombre) {
  if (!nombre) return '?'
  return nombre.split(' ').map((n) => n[0]).slice(0, 2).join('').toUpperCase()
}
</script>

<style scoped src="../../assets/styles/devoluciones/devoluciones.css"></style>
