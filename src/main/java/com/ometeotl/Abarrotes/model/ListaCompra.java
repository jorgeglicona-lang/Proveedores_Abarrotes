package com.ometeotl.Abarrotes.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "listas_compras")
public class ListaCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha; // Manejo de fechas nativo de Java
    private String proveedor;
    private String estado; // PENDIENTE, PROCESADO, ENTREGADO

    // Relación: Una lista de compra tiene muchos productos dentro
    @OneToMany(mappedBy = "listaCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemProducto> productos = new ArrayList<>();

    public ListaCompra() {}

    public ListaCompra(LocalDate fecha, String proveedor) {
        this.fecha = fecha;
        this.proveedor = proveedor;
        this.estado = "PENDIENTE"; // Inicia en Fase 1
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public List<ItemProducto> getProductos() { return productos; }
    public void setProductos(List<ItemProducto> productos) { this.productos = productos; }
}