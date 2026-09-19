# SOLGASES APP — División por MVPs

## 1. Estrategia

El proyecto se divide en incrementos funcionales para evitar intentar construir toda la solución en una sola etapa.

El **MVP 1 será exclusivamente backend**, de acuerdo con el alcance actual del curso y con el hecho de que `lineamientos.md` define Java 25, Spring Boot 4.1.1, Maven, JPA, MySQL, REST, JUnit, Bean Validation, OpenAPI y Postman como base técnica. fileciteturn0file0L10-L20

Los MVP posteriores incorporan progresivamente frontend, experiencia de cliente, chatbot e integración con WhatsApp.

---

# MVP 1 — Backend de gestión de catálogo e inventario

## Objetivo

Construir la primera versión funcional del backend de SOLGASES, proporcionando APIs REST para administrar usuarios, roles, catálogo y operaciones básicas de inventario.

## Incluye

### 1. Base del proyecto

- Java 25.
- Spring Boot 4.1.1.
- Maven 3.9+.
- MySQL.
- Spring Data JPA.
- Bean Validation.
- JUnit 5.
- OpenAPI/Swagger.
- Postman.

### 2. Arquitectura

Organización por feature y capas internas:

```text
src/main/java
└── com.solgases
    ├── user
    │   ├── controller
    │   ├── service
    │   ├── repository
    │   ├── entity
    │   └── dto
    ├── role
    ├── product
    ├── category
    └── inventory
```

La estructura debe ajustarse a los nombres definitivos del proyecto.

### 3. Usuarios

- Crear.
- Consultar.
- Actualizar.
- Activar/desactivar.
- Asociar roles.

### 4. Roles y permisos

- Crear/consultar roles.
- Definir permisos según el alcance que valide el negocio.
- Aplicar autorización donde corresponda.

### 5. Categorías

- Crear.
- Consultar.
- Actualizar.
- Activar/desactivar.

### 6. Productos

- Crear.
- Consultar.
- Actualizar.
- Activar/desactivar.
- Buscar.
- Filtrar.
- Consultar por categoría.

### 7. Inventario

- Consultar existencia.
- Registrar entrada.
- Registrar salida.
- Registrar ajuste.
- Consultar movimientos.
- Registrar usuario responsable.
- Evitar existencias negativas si el negocio confirma esta regla.

### 8. Validación y errores

- Bean Validation.
- Respuestas HTTP consistentes.
- Manejo centralizado de errores.
- Validación de reglas de negocio.

### 9. Documentación

- Swagger/OpenAPI.
- Descripción de endpoints.
- Parámetros.
- Requests.
- Responses.
- Códigos de error.

### 10. Pruebas

- Pruebas unitarias de servicios.
- Pruebas de validaciones.
- Pruebas de reglas críticas de inventario.
- Pruebas de integración solo donde sean necesarias.

### 11. Postman

Generar colección basada en OpenAPI y organizada por feature, tal como establecen los lineamientos. fileciteturn0file0L110-L119

## No incluye

- Frontend.
- Chatbot.
- Integración con WhatsApp.
- Aplicación móvil.
- Despliegue productivo.
- Integración con proveedores externos no definida.

## Resultado esperado

Un backend ejecutable y documentado que permita probar la operación fundamental de catálogo e inventario sin necesidad de interfaz gráfica.

---

# MVP 2 — Frontend administrativo

## Objetivo

Construir una interfaz web para que los usuarios internos utilicen el backend del MVP 1.

## Incluye

- Login.
- Gestión de usuarios.
- Gestión de roles.
- Gestión de categorías.
- Gestión de productos.
- Consulta de inventario.
- Registro de movimientos.
- Dashboard administrativo básico.
- Manejo de errores del backend.
- Control de acceso según rol.

## Depende de

- MVP 1 terminado.
- APIs REST estables.
- Definición definitiva de roles y permisos.

## No incluye

- Chatbot.
- WhatsApp.
- Recomendaciones inteligentes.

---

# MVP 3 — Catálogo público para clientes

