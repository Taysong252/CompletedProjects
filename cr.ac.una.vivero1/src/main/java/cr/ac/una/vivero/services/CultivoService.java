package cr.ac.una.vivero.services;

import cr.ac.una.vivero.domain.Cultivo;
import cr.ac.una.vivero.repository.CultivoRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CultivoService {

    @Autowired
    private CultivoRepository cultivoRepositorio;

    public ArrayList<Cultivo> listarTodos() {
        return new ArrayList<>(cultivoRepositorio.findAllByOrderByFechaSiembraDesc());
    }

    public ArrayList<Cultivo> listarPorEstado(String estado) {
        boolean completada = "completado".equalsIgnoreCase(estado);
        return new ArrayList<>(cultivoRepositorio.findByCompletada(completada));
    }

    public Cultivo obtenerPorId(int id) {
        return cultivoRepositorio.findById(id).orElse(null);
    }

    public String crear(Cultivo cultivo) {
        String error = validarCampos(cultivo);
        if (error != null) return error;
        cultivo.setCompletada(false);
        try {
            cultivoRepositorio.save(cultivo);
            return null;
        } catch (Exception e) {
            return "No se pudo registrar el cultivo, intente de nuevo.";
        }
    }

    public String actualizar(int id, Cultivo datos) {
        String error = validarCampos(datos);
        if (error != null) return error;
        Cultivo guardado = cultivoRepositorio.findById(id).orElse(null);
        if (guardado == null) return "Cultivo no encontrado.";
        guardado.setPlanta(datos.getPlanta());
        guardado.setAdmin(datos.getAdmin());
        guardado.setFechaSiembra(datos.getFechaSiembra());
        guardado.setFechaCosecha(datos.getFechaCosecha());
        guardado.setFechaTrasplante(datos.getFechaTrasplante());
        guardado.setTecnica(datos.getTecnica());
        guardado.setSustrato(datos.getSustrato());
        guardado.setObservaciones(datos.getObservaciones());
        guardado.setTasaExito(datos.getTasaExito());
        guardado.setCompletada(datos.isCompletada());
        try {
            cultivoRepositorio.save(guardado);
            return null;
        } catch (Exception e) {
            return "No se pudo actualizar el cultivo.";
        }
    }

    @Transactional
    public String completar(int id) {
        Cultivo cultivo = cultivoRepositorio.findById(id).orElse(null);
        if (cultivo == null) return "Cultivo no encontrado.";
        cultivo.setCompletada(true);
        cultivoRepositorio.save(cultivo);
        return null;
    }

    public String eliminar(int id) {
        if (!cultivoRepositorio.existsById(id)) return "Cultivo no encontrado.";
        try {
            cultivoRepositorio.deleteById(id);
            return null;
        } catch (Exception e) {
            return "No se pudo eliminar el cultivo.";
        }
    }

    private String validarCampos(Cultivo c) {
        if (c.getPlanta() == null || c.getPlanta().getId() <= 0)
            return "Debe seleccionar una planta.";
        if (c.getAdmin() == null || c.getAdmin().getId() <= 0)
            return "Debe seleccionar un administrador.";
        if (c.getFechaSiembra() == null)
            return "La fecha de siembra es obligatoria.";
        if (c.getFechaCosecha() != null && c.getFechaCosecha().isBefore(c.getFechaSiembra()))
            return "La fecha de cosecha no puede ser anterior a la de siembra.";
        if (c.getFechaTrasplante() != null && c.getFechaTrasplante().isBefore(c.getFechaSiembra()))
            return "La fecha de trasplante no puede ser anterior a la de siembra.";
        if (c.getTasaExito() < 0 || c.getTasaExito() > 100)
            return "La tasa de éxito debe estar entre 0 y 100.";
        return null;
    }
}