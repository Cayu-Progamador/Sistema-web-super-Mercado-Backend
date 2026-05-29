<template>
  <q-page class="perfil-page q-pa-md">

   

    <!-- GRID PRINCIPAL -->
    <div class="perfil-grid">

      <!-- COLUMNA IZQUIERDA -->
      <div class="col-left">
        <q-card class="perfil-card" flat>

          <!-- Título sección -->
          <div class="section-header q-mb-lg">
            <div class="section-icon">
              <q-icon name="lock" size="28px" style="color:#4a8c25" />
            </div>
            <div>
              <div class="section-title">Cambiar Contraseña</div>
              <div class="section-sub">Actualiza tu contraseña para mantener tu cuenta segura.</div>
            </div>
          </div>

          <q-form @submit.prevent="actualizarPassword" class="q-gutter-sm">

            <!-- Contraseña Actual -->
            <div class="field-group">
              <label class="field-lbl">Contraseña Actual</label>
              <q-input
                v-model="form.actual"
                outlined dense
                :type="mostrar.actual ? 'text' : 'password'"
                placeholder="Ingresa tu contraseña actual"
                class="field-input"
              >
                <template #prepend>
                  <q-icon name="lock_outline" class="input-icon" />
                </template>
                <template #append>
                  <q-icon
                    :name="mostrar.actual ? 'visibility_off' : 'visibility'"
                    class="eye-icon"
                    @click="mostrar.actual = !mostrar.actual"
                  />
                </template>
              </q-input>
              <span class="field-hint">Ingresa tu contraseña actual para verificar tu identidad.</span>
            </div>

            <!-- Nueva Contraseña -->
            <div class="field-group">
              <label class="field-lbl">Nueva Contraseña</label>
              <q-input
                v-model="form.nueva"
                outlined dense
                :type="mostrar.nueva ? 'text' : 'password'"
                placeholder="Ingresa tu nueva contraseña"
                class="field-input"
                @update:model-value="calcularFortaleza"
              >
                <template #prepend>
                  <q-icon name="lock_outline" class="input-icon" />
                </template>
                <template #append>
                  <q-icon
                    :name="mostrar.nueva ? 'visibility_off' : 'visibility'"
                    class="eye-icon"
                    @click="mostrar.nueva = !mostrar.nueva"
                  />
                </template>
              </q-input>
              <span class="field-hint">Debe contener al menos 8 caracteres, incluyendo mayúsculas, minúsculas, números y símbolos.</span>

              <!-- Fortaleza -->
              <div class="fortaleza-wrap q-mt-sm">
                <div class="fortaleza-label">Fortaleza de la contraseña:</div>
                <div class="fortaleza-bars">
                  <div
                    v-for="i in 4" :key="i"
                    class="fortaleza-bar"
                    :class="getBarClass(i)"
                  ></div>
                </div>
                <div class="fortaleza-text" :class="fortalezaColor">{{ fortalezaTexto }}</div>
              </div>
            </div>

            <!-- Confirmar Nueva Contraseña -->
            <div class="field-group">
              <label class="field-lbl">Confirmar Nueva Contraseña</label>
              <q-input
                v-model="form.confirmar"
                outlined dense
                :type="mostrar.confirmar ? 'text' : 'password'"
                placeholder="Repite tu nueva contraseña"
                class="field-input"
                :class="{ 'input-error': form.confirmar && form.nueva !== form.confirmar }"
              >
                <template #prepend>
                  <q-icon name="lock_outline" class="input-icon" />
                </template>
                <template #append>
                  <q-icon
                    :name="mostrar.confirmar ? 'visibility_off' : 'visibility'"
                    class="eye-icon"
                    @click="mostrar.confirmar = !mostrar.confirmar"
                  />
                </template>
              </q-input>
              <span
                v-if="form.confirmar && form.nueva !== form.confirmar"
                class="field-error"
              >
                Las contraseñas no coinciden.
              </span>
              <span v-else class="field-hint">Vuelve a ingresar la nueva contraseña.</span>
            </div>

            <!-- Consejo -->
            <div class="consejo-box q-mt-sm">
              <q-icon name="info" size="18px" style="color:#4a8c25;flex-shrink:0" />
              <span>
                <strong style="color:#2a5c1a">Consejo:</strong>
                <span style="color:#5a8040"> Usa una contraseña única que no hayas utilizado antes.</span>
              </span>
            </div>

            <!-- Botones -->
            <div class="row justify-end q-gutter-sm q-mt-md">
              <q-btn
                label="Cancelar"
                flat
                class="btn-cancel"
                @click="resetForm"
              />
              <q-btn
                type="submit"
                label="Actualizar Contraseña"
                icon="lock"
                class="btn-save"
                unelevated
                :loading="cargando"
              >
                <template #loading>
                  <q-spinner-dots color="white" size="1em" />
                </template>
              </q-btn>
            </div>

          </q-form>
        </q-card>
      </div>

      <!-- COLUMNA DERECHA -->
      <div class="col-right">

        <!-- Requisitos -->
        <q-card class="perfil-card q-mb-md" flat>
          <div class="card-title-row">
            <q-icon name="checklist" class="card-title-icon" />
            Requisitos de Contraseña
          </div>

          <div class="req-list">
            <div
              v-for="req in requisitos"
              :key="req.texto"
              class="req-item"
              :class="{ 'req-ok': req.cumplido }"
            >
              <div class="req-dot" :class="req.cumplido ? 'req-dot-ok' : 'req-dot-pending'">
                <q-icon
                  :name="req.cumplido ? 'check' : 'remove'"
                  size="12px"
                  color="white"
                />
              </div>
              <span class="req-texto">{{ req.texto }}</span>
            </div>
          </div>
        </q-card>

        <!-- Tips de seguridad -->
        <q-card class="perfil-card" flat>
          <div class="card-title-row">
            <q-icon name="security" class="card-title-icon" />
            Tips de Seguridad
          </div>

          <div class="tips-list">
            <div class="tip-item">
              <div class="tip-icon ri-green">
                <q-icon name="shield" size="18px" style="color:#4a8c25" />
              </div>
              <div>
                <div class="tip-title">No compartas tu contraseña</div>
                <div class="tip-sub">Nunca compartas tu contraseña con otras personas.</div>
              </div>
            </div>
            <div class="tip-item">
              <div class="tip-icon ri-orange">
                <q-icon name="vpn_key" size="18px" style="color:#d97b1a" />
              </div>
              <div>
                <div class="tip-title">Usa contraseñas únicas</div>
                <div class="tip-sub">No uses la misma contraseña en diferentes sitios.</div>
              </div>
            </div>
            <div class="tip-item">
              <div class="tip-icon ri-teal">
                <q-icon name="refresh" size="18px" style="color:#0f6e56" />
              </div>
              <div>
                <div class="tip-title">Cámbiala regularmente</div>
                <div class="tip-sub">Se recomienda cambiar tu contraseña periódicamente.</div>
              </div>
            </div>
            <div class="tip-item" style="border-bottom:none">
              <div class="tip-icon ri-blue">
                <q-icon name="lock" size="18px" style="color:#185fa5" />
              </div>
              <div>
                <div class="tip-title">Cierra sesión</div>
                <div class="tip-sub">Cierra sesión en dispositivos compartidos o públicos.</div>
              </div>
            </div>
          </div>
        </q-card>

      </div>
    </div>

    <!-- Footer -->
    <div class="page-footer">© 2024 Mercat - Todos los derechos reservados</div>

  </q-page>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useQuasar } from 'quasar'

