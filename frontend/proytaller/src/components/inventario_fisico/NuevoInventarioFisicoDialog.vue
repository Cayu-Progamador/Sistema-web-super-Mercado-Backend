<template>
  <q-dialog :model-value="modelValue" persistent @update:model-value="$emit('update:modelValue',$event)">
    <q-card class="wizard-card">
      <q-card-section class="dialog-head row items-center justify-between"><div><div class="dialog-title">{{ modoEdicion ? 'Editar borrador' : 'Nuevo inventario físico' }}</div><div class="dialog-subtitle">{{ modoEdicion ? `Actualiza la configuración de ${inventario?.codigo || ''}.` : 'Configura el alcance antes de iniciar el conteo.' }}</div></div><q-btn flat round dense icon="close" v-close-popup /></q-card-section>
      <q-stepper v-model="paso" color="green-8" animated flat alternative-labels>
        <q-step :name="1" title="Tipo de inventario" icon="category" :done="paso>1">
          <div class="section-label">Selecciona qué deseas contar</div>
          <div class="row q-col-gutter-md">
            <div v-for="tipo in tipos" :key="tipo.value" class="col-12 col-sm-6 col-md-4">
              <q-card flat bordered class="type-card cursor-pointer" :class="{selected:form.tipo===tipo.value}" @click="form.tipo=tipo.value">
                <q-card-section><q-icon :name="tipo.icon" size="30px" color="green-8"/><div class="type-title">{{ tipo.label }}</div><div class="type-help">{{ tipo.help }}</div><q-radio v-model="form.tipo" :val="tipo.value" color="green-8" /></q-card-section>
              </q-card>
            </div>
          </div>
        </q-step>
        <q-step :name="2" title="Configuración" icon="tune" :done="paso>2">
          <div class="row q-col-gutter-md">
            <div v-if="requiereAlmacen" class="col-12 col-md-6"><q-select v-model="form.almacen" :options="almacenes" emit-value map-options outlined label="Almacén *" :loading="cargandoCatalogos" @update:model-value="cambiarAlmacen" /></div>
            <div v-if="form.tipo==='COMPLETO'" class="col-12">
              <q-banner rounded class="complete-banner"><template #avatar><q-icon name="domain" color="green-8" /></template><b>Alcance general:</b> se incluirán automáticamente todos los productos de todos los almacenes.</q-banner>
            </div>
            <div v-if="form.tipo==='POR_UBICACION'" class="col-12 col-md-6"><q-select v-model="form.ubicacion" :options="ubicaciones" emit-value map-options outlined label="Ubicación *" :loading="cargandoUbicaciones" :disable="!form.almacen" /></div>
            <div v-if="form.tipo==='POR_CATEGORIA'" class="col-12">
              <q-select v-model="form.categoria" :options="categorias" emit-value map-options outlined label="Categoría *" :loading="cargandoCatalogos"><template #prepend><q-icon name="category" color="green-8" /></template></q-select>
              <div class="field-help">Se contarán todos los productos de esta categoría en todos los almacenes.</div>
            </div>
            <div v-if="form.tipo==='PRODUCTOS'" class="col-12">
              <q-select v-model="form.productos" :options="productosFiltrados" emit-value map-options option-label="label" option-value="value" outlined multiple use-chips use-input input-debounce="200" label="Buscar y seleccionar productos *" :loading="cargandoCatalogos" @filter="filtrarProductos">
                <template #prepend><q-icon name="search" color="green-8" /></template>
                <template #option="scope"><q-item v-bind="scope.itemProps"><q-item-section avatar><q-icon name="inventory_2" color="green-7" /></q-item-section><q-item-section><q-item-label>{{ scope.opt }}</q-item-label><q-item-label caption>Disponible para conteo físico</q-item-label></q-item-section></q-item></template>
              </q-select>
              <div class="selection-summary"><q-icon name="check_circle" color="green-8" /> {{ form.productos.length }} producto(s) seleccionado(s)</div>
            </div>
            <div class="col-12 col-md-6"><q-select v-model="form.responsable" :options="responsables" emit-value map-options outlined label="Responsable *" :loading="cargandoCatalogos" /></div>
            <div class="col-12 col-md-6"><q-input v-model="form.fecha" outlined type="date" label="Fecha programada *" /></div>
            <div class="col-12"><q-input v-model="form.observacion" outlined type="textarea" autogrow label="Observación" /></div>
            <div class="col-12"><q-toggle v-model="form.ciego" color="green-8" :disable="!esAdmin" label="Conteo ciego (el responsable no verá el stock del sistema)" /><div class="toggle-help">{{ esAdmin?'Ayuda a obtener un conteo imparcial y reduce errores por referencia.':'Activado obligatoriamente: solo un administrador puede desactivar el conteo ciego.' }}</div></div>
          </div>
        </q-step>
        <q-step :name="3" title="Confirmación" icon="fact_check">
          <q-card flat bordered class="review-card"><q-card-section>
            <div class="section-label">Resumen del inventario</div>
            <div class="review-grid"><div><span>Tipo</span><b>{{ labelTipo }}</b></div><div><span>Alcance</span><b>{{ alcance }}</b></div><div><span>Responsable</span><b>{{ form.responsable }}</b></div><div><span>Fecha</span><b>{{ form.fecha }}</b></div><div><span>Conteo</span><b>{{ form.ciego?'Ciego':'Visible' }}</b></div><div><span>Productos incluidos</span><b>{{ productosIncluidos }}</b></div></div>
          </q-card-section></q-card>
          <q-banner rounded class="warning-banner q-mt-md"><template #avatar><q-icon name="info" color="orange-8" /></template>{{ modoEdicion ? 'Se actualizará únicamente la configuración del borrador. El stock y el Kardex no cambiarán.' : 'El inventario se guardará como borrador. Podrás iniciarlo cuando el equipo esté preparado.' }}</q-banner>
        </q-step>
        <template #navigation>
          <q-stepper-navigation class="row justify-between">
            <q-btn v-if="paso>1" flat no-caps color="grey-7" label="Anterior" @click="paso--" />
            <q-space/><q-btn v-if="paso<3" unelevated no-caps color="green-8" label="Continuar" icon-right="arrow_forward" @click="siguiente" />
            <q-btn v-else unelevated no-caps color="green-8" :label="modoEdicion ? 'Guardar cambios' : 'Crear inventario'" icon="check" @click="crear" />
          </q-stepper-navigation>
        </template>
      </q-stepper>
    </q-card>
  </q-dialog>
