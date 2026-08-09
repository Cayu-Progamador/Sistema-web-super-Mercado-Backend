package com.backendSupermercado.supermercasdo.modules.proveedor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.proveedor.entity.ContactoEmpresa;

public interface ContactoEmpresaRepository extends JpaRepository<ContactoEmpresa, Long> {

    boolean existsByTelefono(String telefono);

    boolean existsByCorreo(String correo);

    boolean existsByTelefonoAndEmpresa_IdEmpresaNot(String telefono, Long idEmpresa);

    boolean existsByCorreoAndEmpresa_IdEmpresaNot(String correo, Long idEmpresa);
}
