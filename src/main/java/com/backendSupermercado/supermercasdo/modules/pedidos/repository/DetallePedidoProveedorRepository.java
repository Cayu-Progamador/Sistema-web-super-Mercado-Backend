package com.backendSupermercado.supermercasdo.modules.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.pedidos.entity.DetallePedidoProveedor;

public interface DetallePedidoProveedorRepository extends JpaRepository<DetallePedidoProveedor, Long> {

    @Modifying
    @Query("DELETE FROM DetallePedidoProveedor d WHERE d.pedido.id = :idPedido")
    void deleteByPedidoId(@Param("idPedido") Long idPedido);
}