</template>
<script setup>
import { computed, reactive, ref, watch } from 'vue';import { useQuasar } from 'quasar'
const props=defineProps({modelValue:Boolean,modoEdicion:Boolean,inventario:Object,esAdmin:Boolean,almacenes:{type:Array,default:()=>[]},ubicaciones:{type:Array,default:()=>[]},categorias:{type:Array,default:()=>[]},productos:{type:Array,default:()=>[]},responsables:{type:Array,default:()=>[]},cargandoCatalogos:Boolean,cargandoUbicaciones:Boolean});const emit=defineEmits(['update:modelValue','crear','actualizar','cargar-ubicaciones']);const $q=useQuasar();const paso=ref(1);const cargandoInicial=ref(false)
const tipos=[{value:'COMPLETO',label:'Inventario completo',icon:'inventory_2',help:'Todos los productos de todos los almacenes.'},{value:'POR_ALMACEN',label:'Por almacén',icon:'warehouse',help:'Todos los productos de un almacén específico.'},{value:'POR_UBICACION',label:'Por ubicación',icon:'place',help:'Una zona, pasillo o estante.'},{value:'POR_CATEGORIA',label:'Por categoría',icon:'category',help:'Productos de una categoría.'},{value:'PRODUCTOS',label:'Productos seleccionados',icon:'checklist',help:'Una lista específica de artículos.'}]
const hoy=new Date().toISOString().slice(0,10);const form=reactive({tipo:'COMPLETO',almacen:null,ubicacion:null,categoria:null,productos:[],responsable:null,fecha:hoy,observacion:'',ciego:true});const labelTipo=computed(()=>tipos.find(x=>x.value===form.tipo)?.label);const requiereAlmacen=computed(()=>['POR_ALMACEN','POR_UBICACION'].includes(form.tipo));const nombreOpcion=(opciones,id)=>opciones.find(x=>x.value===id)?.label||'';const alcance=computed(()=>({COMPLETO:'Todos los productos de todos los almacenes',POR_ALMACEN:nombreOpcion(props.almacenes,form.almacen),POR_UBICACION:`${nombreOpcion(props.almacenes,form.almacen)} · ${nombreOpcion(props.ubicaciones,form.ubicacion)||'Ubicación pendiente'}`,POR_CATEGORIA:form.categoria?`Categoría: ${nombreOpcion(props.categorias,form.categoria)}`:'Categoría pendiente',PRODUCTOS:`${form.productos.length} producto(s) seleccionado(s)`}[form.tipo]));const productosIncluidos=computed(()=>form.tipo==='COMPLETO'?'Todos':form.tipo==='PRODUCTOS'?form.productos.length:'Automáticos según alcance')
function siguiente(){if(paso.value===2){if(!form.responsable)return $q.notify({type:'warning',message:'Selecciona el responsable.'});if(requiereAlmacen.value&&!form.almacen)return $q.notify({type:'warning',message:'Selecciona el almacén.'});if(form.tipo==='POR_UBICACION'&&!form.ubicacion)return $q.notify({type:'warning',message:'Selecciona la ubicación.'});if(form.tipo==='POR_CATEGORIA'&&!form.categoria)return $q.notify({type:'warning',message:'Selecciona la categoría.'});if(form.tipo==='PRODUCTOS'&&!form.productos.length)return $q.notify({type:'warning',message:'Selecciona al menos un producto.'})}paso.value++}
const productosFiltrados=ref([]);function filtrarProductos(valor,actualizar){actualizar(()=>{const texto=valor.toLowerCase().trim();productosFiltrados.value=texto?props.productos.filter(x=>x.label.toLowerCase().includes(texto)):[...props.productos]})}function cambiarAlmacen(id){form.ubicacion=null;if(form.tipo==='POR_UBICACION'&&id)emit('cargar-ubicaciones',id)}
function crear(){emit(props.modoEdicion?'actualizar':'crear',{...form,tipoLabel:labelTipo.value});emit('update:modelValue',false);paso.value=1}
function cargarFormulario(){const i=props.inventario;if(!props.modoEdicion||!i)return;cargandoInicial.value=true;form.tipo=i.tipo||'COMPLETO';form.almacen=i.almacen?.id??null;form.ubicacion=i.ubicacion?.id??null;form.categoria=i.categoria?.id??null;form.productos=[...(i.idsProductosSeleccionados||[])];form.responsable=i.responsable?.id??null;form.fecha=i.fechaProgramada||hoy;form.observacion=i.observacion||'';form.ciego=Boolean(i.conteoCiego);paso.value=1;cargandoInicial.value=false}
watch(()=>props.productos,v=>{productosFiltrados.value=[...v]},{immediate:true});watch(()=>form.tipo,()=>{if(cargandoInicial.value)return;form.almacen=null;form.ubicacion=null;form.categoria=null;form.productos=[]},{flush:'sync'});watch(()=>props.modelValue,abierto=>{if(!abierto)return;if(props.modoEdicion)cargarFormulario();else{cargandoInicial.value=true;Object.assign(form,{tipo:'COMPLETO',almacen:null,ubicacion:null,categoria:null,productos:[],responsable:null,fecha:hoy,observacion:'',ciego:true});paso.value=1;cargandoInicial.value=false}if(!props.esAdmin)form.ciego=true})
</script>
<style scoped>
.wizard-card{width:min(960px,96vw);max-width:960px;border-radius:18px}.dialog-head{padding:20px 24px;border-bottom:1px solid #e7ece5}.dialog-title{font-size:21px;font-weight:900}.dialog-subtitle,.type-help,.toggle-help,.field-help{font-size:12px;color:#768178}.field-help{margin:7px 2px}.section-label{font-size:15px;font-weight:900;margin-bottom:15px}.type-card{height:100%;border-radius:14px;border-color:#dfe7dc;transition:.2s}.type-card:hover,.type-card.selected{border-color:#4a8c25;background:#f4faef;box-shadow:0 3px 14px rgba(74,140,37,.12)}.type-title{font-weight:900;margin-top:9px}.type-help{min-height:34px;margin-top:3px}.complete-banner{background:#eef8e9;color:#34572b;border:1px solid #d9ead1}.selection-summary{display:flex;align-items:center;gap:6px;margin-top:9px;font-size:12px;font-weight:800;color:#4a633e}.review-card{border-radius:14px;border-color:#dfe7dc;background:#f8fbf6}.review-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:18px}.review-grid div{display:flex;flex-direction:column}.review-grid span{font-size:11px;color:#7b857c}.warning-banner{background:#fff8e8;color:#775817}@media(max-width:650px){.review-grid{grid-template-columns:1fr 1fr}}
</style>
