import request from '../../util/request'
export function login(data) {
    return request({
        url: '/api/auth/login',
        method: 'post',
        data
    })
}