<template>
  <div class="row q-col-gutter-md q-mb-lg">
    <div v-for="item in items" :key="item.label" class="col-12 col-sm-6 col-lg-3">
      <q-card flat bordered class="summary-card">
        <q-card-section class="row items-center no-wrap q-pa-md">
          <div class="summary-icon" :class="item.tone"><q-icon :name="item.icon" size="26px" /></div>
          <div class="q-ml-md">
            <div class="summary-label">{{ item.label }}</div>
            <div class="summary-value">{{ item.value }}</div>
            <div class="summary-help">{{ item.help }}</div>
          </div>
        </q-card-section>
      </q-card>
    </div>
  </div>
</template>

<script setup>
defineProps({ items: { type: Array, default: () => [] } })
</script>

<style scoped>
.summary-card{height:100%;border-radius:16px;border-color:#dfe7dc;box-shadow:0 4px 18px rgba(74,140,37,.07)}
.summary-icon{width:52px;height:52px;border-radius:14px;display:grid;place-items:center;color:#fff;background:linear-gradient(135deg,#82bd43,#4a8c25)}
.summary-icon.warning{background:linear-gradient(135deg,#ffca63,#e89a19)}.summary-icon.danger{background:linear-gradient(135deg,#ff8a80,#d94a41)}.summary-icon.info{background:linear-gradient(135deg,#69b9d6,#317d9c)}
.summary-label{font-size:12px;font-weight:700;color:#6b7280}.summary-value{font-size:27px;line-height:1.1;font-weight:900}.summary-help{font-size:11px;color:#9aa39a;margin-top:3px}
</style>
