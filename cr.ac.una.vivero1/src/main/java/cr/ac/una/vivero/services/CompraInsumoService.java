package cr.ac.una.vivero.services;

import cr.ac.una.vivero.domain.CompraInsumo;
import cr.ac.una.vivero.domain.Insumo;
import cr.ac.una.vivero.repository.CompraInsumoRepository;
import cr.ac.una.vivero.repository.InsumoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompraInsumoService {

    @Autowired
    private CompraInsumoRepository compraRepository;

    @Autowired
    private InsumoRepository insumoRepository;

    public String registrarCompra(CompraInsumo compra) {
        Insumo insumo = insumoRepository.findById(compra.getInsumoId()).orElse(null);
        if (insumo == null) {
            return "El insumo seleccionado no existe.";
        }

        compraRepository.save(compra);

        insumo.setCantidadStock(insumo.getCantidadStock() + compra.getCantidad());
        insumoRepository.save(insumo);

        return null;
    }

    public List<CompraInsumo> getHistorialPorInsumo(int insumoId) {
        List<CompraInsumo> compras = compraRepository.findByInsumoIdOrderByFechaCompraDesc(insumoId);
        for (CompraInsumo c : compras) {
            Insumo insumo = insumoRepository.findById(c.getInsumoId()).orElse(null);
            if (insumo != null) {
                c.setNombreInsumo(insumo.getNombreInsumo());
            }
        }
        return compras;
    }

    public List<CompraInsumo> getTodas() {
        List<CompraInsumo> compras = compraRepository.findAllByOrderByFechaCompraDesc();
        for (CompraInsumo c : compras) {
            Insumo insumo = insumoRepository.findById(c.getInsumoId()).orElse(null);
            if (insumo != null) {
                c.setNombreInsumo(insumo.getNombreInsumo());
            }
        }
        return compras;
    }

    public CompraInsumo getById(int id) {
        return compraRepository.findById(id).orElse(null);
    }

    public void eliminar(int id) {
        compraRepository.deleteById(id);
    }
}
