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

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800&display=swap');

/* ── Card base ── */
.dashboard-card {
  border-radius: 16px !important;
  background: #ffffff !important;
  border: 1px solid #e4edd8 !important;
  box-shadow: 0 2px 16px rgba(74, 140, 37, 0.07) !important;
  transition: box-shadow 0.2s, transform 0.2s;
}
.dashboard-card:hover {
  box-shadow: 0 6px 24px rgba(74, 140, 37, 0.14) !important;
  transform: translateY(-2px);
}

/* ── Ícono contenedor ── */
.card-icon {
  width: 55px;
  height: 55px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

/* Verde oscuro — Total Usuarios */
.icon-teal {
  background: #0f6e56;
}

/* Verde medio — Activos */
.icon-green {
  background: #4a8c25;
}

/* Naranja — Inactivos */
.icon-orange {
  background: #d97b1a;
}

/* Verde muy oscuro — Admins */
.icon-darkgreen {
  background: #2a5c1a;
}

/* ── Tipografía ── */
.card-label {
  font-family: 'Nunito', sans-serif;
  font-size: 13px;
  font-weight: 600;
  color: #7aaa4e;
  line-height: 1.3;
}

.card-number {
  font-family: 'Nunito', sans-serif;
  font-size: 26px;
  font-weight: 800;
  color: #2a5c1a;
  line-height: 1.2;
}

.card-caption {
  font-family: 'Nunito', sans-serif;
  font-size: 11.5px;
  font-weight: 500;
  color: #9dbf78;
  line-height: 1.3;
}
</style>