package com.backendSupermercado.supermercasdo.modules.transferencias_almacen.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.entity.EstadoTransferencia;

public interface EstadoTransferenciaRepository extends JpaRepository<EstadoTransferencia, Long> {
    Optional<EstadoTransferencia> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}
