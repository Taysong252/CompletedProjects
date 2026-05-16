package cr.ac.una.vivero.repository;


import cr.ac.una.vivero.domain.Promocion;
import java.util.List;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromocionRepository extends JpaRepository<Promocion, Integer> {

    Optional<Promocion> findByCodigoPromo(String codigoPromo);

    List<Promocion> findByNombrePromocionContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
            String nombre, String descripcion);

    List<Promocion> findByActivaTrue();

    boolean existsByNombrePromocionIgnoreCase(String nombrePromocion);
    boolean existsByNombrePromocionIgnoreCaseAndIdNot(String nombrePromocion, int id);

    boolean existsByCodigoPromo(String codigoPromo);
    boolean existsByCodigoPromoAndIdNot(String codigoPromo, int id);
    
    List<Promocion> findByActiva(boolean activa);
}
