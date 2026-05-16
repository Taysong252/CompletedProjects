package cr.ac.una.vivero.repository;

import cr.ac.una.vivero.domain.Visita;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitaRepository extends JpaRepository<Visita, Integer> {

    List<Visita> findByMotivoVisitaContainingIgnoreCase(String motivo);

    List<Visita> findByEstadoIgnoreCase(String estado);

    List<Visita> findByClienteId(int clienteId);

    List<Visita> findByClienteIdAndMotivoVisitaContainingIgnoreCase(
            int clienteId,
            String motivo
    );

    List<Visita> findByClienteIdAndEstadoIgnoreCase(
            int clienteId,
            String estado
    );

    List<Visita> findByAdminIdAndFechaVisita(
            int adminId,
            LocalDate fechaVisita
    );

    List<Visita> findByClienteIdAndFechaVisita(
            int clienteId,
            LocalDate fechaVisita
    );
}
