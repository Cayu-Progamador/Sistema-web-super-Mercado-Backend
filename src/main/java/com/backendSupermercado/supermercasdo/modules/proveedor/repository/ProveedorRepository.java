package com.backendSupermercado.supermercasdo.modules.proveedor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.backendSupermercado.supermercasdo.modules.proveedor.entity.Proveedor;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.TipoProveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long>, JpaSpecificationExecutor<Proveedor> {

    long countByTipoProveedor(TipoProveedor tipoProveedor);

    long countByEstado(Boolean estado);
}
