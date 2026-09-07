<template>
  <q-dialog :model-value="modelValue" persistent @update:model-value="emit('update:modelValue', $event)">
    <q-card class="dialog">
      <q-card-section class="head row items-center justify-between">
        <div>
          <div class="title">{{ props.ajusteEditar ? 'Editar borrador de inventario' : 'Nuevo ajuste de inventario' }}</div>
          <div class="sub">Registra una o varias líneas en un mismo documento.</div>
        </div>
        <q-btn flat round dense icon="close" v-close-popup />
      </q-card-section>

      <q-stepper v-model="paso" flat animated color="green-8">
        <q-step :name="1" title="Tipo" icon="swap_vert" :done="paso > 1">
          <div class="row q-col-gutter-md">
            <div v-for="tipo in tipos" :key="tipo.value" class="col-12 col-sm-6">
              <q-card flat bordered class="type" :class="{ selected: formulario.tipo === tipo.value, locked: props.ajusteEditar }" @click="!props.ajusteEditar && seleccionarTipo(tipo.value)">
                <q-card-section>
                  <q-icon :name="tipo.icon" :color="tipo.value === 'ENTRADA' ? 'blue-7' : 'red-6'" size="32px" />
                  <div class="type-title">{{ tipo.label }}</div>
                  <div class="sub">{{ tipo.help }}</div>
                </q-card-section>
              </q-card>
            </div>
            <q-select v-model="formulario.motivo" class="col-12 q-mt-lg motivo-select" popup-content-class="motivo-popup" outlined emit-value map-options :options="motivos" :disable="Boolean(props.ajusteEditar)" :label="formulario.tipo === 'ENTRADA' ? 'Motivo de entrada *' : 'Motivo de salida *'" hint="Selecciona la causa que corresponde al ajuste.">
              <template #option="scope">
                <q-item v-bind="scope.itemProps">
                  <q-item-section>
                    <q-item-label>{{ scope.opt.label }}</q-item-label>
                    <q-item-label caption>{{ scope.opt.descripcion }}</q-item-label>
                  </q-item-section>
                </q-item>
              </template>
            </q-select>
          </div>
        </q-step>

        <q-step :name="2" title="Productos" icon="inventory_2" :done="paso > 2">
          <div class="line-form">
            <div v-if="formulario.motivo !== 'PRODUCTO_VENCIDO'" class="row q-col-gutter-md">
              <div class="col-12">
                <q-select v-if="formulario.motivo === 'PRODUCTO_VENCIDO'" v-model="linea.lote" outlined emit-value map-options :options="productosVencidos" label="Seleccionar producto vencido *" @update:model-value="seleccionarProductoVencido" />
                <q-select v-else v-model="linea.producto" outlined emit-value map-options :options="productosDisponibles" label="Producto *" @update:model-value="seleccionarProducto" />
                <q-select v-if="formulario.motivo === 'MERMA' && linea.producto" v-model="linea.idExistencia" class="q-mt-sm" outlined emit-value map-options :options="existenciasProducto" label="Existencia / ubicacion *" @update:model-value="seleccionarExistencia" />
                <q-banner v-if="formulario.motivo === 'PRODUCTO_VENCIDO'" rounded class="expired-help q-mt-sm">
                  Solo se muestran lotes vencidos con stock disponible para dar de baja.
                </q-banner>
              </div>
              <div class="col-12 col-md-6">
                <q-select v-if="formulario.motivo !== 'MERMA'" v-model="linea.almacen" outlined emit-value map-options :options="almacenes" label="Almacen *" @update:model-value="cambiarAlmacen" />
                <q-input v-else :model-value="nombreAlmacen(linea.almacen)" outlined readonly label="Almacen" />
              </div>
              <div class="col-12 col-md-6">
                <q-select v-if="formulario.motivo !== 'MERMA'" v-model="linea.ubicacion" outlined emit-value map-options :options="ubicaciones" label="Ubicacion *" :disable="!linea.almacen" />
                <q-input v-else :model-value="linea.nombreUbicacion || nombreUbicacion(linea.ubicacion)" outlined readonly label="Ubicacion" />
              </div>
              <div v-if="productoSeleccionado" class="col-12 col-md-6">
                <q-select v-model="linea.presentacionId" outlined emit-value map-options
                  :options="presentacionesProducto" option-label="label" option-value="value"
                  label="Presentacion *" @update:model-value="actualizarCantidadBase" />
              </div>
              <div class="col-12 col-md-6">
                <q-checkbox v-if="formulario.motivo === 'PRODUCTO_ENCONTRADO' && productoSeleccionado?.controlaLote" v-model="linea.nuevoLote" color="green-8" label="Registrar nuevo lote" class="q-mb-sm" @update:model-value="cambiarModoLote" />
                <q-select v-if="(formulario.motivo === 'PRODUCTO_ENCONTRADO' || formulario.motivo === 'MERMA') && productoSeleccionado?.controlaLote && !linea.nuevoLote" v-model="linea.lote" outlined emit-value map-options :options="formulario.motivo === 'MERMA' ? opcionesLoteMerma : opcionesLoteEncontrado" label="Seleccionar lote *" @update:model-value="seleccionarLote" />
                <q-input v-if="productoSeleccionado?.controlaLote && (!requiereLoteExistente && (formulario.motivo !== 'PRODUCTO_ENCONTRADO' || linea.nuevoLote))" v-model="linea.codigoLote" outlined label="Codigo de lote *" />
                <q-input v-else-if="productoSeleccionado && !productoSeleccionado?.controlaLote" model-value="Sin lote" outlined readonly label="Control de lote" />
                <q-input v-else outlined label="Lote" disable placeholder="Selecciona un producto" />
              </div>
              <div v-if="productoSeleccionado?.controlaLote" class="col-12 col-md-3">
                <q-input v-model="linea.fechaFabricacion" outlined type="date" label="Fecha fabricacion" :readonly="requiereLoteExistente || (formulario.motivo === 'PRODUCTO_ENCONTRADO' && !linea.nuevoLote)" />
              </div>
              <div v-if="productoSeleccionado?.controlaVencimiento" class="col-12 col-md-3">
                <q-input v-model="linea.fechaVencimiento" outlined type="date" label="Fecha vencimiento *" :readonly="requiereLoteExistente || (formulario.motivo === 'PRODUCTO_ENCONTRADO' && !linea.nuevoLote)" />
              </div>
              <div class="col-12 col-sm-4">
                <q-input v-model.number="linea.stock" outlined type="number" label="Stock actual" readonly />
              </div>
              <div class="col-12 col-sm-4">
                <q-input v-model.number="linea.cantidadPresentacion" outlined type="number" min="0.001"
                  :label="formulario.motivo === 'PRODUCTO_VENCIDO' ? `Cantidad a dar de baja (${linea.unidad || unidadProducto(linea.producto)}) *` : `Cantidad (${presentacionSeleccionada?.label || linea.unidad || unidadProducto(linea.producto)}) *`"
                  :hint="formulario.tipo === 'SALIDA' ? `Stock disponible: ${linea.stock} ${linea.unidad || unidadProducto(linea.producto)}. Retiro convertido: ${linea.cantidad || 0} ${linea.unidad || unidadProducto(linea.producto)}.` : `Cantidad convertida: ${linea.cantidad || 0} ${linea.unidad || unidadProducto(linea.producto)}.`"
                  persistent-hint :error="excedeStock" error-message="Error: la cantidad supera el stock disponible." @update:model-value="actualizarCantidadBase" @keydown="bloquearNegativos" />
              </div>
              <div class="col-12 col-sm-4">
                <q-input v-model.number="linea.costo" outlined type="number" min="0.01" label="Costo unitario (Bs) *" :readonly="formulario.tipo === 'SALIDA'" @keydown="bloquearNegativos" />
              </div>
              <div class="col-12 col-sm-4">
                <q-input v-model.number="linea.precioVenta" outlined type="number" label="Precio de venta (Bs)" readonly />
              </div>
            </div>
            <div v-if="formulario.motivo === 'PRODUCTO_VENCIDO'" class="expired-panel">
              <div class="section-title">Productos vencidos disponibles</div>
              <q-markup-table v-if="productosVencidos.length" flat bordered class="expired-table q-mt-md">
                <thead><tr><th>Producto</th><th>Unidad</th><th>Lote</th><th>Stock</th><th>Almacen</th><th>Ubicacion</th><th></th></tr></thead>
                <tbody>
                  <tr v-for="item in productosVencidos" :key="item.value" :class="{ selected: linea.lote === item.value }">
                    <td><b>{{ item.productoLabel }}</b></td>
                    <td>{{ item.unidad }}</td>
                    <td>{{ item.codigo }}</td>
                    <td>{{ item.stock }}</td>
                    <td>{{ item.almacen }}</td>
                    <td>{{ item.ubicacion }}</td>
                    <td><q-btn unelevated no-caps dense color="green-8" label="Seleccionar" @click="seleccionarProductoVencido(item.value)" /></td>
                  </tr>
                </tbody>
              </q-markup-table>
              <div v-else class="empty-lines"><q-icon name="event_available" size="28px" /> No hay productos vencidos con stock disponible.</div>
              <div v-if="linea.lote" class="expired-quantity row items-end q-col-gutter-md q-mt-md">
                <div class="col-12 col-sm-5"><q-input v-model.number="linea.cantidad" outlined type="number" min="0.001" label="Cantidad a dar de baja *" :hint="`Stock disponible: ${linea.stock} ${linea.unidad || unidadProducto(linea.producto)}. Maximo permitido: ${linea.stock} ${linea.unidad || unidadProducto(linea.producto)}.`" persistent-hint :error="excedeStock" error-message="Error: la cantidad supera el stock disponible." @keydown="bloquearNegativos" /></div>
                <div class="col-12 col-sm-4"><q-input v-model="linea.fechaVencimiento" outlined type="date" label="Vencimiento" readonly /></div>
                <div class="col-12 col-sm-3"><q-btn unelevated no-caps color="green-8" icon="add" label="Agregar producto" class="full-width" :disable="excedeStock || !linea.cantidad" @click="agregarLinea" /></div>
              </div>
              <q-banner v-if="excedeStock" rounded class="stock-error q-mt-md"><q-icon name="error" /> La cantidad a dar de baja no puede ser mayor al stock disponible: {{ linea.stock }} {{ linea.unidad || unidadProducto(linea.producto) }}.</q-banner>
            </div>
            <q-banner v-if="formulario.motivo !== 'PRODUCTO_VENCIDO'" rounded class="result q-mt-md">
              Stock resultante: <b>{{ formatearNumero(stockResultante) }}</b>
              <span class="result-help">Esta linea se agregara al documento.</span>
            </q-banner>
            <q-banner v-if="formulario.motivo === 'MERMA' && excedeStock" rounded class="stock-error q-mt-md">
              <q-icon name="error" /> <b>No se puede agregar:</b> la cantidad indicada es mayor que el stock disponible ({{ linea.stock }}).
            </q-banner>
            <div v-if="formulario.tipo === 'ENTRADA'" class="profit-box q-mt-md" :class="{ negative: sinGanancia }">
              <span>Ganancia estimada por unidad: <b>Bs {{ gananciaUnitario.toFixed(2) }}</b></span>
              <span>Margen: <b>{{ margen.toFixed(2) }}%</b></span>
            </div>
            <div v-if="formulario.motivo === 'MERMA'" class="loss-box q-mt-md">
              <span>Perdida estimada: <b>Bs {{ perdidaEstimada.toFixed(2) }}</b></span>
              <span>Costo base × cantidad retirada</span>
            </div>
            <q-banner v-if="formulario.tipo === 'ENTRADA' && sinGanancia" rounded class="profit-warning q-mt-md">
              <q-icon name="warning_amber" /> El costo es igual o mayor al precio de venta. No existe ganancia estimada.
            </q-banner>
            <div v-if="formulario.motivo !== 'PRODUCTO_VENCIDO'" class="row justify-end q-mt-md">
              <q-btn outline no-caps color="green-8" :icon="editando ? 'save' : 'add'" :label="editando ? 'Guardar cambios' : 'Agregar producto'" :disable="excedeStock" @click="agregarLinea" />
            </div>
          </div>

          <div class="lines-header row items-center justify-between q-mt-lg">
            <div class="section-title">Productos agregados ({{ lineas.length }})</div>
            <div class="total">Cantidad total: <b>{{ cantidadTotal }}</b></div>
          </div>
          <q-markup-table v-if="lineas.length" flat bordered class="lines-table">
            <thead>
              <tr><th>Producto</th><th>Unidad</th><th>Lote</th><th>Fabricacion</th><th>Vencimiento</th><th>Ubicacion</th><th>Cantidad</th><th>Costo</th><th>Venta</th><th>{{ formulario.tipo === 'SALIDA' ? 'Perdida estimada' : 'Ganancia' }}</th><th>Resultado</th><th></th></tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in lineas" :key="item.producto + '-' + index">
                <td><b>{{ nombreProducto(item.producto) }}</b></td>
                <td>{{ item.unidad || unidadProducto(item.producto) }}</td>
                <td>{{ item.codigoLote || nombreLote(item.lote) }}</td>
                <td>{{ item.fechaFabricacion || '--' }}</td>
                <td>{{ item.fechaVencimiento || '--' }}</td>
                <td>{{ item.ubicacion === 11 ? 'A-01' : 'B-02' }}</td>
                <td>{{ item.cantidad }}</td>
                <td>Bs {{ Number(item.costo || 0).toFixed(2) }}</td>
                <td>Bs {{ Number(item.precioVenta || 0).toFixed(2) }}</td>
                <td :class="{ 'loss-cell': formulario.tipo === 'SALIDA' }">Bs {{ impactoLinea(item).toFixed(2) }}</td>
                <td>{{ formatearNumero(resultadoLinea(item)) }}</td>
                <td class="actions">
                  <q-btn flat round dense color="blue-7" icon="edit" @click="editarLinea(index)" />
                  <q-btn flat round dense color="negative" icon="delete" @click="eliminarLinea(index)" />
                </td>
              </tr>
            </tbody>
          </q-markup-table>
          <div v-else class="empty-lines"><q-icon name="playlist_add" size="28px" /> Agrega al menos un producto al ajuste.</div>
        </q-step>

        <q-step :name="3" title="Justificacion" icon="description">
          <q-banner rounded class="summary">
            <div class="summary-title">Resumen del ajuste</div>
            <div class="summary-grid">
              <span>Tipo: <b>{{ formulario.tipo }}</b></span>
              <span>Motivo: <b>{{ etiquetaMotivo }}</b></span>
              <span>Productos: <b>{{ lineas.length }}</b></span>
              <span>Cantidad total: <b>{{ cantidadTotal }}</b></span>
            </div>
          </q-banner>
          <div v-if="formulario.motivo === 'INVENTARIO_INICIAL'" class="specific-fields q-mt-md">
            <div class="specific-title">Datos del inventario inicial</div>
            <div class="row q-col-gutter-md q-mt-sm">
              <div class="col-12 col-sm-6">
                <q-input v-model="formulario.fechaLevantamiento" outlined type="date" label="Fecha del levantamiento *" hint="Ejemplo: dia en que se realizo el conteo inicial" />
              </div>
              <div class="col-12 col-sm-6">
                <q-input v-model="formulario.responsable" outlined label="Responsable del conteo *" placeholder="Ejemplo: Maria Lopez" />
              </div>
            </div>
          </div>
          <div v-if="formulario.motivo === 'PRODUCTO_ENCONTRADO'" class="specific-fields found-fields q-mt-md">
            <div class="specific-title">Datos del hallazgo</div>
            <div class="row q-col-gutter-md q-mt-sm">
              <div class="col-12 col-sm-6">
                <q-input v-model="formulario.lugarHallazgo" outlined label="Lugar donde fue encontrado *" placeholder="Ejemplo: Estante B-02 del almacen central" />
              </div>
              <div class="col-12 col-sm-6">
                <q-input v-model="formulario.fechaHallazgo" outlined type="date" label="Fecha del hallazgo *" />
              </div>
              <div class="col-12">
                <q-input v-model="formulario.encontradoPor" outlined label="Persona que lo encontro *" placeholder="Ejemplo: Carlos Perez" />
              </div>
            </div>
          </div>
          <div v-if="formulario.motivo === 'MERMA'" class="specific-fields q-mt-md">
            <div class="specific-title">Datos de la merma</div>
            <div class="row q-col-gutter-md q-mt-sm">
              <div class="col-12 col-sm-6">
                <q-select v-model="formulario.tipoMerma" outlined emit-value map-options :options="tiposMerma" label="Tipo de merma *" />
              </div>
              <div class="col-12">
                <q-input v-model="formulario.causaMerma" outlined label="Causa de la merma *" placeholder="Ejemplo: Envase roto durante la manipulacion" />
              </div>
            </div>
          </div>
          <q-input v-model="formulario.observacion" outlined type="textarea" autogrow maxlength="500" counter label="Observacion *" class="q-mt-md" :placeholder="ejemploObservacion" />
          <q-banner rounded class="warning q-mt-md">Se guardara como borrador y no cambiara el stock hasta ser aprobado y aplicado.</q-banner>
        </q-step>

        <template #navigation>
          <q-stepper-navigation class="row justify-between">
            <q-btn v-if="paso > 1" flat no-caps label="Anterior" @click="paso--" />
            <q-space />
            <q-btn v-if="paso < 3" unelevated no-caps color="green-8" label="Continuar" icon-right="arrow_forward" :disable="!puedeContinuar" @click="siguiente" />
            <q-btn v-else unelevated no-caps color="green-8" :label="props.ajusteEditar ? 'Guardar cambios' : 'Guardar borrador'" icon="save" :disable="!puedeGuardar" @click="guardar" />
          </q-stepper-navigation>
        </template>
      </q-stepper>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useQuasar } from 'quasar'