## Objetivo

Permitir que clientes consulten el catálogo de SOLGASES desde una interfaz web.

## Incluye

- Página de catálogo.
- Categorías.
- Búsqueda.
- Filtros.
- Detalle de producto.
- Estado/disponibilidad según reglas del backend.
- Información básica de contacto.
- Botón de WhatsApp.

## WhatsApp

En este MVP se puede implementar inicialmente el botón de redirección al canal de WhatsApp de SOLGASES.

Esto debe distinguirse de una integración programática con la API de WhatsApp.

## No incluye

- Chatbot avanzado.
- Gestión completa de conversaciones.
- Recomendaciones basadas en IA.

---

# MVP 4 — Chatbot de recomendaciones

## Objetivo

Incorporar un asistente capaz de comprender consultas de clientes y recomendar productos existentes en el catálogo.

## Ejemplos de interacción

Un cliente podría preguntar:

> Necesito elementos de protección para trabajo industrial.

El asistente debería utilizar la información disponible en el catálogo para orientar la búsqueda.

Otro ejemplo:

> Busco botas de seguridad.

El sistema debería presentar alternativas existentes y disponibles según la información que tenga autorizada.

## Incluye

- Interfaz de conversación.
- Integración con proveedor/modelo de IA.
- Consulta del catálogo.
- Recuperación de información relevante.
- Recomendaciones fundamentadas en datos del sistema.
- Manejo de consultas sin resultados.
- Protección contra respuestas que inventen productos o características.

## Recomendación arquitectónica

Mantener el chatbot desacoplado del dominio principal de inventario.

Una posible separación futura:

```text
chatbot
├── controller
├── service
├── dto
└── integration
```

El módulo de IA debería consultar información mediante servicios controlados, en lugar de acceder directamente a las entidades JPA.

## No incluye

- Automatización completa de ventas.
- Facturación.
- Pagos.
- Promesas de disponibilidad que no estén respaldadas por el sistema.

---

# MVP 5 — Integración avanzada con WhatsApp

## Objetivo

Evolucionar desde el simple botón de contacto hacia una integración programática, si el negocio realmente la necesita.

## Posibles capacidades

- Recepción de mensajes.
- Envío de respuestas.
- Identificación de conversaciones.
- Atención automática inicial.
- Consulta de productos.
- Derivación a un usuario humano.
- Integración con el chatbot.

## Decisión pendiente

Primero se debe validar si SOLGASES necesita solamente redirección hacia WhatsApp o una integración mediante API.

---

# MVP 6 — Gestión avanzada de operación

## Objetivo

Extender la plataforma más allá del inventario básico.

## Posibles módulos

- Clientes.
- Proveedores.
- Servicios.
- Órdenes de servicio.
- Recarga de extintores.
- Reparación de equipos de soldadura.
- Historial de servicios.
- Seguimiento de estados.
- Reportes.
- Auditoría.

## Importante

Este MVP no debe implementarse hasta levantar los procesos reales de SOLGASES. Los módulos de servicios son candidatos funcionales, no requisitos completamente definidos.

---

# MVP 7 — Analítica y operación avanzada

## Objetivo

Convertir los datos operativos en información para apoyar la gestión empresarial.

## Posibles capacidades

- Dashboard de inventario.
- Productos con baja existencia.
- Historial de movimientos.
- Productos de mayor rotación.
- Reportes.
- Indicadores.
- Alertas.
- Exportación de información.

El alcance concreto deberá definirse con base en las necesidades del negocio.

---

# Dependencias generales

```text
MVP 1
Backend
  │
  ▼
MVP 2
Frontend administrativo
  │
  ├──────────────► MVP 3
  │                Catálogo público
  │                    │
  │                    ▼
  │                MVP 4
  │                Chatbot
  │                    │
  │                    ▼
  │                MVP 5
  │                WhatsApp avanzado
  │
  └──────────────► MVP 6
                   Operación avanzada
                        │
                        ▼
                   MVP 7
                   Analítica
```

