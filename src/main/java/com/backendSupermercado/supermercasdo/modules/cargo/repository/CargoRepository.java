package com.backendSupermercado.supermercasdo.modules.cargo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.cargo.entity.Cargo;



public interface CargoRepository extends JpaRepository<Cargo, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    List<Cargo> findByNombreContainingIgnoreCaseAndEstado(String nombre, Boolean estado);
    List<Cargo> findByNombreContainingIgnoreCase(String nombre);
    List<Cargo> findByEstado(Boolean estado);
}
