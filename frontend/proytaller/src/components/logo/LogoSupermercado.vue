<template>
  <div class="logo-container">
    <q-img 
      class="logo-img" 
      src="/logo/logo2.png" 
      fit="contain" 
      alt="Logo de la Empresa" 
    />
    <div class="title">Supermercado</div>
    <div class="title">LA MARTITA</div>
  </div>
</template>

<style scoped>
.title {
    position: relative;
    bottom: 24px;
    font-size: 15px;
    color: var(--text_color);
    font-weight: bold;
    margin-bottom: 10px;
}
.logo-container {
    /* Reduje el padding al mínimo para que la imagen ocupe casi todo el espacio */
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    transition: all 0.3s ease; 
    width: 100%;
  /* AUMENTAMOS LA ALTURA AQUÍ (Antes era 60px) */
}

.logo-img {
  width: 100%;
  height: 100%;
  transition: all 0.3s ease;
  transform: scale(1.7);
}

</style>