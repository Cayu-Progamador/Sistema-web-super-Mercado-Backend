<template>
  <q-dialog v-model="visible" persistent>
    <q-card class="client-dialog">
      <q-card-section class="dialog-head row items-center no-wrap">
        <q-avatar icon="person_add" color="green-1" text-color="green-9" size="48px" />
        <div class="q-ml-md"><div class="dialog-title">{{ editando ? 'Editar cliente' : 'Nuevo cliente' }}</div><div class="dialog-sub">Datos para identificar las ventas del cliente</div></div>
        <q-space /><q-btn flat round dense icon="close" color="grey-7" v-close-popup />
      </q-card-section>
      <div v-if="errores.telefono" class="phone-validation q-mx-md q-mb-sm">
        {{ errores.telefono }}
      </div>
      <q-separator />
      <q-card-section class="row q-col-gutter-md">
        <div class="col-12 col-md-6"><q-input v-model.trim="form.nombres" outlined label-slot placeholder="Ej.: María Fernanda" color="green-8" class="client-field" :error="!!errores.nombres" :error-message="errores.nombres" @blur="validarCampo('nombres')"><template v-slot:label>Nombres <span class="required-mark">*</span></template></q-input></div>
        <div class="col-12 col-md-6"><q-input v-model.trim="form.apellidoPaterno" outlined label="Apellido paterno" placeholder="Ej.: Pérez" color="green-8" class="client-field" :error="!!errores.apellidoPaterno" :error-message="errores.apellidoPaterno" /></div>
        <div class="col-12 col-md-6"><q-input v-model.trim="form.apellidoMaterno" outlined label="Apellido materno" placeholder="Ej.: Gutiérrez" color="green-8" class="client-field" :error="!!errores.apellidoMaterno" :error-message="errores.apellidoMaterno" /></div>
        <div class="col-12 col-md-6"><q-input v-model.trim="form.ci" outlined label-slot placeholder="Ej.: 6543210" maxlength="8" inputmode="numeric" color="green-8" class="client-field" :error="!!errores.ci" :error-message="errores.ci" @update:model-value="sanearCi" @blur="validarCampo('ci')"><template v-slot:label>CI <span class="required-mark">*</span></template></q-input></div>
        <div class="col-12 col-md-6"><q-input v-model.trim="form.correo" outlined type="email" label-slot placeholder="Ej.: maria@email.com" color="green-8" class="client-field" :error="!!errores.correo" :error-message="errores.correo" @blur="validarCampo('correo')"><template v-slot:label>Correo electrónico <span class="required-mark">*</span></template></q-input></div>
        <div class="col-12 col-md-6"><q-input v-model.trim="form.telefono" outlined type="tel" label-slot placeholder="Ej.: 70000000" maxlength="8" inputmode="numeric" color="green-8" class="client-field" :error="!!errores.telefono" :error-message="errores.telefono" @blur="validarCampo('telefono')"><template v-slot:label>Número de celular <span class="required-mark">*</span></template></q-input></div>
        <div class="col-12 col-md-6"><q-input v-model="form.limiteCredito" outlined type="text" inputmode="numeric" label-slot placeholder="De Bs 0 a Bs 300" prefix="Bs" color="green-8" class="client-field" :error="!!errores.limiteCredito" :error-message="errores.limiteCredito" @keydown="bloquearNoDigito" @update:model-value="sanearLimiteCredito" @blur="validarCampo('limiteCredito')"><template v-slot:label>Límite de crédito <span class="required-mark">*</span></template></q-input></div>
        <div class="col-12 col-md-6"><q-input v-model.trim="form.direccion" outlined label="Dirección" placeholder="Ej.: Av. Arce #123, La Paz" color="green-8" class="client-field" /></div>
      </q-card-section>
      <q-card-actions align="right" class="dialog-actions">
        <q-btn flat label="Cancelar" color="grey-7" no-caps v-close-popup />
        <q-btn unelevated label="Guardar cliente" color="green-8" no-caps :loading="guardando" :disable="!formValido" @click="guardar" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useQuasar } from 'quasar'
