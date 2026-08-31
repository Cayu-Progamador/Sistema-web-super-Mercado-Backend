<template>
  <q-dialog v-model="visible" persistent :maximized="$q.screen.lt.sm" transition-show="slide-up"
    transition-hide="slide-down" class="transfer-dialog-wrap">
    <q-card class="transfer-dialog-card column">
      <div class="dialog-accent" />
      <q-card-section class="dialog-header row items-center no-wrap">
        <div class="dialog-icon">
          <q-icon name="swap_horiz" size="25px" color="green-8" />
        </div>
        <div class="col q-ml-md">
          <div class="dialog-title">Nueva Transferencia</div>
          <div class="dialog-sub">Registra el traslado interno de mercadería.</div>
        </div>
        <q-btn flat round dense icon="close" color="green-8" @click="cerrar" />
      </q-card-section>
      <q-form class="col column" @submit="guardar">
        <q-stepper v-model="paso" flat animated header-nav :vertical="$q.screen.lt.md" color="green-8"
          class="transfer-stepper col">
          <q-step :name="1" title="Datos generales" icon="info" :done="paso > 1">
            <PasoHeader icon="description" title="Datos generales"
              text="Agrega una referencia para identificar el motivo del traslado." />
            <q-input v-model.trim="form.observacion" type="textarea" outlined autogrow maxlength="250" counter
              label="Observación" placeholder="Ejemplo: Reposición de sala de ventas." color="green-8" />
            <q-banner class="info-banner q-mt-md" rounded>El número y la fecha se generan automáticamente.</q-banner>
          </q-step>
          <q-step :name="2" title="Origen" icon="outbox" :done="paso > 2">
            <PasoHeader icon="outbox" title="Punto de origen" text="Selecciona desde dónde saldrán los productos." />
            <div class="row q-col-gutter-md">
              <div class="col-12 col-sm-6">
                <q-select v-model="form.origen" :options="almacenes" emit-value map-options outlined
                  label="Almacén de origen *" color="green-8" :loading="cargandoAlmacenes"
                  :rules="[v => !!v || 'Selecciona el origen']" />
              </div>
              <div class="col-12 col-sm-6">
                <q-select v-model="form.ubicacionOrigen" :options="ubicaciones" emit-value map-options outlined
                  label="Ubicación de origen *" color="green-8" :loading="cargandoUbicaciones" :disable="!form.origen"
                  :rules="[v => !!v || 'Selecciona la ubicación']" />
              </div>
            </div>
          </q-step>
          <q-step :name="3" title="Destino" icon="move_to_inbox" :done="paso > 3">
            <PasoHeader icon="move_to_inbox" title="Punto de destino"
              text="Indica el lugar que recibirá los productos." />
            <div class="row q-col-gutter-md">
              <div class="col-12 col-sm-6">
                <q-select v-model="form.destino" :options="almacenesDestino" emit-value map-options outlined
                  label="Almacén de destino *" color="green-8" :loading="cargandoAlmacenes" :disable="!form.origen"
                  :rules="[v => !!v || 'Selecciona el destino']" />
              </div>
              <div class="col-12 col-sm-6">
                <q-select v-model="form.ubicacionDestino" :options="ubicacionesDestino" emit-value map-options outlined
                  label="Ubicación destino *" color="green-8" :loading="cargandoUbicacionesDestino"
                  :disable="!form.destino" :rules="[v => !!v || 'Selecciona la ubicación destino']" />
              </div>
            </div>
            <q-banner v-if="form.destino === form.origen && form.destino" class="warning q-mt-md" rounded dense>El
              origen y el destino deben ser diferentes.
            </q-banner>
          </q-step>
          <q-step :name="4" title="Productos" icon="inventory_2" :done="paso > 4">
            <PasoHeader icon="inventory_2" title="Productos de la transferencia"
              text="Agrega los productos y cantidades que serán trasladados." />
            <div class="row justify-between items-center q-mb-md">
              <span class="text-caption text-grey-7">Disponibles
                según la ubicación de origen seleccionada.
              </span>
              <q-btn outline color="green-8" icon="add" label="Agregar producto" no-caps
                :disable="!form.ubicacionOrigen" :loading="cargandoProductos" @click="abrirSelectorProducto" />
            </div>
            <q-list v-if="form.productos.length" bordered separator class="productos-lista">
              <q-item v-for="(p, i) in form.productos" :key="i">
                <q-item-section avatar>
                  <q-avatar color="green-1" text-color="green-8" icon="inventory_2" />
                </q-item-section>
                <q-item-section>
                  <q-item-label>{{
                    p.producto }}
                  </q-item-label>
                  <q-item-label caption>{{ p.lote }} · Disponible: {{ p.stockDisponible }}
                    {{ p.unidad }}
                  </q-item-label>
                </q-item-section>
                <q-item-section side class="row items-center no-wrap q-gutter-sm">
                  <q-input v-model="p.cantidad" type="number" dense outlined min="1" :max="p.stockDisponible"
                    style="width:100px" @update:model-value="ajustarCantidad(p)" />
                  <q-btn flat round color="negative" icon="delete_outline" @click="form.productos.splice(i, 1)" />
                </q-item-section>
              </q-item>
            </q-list>
            <q-banner v-else class="empty-banner" rounded>
              <q-icon name="inventory_2" size="22px" class="q-mr-sm" />No
              hay productos agregados.
            </q-banner>
          </q-step>
          <q-step :name="5" title="Validación" icon="fact_check" :done="paso > 5">
            <PasoHeader icon="fact_check" title="Validación de datos"
              text="Comprueba que la transferencia esté lista para continuar." />
            <q-banner rounded :class="validacionOk ? 'ok' : 'warning'" class="q-mb-md">
              {{ validacionOk ? 'La transferencia está lista para ser confirmada.' : 'Completa los requisitos pendientes antes de confirmar.'}}
            </q-banner>
            <q-list bordered separator class="validacion-lista">
              <q-item v-for="item in validacionItems" :key="item.label">
                <q-item-section avatar>
                  <q-icon :name="item.ok ? 'check_circle' : 'radio_button_unchecked'"
                    :color="item.ok ? 'positive' : 'grey-5'" size="21px" />
                </q-item-section>
                <q-item-section>
                  <q-item-label :class="item.ok ? 'requisito-ok' : 'requisito-pendiente'">{{ item.label
                  }}
                  </q-item-label>
                </q-item-section>
                <q-item-section side>
                  <q-badge :color="item.ok ? 'positive' : 'grey-6'" :label="item.ok ? 'Listo' : 'Pendiente'" />
                </q-item-section>
              </q-item>
            </q-list>
          </q-step>
          <q-step :name="6" title="Confirmación" icon="check_circle">
            <PasoHeader icon="check_circle" title="Revisión final"
              text="Verifica la información antes de confirmar la transferencia." />
            <div class="origen-destino">
              <div>
                <small>ORIGEN</small>
                <strong>{{ nombreAlmacen(form.origen) || '-' }}</strong>
                <span>{{
                  nombreUbicacion(form.ubicacionOrigen) }}
                </span>
              </div>
              <q-icon name="arrow_forward" color="green-8" size="26px" />
              <div>
                <small>DESTINO</small>
                <strong>{{ nombreAlmacen(form.destino) || '-' }}</strong>
                <span>{{
                  nombreUbicacion(form.ubicacionDestino) }}
                </span>
              </div>
            </div>
            <q-separator class="q-my-md" />
            <div v-for="p in form.productos" :key="p.producto + p.lote"
              class="producto-resumen row justify-between text-caption q-py-sm">
              <span>{{ p.producto }} · {{ p.lote }}</span>
              <strong>{{ p.cantidad }} {{ p.unidad }}</strong>
            </div>
          </q-step>
          <template #navigation>
            <q-stepper-navigation class="dialog-footer q-pa-md">
              <div class="row items-center justify-between">
                <div class="row items-center q-gutter-sm">
                  <q-btn v-if="paso === 1" flat no-caps color="green-8" icon="close" label="Cancelar" @click="cerrar" />
                  <q-btn v-else flat no-caps color="green-8" icon="arrow_back" label="Atrás" @click="paso--" />
                  <q-btn flat no-caps color="green-8" icon="drafts" label="Guardar borrador" @click="guardarBorrador" />
                </div>
                <q-btn v-if="paso < 6" unelevated no-caps color="green-8" label="Continuar" icon-right="arrow_forward"
                  @click="siguiente" />
                <q-btn v-else unelevated no-caps color="green-8" label="Confirmar Transferencia" icon="save"
                  type="submit" :disable="!validacionOk" />
              </div>
            </q-stepper-navigation>
          </template>
        </q-stepper>
      </q-form>
    </q-card>
    <q-dialog v-model="dialogProducto" persistent>
      <q-card class="producto-dialog">
        <q-card-section class="row items-center dialog-header">
          <div class="dialog-icon">
            <q-icon name="inventory_2" color="green-8" size="24px" />
          </div>
          <div class="col q-ml-md">
            <div class="dialog-title">Agregar producto</div>
            <div class="dialog-sub">Selecciona un producto disponible en la ubicación de origen.</div>
          </div>
          <q-btn flat round dense icon="close" color="green-8" @click="dialogProducto = false" />
        </q-card-section>
        <q-card-section class="q-pa-lg">
          <q-select v-model="productoSeleccionado" :options="productosDisponibles" outlined use-input fill-input
            hide-selected input-debounce="0" label="Producto y lote *" color="green-8" option-label="label"
            @filter="filtrarProductos">
            <template #selected-item="scope">
              <span class="selected-product-label">{{ scope.opt.producto }} · {{
                scope.opt.lote || 'Sin lote' }}
              </span>
            </template>
            <template #option="scope">
              <q-item v-bind="scope.itemProps">
                <q-item-section avatar>
                  <q-avatar color="green-1" text-color="green-8" icon="inventory_2" />
                </q-item-section>
                <q-item-section>
                  <q-item-label>{{
                    scope.opt.producto }}
                  </q-item-label>
                  <q-item-label caption>
                    Lote: {{ scope.opt.lote || 'Sin lote' }} · Stock: {{ scope.opt.stock }} {{ scope.opt.unidad }}
                  </q-item-label>
                </q-item-section>
              </q-item>
            </template>
          </q-select>
          <div v-if="productoSeleccionado" class="producto-seleccionado q-mt-md"
            style="font-family:'Nunito',sans-serif;line-height:1.4">
            <div class="producto-seleccionado-icon">
              <q-icon name="inventory_2" size="24px" />
            </div>
            <div class="col">
              <strong style="font-size:15px;font-weight:900;color:#263238">{{
                productoSeleccionado.producto }}
              </strong>
              <div class="producto-meta" style="font-size:12px;font-weight:600;color:#62705f">
                <span>Lote: {{
                  productoSeleccionado.lote || 'Sin lote' }}</span>
                <span>Disponible: <b style="color:#397020;font-weight:900">{{ productoSeleccionado.stock }} {{
                  productoSeleccionado.unidad
                }}</b>
                </span>
                <span v-if="productoSeleccionado.vencimiento">Vence: {{
                  productoSeleccionado.vencimiento
                }}
                </span>
              </div>
            </div>
          </div>
          <q-input v-model="cantidadProducto" type="number" outlined label="Cantidad a transferir *" color="green-8"
            class="q-mt-md" :disable="!productoSeleccionado"
            :hint="productoSeleccionado ? `Disponible: ${productoSeleccionado.stock} ${productoSeleccionado.unidad}` : 'Selecciona primero un producto'"
            persistent-hint min="1" :max="productoSeleccionado?.stock" step="any"
            :rules="[cantidadRequerida, cantidadPositiva, cantidadDisponible]" />
        </q-card-section>
        <q-card-actions align="right" class="dialog-footer q-pa-md">
          <q-btn flat no-caps color="green-8" label="Cancelar" @click="dialogProducto = false" />
          <q-btn unelevated no-caps color="green-8" icon="add" label="Agregar producto" @click="agregarProducto" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-dialog>
