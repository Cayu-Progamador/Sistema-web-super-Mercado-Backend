<template><q-page class="pages">
    <div class="container">
      <header class="inventory-header">
        <div>
          <div class="title-row"><q-icon name="fact_check" color="green-8" size="33px" />
            <h1>Inventario físico</h1>
          </div>
          <p>Planifica conteos, controla diferencias y aprueba ajustes de existencias.</p>
        </div><q-btn v-permiso="'INVENTARIO_FISICO_CREAR'" unelevated no-caps color="green-8" icon="add" label="Nuevo inventario" @click="abrirNuevo" />
      </header>
      <InventarioFisicoResumen :items="resumen" />
      <InventarioFisicoFiltros :estados="estados" :almacenes="almacenes" @filtrar="filtrar" @limpiar="limpiar" />
      <div class="row items-center justify-between q-mb-md">
        <div class="list-title">Inventarios registrados <q-badge color="green-8" class="q-ml-xs">{{ total }}</q-badge>
        </div>
      </div>
      <InventarioFisicoTabla :rows="inventarios" :loading="cargando" :pagination="pagination" @request="cargar"
        @ver="verDetalle" @editar="editarBorrador" @eliminar="confirmarEliminar" @contar="abrirConteo"
        @revisar="abrirRevision" @pdf="pdf" />
    </div>
    <NuevoInventarioFisicoDialog v-model="nuevo" :modo-edicion="Boolean(editando)" :inventario="editando"
      :es-admin="esAdmin" :almacenes="almacenes" :ubicaciones="ubicaciones" :categorias="categorias"
      :productos="productos" :responsables="responsables" :cargando-catalogos="cargandoCatalogos"
      :cargando-ubicaciones="cargandoUbicaciones" @cargar-ubicaciones="cargarUbicaciones" @crear="crear"
      @actualizar="actualizarBorrador" />
    <ConteoInventarioDialog v-model="conteo" :inventario="seleccionado" @guardar="cargar"
      @finalizar="conteoFinalizado" />
    <RevisionInventarioDialog v-model="revision" :inventario="seleccionado" @aprobado="revisionTerminada"
      @cancelado="revisionTerminada" />
    <DetalleInventarioFisicoDialog v-model="detalle" :inventario="seleccionado" @pdf="pdf" />
    <q-dialog v-model="dialogEliminar" persistent><q-card class="delete-card">
        <div class="accent-bar"></div>
        <div class="dialog-header">
          <div class="header-left">
            <div class="warn-icon"><q-icon name="delete_outline" size="22px" /></div>
            <div>
              <div class="delete-title">Eliminar borrador</div>
              <div class="dialog-sub">Esta acción eliminará el inventario definitivamente</div>
            </div>
          </div><q-btn flat round dense icon="close" class="close-btn" @click="dialogEliminar = false" />
        </div><q-card-section class="dialog-body">
          <div class="draft-card">
            <div class="draft-avatar"><q-icon name="inventory_2" size="21px" /></div>
            <div>
              <div class="draft-name">{{ filaAccion?.codigo || '--' }}</div>
              <div class="draft-type">Inventario físico en estado BORRADOR</div>
            </div>
          </div>
          <div class="warn-box"><q-icon name="warning_amber" size="18px" />
            <p>Al eliminar este borrador, <strong>se borrarán su configuración y productos seleccionados</strong>. No se
              modificará el stock ni el Kardex.</p>
          </div>
          <div class="confirm-row" @click="confirmadoEliminar = !confirmadoEliminar">
            <div class="confirm-check" :class="{ checked: confirmadoEliminar }"><q-icon v-if="confirmadoEliminar"
                name="check" size="12px" color="white" /></div><span>Entiendo que esta acción no se puede
              deshacer</span>
          </div>
        </q-card-section>
        <div class="dialog-footer"><button class="btn-cancel" @click="dialogEliminar = false"><q-icon name="close"
              size="15px" />Cancelar</button><button class="btn-delete" :disabled="!confirmadoEliminar"
            @click="eliminarBorrador"><q-icon name="delete_outline" size="16px" />Eliminar borrador</button></div>
      </q-card></q-dialog>
  </q-page></template>

