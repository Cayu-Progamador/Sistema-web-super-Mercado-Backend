<template>
  <q-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    maximized
    transition-show="slide-up"
    transition-hide="slide-down"
    class="new-provider-dialog"
  >
    <q-card class="dialog-card">
      <div class="accent-bar"></div>

      <q-card-section class="dialog-header q-px-lg q-py-md">
        <div class="row items-center justify-between">
          <div class="row items-center q-gutter-sm">
            <div class="modal-icon">
              <q-icon :name="form.tipo === 'Empresa' ? 'apartment' : 'person'" size="22px" color="#4a8c25" />
            </div>
            <div>
              <div class="modal-title">Nuevo Proveedor</div>
              <div class="modal-eyebrow">Proveedor Empresa o Persona Natural</div>
            </div>
          </div>
          <q-btn icon="close" flat round dense class="close-btn" v-close-popup />
        </div>

        <!-- Pestañas según el tipo seleccionado -->
        <q-tabs
          v-if="form.tipo === 'Empresa'"
          v-model="tab"
          dense
          active-color="green-8"
          indicator-color="green-8"
          align="left"
          narrow-indicator
          class="q-mt-sm"
        >
          <q-tab name="empresa" icon="apartment" label="Empresa" />
          <q-tab name="contactoEmp" icon="contacts" label="Contacto" />
          <q-tab name="direccionEmp" icon="place" label="Dirección" />
        </q-tabs>
        <q-tabs
          v-else-if="form.tipo === 'Persona'"
          v-model="tab"
          dense
          active-color="green-8"
          indicator-color="green-8"
          align="left"
          narrow-indicator
          class="q-mt-sm"
        >
          <q-tab name="datos" icon="person" label="Datos personales" />
          <q-tab name="contactoPer" icon="contacts" label="Contacto" />
          <q-tab name="direccionPer" icon="place" label="Dirección" />
        </q-tabs>
      </q-card-section>

      <q-separator />

      <q-card-section class="dialog-body q-pa-lg">
        <!-- Buscador / selector de tipo -->
        <div v-if="!form.tipo" class="provider-selector">
          <div class="section-title">Seleccione el tipo de proveedor a registrar</div>
          <div class="selector-card">
            <div class="row q-col-gutter-md">
              <div class="col-6">
                <q-card class="tipo-card" flat @click="form.tipo = 'Empresa'">
                  <q-card-section class="text-center q-pa-lg">
                    <div class="tipo-ic tipo-ic-empresa">
                      <q-icon name="apartment" size="40px" color="#2E7D32" />
                    </div>
                    <div class="tipo-title">Proveedor Empresa</div>
                    <q-chip color="green-8" text-color="white" dense class="q-mt-sm">NIT · Contacto · Dirección</q-chip>
                  </q-card-section>
                </q-card>
              </div>
              <div class="col-6">
                <q-card class="tipo-card" flat @click="form.tipo = 'Persona'">
                  <q-card-section class="text-center q-pg-lg">
                    <div class="tipo-ic tipo-ic-persona">
                      <q-icon name="person" size="40px" color="#2E7D32" />
                    </div>
                    <div class="tipo-title">Proveedor Persona</div>
                    <q-chip color="green-8" text-color="white" dense class="q-mt-sm">CI · Contacto · Dirección</q-chip>
                  </q-card-section>
                </q-card>
              </div>
            </div>
          </div>
        </div>

        <template v-else>
          <div class="tipo-banner row items-center no-wrap">
            <div class="row items-center q-gutter-sm">
              <q-icon :name="form.tipo === 'Empresa' ? 'apartment' : 'person'" color="green-8" size="18px" />
              <span class="text-weight-bold text-green-8">Proveedor {{ form.tipo }}</span>
            </div>
            <q-space />
            <q-btn unelevated color="green-8" no-caps icon="swap_horiz" label="Cambiar tipo" @click="cambiarTipo" />
          </div>

          <q-tab-panels v-model="tab" animated class="tab-panels">
            <!-- EMPRESA -->
            <q-tab-panel name="empresa">
              <div class="section-title">Datos de la empresa</div>
              <div class="row q-col-gutter-md">
                <div class="col-12 col-md-4">
                  <q-input v-model="form.empresa.nit" label="NIT *" stack-label outlined color="green-8" mask="###-#######-#" placeholder="102-4567890-1" class="q-mb-md field-green" :rules="[v => !!v || 'NIT requerido']">
                    <template v-slot:prepend><q-icon name="badge" size="20px" /></template>
                  </q-input>
                </div>
                <div class="col-12 col-md-8">
                  <q-input v-model="form.empresa.razonSocial" label="Razón social *" stack-label outlined color="green-8" placeholder="Coca-Cola Bolivia S.A." class="q-mb-md field-green" :rules="[v => !!v || 'Razón social requerida']">
                    <template v-slot:prepend><q-icon name="business" size="20px" /></template>
                  </q-input>
                </div>
                <div class="col-12 col-md-6">
                  <q-input v-model="form.empresa.nombreComercial" label="Nombre comercial" stack-label outlined color="green-8" placeholder="Coca-Cola" class="q-mb-md field-green">
                    <template v-slot:prepend><q-icon name="apartment" size="20px" /></template>
                  </q-input>
                </div>
                <div class="col-12 col-md-6">
                  <q-input v-model="form.empresa.representanteLegal" label="Representante legal" stack-label outlined color="green-8" placeholder="Nombre del representante" class="q-mb-md field-green">
                    <template v-slot:prepend><q-icon name="person_pin" size="20px" /></template>
                  </q-input>
                </div>
              </div>
            </q-tab-panel>

            <q-tab-panel name="contactoEmp">
              <div class="row items-center justify-between">
                <div class="section-title" style="margin:0;">Contactos</div>
                <q-btn unelevated color="green-6" dense size="sm" icon="person_add_alt" no-caps label="Agregar contacto" @click="agregarContacto" class="add-contact-btn" />
              </div>
              <div class="q-mt-md">
                <div v-for="(ct, i) in form.empresa.contactos" :key="i" class="pv-contact-block">
                  <div class="pv-contact-index">{{ i + 1 }}</div>
                  <div class="row q-col-gutter-md col-grow items-center">
                    <div class="col-12 col-md-5">
                      <q-input v-model="ct.telefono" :label="`Teléfono ${i === 0 ? '*' : ''}`" stack-label outlined color="green-8" mask="###-###-####" placeholder="2-2445678" class="q-mb-none field-green">
                      <template v-slot:prepend><q-icon name="phone" size="20px" /></template>
                    </q-input>
                    </div>
                    <div class="col-12 col-md-5">
                      <q-input v-model="ct.correo" :label="`Correo ${i === 0 ? '*' : ''}`" stack-label outlined color="green-8" type="email" placeholder="contacto@empresa.com" class="q-mb-none field-green">
                      <template v-slot:prepend><q-icon name="email" size="20px" /></template>
                    </q-input>
                    </div>
                    <div class="col-12 col-md-2 text-right">
                      <q-btn
                        v-if="form.empresa.contactos.length > 1"
                        flat round dense icon="delete_outline" color="red-6" @click="quitarContacto(i)"
                      >
                        <q-tooltip>Quitar contacto</q-tooltip>
                      </q-btn>
                    </div>
                  </div>
                </div>
              </div>
            </q-tab-panel>

            <q-tab-panel name="direccionEmp">
              <div class="section-title">Dirección de la empresa</div>
              <div class="row q-col-gutter-md">
                <div class="col-12 col-md-4">
                  <q-select v-model="form.empresa.direccion.ciudad" :options="ciudades" label="Ciudad" stack-label outlined color="green-8" emit-value map-options class="q-mb-md field-green">
                    <template v-slot:prepend><q-icon name="location_city" size="20px" /></template>
                  </q-select>
                </div>
                <div class="col-12 col-md-4">
                  <q-input v-model="form.empresa.direccion.zona" label="Zona" stack-label outlined color="green-8" placeholder="San Pedro" class="q-mb-md field-green">
                  <template v-slot:prepend><q-icon name="map" size="20px" /></template>
                </q-input>
                </div>
                <div class="col-12 col-md-4">
                  <q-input v-model="form.empresa.direccion.calle" label="Calle" stack-label outlined color="green-8" placeholder="Av. Arce" class="q-mb-md field-green">
                  <template v-slot:prepend><q-icon name="signpost" size="20px" /></template>
                </q-input>
                </div>
                <div class="col-12 col-md-4">
                  <q-input v-model="form.empresa.direccion.numero" label="Número" stack-label outlined color="green-8" placeholder="1234" class="q-mb-md field-green">
                  <template v-slot:prepend><q-icon name="tag" size="20px" /></template>
                </q-input>
                </div>
                <div class="col-12 col-md-8">
                  <q-input v-model="form.empresa.direccion.referencia" label="Referencia" stack-label outlined color="green-8" placeholder="Frente al parque central" class="q-mb-none field-green">
                  <template v-slot:prepend><q-icon name="place" size="20px" /></template>
                </q-input>
                </div>
              </div>
            </q-tab-panel>

            <!-- PERSONA -->
            <q-tab-panel name="datos">
              <div class="section-title">Datos personales</div>
              <div class="row q-col-gutter-md">
                <div class="col-12 col-md-4">
                  <q-input v-model="form.persona.nombres" label="Nombres *" stack-label outlined color="green-8" placeholder="Juan" class="q-mb-md field-green" :rules="[v => !!v || 'Requerido']">
                  <template v-slot:prepend><q-icon name="person" size="20px" /></template>
                </q-input>
                </div>
                <div class="col-12 col-md-4">
                  <q-input v-model="form.persona.apellidoPaterno" label="Apellido paterno *" stack-label outlined color="green-8" placeholder="Mamani" class="q-mb-md field-green" :rules="[v => !!v || 'Requerido']">
                  <template v-slot:prepend><q-icon name="person_outline" size="20px" /></template>
                </q-input>
                </div>
                <div class="col-12 col-md-4">
                  <q-input v-model="form.persona.apellidoMaterno" label="Apellido materno" stack-label outlined color="green-8" placeholder="Choque" class="q-mb-md field-green">
                  <template v-slot:prepend><q-icon name="person_outline" size="20px" /></template>
                </q-input>
                </div>
                <div class="col-12 col-md-4">
                  <q-input v-model="form.persona.ci" label="CI *" stack-label outlined color="green-8" mask="#######-#" placeholder="6734567-1" class="q-mb-md field-green" :rules="[v => !!v || 'Requerido']">
                  <template v-slot:prepend><q-icon name="badge" size="20px" /></template>
                </q-input>
                </div>
                <div class="col-12 col-md-4">
                  <q-input v-model="form.persona.fechaNacimiento" label="Fecha de nacimiento *" stack-label outlined color="green-8" type="date" :rules="[v => !!v || 'Requerido']" placeholder="DD / MM / AAAA" class="q-mb-md field-green" />
                </div>
                <div class="col-12 col-md-4">
                  <q-select v-model="form.persona.sexo" :options="sexos" label="Sexo *" stack-label outlined color="green-8" emit-value map-options class="q-mb-md field-green" :rules="[v => !!v || 'Requerido']">
                  <template v-slot:prepend><q-icon name="wc" size="20px" /></template>
                </q-select>
                </div>
              </div>
            </q-tab-panel>

            <q-tab-panel name="contactoPer">
              <div class="section-title">Contacto</div>
              <div class="row q-col-gutter-md">
                <div class="col-12 col-md-6">
                  <q-input v-model="form.persona.telefono" label="Teléfono *" stack-label outlined color="green-8" mask="###-###-####" placeholder="7-2445678" class="q-mb-md field-green" :rules="[v => !!v || 'Requerido']">
                  <template v-slot:prepend><q-icon name="phone" size="20px" /></template>
                </q-input>
                </div>
                <div class="col-12 col-md-6">
                  <q-input v-model="form.persona.correo" label="Correo *" stack-label outlined color="green-8" type="email" placeholder="juan.mamani@gmail.com" class="q-mb-md field-green" :rules="[v => !!v || 'Requerido']">
                  <template v-slot:prepend><q-icon name="email" size="20px" /></template>
                </q-input>
                </div>
              </div>
            </q-tab-panel>

            <q-tab-panel name="direccionPer">
              <div class="section-title">Dirección</div>
              <div class="row q-col-gutter-md">
                <div class="col-12 col-md-4">
                  <q-select v-model="form.persona.direccion.ciudad" :options="ciudades" label="Ciudad" stack-label outlined color="green-8" emit-value map-options class="q-mb-md field-green">
                  <template v-slot:prepend><q-icon name="location_city" size="20px" /></template>
                </q-select>
                </div>
                <div class="col-12 col-md-4">
                  <q-input v-model="form.persona.direccion.zona" label="Zona de procedencia *" stack-label outlined color="green-8" placeholder="Valle de Zongo" class="q-mb-md field-green" :rules="[v => !!v || 'Requerido']">
                  <template v-slot:prepend><q-icon name="map" size="20px" /></template>
                </q-input>
                </div>
                <div class="col-12 col-md-4">
                  <q-input v-model="form.persona.direccion.calle" label="Calle" stack-label outlined color="green-8" placeholder="Av. Costanera" class="q-mb-md field-green">
                  <template v-slot:prepend><q-icon name="signpost" size="20px" /></template>
                </q-input>
                </div>
                <div class="col-12 col-md-4">
                  <q-input v-model="form.persona.direccion.numero" label="Número" stack-label outlined color="green-8" placeholder="1234" class="q-mb-md field-green">
                  <template v-slot:prepend><q-icon name="tag" size="20px" /></template>
                </q-input>
                </div>
                <div class="col-12 col-md-8">
                  <q-input v-model="form.persona.direccion.referencia" label="Referencia" stack-label outlined color="green-8" placeholder="Frente a la plaza" class="q-mb-none field-green">
                  <template v-slot:prepend><q-icon name="place" size="20px" /></template>
                </q-input>
                </div>
              </div>
            </q-tab-panel>
          </q-tab-panels>
        </template>
      </q-card-section>

      <q-separator />

      <q-card-actions align="right" class="q-pa-md dialog-actions">
        <q-btn flat label="Cancelar" color="green-8" v-close-popup no-caps />
        <q-btn
          v-if="form.tipo"
          unelevated
          color="green-8"
          label="Guardar proveedor"
          no-caps
          :disable="!formValido"
          @click="guardar"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false }
})

