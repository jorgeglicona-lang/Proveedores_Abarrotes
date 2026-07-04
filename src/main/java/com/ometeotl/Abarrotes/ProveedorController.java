package com.ometeotl.Abarrotes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorRepository proveedorRepo;

    // 1. Mostrar la pantalla con la tabla de proveedores
    @GetMapping
    public String mostrarProveedores(Model model,
                                     @RequestParam(value = "diaFiltro", required = false) String diaFiltro) {

        List<Proveedor> listaProveedores;

        // Si nos envían un día, filtramos. Si no, traemos todos.
        if (diaFiltro != null && !diaFiltro.isEmpty()) {
            listaProveedores = proveedorRepo.findByDiaSemana(diaFiltro);
        } else {
            listaProveedores = proveedorRepo.findAll();
        }

        model.addAttribute("proveedores", listaProveedores);
        model.addAttribute("diaFiltro", diaFiltro); // Para que el select recuerde qué día escogimos
        return "proveedores";
    }

    // 2. Acción para guardar un proveedor nuevo
    @PostMapping("/agregar")
    public String agregarProveedor(@RequestParam("nombre") String nombre,
                                   @RequestParam("diaSemana") String diaSemana) {

        if (nombre != null && !nombre.trim().isEmpty() && diaSemana != null && !diaSemana.isEmpty()) {
            proveedorRepo.save(new Proveedor(nombre.trim(), diaSemana));
        }
        return "redirect:/proveedores";
    }

    // 3. Acción para eliminar un proveedor
    @PostMapping("/eliminar/{id}")
    public String eliminarProveedor(@PathVariable("id") Long id) {
        proveedorRepo.deleteById(id);
        return "redirect:/proveedores";
    }
}