<script setup>
import { actualizarInventarioFisico, descargarActaInventarioFisico, eliminarInventarioFisico } from '../../api/inventarioFisico/inventarioFisico'
const confirmadoEliminar = ref(false)
const editando = ref(null)
import { computed, onMounted, ref } from 'vue'; import { useQuasar } from 'quasar'; import { listarAlmacenes } from '../../api/almacen/almacen'; import { listarCategoriasActivas } from '../../api/categoria/categoria'; import { listarEmpleados } from '../../api/empleado/empleado'; import { crearInventarioFisico, iniciarInventarioFisico, listarInventariosFisicos, obtenerInventarioFisico } from '../../api/inventarioFisico/inventarioFisico'; import { listarProductos } from '../../api/productos/productos'; import { getUserList } from '../../api/usuario/usuario'; import { listarUbicacionesPaginado } from '../../api/ubicacionAlmacen/ubicacionAlmacen'; import ConteoInventarioDialog from '../../components/inventario_fisico/ConteoInventarioDialog.vue'; import DetalleInventarioFisicoDialog from '../../components/inventario_fisico/DetalleInventarioFisicoDialog.vue'; import InventarioFisicoFiltros from '../../components/inventario_fisico/InventarioFisicoFiltros.vue'; import InventarioFisicoResumen from '../../components/inventario_fisico/InventarioFisicoResumen.vue'; import InventarioFisicoTabla from '../../components/inventario_fisico/InventarioFisicoTabla.vue'; import NuevoInventarioFisicoDialog from '../../components/inventario_fisico/NuevoInventarioFisicoDialog.vue'; import RevisionInventarioDialog from '../../components/inventario_fisico/RevisionInventarioDialog.vue';
const $q = useQuasar(), nuevo = ref(false), conteo = ref(false), revision = ref(false), detalle = ref(false), dialogEliminar = ref(false), filaAccion = ref(null), seleccionado = ref(null), inventarios = ref([]), total = ref(0), cargando = ref(false), cargandoCatalogos = ref(false), cargandoUbicaciones = ref(false), esAdmin = ref(false), filtros = ref({}), almacenes = ref([]), ubicaciones = ref([]), categorias = ref([]), productos = ref([]), responsables = ref([]); const estados = [{ label: 'Borrador', value: 'BORRADOR' }, { label: 'En conteo', value: 'EN_CONTEO' }, { label: 'En revisión', value: 'EN_REVISION' }, { label: 'Aprobado', value: 'APROBADO' }, { label: 'Cancelado', value: 'CANCELADO' }]; const pagination = ref({ page: 1, rowsPerPage: 5, rowsNumber: 0, sortBy: 'fechaCreacion', descending: true });
const resumen = computed(() => [{ label: 'Inventarios encontrados', value: total.value, icon: 'inventory_2', help: 'Según los filtros aplicados' }, { label: 'Abiertos en esta página', value: inventarios.value.filter(x => ['BORRADOR', 'EN_CONTEO'].includes(x.estado)).length, icon: 'pending_actions', help: 'Borradores y en conteo' }, { label: 'Pendientes de revisión', value: inventarios.value.filter(x => x.estado === 'EN_REVISION').length, icon: 'rule', help: 'En la página actual', tone: 'warning' }, { label: 'Con diferencias', value: inventarios.value.filter(x => x.diferencias > 0).length, icon: 'difference', help: 'En la página actual', tone: 'danger' }]);
function mapearLista(x) { return { id: x.idInventarioFisico, codigo: x.codigo, fecha: fecha(x.fechaProgramada), fechaIso: x.fechaProgramada, almacen: x.almacen || 'Todos los almacenes', tipo: x.tipo, tipoLabel: x.tipoLabel, responsable: x.responsable, contados: Number(x.productosContados || 0), totalProductos: Number(x.totalProductos || 0), diferencias: Number(x.diferencias || 0), estado: x.estado, ciego: x.conteoCiego } }
async function cargar(ctx) { const p = ctx?.pagination || pagination.value; pagination.value = { ...pagination.value, ...p }; cargando.value = true; try { const r = await listarInventariosFisicos({ buscar: filtros.value.texto || undefined, estado: filtros.value.estado || undefined, idAlmacen: filtros.value.almacen || undefined, fechaDesde: filtros.value.desde || undefined, fechaHasta: filtros.value.hasta || undefined, page: p.page - 1, size: p.rowsPerPage, sortBy: campoOrden(p.sortBy), sortDir: p.descending ? 'desc' : 'asc' }); inventarios.value = (r?.content || []).map(mapearLista); total.value = Number(r?.totalElements || 0); pagination.value.rowsNumber = total.value } catch (e) { inventarios.value = []; total.value = 0; notificarError(e, 'No se pudo cargar el inventario físico.') } finally { cargando.value = false } }
async function cargarCatalogos() { cargandoCatalogos.value = true; try { const [a, c, p, e] = await Promise.all([listarAlmacenes({ page: 0, size: 100, estado: true, sortBy: 'nombre', sortDir: 'asc' }), listarCategoriasActivas(), listarProductos({ page: 0, size: 100, estado: true, sortBy: 'nombre', sortDir: 'asc' }), listarEmpleados({ page: 0, size: 100, estado: true, sortBy: 'nombreCompleto', sortDir: 'asc' })]); almacenes.value = (a?.content || []).filter(x => x.activo !== false).map(x => ({ label: x.nombre, value: x.idAlmacen })); categorias.value = (Array.isArray(c) ? c : []).map(x => ({ label: x.nombre, value: x.idCategoria })); productos.value = (p?.content || []).filter(x => x.estado !== false).map(x => ({ label: `${x.codigoInterno || 'S/C'} · ${x.nombre}`, value: x.idProducto })); responsables.value = (e?.content || []).filter(x => x.estado !== false).map(x => ({ label: x.nombreCompleto, value: x.id })) } catch (e) { notificarError(e, 'No se pudieron cargar los datos del formulario.') } finally { cargandoCatalogos.value = false } }
async function cargarUbicaciones(id) { ubicaciones.value = []; if (!id) return; cargandoUbicaciones.value = true; try { const r = await listarUbicacionesPaginado({ idAlmacen: id, estado: true, page: 0, size: 100 }); ubicaciones.value = (r?.content || []).map(x => ({ label: `${x.codigo} · ${x.nombre}`, value: x.idUbicacion })) } catch (e) { notificarError(e, 'No se pudieron cargar las ubicaciones.') } finally { cargandoUbicaciones.value = false } }
async function crear(f) { try { await crearInventarioFisico({ tipo: f.tipo, idAlmacen: ['POR_ALMACEN', 'POR_UBICACION'].includes(f.tipo) ? f.almacen : null, idUbicacion: f.tipo === 'POR_UBICACION' ? f.ubicacion : null, idCategoria: f.tipo === 'POR_CATEGORIA' ? f.categoria : null, idsProductos: f.tipo === 'PRODUCTOS' ? f.productos : null, idResponsable: f.responsable, fechaProgramada: f.fecha, conteoCiego: esAdmin.value ? f.ciego : true, observacion: f.observacion || null }); $q.notify({ type: 'positive', message: 'Inventario físico creado como borrador.' }); pagination.value.page = 1; await cargar() } catch (e) { nuevo.value = true; notificarError(e, 'No se pudo crear el inventario físico.') } }
function payloadInventario(f) { return { tipo: f.tipo, idAlmacen: ['POR_ALMACEN', 'POR_UBICACION'].includes(f.tipo) ? f.almacen : null, idUbicacion: f.tipo === 'POR_UBICACION' ? f.ubicacion : null, idCategoria: f.tipo === 'POR_CATEGORIA' ? f.categoria : null, idsProductos: f.tipo === 'PRODUCTOS' ? f.productos : null, idResponsable: f.responsable, fechaProgramada: f.fecha, conteoCiego: esAdmin.value ? f.ciego : true, observacion: f.observacion || null } }
async function actualizarBorrador(f) { if (!editando.value?.idInventarioFisico) return; try { await actualizarInventarioFisico(editando.value.idInventarioFisico, payloadInventario(f)); editando.value = null; $q.notify({ type: 'positive', message: 'Borrador actualizado correctamente.' }); await cargar() } catch (e) { nuevo.value = true; notificarError(e, 'No se pudo actualizar el borrador.') } }
async function abrirConteo(row) { try { const r = row.estado === 'BORRADOR' ? await iniciarInventarioFisico(row.id) : await obtenerInventarioFisico(row.id); seleccionado.value = mapearDetalle(r, row); conteo.value = true; if (row.estado === 'BORRADOR') { $q.notify({ type: 'positive', message: 'Conteo iniciado. Se guardó la fotografía del stock.' }); await cargar() } } catch (e) { notificarError(e, 'No se pudo iniciar el conteo.') } }
async function verDetalle(row) { try { seleccionado.value = mapearDetalle(await obtenerInventarioFisico(row.id), row); detalle.value = true } catch (e) { notificarError(e, 'No se pudo cargar el detalle.') } } function mapearDetalle(x, r) { return { ...r, id: x.idInventarioFisico, codigo: x.codigo, fecha: fecha(x.fechaProgramada), fechaIso: x.fechaProgramada, almacen: x.almacen?.nombre || 'Todos los almacenes', ubicacion: x.ubicacion?.nombre || 'Todos', categoria: x.categoria?.nombre || 'Todas', responsable: x.responsable?.nombre || r?.responsable || '--', tipo: x.tipo, tipoLabel: etiquetaTipo(x.tipo), estado: x.estado, totalProductos: Number(x.totalProductos || r?.totalProductos || 0), totalLineas: Number(x.totalLineas || 0), conteoCiego: Boolean(x.conteoCiego), ciego: Boolean(x.conteoCiego), observacion: x.observacion || '', fechaCreacion: x.fechaCreacion, fechaInicio: x.fechaInicio, fechaFinalizacion: x.fechaFinalizacion, fechaAprobacion: x.fechaAprobacion, creadoPor: x.creadoPor?.nombre || '--', resumen: x.resumen, detalles: x.detalles || [] } }
async function cargarPermisos() { try { const perfil = await getUserList(); esAdmin.value = Array.isArray(perfil?.roles) && perfil.roles.includes('ROLE_ADMIN') } catch { esAdmin.value = false } }
async function conteoFinalizado() { await cargar() }
async function editarBorrador(row) { if (row?.estado !== 'BORRADOR') return $q.notify({ type: 'warning', message: 'Solo se puede editar un inventario en borrador.' }); try { const datos = await obtenerInventarioFisico(row.id); if (datos.tipo === 'POR_UBICACION' && datos.almacen?.id) await cargarUbicaciones(datos.almacen.id); editando.value = datos; nuevo.value = true } catch (e) { notificarError(e, 'No se pudo cargar el borrador para editar.') } }
function confirmarEliminar(row) { if (row?.estado !== 'BORRADOR') return $q.notify({ type: 'warning', message: 'Solo se puede eliminar un inventario en borrador.' }); filaAccion.value = row; confirmadoEliminar.value = false; dialogEliminar.value = true }
async function eliminarBorrador() { if (!confirmadoEliminar.value) return $q.notify({ type: 'warning', message: 'Confirma que deseas eliminar el borrador.' }); if (filaAccion.value?.estado !== 'BORRADOR') { dialogEliminar.value = false; return $q.notify({ type: 'warning', message: 'El inventario ya no se encuentra en borrador.' }) } const fila = filaAccion.value; try { await eliminarInventarioFisico(fila.id); dialogEliminar.value = false; confirmadoEliminar.value = false; filaAccion.value = null; $q.notify({ type: 'positive', message: `El borrador ${fila.codigo} fue eliminado correctamente.` }); await cargar({ pagination: { ...pagination.value, page: 1 } }) } catch (e) { $q.notify({ type: 'negative', message: e?.response?.data?.message || 'No se pudo eliminar el borrador.' }) } }
function filtrar(v) { filtros.value = v; pagination.value.page = 1; cargar() } function limpiar() { filtros.value = {}; pagination.value.page = 1; cargar() } function abrirNuevo() { editando.value = null; nuevo.value = true; if (!almacenes.value.length) cargarCatalogos() } function abrirRevision(r) { seleccionado.value = r; revision.value = true } function revisionTerminada() { revision.value = false; seleccionado.value = null; pagination.value.page = 1; cargar() } function sinEndpoint(nombre) { $q.notify({ type: 'info', message: `${nombre} se conectará cuando exista su endpoint en el backend.` }) } async function pdf(r) { try { const blob = await descargarActaInventarioFisico(r.id || r.idInventarioFisico); const url = URL.createObjectURL(blob); const enlace = document.createElement('a'); enlace.href = url; enlace.download = `Acta_Inventario_${r.codigo || r.id}.pdf`; document.body.appendChild(enlace); enlace.click(); enlace.remove(); URL.revokeObjectURL(url); $q.notify({ type: 'positive', message: 'Acta PDF descargada correctamente.' }) } catch (e) { notificarError(e, 'No se pudo descargar el acta PDF.') } } function campoOrden(c) { return ({ codigo: 'codigo', fecha: 'fechaProgramada', estado: 'estado' })[c] || 'fechaCreacion' } function etiquetaTipo(t) { return ({ COMPLETO: 'Inventario completo', POR_ALMACEN: 'Por almacén', POR_UBICACION: 'Por ubicación', POR_CATEGORIA: 'Por categoría', PRODUCTOS: 'Productos seleccionados' })[t] || t } function fecha(v) { return v ? new Date(`${v}T12:00:00`).toLocaleDateString('es-BO') : '--' } function notificarError(e, m) { $q.notify({ type: 'negative', message: e?.response?.data?.message || m }) } onMounted(() => { cargar(); cargarCatalogos(); cargarPermisos() })
</script>
<style scoped>
.pages {
  min-height: 100%;
  background: #ffffff;
  color: #263238;
  font-family: 'Nunito', sans-serif
}

