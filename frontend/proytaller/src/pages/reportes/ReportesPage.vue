<template>
  <q-page class="reportes-page">
    <main class="reportes-shell">
      <header class="reportes-header">
        <div>
          <div class="reportes-eyebrow">
            <q-icon name="insights" />
            Centro de información
          </div>
          <h1>Gestión de Reportes</h1>
          <p>Consulta, filtra y exporta la información clave del supermercado.</p>
        </div>

        <div class="header-badge">
          <q-icon name="verified" />
          <div>
            <span>Información centralizada</span>
            <small>Reportes listos para analizar</small>
          </div>
        </div>
      </header>

      <section class="workspace-grid">
        <aside class="catalog-panel">
          <div class="panel-heading">
            <div>
              <span class="section-kicker">Catálogo</span>
              <h2>Selecciona un reporte</h2>
            </div>
            <q-badge class="count-badge">{{ reportesFiltrados.length }}</q-badge>
          </div>

          <q-input
            v-model="busqueda"
            dense
            outlined
            clearable
            placeholder="Buscar reporte..."
            class="report-search"
          >
            <template #prepend>
              <q-icon name="search" />
            </template>
          </q-input>

          <div class="category-list" role="list">
            <button
              v-for="categoria in categorias"
              :key="categoria.id"
              type="button"
              class="category-button"
              :class="{ active: categoriaActiva === categoria.id }"
              @click="categoriaActiva = categoria.id"
            >
              <span class="category-icon">
                <q-icon :name="categoria.icon" />
              </span>
              <span class="category-copy">
                <strong>{{ categoria.label }}</strong>
                <small>{{ cantidadCategoria(categoria.id) }} reportes</small>
              </span>
              <q-icon name="chevron_right" class="category-arrow" />
            </button>
          </div>
        </aside>

        <section class="reports-panel">
          <div class="panel-heading reports-panel-heading">
            <div>
              <span class="section-kicker">Catálogo de reportes</span>
              <h2 class="reports-route-title">
                <template v-if="categoriaActual.id === 'todos'">Todos los reportes</template>
                <template v-else>
                  <span>{{ categoriaActual.label }}</span>
                  <q-icon name="chevron_right" />
                  <strong>Reportes</strong>
                </template>
              </h2>
            </div>
            <span class="selection-help">Elige una tarjeta para configurar el reporte</span>
          </div>

          <div v-if="reportesFiltrados.length" class="reports-grid">
            <button
              v-for="reporte in reportesFiltrados"
              :key="reporte.id"
              type="button"
              class="report-card"
              :class="{ selected: reporteSeleccionado.id === reporte.id }"
              @click="seleccionarReporte(reporte)"
            >
              <span class="report-card-top">
                <span class="report-icon" :style="{ background: reporte.softColor, color: reporte.color }">
                  <q-icon :name="reporte.icon" />
                </span>
                <q-icon
                  :name="reporteSeleccionado.id === reporte.id ? 'check_circle' : 'arrow_outward'"
                  class="report-state"
                />
              </span>
              <strong>{{ tituloReporte(reporte.nombre) }}</strong>
              <small>{{ reporte.descripcion }}</small>
              <span class="report-formats">
                <span><q-icon name="picture_as_pdf" /> PDF</span>
                <span><q-icon name="table_view" /> Excel</span>
              </span>
            </button>
          </div>

          <div v-else class="empty-state">
            <q-icon name="search_off" />
            <strong>No encontramos ese reporte</strong>
            <span>Prueba con otra palabra o selecciona una categoría diferente.</span>
            <q-btn flat no-caps color="green-8" label="Limpiar búsqueda" @click="busqueda = ''" />
          </div>
        </section>
      </section>

      <q-dialog
        v-model="dialogoReporte"
        transition-show="scale"
        transition-hide="scale"
      >
        <q-card class="builder-card report-dialog">
          <div class="builder-header">
          <div class="builder-title">
            <span class="builder-icon" :style="{ background: reporteSeleccionado.softColor, color: reporteSeleccionado.color }">
              <q-icon :name="reporteSeleccionado.icon" />
            </span>
            <div>
              <span class="section-kicker">Configuración del reporte</span>
              <h2>{{ tituloReporte(reporteSeleccionado.nombre) }}</h2>
              <p>{{ reporteSeleccionado.descripcion }}</p>
            </div>
          </div>
            <q-btn
              flat
              round
              dense
              icon="close"
              aria-label="Cerrar reporte"
              class="dialog-close"
              v-close-popup
            />
          </div>

          <q-card-section class="report-dialog-content">
            <div class="builder-body">
              <div class="filters-column">
                <q-expansion-item
                  v-model="filtrosAbiertos"
                  icon="tune"
                  label="Filtros del reporte"
                  caption="Define la información que deseas consultar y exportar."
                  header-class="filters-expansion-header"
                  expand-icon-class="filters-expansion-icon"
                  class="filters-expansion"
                >
                  <div class="filters-expansion-content">
                    <div class="filter-grid">
                      <template v-for="campo in filtrosReporte" :key="campo.key">
                        <q-input
                          v-if="campo.type === 'date'"
                          v-model="filtros[campo.key]"
                          outlined
                          dense
                          type="date"
                          :max="fechaActual()"
                          :label="campo.label"
                          class="report-field"
                        />
                        <q-select
                          v-else
                          v-model="filtros[campo.key]"
                          :options="campo.options"
                          emit-value
                          map-options
                          outlined
                          dense
                          clearable
                          :label="campo.label"
                          class="report-field"
                        />
                      </template>
                    </div>

                    <div class="filter-actions">
                      <q-btn flat no-caps icon="restart_alt" label="Limpiar" class="clear-button" @click="limpiarFiltros()" />
                      <q-btn unelevated no-caps icon="filter_alt" label="Aplicar filtros" class="preview-button" @click="aplicarFiltros" />
                    </div>

                    <div class="export-box">
                      <div>
                        <strong>Exportar reporte</strong>
                        <span>Conserva los filtros aplicados en el documento.</span>
                      </div>
                      <div class="export-actions">
                        <q-btn
                          outline
                          no-caps
                          icon="picture_as_pdf"
                          label="PDF"
                          class="pdf-button"
                          :disable="exportacionDeshabilitada"
                          :loading="exportandoReporte && formatoExportando === 'PDF'"
                          @click="exportar('PDF')"
                        />
                        <q-btn
                          outline
                          no-caps
                          icon="table_view"
                          label="Excel"
                          class="excel-button"
                          :disable="exportacionDeshabilitada"
                          :loading="exportandoReporte && formatoExportando === 'Excel'"
                          @click="exportar('Excel')"
                        />
                      </div>
                    </div>
                  </div>
                </q-expansion-item>
              </div>
            </div>

            <div class="results-section">
              <div class="results-header">
            <div>
              <span class="section-kicker">Detalle del reporte</span>
              <h3>Resultados de {{ tituloReporte(reporteSeleccionado.nombre) }}</h3>
              <p>La tabla se adapta al tipo de reporte y conserva los filtros aplicados.</p>
            </div>
            <div class="results-meta">
              <q-chip v-if="esReporteBackend" dense icon="cloud_done" class="sample-chip">
                Datos del sistema
              </q-chip>
              <span>{{ totalRegistros }} registros</span>
            </div>
              </div>

              <q-table
            flat
            bordered
            row-key="id"
            class="results-table"
            :rows="tablaActual.filas"
            :columns="tablaActual.columnas"
            :loading="cargandoReporte"
            v-model:pagination="paginacionTabla"
            :rows-per-page-options="[3, 5, 10, 20]"
            @request="cambiarPagina"
            table-style="min-width: 1250px"
            no-data-label="No existen registros para los filtros seleccionados"
          >
            <template #body-cell-estado="props">
              <q-td :props="props">
                <q-badge rounded :class="['table-status', claseEstado(props.value)]">
                  {{ props.value }}
                </q-badge>
              </q-td>
            </template>

            <template #body-cell-estadoCosto="props">
              <q-td :props="props">
                <q-badge
                  rounded
                  :color="props.row.estadoCostoCompleto ? 'positive' : 'warning'"
                  :label="props.value"
                />
              </q-td>
            </template>

            <template #body-cell-acciones="props">
              <q-td :props="props">
                <q-btn
                  flat
                  dense
                  no-caps
                  color="green-8"
                  icon="visibility"
                  label="Ver productos"
                  @click="verDetalleGanancia(props.row)"
                />
              </q-td>
            </template>

            <template #pagination="scope">
              <div class="table-pagination">
                <q-btn
                  flat
                  round
                  dense
                  icon="first_page"
                  aria-label="Primera página"
                  :disable="scope.isFirstPage"
                  @click="scope.firstPage"
                />
                <q-btn
                  flat
                  round
                  dense
                  icon="chevron_left"
                  aria-label="Página anterior"
                  :disable="scope.isFirstPage"
                  @click="scope.prevPage"
                />
                <span class="page-number">
                  {{ scope.pagination.page }} / {{ scope.pagesNumber }}
                </span>
                <q-btn
                  flat
                  round
                  dense
                  icon="chevron_right"
                  aria-label="Página siguiente"
                  :disable="scope.isLastPage"
                  @click="scope.nextPage"
                />
                <q-btn
                  flat
                  round
                  dense
                  icon="last_page"
                  aria-label="Última página"
                  :disable="scope.isLastPage"
                  @click="scope.lastPage"
                />
              </div>
            </template>
              </q-table>
            </div>
          </q-card-section>
        </q-card>
      </q-dialog>

      <q-dialog v-model="dialogoDetalleGanancia">
        <q-card class="profit-detail-card">
          <q-card-section class="profit-detail-header">
            <div>
              <span class="section-kicker">Detalle de la ganancia</span>
              <h3>Productos de {{ ventaGananciaSeleccionada?.numeroVenta }}</h3>
              <p>La ganancia se calcula individualmente por cada producto vendido.</p>
            </div>
            <q-btn flat round dense icon="close" v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-table
              flat
              bordered
              row-key="id"
              :rows="detalleGanancia"
              :columns="columnasDetalleGanancia"
              :loading="cargandoDetalleGanancia"
              :pagination="{ rowsPerPage: 0 }"
              hide-pagination
              table-style="min-width: 900px"
              class="results-table"
              no-data-label="No existen productos para esta venta"
            />
          </q-card-section>
        </q-card>
      </q-dialog>
    </main>
  </q-page>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useQuasar } from 'quasar'
import {
  cargarCatalogosReporteVenta,
  consultarReporteVenta,
  exportarReporteVenta,
  obtenerDetalleGananciaVenta,
  rutasReporteVenta
} from '../../api/reportes/reportesVenta'
import {
  consultarReporteCaja,
  exportarReporteCaja,
  listarSesionesCaja,
  rutasReporteCaja
} from '../../api/reportes/reportesCaja'
import {
  consultarReporteInventario,
  exportarReporteInventario,
  rutasReporteInventario
} from '../../api/reportes/reportesInventario'
import {
  consultarReporteCompras,
  exportarReporteCompras,
  rutasReporteCompras
} from '../../api/reportes/reportesCompras'
import { consultarReportePersonal, exportarReportePersonal, rutasReportePersonal } from '../../api/reportes/reportesPersonal'
import { consultarReporteCliente, exportarReporteCliente, rutasReporteCliente } from '../../api/reportes/reportesCliente'
import { listarAlmacenes } from '../../api/almacen/almacen'
import { listarUbicacionesPaginado } from '../../api/ubicacionAlmacen/ubicacionAlmacen'
import { listarEmpleados } from '../../api/empleado/empleado'
import { listarProveedores } from '../../api/proveedor/proveedor'

