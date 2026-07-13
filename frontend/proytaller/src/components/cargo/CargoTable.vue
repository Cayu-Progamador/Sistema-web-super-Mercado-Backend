<template>
  <q-page class="q-pa-md page-bg">
    <q-card flat bordered class="table-card">
      <div class="row items-center justify-between q-pa-md table-header">
        <div>
          <div class="text-h6 text-weight-bold table-title">Lista de Cargos</div>
          <div class="text-caption table-subtitle">Administra los cargos del sistema</div>
        </div>
        <div class="row items-center q-gutter-sm">
          <q-btn class="btn-add" icon="add" label="Nuevo Cargo" @click="abrirForm()" unelevated />
        </div>
      </div>

      <div class="search-bar">
        <div class="row q-px-md q-py-sm items-center search-bar-inner">
          <div class="search-field nombre-field">
            <q-input
              v-model="search"
              outlined
              dense
              placeholder="Buscar por nombre..."
              clearable
              class="search-input"
              @keyup.enter="buscar"
            >
              <template v-slot:prepend>
                <q-icon name="search" class="search-icon" />
              </template>
            </q-input>
          </div>
          <div class="search-field estado-field">
            <q-select
              v-model="filtroEstado"
              :options="estadoOptions"
              outlined dense
              placeholder="Todos"
              map-options
              emit-value
              class="search-select"
            />
          </div>
          <div class="search-field acciones-field">
            <div class="search-actions">
              <q-btn class="search-btn" icon="search" label="Buscar" @click="buscar" />
              <q-btn class="clear-btn"  icon="clear" label="Limpiar" @click="limpiar" />
            </div>
          </div>
        </div>
      </div>

      <q-table
        flat
        :rows="cargos"
        :columns="columns"
        row-key="id"
        :loading="loading"
        class="custom-table"
        hide-pagination
        :rows-per-page-options="[0]"
      >
        <template v-slot:loading>
          <div class="loading-skeleton">
            <div v-for="n in 4" :key="n" class="skeleton-row">
              <div class="skeleton-cell skeleton-cell--sm"></div>
              <div class="skeleton-cell skeleton-cell--lg"></div>
              <div class="skeleton-cell skeleton-cell--xl"></div>
              <div class="skeleton-cell skeleton-cell--md"></div>
              <div class="skeleton-cell skeleton-cell--sm"></div>
            </div>
          </div>
        </template>

        <template v-slot:no-data>
          <div class="empty-state">
            <div class="empty-icon">
              <q-icon name="work_off" size="48px" />
            </div>
            <div class="empty-title">No hay cargos registrados</div>
            <div class="empty-desc">Haz clic en "Nuevo Cargo" para agregar el primer cargo.</div>
          </div>
        </template>

        <template v-slot:body-cell-numero="props">
          <q-td :props="props">
            <span class="row-num">{{ props.rowIndex + 1 }}</span>
          </q-td>
        </template>

        <template v-slot:body-cell-nombre="props">
          <q-td :props="props">
            <div class="cell-nombre">
              <div class="nombre-avatar">{{ props.row.nombre?.charAt(0).toUpperCase() }}</div>
              <span class="nombre-text">{{ props.row.nombre }}</span>
            </div>
          </q-td>
        </template>

        <template v-slot:body-cell-estado="props">
          <q-td :props="props">
            <span :class="['estado-badge', props.row.estado ? 'estado-activo' : 'estado-inactivo']">
              <span class="estado-dot"></span>
              {{ props.row.estado ? 'Activo' : 'Inactivo' }}
            </span>
          </q-td>
        </template>

        <template v-slot:body-cell-acciones="props">
          <q-td :props="props">
            <div class="row no-wrap justify-center q-gutter-xs">
              <q-btn flat round dense class="action-btn action-edit" icon="edit" @click="abrirForm(props.row)">
                <q-tooltip anchor="top middle" self="bottom middle" :offset="[0, 6]">Editar cargo</q-tooltip>
              </q-btn>
              <q-btn
                v-if="props.row.estado"
                flat round dense class="action-btn action-block"
                icon="block"
                @click="confirmarAccion('desactivar', props.row)"
              >
                <q-tooltip anchor="top middle" self="bottom middle" :offset="[0, 6]">Desactivar cargo</q-tooltip>
              </q-btn>
              <q-btn
                v-else
                flat round dense class="action-btn action-activate"
                icon="check"
                @click="confirmarAccion('activar', props.row)"
              >
                <q-tooltip anchor="top middle" self="bottom middle" :offset="[0, 6]">Activar cargo</q-tooltip>
              </q-btn>
            </div>
          </q-td>
        </template>
      </q-table>
    </q-card>

    <q-dialog v-model="mostrarForm" persistent transition-show="fade" transition-hide="fade">
      <CargoForm
        :cargo="cargoSeleccionado"
        :es-editar="esEditar"
        @guardar="onGuardar"
        @cerrar="cerrarForm"
      />
    </q-dialog>

    <ConfirmarCargoDialog
      v-model="mostrarConfirmar"
      :id="cargoSeleccionado?.id"
      :nombre="cargoSeleccionado?.nombre"
      :tipo="tipoConfirmar"
      @confirmar="onConfirmarAccion"
    />
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { listarCargos, activarCargo, desactivarCargo } from '../../api/cargo/cargo'
import CargoForm from './CargoForm.vue'
import ConfirmarCargoDialog from './ConfirmarCargoDialog.vue'