const props = defineProps({ modelValue: Boolean, cliente: { type: Object, default: null }, guardando: Boolean })
const emit = defineEmits(['update:modelValue', 'guardar'])
const $q = useQuasar()
const visible = computed({ get: () => props.modelValue, set: v => emit('update:modelValue', v) })
const editando = computed(() => Boolean(props.cliente?.idCliente))
const form = reactive({ nombres: '', apellidoPaterno: '', apellidoMaterno: '', ci: '', correo: '', telefono: '', limiteCredito: null, direccion: '' })
const errores = reactive({ nombres: '', apellidoPaterno: '', apellidoMaterno: '', ci: '', correo: '', telefono: '', limiteCredito: '' })
const textoNombreValido = valor => /^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]+$/.test((valor || '').trim())
const formValido = computed(() => Boolean(form.nombres?.trim().length >= 2 && textoNombreValido(form.nombres) && (!form.apellidoPaterno || textoNombreValido(form.apellidoPaterno)) && (!form.apellidoMaterno || textoNombreValido(form.apellidoMaterno)) && /^\d{4,8}$/.test(form.ci?.trim() || '') && /^\S+@\S+\.\S+$/.test(form.correo?.trim() || '') && form.limiteCredito !== null && form.limiteCredito !== '' && /^\d+$/.test(String(form.limiteCredito)) && Number(form.limiteCredito) >= 0 && Number(form.limiteCredito) <= 300))
watch(() => props.cliente, (c) => { Object.assign(form, c ? { nombres: c.nombres || '', apellidoPaterno: c.apellidoPaterno || '', apellidoMaterno: c.apellidoMaterno || '', ci: c.ci || '', correo: c.correo || '', telefono: c.telefono || '', limiteCredito: c.limiteCredito ?? null, direccion: c.direccion || '' } : { nombres: '', apellidoPaterno: '', apellidoMaterno: '', ci: '', correo: '', telefono: '', limiteCredito: null, direccion: '' }); limpiarErrores() }, { immediate: true })
function limpiarErrores() { Object.keys(errores).forEach(k => { errores[k] = '' }) }
function sanearCi(value) {
  const original = String(value || '')
  const soloDigitos = original.replace(/\D/g, '')
  if (original !== soloDigitos) $q.notify({ type: 'warning', message: 'El CI solo permite números, sin espacios ni caracteres especiales.' })
  if (soloDigitos.length > 8) $q.notify({ type: 'warning', message: 'El CI permite un máximo de 8 dígitos.' })
  form.ci = soloDigitos.slice(0, 8)
  errores.ci = form.ci.length > 0 && form.ci.length < 4 ? 'El CI no puede tener menos de 4 dígitos' : ''
}
function validarCampo(campo) {
  if (campo === 'nombres') errores.nombres = !form.nombres?.trim() ? 'Los nombres son obligatorios' : !textoNombreValido(form.nombres) ? 'Los nombres solo pueden contener letras' : ''
  if (campo === 'ci') errores.ci = !form.ci ? 'El CI es obligatorio' : !/^\d{4,8}$/.test(form.ci) ? 'El CI debe tener entre 4 y 8 dígitos' : ''
  if (campo === 'correo') errores.correo = !form.correo?.trim() ? 'El correo electrónico es obligatorio' : !/^\S+@\S+\.\S+$/.test(form.correo) ? 'Ingresa un correo electrónico válido' : ''
  if (campo === 'telefono') errores.telefono = !form.telefono ? 'El número de celular es obligatorio' : !/^\d{8}$/.test(form.telefono) ? 'El celular debe tener exactamente 8 dígitos' : ''
  if (campo === 'limiteCredito') errores.limiteCredito = form.limiteCredito === null || form.limiteCredito === '' ? 'El límite de crédito es obligatorio' : Number(form.limiteCredito) < 0 || Number(form.limiteCredito) > 300 ? 'El límite de crédito debe estar entre Bs 0 y Bs 300.00' : ''
}

function bloquearCaracterNoNumerico(evento) {
  if (['-', '+', 'e', 'E'].indexOf(evento.key) >= 0) evento.preventDefault()
}

function bloquearNoDigito(evento) {
  var teclasPermitidas = ['Backspace', 'Delete', 'Tab', 'ArrowLeft', 'ArrowRight', 'Home', 'End']
  if (teclasPermitidas.indexOf(evento.key) >= 0 || evento.ctrlKey || evento.metaKey) return
  if (!/^[0-9]$/.test(evento.key)) evento.preventDefault()
}

function sanearLimiteCredito(value) {
  var limpio = String(value || '').replace(/\D/g, '')
  form.limiteCredito = limpio
}
function sanearTextoNombre(value) {
  return String(value || '').replace(/[^A-Za-zÁÉÍÓÚáéíóúÑñÜü ]/g, '')
}

