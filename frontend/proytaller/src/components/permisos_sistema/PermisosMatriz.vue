<template>
  <q-card flat bordered class="matrix-card">
    <q-card-section class="matrix-toolbar row items-center">
      <div><div class="section-title"><q-icon name="admin_panel_settings" /> Matriz de permisos</div><div class="text-caption text-grey-6">Marca las acciones permitidas para el rol {{ usuario?.rol }}.</div></div>
      <q-space /><q-input v-model="filtro" outlined dense clearable label="Buscar módulo" class="filter-input"><template #prepend><q-icon name="search" /></template></q-input><q-btn v-permiso="'PERMISOS_SISTEMA_EDITAR'" unelevated no-caps color="green-8" icon="save" label="Guardar cambios" :loading="cargando" class="save-btn" @click="confirmarGuardar" />
    </q-card-section>
    <q-separator />
    <div class="matrix-legend"><span><b class="legend-check">☐</b> Disponible</span><span><b class="legend-check">☑</b> Asignado</span><span><b class="legend-dash">—</b> No aplica</span></div>
    <q-card-section class="table-wrap">
      <table class="permissions-table"><thead><tr><th class="name-column">Módulo / submódulo</th><th v-for="accion in acciones" :key="accion.key">{{ accion.label }}</th></tr></thead><tbody>
        <template v-for="grupo in gruposVisibles" :key="grupo.idMenu">
          <tr class="module-row"><td class="module-cell"><q-icon :name="grupo.icono || 'folder'" color="green-8" /><strong>{{ grupo.nombre }}</strong></td><td v-for="accion in acciones" :key="accion.key" class="check-cell"> <q-checkbox v-if="permisoDeAccion(grupo, accion.key)" v-model="seleccionados" :val="permisoDeAccion(grupo, accion.key).idPermiso" color="green-8" dense /><span v-else>—</span></td></tr>
          <tr v-for="menu in grupo.hijos" :key="menu.idMenu" class="submodule-row"><td class="submodule-cell"><q-icon :name="menu.icono || 'chevron_right'" color="grey-6" /><span>{{ menu.nombre }}</span></td><td v-for="accion in acciones" :key="accion.key" class="check-cell"><q-checkbox v-if="permisoDeAccion(menu, accion.key)" v-model="seleccionados" :val="permisoDeAccion(menu, accion.key).idPermiso" color="green-8" dense /><span v-else>—</span></td></tr>
        </template>
      </tbody></table>
      <div v-if="!gruposVisibles.length" class="empty-permissions">No hay módulos o permisos disponibles.</div>
    </q-card-section>
    <q-card-section class="matrix-footer"><q-icon name="info" color="grey-6" /><span>Los cambios se aplican al rol seleccionado y afectan a todos sus usuarios.</span></q-card-section>
  </q-card>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useQuasar } from 'quasar'

const props = defineProps({ usuario: { type: Object, required: true }, menus: { type: Array, default: () => [] }, permisos: { type: Array, required: true }, permisosSeleccionados: { type: Array, default: () => [] }, cargando: Boolean })
const emit = defineEmits(['guardar'])
const $q = useQuasar()
const filtro = ref('')
const seleccionados = ref([...props.permisosSeleccionados])
const acciones = [{ key: 'VER', label: 'Ver' }, { key: 'CREAR', label: 'Crear' }, { key: 'EDITAR', label: 'Editar' }, { key: 'APROBAR', label: 'Aprobar' }]
const rutasControladas = new Set(['/asistencia', '/permisos'])
const prefijos = { '/': 'DASHBOARD', '/clientes': 'CLIENTES', '/ventas': 'NUEVA_VENTA', '/historial-ventas': 'HISTORIAL_VENTAS', '/gestion-credito': 'CREDITO', '/caja': 'CAJA', '/usuarios': 'USUARIOS', '/roles': 'ROLES', '/perfil': 'PERFIL', '/permisos-sistema': 'PERMISOS_SISTEMA', '/empleado': 'EMPLEADOS', '/cargos': 'CARGOS', '/contratos': 'CONTRATOS', '/asistencia/admin': 'PANEL_ASISTENCIA', '/permisos-admin': 'ADMIN_PERMISOS', '/proveedores': 'PROVEEDORES', '/pedidos': 'PEDIDOS', '/recepcion-mercaderia': 'RECEPCION', '/devoluciones': 'DEVOLUCIONES', '/productos': 'PRODUCTOS', '/categorias': 'CATEGORIAS', '/marcas': 'MARCAS', '/unidad_medida': 'UNIDAD_MEDIDA', '/almacenes': 'ALMACEN', '/ubicaciones-almacen': 'UBICACIONES', '/transferencias-almacen': 'TRANSFERENCIAS', '/inventario': 'INVENTARIO', '/inventario-fisico': 'INVENTARIO_FISICO', '/ajustes-inventario': 'AJUSTES_INVENTARIO' }
const soloVer = new Set(['VER'])
const verCrear = new Set(['VER', 'CREAR'])
const verEditar = new Set(['VER', 'EDITAR'])
const verCrearEditar = new Set(['VER', 'CREAR', 'EDITAR'])
const todas = new Set(['VER', 'CREAR', 'EDITAR', 'APROBAR'])
const accionesAplicables = {
  '/': soloVer,
  '/clientes': verCrearEditar,
  '/ventas': verCrear,
  '/historial-ventas': verEditar,
  '/gestion-credito': verEditar,
  '/caja': verCrearEditar,
  '/usuarios': verCrearEditar,
  '/roles': verCrearEditar,
  '/perfil': verEditar,
  '/permisos-sistema': verEditar,
  '/empleado': verCrearEditar,
  '/cargos': verCrearEditar,
  '/contratos': verCrearEditar,
  '/asistencia/admin': verCrearEditar,
  '/permisos-admin': new Set(['VER', 'EDITAR', 'APROBAR']),
  '/proveedores': verCrearEditar,
  '/pedidos': verCrearEditar,
  '/recepcion-mercaderia': verCrearEditar,
  '/devoluciones': verCrearEditar,
  '/productos': verCrearEditar,
  '/categorias': verCrearEditar,
  '/marcas': verCrearEditar,
  '/unidad_medida': verCrearEditar,
  '/almacenes': verCrearEditar,
  '/ubicaciones-almacen': verCrearEditar,
  '/transferencias-almacen': todas,
  '/inventario': verEditar,
  '/inventario-fisico': todas,
  '/ajustes-inventario': todas
}
watch(() => props.permisosSeleccionados, value => { seleccionados.value = [...value] })

