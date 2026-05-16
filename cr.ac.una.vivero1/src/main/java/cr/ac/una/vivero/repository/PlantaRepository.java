package cr.ac.una.vivero.repository;

import cr.ac.una.vivero.domain.Planta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantaRepository extends JpaRepository<Planta, Integer> {
    List<Planta> findByTipoContainingIgnoreCase(String tipo);
    List<Planta> findByActivo(boolean activo);
    List<Planta> findByActivoTrueOrderByNombreComunAsc();
}