const $q = useQuasar()
const busqueda = ref('')
const categoriaActiva = ref('todos')
const dialogoReporte = ref(false)
const filtrosAbiertos = ref(false)
const cargandoReporte = ref(false)
const exportandoReporte = ref(false)
const formatoExportando = ref(null)
const reporteVentaCargado = ref(false)
const reporteCajaCargado = ref(false)
const reporteInventarioCargado = ref(false)
const reporteComprasCargado = ref(false)
const reportePersonalCargado = ref(false)
const reporteClienteCargado = ref(false)
const filtrosAplicadosVenta = ref({})
const dialogoDetalleGanancia = ref(false)
const cargandoDetalleGanancia = ref(false)
const ventaGananciaSeleccionada = ref(null)
const detalleGanancia = ref([])
const filasReporteVenta = ref([])
const totalReporteVenta = ref(0)
const filasReporteCaja = ref([])
const totalReporteCaja = ref(0)
const filasReporteInventario = ref([])
const totalReporteInventario = ref(0)
const filasReporteCompras = ref([])
const totalReporteCompras = ref(0)
const filasReportePersonal = ref([])
const totalReportePersonal = ref(0)
const filasReporteCliente = ref([])
const totalReporteCliente = ref(0)
const catalogosVentasCargados = ref(false)
const paginacionTabla = ref({
  page: 1,
  rowsPerPage: 3
})

const categorias = [
  { id: 'todos', label: 'Todos los reportes', icon: 'dashboard_customize' },
  { id: 'ventas', label: 'Ventas', icon: 'point_of_sale' },
  { id: 'caja', label: 'Caja', icon: 'account_balance_wallet' },
  { id: 'inventario', label: 'Inventario', icon: 'inventory_2' },
  { id: 'compras', label: 'Compras', icon: 'local_shipping' },
  { id: 'personal', label: 'Personal', icon: 'badge' },
  { id: 'clientes', label: 'Clientes y créditos', icon: 'group' }
]

const reportes = [
  { id: 'ventas-general', categoria: 'ventas', nombre: 'Reporte general de ventas', descripcion: 'Ventas realizadas con cliente, cajero, forma de pago y total.', icon: 'monitoring', color: '#2e7d32', softColor: '#e8f5e9' },
  { id: 'rentabilidad-ventas', categoria: 'ventas', nombre: 'Reporte de ganancias por ventas', descripcion: 'Importe, costo, ganancia y margen obtenido en cada venta.', icon: 'trending_up', color: '#1976a8', softColor: '#e5f3fb' },
  { id: 'productos-mas-vendidos', categoria: 'ventas', nombre: 'Reporte de productos más vendidos', descripcion: 'Productos con mayor cantidad vendida e ingresos generados.', icon: 'trending_up', color: '#2e7d32', softColor: '#e8f5e9' },
  { id: 'productos-menos-vendidos', categoria: 'ventas', nombre: 'Reporte de productos menos vendidos', descripcion: 'Productos con menor movimiento durante el periodo seleccionado.', icon: 'trending_down', color: '#c66a18', softColor: '#fff0df' },
  { id: 'productos-sin-ventas', categoria: 'ventas', nombre: 'Productos sin ventas', descripcion: 'Productos que no registraron ventas durante el periodo consultado.', icon: 'remove_shopping_cart', color: '#c25450', softColor: '#fdeceb' },
  { id: 'ventas-metodo-pago', categoria: 'ventas', nombre: 'Ventas por método de pago', descripcion: 'Operaciones e importes recibidos en efectivo, QR, crédito o mixto.', icon: 'payments', color: '#c66a18', softColor: '#fff0df' },
  { id: 'ventas-cajero', categoria: 'ventas', nombre: 'Ventas por cajero', descripcion: 'Cantidad de operaciones y recaudación registrada por cada cajero.', icon: 'badge', color: '#2e7d32', softColor: '#e8f5e9' },
  { id: 'ventas-contado-credito', categoria: 'ventas', nombre: 'Ventas al contado y a crédito', descripcion: 'Comparación de ventas pagadas y ventas con saldo pendiente.', icon: 'credit_score', color: '#1976a8', softColor: '#e5f3fb' },
  { id: 'ventas-periodo', categoria: 'ventas', nombre: 'Ventas por periodo', descripcion: 'Resumen diario, semanal o mensual de ventas e ingresos.', icon: 'date_range', color: '#6f52a2', softColor: '#f0ebf8' },
  { id: 'ventas-cliente', categoria: 'ventas', nombre: 'Ventas por cliente', descripcion: 'Compras, frecuencia y total gastado por cada cliente.', icon: 'person_search', color: '#1976a8', softColor: '#e5f3fb' },
  { id: 'sesiones-caja', categoria: 'caja', nombre: 'Sesiones de caja', descripcion: 'Aperturas, cierres, montos iniciales y responsables de cada sesión.', icon: 'lock_clock', color: '#2e7d32', softColor: '#e8f5e9' },
  { id: 'movimientos-caja', categoria: 'caja', nombre: 'Movimientos de caja', descripcion: 'Ingresos y egresos registrados con concepto y responsable.', icon: 'swap_vert', color: '#1976a8', softColor: '#e5f3fb' },
  { id: 'conciliacion-caja', categoria: 'caja', nombre: 'Conciliación de caja', descripcion: 'Comparación entre monto esperado y monto contado al cierre.', icon: 'balance', color: '#c25450', softColor: '#fdeceb' },
  { id: 'recaudacion-metodo', categoria: 'caja', nombre: 'Recaudación por método de pago', descripcion: 'Totales de caja distribuidos por cada forma de pago.', icon: 'account_balance', color: '#6f52a2', softColor: '#f0ebf8' },
  { id: 'ventas-sesion-caja', categoria: 'caja', nombre: 'Ventas por sesión de caja', descripcion: 'Ventas y recaudación asociadas a una apertura y cierre específicos.', icon: 'receipt_long', color: '#c66a18', softColor: '#fff0df' },
  { id: 'rendimiento-cajero', categoria: 'caja', nombre: 'Rendimiento por cajero', descripcion: 'Sesiones atendidas, ventas, recaudación y diferencias por cajero.', icon: 'person_search', color: '#2e7d32', softColor: '#e8f5e9' },
  { id: 'existencias', categoria: 'inventario', nombre: 'Existencias actuales', descripcion: 'Stock disponible por producto, almacén y ubicación.', icon: 'warehouse', color: '#2e7d32', softColor: '#e8f5e9' },
  { id: 'stock-minimo', categoria: 'inventario', nombre: 'Stock mínimo', descripcion: 'Productos agotados o que requieren reposición.', icon: 'production_quantity_limits', color: '#c25450', softColor: '#fdeceb' },
  { id: 'productos-agotados', categoria: 'inventario', nombre: 'Productos agotados', descripcion: 'Productos que actualmente no tienen unidades disponibles.', icon: 'remove_shopping_cart', color: '#c25450', softColor: '#fdeceb' },
  { id: 'proximos-vencer', categoria: 'inventario', nombre: 'Productos próximos a vencer', descripcion: 'Lotes cercanos a su fecha de vencimiento.', icon: 'event', color: '#c66a18', softColor: '#fff0df' },
  { id: 'productos-vencidos', categoria: 'inventario', nombre: 'Productos vencidos', descripcion: 'Lotes que superaron su fecha de vencimiento.', icon: 'event_busy', color: '#c25450', softColor: '#fdeceb' },
  { id: 'kardex', categoria: 'inventario', nombre: 'Kardex de producto', descripcion: 'Historial de entradas, salidas y saldo de existencias.', icon: 'receipt_long', color: '#1976a8', softColor: '#e5f3fb' },
  { id: 'movimientos-inventario', categoria: 'inventario', nombre: 'Movimientos de inventario', descripcion: 'Entradas y salidas originadas por las operaciones del sistema.', icon: 'swap_vert', color: '#6f52a2', softColor: '#f0ebf8' },
  { id: 'inventario-almacen', categoria: 'inventario', nombre: 'Inventario por almacén', descripcion: 'Existencias agrupadas por cada almacén registrado.', icon: 'warehouse', color: '#2e7d32', softColor: '#e8f5e9' },
  { id: 'inventario-ubicacion', categoria: 'inventario', nombre: 'Inventario por ubicación', descripcion: 'Productos organizados según su ubicación física.', icon: 'location_on', color: '#1976a8', softColor: '#e5f3fb' },
  { id: 'transferencias-inventario', categoria: 'inventario', nombre: 'Transferencias de inventario', descripcion: 'Traslados de productos entre almacenes y su estado.', icon: 'sync_alt', color: '#c66a18', softColor: '#fff0df' },
  { id: 'inventario-fisico', categoria: 'inventario', nombre: 'Inventario físico', descripcion: 'Comparación entre existencias registradas y cantidades contadas.', icon: 'fact_check', color: '#6f52a2', softColor: '#f0ebf8' },
  { id: 'ajustes-inventario', categoria: 'inventario', nombre: 'Ajustes de inventario', descripcion: 'Aumentos y disminuciones de stock con motivo y responsable.', icon: 'tune', color: '#c25450', softColor: '#fdeceb' },
  { id: 'pedidos-general', categoria: 'compras', nombre: 'Reporte general de pedidos', descripcion: 'Pedidos realizados, proveedor, fecha, total y estado.', icon: 'shopping_cart', color: '#2e7d32', softColor: '#e8f5e9' },
  { id: 'pedidos-pendientes', categoria: 'compras', nombre: 'Pedidos pendientes y parciales', descripcion: 'Pedidos que aún no fueron recibidos completamente.', icon: 'pending_actions', color: '#c66a18', softColor: '#fff0df' },
  { id: 'recepciones-mercaderia', categoria: 'compras', nombre: 'Recepciones de mercadería', descripcion: 'Productos recibidos, lotes, vencimientos y responsables.', icon: 'inventory', color: '#1976a8', softColor: '#e5f3fb' },
  { id: 'comparativo-pedido-recibido', categoria: 'compras', nombre: 'Productos pedidos y recibidos', descripcion: 'Comparación de cantidades solicitadas, recibidas y faltantes.', icon: 'compare_arrows', color: '#6f52a2', softColor: '#f0ebf8' },
  { id: 'compras-proveedor', categoria: 'compras', nombre: 'Compras por proveedor', descripcion: 'Pedidos, productos y montos adquiridos a cada proveedor.', icon: 'local_shipping', color: '#2e7d32', softColor: '#e8f5e9' },
  { id: 'productos-comprados', categoria: 'compras', nombre: 'Productos comprados', descripcion: 'Cantidades recibidas, costos unitarios y costos totales.', icon: 'category', color: '#1976a8', softColor: '#e5f3fb' },
  { id: 'rentabilidad-compras', categoria: 'compras', nombre: 'Rentabilidad estimada', descripcion: 'Costo, precio de venta, margen y ganancia estimada por producto.', icon: 'trending_up', color: '#2e7d32', softColor: '#e8f5e9' },
  { id: 'devoluciones-proveedor', categoria: 'compras', nombre: 'Devoluciones a proveedores', descripcion: 'Productos devueltos, cantidades, motivos y estado.', icon: 'assignment_return', color: '#c25450', softColor: '#fdeceb' },
  { id: 'cumplimiento-proveedores', categoria: 'compras', nombre: 'Cumplimiento de proveedores', descripcion: 'Entregas completas, parciales, retrasos y devoluciones.', icon: 'verified', color: '#6f52a2', softColor: '#f0ebf8' },
  { id: 'compras-periodo', categoria: 'compras', nombre: 'Compras por periodo', descripcion: 'Pedidos y recepciones agrupados por día, semana o mes.', icon: 'date_range', color: '#c66a18', softColor: '#fff0df' },
  { id: 'abastecimiento-general', categoria: 'compras', nombre: 'Reporte general de abastecimiento', descripcion: 'Resumen de pedidos, recepciones, pendientes y devoluciones.', icon: 'analytics', color: '#1976a8', softColor: '#e5f3fb' },
  { id: 'empleados-general', categoria: 'personal', nombre: 'Reporte general de empleados', descripcion: 'Datos del empleado, cargo, fecha de ingreso y estado.', icon: 'groups', color: '#1976a8', softColor: '#e5f3fb' },
  { id: 'empleados-cargo', categoria: 'personal', nombre: 'Empleados por cargo', descripcion: 'Cantidad y detalle del personal asignado a cada cargo.', icon: 'badge', color: '#2e7d32', softColor: '#e8f5e9' },
  { id: 'empleados-estado', categoria: 'personal', nombre: 'Empleados por estado', descripcion: 'Personal habilitado y deshabilitado actualmente.', icon: 'person_check', color: '#6f52a2', softColor: '#f0ebf8' },
  { id: 'contratos-general', categoria: 'personal', nombre: 'Reporte general de contratos', descripcion: 'Tipo, vigencia y estado de los contratos laborales.', icon: 'history_edu', color: '#2e7d32', softColor: '#e8f5e9' },
  { id: 'contratos-vencer', categoria: 'personal', nombre: 'Contratos próximos a vencer', descripcion: 'Contratos que requieren renovación o finalización.', icon: 'event_busy', color: '#c25450', softColor: '#fdeceb' },
  { id: 'asistencia-general', categoria: 'personal', nombre: 'Reporte general de asistencia', descripcion: 'Entradas, salidas, horas trabajadas y estado diario.', icon: 'fact_check', color: '#c66a18', softColor: '#fff0df' },
  { id: 'atrasos-inasistencias', categoria: 'personal', nombre: 'Atrasos e inasistencias', descripcion: 'Llegadas tardías, faltas y justificaciones por empleado.', icon: 'running_with_errors', color: '#c25450', softColor: '#fdeceb' },
  { id: 'horas-trabajadas', categoria: 'personal', nombre: 'Horas trabajadas', descripcion: 'Horas ordinarias registradas por empleado y periodo.', icon: 'schedule', color: '#1976a8', softColor: '#e5f3fb' },
  { id: 'asistencias-justificadas', categoria: 'personal', nombre: 'Asistencias justificadas', descripcion: 'Incidencias justificadas, motivo, fecha y responsable.', icon: 'assignment_turned_in', color: '#2e7d32', softColor: '#e8f5e9' },
  { id: 'permisos-laborales', categoria: 'personal', nombre: 'Permisos laborales', descripcion: 'Solicitudes pendientes, aprobadas, rechazadas o canceladas.', icon: 'event_note', color: '#6f52a2', softColor: '#f0ebf8' },
  { id: 'historial-empleado', categoria: 'personal', nombre: 'Historial del empleado', descripcion: 'Contratos, asistencias y permisos de cada empleado.', icon: 'manage_search', color: '#c66a18', softColor: '#fff0df' },
  { id: 'personal-general', categoria: 'personal', nombre: 'Reporte general de personal', descripcion: 'Resumen de empleados, contratos, asistencias y permisos.', icon: 'analytics', color: '#1976a8', softColor: '#e5f3fb' },
  { id: 'clientes-general', categoria: 'clientes', nombre: 'Reporte general de clientes', descripcion: 'Información, estado y fecha de registro de clientes.', icon: 'groups', color: '#1976a8', softColor: '#e5f3fb' },
  { id: 'compras-cliente', categoria: 'clientes', nombre: 'Compras por cliente', descripcion: 'Cantidad de compras, última compra y total gastado.', icon: 'shopping_bag', color: '#2e7d32', softColor: '#e8f5e9' },
  { id: 'clientes-frecuentes', categoria: 'clientes', nombre: 'Clientes frecuentes', descripcion: 'Clientes con mayor número de compras registradas.', icon: 'workspace_premium', color: '#6f52a2', softColor: '#f0ebf8' },
  { id: 'creditos-general', categoria: 'clientes', nombre: 'Reporte general de créditos', descripcion: 'Importe, plazo, saldo y estado de cada crédito.', icon: 'credit_score', color: '#c66a18', softColor: '#fff0df' },
  { id: 'creditos-pendientes', categoria: 'clientes', nombre: 'Créditos pendientes', descripcion: 'Créditos que todavía tienen un saldo por cobrar.', icon: 'pending_actions', color: '#c66a18', softColor: '#fff0df' },
  { id: 'creditos-vencidos', categoria: 'clientes', nombre: 'Créditos vencidos', descripcion: 'Créditos que superaron su fecha establecida de pago.', icon: 'warning', color: '#c25450', softColor: '#fdeceb' },
  { id: 'pagos-creditos', categoria: 'clientes', nombre: 'Pagos de créditos', descripcion: 'Abonos realizados, fechas, montos y saldos restantes.', icon: 'payments', color: '#2e7d32', softColor: '#e8f5e9' },
  { id: 'saldos-cliente', categoria: 'clientes', nombre: 'Saldos por cliente', descripcion: 'Importe total adeudado actualmente por cada cliente.', icon: 'account_balance_wallet', color: '#1976a8', softColor: '#e5f3fb' },
  { id: 'recuperacion-creditos', categoria: 'clientes', nombre: 'Recuperación de créditos', descripcion: 'Montos otorgados, cobrados y pendientes por periodo.', icon: 'trending_up', color: '#6f52a2', softColor: '#f0ebf8' }
]

