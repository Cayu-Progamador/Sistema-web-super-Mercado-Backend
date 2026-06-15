package com.backendSupermercado.supermercasdo.modules.seguridad.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolSelectDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.Rol;

@Repository
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

}
