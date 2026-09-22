// src/store/auth.js
import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: null,
        userInfo: null,
        controlaAsistencia: false,
        permisos: [],
    }),

    getters: {
        isAuthenticated: (state) => !!state.token,

        fullName: (state) => {
            return state.userInfo?.nombreCompleto ?? "";
        },

        getUsername: (state) => {
            return state.userInfo?.username ?? "";
        },

        tieneControlAsistencia: (state) => state.controlaAsistencia,
    },

    actions: {
        login(token, userInfo, controlaAsistencia) {
            this.token = token;
            this.userInfo = userInfo;
            this.controlaAsistencia = controlaAsistencia;
        },

        setFotoUrl(url) {
            if (this.userInfo) {
                this.userInfo.fotoUrl = url;
            }
        },

        setToken(token) {
            this.token = token;
        },

        setUserInfo(userInfo) {
            this.userInfo = userInfo;
        },

        setPermisos(permisos) {
            this.permisos = Array.isArray(permisos) ? permisos : [];
        },

        tienePermiso(codigo) {
            return this.permisos.includes('ROLE_ADMIN') || this.permisos.includes(codigo);
        },

        logout() {
            this.$reset();
        }
    },

    persist: {
        key: 'auth',
        storage: localStorage,
        paths: ['token', 'userInfo', 'controlaAsistencia', 'permisos']
    }
});
