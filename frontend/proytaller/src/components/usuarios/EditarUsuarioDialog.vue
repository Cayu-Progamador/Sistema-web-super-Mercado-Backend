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
                :rules="[val => !!val || 'El username es obligatorio']" />
            </div>

            <div class="form-group">
              <label class="form-label">
                <q-icon name="lock" size="14px" class="label-icon" />
                Nueva Contraseña
                <span class="label-hint">(opcional)</span>
              </label>
              <q-input v-model="form.password" outlined dense type="password" placeholder="••••••••"
                class="form-input" />
            </div>

            <div class="form-group">
              <label class="form-label">
                <q-icon name="badge" size="14px" class="label-icon" />
                Empleado Asociado
              </label>
              <q-select v-model="form.empleadoId" outlined dense :options="empleados" option-label="nombreCompleto"
                option-value="id" emit-value map-options placeholder="Selecciona un empleado" class="form-input"
                :rules="[val => !!val || 'El empleado es obligatorio']">
                <template v-slot:option="scope">
                  <q-item v-bind="scope.itemProps">
                    <q-item-section>
                      <q-item-label class="emp-nombre">{{ scope.opt.nombreCompleto }}</q-item-label>
                    </q-item-section>
                  </q-item>
                </template>
              </q-select>
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
                class="form-input" :rules="[val => val?.length > 0 || 'Selecciona al menos un rol']">
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

          <div>Empleado seleccionado: {{ form.empleadoId }}</div>
          <div>Roles seleccionados: {{ form.roles }}</div>

        </div>

      </q-card-section>

      <q-card-actions class="dialog-footer">
        <q-btn class="btn-cancel" flat no-caps @click="cerrar">
          <q-icon name="close" size="15px" class="q-mr-xs" />
          Cancelar
        </q-btn>

        <q-btn class="btn-guardar" unelevated no-caps :disable="!esValido || cargando" :loading="cargando"
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

  const props = defineProps({
    modelValue: { type: Boolean, default: false },
    id: { type: [Number, String], default: null },
    username: { type: String, default: '' },
    roles: { type: Array, default: () => [] },
    empleadoId: { type: [Number, String], default: null },
    empleados: { type: Array, default: () => [] },
    rolesDisponibles: { type: Array, default: () => [] }
  })

  const emit = defineEmits(['update:modelValue', 'guardar'])

  const abierto = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
  })

  const cargando = ref(false)

  const form = ref({
    username: '',
    password: '',
    empleadoId: null,
    roles: []
  })

  const usuarioActual = computed(() => ({
    username: props.username || '—'
  }))

  const iniciales = computed(() => {
    if (!props.username) return 'U'
    return props.username.substring(0, 2).toUpperCase()
  })

  const rolesActuales = computed(() => props.roles || [])

  const esValido = computed(() => {
    return form.value.username?.trim() &&
      form.value.empleadoId &&
      form.value.roles?.length > 0
  })

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
      empleadoId: props.empleadoId ? Number(props.empleadoId) : null,
      roles: [...(props.roles || [])]
    }

  }

  const cerrar = () => {
    abierto.value = false
  }

  const guardar = async () => {
    if (!esValido.value) return
    cargando.value = true
    await new Promise(r => setTimeout(r, 600))
    cargando.value = false

    const payload = {
      username: form.value.username,
      empleadoId: form.value.empleadoId,
      roles: form.value.roles
    }

    if (form.value.password?.trim()) {
      payload.password = form.value.password
    }

      


    emit('guardar', { id: props.id, ...payload })
    cerrar()
  }
</script>

  <style scoped>
  @import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap');

  .dialog-card {
    width: 100% !important;
    max-width: 700px !important;
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

  .form-input :deep(.q-field__native) {
    font-family: 'Nunito', sans-serif;
    font-weight: 600;
    color: #2a5c1a;
  }

  .form-input :deep(.q-field__label) {
    font-family: 'Nunito', sans-serif;
    color: #9dbf78;
  }

  .emp-nombre {
    font-weight: 700;
    color: #2a5c1a;
    font-family: 'Nunito', sans-serif;
  }

  .emp-cargo {
    font-weight: 600;
    color: #7aaa4e;
    font-family: 'Nunito', sans-serif;
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