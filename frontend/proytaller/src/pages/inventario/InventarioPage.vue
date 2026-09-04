<template><q-page class="inventory-page">
        <div class="inventory-container">
            <header class="page-header">
                <div class="row items-center no-wrap">
                    <div class="header-icon"><q-icon name="inventory" size="29px" /></div>
                    <div class="q-ml-md">
                        <h1>Inventario</h1>
                        <p>Consulta y controla existencias, lotes, Kardex y vencimientos.</p>
                    </div>
                </div>
                <div class="row q-gutter-sm"><q-btn outline no-caps color="green-8" icon="refresh" label="Actualizar"
                        @click="actualizar" /></div>
            </header>
            <InventarioResumen :items="resumen" /><q-card flat bordered class="main-card"><q-tabs v-model="tab"
                    active-color="green-9" indicator-color="green-8" align="left"><q-tab name="existencias"
                        label="Existencias" icon="inventory_2" /><q-tab name="lotes" label="Lotes" icon="style" /><q-tab
                        name="kardex" label="Kardex" icon="receipt_long" /><q-tab name="vencimientos"
                        label="Vencimientos" icon="event_busy" /></q-tabs><q-separator /><q-tab-panels v-model="tab"
                    animated><q-tab-panel name="existencias">
                        <ExistenciasTab :rows="existencias" @detalle="abrirDetalle" @kardex="verKardexProducto" />
                    </q-tab-panel><q-tab-panel name="lotes">
                        <LotesTab :rows="lotes" @editar="editarLote" @kardex="verKardex" />
                    </q-tab-panel><q-tab-panel name="kardex">
                        <KardexTab :producto-id="kardexProductoId" :lote-id="kardexLoteId"
                            :consulta-key="kardexConsultaKey" />
                    </q-tab-panel><q-tab-panel name="vencimientos">
                        <VencimientosTab @kardex="verKardex" />
                    </q-tab-panel></q-tab-panels></q-card>
        </div>
        <DetalleExistenciaDialog v-model="detalle" :item="seleccionado" @kardex="verKardexProducto" />
        <LoteFormDialog v-model="loteDialog" :lote="loteSeleccionado" />
    </q-page></template>
