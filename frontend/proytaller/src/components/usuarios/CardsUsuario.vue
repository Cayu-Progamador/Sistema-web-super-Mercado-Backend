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
              <div class="card-number">24</div>
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
              <div class="card-number">21</div>
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
              <div class="card-number">3</div>
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
              <div class="card-number">5</div>
              <div class="card-caption">Con rol ADMIN</div>
            </div>
          </q-card-section>
        </q-card>
      </div>

    </div>
</template>

<style scoped src="../../assets/styles/user/cardUser.css">

</style>