import { listarAlmacenes } from '../../api/almacen/almacen'
import { listarUbicacionesPaginado } from '../../api/ubicacionAlmacen/ubicacionAlmacen'
import { listarProductos } from '../../api/productos/productos'
import { listarExistencias } from '../../api/inventario/inventario'

const props = defineProps({ modelValue: Boolean, ajusteEditar: { type: Object, default: null } })
const emit = defineEmits(['update:modelValue', 'guardar'])
const $q = useQuasar()
const paso = ref(1)
const lineas = ref([])
const editando = ref(-1)

const tipos = [
  { value: 'ENTRADA', label: 'Entrada por ajuste', icon: 'add_circle', help: 'Inventario inicial, sobrantes o productos encontrados.' },
  { value: 'SALIDA', label: 'Salida por ajuste', icon: 'remove_circle', help: 'Productos vencidos, danados, perdidos o merma.' }
]
const almacenes = ref([])
const ubicaciones = ref([])
const productos = ref([])
const existencias = ref([])
const lotesRegistrados = ref([])
const catalogosListos = ref(false)
const lotes = computed(() => lotesRegistrados.value)
const productosConExistencia = computed(() => new Set(existencias.value.map((item) => item.producto)))
const productosDisponibles = computed(() => formulario.motivo === 'INVENTARIO_INICIAL'
  ? productos.value.filter((item) => !productosConExistencia.value.has(item.value) || item.value === linea.producto)
  : productos.value)
