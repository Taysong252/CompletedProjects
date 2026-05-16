package cr.ac.una.vivero.controller;

import cr.ac.una.vivero.domain.Planta;
import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.services.PlantaService;
import cr.ac.una.vivero.services.VentaService; 
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/plantas")
public class PlantaController {

    @Autowired
    private PlantaService plantaService;

    @Autowired
    private VentaService ventaService;   // ← NUEVO: para obtener admins en el catálogo

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

        model.addAttribute("plantas", tipo != null && !tipo.isEmpty()
                ? plantaService.buscarPorTipo(tipo)
                : plantaService.obtenerTodas());
        model.addAttribute("tipoFiltro", tipo != null ? tipo : "");
        return "plantas/listPlantas";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        model.addAttribute("planta", new Planta());
        model.addAttribute("modoEdicion", false);
        return "plantas/formPlantas";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Planta planta,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        String error = plantaService.crear(planta, imagen);
        if (error != null) {
            flash.addFlashAttribute("error", error);
            return "redirect:/plantas/nuevo";
        }
        flash.addFlashAttribute("exito", "Planta registrada correctamente.");
        return "redirect:/plantas";
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
        Planta p = plantaService.obtenerPorId(id);
        if (p == null) {
            flash.addFlashAttribute("error", "Planta no encontrada.");
            return "redirect:/plantas";
        }
        model.addAttribute("planta", p);
        model.addAttribute("modoEdicion", true);
        return "plantas/formPlantas";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam int id, @ModelAttribute Planta planta,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        String error = plantaService.actualizar(id, planta, imagen);
        if (error != null) {
            flash.addFlashAttribute("error", error);
            return "redirect:/plantas/editar?id=" + id;
        }
        flash.addFlashAttribute("exito", "Planta actualizada correctamente.");
        return "redirect:/plantas";
    }

    @GetMapping("/activar")
    public String activar(@RequestParam int id, RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        String error = plantaService.activar(id);
        if (error != null) {
            flash.addFlashAttribute("error", error);
        } else {
            flash.addFlashAttribute("exito", "Planta activada correctamente.");
        }
        return "redirect:/plantas";
    }

    @GetMapping("/desactivar")
    public String desactivar(@RequestParam int id, RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        String error = plantaService.desactivar(id);
        if (error != null) {
            flash.addFlashAttribute("error", error);
        } else {
            flash.addFlashAttribute("exito", "Planta desactivada correctamente.");
        }
        return "redirect:/plantas";
    }

    @GetMapping("/eliminar")
    public String eliminar(@RequestParam int id, RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        String error = plantaService.eliminar(id);
        if (error != null) {
            flash.addFlashAttribute("error", error);
        } else {
            flash.addFlashAttribute("exito", "Planta eliminada correctamente.");
        }
        return "redirect:/plantas";
    }

    @GetMapping("/imagen/{id}")
    public ResponseEntity<byte[]> imagen(@PathVariable int id) {
        Planta p = plantaService.obtenerPorId(id);
        if (p != null && p.getDatosImagen() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    p.getTipoImagen() != null ? p.getTipoImagen() : "image/jpeg"));
            return new ResponseEntity<>(p.getDatosImagen(), headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/catalogo")
    @ResponseBody
    public List<Map<String, Object>> catalogo(HttpSession session) {
        if (session.getAttribute("usuarioActivo") == null) {
            return List.of();
        }
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Planta p : plantaService.listarActivas()) {
            Map<String, Object> dto = new java.util.LinkedHashMap<>();
            dto.put("id", p.getId());
            dto.put("nombreComun", p.getNombreComun());
            dto.put("nombreCientifico", p.getNombreCientifico());
            dto.put("tipo", p.getTipo());
            dto.put("precio", p.getPrecio());
            dto.put("cantidad", p.getCantidad());
            dto.put("tieneImagen", p.getDatosImagen() != null);
            resultado.add(dto);
        }
        return resultado;
    }

    @GetMapping("/catalogo-vista")
    public String catalogoVista(Model model, HttpSession session) {
        if (session.getAttribute("usuarioActivo") == null) {
            return "redirect:/";
        }
        model.addAttribute("plantas", plantaService.listarActivas());
        model.addAttribute("admins", ventaService.obtenerAdmins()); // ← NUEVO
        return "plantas/catalogoCliente";
    }
}
