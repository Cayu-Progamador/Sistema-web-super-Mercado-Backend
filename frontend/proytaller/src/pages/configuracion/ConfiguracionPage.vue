<template>
  <q-page class="config-page q-pa-md">
    <div class="config-header">
      <div>
        <div class="eyebrow"><q-icon name="settings" /> Administración del sistema</div>
        <h1><q-icon name="settings" color="green-8" /> Configuración</h1>
        <p>Administra los datos del supermercado y la recuperación de cuentas.</p>
      </div>
      <q-badge color="green-1" text-color="green-9" label="Solo administrador" class="admin-badge" />
    </div>

    <q-card v-if="seccion === 'datos'" flat bordered class="config-card">
      <q-card-section class="card-heading"><q-avatar icon="storefront" color="green-1" text-color="green-9" /><div><h2>Datos del supermercado</h2><p>Estos datos se utilizarán en el sistema y en los comprobantes.</p></div></q-card-section>
      <q-separator />
      <q-card-section class="form-grid">
        <q-input v-model="datos.nombreComercial" outlined label="Nombre comercial" hint="Ejemplo: Supermercado La Economía · Nombre visible del negocio" />
        <q-input v-model="datos.razonSocial" outlined label="Razón social" hint="Ejemplo: Comercial La Economía S.R.L. · Nombre legal" />
        <q-input v-model="datos.nit" outlined label="NIT" hint="Ejemplo: 123456789 · Identificación tributaria" />
        <q-input v-model="datos.telefono" outlined label="Teléfono" hint="Ejemplo: 70000000 · Contacto del supermercado" />
        <q-input v-model="datos.correo" outlined label="Correo electrónico" type="email" hint="Ejemplo: contacto@economia.com · Correo del negocio" />
        <q-select v-model="datos.departamento" outlined label="Departamento" hint="Selecciona el departamento donde está el negocio" :options="departamentosBolivia" />
        <div class="logo-field">
          <div class="logo-preview"><img v-if="datos.logoUrl" :src="resolverArchivo(datos.logoUrl)" alt="Logo del supermercado" /><q-icon v-else name="storefront" size="34px" color="grey-5" /></div>
          <q-file v-model="archivoLogo" class="logo-upload" outlined dense accept="image/png,image/jpeg,image/webp" label="Logo del supermercado" @update:model-value="cargarLogo"><template #prepend><q-icon name="image" color="green-8" /></template><template #hint>Ejemplo: logo.png · Aparece en los PDF</template></q-file>
        </div>
        <div class="background-field full-field">
          <div class="background-preview" :style="datos.fondoLoginUrl ? { backgroundImage: `url(${resolverArchivo(datos.fondoLoginUrl)})` } : {}"><q-icon v-if="!datos.fondoLoginUrl" name="wallpaper" size="34px" color="grey-5" /></div>
          <q-file v-model="archivoFondoLogin" class="logo-upload" outlined dense accept="image/gif,image/png,image/jpeg,image/webp" label="Imagen de fondo del inicio de sesión" @update:model-value="cargarFondoLogin"><template #prepend><q-icon name="wallpaper" color="green-8" /></template><template #hint>Imagen o GIF para el fondo del login</template></q-file>
        </div>
        <q-input v-model="datos.direccion" outlined label="Dirección" class="full-field" hint="Ejemplo: Av. Principal #123 · Dirección del negocio" />
        <q-input v-model="datos.mensajeComprobante" outlined type="textarea" autogrow label="Mensaje del comprobante" class="full-field" hint="Ejemplo: Gracias por su compra. Vuelva pronto. · Texto al final de los PDF" />
      </q-card-section>
      <q-card-actions align="right" class="card-actions"><q-btn flat no-caps label="Restablecer" color="grey-7" :disable="cargandoDatos" @click="cargarDatos" /><q-btn unelevated no-caps color="green-8" icon="save" label="Guardar cambios" :loading="cargandoDatos" @click="guardarDatos" /></q-card-actions>
    </q-card>

    <q-card v-else flat bordered class="config-card">
      <q-card-section class="card-heading"><q-avatar icon="lock_reset" color="orange-1" text-color="orange-9" /><div><h2>Recuperación de contraseña</h2><p>Define cómo se validará la recuperación de una cuenta.</p></div></q-card-section>
      <q-separator />
      <q-card-section class="security-banner"><q-icon name="verified_user" size="24px" /><div><strong>Configuración segura</strong><span>El código debe ser temporal y tener un límite de intentos.</span></div></q-card-section>
      <q-card-section class="form-grid">
        <q-input v-model="recuperacion.nombreUsuario" outlined label="Empleado remitente" readonly><template #prepend><q-icon name="person" color="green-8" /></template></q-input>
        <q-input v-model="recuperacion.correoUsuario" outlined label="Correo del usuario" hint="Correo Gmail configurado para enviar los códigos" readonly class="full-field"><template #prepend><q-icon name="email" color="green-8" /></template></q-input>
        <q-input v-model="recuperacion.passwordEmail" outlined label="Contraseña de aplicación de Gmail" type="password" :hint="recuperacion.passwordConfigurada ? 'Ya existe una contraseña protegida. Déjalo vacío para conservarla.' : 'Ingresa la contraseña de aplicación generada por Google.'" />
        <div class="toggle-field full-field"><div><strong>Recuperación activa</strong><small>Permite solicitar códigos para recuperar contraseñas.</small></div><q-toggle v-model="recuperacion.activa" color="green-8" /></div>
      </q-card-section>
      <q-card-actions align="right" class="card-actions"><q-btn flat no-caps label="Restablecer" color="grey-7" :disable="cargandoRecuperacion" @click="cargarRecuperacion" /><q-btn unelevated no-caps color="green-8" icon="save" label="Guardar cambios" :loading="cargandoRecuperacion" @click="guardarRecuperacion" /></q-card-actions>
    </q-card>
  </q-page>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useQuasar } from 'quasar'
