package cr.ac.una.vivero.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity                       
@Table(name = "promociones")       
public class Promocion {

    @Id                                                 
    @GeneratedValue(strategy = GenerationType.IDENTITY)   
    private int id;

    @Column(name = "nombre_promocion")
    private String nombrePromocion;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "descuento_porcentaje")
    private double descuentoPorcentaje;

    @Column(name = "monto_minimo")
    private double montoMinimo;

    @Column(name = "codigo_promo")
    private String codigoPromo;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "activa")
    private boolean activa;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombrePromocion() {
        return nombrePromocion;
    }

    public void setNombrePromocion(String nombrePromocion) {
        this.nombrePromocion = nombrePromocion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }

    public void setDescuentoPorcentaje(double descuentoPorcentaje) {
        this.descuentoPorcentaje = descuentoPorcentaje;
    }

    public double getMontoMinimo() {
        return montoMinimo;
    }

    public void setMontoMinimo(double montoMinimo) {
        this.montoMinimo = montoMinimo;
    }

    public String getCodigoPromo() {
        return codigoPromo;
    }

    public void setCodigoPromo(String codigoPromo) {
        this.codigoPromo = codigoPromo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

}