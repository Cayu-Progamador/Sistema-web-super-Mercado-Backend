<template>
  <q-drawer
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    side="right"
    overlay
    :width="380"
    class="ausentes-drawer"
  >
    <div class="drawer-inner">
      <div class="drawer-header">
        <div class="drawer-title-row">
          <div class="drawer-icon">
            <q-icon name="person_off" size="20px" color="#2E7D32" />
          </div>
          <div>
            <div class="drawer-title">Empleados ausentes</div>
            <div class="drawer-subtitle">{{ empleados.length }} sin registro {{ modo === 'dia' ? 'hoy' : 'en ' + mesLabel }}</div>
          </div>
          <q-btn flat round dense icon="close" class="drawer-close" @click="cerrar" />
        </div>

        <div class="drawer-filtro-fecha">
          <q-btn dense flat round icon="chevron_left" size="md" class="month-nav" @click="cambiarMes(-1)" />
          <q-input
            v-model="fechaLocal"
            type="month"
            outlined
            dense
            class="month-input"
            @update:model-value="onFechaChange"
          />
          <q-btn dense flat round icon="chevron_right" size="md" class="month-nav" @click="cambiarMes(1)" />
          <q-btn dense flat no-caps icon="today" size="sm" class="hoy-btn" label="Hoy" @click="irHoy" />
        </div>
      </div>

      <div class="drawer-body">
        <div v-if="empleados.length === 0" class="empty-state">
          <q-icon name="check_circle" size="48px" color="green-8" />
          <div class="empty-text">{{ modo === 'dia' ? 'Todos marcaron asistencia hoy' : 'Todos marcaron asistencia en ' + mesLabel }}</div>
        </div>

        <div v-for="emp in empleados" :key="emp.id" class="employee-row" @click="toggleExpand(emp.id)">
          <div class="emp-avatar">
            <img
              :src="`https://ui-avatars.com/api/?name=${encodeURIComponent(emp.nombre)}&background=2E7D32&color=fff&size=40`"
            />
          </div>
          <div class="emp-info">
            <div class="emp-nombre">{{ emp.nombre }}</div>
            <div class="emp-cargo">{{ emp.cargo }}</div>
            <div v-if="modo === 'mes' && emp.dias" class="emp-dias-count">{{ emp.dias.length }} día(s) sin marcar</div>
          </div>
          <div class="emp-actions">
            <q-btn v-if="modo === 'mes' && emp.dias" flat round dense :icon="expandido(emp.id) ? 'expand_less' : 'expand_more'" size="sm" class="action-btn" @click.stop="toggleExpand(emp.id)">
              <q-tooltip>{{ expandido(emp.id) ? 'Ocultar días' : 'Ver días' }}</q-tooltip>
            </q-btn>
          </div>
        </div>

        <q-slide-transition v-for="emp in empleados" :key="'dias-' + emp.id">
          <div v-if="expandido(emp.id) && modo === 'mes' && emp.dias" class="dias-container">
            <div v-for="dia in emp.dias" :key="dia" class="dia-chip">
              <q-icon name="calendar_today" size="14px" class="dia-icon" />
              {{ formatearDia(dia) }}
            </div>
          </div>
        </q-slide-transition>
      </div>
    </div>
  </q-drawer>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  empleados: { type: Array, default: () => [] },
  mes: { type: Number, default: new Date().getMonth() + 1 },
  anio: { type: Number, default: new Date().getFullYear() },
  modo: { type: String, default: 'dia' }
})
const emit = defineEmits(['update:modelValue', 'cambiar-mes', 'ir-hoy'])

const expandedIds = ref(new Set())

function expandido(id) {
  return expandedIds.value.has(id)
}
function toggleExpand(id) {
  const s = new Set(expandedIds.value)
  if (s.has(id)) { s.delete(id) } else { s.add(id) }
  expandedIds.value = s
}

const fechaLocal = ref(`${props.anio}-${String(props.mes).padStart(2, '0')}`)

watch(() => [props.mes, props.anio], ([m, a]) => {
  fechaLocal.value = `${a}-${String(m).padStart(2, '0')}`
})

