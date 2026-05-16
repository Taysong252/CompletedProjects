package cr.ac.una.vivero.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "tareas")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantaId", nullable = false)
    private Planta planta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleadoId", nullable = false)
    private Usuario empleado;

    @Column(nullable = false, length = 100)
    private String tipoTarea;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaProgramada;

    @Column(nullable = false, length = 50)
    private String prioridad;

    @Column(nullable = false)
    private boolean completada;

    @Column(length = 500)
    private String observaciones;

    public Tarea() {
    }

    public Tarea(Planta planta, Usuario empleado, String tipoTarea, String descripcion,
            LocalDate fechaProgramada, String prioridad,
            boolean completada, String observaciones) {
        this.planta = planta;
        this.empleado = empleado;
        this.tipoTarea = tipoTarea;
        this.descripcion = descripcion;
        this.fechaProgramada = fechaProgramada;
        this.prioridad = prioridad;
        this.completada = completada;
        this.observaciones = observaciones;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Planta getPlanta() {
        return planta;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
    }

    public Usuario getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Usuario empleado) {
        this.empleado = empleado;
    }

    public String getTipoTarea() {
        return tipoTarea;
    }

    public void setTipoTarea(String tipoTarea) {
        this.tipoTarea = tipoTarea;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDate fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}