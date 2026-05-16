package cr.ac.una.vivero.controller;

import cr.ac.una.vivero.domain.CompraInsumo;
import cr.ac.una.vivero.domain.Insumo;
import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.services.CompraInsumoService;
import cr.ac.una.vivero.services.InsumoService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/compras")
public class CompraInsumoController {

    @Autowired
    private CompraInsumoService compraService;

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
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        model.addAttribute("compras", compraService.getTodas());
        return "compras/historialCompras";
    }

    @GetMapping("/historial")
    public String historial(@RequestParam int insumoId, Model model, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        Insumo insumo = insumoService.getById(insumoId);
        if (insumo == null) {
            return "redirect:/insumos/";
        }
        model.addAttribute("compras", compraService.getHistorialPorInsumo(insumoId));
        model.addAttribute("insumo", insumo);
        return "compras/historialCompras";
    }

    @GetMapping("/add")
    public String add(@RequestParam(required = false) Integer insumoId,
            Model model, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        CompraInsumo compra = new CompraInsumo();
        if (insumoId != null) {
            compra.setInsumoId(insumoId);
        }
        model.addAttribute("compraNew", compra);
        model.addAttribute("insumos", insumoService.getActivos());
        return "compras/formCompra";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("compraNew") CompraInsumo compra,
            Model model, RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        boolean hayError = false;

        if (compra.getInsumoId() <= 0) {
            model.addAttribute("errorInsumo", "Debe seleccionar un insumo.");
            hayError = true;
        }
        if (compra.getCantidad() <= 0) {
            model.addAttribute("errorCantidad", "La cantidad debe ser mayor a cero.");
            hayError = true;
        }
        if (compra.getPrecioUnitario() <= 0) {
            model.addAttribute("errorPrecio", "El precio unitario debe ser mayor a cero.");
            hayError = true;
        }
        if (compra.getFechaCompra() == null) {
            model.addAttribute("errorFecha", "La fecha de compra es obligatoria.");
            hayError = true;
        }

        if (hayError) {
            model.addAttribute("compraNew", compra);
            model.addAttribute("insumos", insumoService.getActivos());
            return "compras/formCompra";
        }

        String error = compraService.registrarCompra(compra);
        if (error != null) {
            model.addAttribute("errorInsumo", error);
            model.addAttribute("compraNew", compra);
            model.addAttribute("insumos", insumoService.getActivos());
            return "compras/formCompra";
        }

        flash.addFlashAttribute("exito", "Compra registrada y stock actualizado correctamente.");
        return "redirect:/insumos/";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam int id, RedirectAttributes flash, HttpSession session) {
        if (sinSesion(session)) {
            return "redirect:/";
        }
        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }
        compraService.eliminar(id);
        flash.addFlashAttribute("exito", "Compra eliminada correctamente.");
        return "redirect:/compras/";
    }
}
