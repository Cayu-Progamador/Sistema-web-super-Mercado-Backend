package com.backendSupermercado.supermercasdo.modules.productos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.productos.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findBySubcategoriaIdSubcategoriaOrderByNombreAsc(Long idSubcategoria);
}
