package cr.ac.una.vivero.controller;

import cr.ac.una.vivero.domain.Zona;
import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.services.ZonaService;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/zonas")
public class ZonaController {

    @Autowired
    private ZonaService zonaServices;

    private boolean sinSesion(HttpSession session) {
        return session.getAttribute("usuarioActivo") == null;
    }

    private boolean esCliente(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioActivo");
        return u != null && "Cliente".equals(u.getTipoUsuario());
    }

    @GetMapping({"", "/"})
    public String index(Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        model.addAttribute("title", "Gestión de Zonas");
        model.addAttribute("zonas", zonaServices.getAll());
        return "zonas/listZonas";
    }

    @GetMapping("/add")
    public String add(Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        model.addAttribute("zona", new Zona());
        return "zonas/formZonas";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Zona zona, Model model,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";

        Map<String, String> errores = zonaServices.validarConDetalle(zona);
        if (!errores.isEmpty()) {
            model.addAllAttributes(errores);
            model.addAttribute("zona", zona);
            return "zonas/formZonas";
        }

        zonaServices.save(zona);
        flash.addFlashAttribute("exito", "Zona registrada correctamente.");
        return "redirect:/zonas/";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam int id, Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        model.addAttribute("zona", zonaServices.getById(id));
        return "zonas/formZonas";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Zona zona, Model model,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";

        Map<String, String> errores = zonaServices.validarConDetalle(zona);
        if (!errores.isEmpty()) {
            model.addAllAttributes(errores);
            model.addAttribute("zona", zona);
            return "zonas/formZonas";
        }

        zonaServices.save(zona);
        flash.addFlashAttribute("exito", "Zona actualizada correctamente.");
        return "redirect:/zonas/";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam int id, RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        zonaServices.delete(id);
        flash.addFlashAttribute("exito", "Zona eliminada correctamente.");
        return "redirect:/zonas/";
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam String criterio, Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        model.addAttribute("title", "Gestión de Zonas");
        model.addAttribute("zonas", zonaServices.buscar(criterio));
        model.addAttribute("criterioBuscado", criterio);
        return "zonas/listZonas";
    }

    @GetMapping("/filtrarTipo")
    public String filtrarTipo(@RequestParam String tipo, Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        model.addAttribute("title", "Gestión de Zonas");
        model.addAttribute("zonas", zonaServices.buscarPorTipo(tipo));
        model.addAttribute("tipoFiltro", tipo);
        return "zonas/listZonas";
    }

    @GetMapping("/filtrarActiva")
    public String filtrarActiva(@RequestParam String activa, Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        model.addAttribute("title", "Gestión de Zonas");
        if (activa == null || activa.isEmpty()) {
            model.addAttribute("zonas", zonaServices.getAll());
        } else {
            model.addAttribute("zonas", zonaServices.buscarPorActiva(Boolean.parseBoolean(activa)));
        }
        model.addAttribute("activaFiltro", activa);
        return "zonas/listZonas";
    }
}