const emit = defineEmits(['update:modelValue', 'guardar'])

const tab = ref('empresa')

const ciudades = ['La Paz', 'El Alto', 'Cochabamba', 'Santa Cruz', 'Oruro', 'Potosí', 'Sucre', 'Tarija', 'Beni', 'Pando']
const sexos = ['Masculino', 'Femenino', 'Otro']

const form = reactive({
  tipo: null,
  empresa: {
    nit: '',
    razonSocial: '',
    nombreComercial: '',
    representanteLegal: '',
    contactos: [{ telefono: '', correo: '' }],
    direccion: {
      zona: '',
      calle: '',
      numero: '',
      referencia: '',
      ciudad: ''
    }
  },
  persona: {
    nombres: '',
    apellidoPaterno: '',
    apellidoMaterno: '',
    ci: '',
    fechaNacimiento: '',
    sexo: '',
    telefono: '',
    correo: '',
    direccion: {
      ciudad: '',
      zona: '',
      calle: '',
      numero: '',
      referencia: ''
    }
  }
})

const formValido = computed(() => {
  if (form.tipo === 'Empresa') {
    if (!form.empresa.nit || !form.empresa.razonSocial) return false
    return form.empresa.contactos.some((c) => c.telefono || c.correo)
  }
  const p = form.persona
  return p.nombres && p.apellidoPaterno && p.ci && p.fechaNacimiento && p.sexo && p.telefono && p.correo && p.direccion.zona
})

