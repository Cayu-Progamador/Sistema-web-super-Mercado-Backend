<template>
  <q-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    maximized
    transition-show="slide-up"
    transition-hide="slide-down"
    class="new-contract-dialog"
  >
    <q-card class="dialog-card">
      <q-card-section class="dialog-header q-px-lg q-py-md">
        <div class="row items-center justify-between">
          <div class="text-h5 text-weight-bold">{{ esEditar ? 'Editar Contrato' : 'Nuevo Contrato' }}</div>
          <q-btn flat round dense icon="close" v-close-popup />
        </div>
        <q-tabs
          v-model="tab"
          dense
          active-color="primary"
          indicator-color="primary"
          align="left"
          narrow-indicator
          class="q-mt-sm"
        >
          <q-tab name="empleado" icon="person" label="Empleado" />
          <q-tab name="contrato" icon="description" label="Contrato" />
          <q-tab name="salario" icon="payments" label="Salario" />
          <q-tab name="asistencia" icon="fingerprint" label="Control Asistencia" />
          <q-tab name="horario" icon="schedule" label="Horario" />
        </q-tabs>
      </q-card-section>

      <q-separator />

      <q-card-section class="dialog-body q-pa-lg">
        <q-tab-panels v-model="tab" animated class="tab-panels">
          <!-- Tab 1: Empleado -->
          <q-tab-panel name="empleado">
            <div class="text-subtitle1 text-weight-bold q-mb-md">Seleccionar Empleado</div>
            <q-select
              v-model="form.empleado"
              :options="empleadosOptions"
              label="Empleado *"
              outlined
              use-input
              input-debounce="300"
              @filter="filtrarEmpleados"
              option-label="label"
              option-value="id"
              clearable
              :rules="[v => !!v || 'Seleccione un empleado']"
              class="q-mb-md"
            >
              <template v-slot:option="scope">
                <q-item v-bind="scope.itemProps">
                  <q-item-section avatar>
                    <q-avatar size="32px">
                      <img :src="`https://ui-avatars.com/api/?name=${encodeURIComponent(scope.opt.label)}&background=2E7D32&color=fff&size=32`" />
                    </q-avatar>
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{ scope.opt.label }}</q-item-label>
                    <q-item-label caption>CI: {{ scope.opt.cedula }}</q-item-label>
                  </q-item-section>
                </q-item>
              </template>
            </q-select>
            <div v-if="form.empleado" class="bg-green-1 q-pa-md rounded-borders">
              <div class="row q-col-gutter-sm">
                <div class="col-6 text-caption text-grey-6">Email: {{ form.empleado.email }}</div>
                <div class="col-6 text-caption text-grey-6">Teléfono: {{ form.empleado.telefono }}</div>
              </div>
            </div>
          </q-tab-panel>

          <!-- Tab 2: Contrato -->
          <q-tab-panel name="contrato">
            <div class="text-subtitle1 text-weight-bold q-mb-md">Datos del Contrato</div>
            <div class="row q-col-gutter-md">
              <div class="col-12 col-md-6">
                <q-select v-model="form.cargo" :options="cargoOptions" label="Cargo *" outlined emit-value map-options :rules="[v => !!v || 'Seleccione un cargo']" class="q-mb-md" />
              </div>
              <div class="col-12 col-md-6">
                <q-select v-model="form.tipoContrato" :options="tipoContratoOptions" label="Tipo Contrato *" outlined emit-value map-options :rules="[v => !!v || 'Seleccione tipo']" class="q-mb-md" />
              </div>
              <div class="col-12 col-md-6">
                <q-select v-model="form.tipoJornada" :options="tipoJornadaOptions" label="Tipo Jornada *" outlined emit-value map-options :rules="[v => !!v || 'Seleccione jornada']" class="q-mb-md" />
              </div>
              <div class="col-12 col-md-6">
                <q-input v-model="form.fechaInicio" label="Fecha Inicio *" outlined type="date" :rules="[v => !!v || 'Requerido']" class="q-mb-md" />
              </div>
              <div class="col-12 col-md-6">
                <q-input v-model="form.fechaFin" label="Fecha Fin" outlined type="date" :disable="esIndefinido" :hint="esIndefinido ? 'Contrato indefinido — no aplica fecha de fin' : ''" class="q-mb-md" />
              </div>
              <div class="col-12">
                <q-input v-model="form.observaciones" label="Observaciones" outlined type="textarea" maxlength="500" class="q-mb-md" />
              </div>
            </div>
          </q-tab-panel>

          <!-- Tab 3: Salario -->
          <q-tab-panel name="salario">
            <div class="text-subtitle1 text-weight-bold q-mb-md">Información Salarial</div>
            <div class="row q-col-gutter-md">
              <div class="col-12 col-md-4">
              <div class="col-12 col-md-6">
                <q-input v-model="form.sueldoBase" label="Sueldo Base ($) *" outlined type="number" step="0.01" min="0" placeholder="0.00" @keydown="soloDecimales" class="no-spinners" :rules="[v => (v ?? 0) > 0 || 'Debe ser mayor a 0']">
                  <template v-slot:prepend>
                    <span class="text-weight-bold text-grey-7">Bs.</span>
                  </template>
                  <template v-slot:append>
                    <q-btn unelevated round dense size="xs" color="primary" flat icon="add" @click="ajustarSueldo(10)" />
                    <q-btn unelevated round dense size="xs" color="primary" flat icon="remove" @click="ajustarSueldo(-10)" class="q-ml-xs" />
                  </template>
                </q-input>
              </div>
              </div>
              <div class="col-12 col-md-4">
              </div>
              <div class="col-12 col-md-4">
                <q-select v-model="form.tipoPago" :options="tipoPagoOptions" label="Tipo de Pago" outlined emit-value map-options clearable class="q-mb-md" hint="Semanal, Quincenal, Mensual" />
              </div>
            </div>
          </q-tab-panel>

          <!-- Tab 4: Asistencia -->
          <q-tab-panel name="asistencia">
            <div class="text-subtitle1 text-weight-bold q-mb-md">Control de Asistencia</div>
            <div class="row items-center q-mb-lg">
              <span class="q-mr-md text-body1">¿Controlar asistencia?</span>
              <q-toggle v-model="form.controlaAsistencia" color="green-7" :label="form.controlaAsistencia ? 'Sí' : 'No'" size="md" :disable="asistenciaDisable" />
            </div>
            <q-banner
              v-if="form.controlaAsistencia"
              class="bg-green-1 text-green-9 q-mb-md rounded-borders"
            >
              <template v-slot:avatar>
                <q-icon name="info" color="green-8" />
              </template>
              El empleado deberá marcar entrada y salida diariamente. Se generará planilla con descuentos por ausencias.
            </q-banner>
            <q-banner
              v-else
              class="bg-blue-1 text-blue-9 rounded-borders"
            >
              <template v-slot:avatar>
                <q-icon name="info" color="blue-8" />
              </template>
              No se registrará asistencia diaria. La planilla se calculará en base al sueldo fijo.
            </q-banner>
          </q-tab-panel>

          <!-- Tab 5: Horario -->
          <q-tab-panel name="horario">
            <q-slide-transition>
              <div v-if="form.controlaAsistencia" key="horario-content">
                <div class="text-subtitle1 text-weight-bold q-mb-md">Asignación de Horario</div>
                <div class="row q-col-gutter-md">
                  <div class="col-12 col-md-6">
                    <q-input v-model="form.horasDia" label="Horas por Día *" outlined type="number" min="1" max="24" @keydown="soloNumeros" :disable="horasDiaDisable" :hint="horasDiaDisable ? `Fijo para ${jornadaSeleccionada?.label || ''}` : ''" class="q-mb-md" />
                  </div>
                  <div class="col-12 col-md-6">
                    <q-input v-model="form.horasSemana" label="Horas por Semana" outlined type="number" disable class="q-mb-md"
                      :hint="`Calculado: ${form.horasDia || 0}h × ${diasSeleccionados} día(s) = ${form.horasSemana}h/sem`" />
                  </div>
                </div>
                <q-separator class="q-my-md" />
                <div class="text-subtitle2 text-weight-bold q-mb-sm">Asignación de Turno</div>
                <div class="q-col-gutter-md">
                  <div class="row q-col-gutter-md">
                    <div class="col-12 col-md-6">
                      <q-select v-model="form.turnoId" :options="turnoOptionsFiltrados" label="Turno" outlined emit-value map-options clearable class="q-mb-md" />
                    </div>
                    <div class="col-12 col-md-6">
                      <q-input v-model="form.toleranciaMinutos" label="Tolerancia (min)" outlined type="number" min="0" @keydown="soloNumeros" hint="Minutos de gracia para llegada tarde" class="q-mb-md" />
                    </div>
                  </div>
                  <q-slide-transition>
                    <div v-if="turnoSeleccionado" class="bg-grey-2 q-pa-sm rounded-borders q-mb-md">
                      <div class="row q-col-gutter-sm items-center">
                        <div class="col-auto">
                          <q-icon name="schedule" color="primary" size="20px" />
                        </div>
                        <div class="col">
                          <span class="text-caption text-grey-7">Entrada: <strong class="text-primary">{{ turnoSeleccionado.horaEntrada }}</strong></span>
                          <span class="q-mx-sm text-grey-4">|</span>
                          <span class="text-caption text-grey-7">Salida: <strong class="text-primary">{{ turnoSeleccionado.horaSalida }}</strong></span>
                        </div>
                      </div>
                    </div>
                  </q-slide-transition>
                <div class="text-caption text-grey-6 q-mb-sm">Días laborables:</div>
                <div class="row q-gutter-sm">
                  <q-checkbox v-for="d in diasSemana" :key="d.val" v-model="form.dias[d.val]" :label="d.label" color="primary" dense />
                </div>
              </div>
              </div>
            </q-slide-transition>
            <div v-if="!form.controlaAsistencia" class="bg-blue-1 text-blue-9 q-pa-md rounded-borders">
              <div class="row items-center">
                <q-icon name="info" class="q-mr-md" size="24px" />
                <span>Sin control de asistencia — no aplica horario ni turno</span>
              </div>
            </div>
          </q-tab-panel>
        </q-tab-panels>
      </q-card-section>

      <q-separator />

      <q-card-actions align="right" class="q-pa-md dialog-actions">
        <q-btn flat label="Cancelar" color="grey-7" v-close-popup no-caps />
        <q-btn unelevated color="primary" label="Guardar" @click="guardar" no-caps :loading="saving" :disable="saving" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useQuasar } from 'quasar'
