<template>
  <q-card flat bordered class="filter-card q-mb-md">
    <q-card-section>
      <div class="text-subtitle2 text-weight-bold filter-title">Filtros</div>
    </q-card-section>
    <q-separator />
    <q-card-section>
      <div class="row q-col-gutter-md">
        <div class="col-12 col-sm-6 col-md-6">
          <q-input outlined dense v-model="filtros.busqueda" label="Usuario o Nombre" placeholder="Buscar por usuario o nombre del empleado" clearable>
            <template v-slot:append>
              <q-icon name="search" />
            </template>
          </q-input>
        </div>
        <div class="col-12 col-sm-6 col-md-4">
          <q-select outlined dense v-model="filtros.rol" :options="rolOptions" label="Rol" clearable map-options emit-value />
        </div>
        <div class="col-12 col-sm-6 col-md-4">
          <q-select outlined dense v-model="filtros.activo" :options="estadoOptions" label="Estado" clearable map-options emit-value />
        </div>
        <div class="col-12 col-sm-6 col-md-4">
          <q-input outlined dense v-model="filtros.fechaDesde" label="Fecha Desde" mask="####/##/##" placeholder="YYYY/MM/DD" clearable>
            <template v-slot:append>
              <q-icon name="event" class="cursor-pointer">
                <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                  <q-date v-model="filtros.fechaDesde" mask="YYYY/MM/DD" />
                </q-popup-proxy>
              </q-icon>
            </template>
          </q-input>
        </div>
        <div class="col-12 col-sm-6 col-md-4">
          <q-input outlined dense v-model="filtros.fechaHasta" label="Fecha Hasta" mask="####/##/##" placeholder="YYYY/MM/DD" clearable>
            <template v-slot:append>
              <q-icon name="event" class="cursor-pointer">
                <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                  <q-date v-model="filtros.fechaHasta" mask="YYYY/MM/DD" />
                </q-popup-proxy>
              </q-icon>
            </template>
          </q-input>
        </div>
      </div>
      <div class="row q-mt-md q-gutter-sm">
        <q-btn unelevated color="primary" icon="search" label="Buscar" @click="buscar" />
        <q-btn flat color="primary" icon="clear" label="Limpiar" @click="limpiarFiltros" />
      </div>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getListRoles } from '../../api/rol/rol'

const emit = defineEmits(['buscar', 'limpiar', 'rolesCargados'])

const filtros = ref({
  busqueda: '',
  rol: null,
  activo: null,
  fechaDesde: '',
  fechaHasta: ''
})

const estadoOptions = [
  { label: 'Activos', value: true },
  { label: 'Inactivos', value: false }
]

const listaRoles = ref([])

const rolOptions = computed(() => {
  return [
    { label: 'Todos', value: null },
    ...listaRoles.value
  ]
})

const convertirFecha = (fecha) => {
  if (!fecha) return null
  return fecha.replace(/\//g, '-')
}

const cargarRoles = async () => {
  try {
    const respuesta = await getListRoles()
    listaRoles.value = respuesta.map(rol => ({
      label: `${rol.nombre.replace('ROLE_', '')} - ${rol.descripcion}`,
      value: rol.nombre
    }))
    emit('rolesCargados', listaRoles.value)
  } catch (error) {
    console.error('Error cargando roles:', error)
  }
}

const buscar = () => {
  const params = {}
  if (filtros.value.busqueda?.trim()) params.busqueda = filtros.value.busqueda.trim()
  if (filtros.value.rol) params.rol = filtros.value.rol
  if (filtros.value.activo !== null && filtros.value.activo !== undefined) params.activo = filtros.value.activo

  const fechaDesde = convertirFecha(filtros.value.fechaDesde)
  const fechaHasta = convertirFecha(filtros.value.fechaHasta)
  if (fechaDesde) params.fechaDesde = fechaDesde
  if (fechaHasta) params.fechaHasta = fechaHasta

  emit('buscar', params)
}

const limpiarFiltros = () => {
  filtros.value.busqueda = ''
  filtros.value.rol = null
  filtros.value.activo = null
  filtros.value.fechaDesde = ''
  filtros.value.fechaHasta = ''
  emit('limpiar')
}

onMounted(() => {
  cargarRoles()
})
</script>
