package com.solucionTech.service;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.solucionTech.model.Categoria;
import com.solucionTech.repository.CategoriaRepository;
import com.solucionTech.repository.ProductoRepository;
@Service
public class CategoriaService {
    private final CategoriaRepository repository;
    private final ProductoRepository productoRepository;
    public CategoriaService(CategoriaRepository repository, ProductoRepository productoRepository){this.repository=repository; this.productoRepository=productoRepository;}
    @Transactional(readOnly=true) public List<Categoria> listarTodos(){return repository.findAll();}
    @Transactional(readOnly=true) public List<Categoria> listarActivas(){return repository.findByEstadoTrueOrderByNombreAsc();}
    @Transactional(readOnly=true) public Optional<Categoria> buscarPorId(Long id){return repository.findById(id);}
    @Transactional public Categoria registrar(Categoria c){
        if(c.getNombre()==null||c.getNombre().isBlank()) throw new IllegalArgumentException("El nombre es obligatorio.");
        if(repository.existsByNombreIgnoreCase(c.getNombre())) throw new IllegalArgumentException("La categoría ya existe.");
        c.setEstado(true); return repository.save(c);
    }
    @Transactional public Categoria actualizar(Categoria c){
        Categoria old=repository.findById(c.getId()).orElseThrow(()->new IllegalArgumentException("Categoría no encontrada."));
        if(c.getNombre()==null||c.getNombre().isBlank()) throw new IllegalArgumentException("El nombre es obligatorio.");
        c.setEstado(old.getEstado()); return repository.save(c);
    }
    @Transactional public void eliminar(Long id){
        Categoria c=repository.findById(id).orElseThrow(()->new IllegalArgumentException("Categoría no encontrada."));
        c.setEstado(false); repository.save(c);
    }
    @Transactional public void eliminarDefinitivamente(Long id){
        if(!repository.existsById(id)) throw new IllegalArgumentException("Categoría no encontrada.");
        if(productoRepository.existsByCategoriaId(id)) throw new IllegalStateException("No se puede eliminar definitivamente: la categoría tiene productos asociados.");
        repository.deleteById(id);
    }
    @Transactional public void activar(Long id){
        Categoria c=repository.findById(id).orElseThrow(()->new IllegalArgumentException("Categoría no encontrada."));
        c.setEstado(true); repository.save(c);
    }
}
