package com.ometeotl.Abarrotes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ListaCompraRepository extends JpaRepository<ListaCompra, Long> {
    // Esto nos servirá para la Fase 4: buscar si ya existe una lista de ese proveedor hoy
    Optional<ListaCompra> findByFechaAndProveedor(LocalDate fecha, String proveedor);
}