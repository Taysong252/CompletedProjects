package cr.ac.una.vivero.services;

import cr.ac.una.vivero.domain.Tarea;
import cr.ac.una.vivero.repository.TareaRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TareaService implements CRUD<Tarea> {

    private static final List<String> PRIORIDADES_VALIDAS = List.of("Alta", "Media", "Baja");

    @Autowired
    private TareaRepository tareaRepository;

    @Override
    public void save(Tarea tarea) {
        tareaRepository.save(tarea);
    }

    @Override
    public void delete(int id) {
        tareaRepository.deleteById(id);
    }

    @Override
    public List<Tarea> getAll() {
        return tareaRepository.findAllByOrderByFechaProgramadaDesc();
    }

    @Override
    public Tarea getById(int id) {
        return tareaRepository.findById(id).orElse(null);
    }

    public Map<String, String> validar(Tarea t, boolean esEdicion) {
        Map<String, String> errores = new HashMap<>();

        if (t.getPlanta() == null || t.getPlanta().getId() <= 0)
            errores.put("errorPlanta", "Debe seleccionar una planta.");

        if (t.getEmpleado() == null || t.getEmpleado().getId() <= 0)
            errores.put("errorEmpleado", "Debe seleccionar un empleado.");

        if (t.getTipoTarea() == null || t.getTipoTarea().trim().isEmpty()) {
            errores.put("errorTipoTarea", "El tipo de tarea es obligatorio.");
        } else if (t.getTipoTarea().trim().length() > 100) {
            errores.put("errorTipoTarea", "No puede superar los 100 caracteres.");
        } else if (!t.getTipoTarea().trim().matches("[\\p{L}\\s\\-/,\\.()]+")) {
            errores.put("errorTipoTarea", "Solo se permiten letras, espacios y caracteres básicos.");
        }

        if (t.getDescripcion() != null && t.getDescripcion().length() > 255)
            errores.put("errorDescripcion", "La descripción no puede superar los 255 caracteres.");

        if (t.getFechaProgramada() == null) {
            errores.put("errorFechaProgramada", "La fecha programada es obligatoria.");
        } else if (!esEdicion && t.getFechaProgramada().isBefore(LocalDate.now())) {
            errores.put("errorFechaProgramada", "La fecha no puede ser anterior a hoy.");
        } else if (t.getFechaProgramada().isAfter(LocalDate.now().plusYears(5))) {
            errores.put("errorFechaProgramada", "La fecha no puede ser mayor a 5 años en el futuro.");
        }

        if (t.getPrioridad() == null || t.getPrioridad().trim().isEmpty()) {
            errores.put("errorPrioridad", "La prioridad es obligatoria.");
        } else if (PRIORIDADES_VALIDAS.stream()
                .noneMatch(p -> p.equalsIgnoreCase(t.getPrioridad().trim()))) {
            errores.put("errorPrioridad", "La prioridad debe ser: Alta, Media o Baja.");
        }

        if (t.getObservaciones() != null && t.getObservaciones().length() > 500)
            errores.put("errorObservaciones", "Las observaciones no pueden superar los 500 caracteres.");

        return errores;
    }

    public String crear(Tarea tarea) {
        tarea.setCompletada(false);
        try {
            tareaRepository.save(tarea);
            return null;
        } catch (Exception e) {
            return "No se pudo registrar la tarea, intente de nuevo.";
        }
    }

    public String actualizar(int id, Tarea datos) {
        Tarea guardada = tareaRepository.findById(id).orElse(null);
        if (guardada == null) return "Tarea no encontrada.";

        guardada.setPlanta(datos.getPlanta());
        guardada.setEmpleado(datos.getEmpleado());
        guardada.setTipoTarea(datos.getTipoTarea());
        guardada.setDescripcion(datos.getDescripcion());
        guardada.setFechaProgramada(datos.getFechaProgramada());
        guardada.setPrioridad(datos.getPrioridad());
        guardada.setObservaciones(datos.getObservaciones());
        guardada.setCompletada(datos.isCompletada());

        try {
            tareaRepository.save(guardada);
            return null;
        } catch (Exception e) {
            return "No se pudo actualizar la tarea.";
        }
    }

    public List<Tarea> buscar(String criterio) {
        return tareaRepository.findByPrioridadContainingIgnoreCaseOrTipoTareaContainingIgnoreCase(criterio, criterio);
    }

    public List<Tarea> buscarPorPrioridad(String prioridad) {
        return tareaRepository.findByPrioridadIgnoreCase(prioridad);
    }

    public List<Tarea> buscarPorCompletada(boolean completada) {
        return tareaRepository.findByCompletada(completada);
    }
}