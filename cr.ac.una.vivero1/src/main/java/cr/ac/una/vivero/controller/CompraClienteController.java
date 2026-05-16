package cr.ac.una.vivero.controller;

import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.domain.Venta;
import cr.ac.una.vivero.services.PlantaService;
import cr.ac.una.vivero.services.VentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CompraClienteController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private PlantaService plantaService;

    @PostMapping("/ventas/compraCliente")
    public String compraCliente(
            @RequestParam int plantaId,
            @RequestParam int cantidad,
            @RequestParam double subtotal,
            @RequestParam int adminId,
            @RequestParam String metodoPago,
            @RequestParam(required = false, defaultValue = "") String codigoPromo,
            HttpSession session,
            RedirectAttributes flash) {

        // Seguridad: solo clientes con sesión activa
        if (session.getAttribute("usuarioActivo") == null) {
            return "redirect:/";
        }
        Usuario cliente = (Usuario) session.getAttribute("usuarioActivo");
        if (!"Cliente".equals(cliente.getTipoUsuario())) {
            return "redirect:/inicio/admin";
        }

        // Validaciones básicas
        if (cantidad < 1) {
            flash.addFlashAttribute("error", "La cantidad debe ser al menos 1.");
            return "redirect:/plantas/catalogo-vista";
        }
        if (adminId <= 0) {
            flash.addFlashAttribute("error", "Debe seleccionar un administrador.");
            return "redirect:/plantas/catalogo-vista";
        }
        if (subtotal <= 0) {
            flash.addFlashAttribute("error", "El subtotal no es válido.");
            return "redirect:/plantas/catalogo-vista";
        }

        // Verificar stock
        var planta = plantaService.obtenerPorId(plantaId);
        if (planta == null) {
            flash.addFlashAttribute("error", "La planta seleccionada no existe.");
            return "redirect:/plantas/catalogo-vista";
        }
        if (planta.getCantidad() < cantidad) {
            flash.addFlashAttribute("error",
                    "Stock insuficiente. Solo quedan " + planta.getCantidad() + " unidades.");
            return "redirect:/plantas/catalogo-vista";
        }

        // Construir venta
        Venta venta = new Venta();
        venta.setSubtotal(subtotal);
        venta.setMetodoPago(metodoPago);
        venta.setEstado("Pendiente");
        venta.setFacturado(false);
        venta.setNotasAdicionales("Compra desde catálogo: " + planta.getNombreComun()
                + " x" + cantidad + " unidades.");

        // Crear la venta usando el servicio existente
        String error = ventaService.crear(venta, cliente.getId(), adminId, codigoPromo);
        if (error != null) {
            flash.addFlashAttribute("error", error);
            return "redirect:/plantas/catalogo-vista";
        }

        // Descontar stock de la planta
        planta.setCantidad(planta.getCantidad() - cantidad);
        plantaService.actualizar(plantaId, planta, null);

        flash.addFlashAttribute("exito",
                "¡Compra realizada con éxito! Tu venta fue registrada como Pendiente.");
        return "redirect:/plantas/catalogo-vista";
    }
}
