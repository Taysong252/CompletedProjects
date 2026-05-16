package cr.ac.una.vivero.services;

import cr.ac.una.vivero.domain.Promocion;
import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.domain.Venta;
import cr.ac.una.vivero.repository.PromocionRepository;
import cr.ac.una.vivero.repository.UsuarioRepository;
import cr.ac.una.vivero.repository.VentaRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PromocionRepository promocionRepo;

    public List<Venta> listarTodas() {
        return ventaRepo.findAllByOrderByFechaVentaDesc();
    }

    public List<Venta> filtrarPorEstado(String estado) {
        return ventaRepo.findByEstadoOrderByFechaVentaDesc(estado);
    }

    public List<Venta> filtrarPorMetodoPago(String metodoPago) {
        return ventaRepo.findByMetodoPagoOrderByFechaVentaDesc(metodoPago);
    }

    public List<Venta> buscar(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return listarTodas();
        }
        return ventaRepo.buscarPorCriterio(criterio.trim());
    }

    public Venta obtenerPorId(int id) {
        return ventaRepo.findById(id).orElse(null);
    }

    public long contarPorEstado(String estado) {
        return ventaRepo.contarPorEstado(estado);
    }

    public List<Usuario> obtenerClientes() {
        return usuarioRepo.findByTipoUsuarioOrderByNombreAsc("Cliente");
    }

    public List<Usuario> obtenerAdmins() {
        return usuarioRepo.findByTipoUsuarioOrderByNombreAsc("Administrador");
    }

    public Promocion resolverPromocion(String codigo, double subtotal) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }

        Promocion promo = promocionRepo.findByCodigoPromo(codigo.trim()).orElse(null);

        if (promo == null) {
            throw new IllegalArgumentException("El código de promoción no existe.");
        }

        if (!promo.isActiva()) {
            throw new IllegalArgumentException("La promoción no está activa.");
        }

        LocalDate hoy = LocalDate.now();

        if (hoy.isBefore(promo.getFechaInicio())) {
            throw new IllegalArgumentException("La promoción aún no ha comenzado.");
        }

        if (hoy.isAfter(promo.getFechaFin())) {
            throw new IllegalArgumentException("La promoción ya venció.");
        }

        if (subtotal < promo.getMontoMinimo()) {
            throw new IllegalArgumentException(
                    "El subtotal mínimo para esta promoción es ₡"
                    + String.format("%,.0f", promo.getMontoMinimo()) + "."
            );
        }

        return promo;
    }
    private void calcularTotales(Venta venta) {
        double subtotal = venta.getSubtotal();
        double impuesto = Math.round(subtotal * 0.13 * 100.0) / 100.0;
        double bruto = subtotal + impuesto;
        double descuento = venta.getDescuentoAplicado();
        double total = Math.round((bruto - descuento) * 100.0) / 100.0;
        if (total < 0) {
            total = 0;
        }

        venta.setImpuesto(impuesto);
        venta.setTotal(total);
    }

    public String crear(Venta venta, int clienteId, int adminId, String codigoPromo) {
        String error = validar(venta, clienteId, adminId);
        if (error != null) {
            return error;
        }

        Usuario cliente = usuarioRepo.findById(clienteId).orElse(null);
        Usuario admin = usuarioRepo.findById(adminId).orElse(null);

        if (cliente == null) {
            return "El cliente seleccionado no existe.";
        }
        if (admin == null) {
            return "El administrador seleccionado no existe.";
        }

        if (!"Cliente".equals(cliente.getTipoUsuario())) {
            return "El usuario seleccionado como cliente no es de tipo Cliente.";
        }

        Promocion promo;
        try {
            promo = resolverPromocion(codigoPromo, venta.getSubtotal());
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }

        if (promo != null) {
            double sub = venta.getSubtotal();
            double bruto = sub + Math.round(sub * 0.13 * 100.0) / 100.0;
            double descuento = Math.round(bruto * (promo.getDescuentoPorcentaje() / 100.0) * 100.0) / 100.0;
            venta.setPromocion(promo);
            venta.setDescuentoAplicado(descuento);
        } else {
            venta.setPromocion(null);
            venta.setDescuentoAplicado(0.0);
        }

        venta.setCliente(cliente);
        venta.setAdmin(admin);
        venta.setFechaVenta(LocalDate.now());
        calcularTotales(venta);

        try {
            venta.setCodigoVenta("TEMP-" + System.currentTimeMillis());

            Venta guardada = ventaRepo.save(venta);
            guardada.setCodigoVenta(Venta.generarCodigo(guardada.getId()));
            ventaRepo.save(guardada);

            return null;
        } catch (Exception e) {
            return "No se pudo registrar la venta: " + e.getMessage();
        }
    }

    public String actualizar(int id, Venta datos, int clienteId, int adminId, String codigoPromo) {
        String error = validar(datos, clienteId, adminId);
        if (error != null) {
            return error;
        }

        Venta guardada = ventaRepo.findById(id).orElse(null);
        if (guardada == null) {
            return "Venta no encontrada.";
        }

        Usuario cliente = usuarioRepo.findById(clienteId).orElse(null);
        Usuario admin = usuarioRepo.findById(adminId).orElse(null);

        if (cliente == null) {
            return "El cliente seleccionado no existe.";
        }
        if (admin == null) {
            return "El administrador seleccionado no existe.";
        }

        Promocion promo;
        try {
            promo = resolverPromocion(codigoPromo, datos.getSubtotal());
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }

        if (promo != null) {
            double sub = datos.getSubtotal();
            double bruto = sub + Math.round(sub * 0.13 * 100.0) / 100.0;
            double descuento = Math.round(bruto * (promo.getDescuentoPorcentaje() / 100.0) * 100.0) / 100.0;
            guardada.setPromocion(promo);
            guardada.setDescuentoAplicado(descuento);
        } else {
            guardada.setPromocion(null);
            guardada.setDescuentoAplicado(0.0);
        }

        guardada.setCliente(cliente);
        guardada.setAdmin(admin);
        guardada.setSubtotal(datos.getSubtotal());
        calcularTotales(guardada);
        guardada.setMetodoPago(datos.getMetodoPago());
        guardada.setEstado(datos.getEstado());
        guardada.setNotasAdicionales(datos.getNotasAdicionales());
        guardada.setFacturado(datos.isFacturado());

        try {
            ventaRepo.save(guardada);
            return null;
        } catch (Exception e) {
            return "No se pudo actualizar la venta.";
        }
    }
    public String eliminar(int id) {
        if (!ventaRepo.existsById(id)) {
            return "Venta no encontrada.";
        }
        try {
            ventaRepo.deleteById(id);
            return null;
        } catch (Exception e) {
            return "No se pudo eliminar la venta.";
        }
    }

    private String validar(Venta v, int clienteId, int adminId) {
        if (clienteId <= 0) {
            return "Debe seleccionar un cliente.";
        }
        if (adminId <= 0) {
            return "Debe seleccionar un administrador.";
        }
        if (v.getSubtotal() <= 0) {
            return "El subtotal debe ser mayor a 0.";
        }
        if (vacio(v.getMetodoPago())) {
            return "El método de pago es obligatorio.";
        }
        if (vacio(v.getEstado())) {
            return "El estado es obligatorio.";
        }

        if (!List.of("Efectivo", "Tarjeta", "Transferencia").contains(v.getMetodoPago())) {
            return "Método de pago inválido.";
        }
        if (!List.of("Pendiente", "Pagada", "Cancelada").contains(v.getEstado())) {
            return "Estado de venta inválido.";
        }

        return null;
    }

    private boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }
}