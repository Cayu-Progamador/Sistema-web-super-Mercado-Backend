package com.backendSupermercado.supermercasdo.modules.inventario_fisico.service;

import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.CrearInventarioFisicoRequest;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.InventarioFisicoResponse;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.InventarioFisicoListadoDto;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.ConteoFisicoPaginaDto;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.ConteoFisicoResumenDto;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.GuardarConteoFisicoRequest;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.FinalizarConteoFisicoResponse;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.enums.EstadoInventarioFisico;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.GuardarDiferenciasRequest;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.CancelarInventarioFisicoRequest;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.ConteoFisicoDetalleDto;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.InventarioFisicoEstadoResponse;

public interface InventarioFisicoService {
    InventarioFisicoResponse crear(CrearInventarioFisicoRequest request);
    InventarioFisicoResponse actualizarBorrador(Long idInventarioFisico, CrearInventarioFisicoRequest request);
    InventarioFisicoResponse iniciar(Long idInventarioFisico);
    InventarioFisicoResponse obtener(Long idInventarioFisico);
    Page<InventarioFisicoListadoDto> listar(String buscar, EstadoInventarioFisico estado,
            Long idAlmacen, LocalDate fechaDesde, LocalDate fechaHasta, Pageable pageable);
    ConteoFisicoPaginaDto listarConteo(Long idInventarioFisico, String buscar,
            Boolean soloPendientes, Boolean soloDiferencias, Pageable pageable);
    ConteoFisicoResumenDto guardarConteo(Long idInventarioFisico, GuardarConteoFisicoRequest request);
    FinalizarConteoFisicoResponse finalizarConteo(Long idInventarioFisico);
    Page<ConteoFisicoDetalleDto> listarDiferencias(
            Long idInventarioFisico, String buscar, Pageable pageable);
    InventarioFisicoEstadoResponse guardarDiferencias(Long idInventarioFisico, GuardarDiferenciasRequest request);
    InventarioFisicoEstadoResponse aprobar(Long idInventarioFisico);
    InventarioFisicoEstadoResponse cancelar(Long idInventarioFisico, CancelarInventarioFisicoRequest request);
    void eliminarBorrador(Long idInventarioFisico);
    byte[] generarActaPdf(Long idInventarioFisico);
}
