<template>
  <div>
    <div class="row items-center q-gutter-sm q-mb-md">
      <q-input v-model="filtros.busqueda" outlined dense placeholder="Buscar por Usuario o Nombre..." class="search-input" debounce="300" @update:model-value="buscar">
        <template v-slot:prepend>
          <q-icon name="search" class="search-icon" />
        </template>
      </q-input>
      <q-btn flat :icon="filtrosVisibles ? 'expand_less' : 'filter_list'" label="Filtros" class="filter-toggle-btn" @click="filtrosVisibles = !filtrosVisibles" no-caps>
        <q-badge v-if="filtrosActivos" color="primary" floating>!</q-badge>
      </q-btn>
    </div>

    <q-slide-transition>
      <div v-show="filtrosVisibles">
        <q-card flat bordered class="filters-card q-mb-md q-pa-md">
          <div class="row q-col-gutter-md">
            <div class="col-12 col-md-4">
              <q-select v-model="filtros.rol" :options="rolOptions" label="Rol" outlined dense clearable map-options emit-value class="filter-field" />
            </div>
            <div class="col-12 col-md-4">
              <q-select v-model="filtros.activo" :options="estadoOptions" label="Estado" outlined dense clearable map-options emit-value class="filter-field" />
            </div>
            <div class="col-12 col-md-4">
              <div class="row q-col-gutter-sm">
                <div class="col-6">
                  <q-input v-model="filtros.fechaDesde" label="Fecha Desde" outlined dense type="date" class="filter-field" clearable />
                </div>
                <div class="col-6">
                  <q-input v-model="filtros.fechaHasta" label="Fecha Hasta" outlined dense type="date" class="filter-field" clearable />
                </div>
              </div>
            </div>
            <div class="col-12 row justify-end q-gutter-sm q-mt-sm">
              <q-btn flat color="grey-7" icon="clear" label="Limpiar" @click="limpiarFiltros" no-caps />
              <q-btn unelevated color="primary" icon="search" label="Buscar" @click="buscar" no-caps />
            </div>
          </div>
        </q-card>
      </div>
    </q-slide-transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getListRoles } from '../../api/rol/rol'

const emit = defineEmits(['buscar', 'limpiar', 'rolesCargados'])

const filtrosVisibles = ref(false)
const filtrosActivos = ref(false)

const filtros = ref({
  busqueda: '',
  rol: null,
  activo: null,
  fechaDesde: null,
  fechaHasta: null
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

  filtrosActivos.value = Object.values(filtros.value).some(v => v !== null && v !== undefined && v !== '')
  emit('buscar', params)
}

const limpiarFiltros = () => {
  filtros.value.busqueda = ''
  filtros.value.rol = null
  filtros.value.activo = null
  filtros.value.fechaDesde = null
  filtros.value.fechaHasta = null
  filtrosActivos.value = false
  emit('limpiar')
}

onMounted(() => {
  cargarRoles()
})
</script>

<style scoped>
.search-input {
  width: 320px;
}
.search-input :deep(.q-field__control) {
  border-radius: 10px;
  background: white;
}
.search-icon {
  color: #2a5c1a;
}
.search-icon:hover {
  color: #2a5c1a;
}
.filter-toggle-btn {
  color: #2a5c1a;
}
.filter-toggle-btn:hover {
  color: #2a5c1a;
}
.filters-card {
  border-radius: 12px;
  background: white;
  border: 1px solid #e5e7eb;
}
.filter-field :deep(.q-field__control) {
  border-radius: 8px;
  background: #f9fafb;
}
@media (max-width: 600px) {
  .search-input {
    width: 100%;
  }
}
</style>
