<template>
  <q-tabs v-model="tabActiva" class="pv-tabs" active-color="primary" indicator-color="primary" dense>
    <q-tab v-for="tab in tabs" :key="tab.key" :name="tab.key" no-caps class="pv-tab">
      <div class="pv-tab-inner">
        <span>{{ tab.label }}</span>
        <q-badge :color="tab.color" class="pv-tab-badge" rounded>
          {{ tab.count }}
        </q-badge>
      </div>
    </q-tab>
  </q-tabs>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: { type: String, default: 'todos' },
  total: { type: Number, default: 0 },
  empresas: { type: Number, default: 0 },
  personas: { type: Number, default: 0 }
})

const emit = defineEmits(['update:modelValue'])

const tabActiva = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const tabs = computed(() => [
  { key: 'todos', label: 'Todos', count: props.total, color: '#2E7D32' },
  { key: 'empresa', label: 'Proveedor Empresa', count: props.empresas, color: '#1976D2' },
  { key: 'persona', label: 'Proveedor Persona', count: props.personas, color: '#7B1FA2' }
])
</script>

<style scoped>
.pv-tabs {
  background: #ffffff;
  border: 1px solid #e6ebf1;
  border-radius: 12px;
  padding: 0 6px;
  box-shadow: 0 1px 3px rgba(16, 24, 40, 0.04);
  min-height: 44px;
}

.pv-tab {
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 13px;
  color: #5a6a60;
  min-height: 42px;
}

.pv-tab-inner {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.pv-tab-badge {
  font-size: 10px;
  font-weight: 800;
  min-width: 20px;
  height: 18px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0 6px;
}
</style>
