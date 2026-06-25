import request from '../../util/request'

export function actualizarFotoPerfil(formData) {
    return request({
        url: '/api/foto-perfil/subir',
        method: 'post',
        data: formData
    })
}

export function eliminarFotoPerfil() {
    return request({
        url: '/api/foto-perfil/eliminar',
        method: 'delete'
    })
}
