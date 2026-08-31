<template><q-page class="transfer-page">
        <div class="container">
            <header class="transfer-header">
                <div>
                    <div class="title-with-icon"><q-icon name="swap_horiz" color="green-8" size="32px"/><h1>Gestión de Transferencias</h1></div>
                    <p>Controla el traslado de productos entre almacenes, ubicaciones y sala de ventas.</p>
                </div><q-btn unelevated no-caps color="green-8" icon="add" label="Nueva Transferencia"
                    @click="abrirNueva" />
            </header>
            <div class="row q-col-gutter-md q-mb-lg">
                <div v-for="s in stats" :key="s.label" class="col-12 col-sm-6 col-md-3"><q-card flat bordered
                        class="stat"><q-card-section class="row items-center no-wrap q-pa-md">
                            <div class="stat-icon"><q-icon :name="s.icon" size="25px" /></div>
                            <div class="q-ml-md">
                                <div class="stat-title">{{ s.label }}</div>
                                <div class="stat-value">{{ s.value }}</div>
                                <div class="stat-help">{{ s.help }}</div>
                            </div>
                        </q-card-section></q-card></div>
            </div>
            <TransferenciaFilters :estados="estados" @filtrar="filtrar" @limpiar="limpiar" />
            <div class="row justify-between items-center q-mb-md"><span class="list-title">Transferencias registradas
                    <q-badge color="green-8" class="q-ml-sm">{{ total }}</q-badge></span></div><q-card flat bordered
                class="table-card"><q-table v-model:pagination="pagination" rows-per-page-label="Registros por página" :rows="rows" :columns="columns"
                    row-key="idTransferencia" :loading="loading" :rows-per-page-options="[10, 20, 50]"
                    @request="cargar"><template #body-cell-estado="p"><q-td :props="p"><q-badge
                                :color="colorEstado(p.value)" :label="p.value" /></q-td></template><template
                        #body-cell-acciones="p"><q-td :props="p"><q-btn flat round dense icon="visibility"
                                color="green-8" @click="ver(p.row)"><q-tooltip>Ver detalle</q-tooltip></q-btn><q-btn flat round dense icon="picture_as_pdf" color="negative" @click="descargarPdf(p.row)"><q-tooltip>Descargar PDF</q-tooltip></q-btn><q-btn
                                v-if="p.row.estado === 'BORRADOR'" flat round dense icon="edit" color="green-8"
                                @click="continuar(p.row)"><q-tooltip>Editar / Continuar
                                    borrador</q-tooltip></q-btn><q-btn v-if="p.row.estado === 'BORRADOR'" flat round dense
                                icon="delete" color="negative" @click="accion(p.row, 'eliminar')"><q-tooltip>Eliminar
                                    borrador</q-tooltip></q-btn><q-btn v-if="p.row.estado === 'PENDIENTE'" flat round
                                dense icon="play_arrow" color="orange-8"
                                @click="accion(p.row, 'procesar')"><q-tooltip>Procesar
                                    transferencia</q-tooltip></q-btn><q-btn v-if="p.row.estado === 'EN_PROCESO'" flat
                                round dense icon="check" color="green-8"
                                @click="accion(p.row, 'completar')"><q-tooltip>Completar
                                    transferencia</q-tooltip></q-btn><q-btn
                                v-if="['PENDIENTE', 'EN_PROCESO'].includes(p.row.estado)" flat round dense icon="block"
                                color="negative" @click="accion(p.row, 'cancelar')"><q-tooltip>Cancelar
                                    transferencia</q-tooltip></q-btn></q-td></template><template #pagination="scope"><div class="row items-center q-gutter-xs"><q-btn round flat dense icon="first_page" color="green-8" :disable="scope.isFirstPage" @click="scope.firstPage"/><q-btn round flat dense icon="chevron_left" color="green-8" :disable="scope.isFirstPage" @click="scope.prevPage"/><span class="q-px-sm text-caption">Página {{ pagination.page }}</span><q-btn round flat dense icon="chevron_right" color="green-8" :disable="scope.isLastPage" @click="scope.nextPage"/><q-btn round flat dense icon="last_page" color="green-8" :disable="scope.isLastPage" @click="scope.lastPage"/></div></template></q-table></q-card>
        </div>
        <TransferenciaFormDialog v-model="dialogForm" :transferencia="borradorSeleccionado" @guardar="guardar" />
        <TransferenciaDetalleDialog v-model="dialogDetalle" :transferencia="seleccionada" />
    </q-page></template>
