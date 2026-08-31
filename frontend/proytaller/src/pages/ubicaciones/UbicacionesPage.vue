<template>
  <q-page class="ubi-page">
    <div class="ubi-container">
      <header class="ubi-header q-mb-lg">
        <div class="row items-center no-wrap">
          <div class="ubi-header-icon"><q-icon name="location_on" size="28px" /></div>
          <div class="q-ml-md">
            <h1>Gestión de Ubicaciones</h1>
            <p>Administra zonas, pasillos, estantes y sectores físicos dentro de los almacenes.</p>
          </div>
        </div><q-btn unelevated no-caps icon="add" label="Nueva Ubicación" color="green-8"
          @click="abrirNuevaUbicacion" />
      </header>
      <UbicacionIndicadores :total="totalElementos" :activas="ubicacionesActivas" :inactivas="ubicacionesInactivas"
        :ocupadas="ubicacionesOcupadas" />
      <UbicacionFiltros :almacen-inicial="route.query.almacenId || null" :almacenes="almacenesOpciones" :tipos="tiposOpciones" :estados="estadosOpciones"
        @filtrar="aplicarFiltros" @limpiar="limpiarFiltros" />
      <div class="row justify-between items-center q-mb-md"><span class="ubi-list-title">Ubicaciones registradas
          <q-badge color="green-8" class="q-ml-sm">{{ totalElementos }}</q-badge></span><span
          class="text-caption text-grey-6">Mostrando {{ ubicaciones.length }} resultados</span></div>
      <div v-if="cargando" class="row q-col-gutter-lg">
        <div v-for="n in porPagina" :key="n" class="col-12 col-sm-6 col-lg-4 q-mb-lg"><q-card flat bordered
            class="q-pa-lg"><q-skeleton type="QAvatar" /><q-skeleton type="text" /><q-skeleton type="text"
              width="65%" /><q-skeleton height="90px" class="q-mt-md" /></q-card></div>
      </div>
      <div v-else-if="ubicaciones.length" class="row q-col-gutter-lg">
        <div v-for="ubicacion in ubicaciones" :key="ubicacion.id" class="col-12 col-sm-6 col-lg-4 q-mb-lg">
          <UbicacionCard :ubicacion="ubicacion" @ver="verDetalleUbicacion" @editar="editarUbicacion"
            @pdf="descargarPdf" @estado="solicitarCambioEstado" />
        </div>
      </div>
      <q-card v-else flat bordered class="ubi-empty"><q-icon name="search_off" size="58px" color="grey-4" />
        <h3>No se encontraron ubicaciones</h3>
        <p>Intenta modificar los filtros de búsqueda.</p><q-btn outline no-caps color="green-8" icon="restart_alt"
          label="Limpiar filtros" @click="limpiarFiltros" />
      </q-card>
      <div v-if="totalPaginas > 1" class="row justify-center q-mt-sm"><q-pagination v-model="pagina" :max="totalPaginas"
          color="green-8" direction-links boundary-links @update:model-value="cargarUbicaciones" /></div>
    </div>
    <UbicacionFormularioDialog v-model="dialogUbicacion" :ubicacion="ubicacionSeleccionada" :tipos="tiposOpciones" :almacenes="almacenesOpciones"
      @guardar="guardarUbicacion" />
    <UbicacionDetalleDialog v-model="dialogDetalle" :ubicacion="ubicacionSeleccionada" @pdf="descargarPdf" />
    <UbicacionConfirmacionDialog v-model="dialogConfirmacion" :ubicacion="ubicacionSeleccionada"
      @confirmar="confirmarCambioEstado" />
  </q-page>
