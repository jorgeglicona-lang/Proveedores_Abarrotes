package com.ometeotl.Abarrotes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemProductoRepository extends JpaRepository<ItemProducto, Long> {
}