<<template>
  <q-dialog v-model="abierto" persistent>
    <q-card class="dialog-card">

      <div class="accent-bar"></div>

      <div class="dialog-header">
        <div class="header-left">
          <div class="edit-icon">
            <q-icon name="edit" size="22px" style="color:#4a8c25" />
          </div>
          <div>
            <div class="dialog-title">Editar Usuario</div>
            <div class="dialog-sub">Modifica las credenciales y permisos</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" class="close-btn" @click="cerrar" />
      </div>

      <q-card-section class="dialog-body">

        <div class="user-card">
          <div class="user-avatar">{{ iniciales }}</div>
          <div>
            <div class="user-name">{{ usuarioActual?.username || '—' }}</div>
            <div class="user-meta">
              <span v-for="rol in rolesActuales" :key="rol" class="user-role">
                {{ formatRol(rol) }}
              </span>
            </div>
          </div>
        </div>

        <div class="form-grid">

          <div class="form-column">

            <div class="form-group">
              <label class="form-label">
                <q-icon name="person_outline" size="14px" class="label-icon" />
                Username
              </label>
              <q-input v-model="form.username" outlined dense placeholder="Ej: juan.perez" class="form-input"
                :error="tocado.username && !!errores.username" :error-message="errores.username"
                @blur="tocado.username = true" />
            </div>

            <div class="form-group">
              <label class="form-label">
                <q-icon name="lock" size="14px" class="label-icon" />
                Nueva Contraseña
                <span class="label-hint">(opcional)</span>
              </label>
              <q-input v-model="form.password" outlined dense :type="mostrarPassword ? 'text' : 'password'"
                placeholder="Mínimo 8 caracteres" class="form-input"
                :error="tocado.password && !!errores.password" :error-message="errores.password"
                @blur="tocado.password = true">
                <template v-slot:append>
                  <q-icon :name="mostrarPassword ? 'visibility_off' : 'visibility'" class="cursor-pointer"
                    style="color:#82bd43" @click="mostrarPassword = !mostrarPassword" />
                </template>
              </q-input>

              <div v-if="form.password" class="q-mt-sm">
                <div class="fortaleza-wrap">
                  <div class="fortaleza-label">Fortaleza de la contraseña:</div>
                  <div class="fortaleza-bars">
                    <div v-for="i in 4" :key="i" class="fortaleza-bar" :class="getBarClass(i)"></div>
                  </div>
                  <div class="fortaleza-text" :class="fortalezaClase">{{ fuerzaLabel }}</div>
                </div>

                <div class="req-list q-mt-sm">
                  <div v-for="req in requisitos" :key="req.label" class="req-item" :class="{ 'req-ok': req.cumple }">
                    <div class="req-dot" :class="req.cumple ? 'req-dot-ok' : 'req-dot-pending'">
                      <q-icon :name="req.cumple ? 'check' : 'remove'" size="12px" color="white" />
                    </div>
                    <span class="req-texto">{{ req.label }}</span>
                  </div>
                </div>
              </div>
            </div>

          </div>

          <div class="form-column">

            <div class="form-group roles-group">
              <label class="form-label">
                <q-icon name="shield" size="14px" class="label-icon" />
                Roles del Sistema
              </label>
              <q-select v-model="form.roles" outlined dense multiple :options="props.rolesDisponibles"
                option-label="label" option-value="value" emit-value map-options placeholder="Selecciona roles"
                class="form-input" :error="tocado.roles && !!errores.roles" :error-message="errores.roles"
                @blur="tocado.roles = true">
                <template v-slot:option="scope">
                  <q-item v-bind="scope.itemProps">

                    <q-item-section>
                      <q-chip dense :color="getRolColor(scope.opt.value)" text-color="white" size="sm">
                        {{ formatRol(scope.opt.value) }}
                      </q-chip>

                      <q-item-label caption class="q-mt-xs">
                        {{ scope.opt.label }}
                      </q-item-label>
                    </q-item-section>

                    <q-item-section side>
                      <q-icon v-if="form.roles.includes(scope.opt.value)" name="check" color="green" size="18px" />
                    </q-item-section>

                  </q-item>
                </template>

                <template v-slot:selected-item="scope">
                  <q-chip dense removable @remove="scope.removeAtIndex(scope.index)"
                    :color="getRolColor(scope.opt.value)" text-color="white" size="sm" class="q-ma-xs">
                    {{ scope.opt.label }}
                  </q-chip>
                </template>
              </q-select>
            </div>

            <div class="info-box-compact">
              <q-icon name="info" size="16px" style="color:#4a8c25; flex-shrink:0;" />
              <p>Si cambias los <strong>roles</strong>, el usuario deberá volver a iniciar sesión.</p>
            </div>

          </div>

        </div>

      </q-card-section>

      <q-card-actions class="dialog-footer">
        <q-btn class="btn-cancel" flat no-caps @click="cerrar">
          <q-icon name="close" size="15px" class="q-mr-xs" />
          Cancelar
        </q-btn>

        <q-btn class="btn-guardar" unelevated no-caps :disable="!formularioValido || !hayCambios || cargando" :loading="cargando"
          @click="guardar">
          <template v-slot:loading>
            <q-spinner-dots color="white" size="1em" class="q-mr-sm" />
            Guardando...
          </template>
          <template v-slot:default>
            <q-icon name="save" size="16px" class="q-mr-sm" />
            Guardar Cambios
          </template>
        </q-btn>
      </q-card-actions>

    </q-card>
  </q-dialog>
