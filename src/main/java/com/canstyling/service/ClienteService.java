package com.canstyling.service;

import com.canstyling.dao.ClienteDAO;
import com.canstyling.dao.MascotaDAO;
import com.canstyling.dao.impl.ClienteDAOImpl;
import com.canstyling.dao.impl.MascotaDAOImpl;
import com.canstyling.model.Cliente;
import com.canstyling.model.ClienteTelefono;
import com.canstyling.model.Mascota;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Capa de Servicios y Reglas de Negocio para Clientes y Fichas Caninas.
 */
public class ClienteService {

    private final ClienteDAO clienteDAO;
    private final MascotaDAO mascotaDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAOImpl();
        this.mascotaDAO = new MascotaDAOImpl();
    }

    public ClienteService(ClienteDAO clienteDAO, MascotaDAO mascotaDAO) {
        this.clienteDAO = clienteDAO;
        this.mascotaDAO = mascotaDAO;
    }

    /**
     * Registra un nuevo cliente validando unicidad de documento y correo.
     */
    public Cliente registrarCliente(Cliente cliente, String telefonoPrincipal, String tipoTel) throws Exception {
        if (cliente.getNumeroDocumento() == null || cliente.getNumeroDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de documento es obligatorio.");
        }
        if (cliente.getCorreo() == null || !cliente.getCorreo().contains("@")) {
            throw new IllegalArgumentException("El correo electrónico es inválido.");
        }

        // Validar si ya existe documento
        if (clienteDAO.findByDocumento(cliente.getNumeroDocumento()).isPresent()) {
            throw new IllegalStateException("Ya existe un cliente con el documento " + cliente.getNumeroDocumento());
        }

        // Validar si ya existe correo
        if (clienteDAO.findByCorreo(cliente.getCorreo()).isPresent()) {
            throw new IllegalStateException("Ya existe un cliente con el correo " + cliente.getCorreo());
        }

        Cliente nuevo = clienteDAO.create(cliente);

        if (telefonoPrincipal != null && !telefonoPrincipal.trim().isEmpty()) {
            ClienteTelefono tel = new ClienteTelefono(nuevo.getIdCliente(), telefonoPrincipal.trim(), tipoTel, true, false);
            clienteDAO.addTelefono(tel);
            nuevo.addTelefono(tel);
        }

        return nuevo;
    }

    public Optional<Cliente> buscarPorId(Integer idCliente) throws SQLException {
        Optional<Cliente> cliente = clienteDAO.findById(idCliente);
        cliente.ifPresent(c -> {
            try {
                c.setMascotas(mascotaDAO.findByClienteId(c.getIdCliente()));
            } catch (SQLException ignored) {
            }
        });
        return cliente;
    }

    public Optional<Cliente> buscarPorCorreo(String correo) throws SQLException {
        return clienteDAO.findByCorreo(correo);
    }

    public Optional<Cliente> buscarPorTelefono(String telefono) throws SQLException {
        return clienteDAO.findByTelefono(telefono);
    }

    public List<Cliente> listarTodos() throws SQLException {
        return clienteDAO.findAll();
    }

    public boolean actualizarCliente(Cliente cliente) throws SQLException {
        return clienteDAO.update(cliente);
    }

    public boolean eliminarCliente(Integer idCliente) throws SQLException {
        return clienteDAO.deleteById(idCliente);
    }

    public Mascota registrarMascota(Mascota mascota) throws Exception {
        if (mascota.getIdCliente() == null || mascota.getIdRaza() == null || mascota.getNombre() == null) {
            throw new IllegalArgumentException("El cliente, la raza y el nombre de la mascota son obligatorios.");
        }
        return mascotaDAO.create(mascota);
    }

    public List<Mascota> listarMascotasDeCliente(Integer idCliente) throws SQLException {
        return mascotaDAO.findByClienteId(idCliente);
    }
}