const existenciasProducto = computed(() => existencias.value
  .filter((item) => item.producto === linea.producto && Number(item.stock || 0) > 0)
  .map((item) => ({
    label: `${item.almacen || '--'} · ${item.ubicacion || '--'} · ${item.codigoLote || 'Sin lote'} · ${item.stock} ${item.unidad || ''}`,
    value: item.idExistencia
  })))
const motivosEntrada = [
  { label: 'Inventario inicial', value: 'INVENTARIO_INICIAL', descripcion: 'Registra productos que existian antes de usar el sistema.' },
  { label: 'Producto encontrado', value: 'PRODUCTO_ENCONTRADO', descripcion: 'Registra un producto encontrado fisicamente sin stock registrado.' }
]
const motivosSalida = [
  { label: 'Producto vencido', value: 'PRODUCTO_VENCIDO', descripcion: 'Retira productos cuya fecha de vencimiento ya paso.' },
  { label: 'Merma', value: 'MERMA', descripcion: 'Retira productos danados, rotos, derramados o deteriorados.' }
]
const tiposMerma = [
  { label: 'Producto danado', value: 'DANADO' },
  { label: 'Producto roto', value: 'ROTO' },
  { label: 'Derrame', value: 'DERRAME' },
  { label: 'Deterioro', value: 'DETERIORO' },
  { label: 'Otro', value: 'OTRO' }
]
const motivos = computed(() => formulario.tipo === 'ENTRADA' ? motivosEntrada : motivosSalida)
const etiquetaMotivo = computed(() => motivos.value.find((item) => item.value === formulario.motivo)?.label || '--')
const ejemploObservacion = computed(() => ({
  INVENTARIO_INICIAL: 'Ejemplo: Conteo inicial realizado antes de comenzar a usar el sistema.',
  PRODUCTO_ENCONTRADO: 'Ejemplo: Producto encontrado fisicamente fuera del inventario registrado.',
  PRODUCTO_VENCIDO: 'Ejemplo: Se retiran unidades vencidas del lote seleccionado.',
  MERMA: 'Ejemplo: Producto danado detectado durante la revision del almacen.'
}[formulario.motivo] || 'Describe el motivo y los detalles del ajuste.'))

