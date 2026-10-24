package com.solucionTech.service;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.solucionTech.model.Proveedor;
import com.solucionTech.repository.ProveedorRepository;
import com.solucionTech.repository.ProductoRepository;
import com.solucionTech.repository.CompraRepository;
@Service
public class ProveedorService {
    private final ProveedorRepository repository;
    private final ProductoRepository productoRepository;
    private final CompraRepository compraRepository;
    public ProveedorService(ProveedorRepository repository, ProductoRepository productoRepository, CompraRepository compraRepository){this.repository=repository; this.productoRepository=productoRepository; this.compraRepository=compraRepository;}
    @Transactional(readOnly=true) public List<Proveedor> listarTodos(){return repository.findAll();}
    @Transactional(readOnly=true) public List<Proveedor> listarActivos(){return repository.findByEstadoTrueOrderByRazonSocialAsc();}
    @Transactional(readOnly=true) public Optional<Proveedor> buscarPorId(Long id){return repository.findById(id);}
    @Transactional public Proveedor registrar(Proveedor p){
        if(p.getRazonSocial()==null||p.getRazonSocial().isBlank()) throw new IllegalArgumentException("La razón social es obligatoria.");
        if(p.getRuc()!=null&&!p.getRuc().isBlank()&&repository.existsByRuc(p.getRuc())) throw new IllegalArgumentException("El RUC ya está registrado.");
        p.setEstado(true); return repository.save(p);
    }
    @Transactional public Proveedor actualizar(Proveedor p){
        Proveedor old=repository.findById(p.getId()).orElseThrow(()->new IllegalArgumentException("Proveedor no encontrado."));
        if(p.getRazonSocial()==null||p.getRazonSocial().isBlank()) throw new IllegalArgumentException("La razón social es obligatoria.");
        p.setEstado(old.getEstado()); return repository.save(p);
    }
    @Transactional public void eliminar(Long id){
        Proveedor p=repository.findById(id).orElseThrow(()->new IllegalArgumentException("Proveedor no encontrado."));
        p.setEstado(false); repository.save(p);
    }
    @Transactional public void eliminarDefinitivamente(Long id){
        if(!repository.existsById(id)) throw new IllegalArgumentException("Proveedor no encontrado.");
        if(productoRepository.existsByProveedorId(id) || compraRepository.existsByProveedorId(id)) throw new IllegalStateException("No se puede eliminar definitivamente: el proveedor tiene productos o compras relacionadas.");
        repository.deleteById(id);
    }
    @Transactional public void activar(Long id){
        Proveedor p=repository.findById(id).orElseThrow(()->new IllegalArgumentException("Proveedor no encontrado."));
        p.setEstado(true); repository.save(p);
    }
}
