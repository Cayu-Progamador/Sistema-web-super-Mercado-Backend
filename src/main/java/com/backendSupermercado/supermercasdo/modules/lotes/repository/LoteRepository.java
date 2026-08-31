package com.backendSupermercado.supermercasdo.modules.lotes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.lotes.entity.Lote;

public interface LoteRepository extends JpaRepository<Lote, Long> {

    Optional<Lote> findByProducto_IdProductoAndCodigoLote(Long idProducto, String codigoLote);

    boolean existsByProducto_IdProductoAndCodigoLote(Long idProducto, String codigoLote);
}