const $q = useQuasar()
const loading = ref(false)
const cargos = ref([])
const search = ref('')
const filtroEstado = ref('')

const estadoOptions = [
  { label: 'Todos', value: '' },
  { label: 'Activo', value: true },
  { label: 'Inactivo', value: false }
]

const buscar = () => {
  cargarCargos()
}

const limpiar = () => {
  search.value = ''
  filtroEstado.value = ''
  cargarCargos()
}
const mostrarForm = ref(false)
const mostrarConfirmar = ref(false)
const esEditar = ref(false)
const tipoConfirmar = ref('activar')
const cargoSeleccionado = ref(null)

const columns = [
  { name: 'numero', label: 'N°', align: 'center' },
  { name: 'nombre', label: 'Nombre', field: 'nombre', align: 'left', sortable: true },
  { name: 'descripcion', label: 'Descripción', field: 'descripcion', align: 'left' },
  { name: 'estado', label: 'Estado', field: 'estado', align: 'left' },
  { name: 'acciones', label: 'Acciones', align: 'center' }
]

const cargarCargos = async () => {
  loading.value = true
  try {
    cargos.value = await listarCargos({
      busqueda: search.value?.trim() || undefined,
      estado: filtroEstado.value !== '' ? filtroEstado.value : undefined
    })
  } catch {
    $q.notify({ type: 'negative', message: 'Error al cargar cargos' })
  } finally {
    loading.value = false
  }
}

const abrirForm = (row) => {
  cargoSeleccionado.value = row || null
  esEditar.value = !!row
  mostrarForm.value = true
}

const cerrarForm = () => {
  mostrarForm.value = false
  cargoSeleccionado.value = null
  esEditar.value = false
}

const onGuardar = () => {
  cargarCargos()
  cerrarForm()
}

const confirmarAccion = (tipo, row) => {
  tipoConfirmar.value = tipo
  cargoSeleccionado.value = row
  mostrarConfirmar.value = true
}

const onConfirmarAccion = async (id) => {
  try {
    if (tipoConfirmar.value === 'activar') {
      await activarCargo(id)
      $q.notify({ type: 'positive', message: 'Cargo activado correctamente' })
    } else {
      await desactivarCargo(id)
      $q.notify({ type: 'positive', message: 'Cargo desactivado correctamente' })
    }
    await cargarCargos()
  } catch {
    $q.notify({ type: 'negative', message: 'Error al cambiar estado del cargo' })
  }
}

onMounted(() => {
  cargarCargos()
})
</script>

