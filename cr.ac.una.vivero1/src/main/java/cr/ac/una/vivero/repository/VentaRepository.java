package cr.ac.una.vivero.repository;

import cr.ac.una.vivero.domain.Venta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    Optional<Venta> findByCodigoVenta(String codigoVenta);

    List<Venta> findByEstadoOrderByFechaVentaDesc(String estado);

    List<Venta> findByMetodoPagoOrderByFechaVentaDesc(String metodoPago);

    List<Venta> findAllByOrderByFechaVentaDesc();

    // Búsqueda por código o notas
    @Query("SELECT v FROM Venta v WHERE " +
           "LOWER(v.codigoVenta) LIKE LOWER(CONCAT('%',:criterio,'%')) OR " +
           "LOWER(v.estado) LIKE LOWER(CONCAT('%',:criterio,'%')) OR " +
           "LOWER(v.metodoPago) LIKE LOWER(CONCAT('%',:criterio,'%'))")
    List<Venta> buscarPorCriterio(@Param("criterio") String criterio);

    // Estadísticas
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.estado = :estado")
    long contarPorEstado(@Param("estado") String estado);
}