package cr.ac.una.vivero.repository;

import cr.ac.una.vivero.domain.Empaque;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmpaqueRepository extends JpaRepository<Empaque, Integer> {
    boolean existsByTipoIgnoreCase(String tipo);
    boolean existsByTipoIgnoreCaseAndIdNot(String tipo, int id);
    List<Empaque> findByTipoContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String tipo, String descripcion);
    List<Empaque> findByActivoTrue();
    List<Empaque> findByActivo(boolean activo);
}