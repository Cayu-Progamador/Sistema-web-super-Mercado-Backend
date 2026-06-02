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


//verificar PIN
export function veriFyPin(data) {
    return request({
        url: '/api/auth/verify-pin',
        method: 'post',
        data
    })
}

//resetear contraseña
export function resetPassword(data) {
    return request({
        url: '/api/auth/reset-password',
        method: 'post',
        data
    })
}

//reenviar código
export function resendCode(data) {
    return request({
        url: '/api/auth/resend-code',
        method: 'post',
        data
    })
}
