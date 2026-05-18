import requets from '../../util/request'

//ver los datos del usuario nombre completo usuername que se conecto al sistema
export function getUserList() {
    return requets({
        url: '/api/user/info',
        method: 'get'
    })
}