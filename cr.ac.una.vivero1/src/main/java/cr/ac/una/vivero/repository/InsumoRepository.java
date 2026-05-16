package cr.ac.una.vivero.repository;

import cr.ac.una.vivero.domain.Insumo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsumoRepository extends JpaRepository<Insumo, Integer> {

    List<Insumo> findByNombreInsumoContainingIgnoreCase(String nombre);

    List<Insumo> findByCategoriaIgnoreCase(String categoria);
}