const reporteSeleccionado = ref(reportes[0])

const filtros = reactive({
  desde: null,
  hasta: null,
  estado: null,
  agrupacion: null,
  cajero: null,
  cliente: null,
  metodoPago: null,
  tipoVenta: null,
  producto: null,
  categoriaProducto: null,
  marca: null,
  almacen: null,
  ubicacion: null,
  almacenOrigen: null,
  almacenDestino: null,
  estadoStock: null,
  nivelStock: null,
  tipoMovimiento: null,
  lote: null,
  estadoTransferencia: null,
  estadoConteo: null,
  responsable: null,
  margen: null,
  rendimiento: null,
  limite: null,
  caja: null,
  tipoMovimiento: null,
  concepto: null,
  resultado: null,
  sesion: null
})

const opciones = reactive({
  estadosVenta: opcionesSelect([['Confirmada', 'CONFIRMADA'], ['En espera', 'EN_ESPERA'], ['Anulada', 'ANULADA']]),
  cajeros: [],
  clientes: [],
  metodosPago: opcionesSelect([['Efectivo', 'EFECTIVO'], ['QR', 'QR'], ['Crédito', 'CREDITO'], ['Mixto', 'MIXTO']]),
  tiposVenta: opcionesSelect([['Contado', 'CONTADO'], ['Crédito', 'CREDITO']]),
  productos: [],
  categorias: [],
  marcas: [],
  margenes: opcionesSelect([['Menor a 10 %', 'MENOR_10'], ['Entre 10 % y 25 %', '10_25'], ['Mayor a 25 %', 'MAYOR_25']]),
  rendimiento: [],
  limites: [],
  cajas: [],
  estadosCaja: ['Abierta', 'Cerrada'],
  movimientos: ['Ingreso', 'Egreso'],
  conceptos: ['Fondo inicial', 'Venta', 'Gasto operativo', 'Retiro'],
  resultados: ['Sin diferencia', 'Faltante', 'Sobrante'],
  sesiones: [],
  agrupaciones: opcionesSelect([['Por día', 'DIA'], ['Por semana', 'SEMANA'], ['Por mes', 'MES']]),
  estadosCredito: opcionesSelect([['Pendiente', 'PENDIENTE'], ['Parcial', 'PARCIAL'], ['Pagado', 'PAGADO'], ['Anulado', 'ANULADO']]),
  almacenes: [],
  ubicaciones: [],
  estadosStock: opcionesSelect([['Disponible', 'DISPONIBLE'], ['Stock bajo', 'BAJO'], ['Agotado', 'AGOTADO']]),
  nivelesStock: opcionesSelect([['Stock normal', 'NORMAL'], ['Stock bajo', 'BAJO'], ['Agotado', 'AGOTADO']]),
  diasVencimiento: [],
  lotes: [],
  movimientosInventario: [],
  motivosInventario: [],
  responsables: [],
  estadosTransferencia: opcionesSelect([['Borrador', 'BORRADOR'], ['Pendiente', 'PENDIENTE'], ['En proceso', 'EN_PROCESO'], ['Completada', 'COMPLETADA'], ['Cancelada', 'CANCELADA']]),
  estadosConteo: opcionesSelect([['Borrador', 'BORRADOR'], ['En conteo', 'EN_CONTEO'], ['En revisión', 'EN_REVISION'], ['Aprobado', 'APROBADO'], ['Cancelado', 'CANCELADO']]),
  resultadosInventario: opcionesSelect([['Pendiente de conteo', 'PENDIENTE'], ['Con diferencia', 'CON_DIFERENCIA'], ['Sin diferencia', 'SIN_DIFERENCIA']]),
  tiposAjuste: opcionesSelect([['Entrada', 'ENTRADA'], ['Salida', 'SALIDA']]),
  motivosAjuste: opcionesSelect([['Inventario inicial', 'INVENTARIO_INICIAL'], ['Producto encontrado', 'PRODUCTO_ENCONTRADO'], ['Producto vencido', 'PRODUCTO_VENCIDO'], ['Merma', 'MERMA']]),
  estadosAjuste: opcionesSelect([['Borrador', 'BORRADOR'], ['Pendiente de aprobación', 'PENDIENTE_APROBACION'], ['Aprobado', 'APROBADO'], ['Aplicado', 'APLICADO'], ['Rechazado', 'RECHAZADO'], ['Cancelado', 'CANCELADO']]),
  proveedores: [],
  estadosPedido: opcionesSelect([
    ['Borrador', 'BORRADOR'], ['Pendiente', 'PENDIENTE'], ['Aprobado', 'APROBADO'],
    ['Enviado', 'ENVIADO'], ['Parcialmente recibido', 'PARCIALMENTE_RECIBIDO'], ['Recibido', 'RECIBIDO'], ['Cancelado', 'CANCELADO']
  ]),
  estadosRecepcion: [],
  numerosPedido: [],
  numerosRecepcion: [],
  resultadosRecepcion: [],
  motivosDevolucion: [],
  estadosDevolucion: [],
  cumplimientoProveedor: [],
  tiposOperacionCompra: [],
  estadosOperacionCompra: [],
  empleados: [],
  cargos: [],
  estadosEmpleado: [],
  tiposContrato: [],
  estadosContrato: [],
  estadosAsistencia: [],
  incidenciasAsistencia: [],
  estadosJustificacion: [],
  motivosJustificacion: [],
  tiposPermisoLaboral: [],
  estadosPermisoLaboral: [],
  areasHistorial: [],
  estadosCliente: [],
  nivelesFrecuencia: [],
  plazosCredito: [],
  creditos: [],
  diasMora: []
})

const filtroFecha = [
  { key: 'desde', label: 'Fecha inicial', type: 'date' },
  { key: 'hasta', label: 'Fecha final', type: 'date' }
]

