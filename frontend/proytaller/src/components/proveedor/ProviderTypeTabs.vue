<template>
  <q-tabs v-model="tabActiva" class="pv-tabs" active-color="green-8" indicator-color="green-8" dense>
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
  { key: 'todos', label: 'Todos', count: props.total, color: '#43A047' },
  { key: 'empresa', label: 'Proveedor Empresa', count: props.empresas, color: '#66BB6A' },
  { key: 'persona', label: 'Proveedor Persona', count: props.personas, color: '#81C784' }
])
</script>

<style scoped>
.pv-tabs {
  background: #E8F5E9;
  border: 1.5px solid #c8dfda;
  border-radius: 12px;
  padding: 0 6px;
  box-shadow: 0 2px 10px rgba(0, 96, 81, 0.08);
  min-height: 44px;
}

.pv-tab {
  font-family: 'Nunito', sans-serif;
  font-weight: 800;
  font-size: 13px;
  color: #0f6e56;
  min-height: 42px;
  border-radius: 8px;
  margin: 4px 2px;
}

.pv-tab.q-tab--active {
  background: #ffffff;
  color: #006051;
  box-shadow: 0 2px 8px rgba(0, 96, 81, 0.15);
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
  border-radius: 8px;
  background: #F1F8E9 !important;
  color: #2E7D32 !important;
  border: 1px solid #dcf0df;
}
</style>
