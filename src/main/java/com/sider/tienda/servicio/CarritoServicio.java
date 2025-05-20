package com.sider.tienda.servicio;

import com.sider.tienda.modelo.Carrito;
import com.sider.tienda.modelo.CarritoItem; // La entidad JPA, no el antiguo DTO
import com.sider.tienda.modelo.Producto;
import com.sider.tienda.modelo.Usuario;
import com.sider.tienda.repositorio.CarritoItemRepositorio;
import com.sider.tienda.repositorio.CarritoRepositorio;
import com.sider.tienda.repositorio.ProductoRepositorio; // O ProductoServicio
import com.sider.tienda.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
// Ya no es @SessionScope
public class CarritoServicio {

    private final CarritoRepositorio carritoRepositorio;
    private final CarritoItemRepositorio carritoItemRepositorio;
    private final ProductoRepositorio productoRepositorio; // Usaremos ProductoRepositorio directamente
    private final UsuarioRepositorio usuarioRepositorio;

    @Autowired
    public CarritoServicio(CarritoRepositorio carritoRepositorio,
                           CarritoItemRepositorio carritoItemRepositorio,
                           ProductoRepositorio productoRepositorio,
                           UsuarioRepositorio usuarioRepositorio) {
        this.carritoRepositorio = carritoRepositorio;
        this.carritoItemRepositorio = carritoItemRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    // Método auxiliar para obtener o crear el carrito de un usuario
    @Transactional
    protected Carrito getOrCreateCarritoDelUsuario(String username) {
        Usuario usuario = usuarioRepositorio.findByUsername(username);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado: " + username);
        }

        return carritoRepositorio.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    Carrito nuevoCarrito = new Carrito();
                    nuevoCarrito.setUsuario(usuario);
                    return carritoRepositorio.save(nuevoCarrito);
                });
    }

    @Transactional
    public void agregarItem(String username, Integer productoId, int cantidadAnadir) {
        if (cantidadAnadir <= 0) {
            throw new IllegalArgumentException("La cantidad a añadir debe ser positiva.");
        }
        Carrito carrito = getOrCreateCarritoDelUsuario(username);
        Producto producto = productoRepositorio.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));

        // Validar stock
        if (producto.getStock() < cantidadAnadir) {
            throw new RuntimeException("No hay suficiente stock para el producto: " + producto.getNombre() + ". Stock disponible: " + producto.getStock());
        }

        // Buscar si el ítem ya existe en el carrito
        Optional<CarritoItem> itemExistenteOpt = carrito.getItems().stream()
                .filter(item -> item.getProducto().getId().equals(productoId))
                .findFirst();

        if (itemExistenteOpt.isPresent()) {
            CarritoItem itemExistente = itemExistenteOpt.get();
            int nuevaCantidad = itemExistente.getCantidad() + cantidadAnadir;
            if (producto.getStock() < nuevaCantidad) { // Re-validar stock total en carrito
                throw new RuntimeException("No hay suficiente stock para la cantidad total solicitada de: " + producto.getNombre());
            }
            itemExistente.setCantidad(nuevaCantidad);
            carritoItemRepositorio.save(itemExistente);
        } else {
            CarritoItem nuevoItem = new CarritoItem(carrito, producto, cantidadAnadir);
            carrito.addItem(nuevoItem); // El método addItem en Carrito ya establece la relación bidireccional
            // carritoItemRepositorio.save(nuevoItem); // Se guarda por cascada desde Carrito
        }
        carritoRepositorio.save(carrito); // Guardar el carrito actualizará/creará ítems por CascadeType.ALL
    }

