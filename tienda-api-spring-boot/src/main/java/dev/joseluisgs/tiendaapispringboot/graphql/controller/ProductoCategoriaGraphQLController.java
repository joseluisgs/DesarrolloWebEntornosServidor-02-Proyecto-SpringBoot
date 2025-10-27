package dev.joseluisgs.tiendaapispringboot.graphql.controller;

// Aquí están los Resolvers de GraphQL para Producto y Categoria

/* ¿Qué es un resolver?
 * Un resolver en GraphQL es una función que se encarga de obtener los datos para un campo específico en un esquema GraphQL.
 * Cuando un cliente realiza una consulta, GraphQL utiliza los resolvers para determinar cómo obtener los datos solicitados.
 * Cada campo en un tipo de objeto puede tener su propio resolver, lo que permite una gran flexibilidad en la forma en que se obtienen los datos.
 *
 * En este caso, tenemos resolvers para las entidades Producto y Categoria, que permiten obtener productos, categorías
 * y las relaciones entre ellos (por ejemplo, obtener la categoría de un producto o los productos de una categoría).
 */

import dev.joseluisgs.tiendaapispringboot.rest.categorias.models.Categoria;
import dev.joseluisgs.tiendaapispringboot.rest.categorias.repositories.CategoriasRepository;
import dev.joseluisgs.tiendaapispringboot.rest.productos.models.Producto;
import dev.joseluisgs.tiendaapispringboot.rest.productos.repositories.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ProductoCategoriaGraphQLController {

    private final ProductosRepository productosRepository;
    private final CategoriasRepository categoriasRepository;

    @Autowired
    public ProductoCategoriaGraphQLController(ProductosRepository productosRepository, CategoriasRepository categoriasRepository) {
        this.productosRepository = productosRepository;
        this.categoriasRepository = categoriasRepository;
    }

    // --- QUERIES ---

    @QueryMapping
    public List<Producto> productos() {
        // Devuelve todos los productos como entidades (ojo: no paginado)
        return productosRepository.findAll();
    }

    @QueryMapping
    public Producto productoById(@Argument Long id) {
        // Devuelve un producto por id
        Optional<Producto> productoOpt = productosRepository.findById(id);
        return productoOpt.orElse(null);
    }

    @QueryMapping
    public List<Categoria> categorias() {
        // Devuelve todas las categorías como entidades
        return categoriasRepository.findAll();
    }

    @QueryMapping
    public Categoria categoriaById(@Argument String id) {
        // Devuelve una categoría por UUID (pasado como String)
        try {
            UUID uuid = UUID.fromString(id);
            return categoriasRepository.findById(uuid).orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // --- RESOLVERS RELACIONES ---

    @SchemaMapping(typeName = "Producto", field = "categoria")
    public Categoria categoria(Producto producto) {
        // Devuelve la categoría del producto (ya viene cargada en la entidad)
        return producto.getCategoria();
    }

    @SchemaMapping(typeName = "Categoria", field = "productos")
    public List<Producto> productos(Categoria categoria) {
        // Devuelve los productos de una categoría
        return productosRepository.findByCategoria(categoria);
    }
}