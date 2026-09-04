package com.backendSupermercado.supermercasdo.modules.inventario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.inventario.entity.ConfiguracionStock;

public interface ConfiguracionStockRepository extends JpaRepository<ConfiguracionStock, Long> {
    Optional<ConfiguracionStock> findByProductoIdProducto(Long idProducto);
}
