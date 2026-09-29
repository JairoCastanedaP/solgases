# SOLGASES APP — MVP 1: Backend

## 1. Propósito

El MVP 1 constituye la primera etapa de implementación de SOLGASES APP.

Su objetivo es construir un backend funcional, documentado y probado que establezca la base técnica y de negocio sobre la cual podrán desarrollarse posteriormente el frontend administrativo, el catálogo público, el chatbot, WhatsApp y otras capacidades.

El MVP 1 será **exclusivamente backend**.

---

## 2. Documentos de referencia

Antes de implementar cualquier funcionalidad se deberán consultar:

1. `docs/lineamientos.md`
2. `docs/analisis_requerimientos_solgases.md`
3. `docs/roadmap_mvps_solgases.md`
4. `docs/mvp1.md`

Los documentos tienen responsabilidades diferentes:

- `lineamientos.md`: reglas técnicas.
- `analisis_requerimientos_solgases.md`: necesidades del negocio.
- `roadmap_mvps_solgases.md`: evolución completa del producto.
- `mvp1.md`: alcance específico de esta etapa.

---

## 3. Objetivo del MVP

Construir una API REST que permita establecer las capacidades fundamentales para la administración del catálogo e inventario de SOLGASES.

Como parte del MVP se contemplan también las bases para administrar usuarios, roles y permisos.

---

## 4. Alcance

### 4.1 Base técnica

El MVP deberá respetar el stack definido en `lineamientos.md`:

- Java 25.
- Spring Boot 4.1.1.
- Spring Framework 7.
- Jakarta EE.
- Maven 3.9+.
- Spring Data JPA.
- MySQL.
- REST APIs.
- JUnit 5.
- Bean Validation.
- Swagger/OpenAPI mediante springdoc-openapi.
- Postman.

No se deberán introducir frameworks adicionales sin justificación y autorización.

### 4.2 Arquitectura

Se utilizará la estructura estándar de Maven.

La organización del código deberá ser por feature, manteniendo dentro de cada feature las capas que sean necesarias:

- `controller`
- `service`
- `repository`
- `entity`
- `dto`

La estructura deberá evitar paquetes técnicos globales que mezclen funcionalidades no relacionadas.

### 4.3 Categorías

Se deberá contemplar la administración de categorías de productos.

Operaciones iniciales propuestas:

- Crear.
- Consultar.
- Actualizar.
- Activar/desactivar.

### 4.4 Productos

Se deberá contemplar la administración del catálogo de productos.

Operaciones iniciales:

- Crear.
- Consultar.
- Actualizar.
- Activar/desactivar.
- Listar.
- Buscar.
- Filtrar.
- Consultar por categoría.

Los atributos definitivos deberán basarse en los requisitos validados y no deberán inventarse.

### 4.5 Inventario

Se deberá contemplar el control de existencias.

Operaciones iniciales:

- Consultar existencia.
- Registrar entrada.
- Registrar salida.
- Registrar ajuste.
- Consultar movimientos.

Los movimientos deberán permitir trazabilidad del usuario responsable.

La regla de impedir existencias negativas se considera una propuesta de negocio y deberá confirmarse antes de tratarla como una regla obligatoria.

### 4.6 Usuarios

Se deberá contemplar la administración básica de usuarios internos:

- Crear.
- Consultar.
- Actualizar.
- Activar/desactivar.
- Asociar roles.

Los campos de User para este incremento están definidos en las decisiones aprobadas del Incremento 5. La política de autenticación debe definirse antes de implementar una solución de seguridad definitiva. El cambio de contraseña queda aplazado. Hasta contar con autenticación/autorización, el uso es local de desarrollo.

### 4.7 Roles y permisos

Se deberá preparar el modelo para administrar roles y permisos. Las decisiones aprobadas del Incremento 5 se detallan en su sección y prevalecen sobre esta descripción general.

