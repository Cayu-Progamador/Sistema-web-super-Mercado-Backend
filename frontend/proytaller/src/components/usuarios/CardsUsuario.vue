<template>
    <div class="row q-col-gutter-md q-mb-md">

      <!-- Total Usuarios -->
      <div class="col-12 col-sm-6 col-md-3">
        <q-card class="dashboard-card">
          <q-card-section class="row items-center no-wrap">
            <div class="card-icon icon-teal">
              <q-icon name="groups" color="white" size="28px" />
            </div>
            <div class="q-ml-md">
              <div class="card-label">Total Usuarios</div>
              <div class="card-number">{{ dashboard.totalUsuarios }}</div>
              <div class="card-caption">Usuarios registrados</div>
            </div>
          </q-card-section>
        </q-card>
      </div>

      <!-- Activos -->
      <div class="col-12 col-sm-6 col-md-3">
        <q-card class="dashboard-card">
          <q-card-section class="row items-center no-wrap">
            <div class="card-icon icon-green">
              <q-icon name="person_add" color="white" size="28px" />
            </div>
            <div class="q-ml-md">
              <div class="card-label">Activos</div>
              <div class="card-number">{{ dashboard.usuariosActivos }}</div>
              <div class="card-caption">Usuarios activos</div>
            </div>
          </q-card-section>
        </q-card>
      </div>

      <!-- Inactivos -->
      <div class="col-12 col-sm-6 col-md-3">
        <q-card class="dashboard-card">
          <q-card-section class="row items-center no-wrap">
            <div class="card-icon icon-orange">
              <q-icon name="person_remove" color="white" size="28px" />
            </div>
            <div class="q-ml-md">
              <div class="card-label">Inactivos</div>
              <div class="card-number">{{ dashboard.usuariosInactivos }}</div>
              <div class="card-caption">Usuarios inactivos</div>
            </div>
          </q-card-section>
        </q-card>
      </div>

      <!-- Administradores -->
      <div class="col-12 col-sm-6 col-md-3">
        <q-card class="dashboard-card">
          <q-card-section class="row items-center no-wrap">
            <div class="card-icon icon-darkgreen">
              <q-icon name="shield" color="white" size="28px" />
            </div>
            <div class="q-ml-md">
              <div class="card-label">Administradores</div>
              <div class="card-number">{{ dashboard.administradores }}</div>
              <div class="card-caption">Con rol ADMIN</div>
            </div>
          </q-card-section>
        </q-card>
      </div>

    </div>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import { getDashboardUsuarios } from '../../api/usuario/usuario'

const dashboard = ref({
  totalUsuarios: 0,
  usuariosActivos: 0,
  usuariosInactivos: 0,
  administradores: 0
})

const cargarDashboard = async () => {
  try {
    const  data  = await getDashboardUsuarios()
    dashboard.value = data

  } catch (error) {
    console.error('Error al cargar el dashboard de usuarios:', error)
  }
}


onMounted(() => {
  cargarDashboard()
})


</script>
<style scoped src="../../assets/styles/user/cardUser.css">

</style>