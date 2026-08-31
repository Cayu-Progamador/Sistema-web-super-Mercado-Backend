<template><q-dialog v-model="visible"><q-card class="detail"><q-card-section class="head row items-center"><q-icon
                    name="swap_horiz" size="28px" color="green-8" />
                <div class="col q-ml-md"><strong>{{ transferencia?.codigo || 'Transferencia' }}</strong><small>{{ fecha }}
                        · Responsable: {{ transferencia?.responsable || '—' }}</small></div><q-badge
                    :color="color(transferencia?.estado)" :label="transferencia?.estado || '—'" /><q-btn flat round dense
                    icon="picture_as_pdf" color="negative" @click="descargarPdf"><q-tooltip>Descargar
                        PDF</q-tooltip></q-btn><q-btn flat round dense icon="close" @click="visible = false" />
            </q-card-section><q-card-section class="scroll">
                <div class="row q-col-gutter-md">
                    <div class="col-12 col-sm-6"><q-card flat bordered class="pa"><small>Origen</small><strong>{{
                                transferencia?.origen||'Sin definir' }}</strong></q-card></div>
                    <div class="col-12 col-sm-6"><q-card flat bordered class="pa"><small>Destino</small><strong>{{
                                transferencia?.destino||'Sin definir' }}</strong></q-card></div>
                </div>
                <p class="obs"><b>Observación:</b> {{ transferencia?.observacion || 'Sin observación' }}</p>
                <h4>Productos</h4><q-table flat bordered dense :rows="detalles" :columns="cols" row-key="idProducto"
                    hide-bottom no-data-label="No hay productos registrados" />
            </q-card-section></q-card></q-dialog></template>
<script setup>    
    import { computed } from 'vue'; 
    import { useQuasar } from 'quasar'; 
    import { descargarPdfTransferencia } from '../../api/transferencias/transferencias'; 
    const props = defineProps({ modelValue: Boolean, transferencia: Object }); 
    const emit = defineEmits(['update:modelValue']); 
    const $q = useQuasar(); 
    const visible = computed({ get: () => props.modelValue, set: v => emit('update:modelValue', v) }); 
    const detalles = computed(() => props.transferencia?.detalles || []); 
    const fecha = computed(() => props.transferencia?.fechaTransferencia?.replace('T', ' ') || '—'); 
    const cols = [{ name: 'producto', label: 'Producto', field: 'producto' }, { name: 'lote', label: 'Lote', field: 'lote' }, { name: 'cantidad', label: 'Cantidad', field: 'cantidad' }, { name: 'unidad', label: 'Unidad', field: 'unidad' }]; 
    function color(e) { return { BORRADOR: 'grey-7', PENDIENTE: 'amber-8', EN_PROCESO: 'orange-8', COMPLETADA: 'green-7', CANCELADA: 'red-5' }[e] || 'grey-6' } 
    async function descargarPdf() { try { const blob = await descargarPdfTransferencia(props.transferencia.idTransferencia); 
    const url = URL.createObjectURL(blob); 
    const a = document.createElement('a'); a.href = url; a.download = `Transferencia_${props.transferencia.codigo}.pdf`; a.click(); 
    URL.revokeObjectURL(url) } catch { $q.notify({ type: 'negative', message: 'No se pudo descargar el PDF.' }) } }
    </script>
<style scoped>
    .detail {
        width: 95vw;
        max-width: 900px;
        border-radius: 18px
    }

    .head {
        background: #f0f8e9;
        border-bottom: 1px solid #dfe7dc
    }

    .head strong,
    .head small {
        display: block
    }

    .head strong {
        font-size: 18px;
        font-weight: 900
    }

    .head small {
        color: #6b7280
    }

    .pa {
        padding: 14px;
        border-radius: 10px
    }

    .pa small {
        color: #6b7280
    }

    .pa strong {
        display: block;
        margin-top: 4px
    }

    .obs {
        padding: 12px;
        background: #f7faf5;
        border-left: 3px solid #82bd43
    }

    .scroll {
        max-height: 75vh
    }

    h4 {
        font-size: 14px;
        color: #4a8c25;
        margin: 18px 0 8px
    }
</style>
