package cr.ac.una.vivero.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "entregas")
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ventaId", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clienteId", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administradorId", nullable = false)
    private Usuario administrador;

    @Column(nullable = false, length = 255)
    private String direccionEntrega;

    @Column(nullable = false)
    private LocalDate fechaProgramada;

    private LocalDate fechaEntregada;

    @Column(nullable = false, length = 50)
    private String estadoEntrega;

    @Column(nullable = false, length = 50)
    private String metodoEntrega;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    @Column(length = 255)
    private String observaciones;

    @Column(nullable = false)
    private boolean confirmada;

    public Entrega() {
    }

    public String getNombreCliente() {
        return cliente != null
                ? cliente.getNombre() + " " + cliente.getApellido()
                : null;
    }

    public String getNombreAdministrador() {
        return administrador != null
                ? administrador.getNombre() + " " + administrador.getApellido()
                : null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public Usuario getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Usuario administrador) {
        this.administrador = administrador;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String d) {
        this.direccionEntrega = d;
    }

    public LocalDate getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDate f) {
        this.fechaProgramada = f;
    }

    public LocalDate getFechaEntregada() {
        return fechaEntregada;
    }

    public void setFechaEntregada(LocalDate f) {
        this.fechaEntregada = f;
    }

    public String getEstadoEntrega() {
        return estadoEntrega;
    }

    public void setEstadoEntrega(String e) {
        this.estadoEntrega = e;
    }

    public String getMetodoEntrega() {
        return metodoEntrega;
    }

    public void setMetodoEntrega(String m) {
        this.metodoEntrega = m;
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal c) {
        this.costoEnvio = c;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String o) {
        this.observaciones = o;
    }

    public boolean isConfirmada() {
        return confirmada;
    }

    public void setConfirmada(boolean c) {
        this.confirmada = c;
    }
}