import { createContrato, updateContrato, getContrato, listarTiposContrato, listarTiposJornada, listarTurnos, listarTiposPago } from '../../api/contrato/contrato'
import { listarEmpleadosDisponibles } from '../../api/empleado/empleado'
import { listarCargos } from '../../api/cargo/cargo'

const props = defineProps({ modelValue: Boolean, contratoId: [Number, null], esEditar: Boolean })
const emit = defineEmits(['update:modelValue', 'saved'])

const $q = useQuasar()
const tab = ref('empleado')
const saving = ref(false)

const form = reactive({
  empleado: null,
  cargo: null,
  tipoContrato: null,
  tipoJornada: null,
  fechaInicio: '',
  fechaFin: '',
  observaciones: '',
  sueldoBase: null,
  horasDia: 8,
  horasSemana: 40,
  controlaAsistencia: true,
  turnoId: null,
  toleranciaMinutos: null,
  tipoPago: null,
  dias: {
    lunes: true,
    martes: true,
    miercoles: true,
    jueves: true,
    viernes: true,
    sabado: false,
    domingo: false
  }
})

const empleadosOptions = ref([])
const cargoOptions = ref([])
const tipoContratoOptions = ref([])
const tipoJornadaOptions = ref([])
const turnoOptions = ref([])
const tipoPagoOptions = ref([])
const diasSemana = [
  { label: 'Lunes', val: 'lunes' },
  { label: 'Martes', val: 'martes' },
  { label: 'Miércoles', val: 'miercoles' },
  { label: 'Jueves', val: 'jueves' },
  { label: 'Viernes', val: 'viernes' },
  { label: 'Sábado', val: 'sabado' },
  { label: 'Domingo', val: 'domingo' }
]