La granularidad de los permisos se definirá conforme a las operaciones aprobadas; las decisiones del Incremento 5 sobre claves internas, códigos editables, carga inicial idempotente y roles sin permisos están aprobadas y no quedan pendientes.

---

## 5. Fuera de alcance

No se deberán implementar dentro del MVP 1:

- Frontend.
- Catálogo web público.
- Chatbot.
- Integración con IA.
- Integración programática con WhatsApp.
- Aplicación móvil.
- Facturación.
- Pagos.
- Analítica avanzada.
- Reportes avanzados.
- Gestión completa de órdenes de servicio.
- Automatizaciones externas no definidas.

El hecho de que estas funcionalidades aparezcan en el roadmap no significa que deban implementarse en este MVP.

---

## 6. Modelo conceptual inicial

Los conceptos principales candidatos son:

```text
User
Role
Permission

Category
Product

Inventory
InventoryMovement
```

La estructura definitiva de entidades, atributos y relaciones deberá revisarse antes de implementarse.

Una relación conceptual inicial para catálogo e inventario es:

```text
Category
    |
    +---- Product
              |
              +---- Inventory
              |
              +---- InventoryMovement
```

Y para seguridad:

```text
User
  |
  +---- Role
          |
          +---- Permission
```

Estas representaciones son conceptuales y no constituyen todavía un modelo físico definitivo de base de datos.

---

## 7. Reglas de negocio aplicables

Las siguientes reglas proceden del análisis actual y deben distinguirse entre requisitos definidos y propuestas pendientes.

### 7.1 Identificación de productos

Cada producto deberá contar con un identificador único.

### 7.2 Estado de productos

Un producto deberá poder distinguirse entre activo e inactivo.

### 7.3 Trazabilidad de inventario

Un movimiento de inventario deberá registrar como mínimo:

- Producto.
- Tipo de movimiento.
- Cantidad.
- Fecha.
- Usuario responsable.

### 7.4 Integridad de datos

Los datos obligatorios deberán validarse antes de persistirse.

### 7.5 Inventario negativo

Se propone impedir que una operación genere existencias negativas.

**Estado: pendiente de validación con el negocio.**

### 7.6 Separación de catálogo e inventario

La existencia de un producto en el catálogo no deberá interpretarse automáticamente como disponibilidad comercial.

La regla exacta de disponibilidad deberá ser definida.

---

## 8. Reglas de desarrollo

Durante la implementación se deberán respetar los lineamientos técnicos del proyecto.

En particular:

- Constructor injection.
- No field injection.
- DTOs para comunicación REST.
- No exponer entidades JPA directamente.
- Bean Validation.
- Lógica de negocio fuera de controllers.
- Acceso a datos mediante repositories.
- SLF4J para logging.
- Nombres de clases, métodos, variables y paquetes en inglés.
- Código fuente y comentarios técnicos en inglés.

---

## 9. Manejo de errores

Se recomienda establecer desde el comienzo un mecanismo centralizado para errores REST.

Como mínimo se deberá contemplar:

- Errores de validación.
- Recurso no encontrado.
- Conflictos de negocio.
- Errores inesperados.

El formato definitivo de respuesta de error deberá ser acordado antes de convertirlo en estándar del proyecto.

---

## 10. Validación

Las entradas de los endpoints deberán validarse mediante Bean Validation.

La validación deberá cubrir, cuando corresponda:

- Campos obligatorios.
- Longitudes.
- Formatos.
- Valores permitidos.
- Cantidades.
- Reglas específicas de negocio.

---

## 11. Persistencia

La persistencia deberá utilizar:

- Spring Data JPA.
- MySQL.

El acceso a la base de datos deberá realizarse exclusivamente mediante repositories.

Las entidades JPA no deberán ser retornadas directamente desde los endpoints.

---

## 12. API REST

Los endpoints deberán seguir convenciones REST consistentes.

Antes de implementar el conjunto completo se deberá acordar:

