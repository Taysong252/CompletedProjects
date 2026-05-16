package cr.ac.una.vivero.services;

import cr.ac.una.vivero.domain.Promocion;
import cr.ac.una.vivero.repository.PromocionRepository;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromocionService implements CRUD<Promocion> {

    @Autowired
    private PromocionRepository promocionRepository;

    @Override
    public void save(Promocion promocion) {
        promocionRepository.save(promocion);
    }

    @Override
    public void delete(int id) {
        promocionRepository.deleteById(id);
    }

    @Override
    public List getAll() {
        return promocionRepository.findAll();
    }

    public List<Promocion> buscar(String criterio) {
        return promocionRepository
                .findByNombrePromocionContainingIgnoreCaseOrDescripcionContainingIgnoreCase(criterio, criterio);
    }

    @Override
    public Promocion getById(int id) {
        return promocionRepository.findById(id).orElse(null);
    }

    public Promocion getByCodigo(String codigo) {
        return promocionRepository.findByCodigoPromo(codigo).orElse(null);
    }

    public List<Promocion> getAllActivas() {
        return promocionRepository.findByActivaTrue();
    }

    public boolean nombreYaExiste(String nombre, int id) {
        if (id == 0) {
            return promocionRepository.existsByNombrePromocionIgnoreCase(nombre);
        }
        return promocionRepository.existsByNombrePromocionIgnoreCaseAndIdNot(nombre, id);
    }

    public boolean codigoYaExiste(String codigo, int id) {
        if (id == 0) {
            return promocionRepository.existsByCodigoPromo(codigo);
        }
        return promocionRepository.existsByCodigoPromoAndIdNot(codigo, id);
    }

    public List<Promocion> buscarPorActiva(boolean activa) {
        return promocionRepository.findByActiva(activa);
    }

    public Promocion getByCodigoActiva(String codigo, double subtotal) {
        Promocion promo = promocionRepository.findByCodigoPromo(codigo.trim()).orElse(null);

        if (promo == null) {
            throw new IllegalArgumentException("El código de promoción no existe.");
        }

        if (!promo.isActiva()) {
            throw new IllegalArgumentException("La promoción no está activa.");
        }

        LocalDate hoy = LocalDate.now();

        if (hoy.isBefore(promo.getFechaInicio())) {
            throw new IllegalArgumentException("La promoción aún no ha comenzado.");
        }

        if (hoy.isAfter(promo.getFechaFin())) {
            throw new IllegalArgumentException("La promoción ya venció.");
        }

        if (subtotal < promo.getMontoMinimo()) {
            throw new IllegalArgumentException(
                    "El subtotal mínimo para esta promoción es ₡"
                    + String.format("%,.0f", promo.getMontoMinimo()) + "."
            );
        }

        return promo;
    }

}