package com.backendSupermercado.supermercasdo.modules.empleado.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.empleado.dto.CiudadDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.DepartamentoGeograficoDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.PaisDto;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.CiudadRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.DepartamentoGeograficoRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.PaisRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ubicaciones")
@RequiredArgsConstructor
public class UbicacionGeograficaController {

    private final PaisRepository paisRepository;
    private final DepartamentoGeograficoRepository departamentoRepository;
    private final CiudadRepository ciudadRepository;

    @GetMapping("/paises")
    public ResponseEntity<List<PaisDto>> listarPaises() {
        List<PaisDto> paises = paisRepository.findAllByOrderByNombreAsc().stream()
                .map(pais -> new PaisDto(pais.getIdPais(), pais.getNombre()))
                .toList();
        return ResponseEntity.ok(paises);
    }

    @GetMapping("/paises/{idPais}/departamentos")
    public ResponseEntity<List<DepartamentoGeograficoDto>> listarDepartamentos(@PathVariable Long idPais) {
        List<DepartamentoGeograficoDto> departamentos = departamentoRepository
                .findByPaisIdPaisOrderByNombreAsc(idPais).stream()
                .map(departamento -> new DepartamentoGeograficoDto(
                        departamento.getIdDepartamento(), departamento.getNombre()))
                .toList();
        return ResponseEntity.ok(departamentos);
    }

    @GetMapping("/departamentos/{idDepartamento}/ciudades")
    public ResponseEntity<List<CiudadDto>> listarCiudades(@PathVariable Long idDepartamento) {
        List<CiudadDto> ciudades = ciudadRepository
                .findByDepartamentoIdDepartamentoOrderByNombreAsc(idDepartamento).stream()
                .map(ciudad -> new CiudadDto(ciudad.getIdCiudad(), ciudad.getNombre()))
                .toList();
        return ResponseEntity.ok(ciudades);
    }
}
