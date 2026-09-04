<template>
    <div class="row q-col-gutter-md q-mb-lg">
        <div v-for="item in items" :key="item.label" class="col-12 col-sm-6 col-md-3"><q-card flat bordered
                class="summary-card"><q-card-section class="row items-center no-wrap">
                    <div class="summary-icon" :class="item.tone"><q-icon :name="item.icon" size="24px" /></div>
                    <div class="q-ml-md">
                        <div class="summary-label">{{ item.label }}</div>
                        <div class="summary-value">{{ item.value }}</div>
                        <div class="summary-help">{{ item.help }}</div>
                    </div>
                </q-card-section></q-card></div>
    </div>
</template>
<script setup>import{onMounted,ref}from'vue';import{obtenerResumen}from'../../api/inventario/inventario';const props=defineProps({items:{type:Array,required:true}});const items=ref(props.items);onMounted(async()=>{try{const r=await obtenerResumen();items.value=[{label:'Productos con stock',value:r.productosConStock,help:'Existencias disponibles',icon:'inventory_2',tone:'green'},{label:'Stock bajo',value:r.stockBajo,help:'Requieren reposición',icon:'trending_down',tone:'amber'},{label:'Sin stock',value:r.sinStock,help:'Productos agotados',icon:'remove_shopping_cart',tone:'red'},{label:'Próximos a vencer',value:r.proximosAVencer,help:'En los próximos 30 días',icon:'event_busy',tone:'orange'}]}catch(e){console.error('No se pudo cargar el resumen',e)}})</script>
<style
    scoped>
    .summary-card {
        height: 100%;
        border-radius: 15px;
        border-color: #dfe7dc !important
    }

    .summary-icon {
        width: 48px;
        height: 48px;
        border-radius: 13px;
        display: grid;
        place-items: center;
        color: #fff
    }

    .summary-icon.green {
        background: #4a8c25
    }

    .summary-icon.amber {
        background: #d59a32
    }

    .summary-icon.red {
        background: #c95f58
    }

    .summary-icon.orange {
        background: #df8033
    }

    .summary-label {
        font-size: 12px;
        color: #71806c;
        font-weight: 800
    }

    .summary-value {
        font-size: 25px;
        color: #29472d;
        font-weight: 900;
        line-height: 1.15
    }

    .summary-help {
        font-size: 11px;
        color: #9aa39a;
        margin-top: 3px
    }
</style>
