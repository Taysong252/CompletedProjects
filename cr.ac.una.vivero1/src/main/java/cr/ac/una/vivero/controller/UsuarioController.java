package cr.ac.una.vivero.controller;

import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

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
    public String lista(@RequestParam(required = false) String tipo,
            Model model, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        ArrayList<Object> usuarios;
        if (tipo != null && !tipo.isEmpty()) {
            usuarios = usuarioService.listarPorTipo(tipo);
        } else {
            usuarios = usuarioService.listarTodos();
        }

        int totalClientes = 0, totalAdmins = 0, totalActivos = 0;
        for (Object obj : usuarios) {
            Usuario u = (Usuario) obj;
            if ("Cliente".equals(u.getTipoUsuario())) {
                totalClientes++;
            }
            if ("Administrador".equals(u.getTipoUsuario())) {
                totalAdmins++;
            }
            if (u.isActivo()) {
                totalActivos++;
            }
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("tipoFiltro", tipo != null ? tipo : "");
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalAdmins", totalAdmins);
        model.addAttribute("totalActivos", totalActivos);
        return "usuarios/listUsuarios";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("modoEdicion", false);
        return "usuarios/formUsuarios";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        String error = usuarioService.crear(usuario);
        if (error != null) {
            flash.addFlashAttribute("error", error);
            return "redirect:/usuarios/nuevo";
        }
        flash.addFlashAttribute("exito", "Usuario registrado correctamente.");
        return "redirect:/usuarios";
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
        Usuario u = usuarioService.obtenerPorId(id);
        if (u == null) {
            flash.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", u);
        model.addAttribute("modoEdicion", true);
        return "usuarios/formUsuarios";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam int id,
            @ModelAttribute Usuario usuario,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        String error = usuarioService.actualizar(id, usuario);
        if (error != null) {
            flash.addFlashAttribute("error", error);
            return "redirect:/usuarios/editar?id=" + id;
        }
        flash.addFlashAttribute("exito", "Usuario actualizado correctamente.");
        return "redirect:/usuarios";
    }

    @GetMapping("/desactivar")
    public String desactivar(@RequestParam int id,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        String error = usuarioService.desactivar(id);
        if (error != null) {
            flash.addFlashAttribute("error", error);
        } else {
            flash.addFlashAttribute("exito", "Cuenta desactivada correctamente.");
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/activar")
    public String activar(@RequestParam int id,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        String error = usuarioService.activar(id);
        if (error != null) {
            flash.addFlashAttribute("error", error);
        } else {
            flash.addFlashAttribute("exito", "Cuenta activada correctamente.");
        }
        return "redirect:/usuarios";
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
        String error = usuarioService.eliminar(id);
        if (error != null) {
            flash.addFlashAttribute("error", error);
        } else {
            flash.addFlashAttribute("exito", "Usuario eliminado del sistema.");
        }
        return "redirect:/usuarios";
    }
}
