package com.backendSupermercado.supermercasdo.modules.permiso_personal.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.backendSupermercado.supermercasdo.modules.permiso_personal.service.SolicitudPermisoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpiredRequestsJob {

    private final SolicitudPermisoService solicitudPermisoService;

    @Scheduled(cron = "0 0 6 * * ?")
    public void expireRequests() {
        log.info("Iniciando auto-expiracion de solicitudes vencidas...");
        solicitudPermisoService.expirarVencidas();
        log.info("Auto-expiracion completada.");
    }
}
