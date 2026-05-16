package cr.ac.una.vivero.controller;

import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.domain.Venta;
import cr.ac.una.vivero.services.VentaService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    private boolean sinSesion(HttpSession session) {
        return session.getAttribute("usuarioActivo") == null;
    }

    private boolean esCliente(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioActivo");
        return u != null && "Cliente".equals(u.getTipoUsuario());
    }

    // ── Listado ───────────────────────────────────────────────────────────
    @GetMapping({"", "/"})
    public String lista(Model model, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        List<Venta> ventas = ventaService.listarTodas();
        model.addAttribute("ventas", ventas);
        model.addAttribute("totalVentas", ventas.size());
        model.addAttribute("totalPagadas", ventaService.contarPorEstado("Pagada"));
        model.addAttribute("totalPendientes", ventaService.contarPorEstado("Pendiente"));
        return "ventas/listVentas";
    }

    @GetMapping("/filtrarEstado")
    @ResponseBody
    public List<Venta> filtrarEstado(@RequestParam String estado, HttpSession session) {
        if (sinSesion(session)) {
            return List.of();
        }
        return ventaService.filtrarPorEstado(estado);
    }

    @GetMapping("/filtrarMetodo")
    @ResponseBody
    public List<Venta> filtrarMetodo(@RequestParam String metodoPago, HttpSession session) {
        if (sinSesion(session)) {
            return List.of();
        }
        return ventaService.filtrarPorMetodoPago(metodoPago);
    }

    @GetMapping("/buscar")
    @ResponseBody
    public List<Venta> buscar(@RequestParam String criterio, HttpSession session) {
        if (sinSesion(session)) {
            return List.of();
        }
        return ventaService.buscar(criterio);
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        model.addAttribute("venta", new Venta());
        model.addAttribute("clientes", ventaService.obtenerClientes());
        model.addAttribute("admins", ventaService.obtenerAdmins());
        model.addAttribute("modoEdicion", false);
        return "ventas/formVentas";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Venta venta,
            @RequestParam int clienteId,
            @RequestParam int adminId,
            @RequestParam(required = false, defaultValue = "") String codigoPromo,
            RedirectAttributes flash,
            HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        String error = ventaService.crear(venta, clienteId, adminId, codigoPromo);
        if (error != null) {
            flash.addFlashAttribute("error", error);
            return "redirect:/ventas/nuevo";
        }
        flash.addFlashAttribute("exito", "Venta registrada correctamente.");
        return "redirect:/ventas/";
    }

    @GetMapping("/editar")
    public String editar(@RequestParam int id, Model model,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        Venta v = ventaService.obtenerPorId(id);
        if (v == null) {
            flash.addFlashAttribute("error", "Venta no encontrada.");
            return "redirect:/ventas/";
        }
        model.addAttribute("venta", v);
        model.addAttribute("clientes", ventaService.obtenerClientes());
        model.addAttribute("admins", ventaService.obtenerAdmins());
        model.addAttribute("modoEdicion", true);
        return "ventas/formVentas";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam int id,
            @ModelAttribute Venta venta,
            @RequestParam int clienteId,
            @RequestParam int adminId,
            @RequestParam(required = false, defaultValue = "") String codigoPromo,
            RedirectAttributes flash,
            HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        String error = ventaService.actualizar(id, venta, clienteId, adminId, codigoPromo);
        if (error != null) {
            flash.addFlashAttribute("error", error);
            return "redirect:/ventas/editar?id=" + id;
        }
        flash.addFlashAttribute("exito", "Venta actualizada correctamente.");
        return "redirect:/ventas/";
    }

    @GetMapping("/eliminar")
    public String eliminar(@RequestParam int id,
            RedirectAttributes flash,
            HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        String error = ventaService.eliminar(id);
        if (error != null) {
            flash.addFlashAttribute("error", error);
        } else {
            flash.addFlashAttribute("exito", "Venta eliminada correctamente.");
        }
        return "redirect:/ventas/";
    }
}