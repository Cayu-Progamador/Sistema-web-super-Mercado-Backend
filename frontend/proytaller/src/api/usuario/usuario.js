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