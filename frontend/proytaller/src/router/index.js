import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../store/store'

import Layout from '../layouts/index.vue'
import Login from '../pages/login/Login.vue'
import Home from "../layouts/modules/MainContent.vue";
import ChangePassword from '../components/auth/nuevoPassword/ChangePassword.vue'
import UsuariosPage from '../pages/usuario/UsuarioPage.vue'
import Perfil from '../pages/perfil/Perfil.vue'
import Roles from '../pages/rol/Roles.vue'
import Empleado from '../pages/empleado/EmpleadoPage.vue'
import Cargos from '../pages/cargo/CargoPage.vue'
import Contrato from '../pages/contrato/ContratoPage.vue'
import MisAsistencia from '../pages/asistencia/MisAsistencia.vue'
import AsistenciaAdmin from '../pages/asistencia/AsistenciaAdmin.vue'
import PermisoPersonal from '../pages/permiso_personal/EmployeePermissionsView.vue'
import PermisoAdmin from '../pages/permiso_personal/AdminPermissionsView.vue'
import Proveedores from '../pages/proveedor/ProveedorPage.vue'
import Pedidos from '../pages/pedido/PedidoPage.vue'
import Recepcion from '../pages/recepcion/RecepcionPage.vue'
import Devoluciones from '../pages/devoluciones/DevolucionesPagina.vue'
import Productos from '../pages/productos/ProductosPage.vue'
import Categorias from '../pages/categorias/CategoriesPage.vue'
import UnidadMedida from '../pages/unidadMedida/UnidadMedidaPage.vue'
import Marca from '../pages/marca/MarcaPage.vue'
import Almacen from '../pages/almacen/AlmacenPage.vue'
import Inventario from '../pages/inventario/InventarioPage.vue'
import InventarioFisico from '../pages/inventario_fisico/InventarioFisicoPage.vue'
import Ubicaciones from '../pages/ubicaciones/UbicacionesPage.vue'
import Transferencias from '../pages/transferencias/TransferenciasPage.vue'
import AjustesInventario from '../pages/ajustes_inventario/AjustesInventarioPage.vue'
const routes = [
  {
    path: '/login',
    name: 'login',
    component: Login
  },

  {
    path: '/',
    component: Layout,
    children: [
      {
        path: '',
        name: 'home',
        component: Home
      },
      
      {
      path: 'change',
      name : 'change',
      component: ChangePassword
      },
      
      {
        path: 'usuarios', 
        name: 'usuarios',
        component: UsuariosPage
      },
      
      {
        path: 'perfil', 
        name: 'perfil',
        component: Perfil
      },
      {
        path: 'roles', 
        name: 'roles',
        component: Roles
      },
      {
        path: 'empleado', 
        name: 'empleado',
        component: Empleado
      },
      {
        path: 'cargos',
        name: 'cargos',
        component: Cargos
      },
      {
        path: 'contratos',
        name: 'contratos',
        component: Contrato
      },
      {
        path: 'asistencia',
        name: 'asistencia',
        component: MisAsistencia
      },
      {
        path: 'asistencia/admin',
        name: 'asistencia-admin',
        component: AsistenciaAdmin
      },
      {
        path: 'permisos',
        name: 'permisos',
        component: PermisoPersonal
      },
      {
        path: 'permisos-admin',
        name: 'permisos-admin',
        component: PermisoAdmin
      },
      {
        path: 'proveedores',
        name: 'proveedores',
        component: Proveedores
      },
      {
        path: 'pedidos',
        name: 'pedidos',
        component: Pedidos
      },
      {
        path: 'recepcion-mercaderia',
        name: 'recepcion-mercaderia',
        component: Recepcion
      },
      {
        path: 'devoluciones',
        name: 'devoluciones',
        component: Devoluciones
      },
      {
        path: 'productos',
        name: 'productos',
        component: Productos
      },
      {
        path: 'categorias',
        name: 'categorias',
        component: Categorias
      },
      {
        path: 'unidad_medida',
        name: 'unidad_medida',
        component: UnidadMedida
      },
      {
        path: 'marcas',
        name: 'marcas',
        component: Marca
      },
      {
        path: 'almacenes',
        name: 'almacenes',
        component: Almacen
      },
      {
        path: 'inventario',
        name: 'inventario',
        component: Inventario
      },
      {
        path: 'inventario-fisico',
        name: 'inventario-fisico',
        component: InventarioFisico
      },
      {
        path: 'ubicaciones-almacen',
        name: 'ubicaciones-almacen',
        component: Ubicaciones
      },
      {
        path: 'transferencias-almacen',
        name: 'transferencias-almacen',
        component: Transferencias
      },
      {
        path: 'ajustes-inventario',
        name: 'ajustes-inventario',
        component: AjustesInventario
      },

    ]
  },

  {
    path: '/:pathMatch(.*)*', redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const store = useAuthStore();

  const token = store.token || JSON.parse(localStorage.getItem('auth'))?.token

  if (token && to.name === 'login') {
    return next({ name: 'home' })
  }

  if (!token && to.name !== 'login') {
    return next({ name: 'login' })
  }

  if (token && to.name === 'asistencia' && !store.controlaAsistencia) {
    return next({ name: 'home' })
  }

  if (token && to.name === 'permisos' && !store.controlaAsistencia) {
    return next({ name: 'home' })
  }

  next()
})


export default router