function agregarContacto() {
  form.empresa.contactos.push({ telefono: '', correo: '' })
}

function quitarContacto(i) {
  form.empresa.contactos.splice(i, 1)
}

function cambiarTipo() {
  form.tipo = null
  tab.value = 'empresa'
}

watch(
  () => form.tipo,
  (tipo) => {
    tab.value = tipo === 'Empresa' ? 'empresa' : 'datos'
  }
)

function guardar() {
  emit('guardar', {
    tipo: form.tipo,
    ...(form.tipo === 'Empresa'
      ? { empresa: JSON.parse(JSON.stringify(form.empresa)) }
      : { persona: JSON.parse(JSON.stringify(form.persona)) })
  })
  form.tipo = null
  tab.value = 'empresa'
}

watch(
  () => props.modelValue,
  (val) => {
    if (val) {
      tab.value = 'empresa'
      form.tipo = null
      form.empresa.nit = ''
      form.empresa.razonSocial = ''
      form.empresa.nombreComercial = ''
      form.empresa.representanteLegal = ''
      form.empresa.contactos = [{ telefono: '', correo: '' }]
      form.empresa.direccion.zona = ''
      form.empresa.direccion.calle = ''
      form.empresa.direccion.numero = ''
      form.empresa.direccion.referencia = ''
      form.empresa.direccion.ciudad = ''
      form.persona.nombres = ''
      form.persona.apellidoPaterno = ''
      form.persona.apellidoMaterno = ''
      form.persona.ci = ''
      form.persona.fechaNacimiento = ''
      form.persona.sexo = ''
      form.persona.telefono = ''
      form.persona.correo = ''
      form.persona.direccion.ciudad = ''
      form.persona.direccion.zona = ''
      form.persona.direccion.calle = ''
      form.persona.direccion.numero = ''
      form.persona.direccion.referencia = ''
    }
  }
)
</script>

