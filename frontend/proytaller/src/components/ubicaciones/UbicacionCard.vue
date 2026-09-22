<template>
  <q-card flat bordered class="ubicacion-card" :class="{ 'ubicacion-card--inactiva': ubicacion.estado === 'INACTIVO' }">
    <div class="ubi-card-line" />
    <q-card-section class="q-pa-lg col">
      <div class="ubi-card-header">
        <div class="ubi-pin">
          <q-icon name="location_on" size="27px" />
        </div>
        <div class="col ellipsis">
          <div class="ubi-name ellipsis">
            {{ ubicacion.nombre }}

          </div>
          <div class="ubi-code">
            {{ ubicacion.codigo }}
          </div>
        </div>
        <q-badge rounded :color="ubicacion.estado === 'ACTIVO' ? 'green-7' : 'grey-6'" :label="ubicacion.estado" />
      </div>
      <div class="ubi-almacen q-mt-md">
        <q-icon name="warehouse" />
        {{ ubicacion.almacen }}
      </div>
      <q-chip dense outline color="green-8" icon="category">
        {{ ubicacion.tipo.replaceAll('_', ' ') }}

      </q-chip>
      <p class="ubi-desc">
        {{ ubicacion.descripcion || 'Sin descripción registrada.' }}
      </p>
      <div class="ubi-metrics">
        <span>
          <q-icon name="inventory_2" />
          {{ ubicacion.productos }}
          productos
        </span>
        <span>
          <q-icon name="stacked_bar_chart" />
          {{ ubicacion.stockTotal }} unidades
        </span>
      </div>
    </q-card-section>
    <q-separator />
    <q-card-actions class="ubi-card-acciones q-px-md q-py-sm">
      <q-btn flat round color="green-8" icon="visibility" aria-label="Ver detalle" @click="$emit('ver', ubicacion)">
        <q-tooltip>Ver detalle de la ubicación</q-tooltip>
      </q-btn>
      <q-btn v-permiso="'UBICACIONES_EDITAR'" flat round color="orange-8" icon="edit" aria-label="Editar" @click="$emit('editar', ubicacion)">
        <q-tooltip>Editar ubicación</q-tooltip>
      </q-btn>
      <q-btn flat round color="green-8" icon="picture_as_pdf" aria-label="Descargar PDF" @click="$emit('pdf', ubicacion)">
        <q-tooltip>Descargar PDF de la ubicación</q-tooltip>
      </q-btn>

      <q-btn flat round :color="ubicacion.estado === 'ACTIVO' ? 'orange-9' : 'green-8'"
        :icon="ubicacion.estado === 'ACTIVO' ? 'block' : 'check_circle'"
        :aria-label="ubicacion.estado === 'ACTIVO' ? 'Inactivar' : 'Activar'" @click="$emit('estado', ubicacion)">
        <q-tooltip>{{ ubicacion.estado === 'ACTIVO' ? 'Inactivar ubicación' :
          'Activar ubicación' }}
        </q-tooltip>
      </q-btn>
    </q-card-actions>
  </q-card>
</template>
<script setup>
const props = defineProps({ ubicacion: { type: Object, required: true } })
defineEmits(['ver', 'editar', 'estado', 'pdf'])
</script>
<style scoped>
.ubicacion-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  border: 1px solid #dfe7dc !important;
  border-radius: 16px;
  background: #fff;
  transition: transform .2s, box-shadow .2s;
  overflow: hidden
}

.ubicacion-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 30px rgba(74, 140, 37, .12);
  border-color: #82bd43 !important
}

.ubicacion-card--inactiva {
  opacity: .82
}

.ubi-card-line {
  height: 4px;
  background: linear-gradient(90deg, #4a8c25, #82bd43)
}

.ubi-card-header {
  display: grid;
  grid-template-columns: 50px minmax(0, 1fr) auto;
  align-items: start;
  gap: 12px
}

.ubi-pin {
  width: 50px;
  height: 50px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  background: #eef7e7;
  color: #4a8c25;
  border: 1px solid #cfe4bc
}

.ubi-name {
  font-size: 17px;
  font-weight: 900;
  color: #263238
}

.ubi-code {
  font-size: 12px;
  font-weight: 700;
  color: #8a9687
}

.ubi-almacen {
  color: #566454;
  font-size: 13px;
  font-weight: 700
}

.ubi-almacen .q-icon,
.ubi-metrics .q-icon {
  color: #4a8c25;
  font-size: 18px
}

.ubi-desc {
  color: #6b7280;
  font-size: 13px;
  line-height: 1.5;
  min-height: 39px
}

.ubi-metrics {
  display: flex;
  gap: 18px;
  color: #657061;
  font-size: 12px
}

.ubi-metrics span {
  display: flex;
  align-items: center;
  gap: 5px
}

.ubi-card-acciones {
  justify-content: flex-start;
  gap: 4px
}

.ubi-card-acciones .q-btn {
  min-width: 40px;
  min-height: 40px
}

@media(max-width:599px) {
  .ubicacion-card:hover {
    transform: none;
    box-shadow: 0 6px 20px rgba(42, 92, 26, .11)
  }

  .ubicacion-card>.q-card__section {
    padding: 16px
  }

  .ubi-card-header {
    grid-template-columns: 44px minmax(0, 1fr) auto;
    gap: 10px
  }

  .ubi-pin {
    width: 44px;
    height: 44px
  }

  .ubi-name {
    font-size: 16px
  }

  .ubi-metrics {
    flex-direction: column;
    gap: 7px
  }

  .ubi-card-acciones {
    justify-content: space-around;
    padding: 8px 12px
  }

  .ubi-card-acciones .q-btn {
    min-width: 48px;
    min-height: 44px
  }
}
</style>
