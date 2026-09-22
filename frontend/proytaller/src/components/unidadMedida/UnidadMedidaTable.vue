<template>
  <div>
    <!-- ===== Vista tabla estilo ERP (oculta, siempre se muestran tarjetas) ===== -->
    <q-table
      v-if="false"
      :rows="rows"
      :columns="columns"
      row-key="id"
      :loading="loading"
      flat
      bordered
      class="cat-card"
    >
      <template v-slot:no-data>
        <div class="q-pa-md text-grey-7">No se encontraron unidades de medida con los filtros aplicados.</div>
      </template>
    </q-table>

    <!-- ===== Tarjetas ===== -->
    <div v-else>
      <div class="row q-col-gutter-md">
        <div v-for="uni in rows" :key="uni.id" class="col-12 col-sm-6">
          <q-card class="cat-card cat-card-item" style="border-radius: 12px;">
            <q-card-section class="q-pa-md">
              <div class="row items-start justify-between q-mb-sm cat-card-header">
                <div class="row items-center cat-card-nombre" style="gap: 12px; min-width: 0;">
                  <q-avatar
                    size="46px"
                    square
                    :style="{ background: colorTipo(uni) + '1A', border: '1px solid ' + colorTipo(uni) + '40', borderRadius: '10px' }"
                  >
                    <q-icon :name="iconoTipo(uni)" :style="{ color: colorTipo(uni) }" size="26px" />
                  </q-avatar>
                  <div style="min-width: 0;">
                    <div class="text-subtitle1 text-weight-bold ellipsis" style="color: #2E7D32;">{{ uni.nombre }}</div>
                  </div>
                </div>
                <span
                  class="categ-chip-estado cat-card-chip"
                  :class="uni.estado === 'activo' ? 'categ-estado-activo' : 'categ-estado-inactivo'"
                >
                  <q-icon :name="uni.estado === 'activo' ? 'check_circle' : 'cancel'" size="11" class="q-mr-xs" />
                  {{ uni.estado === 'activo' ? 'Activo' : 'Inactivo' }}
                </span>
              </div>

              <div class="row q-mt-sm" style="gap: 8px;">
                <div class="col" style="background: #F5F7FA; border-radius: 8px; padding: 8px; text-align: center;">
                  <div class="text-body2 text-weight-bold" style="color: #1B5E20;">{{ uni.tipoNombre }}</div>
                  <div class="text-caption text-weight-medium" style="color: #455A64;">Tipo</div>
                </div>
                <div class="col" style="background: #F5F7FA; border-radius: 8px; padding: 8px; text-align: center;">
                  <div class="text-body2 text-weight-bold" style="color: #1B5E20;">{{ uni.abreviatura }}</div>
                  <div class="text-caption text-weight-medium" style="color: #455A64;">Abreviatura</div>
                </div>
              </div>
            </q-card-section>

            <q-separator />
            <q-card-actions class="q-pa-sm cat-card-acciones">
              <q-btn no-caps outline align="left" class="cat-card-btn col" label="Ver" icon="visibility" color="green-8" @click="emit('ver-detalle', uni)" />
              <q-btn v-permiso="'UNIDAD_MEDIDA_EDITAR'" no-caps outline align="left" class="cat-card-btn col" label="Editar" icon="edit" color="orange-8" @click="emit('editar', uni)" />
              <q-btn v-permiso="'UNIDAD_MEDIDA_EDITAR'"
                no-caps
                outline
                align="left"
                class="cat-card-btn col"
                :label="uni.estado === 'activo' ? 'Desactivar' : 'Activar'"
                :icon="uni.estado === 'activo' ? 'block' : 'check'"
                :color="uni.estado === 'activo' ? 'red-7' : 'green-8'"
                @click="emit('cambiar-estado', uni)"
              />
            </q-card-actions>
          </q-card>
        </div>
        <div v-if="!rows.length" class="col-12">
          <q-card class="cat-card q-pa-lg">
            <div class="text-center text-grey-7">No se encontraron unidades de medida con los filtros aplicados.</div>
          </q-card>
        </div>
      </div>

      <!-- Paginación de tarjetas -->
      <div v-if="totalPaginas > 1" class="row items-center justify-between q-mt-md cat-paginacion">
        <q-pagination
          :model-value="pagina"
          :max="totalPaginas"
          color="green-8"
          boundary-links
          direction-links
          @update:model-value="emit('pagina-cambio', $event)"
        />
        <div class="text-caption" style="color: #5B6675;">
          Mostrando
          <span class="text-weight-bold" style="color: #1B5E20;">{{ cardsInicio }}</span>-
          <span class="text-weight-bold" style="color: #1B5E20;">{{ cardsFin }}</span>
          de <span class="text-weight-bold" style="color: #1B5E20;">{{ total }}</span> unidades
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  rows: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  pagina: {
    type: Number,
    default: 1
  },
  totalPaginas: {
    type: Number,
    default: 1
  },
  total: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['ver-detalle', 'editar', 'cambiar-estado', 'pagina-cambio'])

