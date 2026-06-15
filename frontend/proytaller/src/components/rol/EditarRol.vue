<template>
  <q-dialog v-model="abierto" persistent>
    <q-card class="dialog-card">

      <!-- HEADER -->
      <div class="dialog-header">
        <div class="header-left">
          <q-icon name="shield" class="header-icon" />
          <div>
            <div class="title">Añadir Rol</div>
            <div class="subtitle">Crea un nuevo rol en el sistema</div>
          </div>
        </div>

        <q-btn icon="close" flat round dense v-close-popup />
      </div>

      <q-separator />

      <!-- FORM -->
      <q-card-section class="q-pt-md">

        <!-- Nombre -->
        <q-input
          v-model="form.nombre"
          label="Nombre del Rol"
          outlined
          dense
          class="q-mb-md"
          placeholder="Ej: ADMINISTRADOR"
        >
          <template v-slot:prepend>
            <q-icon name="person" />
          </template>
        </q-input>

        <!-- Descripción -->
        <q-input
          v-model="form.descripcion"
          type="textarea"
          label="Descripción"
          outlined
          dense
          class="q-mb-md"
          placeholder="Describe las funciones y responsabilidades de este rol..."
        >
          <template v-slot:prepend>
            <q-icon name="description" />
          </template>
        </q-input>

        <!-- Estado -->
        <q-select
          v-model="form.estado"
          :options="estados"
          label="Estado"
          outlined
          dense
        >
          <template v-slot:prepend>
            <q-icon name="verified_user" />
          </template>
        </q-select>

      </q-card-section>

      <!-- FOOTER -->
      <q-card-actions align="right" class="q-pa-md">
        <q-btn flat label="Cancelar" v-close-popup />
        <q-btn
          color="green"
          label="Crear Rol"
          icon="add"
          @click="guardar"
        />
      </q-card-actions>

    </q-card>
  </q-dialog>
</template>

<script setup>
import { reactive } from 'vue'

const abierto = defineModel()
const emit = defineEmits(['guardar'])

const form = reactive({
  nombre: '',
  descripcion: '',
  estado: 'Activo'
})

const estados = ['Activo', 'Inactivo']

function guardar () {
  console.log('Guardar rol:', form)
}
</script>

<style scoped>
.dialog-card {
  width: 500px;
  border-radius: 14px;
  overflow: hidden;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: white;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  font-size: 32px;
  color: #2e7d32;
  background: #e8f5e9;
  padding: 8px;
  border-radius: 10px;
}

.title {
  font-size: 18px;
  font-weight: 600;
}

.subtitle {
  font-size: 12px;
  color: gray;
}
</style>