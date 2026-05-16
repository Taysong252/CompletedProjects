package cr.ac.una.vivero.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "empaques")
public class Empaque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "cantidad_planta")
    private int cantidadPlanta;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "unidad_medida")
    private String unidadMedida;

    @Column(name = "stock")
    private int stock;

    @Column(name = "costo_unitario")
    private double costoUnitario;

    @Column(name = "activo")
    private boolean activo;


    public int getId() {
        return id;
    }
    public void setId(int id) { 
        this.id = id; 
    }

    public int getCantidadPlanta() {
        return cantidadPlanta; 
    }
    public void setCantidadPlanta(int cantidadPlanta) {
        this.cantidadPlanta = cantidadPlanta; 
    }

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida; 
    }

    public int getStock() {
        return stock; 
    }
    public void setStock(int stock) {
        this.stock = stock; 
    }

    public double getCostoUnitario() {
        return costoUnitario;
    }
    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario; 
    }

    public boolean isActivo() {
        return activo; 
    }
    public void setActivo(boolean activo) {
        this.activo = activo; 
    }
}