const diasSeleccionados = computed(() => Object.values(form.dias).filter(Boolean).length)

const tipoJornadaHorasMap = {
  'Tiempo Completo': 8,
  'Medio Tiempo': 4,
  'Por Horas': null
}

const esIndefinido = computed(() => {
  if (!form.tipoContrato) return false
  const opt = tipoContratoOptions.value.find(o => o.value === form.tipoContrato)
  return opt ? opt.label.toLowerCase() === 'indefinido' : false
})

const jornadaSeleccionada = computed(() => {
  if (!form.tipoJornada) return null
  return tipoJornadaOptions.value.find(o => o.value === form.tipoJornada) || null
})

const horasFijasJornada = computed(() => {
  if (!jornadaSeleccionada.value) return null
  const label = jornadaSeleccionada.value.label
  return tipoJornadaHorasMap[label] !== undefined ? tipoJornadaHorasMap[label] : null
})

const horasDiaDisable = computed(() => horasFijasJornada.value !== null)

const cargosConAsistencia = new Set(['Cajero'])

const cargoSeleccionado = computed(() => {
  if (!form.cargo) return null
  return cargoOptions.value.find(o => o.value === form.cargo) || null
})

const requiereAsistencia = computed(() => {
  if (!cargoSeleccionado.value) return true
  return cargosConAsistencia.has(cargoSeleccionado.value.label)
})