<script
    setup>    
    import { computed, ref } from 'vue'; 
    import { useQuasar } from 'quasar'; 
    import InventarioResumen from '../../components/inventario/InventarioResumen.vue'; 
    import ExistenciasTab from '../../components/inventario/ExistenciasTab.vue'; 
    import LotesTab from '../../components/inventario/LotesTab.vue'; 
    import KardexTab from '../../components/inventario/KardexTab.vue'; 
    import VencimientosTab from '../../components/inventario/VencimientosTab.vue'; 
    import DetalleExistenciaDialog from '../../components/inventario/DetalleExistenciaDialog.vue'; 
    import LoteFormDialog from '../../components/inventario/LoteFormDialog.vue'; 
    const kardexProductoId = ref(null)
    const kardexLoteId = ref(null)
    const kardexConsultaKey = ref(0)
     function verKardex(lote) {
        kardexProductoId.value = lote.idProducto
        kardexLoteId.value = lote.idLote
        kardexConsultaKey.value++
        tab.value = 'kardex'
    }
    function verKardexProducto(producto) {
        kardexProductoId.value = producto.idProducto
        kardexLoteId.value = null
        kardexConsultaKey.value++
        detalle.value = false
        tab.value = 'kardex'
    }
    const $q = useQuasar(), tab = ref('existencias'), detalle = ref(false), loteDialog = ref(false), seleccionado = ref(null), loteSeleccionado = ref(null);
     const existencias = ref([{ idProducto: 1, codigo: 'SKU-001', nombre: 'Coca-Cola 2L', categoria: 'Bebidas', unidadBase: 'UND', almacen: 'Almacén Principal', ubicacion: 'A-01-03', lote: 'LT-00825', vencimiento: '20/12/2026', stockActual: 35, stockMinimo: 10, stockMaximo: 80, estado: 'DISPONIBLE', lotes: [{ idLote: 1, codigoLote: 'LT-00825', cantidad: 35, almacen: 'Almacén Principal', ubicacion: 'A-01-03', fechaVencimiento: '20/12/2026' }] }, { idProducto: 2, codigo: 'SKU-002', nombre: 'Leche Entera 1L', categoria: 'Lácteos', unidadBase: 'UND', almacen: 'Cámara Fría', ubicacion: 'B-02-01', lote: 'LT-00102', vencimiento: '05/09/2026', stockActual: 8, stockMinimo: 10, stockMaximo: 50, estado: 'STOCK BAJO', lotes: [] }, { idProducto: 3, codigo: 'SKU-003', nombre: 'Arroz Superior 5kg', categoria: 'Abarrotes', unidadBase: 'UND', almacen: 'Almacén Principal', ubicacion: 'A-02-04', lote: 'LT-00044', vencimiento: '10/10/2027', stockActual: 0, stockMinimo: 12, stockMaximo: 60, estado: 'SIN STOCK', lotes: [] }]); const lotes = ref([{ idLote: 1, codigoLote: 'LT-00825', producto: 'Coca-Cola 2L', fechaIngreso: '25/08/2026', fechaFabricacion: '01/08/2026', fechaVencimiento: '20/12/2026', cantidad: 35, proveedor: 'Distribuidora Norte', estado: 'ACTIVO' }, { idLote: 2, codigoLote: 'LT-00102', producto: 'Leche Entera 1L', fechaIngreso: '20/08/2026', fechaFabricacion: '20/08/2026', fechaVencimiento: '05/09/2026', cantidad: 8, proveedor: 'Lácteos PIL', estado: 'POR_VENCER' }]); const kardex = ref([{ id: 1, fecha: '25/08/2026', documento: 'RCP-00045', operacion: 'Recepción', lote: 'LT-00825', entrada: 50, salida: '—', saldo: 50, usuario: 'Carlos' }, { id: 2, fecha: '27/08/2026', documento: 'VTA-00125', operacion: 'Venta', lote: 'LT-00825', entrada: '—', salida: 15, saldo: 35, usuario: 'Ana' }]); const vencimientos = ref([{ id: 1, producto: 'Leche Entera 1L', lote: 'LT-00102', fecha: '05/09/2026', dias: 4, cantidad: 8, almacen: 'Cámara Fría', ubicacion: 'B-02-01', estado: 'URGENTE' }]); const resumen = computed(() => [{ label: 'Productos con stock', value: 328, help: 'Existencias disponibles', icon: 'inventory_2', tone: 'green' }, { label: 'Stock bajo', value: 18, help: 'Requieren reposición', icon: 'trending_down', tone: 'amber' }, { label: 'Sin stock', value: 6, help: 'Productos agotados', icon: 'remove_shopping_cart', tone: 'red' }, { label: 'Próximos a vencer', value: 14, help: 'En los próximos 30 días', icon: 'event_busy', tone: 'orange' }]); function abrirDetalle(r) { seleccionado.value = r; detalle.value = true } function editarLote(r) { loteSeleccionado.value = r; loteDialog.value = true } function actualizar() { $q.notify({ type: 'positive', message: 'Inventario actualizado.' }) } function exportar() { $q.notify({ message: 'Exportación preparada.' }) }</script>
<style scoped>
.inventory-page {
    min-height: 100%;
    background: #f5f7f4;
    font-family: 'Nunito', sans-serif
}

.inventory-container {
    max-width: 1500px;
    margin: auto;
    padding: 24px
}

.page-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 18px;
    margin-bottom: 24px
}

.header-icon {
    width: 56px;
    height: 56px;
    border-radius: 16px;
    display: grid;
    place-items: center;
    color: #fff;
    background: linear-gradient(135deg, #82bd43, #4a8c25)
}

h1 {
    margin: 0;
    color: #2a5c1a;
    font-size: 27px;
    font-weight: 900
}

.page-header p {
    margin: 4px 0;
    color: #67805f;
    font-size: 13px
}

.main-card {
    border-color: #dfe7dc;
    border-radius: 17px;
    overflow: hidden
}

.q-tab-panels {
    background: transparent
}

.q-tab-panel {
    padding: 22px 0 0
}

@media(max-width:700px) {
    .inventory-container {
        padding: 14px
    }

    .page-header {
        align-items: flex-start;
        flex-direction: column
    }

    .page-header>.row {
        width: 100%
    }

    .page-header .q-btn {
        flex: 1
    }

    h1 {
        font-size: 22px
    }
}
</style>
