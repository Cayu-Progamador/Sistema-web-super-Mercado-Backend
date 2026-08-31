<template><q-dialog ref="dialogRef" @hide="onDialogHide" persistent><q-card class="delete-dialog">
            <div class="accent" /><q-card-section class="head"><q-avatar icon="delete_forever" color="red-1"
                    text-color="negative" size="54px" />
                <div class="q-ml-md">
                    <div class="title">Eliminar borrador</div>
                    <div class="sub">Esta acción es permanente.</div>
                </div>
            </q-card-section><q-card-section>
                <div class="warn"><q-icon name="warning" size="24px" />
                    <p>El borrador y todos sus productos serán eliminados definitivamente. Ya no podrás continuarlo ni
                        recuperarlo.</p>
                </div>
                <div class="confirm" @click="confirmado = !confirmado"><span class="check"
                        :class="{ on: confirmado }"><q-icon v-if="confirmado" name="check"
                            size="13px" /></span><span>Entiendo que deseo eliminar este borrador</span></div>
            </q-card-section><q-card-actions align="right" class="actions"><q-btn flat no-caps color="grey-7"
                    label="Volver" @click="onDialogCancel" /><q-btn unelevated no-caps color="negative"
                    label="Eliminar borrador" :disable="!confirmado" @click="confirmar" /></q-card-actions>
        </q-card></q-dialog></template>
<script setup>
import { ref } from 'vue'
import { useDialogPluginComponent } from 'quasar'
defineEmits([...useDialogPluginComponent.emits])
const {  onDialogHide, onDialogOK, onDialogCancel } = useDialogPluginComponent(); const confirmado = ref(false)
function confirmar() { if (confirmado.value) onDialogOK() }
</script>
<style
    scoped>
    .delete-dialog {
        width: 94vw;
        max-width: 510px;
        border-radius: 18px;
        overflow: hidden;
        font-family: 'Nunito', sans-serif;
        color: #263238
    }

    .accent {
        height: 5px;
        background: linear-gradient(90deg, #ef9a9a, #c62828)
    }

    .head {
        display: flex;
        align-items: center;
        padding: 22px;
        background: #fff5f5;
        border-bottom: 1px solid #f2cccc
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

    .warn {
        display: flex;
        gap: 12px;
        align-items: flex-start;
        padding: 14px;
        border: 1px solid #efb4b4;
        background: #fff1f1;
        border-radius: 11px;
        color: #b42323
    }

    .warn p {
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
        border: 1px solid #efb4b4;
        background: #fff8f8;
        border-radius: 10px;
        font-size: 13px;
        font-weight: 800;
        cursor: pointer
    }

    .check {
        width: 19px;
        height: 19px;
        border: 2px solid #c62828;
        border-radius: 5px;
        display: grid;
        place-items: center;
        color: #fff
    }

    .check.on {
        background: #c62828
    }

    .actions {
        border-top: 1px solid #eee5da;
        background: #fffdf9;
        padding: 14px
    }
</style>
