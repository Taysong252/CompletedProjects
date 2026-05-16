package cr.ac.una.vivero.repository;

import cr.ac.una.vivero.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);
    
    List<Usuario> findByTipoUsuarioOrderByNombreAsc(String tipoUsuario);
    
    List<Usuario> findAllByOrderByNombreAsc();
   
    Optional<Usuario> findByEmailAndContrasenaAndActivoTrue(String email, String contrasena);
    @Modifying
    @Query("UPDATE Usuario u SET u.activo = :activo WHERE u.id = :id")
    int updateActivo(@Param("id") int id, @Param("activo") boolean activo);
}