package cr.ac.una.vivero.repository;

import cr.ac.una.vivero.domain.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EntregaRepository extends JpaRepository<Entrega, Integer> {

    List<Entrega> findAllByOrderByFechaProgramadaAsc();

    List<Entrega> findByEstadoEntregaOrderByFechaProgramadaAsc(String estadoEntrega);

    List<Entrega> findByCliente_IdOrderByFechaProgramadaAsc(int clienteId);

    @Modifying
    @Query("UPDATE Entrega e SET e.confirmada = :confirmada WHERE e.id = :id")
    int updateConfirmada(@Param("id") int id, @Param("confirmada") boolean confirmada);

    @Modifying
    @Query("UPDATE Entrega e SET e.estadoEntrega = :estado WHERE e.id = :id")
    int updateEstado(@Param("id") int id, @Param("estado") String estado);
}