const asistenciaDisable = computed(() => !requiereAsistencia.value)

const jornadaTurnosMap = {
  'Tiempo Completo': ['Completo'],
  'Medio Tiempo': ['Mañana', 'Tarde'],
  'Por Horas': null
}

const turnoOptionsFiltrados = computed(() => {
  if (!jornadaSeleccionada.value) return turnoOptions.value
  const permitidos = jornadaTurnosMap[jornadaSeleccionada.value.label]
  if (!permitidos) return turnoOptions.value
  return turnoOptions.value.filter(t => permitidos.includes(t.label))
})

const turnoSeleccionado = computed(() => {
  if (!form.turnoId) return null
  return turnoOptions.value.find(t => t.value === form.turnoId) || null
})

function recalcularHorasSemana() {
  const horas = parseInt(form.horasDia) || 0
  form.horasSemana = horas * diasSeleccionados.value
}

function manejarTipoContrato() {
  if (esIndefinido.value) {
    form.fechaFin = ''
  }
}

function soloNumeros(event) {
  if (event.ctrlKey || event.altKey || event.metaKey) return
  const teclasPermitidas = ['Backspace', 'Delete', 'Tab', 'Escape', 'Enter', 'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown', 'Home', 'End']
  if (teclasPermitidas.includes(event.key)) return
  if (!/^\d$/.test(event.key)) {
    event.preventDefault()
  }
}

function soloDecimales(event) {
  if (event.ctrlKey || event.altKey || event.metaKey) return
  const teclasPermitidas = ['Backspace', 'Delete', 'Tab', 'Escape', 'Enter', 'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown', 'Home', 'End']
  if (teclasPermitidas.includes(event.key)) return
  if (event.key === '.' && !event.target.value.includes('.')) return
  if (!/^\d$/.test(event.key)) {
    event.preventDefault()
  }
}

function ajustarSueldo(cambio) {
  const actual = parseFloat(form.sueldoBase) || 0
  form.sueldoBase = Math.max(0, +(actual + cambio).toFixed(2))
}

watch([() => form.horasDia, form.dias], recalcularHorasSemana, { deep: true })
watch(() => form.tipoContrato, manejarTipoContrato)
watch(() => form.tipoJornada, (val) => {
  if (!val) return
  const horas = horasFijasJornada.value
  if (horas !== null) {
    form.horasDia = horas
  }
  if (form.turnoId && turnoOptionsFiltrados.value.length > 0 && !turnoOptionsFiltrados.value.some(t => t.value === form.turnoId)) {
    form.turnoId = null
  }
})
watch(() => form.cargo, (val) => {
  if (!val) return
  form.controlaAsistencia = requiereAsistencia.value
})

async function filtrarEmpleados(val, update) {
  try {
    const res = await listarEmpleadosDisponibles({ busqueda: val, page: 0, size: 20 })
    const items = res?.content || []
    update(() => {
      empleadosOptions.value = items.map(e => ({
        id: e.id,
        label: e.nombreCompleto,
        cedula: e.cedula,
        email: e.email,
        telefono: e.telefono
      }))
    })
  } catch {
    update(() => { empleadosOptions.value = [] })
  }
}

async function cargarCargos() {
  try {
    const res = await listarCargos()
    cargoOptions.value = (res || []).map(c => ({ label: c.nombre, value: c.id }))
  } catch { cargoOptions.value = [] }
}

async function cargarTipos() {
  try {
    const [tcRes, tjRes] = await Promise.all([listarTiposContrato(), listarTiposJornada()])
    tipoContratoOptions.value = (tcRes || []).map(t => ({ label: t.nombre, value: t.id }))
    tipoJornadaOptions.value = (tjRes || []).map(t => ({ label: t.nombre, value: t.id }))
  } catch {
    tipoContratoOptions.value = []
    tipoJornadaOptions.value = []
  }
}