const formulario = reactive({ tipo: 'ENTRADA', motivo: null, observacion: '', evidencia: null, fechaLevantamiento: '', responsable: '', acta: null, lugarHallazgo: '', fechaHallazgo: '', encontradoPor: '', tipoMerma: null, causaMerma: '' })
const linea = reactive({ almacen: null, ubicacion: null, nombreUbicacion: '', producto: null, idExistencia: null, unidad: '', presentacionId: null, cantidadPresentacion: null, lote: null, nuevoLote: false, codigoLote: '', fechaFabricacion: '', fechaVencimiento: '', stock: 0, cantidad: null, costo: null, precioVenta: 0 })
const stockResultante = computed(() => formulario.tipo === 'ENTRADA'
  ? Number(linea.stock || 0) + Number(linea.cantidad || 0)
  : Number(linea.stock || 0) - Number(linea.cantidad || 0))
const cantidadTotal = computed(() => lineas.value.reduce((total, item) => total + Number(item.cantidad || 0), 0))
const gananciaUnitario = computed(() => Number(linea.precioVenta || 0) - Number(linea.costo || 0))
const margen = computed(() => Number(linea.precioVenta || 0) > 0 ? (gananciaUnitario.value / Number(linea.precioVenta)) * 100 : 0)
const sinGanancia = computed(() => Number(linea.costo || 0) > 0 && Number(linea.precioVenta || 0) > 0 && Number(linea.costo) >= Number(linea.precioVenta))
const perdidaEstimada = computed(() => Number(linea.costo || 0) * Number(linea.cantidad || 0))
const productoSeleccionado = computed(() => productos.value.find((item) => item.value === linea.producto))
const presentacionesProducto = computed(() => {
  const producto = productoSeleccionado.value
  const presentaciones = (producto?.presentaciones || []).filter((item) => item.estado !== false)
  if (presentaciones.length) {
    return presentaciones.map((item) => ({
      label: `${item.nombrePresentacion || item.nombreUnidadMedida || producto.unidad} (factor ${item.factorConversion || 1})`,
      value: item.idPresentacion,
      factor: Number(item.factorConversion || 1),
      precioVenta: Number(item.precioVenta || 0)
    }))
  }
  return producto ? [{ label: producto.unidad, value: 0, factor: 1, precioVenta: producto.precioVenta || 0 }] : []
})
const presentacionSeleccionada = computed(() => presentacionesProducto.value.find((item) => item.value === linea.presentacionId) || presentacionesProducto.value[0])
const lotesRegistradosDisponibles = computed(() => lotesRegistrados.value.filter((item) => item.producto === linea.producto))
const opcionesLoteEncontrado = computed(() => lotesRegistradosDisponibles.value)
const opcionesLoteMerma = computed(() => lotesRegistradosDisponibles.value.filter((item) => Number(item.stock || 0) > 0))
const productosVencidos = computed(() => lotesRegistrados.value
  .filter((lote) => lote.fechaVencimiento && lote.fechaVencimiento < new Date().toISOString().slice(0, 10) && Number(lote.stock || 0) > 0)
  .map((lote) => ({ ...lote, productoLabel: nombreProducto(lote.producto), unidad: unidadProducto(lote.producto), label: `${nombreProducto(lote.producto)} - ${lote.codigo}`, value: lote.value })))
