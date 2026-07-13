<template>
  <div class="logo-container">
    <div class="logo-wrapper">
      <q-img
        class="logo-img"
        src="/logo/logo2.png"
        fit="contain"
        alt="Logo de la Empresa"
      />
      <div class="logo-badge">
        <span class="logo-super">Supermercado</span>
        <span class="logo-name">LA MARTITA</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.logo-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  padding: 0.5rem 0.25rem;
}

.logo-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.6rem;
}

.logo-img {
  width: 80px;
  height: 80px;
  filter: drop-shadow(0 3px 8px rgba(42, 92, 26, 0.25));
}

.logo-badge {
  display: flex;
  flex-direction: column;
  align-items: center;
  line-height: 1.2;
}

.logo-super {
  font-family: 'Nunito', sans-serif;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 2.5px;
  text-transform: uppercase;
  color: #eaf4d8;
  opacity: 0.85;
}

.logo-name {
  font-family: 'Nunito', sans-serif;
  font-size: 20px;
  font-weight: 900;
  letter-spacing: 1.5px;
  color: #ffffff;
  text-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}
</style>
