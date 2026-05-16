package cr.ac.una.vivero.controller;

import cr.ac.una.vivero.domain.Promocion;
import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.services.PromocionService;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/promociones")
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    private boolean sinSesion(HttpSession session) {
        return session.getAttribute("usuarioActivo") == null;
    }

    private boolean esCliente(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioActivo");
        return u != null && "Cliente".equals(u.getTipoUsuario());
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }

        boolean cliente = esCliente(session);
        List<Promocion> promociones;

        if (cliente) {
            promociones = promocionService.getAllActivas();
        } else {
            promociones = promocionService.getAll();
        }

        long activas = promociones.stream().filter(Promocion::isActiva).count();

        model.addAttribute("promociones", promociones);
        model.addAttribute("totalPromociones", promociones.size());
        model.addAttribute("totalActivas", activas);
        model.addAttribute("esCliente", cliente);

        return "promociones/list";
    }

    @GetMapping("/agregar")
    public String agregar(Model model, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        model.addAttribute("promocion", new Promocion());
        model.addAttribute("esCliente", false);

        return "promociones/formPromocion";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam int id, Model model, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        model.addAttribute("promocion", promocionService.getById(id));
        model.addAttribute("esCliente", false);

        return "promociones/formPromocion";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam int id, RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        promocionService.delete(id);
        flash.addFlashAttribute("exito", "Promoción eliminada correctamente.");
        return "redirect:/promociones/";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Promocion promocion,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        boolean hayError = false;

        String nombre = promocion.getNombrePromocion() == null ? "" : promocion.getNombrePromocion().trim();
        String descripcion = promocion.getDescripcion() == null ? "" : promocion.getDescripcion().trim();
        String codigo = promocion.getCodigoPromo() == null ? "" : promocion.getCodigoPromo().trim();

        if (nombre.isEmpty()) {
            model.addAttribute("errorNombrePromocion", "El nombre es obligatorio.");
            hayError = true;
        }

        if (descripcion.isEmpty()) {
            model.addAttribute("errorDescripcion", "La descripción es obligatoria.");
            hayError = true;
        }

        if (promocion.getDescuentoPorcentaje() <= 0 || promocion.getDescuentoPorcentaje() > 100) {
            model.addAttribute("errorDescuentoPorcentaje", "Debe estar entre 0.01 y 100.");
            hayError = true;
        }

        if (promocion.getMontoMinimo() <= 0) {
            model.addAttribute("errorMontoMinimo", "Debe ser mayor a 0.");
            hayError = true;
        }

        if (codigo.isEmpty()) {
            model.addAttribute("errorCodigoPromo", "El código es obligatorio.");
            hayError = true;
        }

        if (promocion.getFechaInicio() == null) {
            model.addAttribute("errorFechaInicio", "Fecha inicio obligatoria.");
            hayError = true;
        }

        if (promocion.getFechaFin() == null) {
            model.addAttribute("errorFechaFin", "Fecha fin obligatoria.");
            hayError = true;
        }

        if (promocion.getFechaInicio() != null && promocion.getFechaFin() != null) {
            if (promocion.getFechaFin().isBefore(promocion.getFechaInicio())) {
                model.addAttribute("errorFechaFin", "La fecha fin no puede ser anterior a la fecha inicio.");
                hayError = true;
            }
        }

        if (!nombre.isEmpty() && promocionService.nombreYaExiste(nombre, promocion.getId())) {
            model.addAttribute("errorNombrePromocion", "Ya existe una promoción con ese nombre.");
            hayError = true;
        }

        if (!codigo.isEmpty() && promocionService.codigoYaExiste(codigo, promocion.getId())) {
            model.addAttribute("errorCodigoPromo", "Ya existe una promoción con ese código.");
            hayError = true;
        }

        if (hayError) {
            model.addAttribute("promocion", promocion);
            model.addAttribute("esCliente", false);
            return "promociones/formPromocion";
        }

        boolean esNueva = promocion.getId() == 0;
        promocionService.save(promocion);

        redirectAttributes.addFlashAttribute("exito",
                esNueva ? "Promoción registrada correctamente."
                        : "Promoción actualizada correctamente.");

        return "redirect:/promociones/";
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam String criterio, Model model, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }

        boolean cliente = esCliente(session);
        List<Promocion> resultado;

        if (cliente) {
            resultado = promocionService.getAllActivas();
        } else {
            resultado = promocionService.buscar(criterio);
        }

        long activas = resultado.stream().filter(Promocion::isActiva).count();

        model.addAttribute("promociones", resultado);
        model.addAttribute("totalPromociones", resultado.size());
        model.addAttribute("totalActivas", activas);
        model.addAttribute("criterioBuscado", criterio);
        model.addAttribute("esCliente", cliente);

        return "promociones/list";
    }

    @GetMapping("/verificar")
    @ResponseBody
    public Map<String, Object> verificar(@RequestParam String codigo,
            @RequestParam double subtotal,
            HttpSession session) {

        Map<String, Object> respuesta = new HashMap<>();

        if (sinSesion(session)) {
            respuesta.put("valida", false);
            respuesta.put("mensaje", "Sesión expirada.");
            return respuesta;
        }

        try {
            Promocion promo = promocionService.getByCodigoActiva(codigo, subtotal);
            respuesta.put("valida", true);
            respuesta.put("nombre", promo.getNombrePromocion());
            respuesta.put("porcentaje", promo.getDescuentoPorcentaje());
            respuesta.put("montoMinimo", promo.getMontoMinimo());
        } catch (IllegalArgumentException e) {
            respuesta.put("valida", false);
            respuesta.put("mensaje", e.getMessage());
        }

        return respuesta;
    }
}