</template>

  <script setup>
  import { ref, computed, watch, nextTick } from 'vue'
  import { useQuasar } from 'quasar'
  import { actualizarUsuario } from '../../api/usuario/usuario'

  const props = defineProps({
    modelValue: { type: Boolean, default: false },
    id: { type: [Number, String], default: null },
    username: { type: String, default: '' },
    roles: { type: Array, default: () => [] },
    rolesDisponibles: { type: Array, default: () => [] }
  })

  const emit = defineEmits(['update:modelValue', 'actualizado'])
  const $q = useQuasar()

  const abierto = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
  })

  const cargando = ref(false)
  const mostrarPassword = ref(false)

  const form = ref({
    username: '',
    password: '',
    roles: []
  })

  const tocado = ref({
    username: false,
    password: false,
    roles: false
  })

  const errores = computed(() => ({
    username: validarUsername(form.value.username),
    password: validarPassword(form.value.password),
    roles: form.value.roles.length > 0 ? '' : 'Debe asignar al menos un rol.'
  }))

  const formularioValido = computed(() =>
    !errores.value.username &&
    !errores.value.password &&
    !errores.value.roles
  )

  const usuarioActual = computed(() => ({
    username: props.username || '—'
  }))

  const iniciales = computed(() => {
    if (!props.username) return 'U'
    return props.username.substring(0, 2).toUpperCase()
  })

  const rolesActuales = computed(() => props.roles || [])

  const hayCambios = computed(() => {
    const usernameCambio = form.value.username.trim() !== (props.username || '').trim()
    const passwordCambio = Boolean(form.value.password.trim())
    const rolesActualesOrdenados = [...(props.roles || [])].sort().join(',')
    const rolesNuevosOrdenados = [...form.value.roles].sort().join(',')
    return usernameCambio || passwordCambio || rolesActualesOrdenados !== rolesNuevosOrdenados
  })

  function validarUsername(username) {
    const valor = username?.trim() || ''
    if (!valor) return 'El username es obligatorio.'
    if (valor.length < 3 || valor.length > 50) return 'Debe tener entre 3 y 50 caracteres.'
    if (!/^[A-Za-z0-9._-]+$/.test(valor)) {
      return 'Use solo letras, números, punto, guion o guion bajo.'
    }
    return ''
  }

  function validarPassword(password) {
    if (!password) return ''
    if (password.length < 8) return 'Debe tener al menos 8 caracteres.'
    if (!/[A-Z]/.test(password) || !/[a-z]/.test(password) ||
        !/[0-9]/.test(password) || !/[^A-Za-z0-9]/.test(password)) {
      return 'Incluya mayúscula, minúscula, número y carácter especial.'
    }
    return ''
  }

  const requisitos = computed(() => [
    { label: 'Mínimo 8 caracteres', cumple: form.value.password.length >= 8 },
    { label: 'Mayúscula', cumple: /[A-Z]/.test(form.value.password) },
    { label: 'Minúscula', cumple: /[a-z]/.test(form.value.password) },
    { label: 'Número', cumple: /[0-9]/.test(form.value.password) },
    { label: 'Carácter especial (!@#$%)', cumple: /[^A-Za-z0-9]/.test(form.value.password) }
  ])

  const fuerzaValor = computed(() =>
    requisitos.value.filter(req => req.cumple).length / requisitos.value.length
  )

  const fuerzaLabel = computed(() => {
    if (fuerzaValor.value <= 0.2) return 'Muy débil'
    if (fuerzaValor.value <= 0.4) return 'Débil'
    if (fuerzaValor.value <= 0.6) return 'Regular'
    return 'Fuerte'
  })

  const fortalezaClase = computed(() => {
    if (fuerzaValor.value <= 0.2) return 'ft-rojo'
    if (fuerzaValor.value <= 0.4) return 'ft-naranja'
    if (fuerzaValor.value <= 0.6) return 'ft-amarillo'
    return 'ft-verde'
  })

  const getBarClass = (i) => {
    if (fuerzaValor.value === 0) return ''
    if (fuerzaValor.value <= 0.2) return i <= 1 ? 'bar-rojo' : ''
    if (fuerzaValor.value <= 0.4) return i <= 2 ? 'bar-naranja' : ''
    if (fuerzaValor.value <= 0.6) return i <= 3 ? 'bar-amarillo' : ''
    return 'bar-verde'
  }

  const formatRol = (rol) => {
    return rol
      ?.replace('ROLE_', '')
      .charAt(0)
      .toUpperCase() +
      rol
        ?.replace('ROLE_', '')
        .slice(1)
        .toLowerCase()
  }

  const getRolColor = (rol) => {
    if (!rol) return 'green-4'
    const colores = ['green-3', 'green-7', 'deep-purple-6', 'amber-8', 'teal-7', 'pink-6', 'orange-8', 'cyan-7']
    const rolStr = typeof rol === 'string' ? rol : rol.value || ''
    const indice = rolStr.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0) % colores.length
    return colores[indice]
  }

  // Cargar datos cuando se abre el diálogo
  watch(() => props.modelValue, async (val) => {
    if (val) {
      await nextTick()
      cargarDatos()
    }
  })

  const cargarDatos = () => {
    form.value = {
      username: props.username || '',
      password: '',
      roles: [...(props.roles || [])]
    }
    mostrarPassword.value = false
    tocado.value = { username: false, password: false, roles: false }
  }

  const cerrar = () => {
    abierto.value = false
  }

  const guardar = async () => {
    tocado.value = { username: true, password: true, roles: true }
    if (!formularioValido.value || !hayCambios.value) return

    cargando.value = true

    try {
      const payload = {
        username: form.value.username.trim(),
        roles: form.value.roles
      }

      if (form.value.password.trim()) payload.password = form.value.password

      await actualizarUsuario(props.id, payload)

      $q.notify({ type: 'positive', message: 'Usuario actualizado correctamente' })
      emit('actualizado')
      cerrar()
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: error.response?.data?.message || 'Error al actualizar usuario'
      })
    } finally {
      cargando.value = false
    }
  }