const requiereLoteExistente = computed(() => ['PRODUCTO_VENCIDO', 'MERMA'].includes(formulario.motivo))
const excedeStock = computed(() => formulario.tipo === 'SALIDA' && Number(linea.cantidad || 0) > Number(linea.stock || 0))
const puedeContinuar = computed(() => paso.value === 1 ? Boolean(formulario.motivo) : lineas.value.length > 0)
const puedeGuardar = computed(() => {
  if (!formulario.motivo || !formulario.observacion.trim() || !lineas.value.length) return false
  if (formulario.motivo === 'INVENTARIO_INICIAL') return Boolean(formulario.fechaLevantamiento && formulario.responsable.trim())
  if (formulario.motivo === 'PRODUCTO_ENCONTRADO') return Boolean(formulario.lugarHallazgo.trim() && formulario.fechaHallazgo && formulario.encontradoPor.trim())
  if (formulario.motivo === 'MERMA') return Boolean(formulario.tipoMerma && formulario.causaMerma.trim())
  return true
})

function limpiarLinea() {
  Object.assign(linea, { almacen: null, ubicacion: null, nombreUbicacion: '', producto: null, idExistencia: null, unidad: '', presentacionId: null, cantidadPresentacion: null, lote: null, nuevoLote: false, codigoLote: '', fechaFabricacion: '', fechaVencimiento: '', stock: 0, cantidad: null, costo: null, precioVenta: 0 })
  editando.value = -1
}
function seleccionarTipo(tipo) {
  formulario.tipo = tipo
  formulario.motivo = null
}
async function cambiarAlmacen(id) {
  linea.ubicacion = null
  if (!id) {
    ubicaciones.value = []
    return
  }
  try {
    const respuesta = await listarUbicacionesPaginado({ idAlmacen: id, estado: true, page: 0, size: 100 })
    ubicaciones.value = (respuesta?.content || []).map((item) => ({ label: `${item.codigo} - ${item.nombre}`, value: item.idUbicacion }))
  } catch (error) {
    ubicaciones.value = []
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudieron cargar las ubicaciones.' })
  }
}
function nombreAlmacen(id) { return almacenes.value.find((item) => item.value === id)?.label || '--' }
function nombreUbicacion(id) { return ubicaciones.value.find((item) => item.value === id)?.label || '--' }
function actualizarCantidadBase() {
  const cantidad = Number(linea.cantidadPresentacion)
  const factor = Number(presentacionSeleccionada.value?.factor || 1)
  const precio = Number(presentacionSeleccionada.value?.precioVenta || 0)
  if (precio > 0) linea.precioVenta = Number((precio / factor).toFixed(6))
  linea.cantidad = Number.isFinite(cantidad) && cantidad > 0
    ? Number((cantidad * factor).toFixed(6))
    : null
}
function seleccionarProducto(id) {
  const producto = productos.value.find((item) => item.value === id)
  linea.stock = producto?.stock || 0
  linea.unidad = producto?.unidad || ''
  linea.costo = producto?.costo || 0
  linea.precioVenta = producto?.precioVenta || 0
  linea.presentacionId = producto?.presentaciones?.find((item) => item.esPrincipal && item.estado !== false)?.idPresentacion
    || producto?.presentaciones?.find((item) => item.estado !== false)?.idPresentacion || 0
  linea.cantidadPresentacion = null
  linea.cantidad = null
  if (formulario.motivo === 'MERMA') {
    linea.idExistencia = null
    linea.almacen = null
    linea.ubicacion = null
    linea.nombreUbicacion = ''
    linea.stock = 0
    linea.lote = null
  }
  linea.lote = null
  linea.nuevoLote = false
  linea.codigoLote = ''
  linea.fechaFabricacion = ''
  linea.fechaVencimiento = ''
}
function seleccionarExistencia(id) {
  const existencia = existencias.value.find((item) => item.idExistencia === id)
  if (!existencia) return
  linea.almacen = existencia.idAlmacen || null
  linea.ubicacion = existencia.idUbicacion || null
  linea.nombreUbicacion = existencia.ubicacion || '--'
  linea.lote = existencia.idLote || null
  linea.codigoLote = existencia.codigoLote || ''
  linea.fechaVencimiento = existencia.fechaVencimiento || ''
  linea.stock = Number(existencia.stock || 0)
  linea.costo = Number(existencia.costo || 0)
  linea.unidad = existencia.unidad || productoSeleccionado.value?.unidad || ''
}
function seleccionarLote(id) {
  const lote = lotesRegistrados.value.find((item) => item.value === id)
  linea.codigoLote = lote?.codigo || ''
  linea.fechaFabricacion = lote?.fechaFabricacion || ''
  linea.fechaVencimiento = lote?.fechaVencimiento || ''
  if (lote?.stock != null) linea.stock = lote.stock
  if (lote?.costo != null) linea.costo = lote.costo
  if (formulario.motivo === 'MERMA') {
    linea.almacen = lote?.idAlmacen || linea.almacen
    linea.ubicacion = lote?.idUbicacion || linea.ubicacion
  }
}
function seleccionarProductoVencido(idLote) {
  const lote = lotesRegistrados.value.find((item) => item.value === idLote)
  const producto = productos.value.find((item) => item.value === lote?.producto)
  linea.producto = lote?.producto || null
  linea.unidad = producto?.unidad || ''
  linea.almacen = lote?.idAlmacen || null
  linea.ubicacion = lote?.idUbicacion || null
  linea.lote = idLote
  linea.nuevoLote = false
  linea.codigoLote = lote?.codigo || ''
  linea.fechaFabricacion = lote?.fechaFabricacion || ''
  linea.fechaVencimiento = lote?.fechaVencimiento || ''
  linea.stock = lote?.stock ?? producto?.stock ?? 0
  linea.costo = lote?.costo ?? producto?.costo ?? 0
  linea.precioVenta = producto?.precioVenta || 0
}
function cambiarModoLote(nuevo) {
  linea.lote = null
  if (nuevo) {
    linea.codigoLote = ''
    linea.fechaFabricacion = ''
    linea.fechaVencimiento = ''
  }
}
function bloquearNegativos(event) {
  if (event.key === '-') event.preventDefault()
}
function nombreProducto(id) { return productos.value.find((item) => item.value === id)?.label || '--' }
function unidadProducto(id) { return productos.value.find((item) => item.value === id)?.unidad || '--' }
function nombreLote(id) { return lotes.value.find((item) => item.value === id)?.label || '--' }
function formatearNumero(valor) {
  return Number(valor || 0).toFixed(2)
}
function resultadoLinea(item) { return formulario.tipo === 'ENTRADA' ? Number(item.stock) + Number(item.cantidad) : Number(item.stock) - Number(item.cantidad) }
function impactoLinea(item) { return formulario.tipo === 'SALIDA' ? Number(item.costo || 0) * Number(item.cantidad || 0) : Number(item.precioVenta || 0) - Number(item.costo || 0) }

