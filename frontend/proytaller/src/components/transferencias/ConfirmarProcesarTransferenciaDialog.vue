<template><q-dialog ref="dialogRef" @hide="onDialogHide" persistent><q-card class="process-dialog">
            <div class="accent" /><q-card-section class="head"><q-avatar icon="play_arrow" color="orange-1"
                    text-color="orange-9" size="54px" />
                <div class="q-ml-md">
                    <div class="title">Procesar transferencia</div>
                    <div class="sub">Confirma el inicio del traslado.</div>
                </div>
            </q-card-section><q-card-section>
                <div class="info"><q-icon name="info" size="24px" />
                    <p>La transferencia cambiará de <b>PENDIENTE</b> a <b>EN PROCESO</b> y quedará lista para
                        completarse.</p>
                </div>
                <div class="confirm" @click="confirmado = !confirmado"><span class="check"
                        :class="{ on: confirmado }"><q-icon v-if="confirmado" name="check"
                            size="13px" /></span><span>Confirmo que deseo procesar esta transferencia</span></div>
            </q-card-section><q-card-actions align="right" class="actions"><q-btn flat no-caps color="grey-7"
                    label="Volver" @click="onDialogCancel" /><q-btn unelevated no-caps color="orange-8"
                    label="Procesar transferencia" :disable="!confirmado" @click="confirmar" /></q-card-actions>
        </q-card></q-dialog></template>
<script setup>
import { ref } from 'vue'; 
import { useDialogPluginComponent } from 'quasar'
defineEmits([...useDialogPluginComponent.emits]); 
const {  onDialogHide, onDialogOK, onDialogCancel } = useDialogPluginComponent(); 
const confirmado = ref(false); 
function confirmar() { if (confirmado.value) onDialogOK() }
</script>
<style
    scoped>
    .process-dialog {
        width: 94vw;
        max-width: 510px;
        border-radius: 18px;
        overflow: hidden;
        font-family: 'Nunito', sans-serif;
        color: #263238
    }

    .accent {
        height: 5px;
        background: linear-gradient(90deg, #f6c56b, #ef8d19)
    }

    .head {
        display: flex;
        align-items: center;
        padding: 22px;
        background: #fff8f0;
        border-bottom: 1px solid #f5dbb8
    }

    .title {
        font-size: 20px;
        font-weight: 900
    }

    .sub {
        font-size: 13px;
        color: #8b8176;
        margin-top: 3px
    }

    .info {
        display: flex;
        gap: 12px;
        align-items: flex-start;
        padding: 14px;
        border: 1px solid #f5c97a;
        background: #fff8e8;
        border-radius: 11px;
        color: #9a6510
    }

    .info p {
        margin: 0;
        font-size: 13px;
        font-weight: 700;
        line-height: 1.45
    }

    .confirm {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-top: 18px;
        padding: 13px;
        border: 1px solid #f5c97a;
        background: #fffaf3;
        border-radius: 10px;
        font-size: 13px;
        font-weight: 800;
        cursor: pointer
    }

    .check {
        width: 19px;
        height: 19px;
        border: 2px solid #d97b1a;
        border-radius: 5px;
        display: grid;
        place-items: center;
        color: #fff
    }

    .check.on {
        background: #d97b1a
    }

    .actions {
        border-top: 1px solid #eee5da;
        background: #fffdf9;
        padding: 14px
    }
</style>
