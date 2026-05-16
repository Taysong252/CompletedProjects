package cr.ac.una.vivero.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false, length = 50)
    private String tipoUsuario;

    @Column(nullable = false, length = 255)
    private String contrasena;

    @Column(length = 255)
    private String direccion;

    @Column(nullable = false)
    private LocalDate fechaRegistro;

    @Column(nullable = true)
    private boolean activo;

    public Usuario() {
    }

    public Usuario(int id, String nombre, String apellido, String email,
            String telefono, String tipoUsuario, String contrasena,
            String direccion, LocalDate fechaRegistro, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.tipoUsuario = tipoUsuario;
        this.contrasena = contrasena;
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro;
        this.activo = activo;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public String getIniciales() {
        String i = "";
        if (nombre != null && !nombre.isEmpty()) {
            i += nombre.charAt(0);
        }
        if (apellido != null && !apellido.isEmpty()) {
            i += apellido.charAt(0);
        }
        return i.toUpperCase();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fecha) {
        this.fechaRegistro = fecha;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
