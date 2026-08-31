package com.backendSupermercado.supermercasdo.modules.transferencias_almacen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.entity.TransferenciaAlmacen;

public interface TransferenciaAlmacenRepository extends JpaRepository<TransferenciaAlmacen, Long>, JpaSpecificationExecutor<TransferenciaAlmacen> { }
