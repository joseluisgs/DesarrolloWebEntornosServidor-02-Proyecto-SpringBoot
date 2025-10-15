📝 Consultas Básicas
1. Obtener todos los productos (básico)
GraphQL
```graphql
query {
  productos {
    id
    marca
    modelo
    precio
  }
}
```
2. Obtener todos los productos con su categoría
GraphQL
```graphql
query {
  productos {
    id
    marca
    modelo
    descripcion
    precio
    stock
    categoria {
      id
      nombre
    }
  }
}
```
3. Obtener un producto específico por ID
GraphQL
```graphql
query {
  productoById(id: "1") {
    id
    marca
    modelo
    descripcion
    precio
    imagen
    stock
    uuid
    createdAt
    updatedAt
    isDeleted
    categoria {
      id
      nombre
    }
  }
}
```
📂 Consultas de Categorías
4. Obtener todas las categorías
GraphQL
```graphql
query {
  categorias {
    id
    nombre
    createdAt
    updatedAt
  }
}
```
5. Obtener una categoría con todos sus productos
GraphQL
```graphql
query {
  categoriaById(id: "TU_UUID_AQUI") {
    id
    nombre
    productos {
      id
      marca
      modelo
      precio
      stock
    }
  }
}
```
6. Obtener categorías con conteo de productos
GraphQL
query {
  categorias {
    id
    nombre
    productos {
      id
      marca
    }
  }
}
🔗 Consultas con Relaciones Anidadas
7. Productos → Categoría → Otros productos de la misma categoría
GraphQL
query {
  productos {
    id
    marca
    modelo
    categoria {
      nombre
      productos {
        id
        marca
        modelo
      }
    }
  }
}
8. Consulta selectiva (solo campos que necesitas)
GraphQL
```graphql
query {
  productos {
    marca
    precio
    categoria {
      nombre
    }
  }
}
```
🎯 Consultas Avanzadas
9. Obtener productos con información completa
GraphQL
```graphql
query {
  productos {
    id
    marca
    modelo
    descripcion
    precio
    imagen
    stock
    uuid
    createdAt
    updatedAt
    isDeleted
    categoria {
      id
      nombre
      createdAt
      updatedAt
      isDeleted
    }
  }
}
```
10. Consulta con alias (para evitar conflictos)
GraphQL
````graphql
query {
  todosLosProductos: productos {
    id
    marca
    modelo
  }
  todasLasCategorias: categorias {
    id
    nombre
  }
}
````
🧪 Para probar las relaciones
11. Ver si las relaciones funcionan correctamente
GraphQL
query {
  categorias {
    nombre
    productos {
      marca
      modelo
      categoria {
        nombre
      }
    }
  }
}
```