<script setup>
import { descargarPdfTransferencia } from '../../api/transferencias/transferencias'
import { computed, onMounted, ref } from 'vue'; 
import { useQuasar } from 'quasar'; 
import { listarTransferencias, listarEstadosTransferencia, crearTransferencia, procesarTransferencia, completarTransferencia, cancelarTransferencia, eliminarBorradorTransferencia } 
from '../../api/transferencias/transferencias'; 
import TransferenciaFilters from '../../components/transferencias/TransferenciaFilters.vue'; 
import TransferenciaFormDialog from '../../components/transferencias/TransferenciaFormDialog.vue'; 
import TransferenciaDetalleDialog from '../../components/transferencias/TransferenciaDetalleDialog.vue'; 
const $q = useQuasar(); 
const rows = ref([]); 
const total = ref(0); 
const loading = ref(false); 
const estados = ref([]); 
const filtros = ref({}); 
const dialogForm = ref(false); 
const dialogDetalle = ref(false); 
const seleccionada = ref(null); 
const borradorSeleccionado = ref(null); 
const pagination = ref({ page: 1, rowsPerPage: 10, sortBy: 'fechaTransferencia', descending: true, rowsNumber: 0 }); 
const columns = [{ name: 'codigo', label: 'Nº', field: 'codigo' }, { name: 'fecha', label: 'Fecha', field: r => r.fechaTransferencia?.slice(0, 10) || '-' }, { name: 'origen', label: 'Origen', field: 'origen' }, { name: 'destino', label: 'Destino', field: 'destino' }, { name: 'cantidadProductos', label: 'Productos', field: r => `${r.cantidadProductos || 0} productos` }, { name: 'cantidadTotal', label: 'Cantidad total', field: r => `${r.cantidadTotal || 0} unidades` }, { name: 'estado', label: 'Estado', field: 'estado' }, { name: 'responsable', label: 'Responsable', field: 'responsable' }, { name: 'acciones', label: 'Acciones', field: 'acciones' }]; const stats = computed(() => [{ label: 'Total transferencias', icon: 'swap_horiz', value: total.value, help: 'Registros encontrados' }, { label: 'Pendientes', icon: 'schedule', value: rows.value.filter(x => x.estado === 'PENDIENTE').length, help: 'Esperando procesamiento' }, { label: 'En proceso', icon: 'sync', value: rows.value.filter(x => x.estado === 'EN_PROCESO').length, help: 'Traslados en curso' }, { label: 'Completadas', icon: 'check_circle', value: rows.value.filter(x => x.estado === 'COMPLETADA').length, help: 'Traslados finalizados' }]); function colorEstado(e) { return { BORRADOR: 'grey-7', PENDIENTE: 'amber-8', EN_PROCESO: 'orange-8', COMPLETADA: 'green-7', CANCELADA: 'red-5' }[e] || 'grey-6' } async function cargar(ctx) { const p = ctx?.pagination || pagination.value; pagination.value = { ...pagination.value, ...p }; loading.value = true; try { const r = await listarTransferencias({ texto: filtros.value.texto || undefined, estado: filtros.value.estado || undefined, fechaDesde: filtros.value.desde || undefined, fechaHasta: filtros.value.hasta || undefined, page: p.page - 1, size: p.rowsPerPage, sortBy: p.sortBy || 'fechaTransferencia', sortDir: p.descending ? 'desc' : 'asc' }); rows.value = r?.content || []; total.value = Number(r?.totalElements || 0); pagination.value.rowsNumber = total.value } catch { $q.notify({ type: 'negative', message: 'No se pudieron cargar las transferencias.' }) } finally { loading.value = false } } async function cargarEstados() { try { estados.value = await listarEstadosTransferencia() } catch { } } function filtrar(v) { filtros.value = v; pagination.value.page = 1; cargar() } function limpiar() { filtros.value = {}; pagination.value.page = 1; cargar() } function abrirNueva() { borradorSeleccionado.value = null; dialogForm.value = true } function continuar(t) { borradorSeleccionado.value = t; dialogForm.value = true } function ver(t) { seleccionada.value = t; dialogDetalle.value = true } async function guardar(d) { try { await crearTransferencia({ borrador: Boolean(d.borrador), idUbicacionOrigen: d.idUbicacionOrigen, idUbicacionDestino: d.idUbicacionDestino, observacion: d.observacion, detalles: (d.productos || []).filter(p => p.idProducto).map(p => ({ idProducto: p.idProducto, idLote: p.idLote || null, cantidad: p.cantidad })) }); dialogForm.value = false; $q.notify({ type: 'positive', message: 'Guardado correctamente.' }); cargar() } catch (e) { $q.notify({ type: 'negative', message: e?.response?.data?.message || 'No se pudo guardar.' }) } } async function accion(t, tipo) { try { if (tipo === 'procesar') await procesarTransferencia(t.idTransferencia); else if (tipo === 'completar') await completarTransferencia(t.idTransferencia); else if (tipo === 'cancelar') await cancelarTransferencia(t.idTransferencia); else await eliminarBorradorTransferencia(t.idTransferencia); $q.notify({ type: 'positive', message: 'Operación realizada correctamente.' }); cargar() } catch (e) { $q.notify({ type: 'negative', message: e?.response?.data?.message || 'No se pudo realizar la operación.' }) } } onMounted(() => { cargar(); cargarEstados() })
async function descargarPdf(t){try{const blob=await descargarPdfTransferencia(t.idTransferencia);const url=URL.createObjectURL(blob);const a=document.createElement('a');a.href=url;a.download=`Transferencia_${t.codigo}.pdf`;a.click();URL.revokeObjectURL(url)}catch{$q.notify({type:'negative',message:'No se pudo descargar el PDF.'})}}
</script>
<style
    scoped>
    .transfer-page {
        min-height: 100%;
        background: #f5f7f4;
        color: #263238;
        font-family: 'Nunito', sans-serif
    }

    .container {
        max-width: 1500px;
        margin: auto;
        padding: 24px
    }

    .transfer-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 16px;
        margin-bottom: 24px
    }

    .transfer-header h1 {
        margin: 0;
        font-size: 27px;
        font-weight: 900
    }

    .title-with-icon { display: flex; align-items: center; gap: 10px; }

    .transfer-header p {
        margin: 4px 0;
        color: #6b7280;
        font-size: 13px
    }

    .stat {
        height: 100%;
        border-radius: 16px;
        border-color: #dfe7dc !important;
        box-shadow: 0 4px 18px rgba(74, 140, 37, .07)
    }

    .stat-icon {
        width: 52px;
        height: 52px;
        border-radius: 14px;
        display: grid;
        place-items: center;
        color: #fff;
        background: linear-gradient(135deg, #82bd43, #4a8c25)
    }

    .stat-title {
        font-size: 12px;
        font-weight: 700;
        color: #6b7280
    }

    .stat-value {
        font-size: 27px;
        line-height: 1.1;
        font-weight: 900
    }

    .stat-help {
        font-size: 11px;
        color: #9aa39a;
        margin-top: 3px
    }

    .list-title {
        font-size: 16px;
        font-weight: 900
    }

    .table-card {
        border-color: #dfe7dc;
        border-radius: 16px;
        overflow: hidden
    }

    :deep(.q-table thead tr) {
        background: #f0f8e9
    }

    @media(max-width:700px) {
        .container {
            padding: 14px
        }

        .transfer-header {
            align-items: flex-start;
            flex-direction: column
        }

        .transfer-header>.q-btn {
            width: 100%
        }
    }
</style>