</template>

<script setup>
import { computed, defineComponent, h, onMounted, reactive, ref, watch, useAttrs } from 'vue'
import { useQuasar } from 'quasar'
import { listarAlmacenes } from '../../api/almacen/almacen'
import { listarProductosUbicacion, listarUbicacionesPaginado } from '../../api/ubicacionAlmacen/ubicacionAlmacen'
const PasoHeader = defineComponent({ props: { icon: String, title: String, text: String }, setup: p => () => h('div', { class: 'paso-head' }, [h('span', { class: 'material-icons paso-icon' }, p.icon), h('div', [h('strong', p.title), h('small', p.text)])]) })
const $q = useQuasar(); const props = defineProps({ modelValue: Boolean }); const emit = defineEmits(['update:modelValue', 'guardar']); const visible = computed({ get: () => props.modelValue, set: v => emit('update:modelValue', v) }); const paso = ref(1); const almacenes = ref([]); const ubicaciones = ref([]); const ubicacionesDestino = ref([]); const inventario = ref([]); const productosDisponibles = ref([]); const cargandoAlmacenes = ref(false); const cargandoUbicaciones = ref(false); const cargandoUbicacionesDestino = ref(false); const cargandoProductos = ref(false); const dialogProducto = ref(false); const productoSeleccionado = ref(null); const cantidadProducto = ref(null); const form = reactive({ origen: null, destino: null, ubicacionOrigen: null, ubicacionDestino: null, observacion: '', productos: [] }); const almacenesDestino = computed(() => almacenes.value.filter(a => a.value !== form.origen)); const cantidadesValidas = computed(() => form.productos.length > 0 && form.productos.every(p => Number(p.cantidad) > 0 && Number(p.cantidad) <= Number(p.stockDisponible))); const validacionItems = computed(() => [{ label: 'Almacén de origen seleccionado', ok: !!form.origen }, { label: 'Ubicación de origen seleccionada', ok: !!form.ubicacionOrigen }, { label: 'Almacén de destino seleccionado', ok: !!form.destino }, { label: 'Ubicación de destino seleccionada', ok: !!form.ubicacionDestino }, { label: 'Origen y destino son diferentes', ok: !!form.origen && !!form.destino && form.origen !== form.destino }, { label: 'Al menos un producto agregado', ok: form.productos.length > 0 }, { label: 'Cantidades válidas y dentro del stock disponible', ok: cantidadesValidas.value }]); const validacionOk = computed(() => validacionItems.value.every(item => item.ok))
function siguiente() { if (paso.value === 2 && (!form.origen || !form.ubicacionOrigen)) { $q.notify({ type: 'negative', message: 'Selecciona el almacén y la ubicación de origen.' }); return } if (paso.value === 3 && (!form.destino || form.origen === form.destino)) { $q.notify({ type: 'negative', message: 'Selecciona un destino diferente al origen.' }); return } if (paso.value === 4 && !form.productos.length) { $q.notify({ type: 'negative', message: 'Agrega al menos un producto.' }); return } paso.value++ }
async function cargarAlmacenes() { cargandoAlmacenes.value = true; try { const r = await listarAlmacenes({ page: 0, size: 100, sortBy: 'nombre', sortDir: 'asc', estado: true }); almacenes.value = (r?.content || []).map(a => ({ label: a.nombre, value: a.idAlmacen })) } catch { almacenes.value = []; $q.notify({ type: 'negative', message: 'No se pudieron cargar los almacenes.' }) } finally { cargandoAlmacenes.value = false } }
async function cargarUbicacionesOrigen(idAlmacen) { ubicaciones.value = []; inventario.value = []; form.ubicacionOrigen = null; form.productos = []; if (!idAlmacen) return; cargandoUbicaciones.value = true; try { const r = await listarUbicacionesPaginado({ idAlmacen: Number(idAlmacen), estado: true, page: 0, size: 100 }); ubicaciones.value = (r?.content || []).map(u => ({ label: `${u.codigo} · ${u.nombre}`, value: u.idUbicacion })) } catch { $q.notify({ type: 'negative', message: 'No se pudieron cargar las ubicaciones del almacén.' }) } finally { cargandoUbicaciones.value = false } }
async function cargarUbicacionesDestino(idAlmacen) { ubicacionesDestino.value = []; form.ubicacionDestino = null; if (!idAlmacen) return; cargandoUbicacionesDestino.value = true; try { const r = await listarUbicacionesPaginado({ idAlmacen: Number(idAlmacen), estado: true, page: 0, size: 100 }); ubicacionesDestino.value = (r?.content || []).map(u => ({ label: `${u.codigo} · ${u.nombre}`, value: u.idUbicacion })) } catch { $q.notify({ type: 'negative', message: 'No se pudieron cargar las ubicaciones destino.' }) } finally { cargandoUbicacionesDestino.value = false } }
async function cargarInventario(idUbicacion) { inventario.value = []; form.productos = []; if (!idUbicacion) return; cargandoProductos.value = true; try { const r = await listarProductosUbicacion(idUbicacion); inventario.value = (Array.isArray(r) ? r : []).filter(p => Number(p.cantidad) > 0).map((p, i) => ({ ...p, id: `${p.producto}-${p.lote || i}`, stock: Number(p.cantidad), label: `${p.producto} · ${p.lote || 'Sin lote'} · ${p.cantidad} ${p.unidad}` })); productosDisponibles.value = [...inventario.value] } catch { $q.notify({ type: 'negative', message: 'No se pudo cargar el inventario de la ubicación.' }) } finally { cargandoProductos.value = false } }
function abrirSelectorProducto() { if (!inventario.value.length) { $q.notify({ type: 'warning', message: 'La ubicación seleccionada no tiene productos con stock disponible.' }); return } productoSeleccionado.value = null; cantidadProducto.value = null; productosDisponibles.value = [...inventario.value]; dialogProducto.value = true }
function filtrarProductos(valor, actualizar) { actualizar(() => { const q = (valor || '').toLowerCase(); productosDisponibles.value = inventario.value.filter(p => p.label.toLowerCase().includes(q)) }) }
function cantidadValida(valor) { if (valor === null || valor === undefined || String(valor).trim() === '') return false; const texto = String(valor).trim(); if (!/^(?:\d+\.?\d*|\.\d+)$/.test(texto)) return false; const numero = Number(texto); return Number.isFinite(numero) && numero > 0 }
function cantidadRequerida(v) { return cantidadValida(v) || 'Ingresa una cantidad válida.' } function cantidadPositiva(v) { return cantidadValida(v) || 'La cantidad debe ser mayor que cero.' } function cantidadDisponible(v) { const p = productoSeleccionado.value; if (!p || !cantidadValida(v)) return true; const existente = form.productos.find(x => x.id === p.id); return (Number(existente?.cantidad || 0) + Number(v)) <= Number(p.stock) || `No puedes superar el stock disponible (${p.stock} ${p.unidad}).` }
function agregarProducto() { const p = productoSeleccionado.value; const texto = String(cantidadProducto.value ?? '').trim(); const cantidad = Number(texto); if (!p) { $q.notify({ type: 'negative', message: 'Selecciona un producto.' }); return } if (!cantidadValida(texto)) { $q.notify({ type: 'negative', message: 'La cantidad debe ser un número mayor que cero. No se permiten símbolos ni valores negativos.' }); return } const existente = form.productos.find(x => x.id === p.id); const acumulado = (existente?.cantidad || 0) + cantidad; if (acumulado > p.stock) { $q.notify({ type: 'negative', message: `La cantidad supera el stock disponible (${p.stock} ${p.unidad}).` }); return } if (existente) existente.cantidad = acumulado; else form.productos.push({ id: p.id, producto: p.producto, lote: p.lote || 'Sin lote', cantidad, unidad: p.unidad, stockDisponible: p.stock }); dialogProducto.value = false }
function ajustarCantidad(producto) { const valor = String(producto.cantidad ?? '').trim(); if (!cantidadValida(valor)) { producto.cantidad = 1; $q.notify({ type: 'negative', message: 'La cantidad debe ser un número positivo.' }); return } if (Number(valor) > Number(producto.stockDisponible)) { producto.cantidad = Number(producto.stockDisponible); $q.notify({ type: 'negative', message: `La cantidad máxima disponible es ${producto.stockDisponible} ${producto.unidad}.` }) } else producto.cantidad = Number(valor) }
function nombreAlmacen(id) { return almacenes.value.find(a => a.value === id)?.label || '' } function nombreUbicacion(id) { return [...ubicaciones.value, ...ubicacionesDestino.value].find(u => u.value === id)?.label || '' }
function cerrar() { visible.value = false; paso.value = 1; form.productos = []; form.origen = null; form.destino = null; form.ubicacionOrigen = null; form.ubicacionDestino = null; form.observacion = ''; inventario.value = []; ubicaciones.value = []; ubicacionesDestino.value = [] }
function guardarBorrador() { emit('guardar', { ...form, idUbicacionOrigen: form.ubicacionOrigen, idUbicacionDestino: form.ubicacionDestino, borrador: true, productos: form.productos.map(p => ({ ...p })) }) }
function guardar() { if (!validacionOk.value) { $q.notify({ type: 'negative', message: 'Completa los datos de la transferencia.' }); return } const origen = nombreAlmacen(form.origen); const destino = nombreAlmacen(form.destino); emit('guardar', { ...form, idUbicacionOrigen: form.ubicacionOrigen, idUbicacionDestino: form.ubicacionDestino, origen, destino, ubicacionOrigen: nombreUbicacion(form.ubicacionOrigen), ubicacionDestino: nombreUbicacion(form.ubicacionDestino) }); cerrar() }
watch(() => form.origen, (valor, anterior) => { if (valor !== anterior) { form.destino = null; cargarUbicacionesOrigen(valor) } })
watch(() => form.destino, (valor, anterior) => { if (valor !== anterior) cargarUbicacionesDestino(valor) })
watch(() => form.ubicacionOrigen, (valor, anterior) => { if (valor !== anterior) cargarInventario(valor) })
watch(() => form.productos, (lista) => { lista.forEach(item => { const fuente = inventario.value.find(p => p.id === item.id); if (fuente) { item.idProducto = fuente.idProducto; item.idLote = fuente.idLote || null } }) }, { deep: true })
const attrs = useAttrs()
watch(() => props.modelValue, (abierto) => { if (!abierto) delete window.__transferenciaBorradorId })
async function cargarBorradorEnFormulario(t) {
  if (!t) return
  form.idTransferencia = t.idTransferencia
  window.__transferenciaBorradorId = t.idTransferencia
  form.observacion = t.observacion || ''
  form.origen = t.idAlmacenOrigen || null
  form.destino = t.idAlmacenDestino || null
  await cargarUbicacionesOrigen(t.idAlmacenOrigen)
  form.ubicacionOrigen = t.idUbicacionOrigen || null
  await cargarInventario(t.idUbicacionOrigen)
  await cargarUbicacionesDestino(t.idAlmacenDestino)
  form.ubicacionDestino = t.idUbicacionDestino || null
  form.productos = (t.detalles || []).map(d => ({ id: `${d.idProducto}-${d.idLote || 'sin'}`, idProducto: d.idProducto, idLote: d.idLote || null, producto: d.producto, lote: d.lote || 'Sin lote', cantidad: Number(d.cantidad), unidad: 'unidades', stockDisponible: Number(d.cantidad) }))
  paso.value = 1
}
watch(() => attrs.transferencia, (t) => { if (t) cargarBorradorEnFormulario(t) }, { immediate: true })
onMounted(cargarAlmacenes)
</script>

