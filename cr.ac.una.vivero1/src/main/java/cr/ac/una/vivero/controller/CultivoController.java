package cr.ac.una.vivero.controller;

import cr.ac.una.vivero.domain.Cultivo;
import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.services.CultivoService;
import cr.ac.una.vivero.services.PlantaService;
import cr.ac.una.vivero.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cultivos")
public class CultivoController {

    @Autowired
    private CultivoService cultivoService;

    @Autowired
    private PlantaService plantaService;

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
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";

        ArrayList<Cultivo> cultivos = estado != null && !estado.isEmpty()
                ? cultivoService.listarPorEstado(estado)
                : cultivoService.listarTodos();

        long activos     = cultivos.stream().filter(c -> !c.isCompletada()).count();
        long completados = cultivos.stream().filter(Cultivo::isCompletada).count();

        model.addAttribute("cultivos", cultivos);
        model.addAttribute("estadoFiltro", estado != null ? estado : "");
        model.addAttribute("totalActivos", activos);
        model.addAttribute("totalCompletados", completados);
        return "cultivos/listCultivos";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        model.addAttribute("cultivo", new Cultivo());
        model.addAttribute("modoEdicion", false);
        cargarListas(model);
        return "cultivos/formCultivos";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cultivo cultivo,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        String error = cultivoService.crear(cultivo);
        if (error != null) {
            flash.addFlashAttribute("error", error);
            return "redirect:/cultivos/nuevo";
        }
        flash.addFlashAttribute("exito", "Cultivo registrado correctamente.");
        return "redirect:/cultivos";
    }

    @GetMapping("/editar")
    public String editar(@RequestParam int id, Model model,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        Cultivo c = cultivoService.obtenerPorId(id);
        if (c == null) {
            flash.addFlashAttribute("error", "Cultivo no encontrado.");
            return "redirect:/cultivos";
        }
        model.addAttribute("cultivo", c);
        model.addAttribute("modoEdicion", true);
        cargarListas(model);
        return "cultivos/formCultivos";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam int id,
            @ModelAttribute Cultivo cultivo,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        String error = cultivoService.actualizar(id, cultivo);
        if (error != null) {
            flash.addFlashAttribute("error", error);
            return "redirect:/cultivos/editar?id=" + id;
        }
        flash.addFlashAttribute("exito", "Cultivo actualizado correctamente.");
        return "redirect:/cultivos";
    }

    @GetMapping("/completar")
    public String completar(@RequestParam int id,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        String error = cultivoService.completar(id);
        if (error != null) {
            flash.addFlashAttribute("error", error);
        } else {
            flash.addFlashAttribute("exito", "Cultivo marcado como completado.");
        }
        return "redirect:/cultivos";
    }

    @GetMapping("/eliminar")
    public String eliminar(@RequestParam int id,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        String error = cultivoService.eliminar(id);
        if (error != null) {
            flash.addFlashAttribute("error", error);
        } else {
            flash.addFlashAttribute("exito", "Cultivo eliminado correctamente.");
        }
        return "redirect:/cultivos";
    }

    private void cargarListas(Model model) {
        model.addAttribute("plantas", plantaService.obtenerTodas());
        model.addAttribute("admins", usuarioService.listarPorTipo("Administrador"));
    }
}