import { useRoute } from 'vue-router'
import { guardarConfiguracionCorreo, obtenerConfiguracionCorreo } from '../../api/configuracion/configuracionCorreo'
import { guardarConfiguracionEmpresa, obtenerConfiguracionEmpresa } from '../../api/configuracion/configuracionEmpresa'

const $q = useQuasar()
const route = useRoute()
const seccion = computed(() => route.path.endsWith('/recuperacion') ? 'recuperacion' : 'datos')
const datosIniciales = { nombreComercial: '', razonSocial: '', nit: '', telefono: '', correo: '', departamento: '', direccion: '', logoUrl: '', fondoLoginUrl: '', mensajeComprobante: 'Gracias por su compra. Vuelva pronto.' }
const recuperacionInicial = { nombreUsuario: '', correoUsuario: '', passwordEmail: '', passwordConfigurada: false, activa: false }
const departamentosBolivia = ['Chuquisaca', 'La Paz', 'Cochabamba', 'Oruro', 'Potosí', 'Tarija', 'Santa Cruz', 'Beni', 'Pando']
const datos = reactive({ ...datosIniciales })
const cargandoDatos = ref(false)
const recuperacion = reactive({ ...recuperacionInicial })
const cargandoRecuperacion = ref(false)
const archivoLogo = ref(null)
const archivoFondoLogin = ref(null)
function cargarLogo(archivo) {
  if (!archivo) return
  const lector = new FileReader()
  lector.onload = evento => { datos.logoUrl = evento.target.result }
  lector.readAsDataURL(archivo)
}
function cargarFondoLogin(archivo) {
  if (!archivo) return
  const lector = new FileReader()
  lector.onload = evento => { datos.fondoLoginUrl = evento.target.result }
  lector.readAsDataURL(archivo)
}
function resolverArchivo(url) {
  if (!url || url.startsWith('data:') || url.startsWith('http')) return url
  return `${import.meta.env.VITE_API_URL || ''}${url}`
}
async function cargarDatos() {
  cargandoDatos.value = true
  try {
    const respuesta = await obtenerConfiguracionEmpresa()
    Object.assign(datos, datosIniciales, respuesta || {})
    archivoLogo.value = null
    archivoFondoLogin.value = null
    localStorage.removeItem('config-supermercado-datos')
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudieron cargar los datos del supermercado.' })
  } finally {
    cargandoDatos.value = false
  }
}
async function guardarDatos() {
  if (!datos.nombreComercial.trim()) {
    $q.notify({ type: 'warning', message: 'El nombre comercial es obligatorio.' })
    return
  }
  cargandoDatos.value = true
  try {
    const payload = {
      nombreComercial: datos.nombreComercial,
      razonSocial: datos.razonSocial,
      nit: datos.nit,
      telefono: datos.telefono,
      correo: datos.correo,
      departamento: datos.departamento,
      direccion: datos.direccion,
      mensajeComprobante: datos.mensajeComprobante
    }
    const respuesta = await guardarConfiguracionEmpresa(payload, archivoLogo.value, archivoFondoLogin.value)
    Object.assign(datos, datosIniciales, respuesta || {})
    archivoLogo.value = null
    archivoFondoLogin.value = null
    localStorage.removeItem('config-supermercado-datos')
    $q.notify({ type: 'positive', message: 'Datos del supermercado guardados.' })
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudieron guardar los datos del supermercado.' })
  } finally {
    cargandoDatos.value = false
  }
}
async function cargarRecuperacion() {
  cargandoRecuperacion.value = true
  try {
    const respuesta = await obtenerConfiguracionCorreo()
    Object.assign(recuperacion, recuperacionInicial, respuesta || {}, { passwordEmail: '' })
    localStorage.removeItem('config-supermercado-recuperacion')
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo cargar la configuración de correo.' })
  } finally {
    cargandoRecuperacion.value = false
  }
}
async function guardarRecuperacion() {
  cargandoRecuperacion.value = true
  try {
    const payload = { activo: recuperacion.activa }
    if (recuperacion.passwordEmail.trim()) payload.passwordEmail = recuperacion.passwordEmail
    const respuesta = await guardarConfiguracionCorreo(payload)
    Object.assign(recuperacion, respuesta || {}, { passwordEmail: '' })
    localStorage.removeItem('config-supermercado-recuperacion')
    $q.notify({ type: 'positive', message: 'Configuración de recuperación guardada.' })
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudo guardar la configuración de correo.' })
  } finally {
    cargandoRecuperacion.value = false
  }
}
onMounted(() => {
  if (seccion.value === 'recuperacion') cargarRecuperacion()
  else cargarDatos()
})
watch(seccion, value => {
  if (value === 'recuperacion') cargarRecuperacion()
  else cargarDatos()
})
</script>

