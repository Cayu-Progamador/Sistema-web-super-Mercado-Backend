<template>
  <q-dialog
    v-model="visible"
    persistent
    :maximized="$q.screen.lt.sm"
    transition-show="slide-up"
    transition-hide="slide-down"
    class="ubi-dialog-wrap"
  >
    <q-card class="ubi-dialog-card column">
      <div class="ubi-dialog-accent" />
      <q-card-section class="ubi-dialog-header row items-center no-wrap">
        <div class="ubi-dialog-icon"><q-icon name="location_on" size="25px" color="green-8" /></div>
        <div class="col q-ml-md"><div class="ubi-dialog-title">{{ modoEdicion ? 'Editar Ubicación' : 'Nueva Ubicación' }}</div><div class="ubi-dialog-sub">{{ modoEdicion ? 'Actualiza los datos del espacio físico.' : 'Registra un nuevo espacio físico dentro del almacén.' }}</div></div>
        <q-btn flat round dense icon="close" color="green-8" @click="cerrar" />
      </q-card-section>

      <q-form ref="formRef" class="col column" @submit="guardar">
        <q-stepper v-model="paso" flat animated header-nav :vertical="$q.screen.lt.md" color="green-8" class="ubi-stepper col">
          <q-step :name="1" title="Almacén" icon="warehouse" :done="paso > 1">
            <CabeceraPaso icono="warehouse" titulo="Almacén de destino" texto="Selecciona el almacén al que pertenecerá esta ubicación." />
            <q-select v-model="form.almacen" :options="almacenes" emit-value map-options outlined label="Almacén *" hint="Ejemplo: Almacén Principal" persistent-hint color="green-8" :disable="modoEdicion" :rules="[requerido('El almacén es obligatorio')]" />
          </q-step>
          <q-step :name="2" title="Información general" icon="info" :done="paso > 2">
            <CabeceraPaso icono="badge" titulo="Información general" texto="Identifica y clasifica el espacio físico." />
            <div class="row q-col-gutter-md">
              <div class="col-12 col-sm-6"><q-input v-model.trim="form.nombre" outlined label="Nombre *" placeholder="Ejemplo: Estante A01" hint="Nombre descriptivo del espacio" persistent-hint color="green-8" :rules="[requerido('El nombre es obligatorio')]" /></div>
              <div class="col-12 col-sm-6"><q-select v-model="form.tipo" :options="tipos" emit-value map-options outlined label="Tipo *" hint="Ejemplo: ESTANTE" persistent-hint color="green-8" :rules="[requerido('El tipo es obligatorio')]" /></div>
            </div>
          </q-step>
          <q-step :name="3" title="Descripción" icon="notes" :done="paso > 3">
            <CabeceraPaso icono="notes" titulo="Descripción" texto="Agrega información útil sobre el uso de la ubicación." />
            <q-input v-model.trim="form.descripcion" outlined type="textarea" autogrow label="Descripción" placeholder="Ejemplo: Estante destinado a alimentos secos." color="green-8" />
          </q-step>
          <q-step :name="4" title="Confirmación" icon="fact_check">
            <CabeceraPaso icono="fact_check" titulo="Revisión final" texto="Verifica la información antes de guardar." />
            <div class="ubi-summary">
              <div v-for="dato in resumen" :key="dato.etiqueta"><small>{{ dato.etiqueta }}</small><strong>{{ dato.valor || '-' }}</strong></div>
            </div>
          </q-step>
          <template #navigation>
            <q-stepper-navigation class="ubi-dialog-footer q-pa-md"><div class="row items-center justify-between">
              <q-btn v-if="paso === 1" flat no-caps color="green-8" icon="close" label="Cancelar" @click="cerrar" />
              <q-btn v-else flat no-caps color="green-8" icon="arrow_back" label="Atrás" @click="paso--" />
              <q-btn v-if="paso < 4" unelevated no-caps color="green-8" icon-right="arrow_forward" label="Continuar" @click="paso++" />
              <q-btn v-else unelevated no-caps color="green-8" icon="save" :label="modoEdicion ? 'Guardar Cambios' : 'Guardar Ubicación'" type="submit" />
            </div></q-stepper-navigation>
          </template>
        </q-stepper>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, defineComponent, h, reactive, ref, watch } from 'vue'
