package cr.ac.una.vivero.controller;

import cr.ac.una.vivero.domain.Insumo;
import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.services.InsumoService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/insumos")
public class InsumoController {

    @Autowired
    private InsumoService insumoService;

    private boolean sinSesion(HttpSession session) {
        return session.getAttribute("usuarioActivo") == null;
    }

    private boolean esCliente(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioActivo");
        return u != null && "Cliente".equals(u.getTipoUsuario());
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        model.addAttribute("insumos", insumoService.getAll());
        return "insumos/list";
    }

    @GetMapping("/add")
    public String add(Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        model.addAttribute("insumoNew", new Insumo());
        return "insumos/formInsumos";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("insumoNew") Insumo insumo,
            Model model, RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";

        boolean hayError = false;

        if (insumo.getNombreInsumo() == null || insumo.getNombreInsumo().trim().isEmpty()) {
            model.addAttribute("errorNombre", "El nombre del insumo es obligatorio.");
            hayError = true;
        }
        if (insumo.getCategoria() == null || insumo.getCategoria().trim().isEmpty()) {
            model.addAttribute("errorCategoria", "La categoría es obligatoria.");
            hayError = true;
        }
        if (insumo.getUnidadMedida() == null || insumo.getUnidadMedida().trim().isEmpty()) {
            model.addAttribute("errorUnidadMedida", "La unidad de medida es obligatoria.");
            hayError = true;
        }
        if (insumo.getCantidadStock() < 0) {
            model.addAttribute("errorStock", "La cantidad en stock no puede ser negativa.");
            hayError = true;
        }
        if (insumo.getPrecioUnitario() <= 0) {
            model.addAttribute("errorPrecio", "El precio unitario debe ser mayor a cero.");
            hayError = true;
        }

        if (hayError) {
            model.addAttribute("insumoNew", insumo);
            return "insumos/formInsumos";
        }

        boolean esNuevo = insumo.getId() == 0;
        insumoService.save(insumo);
        flash.addFlashAttribute("exito", esNuevo
                ? "Insumo registrado correctamente."
                : "Insumo actualizado correctamente.");
        return "redirect:/insumos/";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam int id, RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        insumoService.delete(id);
        flash.addFlashAttribute("exito", "Insumo eliminado correctamente.");
        return "redirect:/insumos/";
    }

    @GetMapping("/update")
    public String update(@RequestParam int id, Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        model.addAttribute("insumoNew", insumoService.getById(id));
        return "insumos/formInsumos";
    }

    @GetMapping("/buscar")
    @ResponseBody
    public ResponseEntity<List<Insumo>> buscar(@RequestParam String criterio, HttpSession session) {
        if (sinSesion(session)) return ResponseEntity.status(401).build();
        if (esCliente(session)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(insumoService.buscarPorNombre(criterio));
    }

    @GetMapping("/filtrarCategoria")
    @ResponseBody
    public ResponseEntity<List<Insumo>> filtrarCategoria(@RequestParam String categoria, HttpSession session) {
        if (sinSesion(session)) return ResponseEntity.status(401).build();
        if (esCliente(session)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(insumoService.buscarPorCategoria(categoria));
    }
}