<style scoped>
.config-page{min-height:100vh;background:#f5f7fa}.config-header{max-width:1180px;margin:0 auto 22px;display:flex;align-items:flex-end;justify-content:space-between;gap:18px}.eyebrow{display:flex;align-items:center;gap:6px;color:#6f8375;font-size:12px;font-weight:800;text-transform:uppercase;letter-spacing:.06em}.config-header h1{display:flex;align-items:center;gap:9px;margin:7px 0 4px;color:#1b5e20;font-size:30px;font-weight:900}.config-header p{margin:0;color:#789080}.admin-badge{padding:8px 12px;border-radius:20px;font-weight:800}.config-tabs{max-width:1180px;margin:0 auto 18px;display:grid;grid-template-columns:repeat(2,1fr);gap:12px}.config-tab{display:flex;align-items:center;gap:13px;padding:16px 18px;border:1px solid #dce8df;border-radius:15px;background:#fff;color:#5f7466;cursor:pointer;transition:.2s}.config-tab:hover,.config-tab.active{border-color:#66a56d;background:#f0f8f1;color:#1b5e20;box-shadow:0 5px 16px rgba(45,95,52,.08)}.config-tab>.q-icon{font-size:29px}.config-tab span{display:flex;flex-direction:column;gap:3px}.config-tab strong{font-size:14px}.config-tab small{font-size:11px;color:#839288}.config-card{max-width:1180px;margin:0 auto;border-radius:18px;overflow:hidden;border-color:#dce8df}.card-heading{display:flex;align-items:center;gap:14px;padding:22px}.card-heading h2{margin:0;color:#294b32;font-size:20px;font-weight:900}.card-heading p{margin:4px 0 0;color:#849389;font-size:12px}.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:18px;padding:24px}.full-field{grid-column:1/-1}.card-actions{padding:14px 24px 20px;background:#fbfdfb}.card-actions .q-btn{border-radius:9px;font-weight:800}.security-banner{display:flex;align-items:center;gap:12px;margin:22px 24px 0;padding:14px 16px;border:1px solid #f2dfb8;border-radius:12px;background:#fffaf0;color:#9a6611}.security-banner div{display:flex;flex-direction:column;gap:3px}.security-banner span{font-size:12px;color:#9c8965}.toggle-field{display:flex;align-items:center;justify-content:space-between;padding:13px 15px;border:1px solid #dce8df;border-radius:10px;background:#fbfdfb}.toggle-field div{display:flex;flex-direction:column;gap:3px}.toggle-field small{color:#839288;font-size:11px}@media(max-width:700px){.config-header{align-items:flex-start;flex-direction:column}.config-header h1{font-size:25px}.config-tabs{grid-template-columns:1fr}.form-grid{grid-template-columns:1fr;padding:18px}.full-field{grid-column:auto}.security-banner{margin:18px}.card-actions{padding:12px 18px 18px;flex-wrap:wrap}.card-actions .q-btn{flex:1}}
 </style>
<style scoped>
.logo-field,.background-field{display:flex;align-items:center;gap:14px;padding:10px 12px;border:1px solid #dce8df;border-radius:10px;background:#fbfdfb}.logo-preview{display:flex;align-items:center;justify-content:center;width:62px;height:62px;overflow:hidden;border-radius:12px;background:#eef7ef}.logo-preview img{width:100%;height:100%;object-fit:contain}.background-preview{display:flex;align-items:center;justify-content:center;width:180px;height:62px;overflow:hidden;border-radius:12px;background:#eef7ef center/cover no-repeat}.logo-upload{flex:1}@media(max-width:700px){.background-field{align-items:stretch;flex-direction:column}.background-preview{width:100%}}
</style>