- Prefijo de API.
- Convención de nombres.
- Códigos HTTP.
- Paginación.
- Filtrado.
- Ordenamiento.
- Manejo de errores.

Estas convenciones deben mantenerse consistentes durante todo el MVP.

---

## 13. Pruebas

Se deberán crear pruebas unitarias con JUnit 5.

Prioridad inicial:

1. Reglas de negocio.
2. Servicios.
3. Validaciones.
4. Operaciones de inventario.
5. Casos de error.

Las pruebas unitarias deberán mantenerse aisladas de la base de datos y de la red.

Las pruebas de integración se utilizarán únicamente cuando aporten valor.

---

## 14. OpenAPI

Todos los endpoints deberán quedar documentados.

La documentación deberá contemplar:

- Summary.
- Parámetros.
- Request body.
- Responses.
- Códigos de error.

Swagger UI y OpenAPI deberán estar disponibles en los perfiles donde lo establezcan los lineamientos.

---

## 15. Postman

Al finalizar el MVP deberá existir una colección Postman basada en la especificación OpenAPI.

Debe incluir:

- Una carpeta por feature.
- Un request por endpoint.
- Variables para la URL base.
- Ejemplos de requests.
- Headers necesarios.
- Sin credenciales reales.

Los archivos deberán quedar en:

```text
docs/postman/
```

---

# 16. Estrategia de implementación incremental

El MVP 1 no deberá implementarse completo en una sola operación.

Se dividirá en incrementos.

## Incremento 0 — Análisis y arquitectura

Objetivo:

- Revisar documentación.
- Revisar repositorio.
- Identificar estado actual.
- Identificar decisiones pendientes.
- Proponer arquitectura inicial.
- No implementar funcionalidades todavía.

### Checkpoint 0

La implementación solamente comenzará después de revisar y aprobar la propuesta.

---

## Incremento 1 — Bootstrap

Objetivo:

- Crear/configurar proyecto Spring Boot.
- Configurar Maven.
- Configurar Java 25.
- Configurar perfiles.
- Configurar conexión a MySQL.
- Configurar estructura inicial.
- Configurar pruebas básicas.

### Resultado esperado

Aplicación arrancando correctamente y estructura preparada para las features.

### Checkpoint 1

Compilar y ejecutar pruebas antes de continuar.

---

## Incremento 2 — Category

Implementar:

- Entity.
- DTOs.
- Repository.
- Service.
- Controller.
- Validaciones.
- Pruebas.
- Documentación OpenAPI.

### Checkpoint 2

Revisar estructura y comportamiento antes de continuar.

---

## Incremento 3 — Product

Implementar:

- Entity.
- DTOs.
- Repository.
- Service.
- Controller.
- Relación con Category.
- Validaciones.
- Consultas.
- Pruebas.
- OpenAPI.

### Checkpoint 3

Verificar que el modelo de producto sea adecuado antes de comenzar inventario.

---

## Incremento 4 — Inventory

Implementar:

- Consulta de existencia.
- Entradas.
- Salidas.
- Ajustes.
- Movimientos.
- Trazabilidad.
- Reglas transaccionales.
- Pruebas.

### Checkpoint 4

Revisar especialmente las reglas de inventario y transacciones.

---

## Incremento 5 — User / Role / Permission

Implementar gestión administrativa de:

- User.
- Role.
- Permission.
- Relaciones entre usuarios, roles y permisos.
- Operaciones administrativas necesarias dentro del alcance acordado.
- Pruebas.

### Decisiones aprobadas para el Incremento 5

