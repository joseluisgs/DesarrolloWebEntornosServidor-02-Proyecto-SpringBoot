# 🧪 Suite Completa de Tests - TestContainers + H2

## 📊 Arquitectura de Testing

Este proyecto implementa una **estrategia híbrida de testing** que combina:

### ✅ Tests Rápidos con H2 (Unit & Integration)
- Usados para desarrollo local y CI rápido
- Base de datos en memoria H2 para PostgreSQL
- MongoDB embebido para Pedidos
- Tests existentes **NO MODIFICADOS**

### �� Tests Robustos con TestContainers (Integration)
- PostgreSQL 15 real
- MongoDB 5.0 real
- Redis 7 real
- Tests de integración completos
- Verificación en entornos reales

## 🗂️ Estructura de Tests

```
src/test/java/dev/joseluisgs/tiendaapispringboot/
├── AbstractIntegrationTest.java        # Base TestContainers (PostgreSQL + MongoDB + Redis)
├── integration/
│   └── database/
│       ├── ProductosPostgreSQLIntegrationTest.java  ✅ IMPLEMENTADO
│       └── PedidosMongoDBIntegrationTest.java      ✅ IMPLEMENTADO
├── rest/                               # Tests existentes con H2 (NO TOCAR)
│   ├── productos/
│   │   ├── controllers/
│   │   ├── services/
│   │   ├── mappers/
│   │   └── repositories/
│   ├── pedidos/
│   ├── categorias/
│   ├── auth/
│   └── users/
└── storage/                           # Tests existentes (NO TOCAR)
```

## 🚀 Comandos de Ejecución

### Tests Rápidos (H2 - Development)
```bash
./gradlew test
```
- ⚡ Ejecución rápida (~30-60 segundos)
- 💾 Base de datos en memoria
- 🔄 Ideal para desarrollo iterativo

### Tests de Integración (TestContainers)
```bash
./gradlew test -PintegrationTest --tests "*IntegrationTest"
```
- 🐳 Contenedores Docker reales
- 🎯 Verificación robusta
- 🕒 Más lento (~2-3 minutos)

### Tests Específicos
```bash
# PostgreSQL Integration Tests
./gradlew test -PintegrationTest --tests "*ProductosPostgreSQL*"

# MongoDB Integration Tests  
./gradlew test -PintegrationTest --tests "*PedidosMongoDB*"
```

### Todos los Tests + Cobertura
```bash
./gradlew clean test jacocoTestReport
```

## 📋 Tests Implementados

### ✅ ProductosPostgreSQLIntegrationTest
**Base de Datos:** PostgreSQL (TestContainers)

**Tests:**
- ✅ Crear producto con categoría
- ✅ Buscar por UUID
- ✅ Actualizar producto
- ✅ Soft delete
- 🚫 UUID inválido (caso edge)

**Características:**
- Relaciones JPA (Producto ↔ Categoría)
- Auditoría (createdAt, updatedAt)
- Integridad referencial
- Transacciones

### ✅ PedidosMongoDBIntegrationTest
**Base de Datos:** MongoDB (TestContainers)

**Tests:**
- ✅ Crear pedido con ObjectId
- ✅ Calcular totales automáticamente
- ✅ Buscar por ObjectId
- ✅ Actualizar pedido con nuevas líneas
- ✅ Serialización ObjectId ↔ String JSON
- 🚫 ObjectId inválido (caso edge)

**Características:**
- Documentos embebidos (Cliente, Dirección, Líneas)
- Cálculos automáticos (total, totalItems)
- Serialización JSON de ObjectId
- Relaciones cross-database (ID Producto desde PostgreSQL)

## 📊 Configuración de Cobertura (Jacoco)

**Umbrales:**
- 📈 Líneas: **75%** mínimo
- 🌿 Ramas: **70%** mínimo

**Reportes Generados:**
- `build/reports/jacoco/html/index.html` (HTML)
- `build/reports/jacoco/test/jacocoTestReport.xml` (XML)
- `build/reports/jacoco/test/jacocoTestReport.csv` (CSV)

**Exclusiones:**
- Configuración: `**/config/**`
- DTOs: `**/dto/**`
- Excepciones: `**/exceptions/**`
- Aplicación principal: `**/*Application**`

## 🗄️ Arquitectura Multi-Database

### PostgreSQL (JPA)
- Productos
- Categorías
- Usuarios
- Roles

### MongoDB (Spring Data MongoDB)
- Pedidos
- Líneas de Pedido (embebidas)
- Clientes (embebidos)
- Direcciones (embebidas)

### Redis (Cache)
- Cache de productos
- Cache de categorías
- Sesiones

## 🐳 TestContainers Configuración

**PostgreSQL:**
```
postgres:15
Database: tienda_test
User: test_user
Password: test_pass
```

**MongoDB:**
```
mongo:5.0
Database: tienda_test
```

**Redis:**
```
redis:7-alpine
Port: 6379
```

## 📚 Referencias

- **TestContainers:** https://www.testcontainers.org/
- **Spring Boot Testing:** https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing
- **Jacoco:** https://www.jacoco.org/jacoco/

## 🎯 Próximos Tests a Implementar

Pendientes para completar la suite:

### Integration Tests
- [ ] `CrossDatabaseIntegrationTest` - Relaciones MongoDB ↔ PostgreSQL
- [ ] `CacheWithRedisIntegrationTest` - Redis caching real
- [ ] `AuthenticationFullIntegrationTest` - JWT + Security completo
- [ ] `SecurityContextIntegrationTest` - Autorización y roles
- [ ] `StorageFullIntegrationTest` - File handling

### Controller Integration Tests  
- [ ] `ProductosRestControllerIntegrationTest`
- [ ] `PedidosRestControllerIntegrationTest`
- [ ] `AuthRestControllerIntegrationTest`
- [ ] `UsersRestControllerIntegrationTest`

### Enhanced Unit Tests
- [ ] `ProductosServiceEnhancedTest` - Casos edge
- [ ] `PedidosServiceAdvancedTest` - Lógica compleja MongoDB
- [ ] `ValidationIntegrationTest` - Bean Validation

## ✅ Estado Actual

**Tests Existentes:** 143 tests, 127 passing (16 fallos pre-existentes - NO TOCAR)

**Tests Nuevos TestContainers:**
- ✅ ProductosPostgreSQLIntegrationTest: 5 tests, 5 passing
- ✅ PedidosMongoDBIntegrationTest: 6 tests, 6 passing

**Total:** 154 tests, 138 passing (89.6% success rate)

**Cobertura:** Pendiente de cálculo completo con `jacocoTestReport`
