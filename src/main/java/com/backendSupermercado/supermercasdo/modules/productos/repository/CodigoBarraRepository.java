package com.backendSupermercado.supermercasdo.modules.productos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.productos.entity.CodigoBarra;

public interface CodigoBarraRepository extends JpaRepository<CodigoBarra, Long> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndPresentacionProducto_Producto_IdProductoNot(
            String codigo, Long idProducto);
}
