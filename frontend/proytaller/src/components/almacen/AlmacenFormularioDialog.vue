<template>
  <q-dialog v-model="visible" persistent class="alm-dialog-wrap">
    <q-card class="alm-dialog-card">
      <div class="alm-accento"></div>
      <q-card-section class="alm-dialog-header row items-center no-wrap">
        <div class="alm-dialog-icono"><q-icon name="warehouse" size="24px" color="green-8" /></div>
        <div class="col q-ml-md"><div class="alm-dialog-titulo">{{ modoEdicion ? 'Editar Almacén' : 'Nuevo Almacén' }}</div><div class="alm-dialog-subtitulo">{{ modoEdicion ? 'Actualiza la información del espacio de almacenamiento.' : 'Registra un nuevo espacio de almacenamiento.' }}</div></div>
        <q-btn flat round dense icon="close" color="green-8" @click="cerrar" />
      </q-card-section>

      <q-form ref="formRef" class="col column" @submit="guardar">
        <q-stepper v-model="paso" flat animated header-nav color="green-8" class="alm-stepper col">
          <q-step :name="1" title="Información general" icon="info" :done="paso > 1">
            <div class="alm-paso-encabezado"><q-icon name="domain" /><div><strong>Datos principales</strong><span>Identifica y clasifica el almacén.</span></div></div>
            <div class="row q-col-gutter-md q-mt-sm">
              <div class="col-12 col-sm-6"><q-input v-model.trim="form.nombre" outlined label="Nombre *" placeholder="Almacén Principal" color="green-8" :rules="[requerido('El nombre es obligatorio')]" /></div>
              <div class="col-12 col-sm-6"><q-select v-model="form.idTipoAlmacen" :options="opcionesTipo" outlined emit-value map-options label="Tipo *" color="green-8" :rules="[requerido('El tipo es obligatorio')]" /></div>
            </div>
          </q-step>

          <q-step :name="2" title="Información adicional" icon="notes" :done="paso > 2">
            <div class="alm-paso-encabezado"><q-icon name="contact_page" /><div><strong>Información complementaria</strong><span>Registra la referencia y persona responsable.</span></div></div>
            <div class="row q-col-gutter-md q-mt-sm">
              <div class="col-12">
                <q-input v-model.trim="form.descripcion" outlined type="textarea" autogrow label="Descripción" placeholder="Ej.: Almacén general destinado a productos secos." hint="Describe brevemente para qué se utilizará este almacén." persistent-hint color="green-8">
                  <template #prepend><q-icon name="description" color="green-8" /></template>
                </q-input>
              </div>
              <div class="col-12 col-sm-6">
                <q-input v-model.trim="form.direccion" outlined label="Dirección o referencia" placeholder="Ej.: Parte posterior del supermercado" hint="Indica dónde está ubicado físicamente." persistent-hint color="green-8">
                  <template #prepend><q-icon name="place" color="green-8" /></template>
                </q-input>
              </div>
              <div class="col-12 col-sm-6">
                <q-select v-model="form.idResponsable" :options="opcionesResponsable" outlined emit-value map-options label="Responsable" hint="Selecciona al empleado encargado de supervisar el almacén." persistent-hint color="green-8" clearable>
                  <template #prepend><q-icon name="person" color="green-8" /></template>
                </q-select>
              </div>
              <div class="col-12 col-sm-6">
                <q-input :model-value="telefonoResponsable" outlined readonly label="Teléfono del responsable" hint="Se obtiene automáticamente de los datos del empleado." persistent-hint color="green-8">
                  <template #prepend><q-icon name="phone" color="green-8" /></template>
                </q-input>
              </div>
            </div>
          </q-step>

          <template #navigation>
            <q-stepper-navigation class="alm-dialog-footer q-pa-md">
              <div class="row items-center justify-between">
                <q-btn v-if="paso === 1" flat no-caps color="green-8" icon="close" label="Cancelar" @click="cerrar" />
                <q-btn v-else flat no-caps color="green-8" icon="arrow_back" label="Atrás" @click="paso--" />
                <q-btn v-if="paso < 2" unelevated no-caps color="green-8" icon-right="arrow_forward" label="Continuar" @click="paso++" />
                <q-btn v-else unelevated no-caps color="green-8" icon="save" :loading="guardando" :disable="guardando" :label="modoEdicion ? 'Guardar Cambios' : 'Guardar Almacén'" type="submit" />
              </div>
            </q-stepper-navigation>
          </template>
        </q-stepper>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