<style scoped>
.page-bg {
  background: transparent;
}
.table-card {
  border-radius: 16px;
  border: 1px solid #e4edd8;
  box-shadow: 0 4px 20px rgba(42, 92, 26, 0.08);
  overflow: hidden;
  animation: cardFadeIn 0.35s ease-out;
}
@keyframes cardFadeIn {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}
.table-header {
  border-bottom: 1px solid #e4edd8;
  background: #f7faf4;
}
.table-title {
  color: #2a5c1a;
  font-family: 'DM Sans', sans-serif;
}
.table-subtitle {
  color: #7aaa4e;
  font-family: 'DM Sans', sans-serif;
}
.btn-add {
  background: #82bd43;
  color: #fff;
  border-radius: 12px;
  font-weight: 600;
  font-size: 13px;
  padding: 6px 18px;
  box-shadow: 0 4px 14px rgba(130, 189, 67, 0.3);
  text-transform: none;
  height: 38px;
  transition: all 0.25s ease;
}
.btn-add:hover {
  background: #4a8c25;
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(74, 140, 37, 0.35);
}
.btn-add:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(74, 140, 37, 0.25);
}
.custom-table :deep(thead th) {
  font-weight: 700;
  font-size: 13px;
  color: #5a8040;
  background: #f0f7e8;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  border-bottom: 2px solid #c8e0a0;
  padding: 14px 16px;
}
.custom-table :deep(tbody td) {
  font-size: 14px;
  color: #2a5c1a;
  border-bottom: 1px solid #eaf1e0;
  padding: 12px 16px;
  transition: background 0.2s ease;
}
.custom-table :deep(tbody tr) {
  transition: background 0.2s ease;
}
.custom-table :deep(tbody tr:hover) {
  background: #f0f7e8;
}
.custom-table :deep(tbody tr:last-child td) {
  border-bottom: none;
}
.row-num {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 8px;
  background: #f0f7e8;
  color: #5a8040;
  font-size: 12px;
  font-weight: 800;
}
.cell-nombre {
  display: flex;
  align-items: center;
  gap: 10px;
}
.nombre-avatar {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: #eaf4d8;
  border: 1px solid #c8e0a0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 800;
  color: #2a5c1a;
  flex-shrink: 0;
}
.nombre-text {
  font-weight: 600;
  color: #2a5c1a;
}
.search-bar {
  background: linear-gradient(90deg, #f7faf4, #f0f7e8);
  border-bottom: 1px solid #c8e0a0;
}
.search-bar-inner {
  gap: 8px;
}
.search-field {
  min-width: 0;
}
.nombre-field {
  flex: 2 1 200px;
}
.estado-field {
  flex: 1 1 140px;
}
.acciones-field {
  flex: 0 0 auto;
}
.search-actions {
  display: flex;
  gap: 6px;
  flex-wrap: nowrap;
}
.search-input :deep(.q-field__control) {
  background: #fff;
  border: 1px solid #c8e0a0;
  border-radius: 10px;
  height: 38px;
  transition: all 0.25s ease;
}
.search-input :deep(.q-field__control:focus-within) {
  border-color: #4a8c25;
  box-shadow: 0 0 0 3px rgba(74, 140, 37, 0.12);
}

.search-input :deep(.q-field__native) {
  color: #2a5c1a;
  font-weight: 500;
}

.search-input :deep(.q-field__native::placeholder) {
  color: #9dbf78;
  font-weight: 400;
}
.search-icon {
  color: #7aaa4e;
  font-size: 20px;
}
.search-select :deep(.q-field__inner) {
  border: 1px solid #c8e0a0;
  border-radius: 10px;
  background: #fff;
  height: 38px;
  transition: all 0.25s ease;
}
.search-select :deep(.q-field__control:focus-within) {
  border-color: #4a8c25;
  box-shadow: 0 0 0 3px rgba(74, 140, 37, 0.12);
}
.search-select :deep(.q-field__native) {
  color: #2a5c1a;
  font-weight: 500;
}
.search-btn {
  background: linear-gradient(135deg, #0d4d33, #0a3d28);
  color: #fff;
  border-radius: 10px;
  height: 38px;
  font-weight: 700;
  text-transform: none;
  padding: 0 20px;
  font-size: 13px;
  letter-spacing: 0.3px;
  transition: all 0.25s ease;
}
.search-btn:hover {
  background: linear-gradient(135deg, #0a3d28, #072d1e);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(13, 77, 51, 0.25);
}
.search-btn:active {
  transform: translateY(0);
}
.clear-btn {
  color: #0d4d33;
  border: none !important;
  border-radius: 10px;
  height: 38px;
  font-weight: 700;
  text-transform: none;
  padding: 0 20px;
  font-size: 13px;
  letter-spacing: 0.3px;
  transition: all 0.25s ease;
}

.clear-btn:hover {
  background: #dcf5eb !important;
  border-color: none !important;
}

.clear-btn:active {
  transform: scale(0.97);
}
.action-btn {
  border-radius: 8px;
  transition: all 0.2s ease;
}
.action-btn:hover {
  transform: scale(1.08);
}
.action-btn:active {
  transform: scale(0.92);
}
.action-edit {
  color: #4a8c25;
}
.action-edit:hover {
  background: #eaf4d8;
}
.action-block {
  color: #d97b1a;
}
.action-block:hover {
  background: #fef3e2;
}
.action-activate {
  color: #4a8c25;
}
.action-activate:hover {
  background: #eaf4d8;
}
.estado-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.2s ease;
}
.estado-activo {
  background: #eaf4d8;
  color: #2a7d2a;
  border: 1px solid #b8d98a;
}
.estado-inactivo {
  background: #fef3e2;
  color: #a05c10;
  border: 1px solid #f5c97a;
}
.estado-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
  animation: dotPulse 2s ease-in-out infinite;
}
@keyframes dotPulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}
.estado-activo .estado-dot {
  background: #4caf50;
}
.estado-inactivo .estado-dot {
  background: #ff9800;
}
.loading-skeleton {
  padding: 16px;
}
.skeleton-row {
  display: flex;
  gap: 16px;
  padding: 12px 16px;
  border-bottom: 1px solid #eaf1e0;
  animation: skeletonPulse 1.5s ease-in-out infinite;
}
@keyframes skeletonPulse {
  0%, 100% { opacity: 0.4; }
  50% { opacity: 0.8; }
}
.skeleton-row:last-child {
  border-bottom: none;
}
.skeleton-cell {
  height: 16px;
  border-radius: 6px;
  background: #e4edd8;
}
.skeleton-cell--sm { width: 40px; }
.skeleton-cell--md { width: 100px; }
.skeleton-cell--lg { width: 180px; }
.skeleton-cell--xl { width: 260px; }
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  text-align: center;
}
.empty-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #f0f7e8;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  color: #9dbf78;
}
.empty-title {
  font-size: 16px;
  font-weight: 800;
  color: #2a5c1a;
  margin-bottom: 6px;
  font-family: 'DM Sans', sans-serif;
}
.empty-desc {
  font-size: 13px;
  color: #7aaa4e;
  font-weight: 500;
  max-width: 280px;
  line-height: 1.5;
}
@media (max-width: 768px) {
  .search-bar-inner {
    flex-direction: column;
    gap: 8px;
  }
  .nombre-field {
    flex: 1 1 auto;
    width: 100%;
  }
  .estado-field {
    flex: 1 1 auto;
    width: 100%;
  }
  .acciones-field {
    flex: 1 1 auto;
    width: 100%;
  }
  .search-actions {
    width: 100%;
    margin-top: 18px;
    margin-bottom: 18px;
  }
  
  .search-actions .q-btn {
    flex: 1;
  }
  .table-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  .table-header .row {
    width: 100%;
  }
  .table-header .btn-add {
    width: 100%;
  }
}
@media (max-width: 600px) {
  .custom-table :deep(td:nth-child(3)),
  .custom-table :deep(th:nth-child(3)) {
    display: none;
  }
}
</style>
