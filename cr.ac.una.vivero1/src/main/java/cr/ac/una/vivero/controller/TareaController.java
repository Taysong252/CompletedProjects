package cr.ac.una.vivero.controller;

import cr.ac.una.vivero.domain.Tarea;
import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.services.PlantaService;
import cr.ac.una.vivero.services.TareaService;
import cr.ac.una.vivero.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tareas")
public class TareaController {

    @Autowired
    private TareaService tareaService;

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

    private void cargarListas(Model model) {
        model.addAttribute("plantas", plantaService.obtenerTodas());
        model.addAttribute("empleados", usuarioService.listarPorTipo("Administrador"));
    }

    @GetMapping({"", "/"})
    public String listar(Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        model.addAttribute("title", "Gestión de Tareas");
        model.addAttribute("tareas", tareaService.getAll());
        return "tareas/listTareas";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        model.addAttribute("tarea", new Tarea());
        model.addAttribute("modoEdicion", false);
        cargarListas(model);
        return "tareas/formTareas";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Tarea tarea, Model model,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";

        Map<String, String> errores = tareaService.validar(tarea, false);
        if (!errores.isEmpty()) {
            model.addAllAttributes(errores);
            model.addAttribute("tarea", tarea);
            model.addAttribute("modoEdicion", false);
            cargarListas(model);
            return "tareas/formTareas";
        }

        String error = tareaService.crear(tarea);
        if (error != null) {
            model.addAttribute("errorGeneral", error);
            model.addAttribute("tarea", tarea);
            model.addAttribute("modoEdicion", false);
            cargarListas(model);
            return "tareas/formTareas";
        }

        flash.addFlashAttribute("exito", "Tarea registrada correctamente.");
        return "redirect:/tareas";
    }

    @GetMapping("/editar")
    public String editar(@RequestParam int id, Model model,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        Tarea t = tareaService.getById(id);
        if (t == null) {
            flash.addFlashAttribute("error", "Tarea no encontrada.");
            return "redirect:/tareas";
        }
        model.addAttribute("tarea", t);
        model.addAttribute("modoEdicion", true);
        cargarListas(model);
        return "tareas/formTareas";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam int id,
            @ModelAttribute Tarea tarea, Model model,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";

        Map<String, String> errores = tareaService.validar(tarea, true);
        if (!errores.isEmpty()) {
            model.addAllAttributes(errores);
            model.addAttribute("tarea", tarea);
            model.addAttribute("modoEdicion", true);
            cargarListas(model);
            return "tareas/formTareas";
        }

        String error = tareaService.actualizar(id, tarea);
        if (error != null) {
            model.addAttribute("errorGeneral", error);
            model.addAttribute("tarea", tarea);
            model.addAttribute("modoEdicion", true);
            cargarListas(model);
            return "tareas/formTareas";
        }

        flash.addFlashAttribute("exito", "Tarea actualizada correctamente.");
        return "redirect:/tareas";
    }

    @GetMapping("/eliminar")
    public String eliminar(@RequestParam int id,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        tareaService.delete(id);
        flash.addFlashAttribute("exito", "Tarea eliminada correctamente.");
        return "redirect:/tareas";
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam String criterio, Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        model.addAttribute("title", "Gestión de Tareas");
        model.addAttribute("tareas", tareaService.buscar(criterio));
        model.addAttribute("criterioBuscado", criterio);
        return "tareas/listTareas";
    }
}