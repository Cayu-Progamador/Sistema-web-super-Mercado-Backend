<template>
  <q-scroll-area class="fit" :horizontal-thumb-style="{ opacity: 0 }">
    <q-list padding class="sidebar-list">

      <!-- Logo -->
      <div v-show="!isMini" class="q-pa-sm text-center q-mb-sm logo-area">
        <LogoSupermercado />
      </div>

      <q-separator v-show="!isMini" class="sidebar-separator q-mb-sm" />

      <!-- Dashboard -->
      <q-item clickable v-ripple to="/" exact class="sidebar-item" active-class="sidebar-item-active">
        <q-item-section avatar>
          <q-icon name="dashboard" class="sidebar-icon" />
        </q-item-section>
        <q-item-section class="sidebar-label">Dashboard</q-item-section>
      </q-item>

      <q-separator v-show="!isMini" class="sidebar-separator q-my-sm" />

      <div v-show="!isMini" class="sidebar-section-label">Seguridad</div>

      <q-item clickable v-ripple to="/usuarios" class="sidebar-item" active-class="sidebar-item-active">
        <q-item-section avatar>
          <q-icon name="people" class="sidebar-icon" />
        </q-item-section>
        <q-item-section class="sidebar-label">Usuarios</q-item-section>
      </q-item>

      <q-item clickable v-ripple to="/roles" class="sidebar-item" active-class="sidebar-item-active">
        <q-item-section avatar>
          <q-icon name="security" class="sidebar-icon" />
        </q-item-section>
        <q-item-section class="sidebar-label">Roles</q-item-section>
      </q-item>

      <q-item clickable v-ripple to="/perfil" class="sidebar-item" active-class="sidebar-item-active">
        <q-item-section avatar>
          <q-icon name="person" class="sidebar-icon" />
        </q-item-section>
        <q-item-section class="sidebar-label">Perfil</q-item-section>
      </q-item>

      <q-separator v-show="!isMini" class="sidebar-separator q-my-sm" />

      <div v-show="!isMini" class="sidebar-section-label">Personal</div>

      <q-expansion-item v-model="expandido" expand-separator class="sidebar-item sidebar-expansion"
        :class="{ 'sidebar-expansion-activo': expandido }">
        <template v-slot:header>
          <q-item-section avatar>
            <q-icon name="badge" class="sidebar-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Gestion Empleado</q-item-section>
        </template>
        <q-item clickable v-ripple to="/empleado" class="sidebar-sub-item" active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="groups" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Empleados</q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/cargos" class="sidebar-sub-item" active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="work_history" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Cargos</q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/contratos" class="sidebar-sub-item" active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="history_edu" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Contratos</q-item-section>
        </q-item>

        <q-item v-if="tieneAcceso" clickable v-ripple to="/asistencia" class="sidebar-sub-item"
          active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="person_pin" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Mi Asistencia</q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/asistencia/admin" class="sidebar-sub-item" active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="admin_panel_settings" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Panel Asistencia</q-item-section>
        </q-item>

        <q-item v-if="tieneAcceso" clickable v-ripple to="/permisos" class="sidebar-sub-item"
          active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="event_note" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Mis Permisos</q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/permisos-admin" class="sidebar-sub-item" active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="admin_panel_settings" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Admin Permisos</q-item-section>
        </q-item>
      </q-expansion-item>

      <q-separator v-show="!isMini" class="sidebar-separator q-my-sm" />

      <div v-show="!isMini" class="sidebar-section-label">Compras</div>

      <q-expansion-item v-model="expandidoProveedor" expand-separator class="sidebar-item sidebar-expansion"
        :class="{ 'sidebar-expansion-activo': expandidoProveedor }">
        <template v-slot:header>
          <q-item-section avatar>
            <q-icon name="storefront" class="sidebar-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Gestion Proveedor</q-item-section>
        </template>

        <q-item clickable v-ripple to="/proveedores" class="sidebar-sub-item" active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="local_shipping" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Proveedores</q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/pedidos" class="sidebar-sub-item" active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="shopping_cart" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Pedidos</q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/recepcion-mercaderia" class="sidebar-sub-item"
          active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="inventory" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Recepcion Mercaderi­a</q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/devoluciones" class="sidebar-sub-item" active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="assignment_return" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Devoluciones</q-item-section>
        </q-item>
      </q-expansion-item>

      <q-expansion-item v-model="expandidoProducto" expand-separator class="sidebar-item sidebar-expansion"
        :class="{ 'sidebar-expansion-activo': expandidoProducto }">
        <template v-slot:header>
          <q-item-section avatar>
            <q-icon name="inventory_2" class="sidebar-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Gestion de Productos</q-item-section>
        </template>

        <q-item clickable v-ripple to="/productos" class="sidebar-sub-item" active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="category" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Productos</q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/categorias" class="sidebar-sub-item" active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="category" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Categori­as</q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/marcas" class="sidebar-sub-item" active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="label" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Marcas</q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/unidad_medida" class="sidebar-sub-item" active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="straighten" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Unidad Medida</q-item-section>
        </q-item>
      </q-expansion-item>

      <q-separator v-show="!isMini" class="sidebar-separator q-my-sm" />

      <div v-show="!isMini" class="sidebar-section-label">Inventario</div>

      <q-expansion-item v-model="expandidoAlmacen" expand-separator class="sidebar-item sidebar-expansion"
        :class="{ 'sidebar-expansion-activo': expandidoAlmacen }">
        <template v-slot:header>
          <q-item-section avatar>
            <q-icon name="warehouse" class="sidebar-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Gestion Almacen</q-item-section>
        </template>

        <q-item clickable v-ripple to="/almacenes" class="sidebar-sub-item" active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="inventory" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Almacen</q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/ubicaciones-almacen" class="sidebar-sub-item"
          active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="shelves" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Ubicaciones</q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/transferencias-almacen" class="sidebar-sub-item"
          active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="swap_horiz" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Transferencias</q-item-section>
        </q-item>

      </q-expansion-item>

      <q-expansion-item v-model="expandidoInventario" expand-separator class="sidebar-item sidebar-expansion"
        :class="{ 'sidebar-expansion-activo': expandidoInventario }">
        <template v-slot:header>
          <q-item-section avatar>
            <q-icon name="inventory" class="sidebar-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Gestion Inventario</q-item-section>
        </template>

        <q-item clickable v-ripple to="/inventario" class="sidebar-sub-item" active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="inventory_2" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Inventario</q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/inventario-fisico" class="sidebar-sub-item"
          active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="fact_check" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Inventario Fisico</q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/ajustes-inventario" class="sidebar-sub-item"
          active-class="sidebar-item-active">
          <q-item-section avatar>
            <q-icon name="tune" class="sidebar-icon sidebar-sub-icon" />
          </q-item-section>
          <q-item-section class="sidebar-label">Ajustes de Inventario</q-item-section>
        </q-item>

      </q-expansion-item>

      

    </q-list>
  </q-scroll-area>
