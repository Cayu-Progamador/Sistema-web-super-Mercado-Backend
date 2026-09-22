<template>
  <div class="row q-col-gutter-md">
    <div v-for="card in cards" :key="card.label" class="col-12 col-sm-6 col-lg-3">
      <q-card flat bordered class="summary-card">
        <q-card-section class="row items-center no-wrap">
          <div class="summary-icon" :class="`summary-icon--${card.color}`">
            <q-icon :name="card.icon" size="24px" />
          </div>
          <div class="q-ml-md">
            <div class="summary-label">{{ card.label }}</div>
            <div class="summary-value">{{ card.value }}</div>
          </div>
        </q-card-section>
      </q-card>
    </div>
  </div>
</template>

<script setup>
defineProps({
  cards: { type: Array, required: true }
})
</script>

<style scoped>
.summary-card { border-radius: 14px; background: #fff; }
.summary-icon { width: 48px; height: 48px; border-radius: 12px; display: grid; place-items: center; }
.summary-icon--green { color: #16804b; background: #e4f7ec; }
.summary-icon--red { color: #c0392b; background: #fde9e7; }
.summary-icon--blue { color: #2563a8; background: #e8f1fb; }
.summary-icon--orange { color: #a86300; background: #fff1d9; }
.summary-label { color: #697586; font-size: 12px; }
.summary-value { color: #17212b; font-size: 21px; font-weight: 700; margin-top: 3px; }
</style>