const meses = ['Enero','Febrero','Marzo','Abril','Mayo','Junio','Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre']
const mesLabel = computed(() => `${meses[props.mes - 1]} ${props.anio}`)

const diasSemana = ['Dom','Lun','Mar','Mié','Jue','Vie','Sáb']

function formatearDia(fechaStr) {
  const d = new Date(fechaStr + 'T00:00:00')
  const dia = String(d.getDate()).padStart(2, '0')
  const mes = String(d.getMonth() + 1).padStart(2, '0')
  const nombreDia = diasSemana[d.getDay()]
  return `${dia}/${mes} (${nombreDia})`
}

function cerrar() {
  emit('update:modelValue', false)
}
function cambiarMes(delta) {
  emit('cambiar-mes', delta)
}
function onFechaChange(val) {
  if (!val) return
  const [a, m] = val.split('-').map(Number)
  emit('cambiar-mes', 0, m, a)
}
function irHoy() {
  emit('ir-hoy')
}
</script>

<style scoped>
.ausentes-drawer :deep(.q-drawer__content) {
  background: #fafafa;
}
.drawer-inner {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.drawer-header {
  padding: 16px 20px 12px;
  background: linear-gradient(135deg, #e8f5e9, #fff);
  border-bottom: 1px solid #f0f0f0;
}
.drawer-title-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.drawer-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: rgba(46, 125, 50, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.drawer-title {
  font-family: 'Nunito', sans-serif;
  font-size: 17px;
  font-weight: 700;
  color: #1a1a1a;
}
.drawer-subtitle {
  font-family: 'Nunito', sans-serif;
  font-size: 13px;
  font-weight: 500;
  color: #2E7D32;
}
.drawer-close {
  margin-left: auto;
  color: #6b7280;
}
.drawer-filtro-fecha {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 10px;
}
.month-nav {
  color: #2E7D32 !important;
  font-size: 20px !important;
  width: 36px !important;
  height: 36px !important;
  border-radius: 50% !important;
}
.month-nav:hover {
  background: rgba(46, 125, 50, 0.1) !important;
}
.hoy-btn {
  color: #2E7D32 !important;
  font-weight: 600 !important;
  border-radius: 8px !important;
  font-family: 'Nunito', sans-serif !important;
}
.hoy-btn:hover {
  background: rgba(46, 125, 50, 0.1) !important;
}
.month-input {
  width: 180px;
}
.month-input :deep(.q-field__control) {
  border-radius: 8px;
  background: white;
  border: 1.5px solid #2E7D32 !important;
}
.month-input :deep(.q-field__control:focus-within) {
  border-color: #2E7D32 !important;
  box-shadow: 0 0 0 3px rgba(46,125,50,0.15) !important;
}
.month-input :deep(.q-field__native) {
  font-family: 'Nunito', sans-serif;
  font-size: 13px;
  text-align: center;
  font-weight: 600;
  color: #1B5E20;
}
.drawer-body {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 12px;
}
.empty-text {
  font-family: 'Nunito', sans-serif;
  font-size: 15px;
  color: #6b7280;
  font-weight: 500;
}
.employee-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  transition: background 0.2s;
  cursor: pointer;
}
.employee-row:hover {
  background: #f5f5f5;
}
.emp-avatar {
  flex-shrink: 0;
}
.emp-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}
.emp-info {
  flex: 1;
  min-width: 0;
}
.emp-nombre {
  font-family: 'Nunito', sans-serif;
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
}
.emp-cargo {
  font-family: 'Nunito', sans-serif;
  font-size: 12px;
  color: #6b7280;
}
.emp-dias-count {
  font-family: 'Nunito', sans-serif;
  font-size: 11px;
  color: #C10015;
  font-weight: 500;
  margin-top: 2px;
}
.emp-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}
.action-btn {
  color: #6b7280;
}
.action-btn:hover {
  color: #006051;
}
.dias-container {
  padding: 0 20px 12px 72px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.dia-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: #fff5f5;
  border: 1px solid #fecaca;
  border-radius: 12px;
  padding: 3px 10px;
  font-family: 'Nunito', sans-serif;
  font-size: 12px;
  font-weight: 500;
  color: #C10015;
}
.dia-icon {
  flex-shrink: 0;
}
</style>
