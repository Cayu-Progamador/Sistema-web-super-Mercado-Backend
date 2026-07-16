package com.backendSupermercado.supermercasdo.modules.contrato.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.backendSupermercado.supermercasdo.modules.contrato.entity.Contrato;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;

public interface ContratoRepository extends JpaRepository<Contrato, Long>, JpaSpecificationExecutor<Contrato> {
    Optional<Contrato> findByEmpleadoAndEstado(Empleado empleado, String estado);

    @Query("""
        SELECT c FROM Contrato c
        JOIN FETCH c.contratoTurnos ct
        JOIN FETCH ct.turno t
        WHERE c.estado = 'ACTIVO'
        AND c.controlaAsistencia = true
        AND (c.fechaFin IS NULL OR c.fechaFin >= CURRENT_DATE)
    """)
    List<Contrato> findAllWithControlAsistencia();
}