function agregarLinea() {
  if (!linea.almacen || !linea.ubicacion || !linea.producto || !linea.cantidad || Number(linea.cantidad) <= 0) {
    return $q.notify({ type: 'warning', message: 'Completa almacen, ubicacion, producto y cantidad.' })
  }
  if (linea.costo == null || Number(linea.costo) <= 0) {
    return $q.notify({ type: 'warning', message: 'Ingresa un costo unitario mayor a cero.' })
  }
  if (formulario.tipo === 'SALIDA' && Number(linea.cantidad) > Number(linea.stock)) {
    return $q.notify({ type: 'warning', message: 'La salida no puede superar el stock actual.' })
  }
  const duplicado = lineas.value.some((item, index) => index !== editando.value
    && item.producto === linea.producto
    && item.lote === linea.lote
    && item.almacen === linea.almacen
    && item.ubicacion === linea.ubicacion)
  if (duplicado) {
    return $q.notify({ type: 'warning', message: 'Este producto, lote y ubicacion ya fueron agregados.' })
  }
  if (Number(linea.cantidad) < 0 || Number(linea.costo || 0) < 0) {
    return $q.notify({ type: 'warning', message: 'La cantidad y el costo no pueden ser negativos.' })
  }
  if (sinGanancia.value) {
    $q.notify({ type: 'warning', message: 'Advertencia: el costo es igual o mayor al precio de venta.' })
  }
  if (productoSeleccionado.value?.controlaLote && formulario.motivo === 'PRODUCTO_ENCONTRADO' && !linea.nuevoLote && !linea.lote) {
    return $q.notify({ type: 'warning', message: 'Selecciona un lote existente o activa Registrar nuevo lote.' })
  }
  if (productoSeleccionado.value?.controlaLote && requiereLoteExistente.value && !linea.lote) {
    return $q.notify({ type: 'warning', message: 'Selecciona el lote existente del producto.' })
  }
  if (productoSeleccionado.value?.controlaLote && !requiereLoteExistente.value && (formulario.motivo !== 'PRODUCTO_ENCONTRADO' || linea.nuevoLote) && !linea.codigoLote.trim()) {
    return $q.notify({ type: 'warning', message: 'Ingresa el codigo de lote del producto.' })
  }
  if (productoSeleccionado.value?.controlaVencimiento && !linea.fechaVencimiento) {
    return $q.notify({ type: 'warning', message: 'Ingresa la fecha de vencimiento del producto.' })
  }
  if (formulario.motivo === 'PRODUCTO_VENCIDO' && linea.fechaVencimiento >= new Date().toISOString().slice(0, 10)) {
    return $q.notify({ type: 'warning', message: 'El lote seleccionado todavía no está vencido.' })
  }
  const copia = { ...linea }
  if (editando.value >= 0) lineas.value.splice(editando.value, 1, copia)
  else lineas.value.push(copia)
  limpiarLinea()
}
function editarLinea(index) { Object.assign(linea, lineas.value[index]); editando.value = index }
function eliminarLinea(index) { lineas.value.splice(index, 1) }
function siguiente() {
  if (paso.value === 1) {
    if (!formulario.motivo) return $q.notify({ type: 'warning', message: 'Selecciona el motivo del ajuste.' })
    return (paso.value = 2)
  }
  if (!lineas.value.length) return $q.notify({ type: 'warning', message: 'Agrega al menos un producto.' })
  paso.value = 3
}
function guardar() {
  if (!formulario.motivo || !formulario.observacion.trim()) return $q.notify({ type: 'warning', message: 'El motivo y la observacion son obligatorios.' })
  if (formulario.motivo === 'INVENTARIO_INICIAL' && (!formulario.fechaLevantamiento || !formulario.responsable.trim())) {
    return $q.notify({ type: 'warning', message: 'Completa la fecha y el responsable del inventario inicial.' })
  }
  if (formulario.motivo === 'PRODUCTO_ENCONTRADO' && (!formulario.lugarHallazgo.trim() || !formulario.fechaHallazgo || !formulario.encontradoPor.trim())) {
    return $q.notify({ type: 'warning', message: 'Completa el lugar, fecha y responsable del hallazgo.' })
  }
  if (formulario.motivo === 'MERMA' && (!formulario.tipoMerma || !formulario.causaMerma.trim())) {
    return $q.notify({ type: 'warning', message: 'Completa el tipo y la causa de la merma.' })
  }
  emit('guardar', { ...formulario, idAjuste: props.ajusteEditar?.id || null, productos: lineas.value, cantidadTotal: cantidadTotal.value })
  emit('update:modelValue', false)
  paso.value = 1
  lineas.value = []
  limpiarLinea()
}
function reiniciarFormulario() {
  Object.assign(formulario, {
    tipo: 'ENTRADA',
    motivo: null,
    observacion: '',
    evidencia: null,
    fechaLevantamiento: '',
    responsable: '',
    acta: null,
    lugarHallazgo: '',
    fechaHallazgo: '',
    encontradoPor: '',
    tipoMerma: null,
    causaMerma: ''
  })
  lineas.value = []
  limpiarLinea()
  paso.value = 1
}

