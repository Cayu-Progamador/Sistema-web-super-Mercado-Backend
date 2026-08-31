<template>
  <div class="row q-col-gutter-md q-mb-lg">
    <div v-for="item in indicadores" :key="item.titulo" class="col-12 col-sm-6 col-md-3">
      <q-card flat bordered class="alm-indicador">
        <q-card-section class="row items-center no-wrap q-pa-md">
          <div class="alm-indicador-icono" :class="item.clase"><q-icon :name="item.icono" size="25px" /></div>
          <div class="q-ml-md">
            <div class="alm-indicador-titulo">{{ item.titulo }}</div>
            <div class="alm-indicador-valor">{{ item.valor }}</div>
            <div class="alm-indicador-ayuda">{{ item.ayuda }}</div>
          </div>
        </q-card-section>
      </q-card>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ total: Number, activos: Number, inactivos: Number, ubicaciones: Number })
const indicadores = computed(() => [
  { titulo: 'Total almacenes', valor: props.total || 0, icono: 'warehouse', clase: 'principal', ayuda: 'Espacios registrados' },
  { titulo: 'Almacenes activos', valor: props.activos || 0, icono: 'check_circle', clase: 'activo', ayuda: 'Disponibles para operar' },
  { titulo: 'Almacenes inactivos', valor: props.inactivos || 0, icono: 'block', clase: 'inactivo', ayuda: 'Fuera de operación' },
  { titulo: 'Total ubicaciones', valor: props.ubicaciones || 0, icono: 'location_on', clase: 'ubicacion', ayuda: 'Ubicaciones asociadas' }
])
</script>

<style scoped>
.alm-indicador { height: 100%; border-radius: 16px; border-color: #dce9d5 !important; box-shadow: 0 4px 18px rgba(42,92,26,.07); transition: .25s ease; }
.alm-indicador:hover { transform: translateY(-3px); box-shadow: 0 10px 28px rgba(42,92,26,.13); }
.alm-indicador-icono { width: 52px; height: 52px; border-radius: 14px; display: grid; place-items: center; color: white; flex: none; }
.principal,.activo { background: linear-gradient(135deg,#82bd43,#4a8c25); }.inactivo { background: #7d8980; }.ubicacion { background: #2f7d6a; }
.alm-indicador-titulo { color: #668060; font-size: 12px; font-weight: 700; }.alm-indicador-valor { color: #2a5c1a; font-size: 27px; line-height: 1.1; font-weight: 900; }.alm-indicador-ayuda { color: #9aaa96; font-size: 11px; margin-top: 3px; }
</style>
