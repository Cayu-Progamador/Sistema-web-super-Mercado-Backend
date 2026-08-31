package com.backendSupermercado.supermercasdo.modules.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.pedidos.entity.PedidoProveedor;

public interface PedidoRepository extends JpaRepository<PedidoProveedor, Long>, JpaSpecificationExecutor<PedidoProveedor> {

    @Query("SELECT COUNT(p) FROM PedidoProveedor p "
            + "WHERE p.proveedor.id = :idProveedor "
            + "AND p.estadoPedido.nombre IN ('BORRADOR', 'ENVIADO') "
            + "AND (:idPedidoExcluido IS NULL OR p.idPedido <> :idPedidoExcluido)")
    long countActivosPorProveedor(@Param("idProveedor") Long idProveedor,
            @Param("idPedidoExcluido") Long idPedidoExcluido);
}