import request from '../../util/request'

//listar roles para usuario
export function getEmpleadoLista() {
    return request({
        url: '/api/empleados/select',
        method: 'get'
    })
}