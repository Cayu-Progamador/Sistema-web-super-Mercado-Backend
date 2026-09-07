<template>
  <q-card flat bordered class="filters"><q-card-section>
      <div class="filter-title"><q-icon name="tune" color="green-8" /> Filtros de búsqueda</div>
      <div class="row q-col-gutter-md items-end">
        <div class="col-12 col-md-4"><q-input v-model="local.buscar" outlined dense clearable
            label="Buscar código o producto" @update:model-value="emitir"><template #prepend><q-icon
                name="search" /></template></q-input>
        </div>
        <div class="col-12 col-sm-6 col-md-2"><q-select v-model="local.tipo" outlined dense clearable emit-value
            map-options :options="tipos" label="Tipo" @update:model-value="emitir" /></div>
        <div class="col-12 col-sm-6 col-md-2"><q-select v-model="local.estado" outlined dense clearable emit-value
            map-options :options="estados" label="Estado" @update:model-value="emitir" /></div>
        <div class="col-12 col-sm-6 col-md-2"><q-input v-model="local.desde" outlined dense type="date" label="Desde"
            @update:model-value="emitir" /></div>
        <div class="col-12 col-sm-6 col-md-2"><q-input v-model="local.hasta" outlined dense type="date" label="Hasta"
            :min="local.desde || undefined" @update:model-value="emitir" /></div>
        <div class="col-12"><q-btn outline no-caps color="green-8" icon="restart_alt" label="Limpiar filtros"
            @click="limpiar" /></div>
      </div>
    </q-card-section></q-card>
</template>
<script setup>
import { reactive } from 'vue'
const emit = defineEmits(['filtrar', 'limpiar']); const tipos = [{ label: 'Entrada', value: 'ENTRADA' }, { label: 'Salida', value: 'SALIDA' }]; const estados = [{ label: 'Borrador', value: 'BORRADOR' }, { label: 'Pendiente aprobación', value: 'PENDIENTE_APROBACION' }, { label: 'Aprobado', value: 'APROBADO' }, { label: 'Aplicado', value: 'APLICADO' }, { label: 'Rechazado', value: 'RECHAZADO' }, { label: 'Cancelado', value: 'CANCELADO' }]; const local = reactive({ buscar: '', tipo: null, estado: null, desde: '', hasta: '' }); function emitir() { emit('filtrar', { ...local }) } function limpiar() { Object.assign(local, { buscar: '', tipo: null, estado: null, desde: '', hasta: '' }); emit('limpiar') }
</script>
<style
  scoped>
  .filters {
    border-radius: 16px;
    border-color: #dfe7dc;
    background: #fff;
    margin-bottom: 22px
  }

  .filter-title {
    display: flex;
    align-items: center;
    gap: 7px;
    font-size: 14px;
    font-weight: 900;
    color: #334155;
    margin-bottom: 16px
  }
</style>
