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
        <div class="col-12 col-md-6"><q-input v-model.trim="form.nombres" outlined label="Nombres *" color="green-8" :error="!!errores.nombres" :error-message="errores.nombres" /></div>
        <div class="col-12 col-md-6"><q-input v-model.trim="form.apellidoPaterno" outlined label="Apellido paterno *" color="green-8" :error="!!errores.apellidoPaterno" :error-message="errores.apellidoPaterno" /></div>
        <div class="col-12 col-md-6"><q-input v-model.trim="form.apellidoMaterno" outlined label="Apellido materno *" color="green-8" :error="!!errores.apellidoMaterno" :error-message="errores.apellidoMaterno" /></div>
        <div class="col-12 col-md-6"><q-input v-model.trim="form.ci" outlined label="CI *" color="green-8" :error="!!errores.ci" :error-message="errores.ci" /></div>
        <div class="col-12"><q-input v-model.trim="form.correo" outlined type="email" label="Correo electrónico" color="green-8" :error="!!errores.correo" :error-message="errores.correo" /></div>
      </q-card-section>
      <q-card-actions align="right" class="dialog-actions">
        <q-btn flat label="Cancelar" color="grey-7" no-caps v-close-popup />
        <q-btn unelevated label="Guardar cliente" color="green-8" no-caps :loading="guardando" @click="guardar" />
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
const form = reactive({ nombres: '', apellidoPaterno: '', apellidoMaterno: '', ci: '', correo: '' })
const errores = reactive({ nombres: '', apellidoPaterno: '', apellidoMaterno: '', ci: '', correo: '' })
watch(() => props.cliente, (c) => { Object.assign(form, c ? { nombres: c.nombres || '', apellidoPaterno: c.apellidoPaterno || '', apellidoMaterno: c.apellidoMaterno || '', ci: c.ci || '', correo: c.correo || '' } : { nombres: '', apellidoPaterno: '', apellidoMaterno: '', ci: '', correo: '' }); limpiarErrores() }, { immediate: true })
function limpiarErrores() { Object.keys(errores).forEach(k => { errores[k] = '' }) }
function guardar() {
  limpiarErrores()
  if (!form.nombres) errores.nombres = 'Los nombres son obligatorios'
  if (!form.apellidoPaterno) errores.apellidoPaterno = 'El apellido paterno es obligatorio'
  if (!form.apellidoMaterno) errores.apellidoMaterno = 'El apellido materno es obligatorio'
  if (!form.ci) errores.ci = 'El CI es obligatorio'
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
</style>
