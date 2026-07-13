package com.backendSupermercado.supermercasdo.modules.empleado.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.empleado.dto.DashboardEmpleadoDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoDisponibleDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoFiltrosDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoListadoDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoRequestDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoResponseDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoSelectDto;

public interface EmpleadoService {
    
    public List<EmpleadoSelectDto> listarParaSelect();
    public List<EmpleadoSelectDto> listarParaEditar(Long usuarioId);
    public Page<EmpleadoListadoDto> listarEmpleados(String busqueda, Boolean estado, String fechaDesde, String fechaHasta, Pageable pageable);
    public Page<EmpleadoDisponibleDto> listarDisponiblesParaContrato(String busqueda, Pageable pageable);

    public EmpleadoResponseDto crearEmpleado(EmpleadoRequestDto dto);
    public EmpleadoResponseDto actualizarEmpleado(Long id, EmpleadoRequestDto dto);
    public EmpleadoResponseDto obtenerEmpleado(Long id);
    public void activarEmpleado(Long id);
    public void desactivarEmpleado(Long id);

    public byte[] exportarEmpleadoDetallePDF(Long id, String username);

    public List<EmpleadoListadoDto> exportarEmpleados(EmpleadoFiltrosDto filtros);

    public byte[] exportarEmpleadosPDF(EmpleadoFiltrosDto filtros, String username);

    public byte[] exportarEmpleadosExcel(EmpleadoFiltrosDto filtros);

    public DashboardEmpleadoDto obtenerEstadisticasEmpleado();
}
