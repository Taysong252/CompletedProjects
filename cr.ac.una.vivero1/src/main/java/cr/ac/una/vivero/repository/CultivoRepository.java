package cr.ac.una.vivero.repository;

import cr.ac.una.vivero.domain.Cultivo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CultivoRepository extends JpaRepository<Cultivo, Integer> {
    List<Cultivo> findByCompletada(boolean completada);
    List<Cultivo> findAllByOrderByFechaSiembraDesc();
}