- Cada Role y Permission tendrá una clave interna estable e inmutable, independiente de sus valores editables. Los nombres de roles y los códigos de permisos podrán cambiar sin cambiar esa clave interna ni romper las relaciones.
- La carga inicial de roles y permisos será idempotente: repetirla no deberá duplicar registros ni sobrescribir cambios editables existentes.
- La preparación de esquema (tablas, columnas y relaciones) es distinta de la migración o conservación de datos. En desarrollo, los registros actuales pueden restablecerse y recrearse cuando sea necesario; no se requiere un plan de migración de esos datos de desarrollo.
- Antes de contar con autenticación y autorización, el uso queda limitado al entorno local de desarrollo. No se añadirá un guard basado en perfil técnico para simular o restringir ese alcance.
- El cambio de contraseña queda aplazado.
- No se implementará todavía una auditoría administrativa general. La trazabilidad propia de movimientos de inventario sigue sujeta a las reglas del incremento de inventario.
- Un Role puede existir sin permisos asociados.
- User tendrá `username` único, un nombre para mostrar y estado activo/inactivo. No almacenará contraseña ni ninguna otra credencial en este incremento.
- Un usuario podrá tener varios roles (relación User–Role N:M).
- `InventoryMovement.responsibleUser` se mantiene tal como está; no se modifica ni se relaciona con User en este incremento.
- User, Role y Permission tendrán `createdAt` y `updatedAt` técnicos (mismo patrón que Inventory: `@PrePersist`/`@PreUpdate`, sin Spring Data JPA Auditing), sin que esto implique auditoría administrativa general.
- Todo User nuevo nace con `active = true`; el cliente no puede establecerlo en el `POST`.
- `Role.name` y `Permission.code` serán únicos, aunque siguen siendo editables (distintos de la clave interna `key`).
- El cliente proporciona `key` al crear Role/Permission; `key` no podrá cambiar en la actualización.
- La carga inicial asigna el conjunto de permisos de un Role únicamente en el momento en que ese Role se crea por primera vez. Si el Role ya existe (encontrado por su `key`), el seeder no modifica sus asociaciones existentes, aunque no coincidan con la lista semilla.
- Las consultas de User con roles y permisos anidados se resuelven dentro de una misma transacción de lectura, con una cantidad fija de consultas (no proporcional a la cantidad de usuarios, roles o permisos), evitando N+1.
- `updatedAt` se actualiza también cuando cambian las relaciones de User (roles asignados) o de Role (permisos asignados), no solo cuando cambian sus campos propios.
- No habrá `DELETE` para User, Role ni Permission en este incremento.

La implementación de autenticación/autorización completa queda fuera de este incremento hasta contar con una decisión aprobada específica. No convertir el límite de uso local en una protección de seguridad para entornos desplegados.

### Diseño técnico documentado para ejecución posterior

Este diseño queda preparado para revisión y ejecución posterior. Su documentación no autoriza por sí sola la implementación; esta comenzará únicamente cuando el responsable la solicite explícitamente. No se ha implementado todavía.

**Entidades**

- `User`: `id`, `username` (único, `NOT NULL`), `displayName`, `active` (nace en `true`, el cliente no puede fijarlo en el `POST`), `createdAt`/`updatedAt`. Sin campo de contraseña.
- `Role`: `id`, `key` (único, inmutable, provisto por el cliente al crear, no editable después), `name` (único, editable), `createdAt`/`updatedAt`. Sin `active`.
- `Permission`: `id`, `key` (único, inmutable, provisto por el cliente al crear), `code` (único, editable), `createdAt`/`updatedAt`. Sin `active`.
- `User ↔ Role` y `Role ↔ Permission`: ambas `@ManyToMany` unidireccionales y `LAZY`, con tabla intermedia (`tbl_user_role`, `tbl_role_permission`), sin entidad intermedia propia y sin relación de vuelta.

**DTOs**

- `UserRequest` (única forma para crear y actualizar, como en Category/Product): `username`, `displayName`, `roleIds`.
- `RoleCreateRequest(key, name, permissionIds)` / `RoleUpdateRequest(name, permissionIds)` — `key` solo se acepta al crear.
- `PermissionCreateRequest(key, code)` / `PermissionUpdateRequest(code)` — misma razón.
- `UserResponse`, `RoleResponse`, `PermissionResponse`, sin exponer entidades JPA.