<style scoped>
.new-provider-dialog :deep(.q-dialog__inner) {
  padding: 20px;
}
.dialog-card {
  border-radius: 16px;
  max-width: 900px;
  margin: 0 auto;
  box-shadow: 0 4px 24px rgba(0,0,0,0.12);
}
.accent-bar {
  height: 4px;
  background: linear-gradient(90deg, #2E7D32, #4a8c25, #81C784);
  border-radius: 16px 16px 0 0;
}
.dialog-header {
  background: linear-gradient(135deg, #f1f8e9, #e8f5e9);
  border-radius: 0;
  border-bottom: 1px solid #e0e0e0;
}
.dialog-body {
  min-height: 350px;
  background: #fafafa;
}
.tab-panels {
  background: transparent;
}
.dialog-actions {
  background: #f1f8e9;
  border-radius: 0 0 16px 16px;
}
.modal-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: rgba(46, 125, 50, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
}
.modal-eyebrow {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 1px;
  color: #2E7D32;
  text-transform: uppercase;
}
.modal-title {
  font-size: 18px;
  font-weight: 700;
  color: #1B5E20;
  line-height: 1.2;
}
.close-btn {
  color: #2E7D32;
}
.section-title {
  font-size: 15px;
  font-weight: 700;
  color: #2E7D32;
  margin-bottom: 16px;
}
.tipo-card {
  border: 2px solid #e0e0e0;
  border-radius: 14px;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
  background: #ffffff;
}
.tipo-card:hover {
  border-color: #2E7D32;
  box-shadow: 0 8px 22px rgba(46, 125, 50, 0.14);
  transform: translateY(-2px);
}
.tipo-ic {
  width: 72px;
  height: 72px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}
.tipo-ic-empresa {
  background: #e8f5e9;
}
.tipo-ic-persona {
  background: #e8f5e9;
}
.tipo-title {
  font-size: 17px;
  font-weight: 700;
  color: #1B5E20;
  margin-top: 14px;
}
.tipo-banner {
  background: #e8f5e9;
  border: 1px solid #c8e6c9;
  border-radius: 10px;
  padding: 8px 12px;
  margin-bottom: 14px;
}
.provider-selector {
  max-width: 720px;
  margin: 0 auto;
  padding-top: 10px;
}
.selector-card {
  background: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 14px;
  padding: 12px;
}
.row-sub {
  padding-top: 2px;
}
.pv-contact-block {
  display: flex;
  gap: 14px;
  align-items: flex-start;
  background: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 10px;
}
.add-contact-btn {
  border-radius: 8px;
  font-weight: 600;
  box-shadow: 0 2px 6px rgba(46, 125, 50, 0.25);
  padding: 4px 12px;
}
.add-contact-btn:hover {
  background: #2E7D32 !important;
  box-shadow: 0 4px 12px rgba(46, 125, 50, 0.4);
  transform: translateY(-1px);
}
.pv-contact-index {
  width: 26px;
  height: 26px;
  border-radius: 8px;
  background: #E8F5E9;
  color: #2E7D32;
  font-weight: 700;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 4px;
}
.field-green :deep(.q-field__control),
.field-green :deep(.q-field__control:focus-within) {
  border-radius: 10px;
  background: white;
  border: 1.5px solid #2E7D32 !important;
  box-shadow: none !important;
}
.field-green :deep(.q-field__control:focus-within) {
  border-color: #2E7D32 !important;
  box-shadow: 0 0 0 3px rgba(46,125,50,0.15) !important;
}
.field-green :deep(.q-field__before),
.field-green :deep(.q-field__after) {
  border: none !important;
}
.field-green :deep(.q-field__control::before),
.field-green :deep(.q-field__control::after) {
  display: none !important;
}
.field-green :deep(input),
.field-green :deep(.q-field__label),
.field-green :deep(.q-icon) {
  color: #1B5E20 !important;
}
.field-green :deep(.q-checkbox__label) {
  color: #1B5E20 !important;
}
</style>