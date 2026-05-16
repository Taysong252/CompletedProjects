package cr.ac.una.vivero.controller;

import cr.ac.una.vivero.domain.Empaque;
import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.services.EmpaqueService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/empaques")
public class EmpaqueController {

    @Autowired
    private EmpaqueService empaqueService;

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
        List<Empaque> todos = empaqueService.getAll();
        long activos = todos.stream().filter(Empaque::isActivo).count();
        model.addAttribute("empaques", todos);
        model.addAttribute("totalEmpaques", todos.size());
        model.addAttribute("totalActivos", activos);
        return "empaques/listEmpaque";
    }

    @GetMapping("/agregar")
    public String agregar(Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        model.addAttribute("empaque", new Empaque());
        return "empaques/formEmpaque";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam int id, Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        model.addAttribute("empaque", empaqueService.getById(id));
        return "empaques/formEmpaque";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam int id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        empaqueService.delete(id);
        redirectAttributes.addFlashAttribute("exito", "Empaque eliminado correctamente.");
        return "redirect:/empaques/";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Empaque empaque, Model model,
            RedirectAttributes redirectAttributes, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";

        boolean hayError = false;

        String tipo = empaque.getTipo() == null ? "" : empaque.getTipo().trim();
        String descripcion = empaque.getDescripcion() == null ? "" : empaque.getDescripcion().trim();
        String unidadMedida = empaque.getUnidadMedida() == null ? "" : empaque.getUnidadMedida().trim();

        if (tipo.isEmpty()) {
            model.addAttribute("errorTipo", "El tipo de empaque es obligatorio.");
            hayError = true;
        }
        if (!tipo.isEmpty() && empaqueService.existeTipo(tipo, empaque.getId())) {
            model.addAttribute("errorTipo", "Ya existe un empaque con ese tipo.");
            hayError = true;
        }
        if (descripcion.isEmpty()) {
            model.addAttribute("errorDescripcion", "La descripción es obligatoria.");
            hayError = true;
        }
        if (unidadMedida.isEmpty()) {
            model.addAttribute("errorUnidadMedida", "La unidad de medida es obligatoria.");
            hayError = true;
        }
        if (empaque.getCantidadPlanta() <= 0) {
            model.addAttribute("errorCantidadPlanta", "La cantidad de plantas debe ser mayor a 0.");
            hayError = true;
        }
        if (empaque.getStock() < 0) {
            model.addAttribute("errorStock", "El stock no puede ser negativo.");
            hayError = true;
        }
        if (empaque.getCostoUnitario() <= 0) {
            model.addAttribute("errorCostoUnitario", "El costo unitario debe ser mayor a 0.");
            hayError = true;
        }

        if (hayError) {
            model.addAttribute("empaque", empaque);
            return "empaques/formEmpaque";
        }

        boolean esNuevo = empaque.getId() == 0;
        empaqueService.save(empaque);
        redirectAttributes.addFlashAttribute("exito", esNuevo
                ? "Empaque registrado correctamente."
                : "Empaque actualizado correctamente.");
        return "redirect:/empaques/";
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam String criterio, Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        List<Empaque> resultado = empaqueService.buscar(criterio);
        long activos = resultado.stream().filter(Empaque::isActivo).count();
        model.addAttribute("empaques", resultado);
        model.addAttribute("totalEmpaques", resultado.size());
        model.addAttribute("totalActivos", activos);
        model.addAttribute("criterioBuscado", criterio);
        return "empaques/listEmpaque";
    }

    @GetMapping("/filtrarActivo")
    public String filtrarActivo(@RequestParam boolean activo, Model model, HttpSession session) {
        if (sinSesion(session)) return "redirect:/";
        if (esCliente(session)) return "redirect:/inicio/cliente";
        List<Empaque> resultado = empaqueService.buscarPorActivo(activo);
        long activos = resultado.stream().filter(Empaque::isActivo).count();
        model.addAttribute("empaques", resultado);
        model.addAttribute("totalEmpaques", resultado.size());
        model.addAttribute("totalActivos", activos);
        model.addAttribute("activoFiltro", activo);
        return "empaques/listEmpaque";
    }
}