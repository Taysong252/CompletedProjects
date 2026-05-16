package cr.ac.una.vivero.controller;

import cr.ac.una.vivero.domain.Queja;
import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.services.QuejaService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/quejas")
public class QuejaController {

    @Autowired
    private QuejaService quejaService;

    private boolean sinSesion(HttpSession session) {
        return session.getAttribute("usuarioActivo") == null;
    }

    private boolean esCliente(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioActivo");
        return u != null && "Cliente".equals(u.getTipoUsuario());
    }

    private Usuario usuarioActivo(HttpSession session) {
        return (Usuario) session.getAttribute("usuarioActivo");
    }

    // ── Listado principal (solo admin) ─────────────────────────────────────
    @GetMapping({"", "/"})
    public String lista(Model model, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        List<Queja> quejas = quejaService.listarTodas();
        model.addAttribute("quejas", quejas);
        model.addAttribute("totalQuejas", quejas.size());
        model.addAttribute("totalPendientes", quejaService.contarPorEstado("Pendiente"));
        model.addAttribute("totalResueltas", quejaService.contarPorEstado("Resuelta"));
        return "quejas/listQuejas";
    }

    // ── Filtro 1: por estado (AJAX) ────────────────────────────────────────
    @GetMapping("/filtrarEstado")
    @ResponseBody
    public List<Queja> filtrarEstado(@RequestParam String estado, HttpSession session) {
        if (sinSesion(session)) {
            return List.of();
        }
        return quejaService.filtrarPorEstado(estado);
    }

    // ── Filtro 2: por tipo (AJAX) ──────────────────────────────────────────
    @GetMapping("/filtrarTipo")
    @ResponseBody
    public List<Queja> filtrarTipo(@RequestParam String tipo, HttpSession session) {
        if (sinSesion(session)) {
            return List.of();
        }
        return quejaService.filtrarPorTipo(tipo);
    }

    // ── Búsqueda en vivo (AJAX) ────────────────────────────────────────────
    @GetMapping("/buscar")
    @ResponseBody
    public List<Queja> buscar(@RequestParam String criterio, HttpSession session) {
        if (sinSesion(session)) {
            return List.of();
        }
        return quejaService.buscar(criterio);
    }

    // ── Nuevo (solo admin) ─────────────────────────────────────────────────
    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        model.addAttribute("queja", new Queja());
        model.addAttribute("clientes", quejaService.obtenerClientes());
        model.addAttribute("admins", quejaService.obtenerAdmins());
        model.addAttribute("modoEdicion", false);
        model.addAttribute("esCliente", false);
        return "quejas/formQuejas";
    }

    // ── Formulario cliente ─────────────────────────────────────────────────
    @GetMapping("/miQueja")
    public String miQueja(Model model, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (!esCliente(session)) {
            return "redirect:/quejas/";
        }

        model.addAttribute("queja", new Queja());
        model.addAttribute("modoEdicion", false);
        model.addAttribute("esCliente", true);
        return "quejas/formQuejas";
    }

    // ── Guardar queja desde admin ──────────────────────────────────────────
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Queja queja,
            @RequestParam int clienteId,
            @RequestParam(defaultValue = "0") int adminId,
            RedirectAttributes flash,
            HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        String error = quejaService.crear(queja, clienteId, adminId);
        if (error != null) {
            flash.addFlashAttribute("error", error);
            return "redirect:/quejas/nuevo";
        }
        flash.addFlashAttribute("exito", "Queja registrada correctamente.");
        return "redirect:/quejas/";
    }

    // ── Guardar queja desde cliente ────────────────────────────────────────
    @PostMapping("/guardarCliente")
    public String guardarCliente(@ModelAttribute Queja queja,
            RedirectAttributes flash,
            HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (!esCliente(session)) {
            return "redirect:/quejas/";
        }

        int clienteId = usuarioActivo(session).getId();
        String error = quejaService.crear(queja, clienteId, 0);
        if (error != null) {
            flash.addFlashAttribute("error", error);
            return "redirect:/quejas/miQueja";
        }
        flash.addFlashAttribute("exito", "Tu queja o sugerencia fue registrada correctamente.");
        return "redirect:/inicio/cliente";
    }

    // ── Editar (solo admin) ────────────────────────────────────────────────
    @GetMapping("/editar")
    public String editar(@RequestParam int id, Model model,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        Queja q = quejaService.obtenerPorId(id);
        if (q == null) {
            flash.addFlashAttribute("error", "Queja no encontrada.");
            return "redirect:/quejas/";
        }
        model.addAttribute("queja", q);
        model.addAttribute("clientes", quejaService.obtenerClientes());
        model.addAttribute("admins", quejaService.obtenerAdmins());
        model.addAttribute("modoEdicion", true);
        model.addAttribute("esCliente", false);
        return "quejas/formQuejas";
    }

    // ── Actualizar (solo admin) ────────────────────────────────────────────
    @PostMapping("/actualizar")
    public String actualizar(@RequestParam int id,
            @ModelAttribute Queja queja,
            @RequestParam int clienteId,
            @RequestParam(defaultValue = "0") int adminId,
            RedirectAttributes flash,
            HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        String error = quejaService.actualizar(id, queja, clienteId, adminId);
        if (error != null) {
            flash.addFlashAttribute("error", error);
            return "redirect:/quejas/editar?id=" + id;
        }
        flash.addFlashAttribute("exito", "Queja actualizada correctamente.");
        return "redirect:/quejas/";
    }

    // ── Eliminar (solo admin) ──────────────────────────────────────────────
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

        String error = quejaService.eliminar(id);
        if (error != null) {
            flash.addFlashAttribute("error", error);
        } else {
            flash.addFlashAttribute("exito", "Queja eliminada correctamente.");
        }
        return "redirect:/quejas/";
    }
}