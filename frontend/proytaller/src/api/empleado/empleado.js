import request from '../../util/request'

//listar empleados disponibles (sin usuario asignado) - para crear usuario
export function getEmpleadoLista() {
    return request({
        url: '/api/empleados/select',
        method: 'get'
    })
}

//listar empleados para editar (excluye los de OTROS usuarios)
export function getEmpleadoListaEditar(usuarioId) {
    return request({
        url: `/api/empleados/editar/${usuarioId}`,
        method: 'get'
    })
}