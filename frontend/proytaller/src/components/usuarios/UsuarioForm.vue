<template>
  <q-card class="usuario-card">
    <div class="accent-bar" />

    <q-card-section class="card-header-section">
      <div class="header-content">
        <div class="header-left">
          <span class="eyebrow">Sistema de acceso</span>
          <div class="text-h6 card-title">Registrar Nuevo Usuario</div>
        </div>
        <q-btn flat round dense icon="close" v-close-popup class="close-btn" />
      </div>
    </q-card-section>

    <q-separator class="custom-separator" />

    <q-card-section class="q-pt-md card-body">
      <q-form @submit.prevent="guardar" class="q-gutter-md">

        <div class="field-wrapper">
          <label class="field-label">Nombre de usuario</label>
          <q-input
            v-model="formulario.username"
            placeholder="ej. juan.perez"
            outlined
            required
            dense
            class="custom-input"
            bg-color="input-bg"
          >
            <template #prepend>
              <q-icon name="person_outline" class="input-icon" />
            </template>
          </q-input>
        </div>

        <div class="field-wrapper">
          <label class="field-label">Contraseña</label>
          <q-input
            v-model="formulario.password"
            placeholder="ingrese su contraseña"
            type="password"
            outlined
            required
            dense
            class="custom-input"
            bg-color="input-bg"
          >
            <template #prepend>
              <q-icon name="lock_outline" class="input-icon" />
            </template>
          </q-input>
        </div>

        <div class="field-wrapper">
          <label class="field-label">ID del Empleado</label>
          <q-input
            v-model.number="formulario.empleadoId"
            placeholder="ej. 1042"
            type="number"
            outlined
            required
            dense
            class="custom-input"
            bg-color="input-bg"
          >
            <template #prepend>
              <q-icon name="badge" class="input-icon" />
            </template>
          </q-input>
        </div>

        <div class="field-wrapper">
          <label class="field-label">Roles asignados</label>
          <q-select
            v-model="formulario.roles"
            :options="opcionesRoles"
            multiple
            emit-value
            map-options
            outlined
            required
            dense
            class="custom-input"
            bg-color="input-bg"
            placeholder="Selecciona uno o más roles"
          >
            <template #prepend>
              <q-icon name="manage_accounts" class="input-icon" />
            </template>
            <template #selected-item="scope">
              <q-chip
                removable
                dense
                @remove="scope.removeAtIndex(scope.index)"
                :label="scope.opt.label"
                class="role-chip"
                color="primary"
                text-color="white"
              />
            </template>
          </q-select>
        </div>

        <q-separator class="custom-separator q-mt-sm" />

        <div class="row justify-end q-mt-sm footer-actions">
          <q-btn
            label="Cancelar"
            flat
            @click="$emit('cerrar')"
            class="btn-cancel q-mr-sm"
          />
          <q-btn
            label="Guardar"
            type="submit"
            :loading="cargando"
            class="btn-save"
            unelevated
          >
            <template #loading>
              <q-spinner-dots color="white" size="1.2em" />
            </template>
          </q-btn>
        </div>

      </q-form>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { ref } from 'vue';
import { useQuasar } from 'quasar';
import { registrarUsuario } from '../../api/usuario/usuario';

const emit = defineEmits(['registrado', 'cerrar']);
const $q = useQuasar();
const cargando = ref(false);

const formulario = ref({
  username: '',
  password: '',
  empleadoId: null,
  roles: []
});

const opcionesRoles = [
  { label: 'Cajero', value: 'ROLE_CAJERO' },
  { label: 'Administrador', value: 'ROLE_ADMIN' }
];