.container {
  max-width: 1500px;
  margin: auto;
  padding: 24px
}

.inventory-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px
}

.title-row {
  display: flex;
  align-items: center;
  gap: 10px
}

.inventory-header h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 900
}

.inventory-header p {
  margin: 4px 0;
  color: #6b7280;
  font-size: 13px
}

.list-title {
  font-size: 16px;
  font-weight: 900
}

.legend {
  font-size: 11px;
  color: #5d8055;
  display: flex;
  align-items: center;
  gap: 4px
}

.delete-card {
  width: min(470px, 94vw);
  border-radius: 20px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #e4edd8;
  box-shadow: 0 20px 60px rgba(42, 92, 26, .15);
  font-family: 'Nunito', sans-serif
}

.accent-bar {
  height: 3px;
  background: linear-gradient(90deg, #82bd43, #64992b, #d97b1a)
}

.dialog-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 16px 20px 14px;
  background: #fff8f0;
  border-bottom: 1px solid #f5dbb8
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px
}

.warn-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  background: #fef3e2;
  border: 1.5px solid #f5c97a;
  color: #d97b1a
}

.delete-title {
  font-size: 16px;
  font-weight: 900;
  color: #2a5c1a;
  margin-bottom: 2px
}

.dialog-sub {
  font-size: 11px;
  font-weight: 600;
  color: #a56a22
}

