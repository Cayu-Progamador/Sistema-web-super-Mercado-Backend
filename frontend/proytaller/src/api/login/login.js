import request from '../../util/request'
//login
export function login(data) {
    return request({
        url: '/api/auth/login',
        method: 'post',
        data
    })
}

// enviar correo de recuperación
export function forgotPassword(data) {
    return request({
        url: '/api/auth/forgot-password',
        method: 'post',
        data
    })
}