import { useQuasar } from 'quasar'
const CabeceraPaso = defineComponent({ props: { icono:String, titulo:String, texto:String }, setup: p => () => h('div',{class:'ubi-paso-head'},[h('span',{class:'material-icons ubi-paso-icon'},p.icono),h('div',[h('strong',p.titulo),h('small',p.texto)])]) })
const props=defineProps({modelValue:Boolean,ubicacion:{type:Object,default:null},tipos:{type:Array,default:()=>[]},almacenes:{type:Array,default:()=>[]}})
const $q = useQuasar()
const emit=defineEmits(['update:modelValue','guardar']);const formRef=ref(null);const paso=ref(1);const visible=computed({get:()=>props.modelValue,set:v=>emit('update:modelValue',v)});const modoEdicion=computed(()=>Boolean(props.ubicacion?.id));const base=()=>({id:null,almacen:null,nombre:'',tipo:null,descripcion:''});const form=reactive(base());const almacenes=computed(()=>props.almacenes);const tipos=computed(()=>props.tipos);const requerido=m=>v=>Boolean(v)||m
const resumen=computed(()=>[{etiqueta:'Almacén',valor:form.almacen},{etiqueta:'Nombre',valor:form.nombre},{etiqueta:'Tipo',valor:form.tipo},{etiqueta:'Descripción',valor:form.descripcion}])
watch(()=>props.modelValue,v=>{if(v){paso.value=1;Object.assign(form,base(),props.ubicacion||{});if(props.ubicacion?.idAlmacen) form.almacen=props.ubicacion.idAlmacen}});function cerrar(){visible.value=false;paso.value=1;formRef.value?.resetValidation()}function guardar(){emit('guardar',{...form})}
</script>

<style scoped>
.ubi-dialog-wrap :deep(.q-dialog__inner){padding:18px}.ubi-dialog-card{width:96vw;max-width:1050px;height:90vh;max-height:780px;border-radius:20px;overflow:hidden}.ubi-dialog-accent{height:4px;flex:none;background:linear-gradient(90deg,#4a8c25,#82bd43)}.ubi-dialog-header{flex:none;padding:18px 22px;background:linear-gradient(135deg,#f1f8eb,#e8f3df);border-bottom:1px solid #d7e7ca}.ubi-dialog-icon{width:48px;height:48px;border-radius:14px;display:grid;place-items:center;flex:none;background:#fff;border:1px solid #cce1b8}.ubi-dialog-title{font-size:18px;font-weight:900;color:#263238}.ubi-dialog-sub{font-size:12px;color:#6b7280}.ubi-stepper{box-shadow:none;min-height:0;overflow:hidden}.ubi-stepper :deep(.q-stepper__header){flex:none;background:#fbfcfa;border-bottom:1px solid #dfe7dc;overflow-x:auto;flex-wrap:nowrap}.ubi-stepper :deep(.q-stepper__tab){min-width:145px}.ubi-stepper :deep(.q-stepper__content){height:100%;min-height:0;overflow-x:hidden;overflow-y:auto;scroll-behavior:smooth}.ubi-stepper :deep(.q-stepper__step-inner){padding:28px 34px;min-height:390px}.ubi-stepper :deep(.q-stepper__tab--active){color:#4a8c25}.ubi-paso-head{display:flex;align-items:center;gap:12px;padding:13px 15px;margin-bottom:20px;background:#f3f8ef;border:1px solid #dbe8d3;border-radius:12px}.ubi-paso-icon{font-size:25px;color:#4a8c25}.ubi-paso-head strong,.ubi-paso-head small{display:block}.ubi-paso-head strong{font-size:14px;color:#263238}.ubi-paso-head small{font-size:11px;color:#6b7280;margin-top:2px}.ubi-banner{background:#f3f8ef;color:#61705c;border:1px solid #dbe8d3;font-size:12px}.ubi-summary{display:grid;grid-template-columns:repeat(4,1fr);gap:12px;padding:16px;background:#f7faf5;border:1px solid #dfe7dc;border-radius:12px}.ubi-summary div{padding:10px;background:#fff;border:1px solid #e5ece1;border-radius:9px}.ubi-summary small,.ubi-summary strong{display:block}.ubi-summary small{color:#889284;font-size:11px}.ubi-summary strong{color:#263238;margin-top:4px;overflow-wrap:anywhere}.ubi-dialog-footer{position:sticky;bottom:0;z-index:2;background:#f8faf7;border-top:1px solid #dfe7dc}:deep(.q-field__control){border-radius:10px}
@media(max-width:1023px){.ubi-dialog-card{max-width:760px;height:92vh}.ubi-stepper :deep(.q-stepper__header){overflow:visible;display:block}.ubi-stepper :deep(.q-stepper__tab){min-width:0}.ubi-stepper :deep(.q-stepper__step-inner){padding:20px 24px;min-height:auto}.ubi-summary{grid-template-columns:repeat(2,1fr)}}
@media(max-width:599px){.ubi-dialog-wrap :deep(.q-dialog__inner){padding:0}.ubi-dialog-card{width:100vw;height:100vh;max-width:none;max-height:none;border-radius:0}.ubi-dialog-header{padding:13px 14px}.ubi-dialog-icon{width:42px;height:42px;border-radius:12px}.ubi-dialog-title{font-size:16px}.ubi-dialog-sub{font-size:10.5px;line-height:1.3}.ubi-stepper :deep(.q-stepper__step-inner){padding:16px 14px}.ubi-paso-head{padding:11px 12px;margin-bottom:14px}.ubi-summary{grid-template-columns:1fr;gap:8px;padding:10px}.ubi-dialog-footer{padding:10px!important}.ubi-dialog-footer .q-btn{font-size:12px}}
</style>
