package com.ometeotl.Abarrotes.repository;

import com.ometeotl.Abarrotes.model.ItemProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemProductoRepository extends JpaRepository<ItemProducto, Long> {
}