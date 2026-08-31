<template>
  <q-dialog v-model="visible"><q-card class="ubi-detail-card">
      <div class="ubi-detail-head">
        <div class="row items-start no-wrap">
          <div class="ubi-detail-pin"><q-icon name="location_on" size="31px" /></div>
          <div class="col q-ml-md">
            <div class="ubi-detail-name">{{ ubicacion?.nombre }}</div>
            <div class="ubi-detail-code">{{ ubicacion?.codigo }} · {{ ubicacion?.almacen }}</div>
            <div class="q-mt-sm"><q-badge :color="ubicacion?.estado === 'ACTIVO' ? 'green-7' : 'grey-6'"
                :label="ubicacion?.estado" /><q-chip dense color="white" text-color="green-9" class="q-ml-sm">{{
                  ubicacion?.tipo }}</q-chip></div>
          </div><q-btn flat round dense icon="picture_as_pdf" color="green-9" aria-label="Descargar PDF" @click="emit('pdf', ubicacion)" /><q-btn flat round dense icon="close" color="green-9" @click="visible = false" />
        </div>
      </div>
      <q-card-section class="ubi-detail-body scroll">
        <div class="ubi-detail-title">Información general</div>
        <div class="ubi-info">
          <div v-for="dato in informacion" :key="dato.label"><small>{{ dato.label }}</small><strong>{{ dato.valor || '-'
          }}</strong></div>
        </div>
        <div class="ubi-description q-mt-md">{{ ubicacion?.descripcion || 'Sin descripción registrada.' }}</div>
        <div class="ubi-detail-title q-mt-lg">Resumen de la ubicación</div>
        <div class="row q-col-gutter-sm">
          <div v-for="dato in resumen" :key="dato.label" class="col-6 col-sm-3">
            <div class="ubi-mini"><q-icon :name="dato.icon" /><strong>{{ dato.valor }}</strong><small>{{ dato.label
            }}</small></div>
          </div>
        </div>
        <div class="ubi-detail-title q-mt-lg">Productos almacenados</div><q-table
          :rows="ubicacion?.productosDetalle || []" :columns="columnas" row-key="lote" flat bordered dense hide-bottom
          :pagination="{ rowsPerPage: 0 }" class="ubi-products"><template #body-cell-estado="p"><q-td
              :props="p"><q-badge :color="getColorEstadoProducto(p.value)"
                :label="p.value.replaceAll('_', ' ')" /></q-td></template></q-table><q-banner rounded
          class="ubi-info-banner q-mt-md"><template #avatar><q-icon name="info" color="green-8" /></template>Las
          cantidades se modifican desde Recepciones, Transferencias, Movimientos o Ajustes.</q-banner>
      </q-card-section><q-card-actions align="right" class="q-pa-md ubi-detail-footer"><q-btn flat no-caps
          color="grey-7" label="Cerrar" @click="visible = false" /></q-card-actions>
    </q-card></q-dialog>
</template>
<script setup>
import { computed } from 'vue'
const props = defineProps({ modelValue: Boolean, ubicacion: { type: Object, default: null } });

const emit = defineEmits(['update:modelValue', 'pdf']);
const visible = computed({ get: () => props.modelValue, set: v => emit('update:modelValue', v) });
const informacion = computed(() => [{ label: 'Almacén', valor: props.ubicacion?.almacen }]); const resumen = computed(() => [{ label: 'Productos diferentes', valor: props.ubicacion?.productos || 0, icon: 'inventory_2' }, { label: 'Stock total', valor: props.ubicacion?.stockTotal || 0, icon: 'stacked_bar_chart' }]); const columnas = [{ name: 'producto', label: 'Producto', field: 'producto', align: 'left' }, { name: 'lote', label: 'Lote', field: 'lote', align: 'center' }, { name: 'cantidad', label: 'Cantidad', field: 'cantidad', align: 'center' }, { name: 'unidad', label: 'Unidad', field: 'unidad', align: 'center' }, { name: 'vencimiento', label: 'Vencimiento', field: 'vencimiento', align: 'center' }, { name: 'estado', label: 'Estado', field: 'estado', align: 'center' }];
function getColorEstadoProducto(e) { return { DISPONIBLE: 'green-7', STOCK_BAJO: 'amber-8', PROXIMO_VENCER: 'orange-8', VENCIDO: 'red-8' }[e] || 'grey-6' }
</script>
<style scoped>
.ubi-detail-card {
  width: 96vw;
  max-width: 980px;
  border-radius: 20px;
  overflow: hidden
}

.ubi-detail-head {
  padding: 20px 24px;
  background: linear-gradient(135deg, #edf7e5, #dcefc9);
  border-top: 4px solid #4a8c25
}

.ubi-detail-pin {
  width: 58px;
  height: 58px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  background: #4a8c25;
  color: #fff
}

.ubi-detail-name {
  font-size: 21px;
  font-weight: 900;
  color: #263238
}

.ubi-detail-code {
  font-size: 12px;
  color: #67805f;
  font-weight: 700
}

.ubi-detail-body {
  max-height: 72vh
}

.ubi-detail-title {
  font-size: 14px;
  font-weight: 900;
  color: #4a8c25;
  margin-bottom: 12px
}

.ubi-info {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px
}

.ubi-info div {
  padding: 11px 13px;
  border-radius: 10px;
  background: #f7faf5;
  border: 1px solid #dfe7dc
}

.ubi-info small,
.ubi-info strong {
  display: block
}

.ubi-info small,
.ubi-mini small {
  color: #879184
}

.ubi-info strong {
  color: #263238;
  margin-top: 3px
}

.ubi-description {
  padding: 13px;
  color: #5d685a;
  background: #f4f8f1;
  border-left: 3px solid #82bd43;
  border-radius: 8px
}

.ubi-mini {
  text-align: center;
  padding: 14px 7px;
  border-radius: 12px;
  background: #f7faf5;
  border: 1px solid #dfe7dc
}

.ubi-mini .q-icon {
  display: block;
  margin: auto;
  color: #4a8c25;
  font-size: 22px
}

.ubi-mini strong,
.ubi-mini small {
  display: block
}

.ubi-mini strong {
  font-size: 19px;
  color: #263238
}

.ubi-products {
  border-radius: 12px
}

.ubi-info-banner {
  background: #f3f8ef;
  color: #62705e;
  border: 1px solid #dae7d2;
  font-size: 12px
}

.ubi-detail-footer {
  border-top: 1px solid #dfe7dc
}

@media(max-width:650px) {
  .ubi-detail-card {
    width: 100vw;
    max-width: none;
    border-radius: 0
  }

  .ubi-info {
    grid-template-columns: 1fr 1fr
  }
}
</style>