watch(() => form.ci, value => {
  const limpio = String(value || '').replace(/\D/g, '').slice(0, 8)
  if (limpio !== value) form.ci = limpio
})

watch(() => form.correo, value => {
  const correo = String(value || '').trim()
  if (!correo) {
    errores.correo = ''
  } else if (!/^\S+@\S+\.\S+$/.test(correo)) {
    errores.correo = 'Ingresa un correo electrónico válido'
  } else {
    errores.correo = ''
  }
})

watch(() => form.telefono, value => {
  const original = String(value || '')
  const limpio = original.replace(/\D/g, '').slice(0, 8)
  if (limpio !== original) form.telefono = limpio

  errores.telefono = limpio.length > 0 && limpio.length !== 8
    ? 'El celular debe tener exactamente 8 dígitos'
    : ''
})

watch(() => form.nombres, value => {
  const limpio = sanearTextoNombre(value)
  if (limpio !== value) form.nombres = limpio
})

watch(() => form.apellidoPaterno, value => {
  const limpio = sanearTextoNombre(value)
  if (limpio !== value) form.apellidoPaterno = limpio
})

watch(() => form.apellidoMaterno, value => {
  const limpio = sanearTextoNombre(value)
  if (limpio !== value) form.apellidoMaterno = limpio
})

function guardar() {
  limpiarErrores()
  if (!form.correo?.trim()) {
    errores.correo = 'El correo electrónico es obligatorio'
  } else if (!/^\S+@\S+\.\S+$/.test(form.correo.trim())) {
    errores.correo = 'Ingresa un correo electrónico válido'
  }
  if (!form.telefono) {
    errores.telefono = 'El número de celular es obligatorio'
  } else if (!/^\d{8}$/.test(form.telefono)) {
    errores.telefono = 'El celular debe tener exactamente 8 dígitos'
  }
  if (!form.nombres) errores.nombres = 'Los nombres son obligatorios'
  else if (!textoNombreValido(form.nombres)) errores.nombres = 'Los nombres solo pueden contener letras'
  if (form.apellidoPaterno && !textoNombreValido(form.apellidoPaterno)) errores.apellidoPaterno = 'El apellido paterno solo puede contener letras'
  if (form.apellidoMaterno && !textoNombreValido(form.apellidoMaterno)) errores.apellidoMaterno = 'El apellido materno solo puede contener letras'
  if (!form.ci) errores.ci = 'El CI es obligatorio'
  else if (!/^\d{7,8}$/.test(form.ci)) errores.ci = 'El CI debe tener entre 7 y 8 dígitos'
  if (form.correo && !/^\S+@\S+\.\S+$/.test(form.correo)) errores.correo = 'Ingresa un correo válido'
  if (Object.values(errores).some(Boolean)) { $q.notify({ type: 'warning', message: 'Completa correctamente los campos obligatorios.' }); return }
  emit('guardar', { ...form, idCliente: props.cliente?.idCliente })
}
</script>

<style scoped>
.client-dialog { width: 650px; max-width: 94vw; border-radius: 20px; }
.dialog-head { padding: 20px 24px; background: #f4fbf5; }
.dialog-title { color: #1b5e20; font-size: 20px; font-weight: 900; }
.dialog-sub { color: #718078; font-size: 12px; margin-top: 3px; }
.dialog-actions { padding: 14px 20px; background: #fbfdfb; }
.client-field { margin-bottom: 4px; }
.client-field :deep(.q-field__control) { min-height: 52px; border-radius: 12px; background: #fcfefd; transition: background .2s ease, box-shadow .2s ease; }
.client-field :deep(.q-field__control:hover) { background: #f5fbf6; }
.client-field :deep(.q-field--focused .q-field__control) { box-shadow: 0 0 0 3px rgba(46, 125, 50, .12); }
.client-field :deep(.q-field__label) { color: #55705c; font-size: 11px; font-weight: 700; }
.client-field :deep(.q-field__native), .client-field :deep(.q-field__input) { color: #263238; font-weight: 600; }
.client-field :deep(input::placeholder) { color: #a0ada4; opacity: 1; font-weight: 400; }
.phone-validation { color: #c62828; font-size: 12px; font-weight: 700; }
.required-mark { color: #d32f2f; font-weight: 900; }
@media (max-width: 600px) { .dialog-head { padding: 16px; } .client-dialog .q-card__section:nth-child(3) { padding: 16px; } .dialog-actions { padding: 12px 16px; } }
</style>
