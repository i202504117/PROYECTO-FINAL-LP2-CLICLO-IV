package com.solucionTech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solucionTech.model.Producto;
import com.solucionTech.model.TipoProducto;

public interface ProductoRepository
        extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);
    boolean existsByCategoriaId(Long categoriaId);
    boolean existsByProveedorId(Long proveedorId);
  
 

    List<Producto> findByEstadoTrueOrderByNombreAsc();

    List<Producto> findByTipoProductoAndEstadoTrueOrderByNombreAsc(
            TipoProducto tipoProducto);

    List<Producto> findByStockActualLessThanEqualAndEstadoTrue(
            Integer stockMinimo);
    List<Producto> findByEstadoTrueOrderByStockActualAsc();
}
