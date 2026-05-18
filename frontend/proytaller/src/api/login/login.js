import requets from '../../util/request'
export function login(data) {
    return requets({
        url: '/auth/login',
        method: 'post',
        data
    })
}