async function cargarFormularioEdicion(ajuste) {
  if (!catalogosListos.value) await cargarCatalogos()
  reiniciarFormulario()
  Object.assign(formulario, {
    tipo: ajuste.tipo,
    motivo: ajuste.motivo,
    observacion: ajuste.observacion || '',
    fechaLevantamiento: ajuste.fechaLevantamiento || '',
    responsable: ajuste.responsableLevantamiento || '',
    lugarHallazgo: ajuste.lugarHallazgo || '',
    fechaHallazgo: ajuste.fechaHallazgo || '',
    encontradoPor: ajuste.encontradoPor || ''
  })
  lineas.value = (ajuste.detalles || []).map((item) => {
    const existencia = existencias.value.find((registro) => registro.producto === item.idProducto
      && registro.idUbicacion === item.idUbicacion && registro.idLote === item.idLote)
    const producto = productos.value.find((registro) => registro.value === item.idProducto)
    const principal = producto?.presentaciones?.find((presentacion) => presentacion.esPrincipal && presentacion.estado !== false)
      || producto?.presentaciones?.find((presentacion) => presentacion.estado !== false)
    const factor = Number(principal?.factorConversion || 1)
    return {
      almacen: item.idAlmacen,
      ubicacion: item.idUbicacion,
      nombreUbicacion: item.ubicacion || '',
      producto: item.idProducto,
      idExistencia: existencia?.idExistencia || null,
      unidad: item.unidad || producto?.unidad || '',
      presentacionId: principal?.idPresentacion || 0,
      cantidadPresentacion: Number(item.cantidad || 0) / factor,
      lote: item.idLote,
      nuevoLote: false,
      codigoLote: item.codigoLote || '',
      fechaFabricacion: item.fechaFabricacion || '',
      fechaVencimiento: item.fechaVencimiento || '',
      stock: Number(item.stockAnterior || 0),
      cantidad: Number(item.cantidad || 0),
      costo: Number(item.costoUnitario || 0),
      precioVenta: producto?.precioVenta || 0
    }
  })
  paso.value = 3
}

watch([() => props.modelValue, () => props.ajusteEditar], async ([visible, ajuste]) => {
  if (!visible) return
  if (ajuste) await cargarFormularioEdicion(ajuste)
  else reiniciarFormulario()
})

async function cargarCatalogos() {
  try {
    const [almacenesRespuesta, productosRespuesta] = await Promise.all([
      listarAlmacenes({ page: 0, size: 100, estado: true, sortBy: 'nombre', sortDir: 'asc' }),
      listarProductos({ page: 0, size: 100, estado: true, sortBy: 'nombre', sortDir: 'asc' })
    ])
    const existenciasRespuesta = await cargarTodasLasExistencias()

    almacenes.value = (almacenesRespuesta?.content || []).map((item) => ({ label: item.nombre, value: item.idAlmacen }))
    productos.value = (productosRespuesta?.content || [])
      .filter((item) => item.estado !== false)
      .map((item) => ({
        label: `${item.codigoInterno || 'S/C'} - ${item.nombre}`,
        value: item.idProducto,
        unidad: item.abreviaturaUnidadBase || item.nombreUnidadBase || '--',
        stock: 0,
        costo: 0,
        presentaciones: item.presentaciones || [],
        precioVenta: item.presentaciones?.find((presentacion) => presentacion.esPrincipal)?.precioVenta
          ?? item.presentaciones?.[0]?.precioVenta
          ?? 0,
        controlaLote: Boolean(item.controlaLote),
        controlaVencimiento: Boolean(item.controlaVencimiento)
      }))
    existencias.value = (existenciasRespuesta?.content || []).map((item) => ({
      idExistencia: item.idExistencia,
      producto: item.idProducto,
      stock: Number(item.cantidad || 0),
      costo: Number(item.costoUnitario || 0),
      idUbicacion: item.idUbicacion,
      idAlmacen: almacenes.value.find((almacen) => almacen.label === item.almacen)?.value || null,
      almacen: item.almacen || '--',
      ubicacion: item.codigoUbicacion || item.nombreUbicacion || '--',
      idLote: item.idLote,
      codigoLote: item.codigoLote,
      fechaVencimiento: item.fechaVencimiento,
      unidad: item.unidadBase || '--'
    }))
    lotesRegistrados.value = (existenciasRespuesta?.content || [])
      .filter((item) => item.idLote != null)
      .map((item) => ({
        label: `${item.codigoLote} - ${item.nombreProducto}`,
        value: item.idLote,
        producto: item.idProducto,
        codigo: item.codigoLote,
        costo: Number(item.costoUnitario || 0),
        fechaFabricacion: '',
        fechaVencimiento: item.fechaVencimiento || '',
        stock: Number(item.cantidad || 0),
        almacen: item.almacen || '--',
        ubicacion: item.codigoUbicacion || item.nombreUbicacion || '--',
        idUbicacion: item.idUbicacion,
        idAlmacen: almacenes.value.find((almacen) => almacen.label === item.almacen)?.value || null
      }))
    productos.value.forEach((producto) => {
      producto.stock = existencias.value
        .filter((item) => item.producto === producto.value)
        .reduce((total, item) => total + Number(item.stock || 0), 0)
      producto.costo = existencias.value.find((item) => item.producto === producto.value && Number(item.costo || 0) > 0)?.costo || 0
    })
    catalogosListos.value = true
  } catch (error) {
    $q.notify({ type: 'negative', message: error?.response?.data?.message || 'No se pudieron cargar los datos del ajuste.' })
  }
}

async function cargarTodasLasExistencias() {
  const registros = []
  let page = 0
  let totalPages = 1
  do {
    const respuesta = await listarExistencias({ page, size: 100 })
    registros.push(...(respuesta?.content || []))
    totalPages = Number(respuesta?.totalPages || 1)
    page += 1
  } while (page < totalPages)
  return { content: registros }
}

onMounted(cargarCatalogos)
</script>

