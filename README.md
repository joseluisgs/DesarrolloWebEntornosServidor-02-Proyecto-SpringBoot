# TiendaApiDaw-SpringBoot
Proyecto de una API REST usando Spring Boot.

- [TiendaApiDaw-SpringBoot](#tiendaapidaw-springboot)
- [Proyecto](#proyecto)
- [Problema](#problema)
  - [Elementos del dominio](#elementos-del-dominio)
  - [Diagrama](#diagrama)
  - [Bases de datos:](#bases-de-datos)
  - [UUID vs Auto](#uuid-vs-auto)
  - [Almacenamiento de imágenes](#almacenamiento-de-imágenes)
  - [Manejo de plantillas](#manejo-de-plantillas)
  - [Documentación](#documentación)
  - [Testing](#testing)
  - [Perfiles](#perfiles)
  - [Despliegue](#despliegue)
  - [Postman](#postman)
  - [Endpoints](#endpoints)
    - [Auth](#auth)
    - [Auth](#auth-1)
    - [Categorías](#categorías)
    - [Pedidos](#pedidos)
    - [Productos](#productos)
    - [Storage](#storage)
    - [Users](#users)
  - [Autor](#autor)
    - [Contacto](#contacto)
  - [Licencia de uso](#licencia-de-uso)


![](images/banner.jpg)

# Proyecto
Este proyecto es una muestra de lo que se ha ido viendo en clase siguiendo los pasos del [repositorio del tema](https://github.com/joseluisgs?tab=repositories&q=DesarrolloWebEntornosServidor-02&type=&language=&sort=).

Podrás seguir sus pasos commit a commit o en las ramas indicadas por clase.

# Problema
Vamos a crear una API REST y página web de una tienda de productos
- Tenemos una serie de productos con sus atributos
- Sabemos que todos los productos pertenecen a una categoría
- Hay usuarios en el sistema, que pueden ser clientes o administradores
- Los clientes hacen pedidos
- Los pedidos esta formados por líeneas de pedidos
- Cada línea de pedido está formada por un producto.

## Elementos del dominio
Entidad: Usuario
- Atributos: ID_Usuario (Auto), Nombre, Dirección, Tipo, etc.
- Relaciones: Realiza uno o varios Pedidos, Tiene varios Roles.

Entidad: Pedido
- Atributos: ID_Usuario (BSON_ID), Fecha, Total, etc.
- Relaciones: Es realizado por un Cliente, Está compuesto por una o varias Líneas de pedido. Los datos de un cliente y su dirección, todo como embebido

Entidad: Línea de pedido
- Atributos: ID_Línea, Cantidad, Subtotal, etc.
- Relaciones: Está formada por un Producto

Entidad: Producto
- Atributos: ID_Producto (AUTO), Nombre, Precio, Imagen, etc.
- Relaciones: Pertenece a una Categoría

Entidad: Categoría
- Atributos: ID_Categoría (UUID), Nombre
- Relaciones: Tiene varios Productos

Entidad: Role
- Atributos: ID_Role (AUTO), Nombre
- Relaciones: Tiene varios Usuarios

## Diagrama
Role * <----- * Usuario 1 ----- * Pedido 1 -----> * Línea de Pedido * -----> 1 Producto * ----- Categoría

## Bases de datos:
Se analizará ventajas e inconvenientes para gestionar la información y alternativas.
- BB.DD Relacionales: Para Roles, Usuarios, Categorías y Productos
- BB.DD NoSQL: Pedidos y Lineas de Pedidos

## UUID vs Auto
Se abordará el uso de UUID para identificar las entidades en lugar de usar un ID autoincremental Analizando Pros y contras

## Almacenamiento de imágenes
Se almacenará imágenes para productos.

## Manejo de plantillas
Se usará Thymeleaf para el manejo de plantillas.

## Documentación
Se documentará con Swagger.

## Testing
Se introducirá el testing en distintas capas.

## Perfiles
Tendremos distintos perfiles de desarrollo.

## Despliegue
Desplegaremos en contenedores el servició y sus dependencias: almacenamiento y bases de datos.

## Postman
Se usará Postman como cliente para acceder al servicio.

## Endpoints

### Auth

Aquí tienes las tablas actualizadas con la columna "Otras Salidas" y ejemplos de posibles códigos de estado, excluyendo el "500 Internal Server Error":

### Auth

| Endpoint                               | URL                            | HTTP Verbo | AUTH                      | Descripción                              | HTTP Status Code | Otras Salidas                                        |
|----------------------------------------|--------------------------------|------------|---------------------------|------------------------------------------|------------------|-----------------------------------------------------|
| Registra un usuario                    | `POST /api.version/auth/signup` | POST       | No se requiere autenticación | Registra un nuevo usuario                 | 200 OK           | 400 Bad Request, 409 Conflict, 422 Unprocessable Entity |
| Inicia sesión de un usuario            | `POST /api.version/auth/signin` | POST       | No se requiere autenticación | Inicia sesión de un usuario               | 200 OK           | 400 Bad Request, 401 Unauthorized, 404 Not Found       |

### Categorías

| Endpoint                               | URL                            | HTTP Verbo | AUTH                      | Descripción                              | HTTP Status Code | Otras Salidas                                        |
|----------------------------------------|--------------------------------|------------|---------------------------|------------------------------------------|------------------|-----------------------------------------------------|
| Obtiene todas las categorias            | `GET /api.version/categorias`  | GET        | Requiere autenticación    | Obtiene todas las categorías disponibles | 200 OK           | 401 Unauthorized, 403 Forbidden, 404 Not Found         |
| Obtiene una categoría por su id         | `GET /api.version/categorias/{id}` | GET        | Requiere autenticación    | Obtiene una categoría por su id           | 200 OK           | 401 Unauthorized, 403 Forbidden, 404 Not Found         |
| Crear una categoría                     | `POST /api.version/categorias` | POST       | Requiere autenticación de administrador | Crea una nueva categoría                  | 201 Created      | 401 Unauthorized, 403 Forbidden, 409 Conflict          |

### Pedidos

| Endpoint                        | URL                                | HTTP Verbo | AUTH                                    | Descripción                              | HTTP Status Code | Otras Salidas                                  |
| ------------------------------- | ---------------------------------- | ---------- | --------------------------------------- | ---------------------------------------- | ---------------- | ---------------------------------------------- |
| Obtiene todos los pedidos | `GET /api.version/pedidos` | GET | Requiere ser administrador | Obtiene todos los pedidos disponibles | 200 OK | 401 Unauthorized, 403 Forbidden, 404 Not Found |
| Obtiene un pedido por su id | `GET /api.version/pedidos/{id}` | GET | Requiere ser administrador | Obtiene un pedido por su id | 200 OK | 401 Unauthorized, 403 Forbidden, 404 Not Found |
| Crea un nuevo pedido | `POST /api.version/pedidos` | POST | Requiere ser administrador | Crea un nuevo pedido | 201 Created | 401 Unauthorized, 403 Forbidden, 404 Not Found |
| Actualiza un pedido | `PUT /api.version/pedidos/{id}` | PUT | Requiere ser administrador | Actualiza un pedido existente | 200 OK | 401 Unauthorized, 403 Forbidden, 404 Not Found, 409 Conflict |
| Elimina un pedido | `DELETE /api.version/pedidos/{id}` | DELETE | Requiere ser administrador | Elimina un pedido existente | 204 No Content | 401 Unauthorized, 403 Forbidden, 404 Not Found |


### Productos

| Endpoint                                       | URL                                     | HTTP Verbo | AUTH                      | Descripción                              | HTTP Status Code | Otras Salidas                                        |
|------------------------------------------------|-----------------------------------------|------------|---------------------------|------------------------------------------|------------------|-----------------------------------------------------|
| Obtiene todos los productos                    | `GET /api.version/productos`            | GET        | No requiere autenticación | Obtiene todos los productos disponibles con opciones de filtrado | 200 OK           | 401 Unauthorized, 403 Forbidden, 404 Not Found         |
| Obtiene un producto por su id                  | `GET /api.version/productos/{id}`       | GET        | No requiere autenticación | Obtiene un producto por su id           | 200 OK           | 401 Unauthorized, 403 Forbidden, 404 Not Found         |
| Crear un producto                              | `POST /api.version/productos`           | POST       | Requiere autenticación de administrador | Crea un nuevo producto                  | 201 Created      | 401 Unauthorized, 403 Forbidden, 409 Conflict          |
| Actualiza un producto                          | `PUT /api.version/productos/{id}`       | PUT        | Requiere autenticación de administrador | Actualiza un producto                   | 200 OK           | 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found |
| Actualiza parcialmente un producto             | `PATCH /api.version/productos/{id}`     | PATCH      | Requiere autenticación de administrador | Actualiza parcialmente un producto      | 200 OK           | 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found |
| Borra un producto                              | `DELETE /api.version/productos/{id}`    | DELETE     | Requiere autenticación de administrador | Borra un producto                       | 204 No Content    | 401 Unauthorized, 403 Forbidden, 404 Not Found |
| Actualiza la imagen de un producto             | `PATCH /api.version/productos/imagen/{id}` | PATCH   | Requiere autenticación de administrador | Actualiza la imagen de un producto      | 200 OK           | 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found |

### Storage

| Endpoint                                       | URL                                     | HTTP Verbo | AUTH                      | Descripción                              | HTTP Status Code | Otras Salidas                                        |
|------------------------------------------------|-----------------------------------------|------------|---------------------------|------------------------------------------|------------------|-----------------------------------------------------|
| Obtiene un fichero del sistema de almacenamiento | `GET /storage/{filename:.+}`            | GET        | No requiere autenticación | Obtiene un fichero del sistema de almacenamiento. El parámetro `filename` debe ser el nombre del fichero a obtener | 200 OK           | 401 Unauthorized, 403 Forbidden, 404 Not Found         |

### Users

| Endpoint                                       | URL                                     | HTTP Verbo | AUTH                      | Descripción                              | HTTP Status Code | Otras Salidas                                        |
|------------------------------------------------|-----------------------------------------|------------|---------------------------|------------------------------------------|------------------|-----------------------------------------------------|
| Obtiene todos los usuarios                    | `GET /api/v1/users`            | GET        | Requiere autenticación de administrador | Obtiene todos los usuarios con opciones de filtrado | 200 OK           | 401 Unauthorized, 403 Forbidden, 404 Not Found         |
| Obtiene un usuario por su id                  | `GET /api/v1/users/{id}`       | GET        | Requiere autenticación de administrador | Obtiene un usuario por su id           | 200 OK           | 401 Unauthorized, 403 Forbidden, 404 Not Found         |
| Crear un usuario                              | `POST /api/v1/users`           | POST       | Requiere autenticación de administrador | Crea un nuevo usuario                  | 201 Created      | 401 Unauthorized, 403 Forbidden, 409 Conflict          |
| Actualiza un usuario                          | `PUT /api/v1/users/{id}`       | PUT        | Requiere autenticación de administrador | Actualiza un usuario                   | 200 OK           | 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found |
| Borra un usuario                              | `DELETE /api/v1/users/{id}`    | DELETE     | Requiere autenticación de administrador | Borra un usuario                       | 204 No Content    | 401 Unauthorized, 403 Forbidden, 404 Not Found |
| Obtiene el usuario actual             | `GET /api/v1/users/me/profile` | GET   | Requiere autenticación de usuario | Obtiene el perfil del usuario actual      | 200 OK           | 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found |
| Actualiza el usuario actual             | `PUT /api/v1/users/me/profile` | PUT   | Requiere autenticación de usuario | Actualiza el perfil del usuario actual      | 200 OK           | 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found |
| Borra el usuario actual             | `DELETE /api/v1/users/me/profile` | DELETE   | Requiere autenticación de usuario | Borra el perfil del usuario actual      | 204 No Content           | 401 Unauthorized, 403 Forbidden, 404 Not Found |

## Autor

Codificado con :sparkling_heart: por [José Luis González Sánchez](https://twitter.com/JoseLuisGS_)

[![Twitter](https://img.shields.io/twitter/follow/JoseLuisGS_?style=social)](https://twitter.com/JoseLuisGS_)
[![GitHub](https://img.shields.io/github/followers/joseluisgs?style=social)](https://github.com/joseluisgs)
[![GitHub](https://img.shields.io/github/stars/joseluisgs?style=social)](https://github.com/joseluisgs)

### Contacto

<p>
  Cualquier cosa que necesites házmelo saber por si puedo ayudarte 💬.
</p>
<p>
 <a href="https://joseluisgs.dev" target="_blank">
        <img src="https://joseluisgs.github.io/img/favicon.png" 
    height="30">
    </a>  &nbsp;&nbsp;
    <a href="https://github.com/joseluisgs" target="_blank">
        <img src="https://distreau.com/github.svg" 
    height="30">
    </a> &nbsp;&nbsp;
        <a href="https://twitter.com/JoseLuisGS_" target="_blank">
        <img src="https://i.imgur.com/U4Uiaef.png" 
    height="30">
    </a> &nbsp;&nbsp;
    <a href="https://www.linkedin.com/in/joseluisgonsan" target="_blank">
        <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/LinkedIn_logo_initials.png/768px-LinkedIn_logo_initials.png" 
    height="30">
    </a>  &nbsp;&nbsp;
    <a href="https://g.dev/joseluisgs" target="_blank">
        <img loading="lazy" src="https://googlediscovery.com/wp-content/uploads/google-developers.png" 
    height="30">
    </a>  &nbsp;&nbsp;
<a href="https://www.youtube.com/@joseluisgs" target="_blank">
        <img loading="lazy" src="https://upload.wikimedia.org/wikipedia/commons/e/ef/Youtube_logo.png" 
    height="30">
    </a>  
</p>

## Licencia de uso

Este repositorio y todo su contenido está licenciado bajo licencia **Creative Commons**, si desea saber más, vea
la [LICENSE](https://joseluisgs.dev/docs/license/). Por favor si compartes, usas o modificas este proyecto cita a su
autor, y usa las mismas condiciones para su uso docente, formativo o educativo y no comercial.

<a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Licencia de Creative Commons" style="border-width:0" src="https://i.creativecommons.org/l/by-nc-sa/4.0/88x31.png" /></a><br /><span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">
JoseLuisGS</span>
by <a xmlns:cc="http://creativecommons.org/ns#" href="https://joseluisgs.dev/" property="cc:attributionName" rel="cc:attributionURL">
José Luis González Sánchez</a> is licensed under
a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
Reconocimiento-NoComercial-CompartirIgual 4.0 Internacional License</a>.<br />Creado a partir de la obra
en <a xmlns:dct="http://purl.org/dc/terms/" href="https://github.com/joseluisgs" rel="dct:source">https://github.com/joseluisgs</a>.
