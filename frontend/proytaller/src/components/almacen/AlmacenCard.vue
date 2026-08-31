<template>
  <q-card flat bordered class="alm-card" :class="{ 'alm-card--inactivo': almacen.estado === 'INACTIVO' }">
    <q-card-section class="q-pa-lg">
      <div class="alm-card-header">
        <div class="alm-card-icono"><q-icon name="warehouse" size="28px" /></div>
        <div class="col ellipsis">
          <div class="alm-card-nombre ellipsis">{{ almacen.nombre }}</div>
          <div class="alm-card-codigo">{{ almacen.codigo }}</div>
        </div>
        <q-badge rounded :color="almacen.estado === 'ACTIVO' ? 'green-7' : 'grey-6'" :label="almacen.estado" class="q-px-sm q-py-xs" />
      </div>

      <q-chip dense outline color="green-8" icon="category" class="q-mt-md">{{ textoTipoBackend(almacen.tipo) }}</q-chip>
      <p class="alm-card-descripcion">{{ almacen.descripcion || 'Sin descripción registrada.' }}</p>

      <div class="alm-datos-grid">
        <div><q-icon name="location_on" /> {{ almacen.ubicaciones }} ubicaciones</div>
        <div><q-icon name="inventory_2" /> {{ almacen.productos }} productos</div>
        <div><q-icon name="person" /> {{ almacen.responsable || 'Sin responsable' }}</div>
        <div><q-icon name="stacked_bar_chart" /> {{ almacen.stockTotal }} unidades</div>
      </div>

      <template v-if="almacen.capacidadMaxima > 0">
        <q-separator class="q-my-md" />
        <div class="row items-center justify-between q-mb-xs"><span class="alm-capacidad-label">Capacidad utilizada</span><strong :class="colorTexto">{{ almacen.ocupacion }}%</strong></div>
        <q-linear-progress rounded size="9px" :value="almacen.ocupacion / 100" :color="colorProgreso" track-color="grey-3" />
      </template>
    </q-card-section>

    <q-separator />
    <q-card-actions class="alm-card-acciones q-pa-sm q-px-md">
      <q-btn flat round color="green-8" icon="visibility" aria-label="Ver detalles del almacén" @click="$emit('ver', almacen)">
        <q-tooltip>Ver detalles del almacén</q-tooltip>
      </q-btn>
      <q-btn flat round color="orange-8" icon="edit" aria-label="Editar almacén" @click="$emit('editar', almacen)">
        <q-tooltip>Editar almacén</q-tooltip>
      </q-btn>
      <q-btn
        flat
        round
        :icon="almacen.estado === 'ACTIVO' ? 'block' : 'check_circle'"
        :color="almacen.estado === 'ACTIVO' ? 'orange-9' : 'green-8'"
        :aria-label="almacen.estado === 'ACTIVO' ? 'Inactivar almacén' : 'Activar almacén'"
        @click="$emit('estado', almacen)"
      >
        <q-tooltip>{{ almacen.estado === 'ACTIVO' ? 'Inactivar almacén' : 'Activar almacén' }}</q-tooltip>
      </q-btn>
      <q-space />
      <q-btn flat round icon="picture_as_pdf" color="red-7" @click="$emit('pdf', almacen)">
        <q-tooltip>Descargar PDF</q-tooltip>
      </q-btn>
    </q-card-actions>
  </q-card>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ almacen: { type: Object, required: true } })
defineEmits(['ver', 'editar', 'estado', 'pdf'])
function textoTipoBackend(tipo) {
  if (tipo === 'ALMACEN') return 'Almacén'
  if (tipo === 'SALA_VENTAS') return 'Sala de ventas'
  return textoTipo(tipo)
}
const colorProgreso = computed(() => props.almacen.ocupacion > 85 ? 'red-7' : props.almacen.ocupacion > 60 ? 'orange-7' : 'green-7')
const colorTexto = computed(() => props.almacen.ocupacion > 85 ? 'text-red-8' : props.almacen.ocupacion > 60 ? 'text-orange-9' : 'text-green-8')
function textoTipo(tipo) { return { PRINCIPAL: 'Principal', SECUNDARIO: 'Secundario', CAMARA_FRIA: 'Cámara Fría', DEPOSITO: 'Depósito' }[tipo] || tipo }
</script>

<style scoped>
.alm-card { height: 100%; display: flex; flex-direction: column; border-radius: 18px; border-color: #dce9d5 !important; box-shadow: 0 4px 18px rgba(42,92,26,.07); transition: transform .25s ease, box-shadow .25s ease, border-color .25s ease; overflow: hidden; }
.alm-card:hover { transform: translateY(-4px); border-color: #b9d8a9 !important; box-shadow: 0 14px 34px rgba(42,92,26,.14); }
.alm-card--inactivo { opacity: .82; }
.alm-card > .q-card__section { flex: 1; }
.alm-card-header { display: flex; align-items: flex-start; gap: 12px; min-width: 0; }
.alm-card-icono { width: 54px; height: 54px; flex: 0 0 54px; border-radius: 15px; display: grid; place-items: center; color: white; background: linear-gradient(135deg,#82bd43,#4a8c25); }
.alm-card-nombre { color: #2a5c1a; font-size: 17px; font-weight: 900; }
.alm-card-codigo { color: #8aa082; font-size: 12px; font-weight: 700; letter-spacing: .5px; }
.alm-card-descripcion { color: #667064; font-size: 13px; min-height: 42px; line-height: 1.55; margin: 10px 0 14px; }
.alm-datos-grid { display: grid; grid-template-columns: minmax(0,1fr) minmax(0,1fr); gap: 10px; color: #657561; font-size: 12px; }
.alm-datos-grid > div { min-width: 0; overflow-wrap: anywhere; }
.alm-datos-grid .q-icon { color: #4a8c25; font-size: 17px; margin-right: 4px; }
.alm-capacidad-label { color: #667064; font-size: 12px; font-weight: 700; }
.alm-card-acciones { gap: 4px; min-height: 56px; }
.alm-card-acciones .q-btn { min-width: 40px; min-height: 40px; }

@media (max-width: 599px) {
  .alm-card { border-radius: 15px; }
  .alm-card:hover { transform: none; box-shadow: 0 6px 20px rgba(42,92,26,.11); }
  .alm-card > .q-card__section { padding: 16px; }
  .alm-card-header { display: grid; grid-template-columns: 46px minmax(0,1fr) auto; align-items: start; gap: 10px; }
  .alm-card-icono { width: 46px; height: 46px; flex-basis: 46px; border-radius: 13px; }
  .alm-card-nombre { font-size: 16px; white-space: normal; line-height: 1.25; }
  .alm-card-descripcion { min-height: 0; }
  .alm-datos-grid { grid-template-columns: 1fr; gap: 9px; font-size: 13px; }
  .alm-card-acciones { display: grid; grid-template-columns: repeat(4, minmax(44px,1fr)); width: 100%; padding: 8px 12px; gap: 6px; }
  .alm-card-acciones .q-space { display: none; }
  .alm-card-acciones .q-btn { width: 100%; min-height: 44px; border-radius: 11px; }
}

@media (max-width: 360px) {
  .alm-card-header { grid-template-columns: 42px minmax(0,1fr); }
  .alm-card-header > .q-badge { grid-column: 2; justify-self: start; }
  .alm-card-icono { width: 42px; height: 42px; }
}
</style>
