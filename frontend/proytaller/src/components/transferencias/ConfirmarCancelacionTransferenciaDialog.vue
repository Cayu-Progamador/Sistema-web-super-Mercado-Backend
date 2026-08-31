<template>
  <q-dialog ref="dialogRef" @hide="onDialogHide" persistent>
    <q-card class="cancel-dialog">
      <div class="accent" />
      <q-card-section class="head">
        <q-avatar icon="block" color="orange-1" text-color="orange-9" size="54px" />
        <div class="q-ml-md">
          <div class="title">Cancelar transferencia</div>
          <div class="sub">Esta acción requiere confirmación.</div>
        </div>
      </q-card-section>
      <q-card-section>
        <div class="warn"><q-icon name="warning" size="24px" />
          <p>La transferencia pendiente será cancelada y ya no podrá procesarse.</p>
        </div>
        <div class="confirm" @click="confirmado = !confirmado"><span class="check" :class="{ on: confirmado }"><q-icon
              v-if="confirmado" name="check" size="13px" /></span><span>Entiendo que la transferencia será
            cancelada</span></div>
      </q-card-section>
      <q-card-actions align="right" class="actions"><q-btn flat no-caps color="grey-7" label="Volver"
          @click="cerrar" /><q-btn unelevated no-caps color="negative" label="Cancelar transferencia"
          :disable="!confirmado" @click="confirmar" /></q-card-actions>
    </q-card>
  </q-dialog>
</template>
<script setup>
import { ref } from 'vue'
import { useDialogPluginComponent } from 'quasar'
const emit = defineEmits([...useDialogPluginComponent.emits, 'confirmar'])
const { onDialogHide, onDialogOK, onDialogCancel } = useDialogPluginComponent()
const confirmado = ref(false)
function cerrar() { confirmado.value = false; onDialogCancel() }
function confirmar() { if (!confirmado.value) return; emit('confirmar'); onDialogOK() }
</script>
<style scoped>
.cancel-dialog {
  width: 94vw;
  max-width: 510px;
  border-radius: 18px;
  overflow: hidden;
  font-family: 'Nunito', sans-serif;
  color: #263238
}

.accent {
  height: 5px;
  background: linear-gradient(90deg, #82bd43, #d97b1a)
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

.warn {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  padding: 14px;
  border: 1px solid #f5c97a;
  background: #fef3e2;
  border-radius: 11px;
  color: #a05c10
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
