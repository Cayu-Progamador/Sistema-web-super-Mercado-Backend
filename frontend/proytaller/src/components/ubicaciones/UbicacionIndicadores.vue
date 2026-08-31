<template>
  <div class="row q-col-gutter-md q-mb-lg">
    <div v-for="item in indicadores" :key="item.titulo" class="col-12 col-sm-6 col-md-3">
      <q-card flat bordered class="ubi-stat"><q-card-section class="row items-center no-wrap q-pa-md"><div class="ubi-stat-icon" :class="item.clase"><q-icon :name="item.icono" size="25px" /></div><div class="q-ml-md"><div class="ubi-stat-title">{{ item.titulo }}</div><div class="ubi-stat-value">{{ item.valor }}</div><div class="ubi-stat-help">{{ item.ayuda }}</div></div></q-card-section></q-card>
    </div>
  </div>
</template>
<script setup>
import { computed } from 'vue'
const props=defineProps({total:Number,activas:Number,inactivas:Number,ocupadas:Number})
const indicadores=computed(()=>[
  {titulo:'Total ubicaciones',valor:props.total||0,icono:'location_on',clase:'total',ayuda:'Espacios registrados'},
  {titulo:'Ubicaciones activas',valor:props.activas||0,icono:'check_circle',clase:'activa',ayuda:'Disponibles para operar'},
  {titulo:'Ubicaciones inactivas',valor:props.inactivas||0,icono:'block',clase:'inactiva',ayuda:'Fuera de operación'},
  {titulo:'Ubicaciones ocupadas',valor:props.ocupadas||0,icono:'inventory_2',clase:'ocupada',ayuda:'Con productos almacenados'}
])
</script>
<style scoped>
.ubi-stat{height:100%;border-radius:16px;border-color:#dfe7dc!important;box-shadow:0 4px 18px rgba(74,140,37,.07);transition:.25s}.ubi-stat:hover{transform:translateY(-3px);box-shadow:0 10px 28px rgba(74,140,37,.13)}.ubi-stat-icon{width:52px;height:52px;border-radius:14px;display:grid;place-items:center;color:#fff}.total,.activa{background:linear-gradient(135deg,#82bd43,#4a8c25)}.inactiva{background:#7d8980}.ocupada{background:#357c69}.ubi-stat-title{font-size:12px;font-weight:700;color:#6b7280}.ubi-stat-value{font-size:27px;line-height:1.1;font-weight:900;color:#263238}.ubi-stat-help{font-size:11px;color:#9aa39a;margin-top:3px}
</style>
