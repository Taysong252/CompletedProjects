package cr.ac.una.vivero.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "visitas")
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clienteId")
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administradorId")
    private Usuario admin;

    
    @Column(name = "clienteId", insertable = false, updatable = false)
    private Integer clienteId;

    @Column(name = "administradorId", insertable = false, updatable = false)
    private Integer adminId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaVisita;

    private String motivoVisita;
    private int cantidadPersonas;
    private String estado;
    private String observaciones;
    private boolean confirmada;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaFin;

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonIgnore
    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    @JsonIgnore
    public Usuario getAdmin() {
        return admin;
    }

    public void setAdmin(Usuario admin) {
        this.admin = admin;
    }

    // IDs REALES
    public Integer getClienteId() {
        return clienteId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public LocalDate getFechaVisita() {
        return fechaVisita;
    }

    public void setFechaVisita(LocalDate fechaVisita) {
        this.fechaVisita = fechaVisita;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public String getMotivoVisita() {
        return motivoVisita;
    }

    public void setMotivoVisita(String motivoVisita) {
        this.motivoVisita = motivoVisita;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(int cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public boolean isConfirmada() {
        return confirmada;
    }

    public void setConfirmada(boolean confirmada) {
        this.confirmada = confirmada;
    }

    public String getClienteNombre() {
        return cliente != null
                ? cliente.getNombre() + " " + cliente.getApellido()
                : "";
    }

    public String getAdminNombre() {
        return admin != null
                ? admin.getNombre() + " " + admin.getApellido()
                : "";
    }
}
