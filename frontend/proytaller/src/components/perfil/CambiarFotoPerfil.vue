<template>
    <q-dialog v-model="abierto" persistent>
        <q-card class="foto-card">

            <!-- Header -->
            <div class="foto-header">
                <span class="foto-title">Cambiar foto de perfil</span>
                <q-btn flat round dense icon="close" class="close-btn" @click="cerrar" />
            </div>

            <!-- Body -->
            <q-card-section class="foto-body">

                <!-- Avatar preview -->
                <div class="avatar-section">
                    <div class="avatar-wrap">
                        <div class="avatar-circle"
                            :style="avatarStyle">
                            <span v-if="!previewUrl && !props.fotoUrl">{{ iniciales }}</span>
                        </div>
                        <button class="avatar-cam-btn" @click="triggerInput">
                            <q-icon name="photo_camera" size="14px" color="white" />
                        </button>
                    </div>
                    <span class="formats-txt">JPG, JPEG, PNG o WEBP · Máximo 5MB · Mínimo 200×200px</span>
                </div>

                <!-- Requisitos -->
                <div class="req-grid">
                    <div class="req-item">
                        <div class="req-dot"><q-icon name="check" size="10px" style="color:#4a8c25" /></div>
                        <span class="req-text">Formato: JPG, JPEG, PNG, WEBP</span>
                    </div>
                    <div class="req-item">
                        <div class="req-dot"><q-icon name="check" size="10px" style="color:#4a8c25" /></div>
                        <span class="req-text">Tamaño máximo: 5 MB</span>
                    </div>
                    <div class="req-item">
                        <div class="req-dot"><q-icon name="check" size="10px" style="color:#4a8c25" /></div>
                        <span class="req-text">Resolución mínima: 200×200 px</span>
                    </div>
                    <div class="req-item">
                        <div class="req-dot"><q-icon name="check" size="10px" style="color:#4a8c25" /></div>
                        <span class="req-text">Recomendada: 500×500 px</span>
                    </div>
                </div>

                <!-- Error -->
                <div v-if="errorMsg" class="error-box">
                    <q-icon name="error_outline" size="15px" style="color:#b91c1c;flex-shrink:0" />
                    <span>{{ errorMsg }}</span>
                </div>

                <!-- Drop zone -->
                <div class="drop-zone" :class="{ 'drag-over': isDragging }" @click="triggerInput"
                    @dragover.prevent="isDragging = true" @dragleave="isDragging = false" @drop.prevent="onDrop">
                    <q-icon name="cloud_upload" size="36px" class="drop-icon" />
                    <div class="drop-title">Arrastra tu imagen aquí</div>
                    <div class="drop-or">o</div>
                    <button class="btn-select" @click.stop="triggerInput">
                        Seleccionar archivo
                    </button>
                </div>

                <!-- Progress -->
                <div v-if="progreso > 0" class="progress-wrap">
                    <div class="progress-header">
                        <span class="progress-name">{{ archivoNombre }}</span>
                        <span class="progress-pct">{{ Math.floor(progreso) }}%</span>
                    </div>
                    <div class="progress-bg">
                        <div class="progress-fill" :style="{ width: progreso + '%' }"></div>
                    </div>
                </div>

                <!-- Input oculto -->
                <input ref="fileInput" type="file" accept=".jpg,.jpeg,.png,.webp" style="display:none"
                    @change="onFileChange" />

            </q-card-section>

            <!-- Footer -->
            <div class="foto-footer">
                <button class="btn-cancel" @click="cerrar">Cancelar</button>
                <button class="btn-save" :disabled="!archivoListo || guardando"
                    :class="{ 'btn-disabled': !archivoListo || guardando }" @click="guardarFoto">
                    <q-spinner-dots v-if="guardando" color="white" size="1em" />
                    <q-icon v-else name="save" size="16px" />
                    {{ guardando ? 'Guardando...' : 'Guardar cambios' }}
                </button>
            </div>

        </q-card>
    </q-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useQuasar } from 'quasar'
import { actualizarFotoPerfil } from '../../api/fotoPerfil/fotoPerfil'

