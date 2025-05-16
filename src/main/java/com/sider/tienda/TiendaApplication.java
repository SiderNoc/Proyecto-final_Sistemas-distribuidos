package com.sider.tienda;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.boot.CommandLineRunner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class TiendaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiendaApplication.class, args);
	}
	/*@Bean
	public CommandLineRunner createAdminPassword() {
		return args -> {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			// Para generar hashes de contraseña
			String rawPassword = "admin123";
			String encodedPassword = passwordEncoder.encode(rawPassword);

			System.out.println("======================================================================");
			System.out.println("NUEVO usuario - Contraseña en texto plano: " + rawPassword);
			System.out.println("NUEVO usuario - Contraseña hasheada (para la BD): " + encodedPassword);
			System.out.println("Copia esta contraseña hasheada para el nuevo usuario comun.");
			System.out.println("======================================================================");
		};
	}*/

}
