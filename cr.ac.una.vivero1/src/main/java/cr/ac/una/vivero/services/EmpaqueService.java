package cr.ac.una.vivero.services;

import cr.ac.una.vivero.domain.Empaque;
import cr.ac.una.vivero.repository.EmpaqueRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpaqueService implements CRUD<Empaque> {

    @Autowired
    private EmpaqueRepository empaqueRepository;

    @Override
    public void save(Empaque empaque) {
        empaqueRepository.save(empaque);
    }

    @Override
    public void delete(int id) {
        empaqueRepository.deleteById(id);
    }

    @Override
    public List getAll() {
        return empaqueRepository.findAll();
    }

    @Override
    public Empaque getById(int id) {
        return empaqueRepository.findById(id).orElse(null);
    }

    public boolean existeTipo(String tipo, int id) {
        return empaqueRepository.existsByTipoIgnoreCaseAndIdNot(tipo, id);
    }

    public List<Empaque> buscar(String criterio) {
        return empaqueRepository.findByTipoContainingIgnoreCaseOrDescripcionContainingIgnoreCase(criterio, criterio);
    }

    public List<Empaque> getAllActivos() {
        return empaqueRepository.findByActivoTrue();
    }

    public List<Empaque> buscarPorActivo(boolean activo) {
        return empaqueRepository.findByActivo(activo);
    }
}