const guardar = async () => {
  try {
    cargando.value = true;
    await registrarUsuario(formulario.value);
    $q.notify({ type: 'positive', message: 'Usuario registrado exitosamente' });
    emit('registrado');
  } catch (error) {
    const mensajeError = error.response?.data?.message || 'Error al registrar el usuario';
    $q.notify({ type: 'negative', message: mensajeError });
  } finally {
    cargando.value = false;
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800&display=swap');

/* ── Card principal ── */
.usuario-card {
  min-width: 420px;
  max-width: 480px;
  border-radius: 20px !important;
  overflow: hidden;
  background: #ffffff !important;
  border: 1px solid #ddecc5 !important;
  box-shadow: 0 12px 48px rgba(74, 140, 37, 0.1), 0 2px 8px rgba(0,0,0,0.06) !important;
  font-family: 'Nunito', sans-serif;
}

/* ── Barra de acento ── */
.accent-bar {
  height: 3px;
  background: linear-gradient(90deg, #4a8c25, #7aaa4e, #d97b1a, #0f9e82);
}

/* ── Header ── */
.card-header-section {
  padding: 1.25rem 1.5rem 1rem;
  background: #f7f9f4 !important;
}

.header-content {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.eyebrow {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: #7aaa4e;
  font-family: 'Nunito', sans-serif;
}

.card-title {
  font-family: 'Nunito', sans-serif !important;
  font-size: 18px !important;
  font-weight: 800 !important;
  color: #2a5c1a !important;
  line-height: 1.25 !important;
}

.close-btn {
  color: #9dbf78 !important;
  background: #f0f7e8 !important;
  border-radius: 8px !important;
  transition: all 0.2s;
}
.close-btn:hover {
  background: #ddecc5 !important;
  color: #4a8c25 !important;
}

/* ── Separador ── */
.custom-separator {
  background: #e4edd8 !important;
}

/* ── Cuerpo ── */
.card-body {
  padding: 1.25rem 1.5rem 1.5rem;
  background: #ffffff !important;
}

/* ── Labels de campo ── */
.field-wrapper {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.field-label {
  font-size: 11px;
  font-weight: 700;
  color: #5a8040;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  font-family: 'Nunito', sans-serif;
}

/* ── Ícono dentro del input ── */
.input-icon {
  color: #9dbf78 !important;
  font-size: 18px;
}

/* ── Overrides Quasar inputs ── */
.custom-input :deep(.q-field__control) {
  background: #fbfdf8 !important;
  border-radius: 10px !important;
  color: #2a5c1a !important;
}

.custom-input :deep(.q-field__native),
.custom-input :deep(.q-field__input) {
  color: #2a5c1a !important;
  font-family: 'Nunito', sans-serif !important;
  font-size: 14px !important;
  font-weight: 500 !important;
}

.custom-input :deep(.q-field__native::placeholder),
.custom-input :deep(input::placeholder) {
  color: #bdd49a !important;
}

.custom-input :deep(.q-field__control:before) {
  border-color: #ddecc5 !important;
  border-width: 1.5px !important;
  border-radius: 10px !important;
}

.custom-input :deep(.q-field__control:hover:before) {
  border-color: #7aaa4e !important;
}

.custom-input :deep(.q-field--focused .q-field__control:before) {
  border-color: #4a8c25 !important;
  border-width: 2px !important;
}

.custom-input :deep(.q-field--focused .q-field__control) {
  box-shadow: 0 0 0 3px rgba(122, 170, 78, 0.15) !important;
}

.custom-input :deep(.q-field__label) {
  display: none;
}

/* ── Chips de roles ── */
.role-chip {
  font-size: 12px !important;
  font-family: 'Nunito', sans-serif !important;
  border-radius: 6px !important;
  background: #eaf4d8 !important;
  color: #3b6d11 !important;
}

/* ── Dropdown del select ── */
.custom-input :deep(.q-menu) {
  background: #ffffff !important;
  border: 1px solid #ddecc5 !important;
  border-radius: 10px !important;
  box-shadow: 0 8px 32px rgba(74, 140, 37, 0.12) !important;
}

.custom-input :deep(.q-item) {
  color: #2a5c1a !important;
  font-family: 'Nunito', sans-serif !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  border-radius: 6px !important;
}

.custom-input :deep(.q-item:hover) {
  background: #f0f7e8 !important;
  color: #2a5c1a !important;
}

.custom-input :deep(.q-item__label) {
  font-family: 'Nunito', sans-serif !important;
}

/* ── Botones ── */
.footer-actions {
  gap: 10px;
  align-items: center;
}

.btn-cancel {
  color: #7aaa4e !important;
  font-family: 'Nunito', sans-serif !important;
  font-size: 13.5px !important;
  font-weight: 600 !important;
  border-radius: 10px !important;
  padding: 0.5rem 1.1rem !important;
  transition: all 0.2s !important;
}
.btn-cancel:hover {
  background: #f0f7e8 !important;
  color: #4a8c25 !important;
}

.btn-save {
  background: #4a8c25 !important;
  color: #ffffff !important;
  font-family: 'Nunito', sans-serif !important;
  font-size: 14px !important;
  font-weight: 700 !important;
  border-radius: 10px !important;
  padding: 0.5rem 1.5rem !important;
  box-shadow: 0 4px 16px rgba(74, 140, 37, 0.3) !important;
  transition: all 0.2s !important;
}
.btn-save:hover {
  background: #3d7a1e !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 6px 24px rgba(74, 140, 37, 0.4) !important;
}
.btn-save:active {
  transform: translateY(0) !important;
}
</style>