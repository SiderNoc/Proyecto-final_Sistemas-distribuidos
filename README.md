
# Tienda basica usando SPRING

Aplicacion web simple de una tienda en linea usando Java, Spring boot y MySQL

## Caracteristicas
* Autenticación simple de usuarios mediante usuario y contraseña
* Vista de administrador con el CRUD de productos
* Carrito de compras
* Registro de usuarios

***Nota:*** *Solo se pueden insertar usuarios del tipo ADMIN usando el siguiente fragmento de codigo comentado en* **TiendaApplication.java**

## Instrucciones de ejecución
 

Crea una nueva base de datos en tu entorno local de MySQL llamada  **tienda_spring** y luego ejecuta lo siguiente para crear las tablas de la base de datos

```mysql
USE tienda_spring;

-- Tabla para los usuarios
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL
);
-- Tabla para los productos
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2),
    stock INT
);

-- Tabla para los carritos de compra
CREATE TABLE carritos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL UNIQUE, -- Cada usuario tiene un solo carrito
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_carrito_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabla para los ítems dentro de cada carrito
CREATE TABLE carrito_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    carrito_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL,
    CONSTRAINT fk_item_carrito FOREIGN KEY (carrito_id) REFERENCES carritos(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    UNIQUE KEY uk_carrito_producto (carrito_id, producto_id) -- Un producto solo puede estar una vez por carrito (se actualiza cantidad)
);
```
En el archivo **application.properties** cambia los siguientes valores por los tuyos

```java
spring.datasource.url=jdbc:mysql://localhost:3306/tienda_spring?useSSL=false&serverTimezone=UTC
spring.datasource.username= Tu nombre de usuario aquí
spring.datasource.password= Tu contraseña aquí
```
## Como crear usuarios tipo ADMIN
En **TiendaApplication.java** ingresa la contrseña deseada en *String rawPassword*.
```java
@Bean
	public CommandLineRunner createAdminPassword() {
		return args -> {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			// Para generar hashes de contraseña
			String rawPassword = "Tu Contraseña Aquí";
			String encodedPassword = passwordEncoder.encode(rawPassword);

			System.out.println("======================================================================");
			System.out.println("NUEVO usuario - Contraseña en texto plano: " + rawPassword);
			System.out.println("NUEVO usuario - Contraseña hasheada (para la BD): " + encodedPassword);
			System.out.println("Copia esta contraseña hasheada para el nuevo usuario comun.");
			System.out.println("======================================================================");
		};
```
Inserta un nuevo registro en la base de datos siguiendo el ejemplo:
```mysql
INSERT INTO usuarios (username, password, rol)
VALUES ('admin', 'Aqui va la contraseña hasheada', 'ADMIN');
```
***Nota:*** *Procura usar solo ADMIN y USER como roles*
