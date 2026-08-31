package com.backendSupermercado.supermercasdo.modules.transferencias_almacen.service;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.dto.TransferenciaListadoDto;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.dto.CrearTransferenciaRequest;

public interface TransferenciaAlmacenService {
    TransferenciaListadoDto crear(CrearTransferenciaRequest request, boolean borrador);
    TransferenciaListadoDto actualizarBorrador(Long id, CrearTransferenciaRequest request);
    TransferenciaListadoDto cambiarEstado(Long id, String estado);
    TransferenciaListadoDto confirmarBorrador(Long id);
    void eliminarBorrador(Long id);
    Page<TransferenciaListadoDto> listar(String texto, String estado, Long usuarioId, String responsable,
            Long almacenOrigenId, Long almacenDestinoId, LocalDate fechaDesde, LocalDate fechaHasta,
            Pageable pageable);
}