const filtrosPorReporte = {
  'ventas-general': [campo('estado', 'Estado de venta', opciones.estadosVenta), campo('cajero', 'Cajero', opciones.cajeros), campo('cliente', 'Cliente', opciones.clientes), campo('metodoPago', 'Método de pago', opciones.metodosPago), campo('tipoVenta', 'Tipo de venta', opciones.tiposVenta)],
  'rentabilidad-ventas': [campo('producto', 'Producto', opciones.productos), campo('categoriaProducto', 'Categoría', opciones.categorias), campo('marca', 'Marca', opciones.marcas), campo('cajero', 'Cajero', opciones.cajeros), campo('margen', 'Margen de ganancia', opciones.margenes)],
  'productos-mas-vendidos': [campo('producto', 'Producto', opciones.productos), campo('categoriaProducto', 'Categoría', opciones.categorias), campo('marca', 'Marca', opciones.marcas)],
  'productos-menos-vendidos': [campo('producto', 'Producto', opciones.productos), campo('categoriaProducto', 'Categoría', opciones.categorias), campo('marca', 'Marca', opciones.marcas)],
  'productos-sin-ventas': [campo('producto', 'Producto', opciones.productos), campo('categoriaProducto', 'Categoría', opciones.categorias), campo('marca', 'Marca', opciones.marcas)],
  'ventas-metodo-pago': [campo('metodoPago', 'Método de pago', opciones.metodosPago), campo('cajero', 'Cajero', opciones.cajeros), campo('caja', 'Caja', opciones.cajas)],
  'ventas-cajero': [campo('cajero', 'Cajero', opciones.cajeros), campo('caja', 'Caja', opciones.cajas), campo('estado', 'Estado de venta', opciones.estadosVenta)],
  'ventas-contado-credito': [campo('tipoVenta', 'Tipo de venta', opciones.tiposVenta), campo('cliente', 'Cliente', opciones.clientes), campo('estado', 'Estado del crédito', opciones.estadosCredito), campo('cajero', 'Cajero', opciones.cajeros)],
  'ventas-periodo': [campo('agrupacion', 'Agrupar por', opciones.agrupaciones), campo('cajero', 'Cajero', opciones.cajeros), campo('metodoPago', 'Método de pago', opciones.metodosPago)],
  'ventas-cliente': [campo('cliente', 'Cliente', opciones.clientes), campo('tipoVenta', 'Tipo de venta', opciones.tiposVenta), campo('estado', 'Estado de venta', opciones.estadosVenta)],
  existencias: [campo('almacen', 'Almacén', opciones.almacenes), campo('ubicacion', 'Ubicación', opciones.ubicaciones), campo('producto', 'Producto', opciones.productos), campo('categoriaProducto', 'Categoría', opciones.categorias), campo('marca', 'Marca', opciones.marcas), campo('estadoStock', 'Estado del stock', opciones.estadosStock)],
  'stock-minimo': [campo('almacen', 'Almacén', opciones.almacenes), campo('ubicacion', 'Ubicación', opciones.ubicaciones), campo('producto', 'Producto', opciones.productos), campo('categoriaProducto', 'Categoría', opciones.categorias), campo('nivelStock', 'Nivel de stock', opciones.nivelesStock)],
  'productos-agotados': [campo('almacen', 'Almacén', opciones.almacenes), campo('ubicacion', 'Ubicación', opciones.ubicaciones), campo('categoriaProducto', 'Categoría', opciones.categorias), campo('marca', 'Marca', opciones.marcas)],
  'proximos-vencer': [campo('almacen', 'Almacén', opciones.almacenes), campo('producto', 'Producto', opciones.productos), campo('categoriaProducto', 'Categoría', opciones.categorias), campo('diasVencimiento', 'Días para vencer', opciones.diasVencimiento)],
  'productos-vencidos': [campo('almacen', 'Almacén', opciones.almacenes), campo('producto', 'Producto', opciones.productos), campo('categoriaProducto', 'Categoría', opciones.categorias), campo('lote', 'Lote', opciones.lotes)],
  kardex: [campo('producto', 'Producto', opciones.productos), campo('almacen', 'Almacén', opciones.almacenes), campo('tipoMovimiento', 'Tipo de movimiento', opciones.movimientosInventario), campo('lote', 'Número de lote', opciones.lotes)],
  'movimientos-inventario': [campo('almacen', 'Almacén', opciones.almacenes), campo('producto', 'Producto', opciones.productos), campo('tipoMovimiento', 'Tipo de movimiento', opciones.movimientosInventario), campo('motivo', 'Motivo', opciones.motivosInventario), campo('responsable', 'Responsable', opciones.responsables)],
  'inventario-almacen': [campo('almacen', 'Almacén', opciones.almacenes), campo('producto', 'Producto', opciones.productos), campo('categoriaProducto', 'Categoría', opciones.categorias), campo('marca', 'Marca', opciones.marcas), campo('estadoStock', 'Estado del stock', opciones.estadosStock)],
  'inventario-ubicacion': [campo('almacen', 'Almacén', opciones.almacenes), campo('ubicacion', 'Ubicación', opciones.ubicaciones), campo('producto', 'Producto', opciones.productos), campo('categoriaProducto', 'Categoría', opciones.categorias)],
  'transferencias-inventario': [campo('almacenOrigen', 'Almacén de origen', opciones.almacenes), campo('almacenDestino', 'Almacén de destino', opciones.almacenes), campo('producto', 'Producto', opciones.productos), campo('estadoTransferencia', 'Estado de transferencia', opciones.estadosTransferencia)],
  'inventario-fisico': [campo('almacen', 'Almacén', opciones.almacenes), campo('responsable', 'Responsable', opciones.responsables), campo('estadoConteo', 'Estado del conteo', opciones.estadosConteo), campo('resultado', 'Resultado', opciones.resultadosInventario)],
  'ajustes-inventario': [campo('almacen', 'Almacén', opciones.almacenes), campo('producto', 'Producto', opciones.productos), campo('tipoAjuste', 'Tipo de ajuste', opciones.tiposAjuste), campo('motivoAjuste', 'Motivo', opciones.motivosAjuste), campo('estadoAjuste', 'Estado', opciones.estadosAjuste), campo('responsable', 'Responsable', opciones.responsables)],
  'pedidos-general': [campo('proveedor', 'Proveedor', opciones.proveedores), campo('estadoPedido', 'Estado del pedido', opciones.estadosPedido), campo('responsable', 'Responsable', opciones.responsables)],
  'pedidos-pendientes': [campo('proveedor', 'Proveedor', opciones.proveedores), campo('estadoPedido', 'Estado', ['Pendiente', 'Parcial']), campo('producto', 'Producto', opciones.productos)],
  'recepciones-mercaderia': [campo('proveedor', 'Proveedor', opciones.proveedores), campo('recepcion', 'Número de recepción', opciones.numerosRecepcion), campo('producto', 'Producto', opciones.productos), campo('almacen', 'Almacén', opciones.almacenes), campo('estadoRecepcion', 'Estado de recepción', opciones.estadosRecepcion)],
  'comparativo-pedido-recibido': [campo('proveedor', 'Proveedor', opciones.proveedores), campo('pedido', 'Número de pedido', opciones.numerosPedido), campo('producto', 'Producto', opciones.productos), campo('resultadoRecepcion', 'Resultado', opciones.resultadosRecepcion)],
  'compras-proveedor': [campo('proveedor', 'Proveedor', opciones.proveedores), campo('producto', 'Producto', opciones.productos), campo('categoriaProducto', 'Categoría', opciones.categorias), campo('estadoPedido', 'Estado del pedido', opciones.estadosPedido)],
  'productos-comprados': [campo('producto', 'Producto', opciones.productos), campo('categoriaProducto', 'Categoría', opciones.categorias), campo('marca', 'Marca', opciones.marcas), campo('proveedor', 'Proveedor', opciones.proveedores), campo('almacen', 'Almacén', opciones.almacenes)],
  'rentabilidad-compras': [campo('producto', 'Producto', opciones.productos), campo('categoriaProducto', 'Categoría', opciones.categorias), campo('marca', 'Marca', opciones.marcas), campo('proveedor', 'Proveedor', opciones.proveedores), campo('margen', 'Margen de ganancia', opciones.margenes)],
  'devoluciones-proveedor': [campo('proveedor', 'Proveedor', opciones.proveedores), campo('producto', 'Producto', opciones.productos), campo('motivoDevolucion', 'Motivo de devolución', opciones.motivosDevolucion), campo('estadoDevolucion', 'Estado de devolución', opciones.estadosDevolucion)],
  'cumplimiento-proveedores': [campo('proveedor', 'Proveedor', opciones.proveedores), campo('cumplimiento', 'Resultado', opciones.cumplimientoProveedor)],
  'compras-periodo': [campo('agrupacion', 'Agrupar por', opciones.agrupaciones), campo('proveedor', 'Proveedor', opciones.proveedores), campo('categoriaProducto', 'Categoría', opciones.categorias), campo('estadoPedido', 'Estado del pedido', opciones.estadosPedido)],
  'abastecimiento-general': [campo('proveedor', 'Proveedor', opciones.proveedores), campo('producto', 'Producto', opciones.productos), campo('almacen', 'Almacén', opciones.almacenes), campo('tipoOperacion', 'Tipo de operación', opciones.tiposOperacionCompra), campo('estadoOperacion', 'Estado de operación', opciones.estadosOperacionCompra)],
  'empleados-general': [campo('empleado', 'Empleado', opciones.empleados), campo('cargo', 'Cargo', opciones.cargos), campo('estadoEmpleado', 'Estado del empleado', opciones.estadosEmpleado)],
  'empleados-cargo': [campo('cargo', 'Cargo', opciones.cargos), campo('estadoEmpleado', 'Estado del empleado', opciones.estadosEmpleado)],
  'empleados-estado': [campo('estadoEmpleado', 'Estado del empleado', opciones.estadosEmpleado), campo('cargo', 'Cargo', opciones.cargos)],
  'contratos-general': [campo('empleado', 'Empleado', opciones.empleados), campo('tipoContrato', 'Tipo de contrato', opciones.tiposContrato), campo('estadoContrato', 'Estado del contrato', opciones.estadosContrato)],
  'contratos-vencer': [campo('empleado', 'Empleado', opciones.empleados), campo('tipoContrato', 'Tipo de contrato', opciones.tiposContrato), campo('estadoContrato', 'Estado del contrato', ['Próximo a vencer', 'Vencido'])],
  'asistencia-general': [campo('empleado', 'Empleado', opciones.empleados), campo('cargo', 'Cargo', opciones.cargos), campo('estadoAsistencia', 'Estado de asistencia', opciones.estadosAsistencia)],
  'atrasos-inasistencias': [campo('empleado', 'Empleado', opciones.empleados), campo('incidencia', 'Tipo de incidencia', opciones.incidenciasAsistencia), campo('estadoJustificacion', 'Estado de justificación', opciones.estadosJustificacion)],
  'horas-trabajadas': [campo('empleado', 'Empleado', opciones.empleados), campo('cargo', 'Cargo', opciones.cargos), campo('agrupacion', 'Agrupar por', opciones.agrupaciones)],
  'asistencias-justificadas': [campo('empleado', 'Empleado', opciones.empleados), campo('motivoJustificacion', 'Motivo', opciones.motivosJustificacion), campo('responsable', 'Responsable', opciones.responsables)],
  'permisos-laborales': [campo('empleado', 'Empleado', opciones.empleados), campo('tipoPermiso', 'Tipo de permiso', opciones.tiposPermisoLaboral), campo('estadoPermiso', 'Estado de solicitud', opciones.estadosPermisoLaboral)],
  'historial-empleado': [campo('empleado', 'Empleado', opciones.empleados), campo('areaHistorial', 'Tipo de registro', opciones.areasHistorial)],
  'personal-general': [campo('empleado', 'Empleado', opciones.empleados), campo('cargo', 'Cargo', opciones.cargos), campo('estadoEmpleado', 'Estado del empleado', opciones.estadosEmpleado)],
  'clientes-general': [campo('cliente', 'Cliente', opciones.clientes), campo('estadoCliente', 'Estado del cliente', opciones.estadosCliente)],
  'compras-cliente': [campo('cliente', 'Cliente', opciones.clientes), campo('tipoVenta', 'Tipo de venta', opciones.tiposVenta), campo('metodoPago', 'Método de pago', opciones.metodosPago)],
  'clientes-frecuentes': [campo('cliente', 'Cliente', opciones.clientes), campo('frecuencia', 'Nivel de frecuencia', opciones.nivelesFrecuencia), campo('limite', 'Cantidad de resultados', opciones.limites)],
  'creditos-general': [campo('cliente', 'Cliente', opciones.clientes), campo('estadoCredito', 'Estado del crédito', opciones.estadosCredito), campo('plazoCredito', 'Plazo', opciones.plazosCredito)],
  'creditos-pendientes': [campo('cliente', 'Cliente', opciones.clientes), campo('estadoCredito', 'Estado del crédito', ['Pendiente', 'Parcial']), campo('plazoCredito', 'Plazo', opciones.plazosCredito)],
  'creditos-vencidos': [campo('cliente', 'Cliente', opciones.clientes), campo('diasMora', 'Días de mora', opciones.diasMora), campo('plazoCredito', 'Plazo', opciones.plazosCredito)],
  'pagos-creditos': [campo('cliente', 'Cliente', opciones.clientes), campo('credito', 'Número de crédito', opciones.creditos), campo('metodoPago', 'Método de pago', opciones.metodosPago)],
  'saldos-cliente': [campo('cliente', 'Cliente', opciones.clientes), campo('estadoCredito', 'Estado del crédito', opciones.estadosCredito)],
  'recuperacion-creditos': [campo('agrupacion', 'Agrupar por', opciones.agrupaciones), campo('cliente', 'Cliente', opciones.clientes), campo('estadoCredito', 'Estado del crédito', opciones.estadosCredito)],
  'sesiones-caja': [campo('caja', 'Caja', opciones.cajas), campo('cajero', 'Cajero', opciones.cajeros), campo('estado', 'Estado de sesión', opciones.estadosCaja)],
  'movimientos-caja': [campo('caja', 'Caja', opciones.cajas), campo('cajero', 'Responsable', opciones.cajeros), campo('tipoMovimiento', 'Tipo de movimiento', opciones.movimientos), campo('concepto', 'Concepto', opciones.conceptos)],
  'conciliacion-caja': [campo('caja', 'Caja', opciones.cajas), campo('cajero', 'Cajero', opciones.cajeros), campo('resultado', 'Resultado', opciones.resultados)],
  'recaudacion-metodo': [campo('caja', 'Caja', opciones.cajas), campo('cajero', 'Cajero', opciones.cajeros), campo('metodoPago', 'Método de pago', opciones.metodosPago)],
  'ventas-sesion-caja': [campo('sesion', 'Sesión de caja', opciones.sesiones), campo('caja', 'Caja', opciones.cajas), campo('cajero', 'Cajero', opciones.cajeros), campo('metodoPago', 'Método de pago', opciones.metodosPago)],
  'rendimiento-cajero': [campo('cajero', 'Cajero', opciones.cajeros), campo('caja', 'Caja', opciones.cajas), campo('agrupacion', 'Agrupar por', opciones.agrupaciones)]
}

