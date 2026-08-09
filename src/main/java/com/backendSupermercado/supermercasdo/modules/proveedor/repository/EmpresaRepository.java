package com.backendSupermercado.supermercasdo.modules.proveedor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.proveedor.entity.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    boolean existsByNit(String nit);

    boolean existsByNitAndIdEmpresaNot(String nit, Long idEmpresa);
}
