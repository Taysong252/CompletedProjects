package cr.ac.una.vivero.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "codigoVenta", nullable = true)
    private String codigoVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Usuario admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promocion_id")
    private Promocion promocion;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaVenta;

    @Column(nullable = false)
    private double subtotal;

    @Column(nullable = false)
    private double descuentoAplicado;

    @Column(nullable = false)
    private double impuesto;

    @Column(nullable = false)
    private double total;

    @Column(nullable = false, length = 50)
    private String metodoPago;

    @Column(nullable = false, length = 50)
    private String estado;

    @Column(length = 500)
    private String notasAdicionales;

    @Column(nullable = false)
    private boolean facturado;

    public Venta() {
        this.fechaVenta = LocalDate.now();
        this.estado = "Pendiente";
        this.facturado = false;
        this.subtotal = 0.0;
        this.descuentoAplicado = 0.0;
        this.impuesto = 0.0;
        this.total = 0.0;
    }

    public static String generarCodigo(int id) {
        return "VTA-" + id ;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoVenta() {
        return codigoVenta;
    }

    public void setCodigoVenta(String codigoVenta) {
        this.codigoVenta = codigoVenta;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public Usuario getAdmin() {
        return admin;
    }

    public void setAdmin(Usuario admin) {
        this.admin = admin;
    }

    public Promocion getPromocion() {
        return promocion;
    }

    public void setPromocion(Promocion promocion) {
        this.promocion = promocion;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDate fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDescuentoAplicado() {
        return descuentoAplicado;
    }

    public void setDescuentoAplicado(double descuentoAplicado) {
        this.descuentoAplicado = descuentoAplicado;
    }

    public double getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(double impuesto) {
        this.impuesto = impuesto;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNotasAdicionales() {
        return notasAdicionales;
    }

    public void setNotasAdicionales(String notasAdicionales) {
        this.notasAdicionales = notasAdicionales;
    }

    public boolean isFacturado() {
        return facturado;
    }

    public void setFacturado(boolean facturado) {
        this.facturado = facturado;
    }
}