.close-btn {
  color: #7aaa4e !important;
  background: #f0f7e8 !important
}

.dialog-body {
  padding: 18px 20px 8px !important
}

.draft-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f7f9f4;
  border: 1px solid #e4edd8;
  border-radius: 12px;
  padding: 12px 14px;
  margin-bottom: 14px
}

.draft-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: #eaf4d8;
  border: 2px solid #c8e0a0;
  color: #4a8c25
}

.draft-name {
  font-size: 14px;
  font-weight: 800;
  color: #2a5c1a
}

.draft-type {
  font-size: 11px;
  color: #7b857c;
  margin-top: 2px
}

.warn-box {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  border-radius: 10px;
  padding: 11px 13px;
  margin-bottom: 14px;
  background: #fff8f0;
  border: 1px solid #f5c97a;
  color: #d97b1a
}

.warn-box p {
  font-size: 12px;
  font-weight: 600;
  line-height: 1.6;
  margin: 0;
  color: #a05c10
}

.warn-box strong {
  color: #7d4b0d
}

.confirm-row {
  display: flex;
  align-items: center;
  gap: 9px;
  border-radius: 9px;
  padding: 10px 12px;
  margin-bottom: 4px;
  cursor: pointer;
  background: #fef3e2;
  border: 1px solid #f5c97a;
  font-size: 12px;
  font-weight: 700;
  color: #a05c10
}

