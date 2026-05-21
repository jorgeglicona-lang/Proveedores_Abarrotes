package com.ometeotl.Abarrotes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class CompraController {

    @Autowired
    private ListaCompraRepository listaRepo;

    @Autowired
    private ItemProductoRepository productoRepo;

    // 1. Pantalla principal (Carga inicial)
    @GetMapping
    public String mostrarInicio(Model model, 
                               @RequestParam(value = "fecha", required = false) String fechaStr,
                               @RequestParam(value = "proveedor", required = false) String proveedor) {
        
        // Determinar la fecha (Si no elige una, usa la de hoy)
        LocalDate fecha = (fechaStr != null && !fechaStr.isEmpty()) ? LocalDate.parse(fechaStr) : LocalDate.now();
        model.addAttribute("fechaSeleccionada", fecha);
        model.addAttribute("proveedorSeleccionado", proveedor);

        // --- NUEVA LÓGICA: Filtrar por día de la semana ---
        java.time.DayOfWeek diaSemana = fecha.getDayOfWeek(); 
        List<String> proveedoresDelDia = obtenerProveedoresPorDia(diaSemana);
        model.addAttribute("todosLosProveedores", proveedoresDelDia);
        // --------------------------------------------------

        if (proveedor != null && !proveedor.isEmpty()) {
            // Fase 4: Buscar si ya existe una lista para esa fecha y proveedor
            Optional<ListaCompra> listaExistente = listaRepo.findByFechaAndProveedor(fecha, proveedor);
            
            if (listaExistente.isPresent()) {
                model.addAttribute("listaActual", listaExistente.get());
                model.addAttribute("productos", listaExistente.get().getProductos());
                model.addAttribute("bloqueado", "ENTREGADO".equals(listaExistente.get().getEstado()));
            } else {
                // Si no existe, pasamos un objeto limpio en Fase 1
                ListaCompra nuevaLista = new ListaCompra(fecha, proveedor);
                model.addAttribute("listaActual", nuevaLista);
                model.addAttribute("productos", nuevaLista.getProductos());
                model.addAttribute("bloqueado", false);
            }
        }
        return "index";
    }
    
    // 6. Botón de REGRESAR FASE (Para corregir doble clics por error)
    @PostMapping("/lista/regresar")
    public String regresarFase(@RequestParam("listaId") Long listaId,
                               @RequestParam("fecha") String fechaStr,
                               @RequestParam("proveedor") String proveedor) {
        
        listaRepo.findById(listaId).ifPresent(lista -> {
            if ("PROCESADO".equals(lista.getEstado())) {
                lista.setEstado("PENDIENTE"); // De Fase 2 regresa a Fase 1
            } else if ("ENTREGADO".equals(lista.getEstado())) {
                lista.setEstado("PROCESADO"); // De Fase 3 regresa a Fase 2
            }
            listaRepo.save(lista);
        });
        return "redirect:/?fecha=" + fechaStr + "&proveedor=" + proveedor;
    }

    // --- EL HORARIO DE LA TIENDA ---
    // Aquí usted puede configurar qué proveedores vienen cada día
    private List<String> obtenerProveedoresPorDia(java.time.DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> Arrays.asList("Rabbit", "Guna", "Orbit", "Pedeedree"); // Lunes
            case TUESDAY -> Arrays.asList("Bimbo", "Lala", "Coca Cola", "Costeña", "Gamesa",
                    "Prispas/Bocados", "Patos", "Farmacia", "Pepsi", "Sabritas", "Montana"); // Martes
            case WEDNESDAY -> Arrays.asList("El Comal/Tortilla", "Jarro", "Holanda", "Corona",
                    "Malboro", "Dog Chao"); // Miércoles
            case THURSDAY -> Arrays.asList("Mixta/Croqueta", "Coca Cola", "Pepsi", "Jumex",
                    "Lala", "Peñafielt", "Kellog's"); // Jueves
            case FRIDAY -> Arrays.asList("Alpura", "Barcel", "Danone", "Fud/Sigma", "Ricolino",
                    "Gamesa", "Sabritas", "Bimbo", "Yakult", "New Mix"); // Viernes
            case SATURDAY -> Arrays.asList("Marinela", "Tia Rosa", "Jarro", "Danone",
                    "Clemente Jack", "Kinder", "Marlboro", "Pepsi"); // Sábado
            case SUNDAY -> Arrays.asList(); // Domingo (Lista vacía si no hay entregas)
        };
    }

    // 2. Acción del botón AGREGAR producto (Escritura libre)
    @PostMapping("/producto/agregar")
    public String agregarProducto(@RequestParam("fecha") String fechaStr,
                                  @RequestParam("proveedor") String proveedor,
                                  @RequestParam("nombreProducto") String nombreProducto) {
        
        if (nombreProducto == null || nombreProducto.trim().isEmpty()) {
            return "redirect:/?fecha=" + fechaStr + "&proveedor=" + proveedor;
        }

        LocalDate fecha = LocalDate.parse(fechaStr);
        // Validar u obtener la lista de la base de datos
        ListaCompra lista = listaRepo.findByFechaAndProveedor(fecha, proveedor)
                .orElseGet(() -> listaRepo.save(new ListaCompra(fecha, proveedor)));

        // Guardar el producto amarrado a esa lista
        ItemProducto nuevoProducto = new ItemProducto(nombreProducto, lista);
        productoRepo.save(nuevoProducto);

        return "redirect:/?fecha=" + fechaStr + "&proveedor=" + proveedor;
    }

    // 3. Cambiar el estado de los CHECKBOX (Fase 2 y Fase 3)
    @PostMapping("/producto/check/{id}")
    public String cambiarCheck(@PathVariable("id") Long id, 
                               @RequestParam("tipo") String tipo,
                               @RequestParam("fecha") String fechaStr,
                               @RequestParam("proveedor") String proveedor) {
        
        productoRepo.findById(id).ifPresent(prod -> {
            if ("aceptado".equals(tipo)) {
                prod.setPedidoAceptado(!prod.isPedidoAceptado());
            } else if ("recibido".equals(tipo)) {
                prod.setRecibidoFisico(!prod.isRecibidoFisico());
            }
            productoRepo.save(prod);
        });
        return "redirect:/?fecha=" + fechaStr + "&proveedor=" + proveedor;
    }

    // 4. Acción del botón ELIMINAR producto de la lista
    @PostMapping("/producto/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Long id,
                                   @RequestParam("fecha") String fechaStr,
                                   @RequestParam("proveedor") String proveedor) {
        productoRepo.deleteById(id);
        return "redirect:/?fecha=" + fechaStr + "&proveedor=" + proveedor;
    }

    // 5. Botón de GUARDAR LISTA (Cambia de fases según el estado)
    @PostMapping("/lista/guardar")
    public String guardarLista(@RequestParam("listaId") Long listaId,
                               @RequestParam("fecha") String fechaStr,
                               @RequestParam("proveedor") String proveedor) {
        
        listaRepo.findById(listaId).ifPresent(lista -> {
            if ("PENDIENTE".equals(lista.getEstado())) {
                lista.setEstado("PROCESADO"); // Pasa de Fase 1 a Fase 2/3
            } else if ("PROCESADO".equals(lista.getEstado())) {
                lista.setEstado("ENTREGADO"); // Sella la lista (Fase 4 Candado)
            }
            listaRepo.save(lista);
        });
        return "redirect:/?fecha=" + fechaStr + "&proveedor=" + proveedor;
    }
    
}