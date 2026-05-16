package cr.ac.una.vivero.repository;

import cr.ac.una.vivero.domain.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Integer> {

    List<Tarea> findByPrioridadContainingIgnoreCaseOrTipoTareaContainingIgnoreCase(String prioridad, String tipoTarea);

    List<Tarea> findByPrioridadIgnoreCase(String prioridad);

    List<Tarea> findByCompletada(boolean completada);

    List<Tarea> findAllByOrderByFechaProgramadaDesc();
}