// En CarritoServicio.java
// En CarritoServicio.java

    @Transactional
    public void removerItem(String username, Integer productoIdARemover) {
        System.out.println("[SERVICE] Intentando remover item. Usuario: " + username + ", Producto ID a remover: " + productoIdARemover);
        Carrito carrito = getOrCreateCarritoDelUsuario(username);

        if (carrito.getItems().isEmpty()) {
            System.out.println("[SERVICE] El carrito está vacío, nada que remover.");
            return;
        }

        // Log para ver los items actuales (puedes mantener tus logs anteriores si prefieres)
        System.out.println("[SERVICE] Items actuales en el carrito (" + carrito.getItems().size() + ") antes de remover:");
        carrito.getItems().forEach(item ->
                System.out.println("  - CI_ID: " + item.getId() + ", P_ID: " + (item.getProducto() != null ? item.getProducto().getId() : "null"))
        );

        Optional<CarritoItem> itemARemoverOpt = carrito.getItems().stream()
                .filter(item -> item.getProducto() != null && item.getProducto().getId().equals(productoIdARemover))
                .findFirst();

        if (itemARemoverOpt.isPresent()) {
            CarritoItem itemEntity = itemARemoverOpt.get();
            System.out.println("[SERVICE] Item encontrado para remover. CarritoItem ID: " + itemEntity.getId() + ", Producto ID: " + itemEntity.getProducto().getId());

            // **Este es el cambio principal: Modificar la colección y guardar el padre**
            boolean fueRemovidoDeColeccion = carrito.getItems().remove(itemEntity);

            if (fueRemovidoDeColeccion) {
                // Opcional pero buena práctica: romper la relación desde el lado del hijo también si es bidireccional
                // y no quieres depender enteramente de orphanRemoval para limpiar referencias en memoria.
                // itemEntity.setCarrito(null); // Si la relación es bidireccional y JPA no lo maneja automáticamente al remover.

                System.out.println("[SERVICE] Item removido de la colección en memoria. Guardando Carrito para persistir cambios...");
                carritoRepositorio.saveAndFlush(carrito); // Guardar el padre (Carrito) debería activar orphanRemoval para el CarritoItem
                System.out.println("[SERVICE] Carrito guardado y flusheado. El ítem debería estar eliminado de la BD.");
            } else {
                System.err.println("[SERVICE] FALLO: El item NO pudo ser removido de la colección en memoria del carrito. Esto es inesperado si se encontró previamente.");
                // Si esto ocurre, podría haber un problema con cómo se compara el objeto itemEntity con los de la lista,
                // o la lista fue modificada concurrentemente (muy improbable aquí).
            }

        } else {
            System.err.println("[SERVICE] FALLO: No se encontró el ítem con Producto ID: " + productoIdARemover + " en el carrito del usuario " + username);
        }
    }

    @Transactional
    public void actualizarCantidad(String username, Integer productoId, int nuevaCantidad) {
        Carrito carrito = getOrCreateCarritoDelUsuario(username);
        Producto producto = productoRepositorio.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));

        if (nuevaCantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }

        if (nuevaCantidad == 0) {
            removerItem(username, productoId);
            return;
        }

        // Validar stock
        if (producto.getStock() < nuevaCantidad) {
            throw new RuntimeException("No hay suficiente stock para el producto: " + producto.getNombre() + ". Stock disponible: " + producto.getStock());
        }

        Optional<CarritoItem> itemExistenteOpt = carrito.getItems().stream()
                .filter(item -> item.getProducto().getId().equals(productoId))
                .findFirst();

        if (itemExistenteOpt.isPresent()) {
            CarritoItem itemExistente = itemExistenteOpt.get();
            itemExistente.setCantidad(nuevaCantidad);
            carritoItemRepositorio.save(itemExistente);
        } else if (nuevaCantidad > 0) { // Si no existe y la cantidad es positiva, lo agregamos
            CarritoItem nuevoItem = new CarritoItem(carrito, producto, nuevaCantidad);
            carrito.addItem(nuevoItem);
            // carritoItemRepositorio.save(nuevoItem); // Se guarda por cascada
            carritoRepositorio.save(carrito);
        }
        // Si no existe y la cantidad es 0, no hacemos nada.
    }

    @Transactional(readOnly = true)
    public List<CarritoItem> getItems(String username) {
        // Manejar el caso de usuario no encontrado o sin carrito si es necesario
        Usuario usuario = usuarioRepositorio.findByUsername(username);
        if (usuario == null) return new ArrayList<>(); // O lanzar excepción

        Optional<Carrito> carritoOpt = carritoRepositorio.findByUsuarioId(usuario.getId());
        return carritoOpt.map(Carrito::getItems).orElseGet(ArrayList::new);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotal(String username) {
        List<CarritoItem> items = getItems(username);
        return items.stream()
                .map(CarritoItem::getSubtotal) // Usamos el método getSubtotal de la entidad CarritoItem
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public int getNumeroDeItems(String username) {
        List<CarritoItem> items = getItems(username);
        return items.stream()
                .mapToInt(CarritoItem::getCantidad)
                .sum();
    }

    @Transactional
    public void limpiarCarrito(String username) {
        Carrito carrito = getOrCreateCarritoDelUsuario(username);
        // Opción 1: Confiar en orphanRemoval y cascade.
        // carrito.getItems().clear();
        // carritoRepositorio.save(carrito);

        // Opción 2: Eliminar ítems explícitamente (más seguro)
        if (!carrito.getItems().isEmpty()) {
            carritoItemRepositorio.deleteAllInBatch(new ArrayList<>(carrito.getItems())); // Más eficiente para muchos ítems
            // O iterar y eliminar uno por uno si deleteAllInBatch da problemas con relaciones
            // carrito.getItems().clear(); // Luego limpiar la colección en memoria si es necesario refrescarla.
        }
        // No es necesario guardar el carrito aquí si solo hemos borrado los items
        // y la relación es manejada correctamente.
        // Si usamos carrito.getItems().clear() y luego save(carrito), nos aseguramos.
    }
}