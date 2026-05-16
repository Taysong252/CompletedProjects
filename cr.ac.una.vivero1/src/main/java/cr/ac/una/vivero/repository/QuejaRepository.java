package cr.ac.una.vivero.repository;

import cr.ac.una.vivero.domain.Queja;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuejaRepository extends JpaRepository<Queja, Integer> {

    Optional<Queja> findByCodigoQueja(String codigoQueja);

    List<Queja> findByEstadoOrderByFechaRegistroDesc(String estado);

    List<Queja> findByTipoRegistroOrderByFechaRegistroDesc(String tipoRegistro);

    List<Queja> findAllByOrderByFechaRegistroDesc();

    @Query("SELECT q FROM Queja q WHERE " +
           "LOWER(q.codigoQueja) LIKE LOWER(CONCAT('%',:criterio,'%')) OR " +
           "LOWER(q.descripcion) LIKE LOWER(CONCAT('%',:criterio,'%')) OR " +
           "LOWER(q.categoria) LIKE LOWER(CONCAT('%',:criterio,'%'))")
    List<Queja> buscarPorCriterio(@Param("criterio") String criterio);

    @Query("SELECT COUNT(q) FROM Queja q WHERE q.estado = :estado")
    long contarPorEstado(@Param("estado") String estado);
}