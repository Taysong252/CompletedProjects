package cr.ac.una.vivero.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "quejas")
public class Queja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "codigoQueja", nullable = true, unique = true, length = 20)
    private String codigoQueja;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_id")
    private Usuario admin;

    @Column(nullable = false, length = 20)
    private String tipoRegistro;

    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(length = 500)
    private String respuesta;

    @Column(length = 30)
    private String satisfaccion;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaRegistro;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaResolucion;

    public Queja() {
        this.fechaRegistro = LocalDate.now();
        this.estado = "Pendiente";
        this.tipoRegistro = "Queja";
    }

    public static String generarCodigo(int id) {
        return "QJA-" + id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoQueja() {
        return codigoQueja;
    }

    public void setCodigoQueja(String codigoQueja) {
        this.codigoQueja = codigoQueja;
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

    public String getClienteNombre() {
        return cliente != null ? cliente.getNombre() + " " + cliente.getApellido() : "";
    }

    public String getAdminNombre() {
        return admin != null ? admin.getNombre() + " " + admin.getApellido() : "";
    }

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(String tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getSatisfaccion() {
        return satisfaccion;
    }

    public void setSatisfaccion(String satisfaccion) {
        this.satisfaccion = satisfaccion;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDate getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDate fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }
}
