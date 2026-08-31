<template><q-card flat bordered class="filtros q-pa-md q-mb-lg">
        <div class="row q-col-gutter-md items-end">
            <div class="col-12 col-md-3">
                <q-input v-model="local.texto" outlined dense clearable label="Buscar transferencia"
                    placeholder="TRF-00001">
                    <template #prepend>
                        <q-icon name="search" color="green-8" />
                    </template>
                </q-input>
            </div>
            <div class="col-6 col-md-2">
                <q-input v-model="local.desde" type="date" outlined dense label="Fecha desde" />
            </div>
            <div class="col-6 col-md-2">
                <q-input v-model="local.hasta" type="date" outlined dense label="Fecha hasta" />
            </div>
            <div class="col-12 col-sm-6 col-md-2">
                <q-select v-model="local.estado" :options="estados" outlined dense clearable label="Estado" emit-value
                    map-options />
            </div>
            <div class="col-12 col-sm-6 col-md-3">
                <q-input v-model="local.responsable" outlined dense clearable label="Responsable"
                    placeholder="Nombre" />
            </div>
            <div class="col-12 row justify-end q-gutter-sm">
                <q-btn flat no-caps color="green-8" icon="restart_alt" label="Limpiar" @click="limpiar" />
                <q-btn unelevated no-caps color="green-8" icon="search" label="Buscar" @click="emitir" />
            </div>
        </div>
    </q-card>
</template>
<script setup>
import { onBeforeUnmount, reactive, watch } from 'vue'
defineProps({ estados: { type: Array, default: () => [] } });
const emit = defineEmits(['filtrar', 'limpiar']);
const local = reactive({ texto: '', desde: '', hasta: '', estado: null, responsable: '' });
let temporizador = null;
function emitir() { emit('filtrar', { ...local, texto: local.responsable || local.texto }) }
function programarBusqueda() {
    clearTimeout(temporizador);
    temporizador = setTimeout(() =>
        emitir(), 350)
}
watch([() => local.texto, () => local.responsable], programarBusqueda);
function limpiar() {
    clearTimeout(temporizador);
    Object.assign(local, { texto: '', desde: '', hasta: '', estado: null, responsable: '' });
    emit('limpiar')
}
onBeforeUnmount(() => clearTimeout(temporizador))
</script>
<style scoped>
.filtros {
    border-color: #e5e7eb !important;
    border-radius: 12px;
    box-shadow: 0 3px 16px rgba(74, 140, 37, .06)
}

:deep(.q-field__control) {
    border: 1.5px solid #4a8c25;
    border-radius: 8px;
    box-shadow: none
}

@media(max-width:599px) {
    .filtros {
        padding: 14px
    }

    .filtros .q-btn {
        min-height: 44px
    }
}
</style>