const cardsInicio = computed(() => {
  return props.total === 0 ? 0 : (props.pagina - 1) * props.rows.length + 1
})

const cardsFin = computed(() => {
  return props.total === 0 ? 0 : Math.min(props.pagina * props.rows.length, props.total)
})

const tipoIconos = { 'Peso': 'scale', 'Volumen': 'water_drop', 'Conteo': 'inventory_2', 'Lineal': 'straighten' }
const tipoColores = { 'Peso': '#2E7D32', 'Volumen': '#1565C0', 'Conteo': '#F57C00', 'Lineal': '#6A1B9A' }

function iconoTipo(uni) {
  return tipoIconos[uni.tipoNombre] || 'straighten'
}

function colorTipo(uni) {
  return tipoColores[uni.tipoNombre] || '#2E7D32'
}

const columns = [
  { name: 'id', label: 'ID', field: 'id', align: 'left', sortable: true },
  { name: 'nombre', label: 'Nombre', field: 'nombre', align: 'left', sortable: true },
  { name: 'abreviatura', label: 'Abreviatura', field: 'abreviatura', align: 'left' },
  { name: 'tipo', label: 'Tipo', field: 'tipoNombre', align: 'left', sortable: true },
  { name: 'estado', label: 'Estado', field: 'estado', align: 'center', sortable: true }
]
</script>

<style scoped src="../../assets/styles/categorias/categorias.css"></style>
<style scoped>
.cat-card-acciones {
  display: flex;
  gap: 10px;
  padding: 12px !important;
}

.cat-card-acciones :deep(.cat-card-btn) {
  min-height: 44px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.2px;
  flex: 1 1 0;
  justify-content: flex-start;
  padding-left: 14px;
}

.cat-card-acciones :deep(.cat-card-btn .q-icon) {
  font-size: 20px;
}

.cat-card-acciones :deep(.cat-card-btn .q-btn__content) {
  gap: 8px;
  width: 100%;
  justify-content: flex-start;
}

.cat-card-header {
  flex-wrap: nowrap;
  gap: 8px;
}

.cat-card-nombre {
  flex: 1 1 auto;
  min-width: 0;
}

.cat-card-chip {
  flex-shrink: 0;
  margin-left: auto;
}

.cat-paginacion {
  background: #ffffff;
  border: 1px solid #e7eef2;
  border-radius: 12px;
  padding: 10px 14px;
  box-shadow: 0 2px 10px rgba(46, 125, 50, 0.05);
}

.cat-paginacion :deep(.q-pagination .q-btn) {
  border-radius: 8px;
  min-height: 32px;
  min-width: 32px;
}

@media (max-width: 900px) {
  .cat-card-acciones {
    flex-wrap: wrap;
  }

  .cat-card-acciones :deep(.cat-card-btn) {
    flex: 1 1 100%;
  }
}

.categ-chip-estado {
  display: inline-flex;
  align-items: center;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
}

.categ-estado-activo {
  background: #E7F6E9;
  color: #1B7A24;
}

.categ-estado-inactivo {
  background: #EFEFEF;
  color: #5B6675;
}
</style>
