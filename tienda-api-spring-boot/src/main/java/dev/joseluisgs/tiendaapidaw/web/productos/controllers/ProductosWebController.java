package dev.joseluisgs.tiendaapidaw.web.productos.controllers;

import dev.joseluisgs.tiendaapidaw.rest.categorias.models.Categoria;
import dev.joseluisgs.tiendaapidaw.rest.categorias.services.CategoriasService;
import dev.joseluisgs.tiendaapidaw.rest.productos.dto.ProductoCreateRequest;
import dev.joseluisgs.tiendaapidaw.rest.productos.dto.ProductoResponse;
import dev.joseluisgs.tiendaapidaw.rest.productos.dto.ProductoUpdateRequest;
import dev.joseluisgs.tiendaapidaw.rest.productos.services.ProductosService;
import dev.joseluisgs.tiendaapidaw.web.productos.store.UserStore;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
@Slf4j
public class ProductosWebController {
    private final ProductosService productosService;
    private final CategoriasService categoriasService;
    private final MessageSource messageSource;
    private final UserStore userSession;

    @Autowired
    public ProductosWebController(ProductosService productosService, CategoriasService categoriasService, MessageSource messageSource, UserStore userSession) {
        this.productosService = productosService;
        this.categoriasService = categoriasService;
        this.messageSource = messageSource;
        this.userSession = userSession;
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        log.info("Login GET");
        if (isLoggedAndSessionIsActive(session)) {
            log.info("Si está logueado volvemos al index");
            return "redirect:/productos";
        }
        return "productos/login";
    }

    @PostMapping
    public String login(@RequestParam("password") String password, HttpSession session, Model model) {
        log.info("Login POST");
        if ("pass".equals(password)) {
            // Si es correcto, creamos la sesión
            userSession.setLastLogin(new Date());
            userSession.setLogged(true);
            session.setAttribute("userSession", userSession);
            // Establece el tiempo de caducidad de la sesión en 1800 segundos (30 minutos)
            session.setMaxInactiveInterval(1800);
            return "redirect:/productos";
        } else {
            return "productos/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        log.info("Logout GET");
        // Eliminamos la sesión
        session.invalidate();
        return "redirect:/productos";
    }

    @GetMapping(path = {"", "/", "/index", "/list"})
    public String index(HttpSession session,
                        Model model,
                        @RequestParam(value = "search", required = false) Optional<String> search,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "asc") String direction,
                        Locale locale
    ) {

        // Comprobamos si está logueado
        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/productos/login";
        }

        log.info("Index GET con parámetros search: " + search + ", page: " + page + ", size: " + size);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        // Creamos cómo va a ser la paginación
        Pageable pageable = PageRequest.of(page, size, sort);
        // Obtenemos la página de productos
        var productosPage = productosService.findAll(search, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        // Mensaje de bienvenida o de encabezado -- Localización
        String welcomeMessage = messageSource.getMessage("welcome.message", null, locale);

        // Obtenemos la sesión y actualizamos los datos
        UserStore sessionData = (UserStore) session.getAttribute("userSession");
        sessionData.incrementLoginCount();
        var numVisitas = sessionData.getLoginCount();
        var lastLogin = sessionData.getLastLogin();
        var localizedLastLoginDate = getLocalizedDate(lastLogin, locale);
        // Devolvemos la página
        model.addAttribute("productosPage", productosPage);
        model.addAttribute("search", search.orElse(""));
        model.addAttribute("welcomeMessage", welcomeMessage);
        model.addAttribute("numVisitas", numVisitas);
        model.addAttribute("lastLoginDate", localizedLastLoginDate);
        return "productos/index";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Long id, Model model, HttpSession session) {
        log.info("Details GET");

        // Comprobamos si está logueado
        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/productos/login";
        }

        ProductoResponse producto = productosService.findById(id);
        model.addAttribute("producto", producto);
        return "productos/details";
    }

    @GetMapping("/create")
    public String createForm(Model model, HttpSession session) {
        log.info("Create GET");

        // Comprobamos si está logueado
        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/productos/login";
        }

        var categorias = categoriasService.findAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 1000))
                .get()
                .map(Categoria::getNombre);
        var producto = ProductoCreateRequest.builder()
                .imagen("https://via.placeholder.com/150")
                .precio(0.0)
                .stock(0)
                .build();
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categorias);
        return "productos/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("producto") ProductoCreateRequest productoDto,
                         BindingResult result,
                         Model model) {
        log.info("Create POST");
        if (result.hasErrors()) {
            var categorias = categoriasService.findAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 1000))
                    .get()
                    .map(Categoria::getNombre);
            model.addAttribute("categorias", categorias);
            return "productos/create";
        }
        // Salvamos el producto
        var producto = productosService.save(productoDto);
        return "redirect:/productos";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        // Comprobamos si está logueado
        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/productos/login";
        }

        var categorias = categoriasService.findAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 1000))
                .get()
                .map(Categoria::getNombre);
        ProductoResponse producto = productosService.findById(id);
        ProductoUpdateRequest productoUpdateRequest = ProductoUpdateRequest.builder()
                .marca(producto.getMarca())
                .modelo(producto.getModelo())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .imagen(producto.getImagen())
                .categoria(producto.getCategoria())
                .stock(producto.getStock())
                .isDeleted(producto.getIsDeleted())
                .build();
        model.addAttribute("producto", productoUpdateRequest);
        model.addAttribute("categorias", categorias);
        return "productos/update";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable("id") Long id, @Valid @ModelAttribute("producto") ProductoUpdateRequest productoUpdateRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            var categorias = categoriasService.findAll(Optional.empty(), Optional.empty(), PageRequest.of(0, 1000))
                    .get()
                    .map(Categoria::getNombre);
            model.addAttribute("categorias", categorias);
            return "productos/update";
        }
        log.info("Update POST");
        System.out.println(id);
        System.out.println(productoUpdateRequest);
        var res = productosService.update(id, productoUpdateRequest);
        System.out.println(res);
        return "redirect:/productos";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpSession session) {

        // Comprobamos si está logueado
        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/productos/login";
        }

        productosService.deleteById(id);
        return "redirect:/productos";
    }

    @GetMapping("/update-image/{id}")
    public String showUpdateImageForm(@PathVariable("id") Long productId, Model model, HttpSession session) {

        // Comprobamos si está logueado
        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/productos/login";
        }

        ProductoResponse producto = productosService.findById(productId);
        model.addAttribute("producto", producto);
        return "productos/update-image";
    }

    @PostMapping("/update-image/{id}")
    public String updateProductImage(@PathVariable("id") Long productId, @RequestParam("imagen") MultipartFile imagen) {
        log.info("Update POST con imagen");
        productosService.updateImage(productId, imagen, true);
        return "redirect:/productos";
    }

    private String getLocalizedDate(Date date, Locale locale) {
        // Convertir la fecha a LocalDateTime
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        // Obtener el formatter localizado para el Locale correspondiente a España
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withLocale(locale);
        // Formatear la fecha y la hora localizadas
        return localDateTime.format(formatter);
    }

    private boolean isLoggedAndSessionIsActive(HttpSession session) {
        log.info("Comprobando si está logueado");
        UserStore sessionData = (UserStore) session.getAttribute("userSession");
        return sessionData != null && sessionData.isLogged();
    }
}