async function cargarTurnos() {
  try {
    const res = await listarTurnos()
    turnoOptions.value = (res || []).map(t => ({
      label: t.nombre,
      value: t.id,
      horaEntrada: t.horaEntrada,
      horaSalida: t.horaSalida
    }))
  } catch { turnoOptions.value = [] }
}

async function cargarTiposPago() {
  try {
    const res = await listarTiposPago()
    tipoPagoOptions.value = (res || []).map(t => ({ label: t.nombre, value: t.id }))
  } catch { tipoPagoOptions.value = [] }
}

async function guardar() {
  saving.value = true
  try {
    const payload = {
      empleadoId: form.empleado?.id,
      cargoId: form.cargo,
      tipoContratoId: form.tipoContrato,
      tipoJornadaId: form.tipoJornada,
      fechaInicio: form.fechaInicio,
      fechaFin: form.fechaFin || null,
      observaciones: form.observaciones || null,
      sueldoBase: parseFloat(form.sueldoBase) || 0,
      horasDia: form.controlaAsistencia ? (parseInt(form.horasDia) || null) : null,
      horasSemana: form.controlaAsistencia ? (parseInt(form.horasSemana) || null) : null,
      controlaAsistencia: form.controlaAsistencia,
      toleranciaMinutos: form.toleranciaMinutos ? parseInt(form.toleranciaMinutos) : null,
      tipoPagoId: form.tipoPago || null,
      contratoTurno: form.turnoId ? {
        turnoId: form.turnoId,
        lunes: form.dias.lunes,
        martes: form.dias.martes,
        miercoles: form.dias.miercoles,
        jueves: form.dias.jueves,
        viernes: form.dias.viernes,
        sabado: form.dias.sabado,
        domingo: form.dias.domingo
      } : null
    }

    if (props.esEditar) {
      await updateContrato(props.contratoId, payload)
      $q.notify({ type: 'positive', message: 'Contrato actualizado correctamente' })
    } else {
      await createContrato({ ...payload, estado: 'ACTIVO' })
      $q.notify({ type: 'positive', message: 'Contrato creado correctamente' })
    }

    emit('saved')
    emit('update:modelValue', false)
  } catch (error) {
    $q.notify({ type: 'negative', message: error.response?.data?.message || 'Error al guardar contrato' })
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  cargarCargos()
  cargarTipos()
  cargarTurnos()
  cargarTiposPago()
})

watch(() => props.contratoId, async (id) => {
  if (!id) return
  try {
    const c = await getContrato(id)
    form.fechaInicio = c.fechaInicio || ''
    form.fechaFin = c.fechaFin || ''
    form.observaciones = c.observaciones || ''
    form.sueldoBase = c.sueldoBase || null
    form.horasDia = (c.controlaAsistencia ? (c.horasDia ?? 8) : null)
    form.horasSemana = (c.controlaAsistencia ? (c.horasSemana ?? null) : null)
    form.controlaAsistencia = c.controlaAsistencia ?? true
    form.toleranciaMinutos = c.toleranciaMinutos ?? null
    form.tipoPago = c.tipoPagoId || null
    form.cargo = c.cargoId || null
    form.tipoContrato = c.tipoContratoId || null
    form.tipoJornada = c.tipoJornadaId || null
    if (c.contratoTurno) {
      form.turnoId = c.contratoTurno.turnoId || null
      if (c.contratoTurno.dias) Object.assign(form.dias, c.contratoTurno.dias)
    }
    if (c.empleado) {
      form.empleado = {
        id: c.empleado.id,
        label: `${c.empleado.nombres} ${c.empleado.apellidos}`,
        cedula: c.empleado.cedula,
        email: c.empleado.email,
        telefono: c.empleado.telefono
      }
    }
  } catch { /* silent */ }
}, { immediate: true })
</script>

<style scoped>
.new-contract-dialog :deep(.q-dialog__inner) {
  padding: 20px;
}
.dialog-card {
  border-radius: 16px;
  max-width: 900px;
  margin: 0 auto;
}
.dialog-header {
  background: #f9fafb;
  border-radius: 16px 16px 0 0;
}
.dialog-body {
  min-height: 350px;
  background: #fafafa;
}
.tab-panels {
  background: transparent;
}
.dialog-actions {
  background: #f9fafb;
  border-radius: 0 0 16px 16px;
}
.no-spinners :deep(input[type=number])::-webkit-inner-spin-button,
.no-spinners :deep(input[type=number])::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
.no-spinners :deep(input[type=number]) {
  -moz-appearance: textfield;
}
</style>
