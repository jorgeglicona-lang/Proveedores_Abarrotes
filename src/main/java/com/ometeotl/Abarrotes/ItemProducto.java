package com.ometeotl.Abarrotes;

import jakarta.persistence.*;

@Entity
@Table(name = "items_productos")
public class ItemProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // Escritura libre (ej: "3 paquetes de Bimbo")
    private boolean pedidoAceptado; // Checkbox para la Fase 2
    private boolean recibidoFisico; // Checkbox para la Fase 3

    @ManyToOne
    @JoinColumn(name = "lista_compra_id")
    private ListaCompra listaCompra; // A qué lista pertenece este producto

    public ItemProducto() {}

    public ItemProducto(String nombre, ListaCompra listaCompra) {
        this.nombre = nombre;
        this.listaCompra = listaCompra;
        this.pedidoAceptado = false; // Inicia sin marcar
        this.recibidoFisico = false; // Inicia sin marcar
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public boolean isPedidoAceptado() { return pedidoAceptado; }
    public void setPedidoAceptado(boolean pedidoAceptado) { this.pedidoAceptado = pedidoAceptado; }
    public boolean isRecibidoFisico() { return recibidoFisico; }
    public void setRecibidoFisico(boolean recibidoFisico) { this.recibidoFisico = recibidoFisico; }
    public void setListaCompra(ListaCompra listaCompra) { this.listaCompra = listaCompra; }
}