package cr.ac.una.vivero.controller;

import cr.ac.una.vivero.domain.Entrega;
import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.services.EntregaService;
import cr.ac.una.vivero.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/entregas")
public class EntregaController {

    @Autowired
    private EntregaService entregaService;

    @Autowired
    private UsuarioService usuarioService;

    private boolean sinSesion(HttpSession session) {
        return session.getAttribute("usuarioActivo") == null;
    }

    private boolean esCliente(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioActivo");
        return u != null && "Cliente".equals(u.getTipoUsuario());
    }

    @GetMapping({"", "/"})
    public String lista(@RequestParam(required = false) String estado,
            Model model, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        ArrayList<Entrega> entregas = (estado != null && !estado.isEmpty())
                ? entregaService.listarPorEstado(estado)
                : entregaService.listarTodas();

        int totalPendientes = 0, totalEnCamino = 0,
                totalEntregadas = 0, totalCanceladas = 0;
        for (Entrega e : entregas) {
            switch (e.getEstadoEntrega()) {
                case "Pendiente" ->
                    totalPendientes++;
                case "En camino" ->
                    totalEnCamino++;
                case "Entregado" ->
                    totalEntregadas++;
                case "Cancelado" ->
                    totalCanceladas++;
            }
        }

        model.addAttribute("entregas", entregas);
        model.addAttribute("estadoFiltro", estado != null ? estado : "");
        model.addAttribute("totalPendientes", totalPendientes);
        model.addAttribute("totalEnCamino", totalEnCamino);
        model.addAttribute("totalEntregadas", totalEntregadas);
        model.addAttribute("totalCanceladas", totalCanceladas);
        return "entregas/listEntregas";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        model.addAttribute("entrega", new Entrega());
        model.addAttribute("modoEdicion", false);
        cargarListas(model);
        return "entregas/formEntregas";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Entrega entrega,
            @RequestParam int ventaId,
            @RequestParam int clienteId,
            @RequestParam int administradorId,
            Model model,
            RedirectAttributes flash,
            HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        String error = entregaService.crear(entrega, ventaId, clienteId, administradorId);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("entrega", entrega);
            model.addAttribute("modoEdicion", false);
            cargarListas(model);
            return "entregas/formEntregas";
        }
        flash.addFlashAttribute("exito", "Entrega registrada correctamente.");
        return "redirect:/entregas";
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

        Entrega e = entregaService.obtenerPorId(id);
        if (e == null) {
            flash.addFlashAttribute("error", "Entrega no encontrada.");
            return "redirect:/entregas";
        }
        model.addAttribute("entrega", e);
        model.addAttribute("modoEdicion", true);
        cargarListas(model);
        return "entregas/formEntregas";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam int id,
            @ModelAttribute Entrega entrega,
            @RequestParam int ventaId,
            @RequestParam int clienteId,
            @RequestParam int administradorId,
            Model model,
            RedirectAttributes flash,
            HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        String error = entregaService.actualizar(id, entrega, ventaId, clienteId, administradorId);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("entrega", entrega);
            model.addAttribute("modoEdicion", true);
            cargarListas(model);
            return "entregas/formEntregas";
        }
        flash.addFlashAttribute("exito", "Entrega actualizada correctamente.");
        return "redirect:/entregas";
    }

    @GetMapping("/confirmar")
    public String confirmar(@RequestParam int id,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        String error = entregaService.confirmar(id);
        if (error != null) {
            flash.addFlashAttribute("error", error);
        } else {
            flash.addFlashAttribute("exito", "Entrega confirmada correctamente.");
        }
        return "redirect:/entregas";
    }

    @GetMapping("/desconfirmar")
    public String desconfirmar(@RequestParam int id,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        String error = entregaService.desconfirmar(id);
        if (error != null) {
            flash.addFlashAttribute("error", error);
        } else {
            flash.addFlashAttribute("exito", "Entrega marcada como no confirmada.");
        }
        return "redirect:/entregas";
    }

    @GetMapping("/eliminar")
    public String eliminar(@RequestParam int id,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        String error = entregaService.eliminar(id);
        if (error != null) {
            flash.addFlashAttribute("error", error);
        } else {
            flash.addFlashAttribute("exito", "Entrega eliminada del sistema.");
        }
        return "redirect:/entregas";
    }

    private void cargarListas(Model model) {
        model.addAttribute("clientes",
                usuarioService.listarPorTipo("Cliente"));
        model.addAttribute("admins",
                usuarioService.listarPorTipo("Administrador"));
        model.addAttribute("ventas",
                entregaService.obtenerVentas());
    }
}
