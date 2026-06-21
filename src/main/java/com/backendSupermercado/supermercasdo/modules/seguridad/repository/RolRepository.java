package com.backendSupermercado.supermercasdo.modules.seguridad.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolSelectDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {

	Optional<Rol> findByNombre(String nombre);
	
	@Query("""
		SELECT new com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolSelectDto(
		r.nombre,
		r.descripcion
		)	
		FROM Rol r
		ORDER BY r.nombre, r.descripcion			
	""")
	List<RolSelectDto> listarRolSelect();

	List<Rol> findByNombreIn(List<String> nombres);

	boolean existsByNombre(String nombre);

	@Query("""
		SELECT r FROM Rol r
		WHERE LOWER(r.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
		ORDER BY r.nombre
	""")
	Page<Rol> buscarPorNombre(@Param("nombre") String nombre, Pageable pageable);

}
