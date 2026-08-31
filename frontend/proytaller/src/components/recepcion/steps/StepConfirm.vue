<template>
  <div class="column items-center text-center q-py-xl">
    <div class="rp-confirm-ic">
      <q-icon name="check_circle" />
    </div>
    <div class="rp-confirm-title">¿Confirmar recepción?</div>
    <div class="rp-confirm-sub">
      La recepción será registrada y la mercadería aceptada quedará disponible para
      distribución entre almacén y sala de ventas.
    </div>

    <div class="row justify-center q-gutter-md q-mt-lg">
      <q-card flat bordered class="rp-detalle-card q-pa-md" style="width: 220px">
        <div class="rp-mini-label">Productos aceptados</div>
        <div class="rp-mini-valor text-center">{{ items.length }}</div>
      </q-card>
      <q-card flat bordered class="rp-detalle-card q-pa-md" style="width: 220px">
        <div class="rp-mini-label">Total aceptado</div>
        <div class="rp-mini-valor text-center">{{ formatNum(totalAceptado) }}</div>
      </q-card>
      <q-card flat bordered class="rp-detalle-card q-pa-md" style="width: 220px">
        <div class="rp-mini-label">Total general</div>
        <div class="rp-mini-valor text-center">Bs {{ formatNum(totalGeneral) }}</div>
      </q-card>
    </div>

    <q-btn
      class="rp-btn rp-btn-primario q-mt-xl"
      style="min-width: 280px; height: 48px !important; font-size: 15px !important"
      label="Confirmar recepción"
      icon="check_circle"
      no-caps
      :loading="cargando"
      @click="$emit('confirmar')"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  items: { type: Array, default: () => [] },
  cargando: { type: Boolean, default: false }
})

defineEmits(['confirmar'])

const totalAceptado = computed(() => props.items.reduce((s, r) => s + (r.aceptado || 0), 0))
const totalGeneral = computed(() => props.items.reduce((s, r) => s + (r.subtotal || 0), 0))

function formatNum(n) {
  return Number(n || 0).toLocaleString('es-BO')
}
</script>

<style scoped src="../../../assets/styles/recepcion/recepcion.css"></style>