package com.ometeotl.Abarrotes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

    @Autowired
    private ProveedorRepository proveedorRepo;
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
                               @RequestParam("proveedor") String proveedor,
                               RedirectAttributes redirectAttributes) {
        
        listaRepo.findById(listaId).ifPresent(lista -> {
            if ("PROCESADO".equals(lista.getEstado())) {
                lista.setEstado("PENDIENTE"); // De Fase 2 regresa a Fase 1
            } else if ("ENTREGADO".equals(lista.getEstado())) {
                lista.setEstado("PROCESADO"); // De Fase 3 regresa a Fase 2
            }
            listaRepo.save(lista);
        });

        redirectAttributes.addAttribute("fecha", fechaStr);
        redirectAttributes.addAttribute("proveedor", proveedor);
        return "redirect:/";
    }

    // --- EL HORARIO DE LA TIENDA ---
    // --- EL HORARIO DE LA TIENDA (DESDE BASE DE DATOS) ---
    private List<String> obtenerProveedoresPorDia(java.time.DayOfWeek dia) {
        // Busca en la BD por el nombre del día (ej: "MONDAY") y extrae solo los nombres
        return proveedorRepo.findByDiaSemana(dia.name())
                .stream()
                .map(Proveedor::getNombre)
                .toList();
    }

    // 2. Acción del botón AGREGAR producto (Escritura libre)
    @PostMapping("/producto/agregar")
    public String agregarProducto(@RequestParam("fecha") String fechaStr,
                                  @RequestParam("proveedor") String proveedor,
                                  @RequestParam("nombreProducto") String nombreProducto,
                                  RedirectAttributes redirectAttributes) {
        
        if (nombreProducto == null || nombreProducto.trim().isEmpty()) {
            redirectAttributes.addAttribute("fecha", fechaStr);
            redirectAttributes.addAttribute("proveedor", proveedor);
            return "redirect:/";
        }

        LocalDate fecha = LocalDate.parse(fechaStr);
        ListaCompra lista = listaRepo.findByFechaAndProveedor(fecha, proveedor)
                .orElseGet(() -> listaRepo.save(new ListaCompra(fecha, proveedor)));

        ItemProducto nuevoProducto = new ItemProducto(nombreProducto, lista);
        productoRepo.save(nuevoProducto);

        redirectAttributes.addAttribute("fecha", fechaStr);
        redirectAttributes.addAttribute("proveedor", proveedor);
        return "redirect:/";
    }

    // 3. Cambiar el estado de los CHECKBOX (Fase 2 y Fase 3)
    @PostMapping("/producto/check/{id}")
    public String cambiarCheck(@PathVariable("id") Long id, 
                               @RequestParam("tipo") String tipo,
                               @RequestParam("fecha") String fechaStr,
                               @RequestParam("proveedor") String proveedor,
                               RedirectAttributes redirectAttributes) {
        
        productoRepo.findById(id).ifPresent(prod -> {
            if ("aceptado".equals(tipo)) {
                prod.setPedidoAceptado(!prod.isPedidoAceptado());
            } else if ("recibido".equals(tipo)) {
                prod.setRecibidoFisico(!prod.isRecibidoFisico());
            }
            productoRepo.save(prod);
        });

        redirectAttributes.addAttribute("fecha", fechaStr);
        redirectAttributes.addAttribute("proveedor", proveedor);
        return "redirect:/";
    }

    // 4. Acción del botón ELIMINAR producto de la lista
    @PostMapping("/producto/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Long id,
                                   @RequestParam("fecha") String fechaStr,
                                   @RequestParam("proveedor") String proveedor,
                                   RedirectAttributes redirectAttributes) {
        
        productoRepo.deleteById(id);
        
        redirectAttributes.addAttribute("fecha", fechaStr);
        redirectAttributes.addAttribute("proveedor", proveedor);
        return "redirect:/";
    }

    // 5. Botón de GUARDAR LISTA (Cambia de fases según el estado)
    @PostMapping("/lista/guardar")
    public String guardarLista(@RequestParam("listaId") Long listaId,
                               @RequestParam("fecha") String fechaStr,
                               @RequestParam("proveedor") String proveedor,
                               RedirectAttributes redirectAttributes) {
        
        listaRepo.findById(listaId).ifPresent(lista -> {
            if ("PENDIENTE".equals(lista.getEstado())) {
                lista.setEstado("PROCESADO"); 
            } else if ("PROCESADO".equals(lista.getEstado())) {
                lista.setEstado("ENTREGADO"); 
            }
            listaRepo.save(lista);
        });

        redirectAttributes.addAttribute("fecha", fechaStr);
        redirectAttributes.addAttribute("proveedor", proveedor);
        return "redirect:/";
    }
    
}