import { date } from 'quasar'
import requets from '../../util/request'

//ver los datos del usuario nombre completo usuername que se conecto al sistema

export function getUserList() {
    return requets({
        url: '/api/usuarios/perfil',
        method: 'get'
    })
}

//registrar un nuevo usuario
export function registrarUsuario(data) {
    return requets({
        url: '/api/auth/register',
        method: 'post',
        data
    })
}