const reportesSinFecha = new Set([
  'empleados-general',
  'empleados-cargo',
  'empleados-estado'
])

const filtrosReporte = computed(() => [
  ...(reportesSinFecha.has(reporteSeleccionado.value.id) ? [] : filtroFecha),
  ...(filtrosPorReporte[reporteSeleccionado.value.id] || [campo('estado', 'Estado', ['Activo', 'Pendiente', 'Finalizado'])])
])

const categoriaActual = computed(() => categorias.find(item => item.id === categoriaActiva.value) || categorias[0])

const reportesFiltrados = computed(() => {
  const termino = normalizar(busqueda.value)
  return reportes.filter((reporte) => {
    const coincideCategoria = categoriaActiva.value === 'todos' || reporte.categoria === categoriaActiva.value
    const coincideTexto = !termino || normalizar(`${reporte.nombre} ${reporte.descripcion}`).includes(termino)
    return coincideCategoria && coincideTexto
  })
})

const columnasReporteVenta = {
  'ventas-general': columnasApi([['fecha', 'Fecha'], ['numeroVenta', 'N.º venta'], ['cliente', 'Cliente'], ['cajero', 'Cajero'], ['tipoVenta', 'Tipo'], ['metodoPago', 'Método de pago'], ['estado', 'Estado'], ['total', 'Total', 'right']]),
  'rentabilidad-ventas': columnasApi([['numeroVenta', 'N.º venta'], ['fecha', 'Fecha'], ['cajero', 'Cajero'], ['cantidadProductos', 'Productos', 'right'], ['totalVendido', 'Total cobrado', 'right'], ['costoTotal', 'Costo de productos', 'right'], ['ganancia', 'Ganancia neta', 'right'], ['margenPorcentaje', 'Margen', 'right'], ['estadoCosto', 'Estado del costo'], ['acciones', 'Acciones', 'center']]),
  'productos-mas-vendidos': columnasApi([['codigo', 'Código'], ['producto', 'Producto'], ['categoria', 'Categoría'], ['cantidadVendida', 'Cantidad', 'right'], ['ultimaVenta', 'Última venta'], ['importeGenerado', 'Importe', 'right']]),
  'productos-menos-vendidos': columnasApi([['codigo', 'Código'], ['producto', 'Producto'], ['categoria', 'Categoría'], ['cantidadVendida', 'Cantidad', 'right'], ['ultimaVenta', 'Última venta'], ['importeGenerado', 'Importe', 'right']]),
  'productos-sin-ventas': columnasApi([['codigo', 'Código'], ['producto', 'Producto'], ['categoria', 'Categoría'], ['stockActual', 'Stock actual', 'right'], ['ultimaVenta', 'Última venta'], ['diasSinVenta', 'Días sin venta', 'right']]),
  'ventas-metodo-pago': columnasApi([['metodoPago', 'Método de pago'], ['cantidadOperaciones', 'Operaciones', 'right'], ['participacionPorcentaje', 'Participación', 'right'], ['totalRecibido', 'Total recibido', 'right']]),
  'ventas-cajero': columnasApi([['cajero', 'Cajero'], ['caja', 'Caja'], ['cantidadVentas', 'Ventas', 'right'], ['ticketPromedio', 'Ticket promedio', 'right'], ['totalVendido', 'Total vendido', 'right']]),
  'ventas-contado-credito': columnasApi([['tipoVenta', 'Tipo de venta'], ['cantidadOperaciones', 'Operaciones', 'right'], ['importeTotal', 'Importe total', 'right'], ['montoCobrado', 'Cobrado', 'right'], ['saldoPendiente', 'Saldo pendiente', 'right']]),
  'ventas-periodo': columnasApi([['periodo', 'Periodo'], ['cantidadVentas', 'Ventas', 'right'], ['ventasContado', 'Contado', 'right'], ['ventasCredito', 'Crédito', 'right'], ['totalVendido', 'Total vendido', 'right'], ['ticketPromedio', 'Ticket promedio', 'right']]),
  'ventas-cliente': columnasApi([['documento', 'Documento'], ['cliente', 'Cliente'], ['cantidadCompras', 'Compras', 'right'], ['ultimaCompra', 'Última compra'], ['totalContado', 'Contado', 'right'], ['totalCredito', 'Crédito', 'right'], ['totalGastado', 'Total gastado', 'right']])
}

const columnasReporteCaja = {
  'sesiones-caja': columnasApi([['sesion', 'Sesión'], ['fecha', 'Fecha'], ['caja', 'Caja'], ['cajero', 'Cajero'], ['apertura', 'Monto inicial', 'right'], ['cierre', 'Monto contado', 'right'], ['estado', 'Estado']]),
  'movimientos-caja': columnasApi([['fecha', 'Fecha y hora'], ['sesion', 'Sesión'], ['tipo', 'Tipo'], ['concepto', 'Concepto'], ['responsable', 'Responsable'], ['monto', 'Monto', 'right']]),
  'conciliacion-caja': columnasApi([['sesion', 'Sesión'], ['fecha', 'Fecha'], ['caja', 'Caja'], ['cajero', 'Cajero'], ['esperado', 'Monto esperado', 'right'], ['contado', 'Monto contado', 'right'], ['diferencia', 'Diferencia', 'right'], ['resultado', 'Resultado']]),
  'recaudacion-metodo': columnasApi([['metodo', 'Método de pago'], ['operaciones', 'Operaciones', 'right'], ['sesiones', 'Sesiones', 'right'], ['porcentaje', 'Participación', 'right'], ['total', 'Recaudación', 'right']]),
  'ventas-sesion-caja': columnasApi([['sesion', 'Sesión'], ['fecha', 'Fecha'], ['caja', 'Caja'], ['cajero', 'Cajero'], ['ventas', 'Ventas', 'right'], ['recaudacion', 'Recaudación', 'right'], ['estado', 'Estado']]),
  'rendimiento-cajero': columnasApi([['cajero', 'Cajero'], ['sesiones', 'Sesiones', 'right'], ['ventas', 'Ventas', 'right'], ['recaudacion', 'Recaudación', 'right'], ['promedio', 'Promedio por venta', 'right'], ['diferencia', 'Diferencia', 'right']])
}
const columnasReporteInventario = {
  existencias: columnasApi([['codigo', 'Código'], ['producto', 'Producto'], ['categoria', 'Categoría'], ['almacen', 'Almacén'], ['ubicacion', 'Ubicación'], ['stock', 'Stock', 'right']]),
  'stock-minimo': columnasApi([['codigo', 'Código'], ['producto', 'Producto'], ['categoria', 'Categoría'], ['minimo', 'Stock mínimo', 'right'], ['stock', 'Stock actual', 'right']]),
  'productos-agotados': columnasApi([['codigo', 'Código'], ['producto', 'Producto'], ['categoria', 'Categoría'], ['minimo', 'Stock mínimo', 'right'], ['stock', 'Stock actual', 'right']]),
  'proximos-vencer': columnasApi([['lote', 'Lote'], ['codigo', 'Código'], ['producto', 'Producto'], ['almacen', 'Almacén'], ['vencimiento', 'Vencimiento'], ['cantidad', 'Cantidad', 'right']]),
  'productos-vencidos': columnasApi([['lote', 'Lote'], ['codigo', 'Código'], ['producto', 'Producto'], ['almacen', 'Almacén'], ['vencimiento', 'Vencimiento'], ['cantidad', 'Cantidad', 'right']]),
  kardex: columnasApi([['fecha', 'Fecha'], ['referencia', 'Referencia'], ['movimiento', 'Movimiento'], ['codigo', 'Código'], ['producto', 'Producto'], ['cantidad', 'Cantidad', 'right'], ['saldo', 'Saldo', 'right'], ['ubicacion', 'Ubicación'], ['responsable', 'Responsable']])
}
columnasReporteInventario['inventario-almacen'] = columnasReporteInventario.existencias
columnasReporteInventario['inventario-ubicacion'] = columnasReporteInventario.existencias
columnasReporteInventario['movimientos-inventario'] = columnasReporteInventario.kardex
columnasReporteInventario['transferencias-inventario'] = columnasApi([['fecha', 'Fecha'], ['codigo', 'Transferencia'], ['codigoProducto', 'Código producto'], ['producto', 'Producto'], ['origen', 'Almacén origen'], ['destino', 'Almacén destino'], ['cantidad', 'Cantidad', 'right'], ['estado', 'Estado'], ['responsable', 'Responsable']])
columnasReporteInventario['inventario-fisico'] = columnasApi([['codigo', 'Código'], ['fecha', 'Fecha'], ['estado', 'Estado'], ['almacen', 'Almacén'], ['ubicacion', 'Ubicación'], ['codigoProducto', 'Código producto'], ['producto', 'Producto'], ['cantidadSistema', 'Cantidad sistema', 'right'], ['cantidadFisica', 'Cantidad física', 'right'], ['diferencia', 'Diferencia', 'right'], ['responsable', 'Responsable']])
columnasReporteInventario['ajustes-inventario'] = columnasApi([['codigo', 'Ajuste'], ['fecha', 'Fecha'], ['tipo', 'Tipo'], ['motivo', 'Motivo'], ['estado', 'Estado'], ['codigoProducto', 'Código producto'], ['producto', 'Producto'], ['almacen', 'Almacén'], ['cantidad', 'Cantidad', 'right'], ['stockAnterior', 'Stock anterior', 'right'], ['stockResultante', 'Stock resultante', 'right'], ['valorTotal', 'Valor total', 'right'], ['responsable', 'Responsable']])
const columnasReporteCompras = {
  'pedidos-general': columnasApi([['fecha', 'Fecha'], ['proveedor', 'Proveedor'], ['estado', 'Estado'], ['entrega', 'Entrega estimada'], ['productos', 'Productos', 'right']]),
  'pedidos-pendientes': columnasApi([['fecha', 'Fecha'], ['proveedor', 'Proveedor'], ['estado', 'Estado'], ['entrega', 'Entrega estimada'], ['productos', 'Productos', 'right']]),
  'recepciones-mercaderia': columnasApi([['fecha', 'Fecha'], ['proveedor', 'Proveedor'], ['estado', 'Estado'], ['productos', 'Productos', 'right']]),
  'comparativo-pedido-recibido': columnasApi([['codigo', 'Código'], ['producto', 'Producto'], ['solicitado', 'Solicitado', 'right'], ['recibido', 'Recibido', 'right'], ['faltante', 'Faltante', 'right']])
  , 'compras-proveedor': columnasApi([['proveedor', 'Proveedor'], ['pedidos', 'Pedidos', 'right'], ['total', 'Total comprado', 'right']])
  , 'productos-comprados': columnasApi([['codigo', 'Código'], ['producto', 'Producto'], ['cantidad', 'Cantidad', 'right'], ['costo', 'Costo total', 'right']])
  , 'rentabilidad-compras': columnasApi([['codigo', 'Código'], ['producto', 'Producto'], ['costo', 'Costo promedio', 'right'], ['precio', 'Precio venta', 'right'], ['ganancia', 'Ganancia estimada', 'right']])
  , 'devoluciones-proveedor': columnasApi([['fecha', 'Fecha'], ['proveedor', 'Proveedor'], ['estado', 'Estado'], ['motivo', 'Motivo'], ['cantidad', 'Cantidad', 'right']])
  , 'cumplimiento-proveedores': columnasApi([['proveedor', 'Proveedor'], ['pedidos', 'Pedidos', 'right'], ['recepciones', 'Recepciones', 'right']])
  , 'compras-periodo': columnasApi([['fecha', 'Fecha'], ['pedidos', 'Pedidos', 'right'], ['proveedores', 'Proveedores', 'right']])
  , 'abastecimiento-general': columnasApi([['fecha', 'Fecha'], ['pedidos', 'Pedidos', 'right'], ['recepciones', 'Recepciones', 'right'], ['devoluciones', 'Devoluciones', 'right']])
}
const columnasReportePersonal = columnasApi([['fecha', 'Fecha'], ['empleado', 'Empleado'], ['estado', 'Estado'], ['cargo', 'Detalle']])
const columnasReporteCliente = columnasApi([['cliente', 'Cliente'], ['estado', 'Estado'], ['importe', 'Importe', 'right'], ['saldo', 'Saldo', 'right'], ['fecha', 'Fecha']])

