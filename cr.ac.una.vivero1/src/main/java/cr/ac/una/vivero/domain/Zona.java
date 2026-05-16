package cr.ac.una.vivero.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "zonas")
public class Zona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombreZona")
    private String nombreZona;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "capacidadMaxima")
    private int capacidadMaxima;

    @Column(name = "cantidadActual")
    private int cantidadActual;

    @Column(name = "temperaturaPromedio")
    private double temperaturaPromedio;

    @Column(name = "humedadPromedio")
    private double humedadPromedio;

    @Column(name = "activa")
    private boolean activa;

    public Zona() {}

    public Zona(String nombreZona, String tipo, String descripcion,
                int capacidadMaxima, int cantidadActual,
                double temperaturaPromedio, double humedadPromedio,
                boolean activa) {
        this.nombreZona = nombreZona;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.capacidadMaxima = capacidadMaxima;
        this.cantidadActual = cantidadActual;
        this.temperaturaPromedio = temperaturaPromedio;
        this.humedadPromedio = humedadPromedio;
        this.activa = activa;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreZona() {
        return nombreZona;
    }

    public void setNombreZona(String nombreZona) {
        this.nombreZona = nombreZona;
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

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public int getCantidadActual() {
        return cantidadActual;
    }

    public void setCantidadActual(int cantidadActual) {
        this.cantidadActual = cantidadActual;
    }

    public double getTemperaturaPromedio() {
        return temperaturaPromedio;
    }

    public void setTemperaturaPromedio(double temperaturaPromedio) {
        this.temperaturaPromedio = temperaturaPromedio;
    }

    public double getHumedadPromedio() {
        return humedadPromedio;
    }

    public void setHumedadPromedio(double humedadPromedio) {
        this.humedadPromedio = humedadPromedio;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}