const menusFuente = computed(() => props.menus.length ? props.menus : (props.usuario.menus || []))
const gruposVisibles = computed(() => {
  const texto = filtro.value.toLowerCase().trim()
  const disponibles = menusFuente.value.filter(m => !rutasControladas.has(m.ruta))
  const padres = disponibles.filter(m => !m.idMenuPadre)
  return padres.map(padre => ({ ...padre, hijos: disponibles.filter(m => m.idMenuPadre === padre.idMenu) })).map(grupo => ({ ...grupo, hijos: grupo.hijos.filter(hijo => !texto || `${grupo.nombre} ${hijo.nombre}`.toLowerCase().includes(texto)), visible: !texto || `${grupo.nombre} ${grupo.ruta}`.toLowerCase().includes(texto) || grupo.hijos.length > 0 })).filter(grupo => grupo.visible)
})
function accionAplica(menu, accion) { return !accionesAplicables[menu.ruta] || accionesAplicables[menu.ruta].has(accion) }
function permisoDeAccion(menu, accion) { if (!accionAplica(menu, accion)) return null; const codigo = `${prefijos[menu.ruta] || ''}_${accion}`; return (menu.permisos || []).find(p => p.codigo === codigo && props.permisos.some(disponible => disponible.idPermiso === p.idPermiso)) }
function confirmarGuardar() {
  const idsValidos = new Set(menusFuente.value.flatMap(menu => (menu.permisos || []).filter(p => {
    const prefijo = prefijos[menu.ruta]
    return prefijo && acciones.some(accion => accionAplica(menu, accion.key) && p.codigo === `${prefijo}_${accion.key}`)
  }).map(p => p.idPermiso)))
  const permisosLimpios = seleccionados.value.filter(id => idsValidos.has(id))
  $q.dialog({ title: 'Confirmar cambios', message: `¿Guardar los permisos del rol ${props.usuario?.rol}?`, cancel: true, persistent: true }).onOk(() => emit('guardar', permisosLimpios))
}
</script>

<style scoped>
.matrix-card{border-radius:18px;overflow:hidden}.matrix-toolbar{gap:12px;padding:18px 22px;background:#f7fbf7}.section-title{display:flex;align-items:center;gap:8px;color:#1b5e20;font-size:19px;font-weight:900}.filter-input{width:220px}.save-btn{border-radius:9px}.table-wrap{padding:0;overflow:auto}.permissions-table{width:100%;min-width:760px;border-collapse:collapse}.permissions-table th{height:48px;padding:0 12px;background:#eef7ef;color:#55705d;font-size:11px;font-weight:900;text-align:center;text-transform:uppercase;letter-spacing:.04em}.permissions-table th:first-child{text-align:left}.name-column{width:42%}.permissions-table td{height:54px;border-top:1px solid #edf2ee}.module-row td{background:#f7fbf7;border-top:1px solid #dcebdd}.module-cell{display:flex;align-items:center;gap:10px;padding-left:18px;color:#1b5e20;font-size:14px}.empty-cell{text-align:center;color:#b7c3ba}.submodule-cell{display:flex;align-items:center;gap:10px;padding-left:40px;color:#34483a;font-size:13px}.check-cell{text-align:center}.check-cell .q-checkbox{display:inline-flex}.matrix-footer{display:flex;align-items:center;gap:7px;color:#87958d;font-size:11px;background:#fbfdfb}.empty-permissions{padding:35px;text-align:center;color:#87958d}@media(max-width:700px){.matrix-toolbar{align-items:flex-start}.filter-input{width:100%}.save-btn{width:100%}}
.matrix-legend{display:flex;gap:20px;flex-wrap:wrap;padding:10px 22px;background:#fff;color:#68786d;font-size:12px;border-bottom:1px solid #edf2ee}.matrix-legend span{display:flex;align-items:center;gap:5px}.legend-check{color:#2e7d32;font-size:17px}.legend-dash{color:#9aa79d;font-size:17px}
</style>
