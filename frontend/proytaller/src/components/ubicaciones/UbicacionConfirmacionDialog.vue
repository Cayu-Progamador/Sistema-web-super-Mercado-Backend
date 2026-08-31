<template>
    <q-dialog v-model="visible" persistent>
        <q-card class="ubi-confirm">
            <div class="ubi-confirm-line" :class="{ activar: esActivar }">

            </div>
            <q-card-section class="ubi-confirm-head row items-center no-wrap">
                <div class="ubi-confirm-icon" :class="{ activar: esActivar }">
                    <q-icon :name="esActivar ? 'check_circle' : 'block'" size="24px" />
                </div>
                <div class="col q-ml-md">
                    <div class="ubi-confirm-title">
                        {{ esActivar ? 'Activar ubicación' : 'Inactivar ubicación' }}
                    </div>
                    <div class="ubi-confirm-sub">Cambio de disponibilidad operativa</div>
                </div>
                <q-btn flat round dense icon="close" @click="visible = false" />
            </q-card-section>
            <q-card-section class="q-pa-lg">
                <div class="ubi-selected">
                    <q-icon name="location_on" color="green-8" size="25px" />
                    <div>
                        <strong>{{ ubicacion?.nombre }}</strong>
                        <span>
                            {{ ubicacion?.codigo }} · {{ ubicacion?.almacen }}
                        </span>
                    </div>
                </div>
                <div class="ubi-warning" :class="{ activar: esActivar }">
                    <q-icon name="warning_amber" size="20px" />
                    <div>
                        <strong>
                            ¿Está seguro de {{ esActivar ? 'activar' : 'inactivar' }} esta ubicación?

                        </strong>
                        <p>
                            {{ esActivar ? 'La ubicación volverá a estar disponible para nuevos movimientos.' : 'La ubicación dejará de estar disponible para nuevos movimientos.' }}
                        </p>
                    </div>
                </div>
                <div class="ubi-confirm-check-row" :class="{ activar: esActivar }" @click="confirmado = !confirmado">
                    <div class="ubi-confirm-check" :class="[{ marcado: confirmado }, { activar: esActivar }]">
                        <q-icon v-if="confirmado" name="check" size="12px" color="white" />
                    </div>
                    <span>Confirmo que deseo {{ esActivar ? 'activar' : 'inactivar' }} esta ubicación.</span>
                </div>
            </q-card-section>
            <q-card-actions class="q-pa-md ubi-confirm-footer">
                <q-btn class="col" outline no-caps color="grey-7" label="Cancelar" @click="cerrar" />
                <q-btn class="col" unelevated no-caps :color="esActivar ? 'green-8' : 'orange-8'"
                    :disable="!confirmado" :label="esActivar ? 'Activar' : 'Inactivar'" @click="confirmar" />
            </q-card-actions>
        </q-card>
    </q-dialog>
</template>
<script setup>
import { computed, ref, watch } from 'vue';
const props = defineProps({ modelValue: Boolean, ubicacion: { type: Object, default: null } });
const emit = defineEmits(['update:modelValue', 'confirmar']);
const visible = computed({ get: () => props.modelValue, set: v => emit('update:modelValue', v) });
const esActivar = computed(() => props.ubicacion?.estado === 'INACTIVO')
const confirmado = ref(false)
watch(() => props.modelValue, abierto => { if (abierto) confirmado.value = false })
function cerrar() { confirmado.value = false; visible.value = false }
function confirmar() { if (!confirmado.value) return; emit('confirmar'); confirmado.value = false }
</script>
<style scoped>
.ubi-confirm {
    width: 94vw;
    max-width: 440px;
    border-radius: 20px;
    overflow: hidden
}

.ubi-confirm-line {
    height: 4px;
    background: #d97b1a
}

.ubi-confirm-line.activar {
    background: #4a8c25
}

.ubi-confirm-head {
    padding: 17px 20px;
    background: #fff8f0;
    border-bottom: 1px solid #f2dcc1
}

.ubi-confirm-icon {
    width: 46px;
    height: 46px;
    border-radius: 13px;
    display: grid;
    place-items: center;
    background: #fef0dd;
    color: #d97b1a
}

.ubi-confirm-icon.activar {
    background: #eaf4d8;
    color: #4a8c25
}

.ubi-confirm-title {
    font-size: 16px;
    font-weight: 900;
    color: #263238
}

.ubi-confirm-sub {
    font-size: 11px;
    color: #7f897c
}

.ubi-selected {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    background: #f7faf5;
    border: 1px solid #dfe7dc;
    border-radius: 12px
}

.ubi-selected strong,
.ubi-selected span {
    display: block
}

.ubi-selected strong {
    color: #263238
}

.ubi-selected span {
    font-size: 11px;
    color: #879184
}

.ubi-warning {
    display: flex;
    gap: 10px;
    margin-top: 14px;
    padding: 12px;
    background: #fff8f0;
    border: 1px solid #f2d1a8;
    border-radius: 10px;
    color: #a25b0d
}

.ubi-warning.activar {
    background: #f1f8e9;
    border-color: #cce1b6;
    color: #4a7b2b
}

.ubi-warning p {
    font-size: 12px;
    margin: 4px 0 0
}

.ubi-confirm-check-row {
    display: flex;
    align-items: center;
    gap: 9px;
    margin-top: 14px;
    padding: 10px 12px;
    border: 1px solid #f2d1a8;
    border-radius: 9px;
    background: #fff8f0;
    color: #a25b0d;
    font-size: 12px;
    font-weight: 700;
    cursor: pointer;
    user-select: none
}

.ubi-confirm-check-row.activar {
    border-color: #cce1b6;
    background: #f1f8e9;
    color: #4a7b2b
}

.ubi-confirm-check {
    width: 18px;
    height: 18px;
    flex: none;
    display: grid;
    place-items: center;
    border: 2px solid #d97b1a;
    border-radius: 5px;
    background: #fff
}

.ubi-confirm-check.activar { border-color: #82bd43 }
.ubi-confirm-check.marcado { border-color: #d97b1a; background: #d97b1a }
.ubi-confirm-check.marcado.activar { border-color: #82bd43; background: #82bd43 }
.ubi-confirm-footer .q-btn--disable { opacity: .5 !important }

.ubi-confirm-footer {
    border-top: 1px solid #dfe7dc;
    gap: 8px
}
</style>
