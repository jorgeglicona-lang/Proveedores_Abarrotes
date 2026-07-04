package com.ometeotl.Abarrotes;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.Arrays;

@SpringBootApplication
public class AbarrotesApplication {

	public static void main(String[] args) {
		SpringApplication.run(AbarrotesApplication.class, args);
	}

	// Este código se ejecuta una sola vez al arrancar el servidor
	@Bean
	CommandLineRunner iniciarDatos(ProveedorRepository proveedorRepo) {
		return args -> {
			// Si la tabla está vacía, la llenamos con sus datos históricos
			if (proveedorRepo.count() == 0) {
				System.out.println("--- Cargando proveedores iniciales en la BD ---");
				// Lunes
				Arrays.asList("Rabbit", "Guna", "Orbit", "Pedigree", "Marlboro")
						.forEach(nombre -> proveedorRepo.save(new Proveedor(nombre, "MONDAY")));
				// Martes
				Arrays.asList("Bimbo", "Lala", "Coca Cola", "Costeña", "Gamesa", "Prispas Bocados", "Patos", "Farmacia", "Pepsi", "Sabritas", "Montana")
						.forEach(nombre -> proveedorRepo.save(new Proveedor(nombre, "TUESDAY")));
				// Miércoles
				Arrays.asList("El Comal/Tortilla", "Jarro", "Holanda", "Corona", "Malboro", "Dog Chao")
						.forEach(nombre -> proveedorRepo.save(new Proveedor(nombre, "WEDNESDAY")));
				// Jueves
				Arrays.asList("Mixta Croqueta", "Coca Cola", "Pepsi", "Jumex", "Lala", "Peñafielt", "Kellogs")
						.forEach(nombre -> proveedorRepo.save(new Proveedor(nombre, "THURSDAY")));
				// Viernes
				Arrays.asList("Alpura", "Barcel", "Danone", "Fud/Sigma", "Ricolino", "Gamesa", "Sabritas", "Bimbo", "Yakult", "New Mix")
						.forEach(nombre -> proveedorRepo.save(new Proveedor(nombre, "FRIDAY")));
				// Sábado
				Arrays.asList("Marinela", "Tia Rosa", "Jarro", "Danone", "Clemente Jack", "Kinder", "Marlboro", "Pepsi", "Coca Cola")
						.forEach(nombre -> proveedorRepo.save(new Proveedor(nombre, "SATURDAY")));

				System.out.println("--- Proveedores cargados exitosamente ---");
			}
		};
	}
}