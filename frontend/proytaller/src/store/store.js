// src/store/auth.js
import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: null,
        userInfo: null, // { username, nombreCompleto }
    }),

    getters: {
        isAuthenticated: (state) => !!state.token,

        fullName: (state) => {
            return state.userInfo?.nombreCompleto ?? "";
        },

        getUsername: (state) => {
            return state.userInfo?.username ?? "";
        }
    },

    actions: {
        login(token, userInfo) {
            this.token = token;
            this.userInfo = userInfo;
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

        logout() {
            this.$reset(); // Limpia la store completa
        }
    },

    persist: {
        key: 'auth',
        storage: localStorage,
        paths: ['token', 'userInfo'] // Solo guarda las propiedades token y userInfo
    }
});
