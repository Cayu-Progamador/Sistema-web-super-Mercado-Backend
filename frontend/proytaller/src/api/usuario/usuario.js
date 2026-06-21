import request from '../../util/request'

//ver perfil del usuario logueado
export function getUserList() {
    return request({
        url: '/api/usuarios/perfil',
        method: 'get'
    })
}

//registrar un nuevo usuario
export function registrarUsuario(data) {
    return request({
        url: '/api/usuarios/register',
        method: 'post',
        data
    })
}

// cambiar contrasena del usuario logueado
export function cambiarContrasena(data) { 
    return request({
        url: '/api/usuarios/cambiar-contrasena',
        method: 'post',
        data
    })
}


// listar usuarios paginados
export function listarUsuarios(page = 0, size = 10) {
    return request({
        url: '/api/usuarios/listar',
        method: 'get',
        params: {
            page,
            size
        }
    })
}

//  ACTIVAR USUARIO

export function activarUsuario(id) {
    return request({
        url: `/api/usuarios/${id}/activar`,
        method: 'patch'
    })
}


//  DESACTIVAR USUARIO

export function desactivarUsuario(id) {
    return request({
        url: `/api/usuarios/${id}/desactivar`,
        method: 'patch'
    })
}

//datos dashboard de usuarios 
export function getDashboardUsuarios() {
    return request({
        url: '/api/usuarios/estadisticas',
        method: 'get'
    })  
}

//actualizar usuario
export function actualizarUsuario(id, data) {
    return request({
        url: `/api/usuarios/actualizar/${id}`,
        method: 'put',
        data
    })
}

//buscar usuario por nombre
export function buscarUsuario(username) {
    return request({
        url: '/api/usuarios/buscar',
        method: 'get',
        params: {
            username
        }
    })
}

//buscar usuario por nombre paginado
export function buscarUsuarioPaginado(username, page = 0, size = 10) {
    return request({
        url: '/api/usuarios/buscar-paginado',
        method: 'get',
        params: { username, page, size }
    })
}

//detalle de usuario
export function obtenerDetalleUsuario(id) {
  return request({
    url: `/api/usuarios/detalle/${id}`,
    method: 'get'
  })
}