const $q = useQuasar()
const cargando = ref(false)

const form = reactive({ actual: '', nueva: '', confirmar: '' })
const mostrar = reactive({ actual: false, nueva: false, confirmar: false })
const fortaleza = ref(0)

const calcularFortaleza = (val) => {
  let score = 0
  if (val.length >= 8)            score++
  if (/[A-Z]/.test(val))          score++
  if (/[a-z]/.test(val))          score++
  if (/[0-9]/.test(val))          score++
  if (/[!@#$%^&*]/.test(val))     score++
  fortaleza.value = score
}

const fortalezaTexto = computed(() => {
  if (fortaleza.value <= 1) return 'Muy débil'
  if (fortaleza.value === 2) return 'Débil'
  if (fortaleza.value === 3) return 'Regular'
  if (fortaleza.value === 4) return 'Fuerte'
  return 'Muy fuerte'
})

const fortalezaColor = computed(() => {
  if (fortaleza.value <= 1) return 'ft-rojo'
  if (fortaleza.value === 2) return 'ft-naranja'
  if (fortaleza.value === 3) return 'ft-amarillo'
  return 'ft-verde'
})

const getBarClass = (i) => {
  if (fortaleza.value === 0) return ''
  if (fortaleza.value <= 1) return i <= 1 ? 'bar-rojo' : ''
  if (fortaleza.value === 2) return i <= 2 ? 'bar-naranja' : ''
  if (fortaleza.value === 3) return i <= 3 ? 'bar-amarillo' : ''
  return 'bar-verde'
}

const requisitos = computed(() => [
  { texto: 'Mínimo 8 caracteres',             cumplido: form.nueva.length >= 8 },
  { texto: 'Al menos una mayúscula (A-Z)',     cumplido: /[A-Z]/.test(form.nueva) },
  { texto: 'Al menos una minúscula (a-z)',     cumplido: /[a-z]/.test(form.nueva) },
  { texto: 'Al menos un número (0-9)',         cumplido: /[0-9]/.test(form.nueva) },
  { texto: 'Al menos un carácter especial (!@#$%)', cumplido: /[!@#$%^&*]/.test(form.nueva) }
])

const resetForm = () => {
  form.actual = ''; form.nueva = ''; form.confirmar = ''
  fortaleza.value = 0
}

const actualizarPassword = async () => {
  if (form.nueva !== form.confirmar) {
    $q.notify({ type: 'negative', message: 'Las contraseñas no coinciden' })
    return
  }
  cargando.value = true
  await new Promise(r => setTimeout(r, 1200))
  cargando.value = false
  $q.notify({ type: 'positive', message: 'Contraseña actualizada correctamente' })
  resetForm()
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap');

.perfil-page {
  background: #f7f9f4 !important;
  font-family: 'Nunito', sans-serif;
}

/* ── Tabs ── */
.perfil-tabs {
  display: flex;
  border-bottom: 2px solid #e4edd8;
}
.perfil-tab {
  padding: 10px 20px;
  font-size: 13px; font-weight: 700; color: #9dbf78;
  cursor: pointer;
  border-bottom: 2.5px solid transparent;
  margin-bottom: -2px;
  transition: all 0.2s;
  font-family: 'Nunito', sans-serif;
}
.perfil-tab.active { color: #2a5c1a; border-bottom-color: #4a8c25; }
.perfil-tab:hover:not(.active) { color: #5a8040; }

/* ── Grid ── */
.perfil-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 290px;
  gap: 16px;
  align-items: start;
}

/* ── Cards ── */
.perfil-card {
  background: #ffffff !important;
  border: 1px solid #e4edd8 !important;
  border-radius: 16px !important;
  padding: 24px !important;
}

/* ── Section header ── */
.section-header {
  display: flex; align-items: center; gap: 14px;
}
.section-icon {
  width: 56px; height: 56px;
  border-radius: 14px;
  background: #eaf4d8;
  border: 1.5px solid #c8e0a0;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.section-title {
  font-family: 'Nunito', sans-serif;
  font-size: 18px; font-weight: 900; color: #2a5c1a;
  text-transform: uppercase; letter-spacing: 0.04em;
}
.section-sub {
  font-family: 'Nunito', sans-serif;
  font-size: 13px; font-weight: 500; color: #9dbf78; margin-top: 3px;
}

/* ── Fields ── */
.field-group {
  display: flex; flex-direction: column; gap: 5px;
  margin-bottom: 4px;
}
.field-lbl {
  font-size: 12px; font-weight: 700; color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
}
.field-hint {
  font-size: 11.5px; font-weight: 500; color: #bdd49a;
  font-family: 'Nunito', sans-serif;
}
.field-error {
  font-size: 11.5px; font-weight: 700; color: #b91c1c;
  font-family: 'Nunito', sans-serif;
}

/* ── Quasar input overrides ── */
.field-input :deep(.q-field__control) {
  background: #fbfdf8 !important;
  border: 1.5px solid #ddecc5 !important;
  border-radius: 10px !important;
  box-shadow: none !important;
}
.field-input :deep(.q-field__control::before),
.field-input :deep(.q-field__control::after) { display: none !important; }
.field-input :deep(.q-field--focused .q-field__control) {
  border-color: #4a8c25 !important;
  box-shadow: 0 0 0 3px rgba(74,140,37,0.1) !important;
  background: #ffffff !important;
}
.field-input :deep(.q-field__native) {
  color: #2a5c1a !important;
  font-family: 'Nunito', sans-serif !important;
  font-size: 14px !important; font-weight: 600 !important;
}
.field-input :deep(input::placeholder) { color: #c8e0a0 !important; font-weight: 500 !important; }
.field-input :deep(.q-field__label) { display: none !important; }
.field-input :deep(.q-focus-helper) { display: none !important; }

.input-icon { color: #bdd49a !important; font-size: 18px !important; }
.eye-icon {
  color: #bdd49a !important; font-size: 18px !important;
  cursor: pointer; transition: color 0.2s;
}
.eye-icon:hover { color: #4a8c25 !important; }

.input-error :deep(.q-field__control) {
  border-color: #fca5a5 !important;
}
.input-error :deep(.q-field--focused .q-field__control) {
  border-color: #b91c1c !important;
  box-shadow: 0 0 0 3px rgba(185,28,28,0.08) !important;
}

/* ── Fortaleza ── */
.fortaleza-wrap {
  display: flex; align-items: center; gap: 10px; flex-wrap: wrap;
}
.fortaleza-label {
  font-size: 12px; font-weight: 700; color: #5a8040;
  font-family: 'Nunito', sans-serif; white-space: nowrap;
}
.fortaleza-bars {
  display: flex; gap: 5px; flex: 1;
}
.fortaleza-bar {
  flex: 1; height: 5px; border-radius: 4px;
  background: #e4edd8; transition: background 0.3s;
}
.bar-rojo    { background: #ef4444; }
.bar-naranja { background: #d97b1a; }
.bar-amarillo{ background: #eab308; }
.bar-verde   { background: #4a8c25; }

.fortaleza-text {
  font-size: 12px; font-weight: 800;
  font-family: 'Nunito', sans-serif; white-space: nowrap;
}
.ft-rojo    { color: #ef4444; }
.ft-naranja { color: #d97b1a; }
.ft-amarillo{ color: #eab308; }
.ft-verde   { color: #4a8c25; }

/* ── Consejo ── */
.consejo-box {
  display: flex; align-items: flex-start; gap: 10px;
  background: #f0f7e8;
  border: 1px solid #c8e0a0;
  border-radius: 10px;
  padding: 12px 14px;
  font-size: 13px;
  font-family: 'Nunito', sans-serif;
}

/* ── Botones ── */
.btn-cancel {
  color: #7aaa4e !important;
  font-family: 'Nunito', sans-serif !important;
  font-size: 14px !important; font-weight: 700 !important;
  border-radius: 10px !important;
  padding: 8px 20px !important;
  transition: all 0.2s !important;
}
.btn-cancel:hover { background: #f0f7e8 !important; }

.btn-save {
  background: #4a8c25 !important;
  color: #ffffff !important;
  font-family: 'Nunito', sans-serif !important;
  font-size: 14px !important; font-weight: 800 !important;
  border-radius: 10px !important;
  padding: 8px 22px !important;
  box-shadow: 0 4px 14px rgba(74,140,37,0.28) !important;
  transition: all 0.2s !important;
}
.btn-save:hover {
  background: #3d7a1e !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 6px 20px rgba(74,140,37,0.38) !important;
}

/* ── Card title ── */
.card-title-row {
  display: flex; align-items: center; gap: 8px;
  font-size: 12px; font-weight: 800; color: #2a5c1a;
  text-transform: uppercase; letter-spacing: 0.1em;
  margin-bottom: 16px;
  font-family: 'Nunito', sans-serif;
}
.card-title-icon { color: #7aaa4e !important; font-size: 18px !important; }

/* ── Requisitos ── */
.req-list { display: flex; flex-direction: column; gap: 10px; }
.req-item {
  display: flex; align-items: center; gap: 10px;
  transition: all 0.2s;
}
.req-dot {
  width: 20px; height: 20px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0; transition: all 0.2s;
}
.req-dot-pending { background: #ddecc5; }
.req-dot-ok      { background: #4a8c25; }
.req-texto {
  font-size: 13px; font-weight: 600;
  color: #5a8040; font-family: 'Nunito', sans-serif;
  transition: color 0.2s;
}
.req-ok .req-texto { color: #2a5c1a; font-weight: 700; }

/* ── Tips ── */
.tips-list { display: flex; flex-direction: column; }
.tip-item {
  display: flex; align-items: flex-start; gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f5ea;
}
.tip-icon {
  width: 36px; height: 36px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.ri-green  { background: #eaf4d8; }
.ri-orange { background: #fef3e2; }
.ri-teal   { background: #e1f5ee; }
.ri-blue   { background: #e6f1fb; }

.tip-title {
  font-size: 13px; font-weight: 800; color: #2a5c1a;
  font-family: 'Nunito', sans-serif;
}
.tip-sub {
  font-size: 12px; font-weight: 500; color: #9dbf78;
  font-family: 'Nunito', sans-serif; margin-top: 2px;
}

/* ── Footer ── */
.page-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 12px; font-weight: 600; color: #bdd49a;
  font-family: 'Nunito', sans-serif;
}
</style>