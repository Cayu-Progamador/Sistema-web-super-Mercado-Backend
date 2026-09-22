package com.backendSupermercado.supermercasdo.modules.seguridad.repository;

import java.util.List;

import com.backendSupermercado.supermercasdo.modules.seguridad.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findAllByOrderByOrdenAsc();
}