Los MVP 3 y 6 pueden avanzar en paralelo después de que el backend del MVP 1 proporcione las capacidades necesarias.

---

# Orden recomendado para el proyecto del curso

## Fase 1 — MVP 1

Concentrarse únicamente en:

1. Inicialización del proyecto.
2. Arquitectura.
3. Base de datos.
4. Categorías.
5. Productos.
6. Inventario.
7. Usuarios.
8. Roles/permisos.
9. Validaciones.
10. Manejo de errores.
11. Pruebas.
12. OpenAPI.
13. Postman.

## Fase 2 — MVP 2

Construir la interfaz administrativa consumiendo exclusivamente las APIs del backend.

## Fase 3 — MVP 3

Construir el catálogo público y el primer botón de WhatsApp.

## Fase 4 — MVP 4

Agregar recomendaciones mediante IA.

## Fase 5 — MVP 5

Evaluar e implementar integración programática con WhatsApp si existe una necesidad empresarial real.

## Fase 6 — MVP 6

Incorporar la gestión de servicios y otros procesos operativos.

## Fase 7 — MVP 7

Agregar analítica, reportes y capacidades avanzadas.

---

# Recomendaciones para el MVP 1

## 1. No comenzar por todas las entidades

Para reducir complejidad, el núcleo inicial podría ser:

```text
Category
   │
   ▼
Product
   │
   ▼
InventoryMovement
```

Después incorporar:

```text
User → Role → Permission
```

y finalmente integrar autorización sobre las operaciones.

## 2. Mantener el dominio preparado para crecer

No introducir todavía entidades de chatbot, WhatsApp o recomendaciones en el MVP 1 solo porque aparecen en el alcance futuro.

## 3. Separar catálogo e inventario

Un producto representa qué vende SOLGASES.

El inventario representa cuántas unidades existen y cómo han cambiado las existencias.

Esta separación facilitará la evolución posterior.

## 4. Diseñar pensando en auditoría

Los movimientos de inventario deberían ser trazables.

## 5. Mantener el backend independiente del frontend

El MVP 1 debe ser consumible mediante REST y Postman, sin depender de una interfaz web.

## 6. No adelantar la IA

El chatbot debe construirse cuando el catálogo y sus datos sean suficientemente consistentes. Una IA no debería compensar datos de negocio incompletos.

---

# Decisiones que deben cerrarse antes de ampliar el sistema

1. Roles reales de SOLGASES.
2. Permisos de cada rol.
3. Estructura definitiva del catálogo.
4. Atributos específicos por tipo de producto.
5. Reglas de inventario.
6. Manejo de productos sin stock.
7. Manejo de gases industriales.
8. Necesidad de lotes, vencimientos o seriales.
9. Política de precios.
10. Gestión de clientes.
11. Gestión de proveedores.
12. Gestión de servicios.
13. Requerimientos de autenticación.
14. Alcance real de WhatsApp.
15. Proveedor/modelo de IA.
16. Información autorizada para el chatbot.

---

# Relación con `lineamientos.md`

El roadmap debe conservar las reglas técnicas actuales: Java 25, Spring Boot 4.1.1, Spring Data JPA, MySQL, REST, JUnit 5, Bean Validation, OpenAPI y Postman. fileciteturn0file0L10-L20

También debe respetar la organización por features, el uso de DTOs, constructor injection, separación de responsabilidades y acceso a persistencia mediante repositories. fileciteturn0file0L30-L44 fileciteturn0file0L58-L65 fileciteturn0file0L77-L82

## Recomendaciones adicionales

Como evolución de `lineamientos.md`, sería conveniente definir posteriormente:

- estándar de errores REST;
- convenciones de nombres de endpoints;
- paginación y filtrado;
- estrategia de migraciones de base de datos;
- estrategia de autenticación/autorización;
- auditoría;
- transacciones;
- versionado de API;
- estándares de calidad y análisis estático.

Estas recomendaciones no deben incorporarse como requisitos obligatorios del MVP 1 hasta que sean aprobadas por el docente o por el responsable del proyecto.
