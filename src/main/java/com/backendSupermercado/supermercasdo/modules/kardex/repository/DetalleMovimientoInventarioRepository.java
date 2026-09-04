package com.backendSupermercado.supermercasdo.modules.kardex.repository;
import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.data.jpa.repository.JpaSpecificationExecutor; import com.backendSupermercado.supermercasdo.modules.kardex.entity.DetalleMovimientoInventario;
public interface DetalleMovimientoInventarioRepository extends JpaRepository<DetalleMovimientoInventario,Long>, JpaSpecificationExecutor<DetalleMovimientoInventario> { }
