package cr.ac.una.vivero.services;

import cr.ac.una.vivero.domain.Zona;
import cr.ac.una.vivero.repository.ZonaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ZonaService implements CRUD<Zona> {

    @Autowired
    private ZonaRepository zonaRepository;

    @Override
    public void save(Zona zona) {
        zonaRepository.save(zona);
    }

    @Override
    public void delete(int id) {
        zonaRepository.deleteById(id);
    }

    @Override
    public List<Zona> getAll() {
        return zonaRepository.findAll();
    }

    @Override
    public Zona getById(int id) {
        return zonaRepository.findById(id).orElse(null);
    }

    public List<Zona> buscar(String criterio) {
        return zonaRepository.findByNombreZonaContainingIgnoreCaseOrTipoContainingIgnoreCase(criterio, criterio);
    }

    public List<Zona> buscarPorTipo(String tipo) {
        return zonaRepository.findByTipoContainingIgnoreCase(tipo);
    }

    public List<Zona> buscarPorActiva(boolean activa) {
        return zonaRepository.findByActiva(activa);
    }

    public java.util.Map<String, String> validarConDetalle(Zona z) {
        java.util.Map<String, String> e = new java.util.HashMap<>();

        if (z.getNombreZona() == null || z.getNombreZona().trim().isEmpty()) {
            e.put("errorNombreZona", "El nombre de la zona es obligatorio.");
        } else if (z.getNombreZona().trim().length() > 100) {
            e.put("errorNombreZona", "El nombre no puede superar los 100 caracteres.");
        } else if (!z.getNombreZona().trim().matches("[\\p{L}\\s\\-0-9]+")) {
            e.put("errorNombreZona", "El nombre solo puede contener letras, números y guiones.");
        }

        if (z.getTipo() == null || z.getTipo().trim().isEmpty()) {
            e.put("errorTipo", "El tipo de zona es obligatorio.");
        } else if (z.getTipo().trim().length() > 100) {
            e.put("errorTipo", "El tipo no puede superar los 100 caracteres.");
        } else if (!z.getTipo().trim().matches("[\\p{L}\\s\\-]+")) {
            e.put("errorTipo", "El tipo solo puede contener letras, espacios y guiones.");
        }

        if (z.getDescripcion() != null && z.getDescripcion().length() > 255) {
            e.put("errorDescripcion", "La descripción no puede superar los 255 caracteres.");
        }

        if (z.getCapacidadMaxima() <= 0) {
            e.put("errorCapacidadMaxima", "La capacidad máxima debe ser mayor a 0.");
        } else if (z.getCapacidadMaxima() > 10000) {
            e.put("errorCapacidadMaxima", "La capacidad máxima no puede superar 10,000.");
        }

        if (z.getCantidadActual() < 0) {
            e.put("errorCantidadActual", "La cantidad actual no puede ser negativa.");
        } else if (z.getCapacidadMaxima() > 0 && z.getCantidadActual() > z.getCapacidadMaxima()) {
            e.put("errorCantidadActual", "La cantidad actual no puede superar la capacidad máxima.");
        }

        if (z.getTemperaturaPromedio() < -50 || z.getTemperaturaPromedio() > 80) {
            e.put("errorTemperaturaPromedio", "La temperatura debe estar entre -50 °C y 80 °C.");
        }

        if (z.getHumedadPromedio() < 0 || z.getHumedadPromedio() > 100) {
            e.put("errorHumedadPromedio", "La humedad debe estar entre 0% y 100%.");
        }

        return e;
    }
}
