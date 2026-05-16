package cr.ac.una.vivero.services;

import cr.ac.una.vivero.domain.Visita;
import cr.ac.una.vivero.repository.UsuarioRepository;
import cr.ac.una.vivero.repository.VisitaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VisitaService implements CRUD<Visita> {

    @Autowired
    private VisitaRepository visitaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PersistenceContext
    private EntityManager em;

    // =========================
    // ASOCIAR USUARIOS
    // =========================
    private void asociarUsuarios(Visita visita, int clienteId, int adminId) {

        if (clienteId > 0) {
            usuarioRepository.findById(clienteId)
                    .ifPresent(visita::setCliente);
        }

        if (adminId > 0) {
            usuarioRepository.findById(adminId)
                    .ifPresent(visita::setAdmin);
        } else {
            visita.setAdmin(null);
        }
    }

    // =========================
    // GUARDAR CON IDS
    // =========================
    public void save(Visita visita, int clienteId, int adminId) {
        asociarUsuarios(visita, clienteId, adminId);
        visitaRepository.save(visita);
    }

    @Override
    public void save(Visita visita) {
        visitaRepository.save(visita);
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public void delete(int i) {
        visitaRepository.deleteById(i);
    }

    // =========================
    // GET ALL
    // =========================
    @Override
    public List<Visita> getAll() {
        return visitaRepository.findAll();
    }

    // =========================
    // GET BY ID
    // =========================
    @Override
    public Visita getById(int i) {
        return visitaRepository.findById(i).orElse(null);
    }

    // =========================
    // GET POR CLIENTE
    // =========================
    public List<Visita> getByClienteId(int clienteId) {
        return visitaRepository.findByClienteId(clienteId);
    }

    // =========================
    // VALIDAR DISPONIBILIDAD
    // =========================
    public String validarDisponibilidad(
            Visita nueva,
            int clienteId,
            int adminId
    ) {

        // VALIDAR HORARIO ADMIN
        if (nueva.getHoraInicio() != null
                && nueva.getHoraFin() != null
                && adminId > 0) {

            List<Visita> visitasAdmin
                    = visitaRepository.findByAdminIdAndFechaVisita(
                            adminId,
                            nueva.getFechaVisita()
                    );

            for (Visita v : visitasAdmin) {

                if (v.getId() == nueva.getId()) {
                    continue;
                }

                if ("Cancelada".equalsIgnoreCase(v.getEstado())) {
                    continue;
                }

                if (v.getHoraInicio() != null
                        && v.getHoraFin() != null) {

                    boolean traslape
                            = nueva.getHoraInicio().isBefore(v.getHoraFin())
                            && nueva.getHoraFin().isAfter(v.getHoraInicio());

                    if (traslape) {

                        return "El administrador ya tiene una visita agendada en ese horario ("
                                + v.getHoraInicio()
                                + " - "
                                + v.getHoraFin()
                                + ").";
                    }
                }
            }
        }

        // VALIDAR CLIENTE MISMO DIA
        List<Visita> visitasCliente
                = visitaRepository.findByClienteIdAndFechaVisita(
                        clienteId,
                        nueva.getFechaVisita()
                );

        for (Visita v : visitasCliente) {

            if (v.getId() == nueva.getId()) {
                continue;
            }

            if ("Cancelada".equalsIgnoreCase(v.getEstado())) {
                continue;
            }

            return "El cliente ya tiene una visita registrada para ese día.";
        }

        return null;
    }

    // =========================
    // USUARIOS POR TIPO
    // =========================
    public List<Object[]> getUsuariosPorTipo(String tipo) {

        return em.createNativeQuery(
                "SELECT id, nombre, apellido "
                + "FROM usuarios "
                + "WHERE tipoUsuario = ?1 "
                + "AND activo = true")
                .setParameter(1, tipo)
                .getResultList();
    }

    // =========================
    // USUARIO POR ID
    // =========================
    public List<Object[]> getUsuarioPorId(int id) {

        return em.createNativeQuery(
                "SELECT id, nombre, apellido "
                + "FROM usuarios "
                + "WHERE id = ?1 "
                + "AND activo = true")
                .setParameter(1, id)
                .getResultList();
    }

    // =========================
    // BUSCAR
    // =========================
    public List<Visita> buscarPorMotivo(String motivo) {
        return visitaRepository
                .findByMotivoVisitaContainingIgnoreCase(motivo);
    }

    public List<Visita> buscarPorEstado(String estado) {
        return visitaRepository
                .findByEstadoIgnoreCase(estado);
    }

    public List<Visita> buscarPorMotivoYCliente(
            int clienteId,
            String motivo
    ) {

        return visitaRepository
                .findByClienteIdAndMotivoVisitaContainingIgnoreCase(
                        clienteId,
                        motivo
                );
    }

    public List<Visita> buscarPorEstadoYCliente(
            int clienteId,
            String estado
    ) {

        return visitaRepository
                .findByClienteIdAndEstadoIgnoreCase(
                        clienteId,
                        estado
                );
    }
}