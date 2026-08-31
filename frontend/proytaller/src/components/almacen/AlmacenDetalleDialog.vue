<template>
  <q-dialog v-model="visible">
    <q-card class="alm-detalle-card">
      <div class="alm-detalle-header">
        <div class="row items-start no-wrap">
          <div class="alm-detalle-icono"><q-icon name="warehouse" size="30px" /></div>
          <div class="col q-ml-md"><div class="alm-detalle-nombre">{{ almacen?.nombre }}</div><div class="alm-detalle-codigo">{{ almacen?.codigo }}</div><div class="q-mt-sm"><q-badge :color="almacen?.estado === 'ACTIVO' ? 'green-7' : 'grey-6'" :label="almacen?.estado" /><q-chip dense color="white" text-color="green-9" class="q-ml-sm">{{ textoTipo(almacen?.tipo) }}</q-chip></div></div>
          <div class="row items-center no-wrap q-gutter-xs">
            <q-btn flat round icon="picture_as_pdf" color="red-7" aria-label="Descargar inventario en PDF" @click="$emit('pdf', almacen)">
              <q-tooltip>Descargar PDF</q-tooltip>
            </q-btn>
            <q-btn flat round dense icon="close" color="green-9" aria-label="Cerrar detalle" @click="visible=false" />
          </div>
        </div>
      </div>

      <q-card-section class="q-pa-lg scroll alm-detalle-contenido">
        <div class="alm-seccion-titulo">Información general</div>
        <div class="alm-info-grid">
          <div><span>Responsable</span><strong>{{ almacen?.responsable || 'Sin asignar' }}</strong></div><div><span>Teléfono</span><strong>{{ almacen?.telefono || 'Sin registrar' }}</strong></div><div><span>Dirección</span><strong>{{ almacen?.direccion || 'Sin registrar' }}</strong></div><div><span>Fecha de creación</span><strong>{{ almacen?.fechaCreacion }}</strong></div>
        </div>
        <div class="alm-descripcion q-mt-md">{{ almacen?.descripcion || 'Sin descripción registrada.' }}</div>

        <div class="alm-seccion-titulo q-mt-lg">Resumen operativo</div>
        <div class="row q-col-gutter-sm">
          <div v-for="dato in resumen" :key="dato.label" class="col-12 col-sm-4"><div class="alm-resumen"><q-icon :name="dato.icon" /><strong>{{ dato.valor }}</strong><span>{{ dato.label }}</span></div></div>
        </div>

        <div class="row items-center justify-between q-mt-lg q-mb-sm"><div class="alm-seccion-titulo q-mb-none">Ubicaciones asociadas</div><q-btn flat no-caps color="green-8" icon-right="arrow_forward" label="Ver todas las ubicaciones" @click="$emit('ver-ubicaciones', almacen)" /></div>
        <q-list bordered separator class="alm-ubicaciones-lista">
          <q-item v-for="ubicacion in ubicacionesEjemplo" :key="ubicacion.codigo"><q-item-section avatar><q-icon name="place" color="green-7" /></q-item-section><q-item-section><q-item-label class="text-weight-bold">{{ ubicacion.codigo }}</q-item-label><q-item-label caption>{{ ubicacion.nombre }}</q-item-label></q-item-section></q-item>
        </q-list>
      </q-card-section>
      <q-card-actions align="right" class="q-pa-md alm-detalle-footer"><q-btn flat no-caps color="grey-7" label="Cerrar" @click="visible=false" /></q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ modelValue: Boolean, almacen: { type: Object, default: null } })
const emit = defineEmits(['update:modelValue', 'ver-ubicaciones', 'pdf'])
const visible = computed({ get:()=>props.modelValue, set:v=>emit('update:modelValue',v) })
const resumen = computed(() => [{label:'Ubicaciones',valor:props.almacen?.ubicaciones||0,icon:'location_on'},{label:'Productos',valor:props.almacen?.productos||0,icon:'inventory_2'},{label:'Stock total',valor:`${props.almacen?.stockTotal||0}`,icon:'stacked_bar_chart'}])
const ubicacionesEjemplo = computed(() => (props.almacen?.ubicacionesDetalle?.length ? props.almacen.ubicacionesDetalle : [{codigo:'A-01',nombre:'Alimentos secos'},{codigo:'A-02',nombre:'Arroz y cereales'},{codigo:'B-01',nombre:'Bebidas'},{codigo:'C-01',nombre:'Productos de limpieza'}]).slice(0,4))
function textoTipo(tipo){return{PRINCIPAL:'Principal',SECUNDARIO:'Secundario',CAMARA_FRIA:'Cámara Fría',DEPOSITO:'Depósito'}[tipo]||tipo}
</script>

<style scoped>
.alm-detalle-card{width:94vw;max-width:900px;border-radius:20px;overflow:hidden}.alm-detalle-header{padding:20px 24px;background:linear-gradient(135deg,#edf7e5,#dcefc9);border-top:4px solid #4a8c25}.alm-detalle-icono{width:58px;height:58px;border-radius:16px;display:grid;place-items:center;background:#4a8c25;color:#fff}.alm-detalle-nombre{font-size:21px;font-weight:900;color:#2a5c1a}.alm-detalle-codigo{font-size:12px;font-weight:700;color:#71945b}.alm-detalle-contenido{max-height:70vh}.alm-seccion-titulo{color:#2a5c1a;font-size:14px;font-weight:900;margin-bottom:12px}.alm-info-grid{display:grid;grid-template-columns:1fr 1fr;gap:12px}.alm-info-grid div{padding:11px 13px;background:#f8faf6;border:1px solid #e3ebde;border-radius:10px}.alm-info-grid span,.alm-resumen span{display:block;font-size:11px;color:#889784}.alm-info-grid strong{display:block;color:#435340;font-size:13px;margin-top:3px}.alm-descripcion{padding:13px;background:#f4f8f1;border-left:3px solid #82bd43;border-radius:8px;color:#5c6d58}.alm-resumen{text-align:center;padding:14px 8px;background:#f7faf5;border:1px solid #dfeadc;border-radius:12px}.alm-resumen .q-icon{display:block;color:#4a8c25;font-size:22px;margin:auto}.alm-resumen strong{display:block;color:#2a5c1a;font-size:19px}.alm-ubicaciones-lista{border-radius:12px;border-color:#dfeadc}.alm-detalle-footer{border-top:1px solid #e4ece0;background:#fafcf9}@media(max-width:600px){.alm-info-grid{grid-template-columns:1fr}.alm-detalle-card{width:100vw;max-width:none;border-radius:0}}
</style>
