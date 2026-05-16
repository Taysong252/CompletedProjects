package cr.ac.una.vivero.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "plantas")
public class Planta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String nombreComun;

    @Column(nullable = false, length = 100)
    private String nombreCientifico;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false)
    private boolean requiereSol;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(length = 500)
    private String descripcionCuidados;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] datosImagen;

    @Column(length = 100)
    private String tipoImagen;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaIngreso;

    @Column(nullable = false)
    private boolean activo;

    // ── Constructor vacío ─────────────────────────────────────
    public Planta() {}

    // ── Constructor completo ─────────────────────────────────
    public Planta(int id, String nombreComun, String nombreCientifico,
                  Double precio, String tipo, boolean requiereSol,
                  Integer cantidad, String descripcionCuidados,
                  byte[] datosImagen, String tipoImagen,
                  LocalDate fechaIngreso, boolean activo) {
        this.id = id;
        this.nombreComun = nombreComun;
        this.nombreCientifico = nombreCientifico;
        this.precio = precio;
        this.tipo = tipo;
        this.requiereSol = requiereSol;
        this.cantidad = cantidad;
        this.descripcionCuidados = descripcionCuidados;
        this.datosImagen = datosImagen;
        this.tipoImagen = tipoImagen;
        this.fechaIngreso = fechaIngreso;
        this.activo = activo;
    }

    // ── Getters y Setters ────────────────────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombreComun() { return nombreComun; }
    public void setNombreComun(String nombreComun) { this.nombreComun = nombreComun; }

    public String getNombreCientifico() { return nombreCientifico; }
    public void setNombreCientifico(String nombreCientifico) { this.nombreCientifico = nombreCientifico; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public boolean isRequiereSol() { return requiereSol; }
    public void setRequiereSol(boolean requiereSol) { this.requiereSol = requiereSol; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getDescripcionCuidados() { return descripcionCuidados; }
    public void setDescripcionCuidados(String descripcionCuidados) {
        this.descripcionCuidados = descripcionCuidados;
    }

    public byte[] getDatosImagen() { return datosImagen; }
    public void setDatosImagen(byte[] datosImagen) { this.datosImagen = datosImagen; }

    public String getTipoImagen() { return tipoImagen; }
    public void setTipoImagen(String tipoImagen) { this.tipoImagen = tipoImagen; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}