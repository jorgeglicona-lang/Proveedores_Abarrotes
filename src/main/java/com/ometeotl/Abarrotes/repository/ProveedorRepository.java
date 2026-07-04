package com.ometeotl.Abarrotes.repository;

import com.ometeotl.Abarrotes.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    // Este método buscará automáticamente a los proveedores de un día específico
    List<Proveedor> findByDiaSemana(String diaSemana);
}