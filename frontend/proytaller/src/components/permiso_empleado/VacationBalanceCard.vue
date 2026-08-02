<template>
  <q-card class="balance-card">
    <div class="balance-inner">
      <div class="balance-top">
        <div class="balance-info">
          <div class="balance-label">{{ titulo }}</div>
          <div class="balance-numbers">
            <span class="balance-disponible">{{ disponible }}</span>
            <span class="balance-separator">/</span>
            <span class="balance-total">{{ total }}</span>
            <span class="balance-unit">días</span>
          </div>
        </div>
        <div class="balance-icon">
          <q-icon name="beach_access" size="36px" />
        </div>
      </div>
      <q-linear-progress
        :value="progreso"
        color="white"
        class="q-mt-sm"
        size="8px"
        rounded
      />
      <div class="balance-footer">
        <span class="balance-usados">{{ usadoTexto }}</span>
        <span class="balance-porcentaje">{{ porcentajeTexto }}</span>
      </div>
      <div class="balance-mini-indicators row q-gutter-x-sm q-mt-sm">
        <div class="mini-chip mini-pendiente">
          <span class="mini-num">{{ pendientes }}</span>
          <span class="mini-label">Pendientes</span>
        </div>
        <div class="mini-chip mini-aprobado">
          <span class="mini-num">{{ aprobadas }}</span>
          <span class="mini-label">Aprobadas</span>
        </div>
        <div class="mini-chip mini-rechazado">
          <span class="mini-num">{{ rechazadas }}</span>
          <span class="mini-label">Rechazadas</span>
        </div>
      </div>
    </div>
  </q-card>
</template>

<script setup>
defineProps({
  titulo: { type: String, default: 'Saldo de vacaciones' },
  disponible: { type: Number, default: 0 },
  total: { type: Number, default: 0 },
  usado: { type: Number, default: 0 },
  progreso: { type: Number, default: 0 },
  usadoTexto: { type: String, default: '0 usados' },
  porcentajeTexto: { type: String, default: '100% disponible' },
  pendientes: { type: Number, default: 0 },
  aprobadas: { type: Number, default: 0 },
  rechazadas: { type: Number, default: 0 }
})
</script>

<style scoped>
.balance-card {
  border-radius: 16px !important;
  overflow: hidden !important;
  background: transparent !important;
  box-shadow: none !important;
  border: none !important;
}
.balance-inner {
  background: linear-gradient(135deg, #2E7D32 0%, #388E3C 50%, #43A047 100%);
  border-radius: 16px;
  padding: 22px 24px 18px;
  color: #fff;
}
.balance-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.balance-label {
  font-size: 13px;
  font-weight: 600;
  opacity: 0.85;
  letter-spacing: 0.3px;
  margin-bottom: 6px;
}
.balance-numbers {
  display: flex;
  align-items: baseline;
  gap: 3px;
}
.balance-disponible {
  font-size: 40px;
  font-weight: 800;
  line-height: 1;
}
.balance-separator {
  font-size: 24px;
  font-weight: 300;
  opacity: 0.7;
  margin: 0 2px;
}
.balance-total {
  font-size: 24px;
  font-weight: 600;
  opacity: 0.85;
}
.balance-unit {
  font-size: 13px;
  font-weight: 500;
  opacity: 0.75;
  margin-left: 4px;
  align-self: flex-end;
  padding-bottom: 4px;
}
.balance-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.balance-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 4px;
  font-size: 12px;
  font-weight: 500;
  opacity: 0.8;
}
.mini-chip {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 6px 4px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.13);
}
.mini-num {
  font-size: 16px;
  font-weight: 700;
}
.mini-label {
  font-size: 10px;
  font-weight: 500;
  opacity: 0.8;
}
</style>
