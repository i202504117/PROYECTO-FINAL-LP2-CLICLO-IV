package com.solucionTech.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solucionTech.model.Cliente;
import com.solucionTech.repository.ClienteRepository;
import com.solucionTech.repository.AtencionRepository;
import com.solucionTech.repository.VentaRepository;

@Service
public class ClienteService {

    private final VentaRepository ventaRepository;
	private final AtencionRepository atencionRepository;
	private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository, AtencionRepository atencionRepository, VentaRepository ventaRepository) {
        this.clienteRepository = clienteRepository;
		this.atencionRepository = atencionRepository;
		this.ventaRepository = ventaRepository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorTelefono(String telefono) {
        return clienteRepository.findByTelefono(telefono);
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    @Transactional
    public Cliente registrar(Cliente cliente) {
        if (clienteRepository.existsByTelefono(cliente.getTelefono())) {
            throw new IllegalArgumentException(
                    "Ya existe un cliente registrado con ese teléfono");
        }

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente actualizar(Cliente cliente) {
        if (cliente.getId() == null) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }
        Cliente existente = clienteRepository.findById(cliente.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cliente no encontrado"));
        if (clienteRepository.existsByTelefonoAndIdNot(
                cliente.getTelefono(), cliente.getId())) {
            throw new IllegalArgumentException(
                    "Ya existe otro cliente con ese teléfono");
        }
        if (cliente.getEstado() == null) {
            cliente.setEstado(existente.getEstado());
        }
        if (cliente.getFechaRegistro() == null) {
            cliente.setFechaRegistro(existente.getFechaRegistro());
        }
        return clienteRepository.save(cliente);
    }

    @Transactional
    public void desactivar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cliente no encontrado"));

        cliente.setEstado(false);
        clienteRepository.save(cliente);
    }

    @Transactional
    public void activar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cliente no encontrado"));
        cliente.setEstado(true);
        clienteRepository.save(cliente);
    }

    @Transactional
    public void eliminarDefinitivamente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente no encontrado.");
        }
        if (atencionRepository.existsByClienteId(id)
                || ventaRepository.existsByClienteId(id)) {

            throw new IllegalStateException(
                    "No se puede eliminar definitivamente: "
                    + "el cliente tiene atenciones o ventas históricas relacionadas.");
        }

        clienteRepository.deleteById(id);
    }
}