</template>
<script setup>
import { computed, onMounted, ref } from 'vue'
import { useQuasar } from 'quasar'
import { useRoute } from 'vue-router'
import { activarUbicacion, actualizarUbicacion as actualizarUbicacionApi, crearUbicacion, descargarInventarioUbicacionPdf, desactivarUbicacion, listarProductosUbicacion, listarUbicacionesPaginado, listarEstadosUbicacion, listarTiposUbicacion } from '../../api/ubicacionAlmacen/ubicacionAlmacen'
import { listarAlmacenes } from '../../api/almacen/almacen'
import UbicacionIndicadores from '../../components/ubicaciones/UbicacionIndicadores.vue'; import UbicacionFiltros from '../../components/ubicaciones/UbicacionFiltros.vue'; import UbicacionCard from '../../components/ubicaciones/UbicacionCard.vue'; import UbicacionFormularioDialog from '../../components/ubicaciones/UbicacionFormularioDialog.vue'; import UbicacionDetalleDialog from '../../components/ubicaciones/UbicacionDetalleDialog.vue'; import UbicacionConfirmacionDialog from '../../components/ubicaciones/UbicacionConfirmacionDialog.vue'
const $q = useQuasar(); const route = useRoute(); const ubicaciones = ref([]); const almacenesOpciones = ref([]); const tiposOpciones = ref([]); const estadosOpciones = ref([]); const ubicacionSeleccionada = ref(null); const dialogUbicacion = ref(false); const dialogDetalle = ref(false); const dialogConfirmacion = ref(false); const cargando = ref(false); const pagina = ref(1); const porPagina = 6; const totalElementos = ref(0); const totalPaginas = ref(0); const filtros = ref({ texto: '', almacen: route.query.almacenId || null, tipo: null, estado: null })
const ubicacionesActivas = computed(() => ubicaciones.value.filter(u => u.estado === 'ACTIVO').length); const ubicacionesInactivas = computed(() => ubicaciones.value.filter(u => u.estado === 'INACTIVO').length); const ubicacionesOcupadas = computed(() => ubicaciones.value.filter(u => u.stockTotal > 0).length)
async function cargarUbicaciones() { cargando.value = true; try { const r = await listarUbicacionesPaginado({ search: filtros.value.texto || undefined, idAlmacen: Number(filtros.value.almacen) || undefined, tipo: filtros.value.tipo || undefined, estado: filtros.value.estado === 'ACTIVO' ? true : filtros.value.estado === 'INACTIVO' ? false : undefined, page: pagina.value - 1, size: porPagina }); totalElementos.value = Number(r?.totalElements || 0); totalPaginas.value = Number(r?.totalPages || 0); ubicaciones.value = (r?.content || []).map(u => ({ id: u.idUbicacion, idAlmacen: u.idAlmacen, codigo: u.codigo, nombre: u.nombre, almacen: u.nombreAlmacen, tipo: u.tipo || 'UBICACION', estado: u.activo ? 'ACTIVO' : 'INACTIVO', descripcion: u.descripcion || '', productos: Number(u.productos || 0), stockTotal: Number(u.stockTotal || 0), productosDetalle: [] })) } catch (e) { ubicaciones.value = []; totalElementos.value = 0; totalPaginas.value = 0; $q.notify({ type: 'negative', message: 'No se pudieron cargar las ubicaciones.' }) } finally { cargando.value = false } }
async function cargarAlmacenesFiltro() { try { const r = await listarAlmacenes({ page: 0, size: 100, sortBy: 'nombre', sortDir: 'asc' }); almacenesOpciones.value = (r?.content || []).map(a => ({ label: a.nombre, value: a.idAlmacen })) } catch { almacenesOpciones.value = [] } }
async function cargarEstadosFiltro() { try { const r = await listarEstadosUbicacion(); estadosOpciones.value = Array.isArray(r) ? r : [] } catch { estadosOpciones.value = [] } }
async function cargarTiposFiltro() { try { const r = await listarTiposUbicacion(); tiposOpciones.value = Array.isArray(r) ? r : [] } catch { tiposOpciones.value = [] } }
async function descargarPdf(u) { try { const blob = await descargarInventarioUbicacionPdf(u.id); const url = URL.createObjectURL(blob); const enlace = document.createElement('a'); enlace.href = url; enlace.download = `Inventario_Ubicacion_${u.codigo || u.id}.pdf`; enlace.click(); URL.revokeObjectURL(url); $q.notify({ type: 'positive', message: 'PDF descargado correctamente.' }) } catch { $q.notify({ type: 'negative', message: 'No se pudo descargar el PDF.' }) } }
function abrirNuevaUbicacion() { ubicacionSeleccionada.value = null; dialogUbicacion.value = true } function editarUbicacion(u) { ubicacionSeleccionada.value = { ...u }; dialogUbicacion.value = true } async function verDetalleUbicacion(u) { ubicacionSeleccionada.value = { ...u, productosDetalle: [] }; dialogDetalle.value = true; try { ubicacionSeleccionada.value.productosDetalle = await listarProductosUbicacion(u.id) } catch { $q.notify({ type: 'negative', message: 'No se pudieron cargar los productos de la ubicación.' }) } }
async function guardarUbicacion(datos) { try { if (!datos.id) { await crearUbicacion({ idAlmacen: Number(datos.almacen), nombre: datos.nombre, tipo: datos.tipo, descripcion: datos.descripcion || null }); $q.notify({ type: 'positive', message: 'Ubicación registrada correctamente.' }) } else { await actualizarUbicacionApi(datos.id, { nombre: datos.nombre, tipo: datos.tipo, descripcion: datos.descripcion || null }); $q.notify({ type: 'positive', message: 'Ubicación actualizada correctamente.' }) } dialogUbicacion.value = false; await cargarUbicaciones() } catch (e) { $q.notify({ type: 'negative', message: e?.response?.data?.message || 'No se pudo guardar la ubicación.' }) }
}
function solicitarCambioEstado(u) { ubicacionSeleccionada.value = u; dialogConfirmacion.value = true } async function confirmarCambioEstado() { const u = ubicacionSeleccionada.value; if (!u) return; try { if (u.estado === 'ACTIVO') await desactivarUbicacion(u.id); else await activarUbicacion(u.id); dialogConfirmacion.value = false; $q.notify({ type: 'positive', message: 'Estado actualizado correctamente.' }); await cargarUbicaciones() } catch (e) { $q.notify({ type: 'negative', message: e?.response?.data?.message || 'No se pudo cambiar el estado.' }) } }
async function aplicarFiltros(v) { filtros.value = { ...v }; pagina.value = 1; await cargarUbicaciones() } async function limpiarFiltros() { filtros.value = { texto: '', almacen: null, tipo: null, estado: null }; pagina.value = 1; await cargarUbicaciones() }
onMounted(() => Promise.all([cargarUbicaciones(), cargarAlmacenesFiltro(), cargarEstadosFiltro(), cargarTiposFiltro()]))
</script>
<style scoped>
.ubi-page {
  min-height: 100%;
  background: #f5f7f4;
  color: #263238;
  font-family: 'Nunito', sans-serif
}

.ubi-container {
  max-width: 1500px;
  margin: auto;
  padding: 24px
}

.ubi-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px
}

.ubi-header-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  color: #fff;
  background: linear-gradient(135deg, #82bd43, #4a8c25)
}

h1 {
  font-size: 27px;
  margin: 0;
  font-weight: 900
}

.ubi-header p {
  font-size: 13px;
  color: #6b7280;
  margin: 4px 0
}

.ubi-list-title {
  font-size: 16px;
  font-weight: 900
}

.ubi-empty {
  text-align: center;
  padding: 55px 20px;
  border-radius: 18px;
  border-style: dashed !important
}

.ubi-empty h3 {
  margin: 10px 0 4px
}

.ubi-empty p {
  color: #6b7280;
  margin: 0 0 18px
}

@media(max-width:750px) {
  .ubi-container {
    padding: 14px
  }

  .ubi-header {
    align-items: flex-start;
    flex-direction: column
  }

  .ubi-header>.q-btn {
    width: 100%
  }

  h1 {
    font-size: 22px
  }
}
</style>
