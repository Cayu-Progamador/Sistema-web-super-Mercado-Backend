import axios from "axios";
import { useAuthStore } from "../store/store";
import router from "../router";

// Crear instancia de axios
const instance = axios.create({
  baseURL: import.meta.env.VITE_API_URL,     
  timeout: Number(import.meta.env.VITE_TIMEOUT) 
});

// ======================
// Interceptor de Request
// ======================
instance.interceptors.request.use(
  config => {
    const store = useAuthStore();

    if (store.token) {
      config.headers.Authorization = `Bearer ${store.token}`;
    }

    if (!(config.data instanceof FormData)) {
        config.headers["Content-Type"] = "application/json";
    }
    return config;
  },
  error => Promise.reject(error)
);

// =======================
// Interceptor de Response
// =======================
instance.interceptors.response.use(
  response => response.data,
  error => {
    const store = useAuthStore();

    if (!error.response) {
      console.error("Error de conexión con el servidor");
      return Promise.reject(error);
    }

    const code = error.response.status;

    switch (code) {
      case 401:
        store.logout();
        router.push("/login");
        break;

      case 403:
        console.error("Acceso denegado");
        break;

      default:
        console.error(
          error.response.data?.message || "Error en la petición"
        );
    }

    return Promise.reject(error);
  }
);

export default instance;
