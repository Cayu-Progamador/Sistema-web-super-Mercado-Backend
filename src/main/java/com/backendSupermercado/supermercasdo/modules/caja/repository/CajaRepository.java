package com.backendSupermercado.supermercasdo.modules.caja.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backendSupermercado.supermercasdo.modules.caja.entity.Caja;

public interface CajaRepository extends JpaRepository<Caja, Long> {
    Optional<Caja> findByNombre(String nombre);
}
