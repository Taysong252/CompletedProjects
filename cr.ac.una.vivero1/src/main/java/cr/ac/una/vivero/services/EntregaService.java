package cr.ac.una.vivero.services;

import cr.ac.una.vivero.domain.Entrega;
import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.domain.Venta;
import cr.ac.una.vivero.repository.EntregaRepository;
import cr.ac.una.vivero.repository.UsuarioRepository;
import cr.ac.una.vivero.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EntregaService {

    @Autowired
    private EntregaRepository repo;

    @Autowired
    private VentaRepository ventaRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    public ArrayList<Entrega> listarTodas() {
        return new ArrayList<>(repo.findAllByOrderByFechaProgramadaAsc());
    }

    public ArrayList<Entrega> listarPorEstado(String estado) {
        return new ArrayList<>(repo.findByEstadoEntregaOrderByFechaProgramadaAsc(estado));
    }

    public Entrega obtenerPorId(int id) {
        return repo.findById(id).orElse(null);
    }

    public List<Venta> obtenerVentas() {
        return ventaRepo.findAllByOrderByFechaVentaDesc();
    }

    public String crear(Entrega entrega, int ventaId, int clienteId, int administradorId) {

        Venta venta = ventaRepo.findById(ventaId).orElse(null);
        if (venta == null) {
            return "No existe ninguna venta con ese ID";
        }

        Usuario cliente = usuarioRepo.findById(clienteId).orElse(null);
        if (cliente == null) {
            return "Cliente no encontrado";
        }

        Usuario admin = usuarioRepo.findById(administradorId).orElse(null);
        if (admin == null) {
            return "Administrador no encontrado";
        }

        String error = validarCampos(entrega);
        if (error != null) {
            return error;
        }

        entrega.setVenta(venta);
        entrega.setCliente(cliente);
        entrega.setAdministrador(admin);
        entrega.setConfirmada(false);
        if (entrega.getCostoEnvio() == null) {
            entrega.setCostoEnvio(BigDecimal.ZERO);
        }

        try {
            repo.save(entrega);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "No se pudo registrar la entrega: " + e.getMessage();
        }
    }

    public String actualizar(int id, Entrega datos, int ventaId, int clienteId, int administradorId) {

        Entrega guardada = repo.findById(id).orElse(null);
        if (guardada == null) {
            return "Entrega no encontrada";
        }

        Venta venta = ventaRepo.findById(ventaId).orElse(null);
        if (venta == null) {
            return "No existe ninguna venta con ese ID";
        }

        Usuario cliente = usuarioRepo.findById(clienteId).orElse(null);
        if (cliente == null) {
            return "Cliente no encontrado";
        }

        Usuario admin = usuarioRepo.findById(administradorId).orElse(null);
        if (admin == null) {
            return "Administrador no encontrado";
        }

        String error = validarCampos(datos);
        if (error != null) {
            return error;
        }

        guardada.setVenta(venta);
        guardada.setCliente(cliente);
        guardada.setAdministrador(admin);
        guardada.setDireccionEntrega(datos.getDireccionEntrega().trim());
        guardada.setFechaProgramada(datos.getFechaProgramada());
        guardada.setFechaEntregada(datos.getFechaEntregada());
        guardada.setEstadoEntrega(datos.getEstadoEntrega());
        guardada.setMetodoEntrega(datos.getMetodoEntrega());
        guardada.setCostoEnvio(datos.getCostoEnvio() != null
                ? datos.getCostoEnvio() : BigDecimal.ZERO);
        guardada.setObservaciones(datos.getObservaciones());
        guardada.setConfirmada(datos.isConfirmada());

        try {
            repo.save(guardada);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "No se pudo actualizar la entrega: " + e.getMessage();
        }
    }

    @Transactional
    public String confirmar(int id) {
        Entrega e = repo.findById(id).orElse(null);
        if (e == null) {
            return "Entrega no encontrada";
        }
        e.setConfirmada(true);
        e.setEstadoEntrega("Entregado");
        e.setFechaEntregada(LocalDate.now());
        repo.save(e);
        return null;
    }

    @Transactional
    public String desconfirmar(int id) {
        Entrega e = repo.findById(id).orElse(null);
        if (e == null) {
            return "Entrega no encontrada";
        }
        e.setConfirmada(false);
        repo.save(e);
        return null;
    }

    public String eliminar(int id) {
        if (!repo.existsById(id)) {
            return "Entrega no encontrada";
        }
        try {
            repo.deleteById(id);
            return null;
        } catch (Exception e) {
            return "No se pudo eliminar la entrega";
        }
    }

    private String validarCampos(Entrega e) {
        if (vacio(e.getDireccionEntrega())) {
            return "La dirección de entrega es obligatoria";
        }
        if (e.getFechaProgramada() == null) {
            return "La fecha programada es obligatoria";
        }
        if (vacio(e.getEstadoEntrega())) {
            return "El estado de entrega es obligatorio";
        }
        if (vacio(e.getMetodoEntrega())) {
            return "El método de entrega es obligatorio";
        }

        List<String> estadosValidos = List.of("Pendiente", "En camino", "Entregado", "Cancelado");
        if (!estadosValidos.contains(e.getEstadoEntrega())) {
            return "Estado de entrega inválido";
        }

        List<String> metodosValidos = List.of("Domicilio", "Retiro en tienda");
        if (!metodosValidos.contains(e.getMetodoEntrega())) {
            return "Método de entrega inválido";
        }

        if (e.getCostoEnvio() != null && e.getCostoEnvio().compareTo(BigDecimal.ZERO) < 0) {
            return "El costo de envío no puede ser negativo";
        }

        return null;
    }

    private boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }
}
