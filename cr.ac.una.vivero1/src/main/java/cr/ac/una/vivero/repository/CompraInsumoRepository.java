package cr.ac.una.vivero.repository;

import cr.ac.una.vivero.domain.CompraInsumo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompraInsumoRepository extends JpaRepository<CompraInsumo, Integer> {

    List<CompraInsumo> findByInsumoIdOrderByFechaCompraDesc(int insumoId);

    List<CompraInsumo> findAllByOrderByFechaCompraDesc();
}
