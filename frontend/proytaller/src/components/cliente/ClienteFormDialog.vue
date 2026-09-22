<template>
  <q-dialog v-model="visible" persistent>
    <q-card class="client-dialog">
      <q-card-section class="dialog-head row items-center no-wrap">
        <q-avatar icon="person_add" color="green-1" text-color="green-9" size="48px" />
        <div class="q-ml-md"><div class="dialog-title">{{ editando ? 'Editar cliente' : 'Nuevo cliente' }}</div><div class="dialog-sub">Datos para identificar las ventas del cliente</div></div>
        <q-space /><q-btn flat round dense icon="close" color="grey-7" v-close-popup />
      </q-card-section>
      <q-separator />
      <q-card-section class="row q-col-gutter-md">
        <div class="col-12 col-md-6"><q-input v-model.trim="form.nombres" outlined label="Nombres *" placeholder="Ej.: María Fernanda" color="green-8" class="client-field" :error="!!errores.nombres" :error-message="errores.nombres" /></div>
        <div class="col-12 col-md-6"><q-input v-model.trim="form.apellidoPaterno" outlined label="Apellido paterno" placeholder="Ej.: Pérez" color="green-8" class="client-field" :error="!!errores.apellidoPaterno" :error-message="errores.apellidoPaterno" /></div>
        <div class="col-12 col-md-6"><q-input v-model.trim="form.apellidoMaterno" outlined label="Apellido materno" placeholder="Ej.: Gutiérrez" color="green-8" class="client-field" :error="!!errores.apellidoMaterno" :error-message="errores.apellidoMaterno" /></div>
        <div class="col-12 col-md-6"><q-input v-model.trim="form.ci" outlined label="CI *" placeholder="Ej.: 6543210" maxlength="8" inputmode="numeric" color="green-8" class="client-field" :error="!!errores.ci" :error-message="errores.ci" @update:model-value="sanearCi" /></div>
        <div class="col-12 col-md-6"><q-input v-model.trim="form.correo" outlined type="email" label="Correo electrónico" placeholder="Ej.: maria@email.com" color="green-8" class="client-field" :error="!!errores.correo" :error-message="errores.correo" /></div>
        <div class="col-12 col-md-6"><q-input v-model.trim="form.telefono" outlined type="tel" label="Número de celular" placeholder="Ej.: 70000000 (opcional)" maxlength="20" inputmode="tel" color="green-8" class="client-field" /></div>
        <div class="col-12 col-md-6"><q-input v-model.number="form.limiteCredito" outlined type="number" min="0" step="0.01" label="Límite de crédito" placeholder="Ej.: 500.00 (opcional)" prefix="Bs" color="green-8" class="client-field" /></div>
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
const errores = reactive({ nombres: '', apellidoPaterno: '', apellidoMaterno: '', ci: '', correo: '' })
const formValido = computed(() => Boolean(form.nombres?.trim() && /^\d{7,8}$/.test(form.ci?.trim() || '') && (!form.correo || /^\S+@\S+\.\S+$/.test(form.correo))))
watch(() => props.cliente, (c) => { Object.assign(form, c ? { nombres: c.nombres || '', apellidoPaterno: c.apellidoPaterno || '', apellidoMaterno: c.apellidoMaterno || '', ci: c.ci || '', correo: c.correo || '', telefono: c.telefono || '', limiteCredito: c.limiteCredito ?? null, direccion: c.direccion || '' } : { nombres: '', apellidoPaterno: '', apellidoMaterno: '', ci: '', correo: '', telefono: '', limiteCredito: null, direccion: '' }); limpiarErrores() }, { immediate: true })
function limpiarErrores() { Object.keys(errores).forEach(k => { errores[k] = '' }) }
function sanearCi(value) { form.ci = String(value || '').replace(/\D/g, '').slice(0, 8) }
function guardar() {
  limpiarErrores()
  if (!form.nombres) errores.nombres = 'Los nombres son obligatorios'
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
.client-field :deep(.q-field__label) { color: #55705c; font-weight: 700; }
.client-field :deep(.q-field__native), .client-field :deep(.q-field__input) { color: #263238; font-weight: 600; }
.client-field :deep(input::placeholder) { color: #a0ada4; opacity: 1; font-weight: 400; }
@media (max-width: 600px) { .dialog-head { padding: 16px; } .client-dialog .q-card__section:nth-child(3) { padding: 16px; } .dialog-actions { padding: 12px 16px; } }
</style>