const esReporteVenta = computed(() => Boolean(rutasReporteVenta[reporteSeleccionado.value.id]))
const esReporteCaja = computed(() => Boolean(rutasReporteCaja[reporteSeleccionado.value.id]))
const esReporteInventario = computed(() => Boolean(rutasReporteInventario[reporteSeleccionado.value.id]))
const esReporteCompras = computed(() => Boolean(rutasReporteCompras[reporteSeleccionado.value.id]))
const esReportePersonal = computed(() => Boolean(rutasReportePersonal[reporteSeleccionado.value.id]))
const esReporteCliente = computed(() => Boolean(rutasReporteCliente[reporteSeleccionado.value.id]))
const esReporteBackend = computed(() => esReporteVenta.value || esReporteCaja.value || esReporteInventario.value || esReporteCompras.value || esReportePersonal.value || esReporteCliente.value)
const reporteBackendCargado = computed(() => esReporteVenta.value ? reporteVentaCargado.value : esReporteCaja.value ? reporteCajaCargado.value : esReporteInventario.value ? reporteInventarioCargado.value : esReporteCompras.value ? reporteComprasCargado.value : esReportePersonal.value ? reportePersonalCargado.value : reporteClienteCargado.value)
const exportacionDeshabilitada = computed(() =>
  esReporteBackend.value && (
    !reporteBackendCargado.value
    || (esReporteVenta.value ? totalReporteVenta.value : esReporteCaja.value ? totalReporteCaja.value : esReporteInventario.value ? totalReporteInventario.value : esReporteCompras.value ? totalReporteCompras.value : esReportePersonal.value ? totalReportePersonal.value : totalReporteCliente.value) === 0
    || cargandoReporte.value
    || exportandoReporte.value
  )
)
const tablaActual = computed(() => esReporteVenta.value
  ? { columnas: columnasReporteVenta[reporteSeleccionado.value.id], filas: filasReporteVenta.value }
  : esReporteCaja.value
    ? { columnas: columnasReporteCaja[reporteSeleccionado.value.id], filas: filasReporteCaja.value }
    : esReporteInventario.value
      ? { columnas: columnasReporteInventario[reporteSeleccionado.value.id], filas: filasReporteInventario.value }
    : esReporteCompras.value
      ? { columnas: columnasReporteCompras[reporteSeleccionado.value.id], filas: filasReporteCompras.value }
    : esReportePersonal.value
      ? { columnas: columnasReportePersonal, filas: filasReportePersonal.value }
    : esReporteCliente.value
      ? { columnas: columnasReporteCliente, filas: filasReporteCliente.value }
  : { columnas: [], filas: [] })
const totalRegistros = computed(() => esReporteVenta.value ? totalReporteVenta.value : esReporteCaja.value ? totalReporteCaja.value : esReporteInventario.value ? totalReporteInventario.value : esReporteCompras.value ? totalReporteCompras.value : esReportePersonal.value ? totalReportePersonal.value : esReporteCliente.value ? totalReporteCliente.value : tablaActual.value.filas.length)
const columnasDetalleGanancia = columnasApi([
  ['codigo', 'Código'], ['producto', 'Producto'], ['cantidad', 'Cantidad', 'right'],
  ['precioVentaUnitario', 'Precio de venta', 'right'], ['costoUnitario', 'Costo unitario', 'right'],
  ['totalVendido', 'Total vendido', 'right'], ['costoTotal', 'Costo total', 'right'],
  ['ganancia', 'Ganancia', 'right']
])