<style scoped>
.dialog { width: min(1500px, 96vw) !important; max-width: 1500px !important; max-height: 94vh; display: flex; flex-direction: column; border: 1px solid #dce9d7; border-radius: 20px; overflow: hidden; background: #fff; box-shadow: 0 18px 48px rgba(39, 72, 31, .16); }
.head { min-height: 78px; border-bottom: 1px solid #dfe7dc; background: linear-gradient(135deg, #edf8e7 0%, #fff 72%); padding: 20px 30px; }
.dialog :deep(.q-stepper) { display: flex; flex: 1 1 auto; min-height: 0; flex-direction: column; }
.dialog :deep(.q-stepper__content), .dialog :deep(.q-stepper__step-content), .dialog :deep(.q-stepper__step-inner) { min-height: 0; }
.dialog :deep(.q-stepper__content) { flex: 1 1 auto; overflow-y: auto; overflow-x: hidden; }
.dialog :deep(.q-stepper__step-content) { overflow: visible; }
.dialog :deep(.q-stepper__step-inner) { padding: 24px 30px 12px; }
.dialog :deep(.q-stepper__nav) { flex: 0 0 auto; padding: 16px 30px 22px; border-top: 1px solid #e1eadf; background: #fff; }
.title { font-size: 21px; font-weight: 900; color: #243323; }
.sub { font-size: 12px; color: #768178; }
.type { height: 100%; border-radius: 14px; border-color: #dfe7dc; cursor: pointer; }
.type.selected, .type:hover { border-color: #4a8c25; background: #f4faef; }
.type.locked { cursor: not-allowed; opacity: .78; }
.type.locked:hover { border-color: #dfe7dc; background: #fff; }
.type-title { font-weight: 900; margin-top: 8px; }
.line-form { padding: 22px; border: 1px solid #e1eadf; border-radius: 16px; background: #fbfdfb; }
.result { background: #eef8e9; color: #34572b; }
.summary { background: linear-gradient(135deg, #eef8e9, #f8fcf6); border: 1px solid #d9e8d2; color: #34572b; }
.summary-title { margin-bottom: 10px; font-weight: 900; color: #2f5e27; }
.summary-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; font-size: 12px; }
.specific-fields { padding: 16px; border: 1px solid #d9e8d2; border-radius: 14px; background: #fbfdf9; }
.specific-title { color: #356d20; font-size: 14px; font-weight: 900; }
.dialog :deep(.q-field--outlined .q-field__control:before) { border-color: #d8e4d4; }
.dialog :deep(.q-field--outlined.q-field--focused .q-field__control:after) { border-color: #4a8c25; }
.dialog :deep(.q-stepper__tab--active) { color: #4a8c25; }
.dialog :deep(.q-stepper__dot) { background: #4a8c25; }
.dialog :deep(.q-stepper__header) { border-bottom: 1px solid #e1eadf; background: #fcfefb; padding: 4px 18px; }
.dialog :deep(.q-stepper__tab) { min-height: 66px; font-weight: 800; }
.dialog :deep(.q-stepper__label) { font-size: 12px; }
.dialog :deep(.q-btn) { border-radius: 9px; font-weight: 800; }
.dialog :deep(.q-field) { margin-bottom: 2px; }
.dialog :deep(.q-field__label) { color: #657364; }
.dialog :deep(.q-field--readonly .q-field__control) { background: #f5f8f4; }
.motivo-select { width: 100%; max-width: none; flex: 0 0 100%; }
:global(.motivo-popup) { min-width: 520px !important; max-width: 760px; border: 1px solid #dce9d7; border-radius: 12px; box-shadow: 0 12px 28px rgba(39, 72, 31, .15); }
:global(.motivo-popup .q-item) { min-height: 58px; padding: 10px 16px; }
:global(.motivo-popup .q-item--active) { background: #eef8e9; color: #356d20; }
:global(.motivo-popup .q-item__label--caption) { color: #71806f; white-space: normal; line-height: 1.35; }
.result-help { margin-left: 12px; font-size: 12px; }
.profit-box { display: flex; flex-wrap: wrap; gap: 20px; padding: 11px 14px; border: 1px solid #d9e8d2; border-radius: 10px; background: #f5faF2; color: #42613c; font-size: 12px; }
.profit-box.negative { border-color: #f0c36d; background: #fff4df; color: #a66300; }
.loss-box { display: flex; flex-wrap: wrap; gap: 20px; padding: 11px 14px; border: 1px solid #ef9a9a; border-radius: 10px; background: #ffebee; color: #b71c1c; font-size: 12px; }
.loss-cell { color: #c62828; font-weight: 900; }
.profit-warning { background: #fff4df; border: 1px solid #f0d49a; color: #8a5a08; font-size: 12px; }
.lines-header { border-bottom: 1px solid #e1eadf; padding-bottom: 10px; }
.section-title { font-weight: 900; color: #354634; }
.total { color: #667067; font-size: 13px; }
.lines-table { border-radius: 12px; margin-top: 12px; }
.lines-table :deep(.q-table__middle) { max-height: 300px; overflow: auto; }
.lines-table th { color: #3f5140; background: #f0f8e9; font-weight: 900; }
.lines-table td, .expired-table td { color: #405043; font-size: 12px; white-space: nowrap; }
.expired-table th { position: sticky; top: 0; z-index: 1; }
.expired-table td { vertical-align: middle; }
.actions { white-space: nowrap; }
.empty-lines { display: flex; flex-direction: column; align-items: center; gap: 8px; padding: 28px; color: #7b857c; border: 1px dashed #cbdac6; border-radius: 12px; margin-top: 12px; }
.warning { background: #fff8e8; color: #775817; }
.expired-help { background: #fff4df; border: 1px solid #f0d49a; color: #8a5a08; font-size: 12px; }
.expired-panel { padding: 18px; border: 1px solid #f0d49a; border-radius: 16px; background: #fffaf1; }
.expired-table { border-radius: 12px; background: #fff; }
.expired-table th { color: #76500d; background: #fff1d4; font-weight: 900; }
.expired-table tr.selected { background: #fff0d2; }
.expired-quantity { padding-top: 16px; border-top: 1px solid #f0d49a; }
.stock-error { background: #ffebee; border: 1px solid #ef9a9a; color: #b71c1c; font-size: 12px; }
@media (min-width: 1400px) {
  .dialog { width: min(1580px, 90vw) !important; max-width: 1580px !important; }
  .line-form { padding: 26px; }
  .lines-table :deep(.q-table__middle) { max-height: 360px; }
}
@media (max-width: 900px) {
  .dialog { width: 96vw !important; max-width: 96vw !important; max-height: 95vh; }
  .head { padding: 16px; }
  .dialog :deep(.q-stepper__step-inner) { padding: 18px 18px 10px; }
  .dialog :deep(.q-stepper__nav) { padding: 14px 18px 18px; }
  .summary-grid { grid-template-columns: repeat(2, 1fr); }
  .motivo-select { width: 100%; }
  .lines-table { overflow-x: auto; }
  .lines-table :deep(table) { min-width: 900px; }
}
@media (max-width: 600px) {
  .dialog { width: 100vw !important; max-width: 100vw !important; max-height: 100vh; border-radius: 0; }
  .title { font-size: 18px; }
  .q-stepper { padding: 0 4px; }
  .dialog :deep(.q-stepper__step-inner) { padding: 16px 12px 8px; }
  .result-help { display: block; margin: 6px 0 0; }
  .profit-box, .loss-box { gap: 8px; flex-direction: column; }
  .summary-grid { grid-template-columns: 1fr; }
  :global(.motivo-popup) { min-width: 88vw !important; max-width: 92vw; }
}
</style>
