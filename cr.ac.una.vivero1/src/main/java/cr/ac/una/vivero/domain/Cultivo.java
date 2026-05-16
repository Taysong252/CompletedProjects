package cr.ac.una.vivero.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "cultivos")
public class Cultivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planta_id", nullable = false)
    private Planta planta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Usuario admin;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaSiembra;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaCosecha;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaTrasplante;

    @Column(length = 100)
    private String tecnica;

    @Column(length = 100)
    private String sustrato;

    @Column(nullable = false)
    private double tasaExito;

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false)
    private boolean completada;

    // ── Constructor vacío ──────────────────────────────────────────────────────
    public Cultivo() {}

    // ── Constructor completo ───────────────────────────────────────────────────
    public Cultivo(int id, Planta planta, Usuario admin,
                   LocalDate fechaSiembra, LocalDate fechaCosecha,
                   LocalDate fechaTrasplante, String tecnica, String sustrato,
                   double tasaExito, String observaciones, boolean completada) {
        this.id = id;
        this.planta = planta;
        this.admin = admin;
        this.fechaSiembra = fechaSiembra;
        this.fechaCosecha = fechaCosecha;
        this.fechaTrasplante = fechaTrasplante;
        this.tecnica = tecnica;
        this.sustrato = sustrato;
        this.tasaExito = tasaExito;
        this.observaciones = observaciones;
        this.completada = completada;
    }

    // ── Getters y Setters ──────────────────────────────────────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Planta getPlanta() { return planta; }
    public void setPlanta(Planta planta) { this.planta = planta; }

    public Usuario getAdmin() { return admin; }
    public void setAdmin(Usuario admin) { this.admin = admin; }

    public LocalDate getFechaSiembra() { return fechaSiembra; }
    public void setFechaSiembra(LocalDate fechaSiembra) { this.fechaSiembra = fechaSiembra; }

    public LocalDate getFechaCosecha() { return fechaCosecha; }
    public void setFechaCosecha(LocalDate fechaCosecha) { this.fechaCosecha = fechaCosecha; }

    public LocalDate getFechaTrasplante() { return fechaTrasplante; }
    public void setFechaTrasplante(LocalDate fechaTrasplante) { this.fechaTrasplante = fechaTrasplante; }

    public String getTecnica() { return tecnica; }
    public void setTecnica(String tecnica) { this.tecnica = tecnica; }

    public String getSustrato() { return sustrato; }
    public void setSustrato(String sustrato) { this.sustrato = sustrato; }

    public double getTasaExito() { return tasaExito; }
    public void setTasaExito(double tasaExito) { this.tasaExito = tasaExito; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }
}