<style scoped>
.transfer-dialog-wrap {
  font-family: 'Nunito', sans-serif;
  color: #263238
}

.transfer-dialog-wrap :deep(.q-dialog__inner) {
  padding: 18px
}

.transfer-dialog-card {
  width: 94vw;
  max-width: 1120px;
  height: 88vh;
  max-height: 820px;
  border-radius: 20px;
  overflow: hidden
}

.producto-dialog {
  width: 92vw;
  max-width: 600px;
  border-radius: 18px;
  overflow: hidden
}

.dialog-accent {
  height: 4px;
  flex: none;
  background: linear-gradient(90deg, #4a8c25, #82bd43)
}

.dialog-header {
  flex: none;
  padding: 18px 22px;
  background: linear-gradient(135deg, #f1f8eb, #e8f3df);
  border-bottom: 1px solid #d7e7ca
}

.dialog-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  flex: none;
  background: #fff;
  border: 1px solid #cce1b8
}

.dialog-title {
  font-size: clamp(18px, 1.3vw, 22px);
  line-height: 1.2;
  font-weight: 900;
  letter-spacing: -.2px;
  color: #263238
}

.dialog-sub {
  font-size: clamp(12px, .85vw, 14px);
  line-height: 1.45;
  font-weight: 500;
  color: #6b7280;
  margin-top: 3px
}

.transfer-stepper {
  box-shadow: none;
  min-height: 0;
  overflow: hidden;
  font-family: inherit
}

.transfer-stepper :deep(.q-stepper__header) {
  flex: none;
  background: #fbfcfa;
  border-bottom: 1px solid #dfe7dc;
  overflow-x: auto;
  flex-wrap: nowrap
}

.transfer-stepper :deep(.q-stepper__tab) {
  min-width: 135px;
  padding: 15px 10px
}

.transfer-stepper :deep(.q-stepper__title) {
  font-size: 12px;
  font-weight: 800;
  letter-spacing: .1px
}

.transfer-stepper :deep(.q-stepper__caption) {
  font-weight: 600
}

.transfer-stepper :deep(.q-stepper__content) {
  height: 100%;
  min-height: 0;
  overflow-x: hidden;
  overflow-y: auto
}

.transfer-stepper :deep(.q-stepper__step-inner) {
  padding: 28px 34px;
  min-height: 390px
}

.paso-head {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 13px 15px;
  margin-bottom: 20px;
  background: #f3f8ef;
  border: 1px solid #dbe8d3;
  border-radius: 12px
}

.paso-icon {
  font-size: 25px;
  color: #4a8c25
}

.paso-head strong,
.paso-head small {
  display: block
}

.paso-head strong {
  font-size: clamp(14px, 1vw, 16px);
  line-height: 1.3;
  font-weight: 900;
  color: #263238
}

.paso-head small {
  font-size: clamp(11px, .8vw, 13px);
  line-height: 1.45;
  font-weight: 500;
  color: #6b7280;
  margin-top: 2px
}

.info-banner {
  background: #f3f8ef;
  color: #61705c;
  border: 1px solid #dbe8d3;
  font-size: 13px;
  font-weight: 600
}

.productos-lista {
  border-color: #dfe7dc;
  border-radius: 12px;
  overflow: hidden
}

.productos-lista :deep(.q-item__label) {
  font-weight: 800
}

.productos-lista :deep(.q-item__label--caption) {
  font-size: 12px;
  font-weight: 500
}

.empty-banner {
  background: #f7faf5;
  color: #7b8678;
  border: 1px dashed #cfdaca;
  text-align: center;
  font-weight: 700
}

.origen-destino {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: #f7faf5;
  border: 1px solid #dfe7dc;
  border-radius: 12px;
  text-align: center
}

.origen-destino small,
.origen-destino strong {
  display: block
}

.origen-destino small {
  color: #889284;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: .8px
}

.origen-destino strong {
  color: #263238;
  font-size: 15px;
  font-weight: 900;
  margin-top: 4px
}

.producto-resumen {
  border-bottom: 1px solid #edf0ec;
  font-size: 13px
}

.ok {
  background: #edf8e8;
  color: #397020;
  border: 1px solid #cfe6c4;
  font-weight: 700
}

.warning {
  background: #fff7e8;
  color: #9a6510;
  border: 1px solid #f0ddb8;
  font-weight: 700
}

.dialog-footer {
  position: sticky;
  bottom: 0;
  z-index: 2;
  background: #f8faf7;
  border-top: 1px solid #dfe7dc
}

.dialog-footer :deep(.q-btn__content) {
  font-weight: 800;
  letter-spacing: 0
}

:deep(.q-field__control) {
  border-radius: 10px
}

:deep(.q-field__label) {
  font-weight: 700
}

:deep(.q-field__native),
:deep(.q-field__input) {
  font-size: 14px;
  font-weight: 600;
  color: #344238
}

@media(min-width:1440px) {
  .transfer-dialog-card {
    max-width: 1280px;
    height: 86vh;
    max-height: 900px
  }

  .dialog-header {
    padding: 22px 28px
  }

  .dialog-icon {
    width: 54px;
    height: 54px
  }

  .transfer-stepper :deep(.q-stepper__tab) {
    min-width: 155px;
    padding: 18px 14px
  }

  .transfer-stepper :deep(.q-stepper__step-inner) {
    padding: 36px 46px;
    min-height: 470px
  }

  .paso-head {
    padding: 16px 18px;
    margin-bottom: 26px
  }

  .dialog-footer {
    padding: 18px 26px !important
  }
}

@media(min-width:1920px) {
  .transfer-dialog-card {
    max-width: 1420px;
    max-height: 940px
  }

  .transfer-stepper :deep(.q-stepper__step-inner) {
    padding: 42px 56px;
    min-height: 510px
  }
}

@media(max-width:1023px) {
  .transfer-dialog-card {
    max-width: 760px;
    height: 92vh
  }

  .transfer-stepper :deep(.q-stepper__header) {
    overflow: visible;
    display: block
  }

  .transfer-stepper :deep(.q-stepper__tab) {
    min-width: 0
  }

  .transfer-stepper :deep(.q-stepper__step-inner) {
    padding: 20px 24px;
    min-height: auto
  }
}

@media(max-width:599px) {
  .transfer-dialog-wrap :deep(.q-dialog__inner) {
    padding: 0
  }

  .transfer-dialog-card {
    width: 100vw;
    height: 100vh;
    max-width: none;
    max-height: none;
    border-radius: 0
  }

  .dialog-header {
    padding: 13px 14px
  }

  .dialog-icon {
    width: 42px;
    height: 42px
  }

  .dialog-title {
    font-size: 16px
  }

  .dialog-sub {
    font-size: 10.5px
  }

  .transfer-stepper :deep(.q-stepper__step-inner) {
    padding: 16px 14px
  }

  .paso-head {
    padding: 11px 12px;
    margin-bottom: 14px
  }

  .origen-destino {
    grid-template-columns: 1fr;
    gap: 12px
  }

  .origen-destino>.q-icon {
    transform: rotate(90deg)
  }

  .dialog-footer {
    padding: 10px !important
  }

  .dialog-footer .q-btn {
    font-size: 12px
  }
}
</style>