function normalizar(valor) {
  return String(valor || '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase().trim()
}

function tituloReporte(nombre) {
  const texto = String(nombre || '').trim()
  if (!texto || /^reporte\b/i.test(texto)) return texto
  return `Reporte de ${texto.charAt(0).toLowerCase()}${texto.slice(1)}`
}

function cantidadCategoria(id) {
  return id === 'todos' ? reportes.length : reportes.filter(reporte => reporte.categoria === id).length
}

async function seleccionarReporte(reporte) {
  reporteSeleccionado.value = reporte
  reporteVentaCargado.value = false
  reporteCajaCargado.value = false
  reporteInventarioCargado.value = false
  reporteComprasCargado.value = false
  reportePersonalCargado.value = false
  reporteClienteCargado.value = false
  await limpiarFiltros(false)
  filtrosAbiertos.value = false
  paginacionTabla.value = esReporteBackend.value
    ? { page: 1, rowsPerPage: 10, rowsNumber: 0 }
    : { page: 1, rowsPerPage: 3 }
  dialogoReporte.value = true
  if (esReporteBackend.value) {
    await cargarCatalogosVentas()
    if (esReporteVenta.value) await cargarReporteVenta()
    else if (esReporteCaja.value) await cargarReporteCaja()
    else if (esReporteInventario.value) await cargarReporteInventario()
    else if (esReporteCompras.value) await cargarReporteCompras()
    else if (esReportePersonal.value) await cargarReportePersonal()
    else await cargarReporteCliente()
  }
}

async function limpiarFiltros(recargar = true) {
  Object.keys(filtros).forEach((key) => {
    filtros[key] = null
  })
  filtrosAplicadosVenta.value = {}
  paginacionTabla.value.page = 1
  if (recargar && esReporteBackend.value) {
    if (esReporteVenta.value) await cargarReporteVenta()
    else if (esReporteCaja.value) await cargarReporteCaja()
    else if (esReporteInventario.value) await cargarReporteInventario()
    else if (esReporteCompras.value) await cargarReporteCompras()
    else if (esReportePersonal.value) await cargarReportePersonal()
    else await cargarReporteCliente()
  }
}

async function aplicarFiltros() {
  if (!fechasValidas()) return
  if (esReporteVenta.value) {
    filtrosAplicadosVenta.value = { ...filtros }
    paginacionTabla.value.page = 1
    await cargarReporteVenta()
    return
  }
  if (esReporteCaja.value) {
    filtrosAplicadosVenta.value = { ...filtros }
    paginacionTabla.value.page = 1
    await cargarReporteCaja()
    return
  }
  if (esReporteInventario.value) {
    filtrosAplicadosVenta.value = { ...filtros }
    paginacionTabla.value.page = 1
    await cargarReporteInventario()
    return
  }
  if (esReporteCompras.value) {
    filtrosAplicadosVenta.value = { ...filtros }
    paginacionTabla.value.page = 1
    await cargarReporteCompras()
    return
  }
  if (esReportePersonal.value) { filtrosAplicadosVenta.value = { ...filtros }; paginacionTabla.value.page = 1; await cargarReportePersonal(); return }
  if (esReporteCliente.value) { filtrosAplicadosVenta.value = { ...filtros }; paginacionTabla.value.page = 1; await cargarReporteCliente(); return }
  $q.notify({ type: 'positive', message: 'Filtros aplicados al reporte.', position: 'top' })
}

async function exportar(formato) {
  if (esReporteVenta.value) {
    if (!fechasValidas()) return
    if (!reporteVentaCargado.value || totalReporteVenta.value === 0 || exportandoReporte.value) return
    try {
      exportandoReporte.value = true
      formatoExportando.value = formato
      const archivo = await exportarReporteVenta(
        reporteSeleccionado.value.id,
        formato,
        construirParametrosVenta(false)
      )
      descargarArchivo(
        archivo,
        `${rutasReporteVenta[reporteSeleccionado.value.id]}.${formato === 'PDF' ? 'pdf' : 'xlsx'}`
      )
      $q.notify({ type: 'positive', message: `Reporte exportado a ${formato}.`, position: 'top' })
    } catch (error) {
      notificarError(error, `No se pudo exportar el reporte a ${formato}.`)
    } finally {
      exportandoReporte.value = false
      formatoExportando.value = null
    }
    return
  }
  if (esReporteCaja.value) {
    if (!fechasValidas() || !reporteCajaCargado.value || totalReporteCaja.value === 0 || exportandoReporte.value) return
    try {
      exportandoReporte.value = true
      formatoExportando.value = formato
      const archivo = await exportarReporteCaja(reporteSeleccionado.value.id, formato, construirParametrosCaja(false))
      descargarArchivo(archivo, `${rutasReporteCaja[reporteSeleccionado.value.id]}.${formato === 'PDF' ? 'pdf' : 'xlsx'}`)
      $q.notify({ type: 'positive', message: `Reporte exportado a ${formato}.`, position: 'top' })
    } catch (error) {
      notificarError(error, `No se pudo exportar el reporte a ${formato}.`)
    } finally {
      exportandoReporte.value = false
      formatoExportando.value = null
    }
    return
  }
  if (esReporteInventario.value) {
    if (!fechasValidas() || !reporteInventarioCargado.value || totalReporteInventario.value === 0 || exportandoReporte.value) return
    try {
      exportandoReporte.value = true
      formatoExportando.value = formato
      const archivo = await exportarReporteInventario(reporteSeleccionado.value.id, formato, { ...construirParametrosInventario(false), _ts: Date.now() })
      descargarArchivo(archivo, `${rutasReporteInventario[reporteSeleccionado.value.id]}.${formato === 'PDF' ? 'pdf' : 'xlsx'}`)
      $q.notify({ type: 'positive', message: `Reporte exportado a ${formato}.`, position: 'top' })
    } catch (error) { notificarError(error, `No se pudo exportar el reporte a ${formato}.`) }
    finally { exportandoReporte.value = false; formatoExportando.value = null }
    return
  }
  if (esReporteCompras.value) {
    if (!fechasValidas() || !reporteComprasCargado.value || totalReporteCompras.value === 0 || exportandoReporte.value) return
    try { exportandoReporte.value = true; formatoExportando.value = formato; const archivo = await exportarReporteCompras(reporteSeleccionado.value.id, formato, construirParametrosCompras(false)); descargarArchivo(archivo, `${rutasReporteCompras[reporteSeleccionado.value.id]}.${formato === 'PDF' ? 'pdf' : 'xlsx'}`); $q.notify({ type: 'positive', message: `Reporte exportado a ${formato}.`, position: 'top' }) }
    catch (error) { notificarError(error, `No se pudo exportar el reporte a ${formato}.`) }
    finally { exportandoReporte.value = false; formatoExportando.value = null }
    return
  }
  if (esReportePersonal.value) {
    if (!fechasValidas() || !reportePersonalCargado.value || totalReportePersonal.value === 0 || exportandoReporte.value) return
    try { exportandoReporte.value = true; formatoExportando.value = formato; const archivo = await exportarReportePersonal(reporteSeleccionado.value.id, formato, construirParametrosPersonal(false)); descargarArchivo(archivo, `${rutasReportePersonal[reporteSeleccionado.value.id]}.${formato === 'PDF' ? 'pdf' : 'xlsx'}`) } finally { exportandoReporte.value = false; formatoExportando.value = null }
    return
  }
  if (esReporteCliente.value) {
    if (!fechasValidas() || !reporteClienteCargado.value || totalReporteCliente.value === 0 || exportandoReporte.value) return
    try { exportandoReporte.value = true; formatoExportando.value = formato; const archivo = await exportarReporteCliente(reporteSeleccionado.value.id, formato, {}); descargarArchivo(archivo, `${rutasReporteCliente[reporteSeleccionado.value.id]}.${formato === 'PDF' ? 'pdf' : 'xlsx'}`) } finally { exportandoReporte.value = false; formatoExportando.value = null }
    return
  }
  $q.notify({
    type: 'info',
    icon: formato === 'PDF' ? 'picture_as_pdf' : 'table_view',
    message: `Diseño listo. La exportación a ${formato} se habilitará al conectar el backend.`,
    position: 'top'
  })
}

async function cambiarPagina({ pagination }) {
  paginacionTabla.value = { ...pagination }
  if (esReporteVenta.value) await cargarReporteVenta()
  else if (esReporteCaja.value) await cargarReporteCaja()
  else if (esReporteInventario.value) await cargarReporteInventario()
  else if (esReporteCompras.value) await cargarReporteCompras()
  else if (esReportePersonal.value) await cargarReportePersonal()
  else if (esReporteCliente.value) await cargarReporteCliente()
}

async function cargarReporteVenta() {
  try {
    cargandoReporte.value = true
    reporteVentaCargado.value = false
    const respuesta = await consultarReporteVenta(
      reporteSeleccionado.value.id,
      construirParametrosVenta(true)
    )
    filasReporteVenta.value = (respuesta.content || []).map((fila, indice) =>
      normalizarFilaVenta(fila, indice)
    )
    totalReporteVenta.value = respuesta.totalElements || 0
    reporteVentaCargado.value = true
    paginacionTabla.value = {
      ...paginacionTabla.value,
      page: (respuesta.number ?? 0) + 1,
      rowsPerPage: respuesta.size || paginacionTabla.value.rowsPerPage,
      rowsNumber: respuesta.totalElements || 0
    }
  } catch (error) {
    filasReporteVenta.value = []
    totalReporteVenta.value = 0
    reporteVentaCargado.value = false
    notificarError(error, 'No se pudo consultar el reporte de ventas.')
  } finally {
    cargandoReporte.value = false
  }
}

async function cargarReporteCaja() {
  try {
    cargandoReporte.value = true
    reporteCajaCargado.value = false
    const respuesta = await consultarReporteCaja(reporteSeleccionado.value.id, construirParametrosCaja(true))
    filasReporteCaja.value = (respuesta.content || []).map((fila, indice) => normalizarFilaCaja(fila, indice))
    totalReporteCaja.value = respuesta.totalElements || 0
    reporteCajaCargado.value = true
    paginacionTabla.value = { ...paginacionTabla.value, page: (respuesta.number ?? 0) + 1, rowsPerPage: respuesta.size || paginacionTabla.value.rowsPerPage, rowsNumber: respuesta.totalElements || 0 }
  } catch (error) {
    filasReporteCaja.value = []
    totalReporteCaja.value = 0
    reporteCajaCargado.value = false
    notificarError(error, 'No se pudo consultar el reporte de caja.')
  } finally {
    cargandoReporte.value = false
  }
}

async function cargarReporteInventario() {
  try {
    cargandoReporte.value = true
    reporteInventarioCargado.value = false
    const respuesta = await consultarReporteInventario(reporteSeleccionado.value.id, construirParametrosInventario(true))
    filasReporteInventario.value = (respuesta.content || []).map((fila, indice) => normalizarFilaInventario(fila, indice))
    totalReporteInventario.value = respuesta.totalElements || 0
    reporteInventarioCargado.value = true
    paginacionTabla.value = { ...paginacionTabla.value, page: (respuesta.number ?? 0) + 1, rowsPerPage: respuesta.size || paginacionTabla.value.rowsPerPage, rowsNumber: respuesta.totalElements || 0 }
  } catch (error) { filasReporteInventario.value = []; totalReporteInventario.value = 0; reporteInventarioCargado.value = false; notificarError(error, 'No se pudo consultar el reporte de inventario.') }
  finally { cargandoReporte.value = false }
}

async function cargarReporteCompras() {
  try { cargandoReporte.value = true; reporteComprasCargado.value = false; const respuesta = await consultarReporteCompras(reporteSeleccionado.value.id, construirParametrosCompras(true)); filasReporteCompras.value = (respuesta.content || []).map((fila, indice) => normalizarFilaCompras(fila, indice)); totalReporteCompras.value = respuesta.totalElements || 0; reporteComprasCargado.value = true; paginacionTabla.value = { ...paginacionTabla.value, page: (respuesta.number ?? 0) + 1, rowsPerPage: respuesta.size || paginacionTabla.value.rowsPerPage, rowsNumber: respuesta.totalElements || 0 } }
  catch (error) { filasReporteCompras.value = []; totalReporteCompras.value = 0; reporteComprasCargado.value = false; notificarError(error, 'No se pudo consultar el reporte de compras.') }
  finally { cargandoReporte.value = false }
}
async function cargarReportePersonal() {
  try { cargandoReporte.value = true; reportePersonalCargado.value = false; const respuesta = await consultarReportePersonal(reporteSeleccionado.value.id, construirParametrosPersonal(true)); filasReportePersonal.value = (respuesta.content || []).map((fila, indice) => ({ ...fila, id: `${paginacionTabla.value.page}-${indice}`, fecha: fila.fecha ? formatearFecha(fila.fecha) : fila.fecha })); totalReportePersonal.value = respuesta.totalElements || 0; reportePersonalCargado.value = true; paginacionTabla.value = { ...paginacionTabla.value, page: (respuesta.number ?? 0) + 1, rowsNumber: respuesta.totalElements || 0 } } catch (error) { filasReportePersonal.value = []; totalReportePersonal.value = 0; notificarError(error, 'No se pudo consultar el reporte de personal.') } finally { cargandoReporte.value = false }
}
async function cargarReporteCliente() { try { cargandoReporte.value = true; reporteClienteCargado.value = false; const respuesta = await consultarReporteCliente(reporteSeleccionado.value.id, { page: paginacionTabla.value.page - 1, size: paginacionTabla.value.rowsPerPage }); filasReporteCliente.value = (respuesta.content || []).map((fila, indice) => ({ ...fila, id: `${paginacionTabla.value.page}-${indice}` })); totalReporteCliente.value = respuesta.totalElements || 0; reporteClienteCargado.value = true; paginacionTabla.value = { ...paginacionTabla.value, page: (respuesta.number ?? 0) + 1, rowsNumber: respuesta.totalElements || 0 } } catch (error) { filasReporteCliente.value = []; totalReporteCliente.value = 0; notificarError(error, 'No se pudo consultar el reporte de clientes.') } finally { cargandoReporte.value = false } }

function construirParametrosVenta(incluirPagina) {
  const origen = filtrosAplicadosVenta.value
  const parametros = {
    fechaInicio: origen.desde,
    fechaFin: origen.hasta,
    metodoPago: origen.metodoPago,
    tipoVenta: origen.tipoVenta,
    agrupacion: origen.agrupacion,
    cajeroId: origen.cajero,
    clienteId: origen.cliente,
    cajaId: origen.caja,
    productoId: origen.producto,
    categoriaId: origen.categoriaProducto,
    marcaId: origen.marca
  }

  if (reporteSeleccionado.value.id === 'ventas-contado-credito') {
    parametros.estadoCredito = origen.estado
  } else {
    parametros.estadoVenta = origen.estado
  }

  if (origen.margen === 'MENOR_10') parametros.margenMaximo = 10
  if (origen.margen === '10_25') {
    parametros.margenMinimo = 10
    parametros.margenMaximo = 25
  }
  if (origen.margen === 'MAYOR_25') parametros.margenMinimo = 25

  if (incluirPagina) {
    parametros.page = paginacionTabla.value.page - 1
    parametros.size = Number.parseInt(origen.limite, 10) || paginacionTabla.value.rowsPerPage
  }

  return Object.fromEntries(Object.entries(parametros).filter(([, valor]) => valor !== null && valor !== undefined && valor !== ''))
}

function construirParametrosCaja(incluirPagina) {
  const origen = filtrosAplicadosVenta.value
  const parametros = {
    fechaInicio: origen.desde,
    fechaFin: origen.hasta,
    cajaId: origen.caja,
    cajeroId: origen.cajero,
    estadoSesion: ({ Abierta: 'ABIERTA', Cerrada: 'CERRADA' }[origen.estado] || origen.estado),
    tipoMovimiento: ({ Ingreso: 'INGRESO', Egreso: 'EGRESO' }[origen.tipoMovimiento] || origen.tipoMovimiento),
    concepto: ({ 'Fondo inicial': 'FONDO_INICIAL', Venta: 'VENTA', 'Gasto operativo': 'GASTO_OPERATIVO', Retiro: 'RETIRO' }[origen.concepto] || origen.concepto),
    resultado: ({ 'Sin diferencia': 'SIN_DIFERENCIA', Faltante: 'FALTANTE', Sobrante: 'SOBRANTE' }[origen.resultado] || origen.resultado),
    metodoPago: origen.metodoPago,
    sesionId: origen.sesion
  }
  if (incluirPagina) {
    parametros.page = paginacionTabla.value.page - 1
    parametros.size = paginacionTabla.value.rowsPerPage
  }
  return Object.fromEntries(Object.entries(parametros).filter(([, valor]) => valor !== null && valor !== undefined && valor !== ''))
}

function construirParametrosInventario(incluirPagina) {
  const origen = filtrosAplicadosVenta.value
  const parametros = {
    fechaInicio: origen.desde,
    fechaFin: origen.hasta,
    productoId: origen.producto,
    categoriaId: origen.categoriaProducto,
    marcaId: origen.marca,
    almacenId: origen.almacen,
    ubicacionId: origen.ubicacion,
    almacenOrigenId: origen.almacenOrigen,
    almacenDestinoId: origen.almacenDestino,
    estadoStock: origen.estadoStock,
    nivelStock: origen.nivelStock,
    tipoMovimiento: origen.tipoMovimiento,
    lote: origen.lote,
    estadoTransferencia: origen.estadoTransferencia,
    responsableId: origen.responsable,
    estadoConteo: origen.estadoConteo,
    resultadoInventario: origen.resultado,
    tipoAjuste: origen.tipoAjuste,
    motivoAjuste: origen.motivoAjuste,
    estadoAjuste: origen.estadoAjuste
  }
  if (incluirPagina) { parametros.page = paginacionTabla.value.page - 1; parametros.size = paginacionTabla.value.rowsPerPage }
  return Object.fromEntries(Object.entries(parametros).filter(([, valor]) => valor !== null && valor !== undefined && valor !== ''))
}

function construirParametrosCompras(incluirPagina) {
  const origen = filtrosAplicadosVenta.value
  const parametros = { proveedorId: origen.proveedor, estadoPedido: origen.estadoPedido }
  if (incluirPagina) { parametros.page = paginacionTabla.value.page - 1; parametros.size = paginacionTabla.value.rowsPerPage }
  return Object.fromEntries(Object.entries(parametros).filter(([, valor]) => valor !== null && valor !== undefined && valor !== ''))
}
function construirParametrosPersonal(incluirPagina) { const p = {}; if (incluirPagina) { p.page = paginacionTabla.value.page - 1; p.size = paginacionTabla.value.rowsPerPage } return p }

function normalizarFilaCaja(fila, indice) {
  const resultado = { ...fila, id: `${paginacionTabla.value.page}-${indice}` }
  ;['fecha'].forEach((campo) => { if (campo in resultado) resultado[campo] = formatearFecha(resultado[campo]) })
  ;['apertura', 'cierre', 'esperado', 'contado', 'diferencia', 'monto', 'total', 'recaudacion', 'promedio'].forEach((campo) => { if (campo in resultado) resultado[campo] = formatearMoneda(resultado[campo]) })
  if ('porcentaje' in resultado) resultado.porcentaje = formatearPorcentaje(resultado.porcentaje)
  ;['estado', 'resultado', 'tipo', 'concepto', 'metodo'].forEach((campo) => { if (campo in resultado) resultado[campo] = etiquetaEnum(resultado[campo]) })
  return resultado
}

function normalizarFilaInventario(fila, indice) {
  const resultado = { ...fila, id: `${paginacionTabla.value.page}-${indice}` }
  ;['codigoProducto', 'cantidadSistema', 'cantidadFisica', 'stockAnterior', 'stockResultante', 'valorTotal', 'costoUnitario'].forEach((campo) => {
    const alternativo = campo.toLowerCase()
    if (!(campo in resultado) && alternativo in resultado) resultado[campo] = resultado[alternativo]
  })
  if ('fecha' in resultado || 'vencimiento' in resultado) ['fecha', 'vencimiento'].forEach((campo) => { if (campo in resultado) resultado[campo] = formatearFecha(resultado[campo]) })
  ;['stock', 'minimo', 'cantidad', 'saldo', 'cantidadSistema', 'cantidadFisica', 'diferencia', 'stockAnterior', 'stockResultante'].forEach((campo) => {
    if (!(campo in resultado)) return
    if (resultado[campo] === null || resultado[campo] === undefined || resultado[campo] === '') resultado[campo] = '--'
    else resultado[campo] = Number(resultado[campo]).toLocaleString('es-BO', { minimumFractionDigits: 0, maximumFractionDigits: 3 })
  })
  ;['valorTotal', 'costoUnitario'].forEach((campo) => { if (campo in resultado && resultado[campo] !== null && resultado[campo] !== undefined) resultado[campo] = formatearMoneda(resultado[campo]) })
  ;['tipo', 'motivo', 'estado'].forEach((campo) => { if (campo in resultado) resultado[campo] = etiquetaEnum(resultado[campo]) })
  return resultado
}

function normalizarFilaCompras(fila, indice) {
  const resultado = { ...fila, id: `${paginacionTabla.value.page}-${indice}` }
  ;['fecha', 'entrega'].forEach((campo) => { if (campo in resultado) resultado[campo] = formatearFecha(resultado[campo]) })
  ;['total', 'costo', 'precio', 'ganancia'].forEach((campo) => { if (campo in resultado) resultado[campo] = formatearMoneda(resultado[campo]) })
  return resultado
}

async function cargarCatalogosVentas() {
  if (catalogosVentasCargados.value) return
  try {
    const catalogos = await cargarCatalogosReporteVenta()
    reemplazarOpciones(opciones.productos, catalogos.productos.map(item => ({ label: item.nombre, value: item.idProducto })))
    reemplazarOpciones(opciones.categorias, catalogos.categorias.map(item => ({ label: item.nombre, value: item.idCategoria })))
    reemplazarOpciones(opciones.marcas, catalogos.marcas.map(item => ({ label: item.nombre, value: item.idMarca })))
    reemplazarOpciones(opciones.clientes, catalogos.clientes.map(item => ({
      label: [item.nombres, item.apellidoPaterno, item.apellidoMaterno].filter(Boolean).join(' '),
      value: item.idCliente
    })))
    reemplazarOpciones(opciones.cajeros, catalogos.cajeros
      .filter(item => item.activo !== false)
      .map(item => ({ label: item.nombreCompleto || item.username, value: item.idUsuario })))
    reemplazarOpciones(opciones.cajas, catalogos.cajas.map(item => ({ label: item.nombre, value: item.idCaja })))
    const [respuestaAlmacenes, respuestaUbicaciones, respuestaEmpleados, respuestaProveedores] = await Promise.all([
      listarAlmacenes({ page: 0, size: 100, estado: true, sortBy: 'nombre', sortDir: 'asc' }),
      listarUbicacionesPaginado({ page: 0, size: 500, estado: true, sortBy: 'nombre', sortDir: 'asc' }),
      listarEmpleados({ page: 0, size: 100, estado: true, sortBy: 'nombreCompleto', sortDir: 'asc' }),
      listarProveedores({ page: 0, size: 500, estado: true, sortBy: 'idProveedor', sortDir: 'asc' })
    ])
    reemplazarOpciones(opciones.almacenes, (respuestaAlmacenes?.content || []).map(item => ({ label: item.nombre, value: item.idAlmacen })))
    reemplazarOpciones(opciones.ubicaciones, (respuestaUbicaciones?.content || []).map(item => ({ label: `${item.codigo || ''} ${item.nombre || ''}`.trim(), value: item.idUbicacion })))
    reemplazarOpciones(opciones.responsables, (respuestaEmpleados?.content || []).filter(item => item.estado !== false).map(item => ({ label: item.nombreCompleto || item.nombre || `Empleado ${item.id}`, value: item.id })))
    reemplazarOpciones(opciones.proveedores, (respuestaProveedores?.content || []).filter(item => item.estado !== false).map(item => ({ label: item.nombre || item.razonSocial || `Proveedor ${item.id}`, value: item.id })))
    if (esReporteCaja.value) {
      const sesiones = await listarSesionesCaja()
      reemplazarOpciones(opciones.sesiones, (sesiones.content || []).map(item => ({
        label: `SES-${String(item.idSesionCaja).padStart(4, '0')}`,
        value: item.idSesionCaja
      })))
    }
    catalogosVentasCargados.value = true
  } catch (error) {
    notificarError(error, 'No se pudieron cargar todos los filtros del reporte.')
  }
}

function reemplazarOpciones(destino, valores) {
  destino.splice(0, destino.length, ...valores)
}

function normalizarFilaVenta(fila, indice) {
  const resultado = { ...fila, id: `${paginacionTabla.value.page}-${indice}` }
  const camposFecha = ['fecha', 'ultimaVenta', 'ultimaCompra', 'periodo']
  const camposMoneda = ['total', 'totalVendido', 'costoTotal', 'ganancia', 'importeGenerado', 'totalRecibido', 'ticketPromedio', 'importeTotal', 'montoCobrado', 'saldoPendiente', 'ventasContado', 'ventasCredito', 'totalContado', 'totalCredito', 'totalGastado']
  camposFecha.forEach((campo) => {
    if (campo in resultado) resultado[campo] = formatearFecha(resultado[campo])
  })
  camposMoneda.forEach((campo) => {
    if (campo in resultado) resultado[campo] = formatearMoneda(resultado[campo])
  })
  if ('margenPorcentaje' in resultado) resultado.margenPorcentaje = formatearPorcentaje(resultado.margenPorcentaje)
  if ('participacionPorcentaje' in resultado) resultado.participacionPorcentaje = formatearPorcentaje(resultado.participacionPorcentaje)
  if ('estadoCosto' in resultado) {
    resultado.estadoCostoCompleto = resultado.estadoCosto === 'COMPLETO'
    resultado.estadoCosto = resultado.estadoCostoCompleto ? 'Completo' : 'No disponible'
  }
  if ('estado' in resultado) resultado.estado = etiquetaEnum(resultado.estado)
  if ('tipoVenta' in resultado) resultado.tipoVenta = etiquetaEnum(resultado.tipoVenta)
  if ('metodoPago' in resultado) resultado.metodoPago = String(resultado.metodoPago || '').split(', ').map(etiquetaEnum).join(', ')
  return resultado
}

async function verDetalleGanancia(venta) {
  ventaGananciaSeleccionada.value = venta
  detalleGanancia.value = []
  dialogoDetalleGanancia.value = true
  cargandoDetalleGanancia.value = true
  try {
    const respuesta = await obtenerDetalleGananciaVenta(venta.idVenta)
    detalleGanancia.value = (respuesta || []).map((item, indice) => ({
      ...item,
      id: indice + 1,
      precioVentaUnitario: formatearMoneda(item.precioVentaUnitario),
      costoUnitario: formatearMoneda(item.costoUnitario),
      totalVendido: formatearMoneda(item.totalVendido),
      costoTotal: formatearMoneda(item.costoTotal),
      ganancia: formatearMoneda(item.ganancia)
    }))
  } catch (error) {
    notificarError(error, 'No se pudo cargar el detalle de productos de la venta.')
  } finally {
    cargandoDetalleGanancia.value = false
  }
}

function formatearFecha(valor) {
  if (!valor) return 'Sin registro'
  const [fecha, hora] = String(valor).split('T')
  const [anio, mes, dia] = fecha.split('-')
  return hora ? `${dia}/${mes}/${anio} ${hora.slice(0, 5)}` : `${dia}/${mes}/${anio}`
}

function formatearMoneda(valor) {
  if (valor === null || valor === undefined) return 'No disponible'
  return `Bs ${Number(valor || 0).toLocaleString('es-BO', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

function formatearPorcentaje(valor) {
  if (valor === null || valor === undefined) return 'No disponible'
  return `${Number(valor || 0).toLocaleString('es-BO', { minimumFractionDigits: 2, maximumFractionDigits: 2 })} %`
}

function etiquetaEnum(valor) {
  const etiquetas = {
    CONFIRMADA: 'Confirmada', EN_ESPERA: 'En espera', ANULADA: 'Anulada',
    CONTADO: 'Contado', CREDITO: 'Crédito', EFECTIVO: 'Efectivo', MIXTO: 'Mixto',
    ABIERTA: 'Abierta', CERRADA: 'Cerrada', INGRESO: 'Ingreso', EGRESO: 'Egreso',
    FONDO_INICIAL: 'Fondo inicial', VENTA: 'Venta', GASTO_OPERATIVO: 'Gasto operativo', RETIRO: 'Retiro',
    SIN_DIFERENCIA: 'Sin diferencia', FALTANTE: 'Faltante', SOBRANTE: 'Sobrante', PENDIENTE: 'Pendiente'
  }
  return etiquetas[valor] || valor || 'Sin dato'
}

function descargarArchivo(contenido, nombre) {
  const url = URL.createObjectURL(contenido instanceof Blob ? contenido : new Blob([contenido]))
  const enlace = document.createElement('a')
  enlace.href = url
  enlace.download = nombre
  document.body.appendChild(enlace)
  enlace.click()
  enlace.remove()
  URL.revokeObjectURL(url)
}

function notificarError(error, mensaje) {
  $q.notify({
    type: 'negative',
    message: error?.response?.data?.message || mensaje,
    position: 'top'
  })
}

function fechasValidas() {
  const hoy = fechaActual()
  if ((filtros.desde && filtros.desde > hoy) || (filtros.hasta && filtros.hasta > hoy)) {
    $q.notify({ type: 'warning', message: 'Las fechas del reporte no pueden ser futuras.', position: 'top' })
    return false
  }
  if (filtros.desde && filtros.hasta && filtros.desde > filtros.hasta) {
    $q.notify({ type: 'warning', message: 'La fecha inicial no puede ser posterior a la fecha final.', position: 'top' })
    return false
  }
  return true
}

function fechaActual() {
  const fecha = new Date()
  const desplazamiento = fecha.getTimezoneOffset() * 60000
  return new Date(fecha.getTime() - desplazamiento).toISOString().slice(0, 10)
}

function campo(key, label, options) {
  return { key, label, type: 'select', options }
}

function opcionesSelect(valores) {
  return valores.map(([label, value]) => ({ label, value }))
}

function columnasApi(columnas) {
  return columnas.map(([name, label, align = 'left']) => ({
    name,
    label,
    field: name,
    align,
    sortable: true
  }))
}

function tabla(columnas, valores) {
  const definiciones = columnas.map(([name, label, align = 'left']) => ({
    name,
    label,
    field: name,
    align,
    sortable: true
  }))
  const filas = valores.map((fila, indice) => {
    const registro = { id: indice + 1 }
    columnas.forEach(([name], posicion) => {
      registro[name] = fila[posicion]
    })
    return registro
  })
  return { columnas: definiciones, filas }
}

function claseEstado(estado) {
  const texto = normalizar(estado)
  if (['activo', 'disponible', 'confirmada', 'completada', 'completo', 'cerrada', 'procesada', 'aprobado', 'presente', 'pagado', 'sin diferencia', 'mas vendido'].includes(texto)) return 'status-positive'
  if (['en espera', 'pendiente', 'parcial', 'proximo', 'abierta', 'en revision', 'menos vendido', 'sobrante'].includes(texto)) return 'status-warning'
  if (['vencido', 'agotado', 'critico', 'faltante', 'sin ventas'].includes(texto)) return 'status-negative'
  return 'status-neutral'
}
</script>

<style scoped src="../../assets/styles/reportes/reportes.css"></style>