</template>

<script setup>
import { computed, ref } from 'vue'
import LogoSupermercado from '../../components/logo/LogoSupermercado.vue'
import { useAuthStore } from '../../store/store'

defineProps({
  isMini: {
    type: Boolean,
    default: false
  }
})

const expandido = ref(false)
const expandidoProveedor = ref(false)
const expandidoProducto = ref(false)
const expandidoAlmacen = ref(false)
const expandidoInventario = ref(false)

const store = useAuthStore()
const tieneAcceso = computed(() => store.controlaAsistencia)
</script>

<style scoped src="../../assets/styles/layout/aside.css"></style>

<style>
.sidebar-expansion {
  background: transparent !important;
}

.sidebar-expansion>.q-expansion-item__container {
  border-radius: 10px !important;
}

.sidebar-expansion>.q-expansion-item__container>.q-item {
  border-radius: 10px !important;
  min-height: 44px !important;
}

.sidebar-expansion>.q-expansion-item__container>.q-item:hover {
  background: #f0f7e8 !important;
}

.sidebar-expansion>.q-expansion-item__container>.q-item .sidebar-icon,
.sidebar-expansion>.q-expansion-item__container>.q-item .sidebar-label {
  color: #fff !important;
}

.sidebar-expansion>.q-expansion-item__container>.q-item:hover .sidebar-icon {
  color: #4a8c25 !important;
}

.sidebar-expansion>.q-expansion-item__container>.q-item:hover .sidebar-label {
  color: #2a5c1a !important;
}

.sidebar-expansion>.q-expansion-item__container>.q-item .q-expansion-item__toggle-icon {
  color: #fff !important;
  font-size: 18px !important;
  transition: color 0.18s, transform 0.3s !important;
}

.sidebar-expansion>.q-expansion-item__container>.q-item:hover .q-expansion-item__toggle-icon {
  color: #4a8c25 !important;
}

.sidebar-expansion-activo>.q-expansion-item__container>.q-item .sidebar-icon,
.sidebar-expansion-activo>.q-expansion-item__container>.q-item .sidebar-label,
.sidebar-expansion-activo>.q-expansion-item__container>.q-item .q-expansion-item__toggle-icon {
  color: #4a8c25 !important;
}

.sidebar-expansion-activo>.q-expansion-item__container>.q-item .sidebar-label {
  font-weight: 700 !important;
}

.sidebar-expansion-activo>.q-expansion-item__container>.q-item {
  background: #eaf4d8 !important;
}
</style>
