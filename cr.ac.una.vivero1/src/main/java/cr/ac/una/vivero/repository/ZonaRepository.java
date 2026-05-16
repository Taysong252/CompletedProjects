package cr.ac.una.vivero.repository;

import cr.ac.una.vivero.domain.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ZonaRepository extends JpaRepository<Zona, Integer> {

    List<Zona> findByNombreZonaContainingIgnoreCaseOrTipoContainingIgnoreCase(String nombre, String tipo);

    List<Zona> findByTipoContainingIgnoreCase(String tipo);

    List<Zona> findByActiva(boolean activa);
}