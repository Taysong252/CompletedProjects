package cr.ac.una.vivero.services;

import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repo;

    public ArrayList<Object> listarTodos() {
        return new ArrayList<>(repo.findAllByOrderByNombreAsc());
    }

    public ArrayList<Object> listarPorTipo(String tipo) {
        return new ArrayList<>(repo.findByTipoUsuarioOrderByNombreAsc(tipo));
    }

    public Usuario obtenerPorId(int id) {
        return repo.findById(id).orElse(null);
    }

    public String crear(Usuario u) {
        String error = validarCampos(u, true);
        if (error != null) {
            return error;
        }

        u.setEmail(u.getEmail().trim().toLowerCase());
        u.setNombre(u.getNombre().trim());
        u.setApellido(u.getApellido().trim());
        u.setFechaRegistro(LocalDate.now());
        u.setActivo(true);

        try {
            repo.save(u);
            return null;
        } catch (Exception e) {
            return "No se pudo registrar el usuario, intente de nuevo.";
        }
    }

    public String actualizar(int id, Usuario u) {
        String error = validarCampos(u, false);
        if (error != null) {
            return error;
        }

        String emailNorm = u.getEmail().trim().toLowerCase();
        Optional<Usuario> existente = repo.findByEmail(emailNorm);
        if (existente.isPresent() && existente.get().getId() != id) {
            return "El correo '" + u.getEmail() + "' ya está en uso por otro usuario";
        }

        Usuario guardado = repo.findById(id).orElse(null);
        if (guardado == null) {
            return "No se encontró el usuario con id " + id;
        }

        guardado.setNombre(u.getNombre().trim());
        guardado.setApellido(u.getApellido().trim());
        guardado.setEmail(emailNorm);
        guardado.setTelefono(u.getTelefono());
        guardado.setTipoUsuario(u.getTipoUsuario());
        guardado.setDireccion(u.getDireccion());
        guardado.setActivo(u.isActivo());

        try {
            repo.save(guardado);
            return null;
        } catch (Exception e) {
            return "No se pudo actualizar el usuario.";
        }
    }

    @Transactional
    public String desactivar(int id) {
        if (!repo.existsById(id)) {
            return "Usuario no encontrado";
        }
        repo.updateActivo(id, false);
        return null;
    }

    @Transactional
    public String activar(int id) {
        if (!repo.existsById(id)) {
            return "Usuario no encontrado";
        }
        repo.updateActivo(id, true);
        return null;
    }

    public String eliminar(int id) {
        if (!repo.existsById(id)) {
            return "Usuario no encontrado";
        }
        try {
            repo.deleteById(id);
            return null;
        } catch (Exception e) {
            return "No se pudo eliminar el usuario";
        }
    }

    public Usuario autenticar(String email, String contrasena) {
        if (email == null || contrasena == null) {
            return null;
        }
        return repo.findByEmailAndContrasenaAndActivoTrue(
                email.trim().toLowerCase(), contrasena
        ).orElse(null);
    }

    private String validarCampos(Usuario u, boolean esNuevo) {
        if (vacio(u.getNombre())) {
            return "El nombre es obligatorio";
        }
        if (vacio(u.getApellido())) {
            return "El apellido es obligatorio";
        }
        if (vacio(u.getEmail())) {
            return "El correo es obligatorio";
        }
        if (vacio(u.getTelefono())) {
            return "El teléfono es obligatorio";
        }
        if (vacio(u.getTipoUsuario())) {
            return "El tipo de usuario es obligatorio";
        }

        if (!u.getEmail().contains("@") || !u.getEmail().contains(".")) {
            return "El correo electrónico no tiene un formato válido";
        }

        if (!u.getTelefono().replaceAll("-", "").matches("\\d{8}")) {
            return "El teléfono debe contener 8 dígitos numéricos";
        }

        if (!u.getTipoUsuario().equals("Cliente") && !u.getTipoUsuario().equals("Administrador")) {
            return "El tipo de usuario debe ser Cliente o Administrador";
        }

        if (esNuevo) {
            if (repo.findByEmail(u.getEmail().trim().toLowerCase()).isPresent()) {
                return "El correo '" + u.getEmail() + "' ya está registrado en el sistema";
            }
            if (vacio(u.getContrasena())) {
                return "La contraseña es obligatoria";
            }
            if (u.getContrasena().length() < 8) {
                return "La contraseña debe tener al menos 8 caracteres";
            }
        }

        return null;
    }

    private boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }
}