.confirm-check {
  width: 18px;
  height: 18px;
  border-radius: 5px;
  background: #fff;
  border: 2px solid #d97b1a;
  display: grid;
  place-items: center;
  flex-shrink: 0
}

.confirm-check.checked {
  background: #d97b1a;
  border-color: #d97b1a
}

.dialog-footer {
  display: flex;
  gap: 10px;
  padding: 14px 20px 18px;
  border-top: 1px solid #e4edd8
}

.btn-cancel,
.btn-delete {
  flex: 1;
  padding: 11px;
  border-radius: 9px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px
}

.btn-cancel {
  background: #fff;
  color: #5a5a5a;
  border: 1.5px solid #d0d0d0
}

.btn-delete {
  flex: 2;
  background: #d97b1a;
  color: #fff;
  border: 0;
  font-weight: 800;
  box-shadow: 0 4px 14px rgba(217, 123, 26, .3)
}

.btn-delete:hover:not(:disabled) {
  background: #c06a10;
  transform: translateY(-1px)
}

.btn-delete:disabled {
  opacity: .5;
  cursor: not-allowed;
  box-shadow: none
}

@media(max-width:700px) {
  .container {
    padding: 14px
  }

  .inventory-header {
    align-items: flex-start;
    flex-direction: column
  }

  .inventory-header>.q-btn {
    width: 100%
  }

  .legend {
    display: none
  }
}

@media(max-width:600px) {
  .delete-card {
    width: 100vw;
    border-radius: 0
  }

  .dialog-footer {
    padding: 14px
  }

  .dialog-body {
    padding: 18px 14px 8px !important
  }
}
</style>
