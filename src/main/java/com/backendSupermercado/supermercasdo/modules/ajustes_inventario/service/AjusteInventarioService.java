package com.backendSupermercado.supermercasdo.modules.ajustes_inventario.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto.AjusteInventarioListadoDto;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto.AjusteInventarioResponse;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto.CancelarAjusteRequest;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto.CrearAjusteInventarioRequest;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto.RechazarAjusteRequest;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.EstadoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.MotivoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.TipoAjusteInventario;

public interface AjusteInventarioService {

    AjusteInventarioResponse crear(CrearAjusteInventarioRequest request);

    AjusteInventarioResponse actualizarBorrador(Long id, CrearAjusteInventarioRequest request);

    AjusteInventarioResponse obtener(Long id);

    byte[] exportarPdf(Long id);

    Page<AjusteInventarioListadoDto> listar(String buscar, TipoAjusteInventario tipo,
            MotivoAjusteInventario motivo, EstadoAjusteInventario estado,
            LocalDate fechaDesde, LocalDate fechaHasta, Pageable pageable);

    AjusteInventarioResponse enviar(Long id);

    AjusteInventarioResponse aprobar(Long id);

    AjusteInventarioResponse rechazar(Long id, RechazarAjusteRequest request);

    AjusteInventarioResponse aplicar(Long id);

    AjusteInventarioResponse cancelar(Long id, CancelarAjusteRequest request);

    void eliminarBorrador(Long id);
}
