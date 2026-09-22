<template>
  <q-page class="permissions-page q-pa-md"><div class="page-wrap"><div class="page-heading row items-center justify-between q-mb-lg"><div><h1><q-icon name="admin_panel_settings" color="green-8" /> Permisos del sistema</h1><p>Administra el acceso de cada usuario según su rol.</p></div></div><PermisosResumenCards :cards="cards" class="q-mb-lg" /><PermisosUsuariosPanel :usuarios="usuarios" :seleccionado="usuarioSeleccionado" @seleccionar="abrirMatriz" /></div>
    <q-dialog v-model="matrizAbierta" maximized><q-card class="matrix-dialog"><q-card-section class="dialog-top row items-center"><div><div class="text-h6 text-weight-bold text-green-9">Matriz de permisos</div><div class="text-caption text-grey-7">{{ usuarioActual?.nombre }}</div></div><q-space /><q-btn flat round icon="close" color="grey-7" v-close-popup /></q-card-section><q-separator /><q-card-section class="dialog-body"><PermisosMatriz v-if="usuarioActual && matriz" :usuario="usuarioActual" :permisos="matriz.permisos" :permisos-seleccionados="permisosSeleccionados" :cargando="guardando" @guardar="guardar" /></q-card-section></q-card></q-dialog>
  </q-page>
</template>
<script setup>
import { computed, onMounted, ref } from 'vue'
import { useQuasar } from 'quasar'
import { listarUsuarios } from '../../api/usuario/usuario'
import { listarRoles } from '../../api/rol/rol'
import { obtenerMatrizPermisos, guardarMatrizPermisos } from '../../api/permisosSistema/permisosSistema'
import PermisosResumenCards from '../../components/permisos_sistema/PermisosResumenCards.vue'
import PermisosUsuariosPanel from '../../components/permisos_sistema/PermisosUsuariosPanel.vue'
import PermisosMatriz from '../../components/permisos_sistema/PermisosMatriz.vue'
const $q=useQuasar(), usuarios=ref([]), roles=ref([]), usuarioSeleccionado=ref(null), matrizAbierta=ref(false), matriz=ref(null), permisosSeleccionados=ref([]), guardando=ref(false)
const usuarioActual=computed(()=>usuarios.value.find(u=>u.id===usuarioSeleccionado.value))
const cards=computed(()=>[{label:'Usuarios',value:usuarios.value.length,icon:'groups',color:'green'},{label:'Usuarios activos',value:usuarios.value.filter(u=>u.estado).length,icon:'verified_user',color:'blue'},{label:'Permisos disponibles',value:matriz.value?.permisos?.length||0,icon:'lock',color:'purple'},{label:'Cambios pendientes',value:0,icon:'pending_actions',color:'orange'}])
const contenido=r=>Array.isArray(r)?r:(r?.content||[])
async function cargarDatos(){try{const [ur,rr]=await Promise.all([listarUsuarios(0,1000),listarRoles(0,1000)]);roles.value=contenido(rr);const porNombre=new Map(roles.value.map(r=>[r.nombre,r]));usuarios.value=contenido(ur).map(u=>({id:u.idUsuario,nombre:u.nombreCompleto||u.username,rol:u.rol||'Sin rol',roleId:porNombre.get(u.rol)?.idRol,estado:Boolean(u.activo),ultimoAcceso:u.ultimoAcceso?new Date(u.ultimoAcceso).toLocaleString():null,avatar:'manage_accounts'}))}catch(e){$q.notify({type:'negative',message:'No se pudieron cargar usuarios y roles.'})}}
async function abrirMatriz(id){const u=usuarios.value.find(x=>x.id===id);if(!u?.roleId){$q.notify({type:'warning',message:'El usuario no tiene un rol válido configurado.'});return}usuarioSeleccionado.value=id;matrizAbierta.value=true;try{matriz.value=await obtenerMatrizPermisos(u.roleId);u.menus=matriz.value?.menus||[];permisosSeleccionados.value=[...(matriz.value?.permisosSeleccionados||[])]}catch(e){matriz.value=null;$q.notify({type:'negative',message:'No se pudo cargar la matriz de permisos.'})}}
async function guardar(ids){if(!usuarioActual.value?.roleId)return;guardando.value=true;try{await guardarMatrizPermisos(usuarioActual.value.roleId,ids);permisosSeleccionados.value=[...ids];$q.notify({type:'positive',message:'Permisos guardados correctamente.'})}catch(e){$q.notify({type:'negative',message:'No se pudieron guardar los permisos.'})}finally{guardando.value=false}}
onMounted(cargarDatos)
</script>
<style scoped>
.permissions-page{min-height:100vh;background:#f5f7fa}.page-wrap{max-width:1450px;margin:auto}.page-heading h1{display:flex;align-items:center;gap:9px;margin:0;color:#1b5e20;font-size:30px;font-weight:900}.page-heading p{margin:4px 0 0;color:#789080}.matrix-dialog{width:96vw;max-width:1500px;height:94vh;border-radius:16px}.dialog-top{padding:16px 24px;background:#f7fbf7}.dialog-body{height:calc(94vh - 78px);overflow:auto}@media(max-width:700px){.page-heading h1{font-size:25px}.matrix-dialog{width:100vw;height:100vh;border-radius:0}.dialog-body{height:calc(100vh - 78px)}}
</style>
