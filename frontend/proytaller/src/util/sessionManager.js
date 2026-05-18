import { useAuthStore } from '../store/store';

export function initSessionManager(router) {
    const auth = useAuthStore();

    // Solo activar si hay usuario logueado
    if (!auth.token) return;

    let lastActivity = Date.now();
    const TIMEOUT = 30 * 60 * 1000; // 1 hora

   //const TIMEOUT = 10000; // 10 segundos

    function resetTimer() {
        lastActivity = Date.now();
    }

    window.addEventListener("mousemove", resetTimer);
    window.addEventListener("keydown", resetTimer);
    window.addEventListener("click", resetTimer);

    setInterval(() => {
        if (!auth.token) return; // Si cerró sesión manualmente

        const diff = Date.now() - lastActivity;
        if (diff > TIMEOUT) {
            auth.logout();
            router.push("/login");
        }
    }, 30000); // revisa cada 30 segundos
}