</script>

  <style scoped>
  @import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap');

  .dialog-card {
    width: 100% !important;
    max-width: 700px !important;
    max-height: 90vh !important;
    border-radius: 20px !important;
    overflow: hidden !important;
    background: #ffffff !important;
    border: 1px solid #e4edd8 !important;
    box-shadow: 0 20px 60px rgba(42, 92, 26, 0.15) !important;
    font-family: 'Nunito', sans-serif;
  }

  .accent-bar {
    height: 3px;
    background: linear-gradient(90deg, #82bd43, #4a8c25, #64992b);
  }

  .dialog-header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    background: #f0f7e8;
    border-bottom: 1px solid #c8e0a0;
    padding: 16px 20px 14px;
  }

  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .edit-icon {
    width: 44px;
    height: 44px;
    border-radius: 12px;
    background: #eaf4d8;
    border: 1.5px solid #82bd43;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .dialog-title {
    font-size: 15px;
    font-weight: 900;
    color: #2a5c1a;
    font-family: 'Nunito', sans-serif;
    margin-bottom: 2px;
  }

  .dialog-sub {
    font-size: 11px;
    font-weight: 600;
    color: #7aaa4e;
    font-family: 'Nunito', sans-serif;
  }

  .close-btn {
    color: #7aaa4e !important;
    background: #f0f7e8 !important;
    border-radius: 8px !important;
  }

  .close-btn:hover {
    background: #ddecc5 !important;
    color: #4a8c25 !important;
  }

  .dialog-body {
    padding: 18px 20px 10px !important;
    overflow-y: auto;
    max-height: calc(90vh - 150px);
  }

  .user-card {
    display: flex;
    align-items: center;
    gap: 12px;
    background: #f7f9f4;
    border: 1px solid #e4edd8;
    border-radius: 12px;
    padding: 12px 14px;
    margin-bottom: 18px;
  }

  .user-avatar {
    width: 42px;
    height: 42px;
    border-radius: 50%;
    background: #eaf4d8;
    border: 2px solid #c8e0a0;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 15px;
    font-weight: 900;
    color: #2a5c1a;
    flex-shrink: 0;
    font-family: 'Nunito', sans-serif;
  }

  .user-name {
    font-size: 14px;
    font-weight: 800;
    color: #2a5c1a;
    margin-bottom: 3px;
    font-family: 'Nunito', sans-serif;
  }

  .user-meta {
    display: flex;
    align-items: center;
    gap: 6px;
    flex-wrap: wrap;
  }

  .user-role {
    font-size: 10px;
    font-weight: 700;
    color: #7aaa4e;
    background: #eaf4d8;
    border: 1px solid #c8e0a0;
    padding: 2px 8px;
    border-radius: 20px;
    font-family: 'Nunito', sans-serif;
  }

  .form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    margin-bottom: 14px;
  }

  .form-column {
    display: flex;
    flex-direction: column;
    gap: 14px;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 5px;
  }

  .roles-group {
    flex: 1;
  }

  .form-label {
    font-size: 11px;
    font-weight: 800;
    color: #5a8040;
    text-transform: uppercase;
    letter-spacing: 0.08em;
    font-family: 'Nunito', sans-serif;
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .label-icon {
    color: #82bd43;
  }

  .label-hint {
    font-size: 10px;
    font-weight: 600;
    color: #9dbf78;
    text-transform: none;
    letter-spacing: 0;
    margin-left: 4px;
  }

  .form-input :deep(.q-field__control) {
    border-radius: 10px !important;
    background: #f7f9f4 !important;
    border: 1px solid #e4edd8 !important;
  }

  .form-input :deep(.q-field__control:focus-within) {
    border-color: #82bd43 !important;
    box-shadow: 0 0 0 3px rgba(130, 189, 67, 0.15) !important;
  }

  .form-input :deep(.q-field--error .q-field__control) {
    border-color: #e53935 !important;
  }

  .form-input :deep(.q-field__bottom) {
    font-size: 11px;
    font-weight: 600;
  }

  .form-input :deep(.q-field__native) {
    font-family: 'Nunito', sans-serif;
    font-weight: 600;
    color: #2a5c1a;
  }

  .form-input :deep(.q-field__label) {
    font-family: 'Nunito', sans-serif;
    color: #9dbf78;
  }

  .info-box-compact {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    background: #f0f7e8;
    border: 1px solid #c8e0a0;
    border-radius: 10px;
    padding: 10px 12px;
    margin-top: auto;
  }

  .info-box-compact p {
    font-size: 11px;
    font-weight: 600;
    color: #4a8c25;
    line-height: 1.5;
    font-family: 'Nunito', sans-serif;
    margin: 0;
  }

  .info-box-compact p strong {
    color: #2a5c1a;
    font-weight: 800;
  }

  .fortaleza-wrap {
    display: flex;
    align-items: center;
    gap: 10px;
    flex-wrap: wrap;
  }

  .fortaleza-label {
    font-size: 12px;
    font-weight: 700;
    color: #5a8040;
  }

  .fortaleza-bars {
    display: flex;
    gap: 5px;
    flex: 1;
  }

  .fortaleza-bar {
    flex: 1;
    height: 5px;
    border-radius: 4px;
    background: #e4edd8;
  }

  .fortaleza-text {
    font-size: 12px;
    font-weight: 800;
  }

  .bar-rojo { background: #ef4444; }
  .bar-naranja { background: #d97b1a; }
  .bar-amarillo { background: #eab308; }
  .bar-verde { background: #4a8c25; }
  .ft-rojo { color: #ef4444; }
  .ft-naranja { color: #d97b1a; }
  .ft-amarillo { color: #eab308; }
  .ft-verde { color: #4a8c25; }

  .req-list {
    display: flex;
    flex-direction: column;
    gap: 7px;
    border-top: 1px solid #e4edd8;
    padding-top: 10px;
  }

  .req-item {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .req-dot {
    width: 18px;
    height: 18px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .req-dot-pending { background: #ddecc5; }
  .req-dot-ok { background: #4a8c25; }
  .req-texto { font-size: 12px; font-weight: 600; color: #5a8040; }
  .req-ok .req-texto { color: #2a5c1a; font-weight: 700; }

  .dialog-footer {
    display: flex;
    gap: 10px;
    padding: 14px 20px 18px;
    border-top: 1px solid #e4edd8;
    background: #ffffff;
  }

  .btn-cancel {
    flex: 1;
    background: #fff !important;
    color: #5a5a5a !important;
    border: 1.5px solid #d0d0d0 !important;
    border-radius: 9px !important;
    font-family: 'Nunito', sans-serif;
    font-size: 14px;
    font-weight: 700;
    height: 42px;
  }

  .btn-cancel:hover {
    background: #f7f7f7 !important;
    border-color: #bbb !important;
  }

  .btn-guardar {
    flex: 2;
    background: #82bd43 !important;
    color: #fff !important;
    border-radius: 9px !important;
    font-family: 'Nunito', sans-serif;
    font-size: 14px;
    font-weight: 800;
    height: 42px;
    box-shadow: 0 4px 14px rgba(74, 140, 37, 0.3);
  }

  .btn-guardar:hover:not(.q-btn--disabled) {
    background: #4a8c25 !important;
  }

  .btn-guardar.q-btn--disabled {
    opacity: 0.5 !important;
    background: #82bd43 !important;
    color: #fff !important;
  }

  @media (max-width: 600px) {
    .form-grid {
      grid-template-columns: 1fr;
    }

    .dialog-card {
      max-width: 95vw !important;
    }
  }
</style>