const $q = useQuasar()

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  nombreUsuario: { type: String, default: 'Admin Sistema' },
  fotoUrl: { type: String, default: '' }
})


const emit = defineEmits(['update:modelValue', 'foto-actualizada'])
// ✅ v-model reactivo
const abierto = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const fileInput = ref(null)
const previewUrl = ref('')
const archivoNombre = ref('')
const archivoFile = ref(null)
const errorMsg = ref('')
const isDragging = ref(false)
const progreso = ref(0)
const archivoListo = ref(false)
const guardando = ref(false)

const FORMATOS = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp']
const MAX_MB = 5
const MIN_PX = 200

const iniciales = computed(() => {
    if (!props.nombreUsuario) return 'AS'
    return props.nombreUsuario
        .split(' ')
        .slice(0, 2)
        .map(n => n[0])
        .join('')
        .toUpperCase()
})
const avatarStyle = computed(() => {
    if (previewUrl.value) {
        return { backgroundImage: `url(${previewUrl.value})`, backgroundSize: 'cover', backgroundPosition: 'center' }
    }
    if (props.fotoUrl) {
        return { backgroundImage: `url(${props.fotoUrl})`, backgroundSize: 'cover', backgroundPosition: 'center' }
    }
    return {}
})


const cerrar = () => {
    reset()
    abierto.value = false
}


/* ── Reset ── */
const reset = () => {
    previewUrl.value = ''
    archivoNombre.value = ''
    archivoFile.value = null
    errorMsg.value = ''
    progreso.value = 0
    archivoListo.value = false
    guardando.value = false
    isDragging.value = false
    if (fileInput.value) fileInput.value.value = ''
}

/* ── Trigger input ── */
const triggerInput = () => fileInput.value?.click()

/* ── On change input ── */
const onFileChange = (e) => {
    const file = e.target.files?.[0]
    if (file) procesarArchivo(file)
}

/* ── On drop ── */
const onDrop = (e) => {
    isDragging.value = false
    const file = e.dataTransfer.files?.[0]
    if (file) procesarArchivo(file)
}

/* ── Procesar archivo ── */
const procesarArchivo = (file) => {
    errorMsg.value = ''
    archivoListo.value = false
    progreso.value = 0

    // Validar formato
    if (!FORMATOS.includes(file.type)) {
        errorMsg.value = 'Formato no permitido. Usa JPG, JPEG, PNG o WEBP.'
        return
    }

    // Validar tamaño
    if (file.size > MAX_MB * 1024 * 1024) {
        errorMsg.value = `El archivo supera el tamaño máximo de ${MAX_MB}MB.`
        return
    }

    const reader = new FileReader()
    reader.onload = (e) => {
        const img = new Image()
        img.onload = () => {
            // Validar resolución
            if (img.width < MIN_PX || img.height < MIN_PX) {
                errorMsg.value = `La imagen debe ser mínimo ${MIN_PX}×${MIN_PX} píxeles.`
                return
            }
            previewUrl.value = e.target.result
            archivoNombre.value = file.name
            archivoFile.value = file
            simularProgreso()
        }
        img.src = e.target.result
    }
    reader.readAsDataURL(file)
}

/* ── Simular progreso ── */
const simularProgreso = () => {
    progreso.value = 0
    archivoListo.value = false
    const interval = setInterval(() => {
        progreso.value += Math.random() * 18 + 5
        if (progreso.value >= 100) {
            progreso.value = 100
            archivoListo.value = true
            clearInterval(interval)
        }
    }, 100)
}

const guardarFoto = async () => {
    if (!archivoFile.value) return
    guardando.value = true
    try {
        const formData = new FormData()
        formData.append('foto', archivoFile.value)
        const response = await actualizarFotoPerfil(formData)
        $q.notify({ type: 'positive', message: 'Foto de perfil actualizada correctamente' })
        emit('foto-actualizada', response.url || previewUrl.value)
        cerrar()
    } catch {
        $q.notify({ type: 'negative', message: 'Error al guardar la foto. Intenta de nuevo.' })
    } finally {
        guardando.value = false
    }
}
</script>

<style scoped src="../../assets/styles/perfil/cambiarFotoPerfil.css"></style>