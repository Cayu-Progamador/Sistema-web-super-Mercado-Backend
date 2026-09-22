import { defineStore } from 'pinia'

export const useVentaStore = defineStore('venta', {
  state: () => ({
    carrito: [],
    ventasEspera: []
  }),

  actions: {
    limpiarCarrito() {
      this.carrito = []
    },
    guardarVentaEnEspera(venta) {
      this.ventasEspera.push(venta)
      this.carrito = []
    },
    quitarVentaEnEspera(codigo) {
      this.ventasEspera = this.ventasEspera.filter((venta) => venta.codigo !== codigo)
    }
  },

  persist: {
    key: 'supermercado-ventas-estado',
    storage: localStorage,
    paths: ['carrito', 'ventasEspera']
  }
})
