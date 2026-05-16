package cr.ac.una.vivero.controller;

import cr.ac.una.vivero.domain.Usuario;
import cr.ac.una.vivero.domain.Visita;
import cr.ac.una.vivero.services.VisitaService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/visitas")
public class VisitaController {

    @Autowired
    private VisitaService visitaService;

    private boolean sinSesion(HttpSession session) {
        return session.getAttribute("usuarioActivo") == null;
    }

    private boolean esCliente(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioActivo");

        return u != null
                && "Cliente".equalsIgnoreCase(u.getTipoUsuario());
    }

    private Usuario usuarioActivo(HttpSession session) {
        return (Usuario) session.getAttribute("usuarioActivo");
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {

        if (sinSesion(session)) {
            return "redirect:/";
        }

        boolean cliente = esCliente(session);

        List<Visita> visitas = cliente
                ? visitaService.getByClienteId(
                        usuarioActivo(session).getId()
                )
                : visitaService.getAll();

        model.addAttribute("visitas", visitas);
        model.addAttribute("esCliente", cliente);

        return "visitas/list";
    }

    @GetMapping("/add")
    public String add(Model model, HttpSession session) {

        if (sinSesion(session)) {
            return "redirect:/";
        }

        boolean cliente = esCliente(session);

        model.addAttribute("visitaNew", new Visita());
        model.addAttribute("esCliente", cliente);

        cargarListas(model, session);

        return "visitas/formVisitas";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute("visitaNew") Visita visitaNew,
            @RequestParam(defaultValue = "0") int clienteId,
            @RequestParam(value = "adminId", defaultValue = "0") int administradorId,
            Model model,
            RedirectAttributes flash,
            HttpSession session
    ) {

        if (sinSesion(session)) {
            return "redirect:/";
        }

        boolean cliente = esCliente(session);

        if (cliente) {

            clienteId = usuarioActivo(session).getId();

            administradorId = 1;

            visitaNew.setEstado("Pendiente");

            visitaNew.setHoraInicio(null);
            visitaNew.setHoraFin(null);
        }

        boolean hayError = false;

        if (clienteId <= 0) {

            model.addAttribute(
                    "errorCliente",
                    "Debe seleccionar un cliente."
            );

            hayError = true;
        }

        if (visitaNew.getFechaVisita() == null) {

            model.addAttribute(
                    "errorFecha",
                    "La fecha de visita es obligatoria."
            );

            hayError = true;
        }

        if (visitaNew.getMotivoVisita() == null
                || visitaNew.getMotivoVisita().trim().isEmpty()) {

            model.addAttribute(
                    "errorMotivo",
                    "El motivo de la visita es obligatorio."
            );

            hayError = true;
        }

        if (visitaNew.getCantidadPersonas() <= 0) {

            model.addAttribute(
                    "errorPersonas",
                    "La cantidad de personas debe ser mayor a 0."
            );

            hayError = true;
        }

        if (!cliente) {

            if (administradorId <= 0) {

                model.addAttribute(
                        "errorAdmin",
                        "Debe seleccionar un administrador."
                );

                hayError = true;
            }

            if (visitaNew.getHoraInicio() == null
                    || visitaNew.getHoraFin() == null) {

                model.addAttribute(
                        "errorHoras",
                        "Las horas de inicio y fin son obligatorias."
                );

                hayError = true;

            } else if (visitaNew.getHoraFin().isBefore(
                    visitaNew.getHoraInicio()
            )
                    || visitaNew.getHoraFin().equals(
                            visitaNew.getHoraInicio()
                    )) {

                model.addAttribute(
                        "errorHoras",
                        "La hora de fin debe ser posterior a la hora de inicio."
                );

                hayError = true;
            }
        }

        if (!hayError) {

            String errorDisponibilidad
                    = visitaService.validarDisponibilidad(
                            visitaNew,
                            clienteId,
                            administradorId
                    );

            if (errorDisponibilidad != null) {

                model.addAttribute(
                        "errorHoras",
                        errorDisponibilidad
                );

                hayError = true;
            }
        }

        if (hayError) {

            model.addAttribute("visitaNew", visitaNew);
            model.addAttribute("esCliente", cliente);

            cargarListas(model, session);

            return "visitas/formVisitas";
        }

        boolean esNueva = visitaNew.getId() == 0;

        visitaService.save(
                visitaNew,
                clienteId,
                administradorId
        );

        flash.addFlashAttribute(
                "exito",
                esNueva
                        ? (cliente
                                ? "Solicitud enviada. Un administrador la confirmará pronto."
                                : "Visita agendada correctamente.")
                        : "Visita actualizada correctamente."
        );

        return "redirect:/visitas/";
    }

    @GetMapping("/delete")
    public String delete(
            @RequestParam int id,
            RedirectAttributes flash,
            HttpSession session
    ) {

        if (sinSesion(session)) {
            return "redirect:/";
        }

        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        visitaService.delete(id);

        flash.addFlashAttribute(
                "exito",
                "Visita eliminada correctamente."
        );

        return "redirect:/visitas/";
    }

    @GetMapping("/update")
    public String update(
            @RequestParam int id,
            Model model,
            HttpSession session
    ) {

        if (sinSesion(session)) {
            return "redirect:/";
        }

        if (esCliente(session)) {
            return "redirect:/inicio/cliente";
        }

        model.addAttribute(
                "visitaNew",
                visitaService.getById(id)
        );

        model.addAttribute("esCliente", false);

        cargarListas(model, session);

        return "visitas/formVisitas";
    }

    @GetMapping("/buscar")
    @ResponseBody
    public ResponseEntity<List<Visita>> buscar(
            @RequestParam String criterio,
            HttpSession session
    ) {

        if (sinSesion(session)) {
            return ResponseEntity.status(401).build();
        }

        List<Visita> resultado = esCliente(session)
                ? visitaService.buscarPorMotivoYCliente(
                        usuarioActivo(session).getId(),
                        criterio
                )
                : visitaService.buscarPorMotivo(criterio);

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/filtrarEstado")
    @ResponseBody
    public ResponseEntity<List<Visita>> filtrarEstado(
            @RequestParam String estado,
            HttpSession session
    ) {

        if (sinSesion(session)) {
            return ResponseEntity.status(401).build();
        }

        List<Visita> resultado = esCliente(session)
                ? visitaService.buscarPorEstadoYCliente(
                        usuarioActivo(session).getId(),
                        estado
                )
                : visitaService.buscarPorEstado(estado);

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/misVisitas")
    @ResponseBody
    public ResponseEntity<List<Visita>> misVisitas(
            HttpSession session
    ) {

        if (sinSesion(session)) {
            return ResponseEntity.status(401).build();
        }

        if (!esCliente(session)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                visitaService.getByClienteId(
                        usuarioActivo(session).getId()
                )
        );
    }

    private void cargarListas(Model model, HttpSession session) {

        if (esCliente(session)) {

            model.addAttribute(
                    "clientes",
                    visitaService.getUsuarioPorId(
                            usuarioActivo(session).getId()
                    )
            );

        } else {

            model.addAttribute(
                    "clientes",
                    visitaService.getUsuariosPorTipo("Cliente")
            );
        }

        model.addAttribute(
                "administradores",
                visitaService.getUsuariosPorTipo("Administrador")
        );
    }
}