**Endpoints** (sin `/v1`, sin paginación, `ProblemDetail` reutilizando `ResourceNotFoundException`/`ResourceConflictException`):

```
POST/GET/GET{id}/PUT   /api/users            + PATCH activate/deactivate
POST/GET/GET{id}/PUT   /api/roles
POST/GET/GET{id}/PUT   /api/permissions
```

Sin `DELETE` en ninguno. Sin activar/desactivar en Role/Permission (aplazado). `PUT` nunca modifica `active` de `User` ni `key` de `Role`/`Permission`.

**Unicidad:** `username`, `Role.key`, `Role.name`, `Permission.key`, `Permission.code`, todas respaldadas por restricción de base de datos además de verificación previa en el service (mismo patrón `saveAndFlush` + captura de `DataIntegrityViolationException` → 409 ya usado en Category/Product/UnitOfMeasure).

**Carga inicial idempotente:** un componente en `config/` (`ApplicationRunner`), que por cada rol/permiso verifica `existsByKey` antes de insertar. El conjunto de permisos de un Role se asigna únicamente en el instante en que ese Role se crea por primera vez; si el Role ya existe, el seeder no toca sus asociaciones existentes, aunque difieran de la lista semilla. El contenido concreto del catálogo (qué roles, qué permisos, qué asignación inicial) queda pendiente — ver la tabla de aclaraciones.

**N+1 en `UserResponse` con roles y permisos anidados:** dentro de una misma transacción de lectura, en una cantidad fija de consultas: (1) `SELECT u FROM User u LEFT JOIN FETCH u.roles ...`; (2) `SELECT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.id IN (:idsDeRolesDelPaso1)`. Por la identidad de sesión de Hibernate, los objetos `Role` de la consulta 1 quedan completados con sus permisos tras la consulta 2, sin necesidad de una tercera consulta por rol ni por usuario.

**`updatedAt` ante cambios de relación:** un cambio solo en la tabla intermedia no dispara `@PreUpdate` sobre la fila propia de `User`/`Role` (Hibernate no emite `UPDATE` sobre esa fila si ningún campo propio cambió). Para cumplir la decisión aprobada, los métodos que reemplazan el conjunto de roles/permisos deben fijar `updatedAt` explícitamente solo si el conjunto realmente cambia. Si el PUT envía las mismas relaciones, no se modifica `updatedAt`.

El método deberá comparar las claves de las relaciones actuales y recibidas antes de actualizar, por ejemplo:

```java
public void replaceRoles(Set<Role> newRoles) {
    if (sameRoleIds(this.roles, newRoles)) {
        return;
    }
    this.roles.clear();
    this.roles.addAll(newRoles);
    this.updatedAt = Instant.now();
}
```

Análogo en `Role.replacePermissions(...)`. Las operaciones idempotentes de activar/desactivar `User` tampoco modificarán `updatedAt` si el usuario ya tiene el estado solicitado.

**Pruebas previstas:** validación de DTOs; service (Mockito) cubriendo unicidad de `key`/`name`/`code`/`username`, referencias inexistentes (404), conjuntos vacíos de roles/permisos, que `key`/`active` no cambien vía `PUT`, que `updatedAt` cambie cuando realmente cambian campos o relaciones y permanezca igual en operaciones sin cambios; controller (`MockMvc` standalone) para códigos HTTP y `ProblemDetail`; verificación manual contra Docker para el esquema, las FKs de las tablas intermedias y que la carga semilla no duplique al reiniciar la aplicación dos veces.

### Aclaraciones y propuestas pendientes de aprobación

