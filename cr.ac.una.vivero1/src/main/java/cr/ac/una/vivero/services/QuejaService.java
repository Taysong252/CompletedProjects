package cr.ac.una.vivero.services;

import cr.ac.una.vivero.domain.Queja;
import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.repository.QuejaRepository;
import cr.ac.una.vivero.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuejaService {

    @Autowired
    private QuejaRepository quejaRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    public List<Queja> listarTodas() {
        return quejaRepo.findAllByOrderByFechaRegistroDesc();
    }

    public List<Queja> filtrarPorEstado(String estado) {
        return quejaRepo.findByEstadoOrderByFechaRegistroDesc(estado);
    }

    public List<Queja> filtrarPorTipo(String tipo) {
        return quejaRepo.findByTipoRegistroOrderByFechaRegistroDesc(tipo);
    }

    public List<Queja> buscar(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return listarTodas();
        }
        return quejaRepo.buscarPorCriterio(criterio.trim());
    }

    public Queja obtenerPorId(int id) {
        return quejaRepo.findById(id).orElse(null);
    }

    public long contarPorEstado(String estado) {
        return quejaRepo.contarPorEstado(estado);
    }

    public List<Usuario> obtenerClientes() {
        return usuarioRepo.findByTipoUsuarioOrderByNombreAsc("Cliente");
    }

    public List<Usuario> obtenerAdmins() {
        return usuarioRepo.findByTipoUsuarioOrderByNombreAsc("Administrador");
    }

    public String crear(Queja queja, int clienteId, int adminId) {
        String error = validar(queja, clienteId);
        if (error != null) {
            return error;
        }

        Usuario cliente = usuarioRepo.findById(clienteId).orElse(null);
        if (cliente == null) {
            return "El cliente seleccionado no existe";
        }
        if (!"Cliente".equals(cliente.getTipoUsuario())) {
            return "El usuario seleccionado no es de tipo Cliente";
        }

        queja.setCliente(cliente);

        if (adminId > 0) {
            Usuario admin = usuarioRepo.findById(adminId).orElse(null);
            if (admin != null) {
                queja.setAdmin(admin);
            }
        }

        queja.setFechaRegistro(LocalDate.now());
        queja.setEstado("Pendiente");

        try {
            Queja guardada = quejaRepo.save(queja);
            guardada.setCodigoQueja(Queja.generarCodigo(guardada.getId()));
            quejaRepo.save(guardada);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "No se pudo registrar la queja. Intente de nuevo.";
        }
    }

    public String actualizar(int id, Queja datos, int clienteId, int adminId) {
        String error = validar(datos, clienteId);
        if (error != null) {
            return error;
        }

        Queja guardada = quejaRepo.findById(id).orElse(null);
        if (guardada == null) {
            return "Queja no encontrada";
        }

        Usuario cliente = usuarioRepo.findById(clienteId).orElse(null);
        if (cliente == null) {
            return "El cliente seleccionado no existe";
        }

        guardada.setCliente(cliente);

        if (adminId > 0) {
            Usuario admin = usuarioRepo.findById(adminId).orElse(null);
            if (admin != null) {
                guardada.setAdmin(admin);
            }
        } else {
            guardada.setAdmin(null);
        }

        guardada.setTipoRegistro(datos.getTipoRegistro());
        guardada.setCategoria(datos.getCategoria());
        guardada.setDescripcion(datos.getDescripcion());
        guardada.setEstado(datos.getEstado());
        guardada.setRespuesta(datos.getRespuesta());
        guardada.setSatisfaccion(datos.getSatisfaccion());

        if ("Resuelta".equals(datos.getEstado()) && guardada.getFechaResolucion() == null) {
            guardada.setFechaResolucion(LocalDate.now());
        }

        try {
            quejaRepo.save(guardada);
            return null;
        } catch (Exception e) {
            return "No se pudo actualizar la queja.";
        }
    }

    public String eliminar(int id) {
        if (!quejaRepo.existsById(id)) {
            return "Queja no encontrada";
        }
        try {
            quejaRepo.deleteById(id);
            return null;
        } catch (Exception e) {
            return "No se pudo eliminar la queja.";
        }
    }

    private String validar(Queja q, int clienteId) {
        if (clienteId <= 0) {
            return "Debe seleccionar un cliente";
        }
        if (vacio(q.getTipoRegistro())) {
            return "El tipo de registro es obligatorio";
        }
        if (vacio(q.getCategoria())) {
            return "La categoría es obligatoria";
        }
        if (vacio(q.getDescripcion())) {
            return "La descripción es obligatoria";
        }
        if (q.getDescripcion() != null && q.getDescripcion().trim().length() < 10) {
            return "La descripción debe tener al menos 10 caracteres";
        }

        List<String> tipos = List.of("Queja", "Sugerencia");
        if (!tipos.contains(q.getTipoRegistro())) {
            return "Tipo de registro inválido";
        }

        List<String> categorias = List.of("Atención al Cliente", "Productos", "Precios", "Personal", "Otro");
        if (!categorias.contains(q.getCategoria())) {
            return "Categoría inválida";
        }

        List<String> estados = List.of("Pendiente", "En proceso", "Resuelta");
        if (!vacio(q.getEstado()) && !estados.contains(q.getEstado())) {
            return "Estado inválido";
        }

        return null;
    }

    private boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }
}
