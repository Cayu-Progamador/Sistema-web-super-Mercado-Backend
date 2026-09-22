<template>
  <q-card flat bordered class="movements-card">
    <q-card-section class="row items-center justify-between"><div class="section-title"><q-icon name="swap_vert" /> Movimientos de caja</div><q-btn flat dense color="green-8" icon="refresh" label="Actualizar" @click="$emit('actualizar')" /></q-card-section>
    <q-separator />
    <q-table flat :rows="movimientos" :columns="columns" row-key="id" :pagination="{ rowsPerPage: 6 }" no-data-label="No hay movimientos registrados">
      <template #body-cell-tipo="props"><q-td :props="props"><q-badge :color="props.row.tipo === 'INGRESO' ? 'positive' : 'negative'" :label="props.row.tipo" /></q-td></template>
      <template #body-cell-monto="props"><q-td :props="props" class="text-weight-bold" :class="props.row.tipo === 'INGRESO' ? 'text-positive' : 'text-negative'">{{ props.row.tipo === 'INGRESO' ? '+' : '-' }} Bs {{ Number(props.row.monto).toFixed(2) }}</q-td></template>
    </q-table>
  </q-card>
</template>
<script setup>
defineProps({ movimientos: { type: Array, required: true } })
defineEmits(['actualizar'])
const columns = [{ name: 'fecha', label: 'Fecha', field: 'fecha', align: 'left' }, { name: 'tipo', label: 'Tipo', field: 'tipo', align: 'left' }, { name: 'concepto', label: 'Concepto', field: 'concepto', align: 'left' }, { name: 'metodo', label: 'Método', field: 'metodo', align: 'left' }, { name: 'monto', label: 'Monto', field: 'monto', align: 'right' }, { name: 'usuario', label: 'Usuario', field: 'usuario', align: 'left' }]
</script>
<style scoped>.movements-card{border-radius:15px}.section-title{display:flex;align-items:center;gap:8px;color:#1b5e20;font-size:16px;font-weight:900}</style>