| Tema | Estado / impacto |
|---|---|
| Estado activo/inactivo de Role y Permission | Aplazado explícitamente; no se implementa en este incremento. |
| Contenido del catálogo semilla (roles y permisos concretos) | El mecanismo idempotente de carga (por clave interna) está aprobado y se diseña en este incremento; el contenido real (qué roles y qué permisos existen) sigue pendiente y no debe inventarse. Se necesita del responsable: la lista de roles con su clave interna y nombre, la lista de permisos con su clave interna y código, y la asignación inicial de permisos a cada rol. |

### Checkpoint 5

Revisar el modelo de seguridad.

---

## Incremento 6 — Calidad y documentación

Completar:

- Manejo de errores.
- Validaciones.
- Pruebas faltantes.
- OpenAPI.
- Postman.
- Revisión de logs.
- Revisión de configuración.
- Revisión de secretos.

### Checkpoint 6

Ejecutar compilación y pruebas completas del MVP.

---

## 17. Criterios de aceptación

El MVP 1 podrá considerarse terminado cuando:

- El proyecto compile correctamente.
- La aplicación arranque correctamente.
- La conexión a MySQL funcione.
- Las funcionalidades incluidas estén expuestas mediante REST.
- Las entradas inválidas sean rechazadas.
- Las entidades no se expongan directamente.
- Las reglas de negocio implementadas estén cubiertas por pruebas.
- Los endpoints estén documentados.
- Exista colección Postman.
- No existan secretos en el código.
- Se hayan ejecutado las pruebas relevantes.
- Se hayan documentado problemas pendientes.

No se deberá declarar el MVP terminado basándose únicamente en que el código fue generado.

---

## 18. Decisiones pendientes

Antes o durante la implementación deberán identificarse explícitamente:

- Campos definitivos de Product.
- Categorías iniciales.
- Unidades de medida.
- Política de inventario negativo.
- Lotes.
- Fechas de vencimiento.
- Números de serie.
- Política de precios.
- Autenticación.
- Autorización.
- Detalle adicional de la granularidad de permisos, si resulta necesario para las operaciones aprobadas.
- Formato estándar de errores.
- Convenciones REST.
- Estrategia general de migraciones de esquema, independiente del restablecimiento de datos de desarrollo.
- Auditoría administrativa general para incrementos posteriores; no incluida en el Incremento 5.
- Estrategia transaccional.

Una decisión pendiente no deberá convertirse en requisito por iniciativa de Claude Code. Las decisiones aprobadas para el Incremento 5 en esta sección no deben volver a tratarse como pendientes.

---

## 19. Regla fundamental para Claude Code

Antes de realizar cambios significativos:

1. Inspeccionar el código existente.
2. Revisar los documentos relevantes.
3. Explicar los cambios propuestos.
4. Identificar supuestos.
5. Identificar decisiones pendientes.
6. No inventar requisitos.
7. No implementar funcionalidades fuera del alcance.
8. Esperar autorización cuando la decisión requiera validación.

Después de realizar cambios:

1. Compilar.
2. Ejecutar pruebas relevantes.
3. Reportar archivos creados o modificados.
4. Reportar decisiones tomadas.
5. Reportar problemas pendientes.

---

## 20. Definición de terminado

Una funcionalidad individual no deberá considerarse terminada simplemente porque existe código.

Para cada incremento se buscará:

```text
Código
  +
Validación
  +
Pruebas
  +
Documentación
  +
Compilación
  +
Revisión
```

Solo después del checkpoint correspondiente se continuará con el siguiente incremento.

---

## 21. Fuera de las decisiones del MVP 1

Las funcionalidades futuras del roadmap no deberán incorporarse anticipadamente:

```text
MVP 2 → Frontend administrativo
MVP 3 → Catálogo público
MVP 4 → Chatbot
MVP 5 → WhatsApp avanzado
MVP 6 → Gestión avanzada de servicios
MVP 7 → Analítica
```

El MVP 1 debe concentrarse en establecer una base backend sólida para que esas capacidades puedan construirse posteriormente.
