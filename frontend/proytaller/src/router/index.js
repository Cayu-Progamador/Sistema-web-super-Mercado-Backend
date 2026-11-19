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
import Clientes from '../pages/cliente/ClientesPage.vue'
import Ventas from '../pages/venta/VentasPage.vue'
import HistorialVentas from '../pages/venta/HistorialVentasPage.vue'
import GestionCredito from '../pages/credito/GestionCreditoPage.vue'
import Caja from '../pages/caja/CajaPage.vue'
import PermisosSistema from '../pages/permisos/PermisosSistemaPage.vue'
import VerificarVenta from '../pages/venta/VerificarVentaPage.vue'
import Configuracion from '../pages/configuracion/ConfiguracionPage.vue'
import Reportes from '../pages/reportes/ReportesPage.vue'
const routes = [
  {
    path: '/verificar-venta/:codigo',
    name: 'verificar-venta',
    component: VerificarVenta
  },
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
        component: Home,
        meta: { permiso: 'DASHBOARD_VER' }
      },
      
      {
      path: 'change',
      name : 'change',
      component: ChangePassword
      },
      
      {
        path: 'usuarios', 
        name: 'usuarios',
        component: UsuariosPage,
        meta: { permiso: 'USUARIOS_VER' }
      },
      
      {
        path: 'perfil', 
        name: 'perfil',
        component: Perfil,
        meta: { permiso: 'PERFIL_VER' }
      },
      {
        path: 'roles', 
        name: 'roles',
        component: Roles,
        meta: { permiso: 'ROLES_VER' }
      },
      {
        path: 'empleado', 
        name: 'empleado',
        component: Empleado,
        meta: { permiso: 'EMPLEADOS_VER' }
      },
      {
        path: 'cargos',
        name: 'cargos',
        component: Cargos,
        meta: { permiso: 'CARGOS_VER' }
      },
      {
        path: 'contratos',
        name: 'contratos',
        component: Contrato,
        meta: { permiso: 'CONTRATOS_VER' }
      },
      {
        path: 'asistencia',
        name: 'asistencia',
        component: MisAsistencia
      },
      {
        path: 'asistencia/admin',
        name: 'asistencia-admin',
        component: AsistenciaAdmin,
        meta: { permiso: 'PANEL_ASISTENCIA_VER' }
      },
      {
        path: 'permisos',
        name: 'permisos',
        component: PermisoPersonal
      },
      {
        path: 'permisos-admin',
        name: 'permisos-admin',
        component: PermisoAdmin,
        meta: { permiso: 'ADMIN_PERMISOS_VER' }
      },
      {
        path: 'proveedores',
        name: 'proveedores',
        component: Proveedores,
        meta: { permiso: 'PROVEEDORES_VER' }
      },
      {
        path: 'clientes',
        name: 'clientes',
        component: Clientes,
        meta: { permiso: 'CLIENTES_VER' }
      },
      {
        path: 'ventas',
        name: 'ventas',
        component: Ventas,
        meta: { permisos: ['NUEVA_VENTA_VER', 'NUEVA_VENTA_CREAR'] }
      },
      {
        path: 'historial-ventas',
        name: 'historial-ventas',
        component: HistorialVentas,
        meta: { permiso: 'HISTORIAL_VENTAS_VER' }
      },
      {
        path: 'gestion-credito',
        name: 'gestion-credito',
        component: GestionCredito,
        meta: { permiso: 'CREDITO_VER' }
      },
      {
        path: 'caja',
        name: 'caja',
        component: Caja,
        meta: { permiso: 'CAJA_VER' }
      },
      {
        path: 'permisos-sistema',
        name: 'permisos-sistema',
        component: PermisosSistema,
        meta: { permiso: 'PERMISOS_SISTEMA_VER' }
      },
      {
        path: 'configuracion/:seccion(datos|recuperacion)?',
        name: 'configuracion',
        component: Configuracion,
        meta: { permiso: 'CONFIGURACION_VER' }
      },
      {
        path: 'reportes',
        name: 'reportes',
        component: Reportes
      },
      {
        path: 'pedidos',
        name: 'pedidos',
        component: Pedidos,
        meta: { permiso: 'PEDIDOS_VER' }
      },
      {
        path: 'recepcion-mercaderia',
        name: 'recepcion-mercaderia',
        component: Recepcion,
        meta: { permiso: 'RECEPCION_VER' }
      },
      {
        path: 'devoluciones',
        name: 'devoluciones',
        component: Devoluciones,
        meta: { permiso: 'DEVOLUCIONES_VER' }
      },
      {
        path: 'productos',
        name: 'productos',
        component: Productos,
        meta: { permiso: 'PRODUCTOS_VER' }
      },
      {
        path: 'categorias',
        name: 'categorias',
        component: Categorias,
        meta: { permiso: 'CATEGORIAS_VER' }
      },
      {
        path: 'unidad_medida',
        name: 'unidad_medida',
        component: UnidadMedida,
        meta: { permiso: 'UNIDAD_MEDIDA_VER' }
      },
      {
        path: 'marcas',
        name: 'marcas',
        component: Marca,
        meta: { permiso: 'MARCAS_VER' }
      },
      {
        path: 'almacenes',
        name: 'almacenes',
        component: Almacen,
        meta: { permiso: 'ALMACEN_VER' }
      },
      {
        path: 'inventario',
        name: 'inventario',
        component: Inventario,
        meta: { permiso: 'INVENTARIO_VER' }
      },
      {
        path: 'inventario-fisico',
        name: 'inventario-fisico',
        component: InventarioFisico,
        meta: { permiso: 'INVENTARIO_FISICO_VER' }
      },
      {
        path: 'ubicaciones-almacen',
        name: 'ubicaciones-almacen',
        component: Ubicaciones,
        meta: { permiso: 'UBICACIONES_VER' }
      },
      {
        path: 'transferencias-almacen',
        name: 'transferencias-almacen',
        component: Transferencias,
        meta: { permiso: 'TRANSFERENCIAS_VER' }
      },
      {
        path: 'ajustes-inventario',
        name: 'ajustes-inventario',
        component: AjustesInventario,
        meta: { permiso: 'AJUSTES_INVENTARIO_VER' }
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

function primerDestinoPermitido(store) {
  const ruta = router.getRoutes().find(item => item.path !== '/' && (
    (item.meta.permiso && store.tienePermiso(item.meta.permiso)) ||
    (item.meta.permisos && item.meta.permisos.some(codigo => store.tienePermiso(codigo)))
  ))
  if (ruta) return ruta.path
  if (store.controlaAsistencia) return '/asistencia'
  return '/'
}

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

  if (token && to.meta.permiso && !store.tienePermiso(to.meta.permiso)) {
    const destino = primerDestinoPermitido(store)
    return destino === to.path ? next() : next(destino)
  }

  if (token && to.meta.permisos && !to.meta.permisos.some(codigo => store.tienePermiso(codigo))) {
    const destino = primerDestinoPermitido(store)
    return destino === to.path ? next() : next(destino)
  }

  next()
})


export default router
