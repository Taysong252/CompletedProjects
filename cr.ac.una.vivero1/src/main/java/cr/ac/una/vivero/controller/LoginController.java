package cr.ac.una.vivero.controller;

import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String login(HttpSession session) {
        if (session.getAttribute("usuarioActivo") != null) {
            return "redirect:/inicio";
        }
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email,
            @RequestParam String contrasena,
            HttpSession session,
            RedirectAttributes flash) {

        Usuario u = usuarioService.autenticar(email, contrasena);
        if (u == null) {
            flash.addFlashAttribute("error", "Correo o contraseña incorrectos");
            return "redirect:/";
        }
        session.setAttribute("usuarioActivo", u);
        if ("Administrador".equals(u.getTipoUsuario())) {
            return "redirect:/inicio/admin";
        } else {
            return "redirect:/inicio/cliente";
        }
    }

    @GetMapping("/registro")
    public String mostrarRegistro(HttpSession session, Model model) {
        if (session.getAttribute("usuarioActivo") != null) {
            return "redirect:/inicio";
        }
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro/guardar")
    public String procesarRegistro(@ModelAttribute Usuario usuario,
            RedirectAttributes flash, Model model) {  
        usuario.setTipoUsuario("Cliente");
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDate.now());

        String error = usuarioService.crear(usuario);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("usuario", usuario);   
            return "registro";                        
        }
        flash.addFlashAttribute("exito", "Cuenta creada correctamente. Ya podes iniciar sesion");
        return "redirect:/";
    }

    @GetMapping("/inicio/admin")
    public String inicioAdmin(HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuarioActivo");
        if (u == null) {
            return "redirect:/";
        }
        if (!"Administrador".equals(u.getTipoUsuario())) {
            return "redirect:/inicio/cliente";
        }
        model.addAttribute("usuario", u);
        return "indexAdmin";
    }

    @GetMapping("/inicio/cliente")
    public String inicioCliente(HttpSession session, Model model) {
        Usuario u = (Usuario) session.getAttribute("usuarioActivo");
        if (u == null) {
            return "redirect:/";
        }
        if (!"Cliente".equals(u.getTipoUsuario())) {
            return "redirect:/inicio/admin";
        }
        model.addAttribute("usuario", u);
        return "indexCliente";
    }

    @GetMapping("/inicio")
    public String inicio(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioActivo");
        if (u == null) {
            return "redirect:/";
        }
        if ("Administrador".equals(u.getTipoUsuario())) {
            return "redirect:/inicio/admin";
        }
        return "redirect:/inicio/cliente";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes flash) {
        session.invalidate();
        flash.addFlashAttribute("exito", "Sesion cerrada correctamente");
        return "redirect:/";
    }
}
