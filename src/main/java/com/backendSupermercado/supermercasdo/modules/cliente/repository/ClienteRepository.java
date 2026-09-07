package com.backendSupermercado.supermercasdo.modules.cliente.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.cliente.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByCiIgnoreCase(String ci);

    boolean existsByCiIgnoreCaseAndIdClienteNot(String ci, Long idCliente);

    @Query("""
            SELECT c FROM Cliente c
            WHERE (:patron IS NULL
                   OR LOWER(c.nombres) LIKE :patron
                   OR LOWER(c.apellidoPaterno) LIKE :patron
                   OR LOWER(c.apellidoMaterno) LIKE :patron
                   OR LOWER(c.ci) LIKE :patron
                   OR LOWER(COALESCE(c.correo, '')) LIKE :patron)
              AND (:estado IS NULL OR c.estado = :estado)
            """)
    Page<Cliente> filtrar(@Param("patron") String patron,
            @Param("estado") Boolean estado,
            Pageable pageable);
}
