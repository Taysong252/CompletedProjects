package cr.ac.una.vivero.services;

import cr.ac.una.vivero.domain.Insumo;
import cr.ac.una.vivero.repository.InsumoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InsumoService implements CRUD<Insumo> {

    @Autowired
    private InsumoRepository insumoRepository;

    @Override
    public void save(Insumo insumo) {
        insumoRepository.save(insumo);
    }

    @Override
    public void delete(int i) {
        insumoRepository.deleteById(i);
    }

    @Override
    public List<Insumo> getAll() {
        return insumoRepository.findAll();
    }

    @Override
    public Insumo getById(int i) {
        return insumoRepository.findById(i).orElse(null);
    }

    public List<Insumo> buscarPorNombre(String nombre) {
        return insumoRepository.findByNombreInsumoContainingIgnoreCase(nombre);
    }

    public List<Insumo> buscarPorCategoria(String categoria) {
        return insumoRepository.findByCategoriaIgnoreCase(categoria);
    }

    public List<Insumo> getActivos() {
        return insumoRepository.findAll().stream()
                .filter(Insumo::isActivo)
                .collect(java.util.stream.Collectors.toList());
    }
}
