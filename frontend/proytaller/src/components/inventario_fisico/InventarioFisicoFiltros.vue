<template>
  <q-card flat bordered class="filter-card q-mb-lg">
    <q-card-section>
      <div class="filter-title"><q-icon name="filter_alt" color="green-8" size="22px" /> Filtros de búsqueda</div>
      <div class="row q-col-gutter-md items-end">
        <div class="col-12 col-md-3"><q-input v-model="local.texto" outlined dense clearable label="Buscar código o responsable" debounce="350" @update:model-value="emitir"><template #prepend><q-icon name="search" /></template></q-input></div>
        <div class="col-12 col-sm-6 col-md-2"><q-select v-model="local.estado" :options="estados" emit-value map-options outlined dense clearable label="Estado" @update:model-value="emitir" /></div>
        <div class="col-12 col-sm-6 col-md-2"><q-select v-model="local.almacen" :options="almacenes" emit-value map-options outlined dense clearable label="Almacén" @update:model-value="emitir" /></div>
        <div class="col-12 col-sm-6 col-md-2"><q-input v-model="local.desde" outlined dense type="date" label="Desde" @update:model-value="emitir" /></div>
        <div class="col-12 col-sm-6 col-md-2"><q-input v-model="local.hasta" outlined dense type="date" label="Hasta" :min="local.desde || undefined" @update:model-value="emitir" /></div>
        <div class="col-12 col-sm-6 col-md-1"><q-btn outline no-caps color="green-8" icon="restart_alt" class="full-width" @click="limpiar"><q-tooltip>Limpiar filtros</q-tooltip></q-btn></div>
      </div>
    </q-card-section>
  </q-card>
</template>
<script setup>
import { reactive } from 'vue'
const emit=defineEmits(['filtrar','limpiar'])
defineProps({ estados:{type:Array,default:()=>[]}, almacenes:{type:Array,default:()=>[]} })
const local=reactive({texto:'',estado:null,almacen:null,desde:'',hasta:''})
function emitir(){emit('filtrar',{...local})}
function limpiar(){Object.assign(local,{texto:'',estado:null,almacen:null,desde:'',hasta:''});emit('limpiar')}
</script>
<style scoped>
.filter-card{border-radius:16px;border-color:#dfe7dc;background:#fff}.filter-title{display:flex;align-items:center;gap:7px;font-size:14px;font-weight:900;margin-bottom:16px;color:#334155}
</style>
