package cr.ac.una.vivero.services;

import cr.ac.una.vivero.domain.Planta;
import cr.ac.una.vivero.repository.PlantaRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PlantaService {

    @Autowired
    private PlantaRepository plantaRepositorio;

    public List<Planta> obtenerTodas() {
        return plantaRepositorio.findAll();
    }

    public Planta obtenerPorId(int id) {
        return plantaRepositorio.findById(id).orElse(null);
    }

    public List<Planta> buscarPorTipo(String tipo) {
        return plantaRepositorio.findByTipoContainingIgnoreCase(tipo);
    }

    public String crear(Planta planta, MultipartFile imagen) {
        String error = validarCampos(planta);
        if (error != null) {
            return error;
        }
        planta.setFechaIngreso(LocalDate.now());
        planta.setActivo(true);
        return procesarImagenYGuardar(planta, imagen);
    }

    public String actualizar(int id, Planta datos, MultipartFile imagen) {
        String error = validarCampos(datos);
        if (error != null) {
            return error;
        }
        Planta guardada = plantaRepositorio.findById(id).orElse(null);
        if (guardada == null) {
            return "Planta no encontrada.";
        }
        guardada.setNombreComun(datos.getNombreComun().trim());
        guardada.setNombreCientifico(datos.getNombreCientifico().trim());
        guardada.setPrecio(datos.getPrecio());
        guardada.setTipo(datos.getTipo().trim());
        guardada.setCantidad(datos.getCantidad());
        guardada.setDescripcionCuidados(datos.getDescripcionCuidados());
        guardada.setRequiereSol(datos.isRequiereSol());
        guardada.setActivo(datos.isActivo());
        if (imagen != null && !imagen.isEmpty()) {
            return procesarImagenYGuardar(guardada, imagen);
        }
        try {
            plantaRepositorio.save(guardada);
            return null;
        } catch (Exception e) {
            return "No se pudo actualizar la planta.";
        }
    }

    @Transactional
    public String activar(int id) {
        Planta p = plantaRepositorio.findById(id).orElse(null);
        if (p == null) {
            return "Planta no encontrada.";
        }
        p.setActivo(true);
        plantaRepositorio.save(p);
        return null;
    }

    @Transactional
    public String desactivar(int id) {
        Planta p = plantaRepositorio.findById(id).orElse(null);
        if (p == null) {
            return "Planta no encontrada.";
        }
        p.setActivo(false);
        plantaRepositorio.save(p);
        return null;
    }

    public String eliminar(int id) {
        if (!plantaRepositorio.existsById(id)) {
            return "Planta no encontrada.";
        }
        try {
            plantaRepositorio.deleteById(id);
            return null;
        } catch (Exception e) {
            return "No se puede eliminar la planta porque tiene cultivos asociados.";
        }
    }

    private String procesarImagenYGuardar(Planta planta, MultipartFile imagen) {
        if (imagen != null && !imagen.isEmpty()) {
            try {
                planta.setDatosImagen(imagen.getBytes());
                planta.setTipoImagen(imagen.getContentType());
            } catch (IOException e) {
                return "Error al procesar la imagen.";
            }
        }
        try {
            plantaRepositorio.save(planta);
            return null;
        } catch (Exception e) {
            return "No se pudo guardar la planta, intente de nuevo.";
        }
    }

    private String validarCampos(Planta p) {
        if (vacio(p.getNombreComun())) {
            return "El nombre común es obligatorio.";
        }
        if (p.getNombreComun().trim().length() > 100) {
            return "El nombre común no puede exceder 100 caracteres.";
        }
        if (vacio(p.getNombreCientifico())) {
            return "El nombre científico es obligatorio.";
        }
        if (p.getNombreCientifico().trim().length() > 100) {
            return "El nombre científico no puede exceder 100 caracteres.";
        }
        if (p.getPrecio() == null) {
            return "El precio es obligatorio.";
        }
        if (p.getPrecio() < 0) {
            return "El precio no puede ser negativo.";
        }
        if (vacio(p.getTipo())) {
            return "El tipo de planta es obligatorio.";
        }
        if (p.getCantidad() == null) {
            return "La cantidad es obligatoria.";
        }
        if (p.getCantidad() < 0) {
            return "La cantidad no puede ser negativa.";
        }
        return null;
    }

    private boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    public List<Planta> listarActivas() {
        return plantaRepositorio.findByActivoTrueOrderByNombreComunAsc();
    }
}