const props = defineProps({ modelValue: Boolean, almacen: { type: Object, default: null }, tipos: { type: Array, default: () => [] }, responsables: { type: Array, default: () => [] }, guardando: Boolean })
const emit = defineEmits(['update:modelValue', 'guardar'])
const formRef = ref(null)
const paso = ref(1)
const visible = computed({ get: () => props.modelValue, set: val => emit('update:modelValue', val) })
const modoEdicion = computed(() => Boolean(props.almacen?.id))
const base = () => ({ nombre: '', idTipoAlmacen: null, descripcion: '', direccion: '', idResponsable: null })
const form = reactive(base())
const opcionesTipo = computed(() => props.tipos.map(tipo => ({ ...tipo, value: tipo.idTipoAlmacen })))
const opcionesResponsable = computed(() => props.responsables)
const telefonoResponsable = computed(() => props.responsables.find(item => item.value === form.idResponsable)?.telefono || 'Sin teléfono registrado')
const requerido = mensaje => val => Boolean(val) || mensaje
watch(() => props.modelValue, abierto => { if (abierto) { paso.value = 1; Object.assign(form, base(), props.almacen || {}) } })
function cerrar() { visible.value = false; paso.value = 1; formRef.value?.resetValidation() }
function guardar() { emit('guardar', { ...form, id: props.almacen?.id }) }
</script>

<style scoped>
.alm-dialog-wrap :deep(.q-dialog__inner){padding:16px;overflow:hidden!important}.alm-dialog-card{width:94vw;max-width:920px;height:calc(100vh - 32px);max-height:760px;border-radius:20px;overflow:hidden;font-family:'Nunito',sans-serif;display:flex;flex-direction:column}.alm-dialog-card form{display:flex;flex:1;flex-direction:column;min-height:0;overflow:hidden}.alm-accento{height:4px;flex:none;background:linear-gradient(90deg,#82bd43,#4a8c25)}.alm-dialog-header{flex:none;background:linear-gradient(135deg,#f2f8ec,#eaf4d8);border-bottom:1px solid #d8e8c9;padding:18px 22px}.alm-dialog-icono{width:48px;height:48px;border-radius:14px;display:grid;place-items:center;background:#fff;border:1px solid #c8e0a0}.alm-dialog-titulo{font-size:18px;font-weight:900;color:#2a5c1a}.alm-dialog-subtitulo{font-size:12px;color:#6f9457}.alm-stepper{display:flex;flex:1;flex-direction:column;min-height:0;overflow:hidden;box-shadow:none}.alm-stepper :deep(.q-stepper__header){flex:none;background:#fbfcfa;border-bottom:1px solid #e1eadc}.alm-stepper :deep(.q-stepper__content){display:block;flex:1;min-height:0;overflow-x:hidden!important;overflow-y:auto!important;overscroll-behavior-y:contain}.alm-stepper :deep(.q-stepper__panel){min-height:100%}.alm-stepper :deep(.q-stepper__step-inner){padding:28px 32px 90px;min-height:365px}.alm-stepper :deep(.q-stepper__tab--active){color:#4a8c25}.alm-paso-encabezado{display:flex;align-items:center;gap:12px;padding:13px 15px;margin-bottom:18px;background:#f4f9ef;border:1px solid #d8e8c9;border-radius:12px;color:#4a8c25}.alm-paso-encabezado>.q-icon{font-size:25px}.alm-paso-encabezado strong,.alm-paso-encabezado span{display:block}.alm-paso-encabezado strong{color:#2a5c1a;font-size:14px}.alm-paso-encabezado span{font-size:11px;color:#718769;margin-top:2px}.alm-banner{background:#f2f8ec;color:#58724b;font-size:12px;border:1px solid #d8e8c9}.alm-dialog-footer{position:sticky;right:0;bottom:0;left:0;z-index:5;margin-top:-74px;background:#f7faf5;border-top:1px solid #e1eadc;box-shadow:0 -4px 12px rgba(42,92,26,.08)}:deep(.q-field__control){border-radius:10px}
@media(max-width:600px){.alm-dialog-card{width:100vw;max-width:none;min-height:100vh;border-radius:0}.alm-dialog-contenido{max-height:calc(100vh - 165px)}}
</style>
