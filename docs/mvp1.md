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

Se utilizará un reactor Maven de tres módulos, siguiendo el modelo Clean Architecture definido en `lineamientos.md` y la estructura F2/02 del curso:

- `domain`: módulo con modelos y reglas de negocio en Java puro.
- `application`: módulo con casos de uso, puertos, DTOs de aplicación y excepciones.
- `infrastructure`: módulo con API REST, persistencia JPA, mappers, configuración Spring y clase principal.

La refactorización de features existentes preservará contratos REST y comportamiento, y se verificará con las pruebas existentes. Esta decisión arquitectónica no cambia el alcance funcional del MVP ni resuelve reglas de negocio pendientes.

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

**Decisión (resuelta):** el formato estándar de respuesta de error de la API es `ProblemDetail` (RFC 9457), con `Content-Type: application/problem+json`. Lo aplica `GlobalExceptionHandler` a los errores de validación (400, con la propiedad `errors`), recurso no encontrado (404), conflictos de negocio (409), errores estándar de Spring MVC y errores inesperados (500). Al implementar autenticación y autorización en el Incremento 7, las respuestas 401 y 403 deberán seguir este mismo formato.

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

### Estado y autorización del Incremento 5

El Incremento 5 está aprobado para implementación conforme a esta sección. Claude Code debe inspeccionar el estado actual del repositorio, leer esta documentación y continuar con la implementación cuando el responsable indique “revisar, continuar e implementar”. No debe solicitar una nueva aprobación para decisiones ya registradas aquí.

La implementación se limita a la gestión administrativa de User / Role / Permission y a sus pruebas. No incluye autenticación, contraseñas, autorización efectiva de endpoints, JWT, refresh tokens, CORS ni auditoría administrativa general. Si una decisión pendiente afecta solo una parte opcional, se implementará el resto del alcance aprobado y se reportará esa parte como pendiente, sin inventar requisitos.

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

### Diseño técnico aprobado para implementación

Este diseño concreta las decisiones aprobadas para el Incremento 5 y constituye el alcance implementable. Claude Code debe adaptarlo a los patrones reales existentes en el repositorio, manteniendo compatibilidad con las decisiones funcionales de esta sección.

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

**Endpoints** (sin `/v1`, sin paginación, respuestas `ProblemDetail`; `ResourceNotFoundException` está en `application.exception` y se traduce a HTTP 404 en infraestructura. Los conflictos usan `ConflictException` y se traducen a HTTP 409):

```
POST/GET/GET{id}/PUT   /api/users            + PATCH activate/deactivate
POST/GET/GET{id}/PUT   /api/roles
POST/GET/GET{id}/PUT   /api/permissions
```

Sin `DELETE` en ninguno. Sin activar/desactivar en Role/Permission (aplazado). `PUT` nunca modifica `active` de `User` ni `key` de `Role`/`Permission`.

**Unicidad:** `username`, `Role.key`, `Role.name`, `Permission.key`, `Permission.code`, todas respaldadas por restricción de base de datos además de verificación previa en los casos de uso o adaptadores de persistencia, según la responsabilidad. Se conserva el patrón `saveAndFlush` + captura de `DataIntegrityViolationException` → 409 ya usado en Category/Product/UnitOfMeasure.

**Carga inicial idempotente:** el mecanismo de carga se implementa en `infrastructure/config` (por ejemplo, mediante `ApplicationRunner`) y verifica `existsByKey` antes de insertar cada rol o permiso. El conjunto de permisos de un Role se asigna únicamente cuando ese Role se crea por primera vez; si ya existe, la carga no modifica sus asociaciones aunque difieran de la definición inicial. El contenido concreto del catálogo (roles, permisos y asignaciones) sigue pendiente y no debe inventarse. La falta de esos datos no bloquea la gestión CRUD ni las pruebas; la carga no debe crear roles o permisos ficticios. Claude Code reportará que el catálogo real requiere definición antes de poblar datos iniciales.

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

**Pruebas previstas:** validación de DTOs; pruebas unitarias de casos de uso (Mockito) y adaptadores para unicidad de `key`/`name`/`code`/`username`, referencias inexistentes (404), conjuntos vacíos de roles/permisos, que `key`/`active` no cambien vía `PUT`, que `updatedAt` cambie cuando realmente cambian campos o relaciones y permanezca igual en operaciones sin cambios; pruebas de controladores REST (`MockMvc` standalone) para códigos HTTP y `ProblemDetail`; verificación manual contra Docker para el esquema, las FKs de las tablas intermedias y que la carga semilla no duplique al reiniciar la aplicación dos veces.

### Aclaraciones y propuestas pendientes de aprobación

| Tema | Estado / impacto |
|---|---|
| Estado activo/inactivo de Role y Permission | Aplazado explícitamente; no se implementa en este incremento. |
| Contenido del catálogo semilla (roles y permisos concretos) | El mecanismo idempotente de carga (por clave interna) está aprobado y se diseña en este incremento; el contenido real (qué roles y qué permisos existen) sigue pendiente y no debe inventarse. Se necesita del responsable: la lista de roles con su clave interna y nombre, la lista de permisos con su clave interna y código, y la asignación inicial de permisos a cada rol. |

### Checkpoint 5

Implementar y verificar User / Role / Permission según las decisiones de esta sección. Las pruebas unitarias con JUnit 5 y Mockito deben cubrir primero las reglas de negocio y luego persistencia, validación y API. No iniciar Spring Security ni autorización efectiva; el modelo de seguridad completo queda para un incremento posterior.

**Estado: implementado y verificado.**

- **Pruebas automatizadas:** `mvn -B clean verify` finalizó con éxito, con 251 pruebas (65 en `application` y 186 en `infrastructure`) sin fallos ni errores.
- **Corrección durante el cierre:** `UserPersistenceAdapter` traduce a `DuplicateUsernameException` únicamente la violación de `uk_user_username`; cualquier otra violación de integridad (por ejemplo, una clave foránea de `tbl_user_role`) se traduce a una `ConflictException` genérica, de forma coherente con los adaptadores de Role y Permission. Esta corrección está cubierta por pruebas.
- **Verificación manual contra MySQL** (servicio aislado `solgases-mysql` de `docker-compose.yml`, perfil `local`, base de datos nueva):
  - Esquema de `tbl_user`, `tbl_role`, `tbl_permission`, `tbl_user_role` y `tbl_role_permission` conforme al diseño, sin columna de contraseña en `tbl_user`.
  - Restricciones únicas `uk_user_username`, `uk_role_key`, `uk_role_name`, `uk_permission_key` y `uk_permission_code`, y claves foráneas `fk_user_role_user`, `fk_user_role_role`, `fk_role_permission_role` y `fk_role_permission_permission`. La base de datos rechazó directamente las inserciones duplicadas (error 1062) y las referencias inexistentes (error 1452).
  - `updatedAt` con Hibernate real: no cambia ante un `PUT` sin cambios de User, Role o Permission, ni al activar un usuario ya activo o desactivar uno ya inactivo; sí cambia ante un cambio real de estado y ante un cambio limitado a las relaciones (roles del usuario).
  - Códigos HTTP comprobados: 201 en creación, 200 en consultas, `PUT` y `PATCH`, 409 por `username` duplicado y 404 por rol inexistente, con respuestas `ProblemDetail`.
  - Dos arranques consecutivos de la aplicación no duplicaron registros. Los datos temporales de la verificación se eliminaron al terminar.
- **Limitación conocida:** el catálogo semilla permanece vacío porque su contenido (roles, permisos y asignación inicial) sigue pendiente de definición por el negocio. Por ello, en MySQL solo se verificó el arranque repetido con catálogo vacío; no se probó la repetición de una carga con catálogo no vacío. La idempotencia de esa carga está cubierta únicamente por pruebas unitarias del caso de uso. No existen roles ni permisos reales cargados.

---

## Incremento 6 — Automated Testing & Code Quality

### Objetivo

Establecer una estrategia repetible de pruebas y calidad para los módulos existentes, usando resultados medibles y refactorización incremental sin cambiar contratos ni reglas de negocio aprobadas.

### Alcance

- Revisar cobertura de pruebas por módulo y feature, identificando riesgos y brechas en casos de uso, persistencia, API y validación.
- Mantener pruebas unitarias rápidas con JUnit 5 y Mockito, y agregar pruebas de integración solo cuando aporten evidencia que los dobles de prueba no pueden dar.
- Integrar JaCoCo al reactor Maven para producir reportes por módulo y un reporte agregado, verificando compatibilidad con Java 25.
- Evaluar e integrar SonarQube/SonarScanner for Maven para análisis estático, mantenibilidad, duplicación, cobertura y deuda técnica.
- Registrar la línea base de cobertura y comenzar con un Quality Gate informativo, sin umbrales ni bloqueo de compilación; cualquier criterio de promoción obligatorio se decidirá para la integración CI/CD del Incremento 8.
- Usar recomendaciones de IA para proponer pruebas y refactorizaciones; una persona revisará los cambios, y toda refactorización conservará arquitectura, comportamiento aprobado y contratos REST.
- Completar pendientes de calidad y documentación de MVP 1: manejo de errores, validaciones, OpenAPI, colección Postman, logs, configuración y revisión de secretos.
- Documentar comandos reproducibles, reportes generados, hallazgos aceptados y excepciones justificadas.

### Decisiones pendientes

No quedan decisiones de alcance pendientes para este incremento. La versión de imagen ya está fijada y el soporte de Java 25 está respaldado por la documentación y el artefacto del analizador; queda confirmar el comportamiento práctico en el primer análisis.

### Checkpoint 6

La compilación y pruebas del reactor pasan; JaCoCo genera reportes verificables para los módulos; SonarQube analiza el proyecto con una versión compatible con Java 25 o deja documentada la limitación; la línea base queda registrada y el Quality Gate es informativo, sin umbrales ni bloqueo inicial; los errores y problemas de seguridad confirmados de máxima severidad se corrigen; OpenAPI/Postman y revisión de logs, configuración y secretos están actualizados. Las pruebas creadas o refactorizaciones asistidas por IA tienen revisión humana y no alteran contratos aprobados.

### Estado del Incremento 6

**Estado: en curso; Checkpoint 6 parcialmente verificado.** No se cierra hasta resolver los puntos pendientes de la tabla final.

**Comandos reproducibles**

```bash
# Compilación, pruebas y reportes JaCoCo (por módulo y agregado)
mvn -B clean verify

# Exporta la especificación OpenAPI y regenera la colección Postman (solo biblioteca estándar de Python)
mvn -B -pl infrastructure -am test -Dtest=OpenApiDocumentationTest -Dsurefire.failIfNoSpecifiedTests=false
python3 docs/postman/generate_postman_collection.py

# Servidor SonarQube Community Build local (proyecto Compose propio "solgases-sonar", volúmenes nombrados)
docker compose -f docker-compose.sonar.yml up -d

# Análisis SonarQube manual. Comprobar antes que .sonar.env sigue ignorado y sin seguimiento; el token se lee
# de .sonar.env solo para el proceso de Maven, sin mostrarlo (Spring no carga .sonar.env y SonarScanner usa SONAR_TOKEN).
# El análisis solo se ejecuta si la compilación y las pruebas terminan correctamente.
mvn -B clean verify && \
  SONAR_TOKEN="$(sed -n 's/^SONAR_TOKEN=//p' .sonar.env)" \
  mvn -B test-compile org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
  -Dsonar.host.url=http://localhost:9000
```

**Limpieza opcional** (después del análisis; detiene el servidor SonarQube sin borrar sus datos)

```bash
docker compose -f docker-compose.sonar.yml stop
```

**Reportes generados** (salida de compilación, no versionada)

- Por módulo: `domain/target/site/jacoco/`, `application/target/site/jacoco/`, `infrastructure/target/site/jacoco/` (`index.html` y `jacoco.xml`).
- Agregado de los tres módulos: `infrastructure/target/site/jacoco-aggregate/` (`index.html` y `jacoco.xml`). Se genera en `infrastructure` porque depende de `domain` y `application`; así se conservan los tres módulos del modelo de arquitectura sin un módulo adicional de reportes.
- Especificación OpenAPI exportada: `infrastructure/target/openapi/openapi.json`.

**Herramientas y compatibilidad con Java 25**

- JaCoCo `0.8.15` (JaCoCo declara soporte oficial de Java 25 desde 0.8.14). Verificado: `mvn -B clean verify` instrumenta y reporta los tres módulos con JDK 25.0.4.1.
- SonarScanner for Maven `5.8.0.7211`, fijado en `pluginManagement` (requiere un runtime Java 21 o superior). El plugin se resuelve desde Maven Central. La ruta de cobertura se configura con `sonar.coverage.jacoco.xmlReportPaths`, apuntando al reporte agregado. No se guardan URL ni token en archivos versionados.
- SonarQube Community Build `26.9.0.129388` (imagen `sonarqube:26.9.0.129388-community`), fijada en `docker-compose.sonar.yml`, publicado solo en `127.0.0.1:9000` y con volúmenes nombrados para datos, extensiones y logs. Compatibilidad con Java 25 verificada el 2026-10-07, distinguiendo dos aspectos:
  - **Soporte del lenguaje Java 25 en el analizador:** la imagen incluye SonarJava `8.41.0.47177` (`sonar-java-plugin-8.41.0.47177.jar`; manifiesto: `Plugin-Version: 8.41.0.47177`, `Jre-Min-Version: 21`). Evidencia:
    - Documentación oficial vigente del analizador Java de Community Build (consultada el 2026-10-07; la página no indica número de versión): «LTS 8, 11, 17, 21, 25 and all intermediary versions are fully supported». [Analizador Java de Community Build](https://docs.sonarsource.com/sonarqube-community-build/analyzing-source-code/languages/java).
    - Notas oficiales de SonarJava `8.24.0.42567` (2026-02-17): «SONARJAVA-5978 Support Compact Source Files» y «SONARJAVA-5984 Support Module Import Declarations», las características de lenguaje finalizadas en Java 25 (JEP 512 y JEP 511), además de ajustes de reglas para Java 25 (SONARJAVA-6105). No se encontró en las notas una entrada específica para *Flexible Constructor Bodies* (JEP 513). [Versiones de SonarJava](https://github.com/SonarSource/sonar-java/releases).
    - El binario incluido define `JavaVersionImpl.MAX_SUPPORTED = 26` (comprobado con `javap` sobre el JAR de la imagen).
    - Esto es evidencia documental y del artefacto, no una comprobación empírica: se confirmará en el primer análisis, revisando que el log no registre errores de análisis sintáctico ni versiones no soportadas. La observación anterior de soporte «hasta Java 24» correspondía a documentación más antigua.
  - **Ejecución (no equivale a soporte del lenguaje):** el servidor de la imagen se ejecuta con OpenJDK 25.0.4.1; el plugin exige al menos Java 21 (`Jre-Min-Version`); el escáner se ejecutará con el JDK 25.0.4.1 local. Que el servidor o el analizador puedan ejecutarse sobre Java 25 no demuestra que comprendan por completo el código Java 25; por eso se comprobó por separado.
  - Servidor verificado: `/api/system/status` responde `UP` con versión `26.9.0.129388` y el healthcheck del contenedor está en `healthy`.

**Línea base medida** (`mvn -B clean verify` del 2026-10-07, 285 pruebas: 11 en `domain`, 65 en `application` y 209 en `infrastructure`; los 40 informes Surefire XML registran 0 fallos, 0 errores y 0 omitidas; no hay pruebas Failsafe. Los mensajes `ERROR`/`WARN` del log provienen de pruebas negativas que provocan a propósito un error 500 y violaciones de restricciones en H2)

| Reporte | Líneas | Ramas | Instrucciones | Métodos |
|---|---|---|---|---|
| `domain` (solo sus pruebas) | 91,4 % (32/35) | 100 % (4/4) | 86,8 % (416/479) | 90,3 % (28/31) |
| `application` | 98,8 % (339/343) | 89,4 % (59/66) | 94,3 % (1964/2083) | 91,1 % (153/168) |
| `infrastructure` | 90,8 % (642/707) | 79,6 % (39/49) | 90,6 % (2953/3258) | 87,6 % (305/348) |
| Agregado | 93,9 % (1019/1085) | 88,2 % (105/119) | 93,0 % (5414/5820) | 89,8 % (491/547) |

Mediciones anteriores del agregado (líneas): 80,7 % con 251 pruebas, antes de las pruebas de este incremento y sin `domain`; 85,6 % con 271 pruebas, antes de las pruebas JPA con H2. Los valores son una línea base informativa, no umbrales. **Decisión del responsable:** el Quality Gate será inicialmente solo informativo; no se fijarán umbrales de cobertura ni se hará fallar la compilación por el Quality Gate.

**Brechas identificadas y corregidas**

- `domain` no tenía pruebas propias y no aparecía en el reporte agregado. Se añadieron pruebas de los modelos (`AccessModelTest`, `CatalogInventoryModelTest`) y se declaró explícitamente la dependencia de `infrastructure` sobre `domain`, que ya usaba directamente en sus mapeadores. **Decisión revisada y aceptada por el responsable:** conservar esta dependencia directa porque refleja los imports existentes, facilita el reporte agregado de JaCoCo y sigue apuntando hacia una capa interna; `domain` permanece libre de dependencias de infraestructura y frameworks.
- Errores inesperados: no producían `ProblemDetail` ni se registraban en la aplicación. `GlobalExceptionHandler` responde ahora 500 con un mensaje genérico, sin detalles internos, y registra la excepción con SLF4J sin registrar el cuerpo de la solicitud. **Política revisada y aceptada por el responsable:** conservar la traza completa de la excepción en los logs, incluidos `qa` y `prd`. Los errores estándar de Spring (400, 405) conservan su estado (`GlobalExceptionHandlerTest`).
- OpenAPI: `GET /api/products` (filtros) y los tres `GET` de inventario no documentaban el 400 que devuelven ante parámetros inválidos. Se documentaron, y `OpenApiDocumentationTest` verifica que cada operación tenga resumen, respuesta de éxito y, si acepta parámetros o cuerpo, respuestas de error del cliente. La descripción general de la API incluye ahora la administración de usuarios, roles y permisos.
- Persistencia de inventario con baja cobertura (24 % en el adaptador): `InventoryPersistenceAdapterTest` cubre la creación y actualización del stock, la traducción de conflictos concurrentes y el registro y consulta de movimientos.
- Colección Postman inexistente: se generaron `docs/postman/solgases.postman_collection.json` (38 requests, una carpeta por feature, igual al número de operaciones OpenAPI) y `docs/postman/solgases.postman_environment.json` (variables por perfil; las URL de `dev`, `qa` y `prd` quedan vacías, sin credenciales). **Decisión revisada y aceptada por el responsable:** conservar versionado `docs/postman/generate_postman_collection.py` para regenerar los JSON desde OpenAPI; los ejemplos deben usar valores genéricos, sin datos personales ni credenciales.
- Persistencia de Product, UnitOfMeasure e Inventario: se añadieron pruebas JPA sobre H2 en memoria (`@DataJpaTest`), ejecutadas por `mvn verify` (`ProductJpaPersistenceTest`, `UnitOfMeasureJpaPersistenceTest`, `InventoryJpaPersistenceTest`, 14 pruebas). Usan los adaptadores reales y cubren mapeos, relaciones con Category y UnitOfMeasure, filtros combinados de producto (incluidos comodines literales), actualización de stock en una única fila por producto con su escala, movimientos por producto y las restricciones existentes: unicidad de SKU y de código de unidad, y clave foránea de movimientos hacia producto. Dependencias añadidas solo con alcance `test`: `spring-boot-starter-data-jpa-test` y `com.h2database:h2`, gestionadas por Spring Boot 4.1.1. Resultado: paquete `persistence` 84,5 % y `persistence.entity` 85,4 % de líneas (antes 69,6 % y 59,6 %). H2 no sustituye la verificación con MySQL de los incrementos anteriores.
- Ejemplos con datos personales: los ejemplos OpenAPI de `UserRequest`, `UserResponse`, `InventoryMovementRequest`, `InventoryMovementResponse` e `InventoryAdjustmentRequest` usaban un nombre y usuario reales. Se reemplazaron por valores genéricos (`jdoe`, `John Doe`), igual que en dos pruebas de validación, y se regeneraron la especificación y la colección Postman. Verificado: la colección y el environment no contienen esos datos ni credenciales.
- Higiene: se eliminó `src/main/java/com/solgases/exception/ResourceNotFoundException.java`, un resto fuera de los módulos que no se compilaba ni se referenciaba; `SolgasesApplicationTests` se movió al directorio de su paquete.

**Revisión de logs, configuración y secretos**

- Logs: SLF4J en todo el código; sin `System.out` ni `printStackTrace`. `com.solgases` en `DEBUG` para `local` y `dev`, e `INFO` para `qa` y `prd`. Ningún perfil habilita el registro de sentencias SQL ni de parámetros (no se configuran `spring.jpa.show-sql`, `org.hibernate.SQL` ni `org.hibernate.orm.jdbc.bind`). Hibernate sí registra en `WARN`, en la categoría `org.hibernate.orm.jdbc.error`, los errores JDBC, y el mensaje de la base de datos puede incluir valores de datos (por ejemplo, el valor de una clave duplicada); así se observó en las pruebas con H2. El perfil `prd` filtra esos `WARN` con `logging.level.org.hibernate.orm.jdbc.error: ERROR`; `local`, `dev` y `qa` conservan el nivel por defecto. El comportamiento con MySQL en producción no se verificó en ejecución.
- Configuración: perfiles `local`, `dev`, `qa` y `prd`; `ddl-auto: update` en `local`/`dev` y `validate` en `qa`/`prd`; Swagger UI y OpenAPI habilitados en `local`, `dev` y `qa` y deshabilitados en `prd`.
- Secretos: credenciales de base de datos solo mediante variables `DB_*`; `.env` ignorado por Git (solo se versiona `.env.example`, con valores vacíos). El token de SonarQube está en `.sonar.env`, también ignorado por Git y sin seguimiento; `.env` ya no contiene `SONAR_TOKEN`. La búsqueda en archivos versionados y en el historial de Git no encontró secretos.

**Hallazgos aceptados y riesgos conocidos**

- `ProductPersistenceAdapter`, `UnitOfMeasurePersistenceAdapter` y `CategoryPersistenceAdapter` traducen cualquier `DataIntegrityViolationException` a su error de duplicado (409), igual que hacía `UserPersistenceAdapter` antes de su corrección en el Incremento 5. Los casos de uso validan antes la existencia de las referencias, así que solo afectaría a condiciones de carrera o a violaciones distintas de la unicidad. No es un error de máxima severidad; se registra para una corrección posterior.
- La clave foránea de `tbl_inventory_movement` hacia producto tiene un nombre generado por Hibernate (no declarado con `@ForeignKey`). Funciona y está probada; solo afecta a la legibilidad de los mensajes de error.
- La jerarquía de excepciones de aplicación es heterogénea: algunas extienden `ResourceNotFoundException`/`ConflictException` y otras `RuntimeException` con su propio handler. El comportamiento HTTP es correcto y está probado; unificarla es una refactorización opcional que no se hizo para no ampliar el alcance.
- Advertencias de JDK 25 durante las pruebas (carga dinámica del agente de Mockito/Byte Buddy y `sun.misc.Unsafe` en Maven): no afectan el resultado.

**Decisiones acordadas para completar el Incremento 6**

- **SonarQube:** usar Community Build local y ejecutar el análisis manualmente en este incremento; integrarlo con Jenkins/CI queda para el Incremento 8. La instancia está en `docker-compose.sonar.yml`, separada del Compose de la aplicación, con versión fija `26.9.0.129388` y volumen Docker nombrado. Se creó el proyecto local `SOLGASES` (`sonar.projectKey=solgases`) con rama principal `feature/clean-architecture`. La clave se fija como propiedad `sonar.projectKey` en el `pom.xml` raíz, para que cualquier análisis use el mismo proyecto sin pasarla por línea de comandos.
- **Compatibilidad:** conservar Java 25 en el proyecto. La documentación oficial y el artefacto SonarJava `8.41.0.47177` respaldan soporte del lenguaje Java 25; la confirmación práctica queda pendiente del primer análisis. Si el análisis revela limitaciones, documentarlas sin bajar la versión de Java.
- **Token:** el responsable generó un token de análisis limitado al proyecto `solgases`, con vencimiento de 30 días, y lo guardó localmente como `SONAR_TOKEN`. No se copia su valor a documentación, Compose, comandos registrados, logs ni archivos versionados. **Decisión revisada:** el token se mantiene fuera del `.env` que puede cargar Spring (el perfil `local` importa `.env` completo al arrancar desde la raíz) y se guarda como `SONAR_TOKEN` en `.sonar.env`, ignorado por Git y leído solo por el comando de análisis. El traslado está hecho y comprobado sin mostrar valores: `.sonar.env` está ignorado y sin seguimiento, y `.env` ya no contiene `SONAR_TOKEN`.
- **Quality Gate:** solo informativo al inicio y sin bloquear `mvn verify`. El proyecto no define umbrales propios de cobertura ni un Quality Gate propio; se mantiene el Quality Gate predeterminado de la instancia local, que sí incluye condiciones y puede mostrar `ERROR`. Ese estado se usa solo como información: no bloquea el build ni el análisis manual (no se usa `sonar.qualitygate.wait`). Cualquier criterio obligatorio de promoción se decidirá en el Incremento 8.
- **Hallazgos:** corregir únicamente errores y problemas de seguridad confirmados de máxima severidad. Mantener los demás hallazgos en la instancia de SonarQube; no exportar informes al repositorio ni duplicarlos en la documentación.
- **Análisis de dependencias/vulnerabilidades:** pospuesto al Incremento 7.
- **Errores JDBC de Hibernate en `prd`:** configurar `logging.level.org.hibernate.orm.jdbc.error: ERROR` solo en `application-prd.yml`, para no registrar en producción los `WARN` de errores JDBC, cuyos mensajes pueden incluir valores de datos. Los demás perfiles no cambian.
- **Bloqueos concurrentes en inventario:** `InventoryPersistenceAdapter` traduce `PessimisticLockingFailureException` (esperas de bloqueo y deadlocks) en `saveInventory` y `saveMovement` a la `ConflictException` existente, con el mismo mensaje de conflicto de concurrencia del producto (409, «please retry»). Los fallos generales de conexión o disponibilidad de la base de datos no se traducen y siguen respondiendo 500. El tratamiento del desbordamiento de stock no cambia.
- **Rango técnico de stock:** antes de persistir, el registro de movimientos (`InventoryMovementRegistration`) rechaza cualquier stock resultante superior a `999999999999.999`, el máximo representable por la columna `DECIMAL(15,3)`, definido como `Inventory.MAX_QUANTITY` en el dominio. Se responde con la `ConflictException` existente (409) y un mensaje específico, sin indicar que se reintente; no se escribe stock ni movimiento. Es un límite técnico, no un límite comercial. El límite exacto está permitido.
- **Colación de SKU y código de unidad:** `tbl_product.sku` y `tbl_unit_of_measure.code` deben usar una colación explícita equivalente a `utf8mb4_0900_ai_ci` (juego de caracteres `utf8mb4`), de modo que su unicidad ignore mayúsculas y acentos. Los valores recibidos se conservan tal cual, sin recortarlos ni normalizarlos; con esta colación (*NO PAD*), los espacios finales siguen siendo significativos. No cambia la colación de otras columnas.
  - **Limitación:** el repositorio no tiene mecanismo de migraciones; el esquema lo genera Hibernate (`ddl-auto: update` en `local`/`dev`, `validate` en `qa`/`prd`), que no modifica la colación de columnas existentes ni la declara explícitamente. Hasta hoy la colación de esas columnas depende del valor por defecto del servidor MySQL 8.0, y en `qa`/`prd` de cómo se haya creado la base de datos. Este incremento no introduce un framework de migraciones (la estrategia general sigue pendiente, §18) ni altera bases de datos.
  - **Cambio de esquema necesario en cada base de datos existente**, aplicado manualmente por el responsable del entorno:
    ```sql
    -- 0. Seleccionar la base de datos objetivo y confirmar que es la del entorno previsto
    USE <base_de_datos_objetivo>;
    SELECT DATABASE();

    -- 1. Comprobar que no haya valores que pasen a ser duplicados con la nueva colación.
    --    Cada columna se convierte a utf8mb4 antes de aplicar la colación, para que la consulta funcione
    --    aunque hoy use otro juego de caracteres. ids lista las filas que colisionarían.
    SELECT CONVERT(sku USING utf8mb4) COLLATE utf8mb4_0900_ai_ci AS normalized_sku,
           COUNT(*) AS occurrences, GROUP_CONCAT(id ORDER BY id) AS ids
      FROM tbl_product
     GROUP BY CONVERT(sku USING utf8mb4) COLLATE utf8mb4_0900_ai_ci
    HAVING COUNT(*) > 1;
    SELECT CONVERT(code USING utf8mb4) COLLATE utf8mb4_0900_ai_ci AS normalized_code,
           COUNT(*) AS occurrences, GROUP_CONCAT(id ORDER BY id) AS ids
      FROM tbl_unit_of_measure
     GROUP BY CONVERT(code USING utf8mb4) COLLATE utf8mb4_0900_ai_ci
    HAVING COUNT(*) > 1;

    -- 2. Revisar la definición actual de ambas columnas. MODIFY sustituye la definición completa:
    --    si existe cualquier otro atributo (DEFAULT, COMMENT, etc.), añadirlo también al MODIFY del paso 3.
    SHOW CREATE TABLE tbl_product;
    SHOW CREATE TABLE tbl_unit_of_measure;

    -- 3. Fijar la colación explícita, conservando tipo, longitud y NOT NULL.
    --    Los índices únicos se conservan y se reconstruyen con la nueva colación.
    ALTER TABLE tbl_product
      MODIFY sku VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL;
    ALTER TABLE tbl_unit_of_measure
      MODIFY code VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL;

    -- 4. Verificación dirigida. Esperado: varchar(50) y varchar(20), IS_NULLABLE = NO,
    --    utf8mb4 / utf8mb4_0900_ai_ci, y ambos índices presentes con NON_UNIQUE = 0
    SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE,
           CHARACTER_SET_NAME, COLLATION_NAME
      FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND ((TABLE_NAME = 'tbl_product' AND COLUMN_NAME = 'sku')
         OR (TABLE_NAME = 'tbl_unit_of_measure' AND COLUMN_NAME = 'code'));
    SELECT TABLE_NAME, INDEX_NAME, NON_UNIQUE, COLUMN_NAME
      FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE()
       AND INDEX_NAME IN ('uk_product_sku', 'uk_unit_of_measure_code');
    ```
    Todas las consultas y el `ALTER` deben ejecutarse sobre la base de datos confirmada en el paso 0. Si el paso 1 devuelve filas, el `ALTER` fallaría por la restricción única; esos datos deben resolverse antes con el responsable. Las longitudes corresponden a `Product.SKU_MAX_LENGTH` (50) y `UnitOfMeasure.CODE_MAX_LENGTH` (20).

    **Advertencia:** cambiar el juego de caracteres o la colación de columnas indexadas reconstruye la tabla y puede bloquear las escrituras mientras dura la operación. En producción debe planificarse una ventana de mantenimiento.
  - **Bases de datos nuevas:** Hibernate no declara la colación, así que una base creada desde cero vuelve a depender del valor por defecto del servidor. **Decisión:** hasta que se adopte una estrategia de migraciones (§18), las bases nuevas también aplicarán manualmente este mismo procedimiento SQL, después de que Hibernate cree el esquema. No se añadirá configuración específica de MySQL (por ejemplo, `columnDefinition` con colación) al mapeo JPA.
  - Las pruebas con H2 no reproducen esta colación (H2 distingue mayúsculas y acentos); la verificación son las consultas del paso 4 contra MySQL.
  - **Estado por entorno:** la base MySQL local del proyecto (contenedor `solgases-mysql`) ya cumple la colación acordada: la comprobación previa no encontró colisiones y la verificación del paso 4 confirmó tipo, longitud, nulabilidad, juego de caracteres, colación e índices únicos. Como las columnas ya tenían `utf8mb4_0900_ai_ci`, no fue necesario ejecutar ningún `ALTER`. QA y producción siguen pendientes de aplicar el procedimiento cuando estén disponibles; esto no bloquea el cierre local del Checkpoint 6.
- **Respuesta 500 en OpenAPI:** documentar la respuesta genérica 500 de `GlobalExceptionHandler` en todas las operaciones REST, con `application/problem+json` y el esquema `ProblemDetail` existente, y regenerar la colección Postman. Solo cambia la documentación; el comportamiento de la API no se modifica.
- Se mantienen las decisiones ya registradas sobre dependencia directa `infrastructure → domain`, logs de excepciones sin cuerpos de solicitud y generador Postman versionado. Los ejemplos Postman deben ser genéricos, sin datos personales ni credenciales; la revisión de los artefactos generados debe confirmarlo.

**Pendientes del Checkpoint 6**

| Punto | Estado |
|---|---|
| Compilación y pruebas del reactor | Verificado |
| Reportes JaCoCo por módulo y agregado con Java 25 | Verificado |
| Pruebas JPA de Producto, Unidad de Medida e Inventario con H2 en `mvn verify` | Verificado: 14 pruebas en verde dentro de `mvn -B clean verify` |
| Configuración local de SonarQube Community Build y proyecto | Verificado: `docker-compose.sonar.yml` con versión `26.9.0.129388`, volúmenes nombrados y servidor `UP`/`healthy`; proyecto `solgases` creado para la rama `feature/clean-architecture` |
| Soporte de Java 25 en el analizador | Respaldado por documentación oficial, notas de SonarJava 8.24 y `MAX_SUPPORTED = 26` del binario 8.41.0.47177; la confirmación empírica queda pendiente del primer análisis |
| Token local para análisis | Verificado: token de análisis limitado a `solgases`, con vencimiento de 30 días, guardado como `SONAR_TOKEN` en `.sonar.env`, ignorado por Git y sin seguimiento; `.env` ya no contiene `SONAR_TOKEN` |
| Análisis SonarQube manual | **Pendiente:** ejecutar el comando documentado con el token local y revisar que no haya errores de análisis sintáctico ni avisos de versión no soportada |
| Línea base y Quality Gate | Línea base medida; sin umbrales propios de cobertura. Se usa el Quality Gate predeterminado de la instancia, solo como información: puede mostrar `ERROR` sin bloquear la compilación |
| Hallazgos críticos | Pendiente del análisis SonarQube: se corregirán solo errores y problemas de seguridad confirmados de máxima severidad; el resto permanecerá en SonarQube |
| OpenAPI, Postman, logs, configuración y secretos | Verificado, incluidos ejemplos Postman genéricos sin datos personales ni credenciales |
| Colación explícita de `tbl_product.sku` y `tbl_unit_of_measure.code` | **Local:** verificado; la base MySQL local del proyecto ya cumple la colación acordada (comprobación previa sin colisiones y verificación del paso 4 conforme), por lo que no se ejecutó ningún `ALTER`. **QA y producción: pendiente** aplicar el procedimiento SQL documentado y su verificación, al igual que en cualquier base nueva; no bloquea el cierre local del Checkpoint 6. No hay mecanismo de migraciones en el repositorio ni configuración de colación en el mapeo JPA |
| Revisión humana de pruebas y cambios asistidos por IA | Pendiente del responsable |

La selección y ejecución de una herramienta de análisis de dependencias y vulnerabilidades se difiere al Incremento 7; no bloquea el Checkpoint 6. El Incremento 6 no se cerrará hasta resolver sus verificaciones obligatorias y realizar la revisión humana.

---

## Incremento 7 — Securing Modern Applications

### Objetivo

Proteger la API con Spring Security, autenticación mediante JWT y autorización basada en políticas/permisos, manteniendo Clean Architecture.

### Alcance

- Incorporar Spring Security únicamente en `infrastructure`; mantener las políticas y puertos de aplicación independientes de tipos del framework.
- Implementar autenticación local con nombre de usuario y contraseña para usuarios internos. La contraseña y su hash se almacenarán en una entidad/tabla de credenciales separada; `domain.User` no almacenará credenciales. No habrá registro público ni recuperación automática de contraseña en este incremento.
- Almacenar contraseñas con Argon2id, usando como mínimo la configuración recomendada por OWASP (19 MiB de memoria, 2 iteraciones y paralelismo 1), y ajustar/verificar el costo en el entorno objetivo. Nunca guardar contraseñas en claro ni usar hashes rápidos como SHA-256.
- Aplicar denegación por defecto y proteger explícitamente rutas y operaciones mediante una matriz de políticas aprobada.
- Resolver autoridades a partir de permisos con claves internas estables; no basar políticas duraderas en nombres editables de roles ni códigos editables de permisos.
- Impedir autenticación de usuarios inactivos. Usar JWT firmado con HS256 y una clave externa al repositorio, con access token de 15 minutos y sin refresh tokens en este incremento. No incluir roles/permisos en el token como fuente de autorización: consultar el estado del usuario y sus permisos actuales en cada solicitud protegida para que desactivaciones y cambios de permisos tengan efecto inmediato.
- Proteger secretos de firma y credenciales mediante configuración externa; no guardar secretos en el repositorio, imágenes Docker, logs ni colección Postman.
- Responder los errores de autenticación (401) y autorización (403) con el formato estándar de errores del proyecto, `ProblemDetail` (`application/problem+json`), definido en §9.
- Añadir pruebas de autenticación/autorización, expiración y rechazo de JWT inválidos, usuario inactivo, acceso anónimo, concesiones y denegaciones.
- Analizar dependencias con OWASP Dependency-Check para Maven, usando NVD como fuente y manteniendo cualquier API key fuera del repositorio. Un hallazgo crítico (CVSS 9.0–10.0) bloquea el cierre; los hallazgos altos (CVSS 7.0–8.9) requieren triage y remediación o excepción justificada y aprobada. No aplicar actualizaciones de dependencias automáticamente. Usar IA solo como apoyo al triage.
- Mantener CORS deshabilitado hasta conocer los orígenes concretos de los clientes autorizados.

### Decisiones aprobadas y pendientes

- **Aprobadas:** credenciales locales separadas de `domain.User`; Argon2id; sin registro público ni recuperación automática; JWT HS256 de 15 minutos sin refresh tokens; estado y permisos consultados en cada solicitud protegida; OWASP Dependency-Check/Maven con NVD; críticos bloqueantes y altos sujetos a triage, remediación o excepción aprobada; CORS deshabilitado hasta definir orígenes.
- *Antecedente histórico (anterior a las decisiones del 2026-10-08).* **Pendientes de negocio:** catálogo real de roles y permisos, matriz endpoint–permiso y mecanismo operativo para habilitar el primer usuario administrador. No inventar roles/permisos ni exponer una ruta pública de alta administrativa. Mientras sigan pendientes, no habilitar usuarios reales ni declarar completa la autorización funcional.
- **Pendiente:** orígenes CORS, hasta que se conozcan los clientes que consumirán la API.
- *Antecedente histórico (anterior a las decisiones del 2026-10-08).* La API solo expondrá como pública la operación de autenticación aprobada; las demás rutas exigirán autenticación y autorización por defecto. Swagger/OpenAPI conserva la disponibilidad establecida por perfiles, pero no constituye una excepción pública a la política de seguridad.
- **Aprobadas el 2026-10-08 (sustituyen a los dos puntos anteriores):**
  - catálogo inicial de roles y permisos;
  - matriz endpoint–permiso de las operaciones actuales;
  - comando local para crear el primer administrador y provisionar credenciales;
  - rutas de Swagger UI y OpenAPI públicas en `local`, `dev` y `qa` y desactivadas en `prd`; las rutas de negocio siguen protegidas y deniegan por defecto;
  - aceptación temporal de los dos hallazgos bajos de DOMPurify;
  - límite de intentos aplazado;
  - política de `username` que no distingue mayúsculas ni acentos.

  El detalle está en «Catálogo, matriz y credenciales».
- **Fuera de alcance de este incremento:** ruta pública de alta, API administrativa de credenciales, cambio de contraseña y rol `INVENTORY_OPERATOR`, este último pendiente de definición del negocio.
- **Aceptadas el 2026-10-08 para el cierre del Checkpoint 7, solo para uso local:**
  - el riesgo de que el último administrador pueda desactivarse o perder el rol `ADMIN`;
  - los riesgos residuales listados en «Pendientes y limitaciones»;
  - que los errores del comando local de credenciales puedan incluir el nombre de usuario introducido.

  Antes de exponer o desplegar la API fuera del entorno local hay que revisar y resolver esos riesgos, además del límite de intentos de autenticación. La revisión humana final se hará al cierre del MVP y no es requisito para cerrar este checkpoint.

### Checkpoint 7

La autenticación y las políticas aprobadas se verifican con pruebas positivas y negativas; rutas no declaradas públicas requieren autenticación; usuario inactivo y permisos insuficientes son rechazados; secretos no aparecen en código ni logs; el análisis de dependencias se ejecuta y sus hallazgos críticos se resuelven, mientras que los altos se remedian o reciben una excepción aprobada. Para cerrar la autorización funcional deben estar aprobados el catálogo real, la matriz endpoint–permiso y el aprovisionamiento inicial de administrador. No habilitar despliegues no locales hasta superar este checkpoint y aprobar el riesgo residual.

### Estado del Incremento 7

**Estado: Checkpoint 7 cerrado para el alcance local (2026-10-08).** Se cumplen las condiciones del checkpoint:
- autenticación y políticas verificadas con pruebas automatizadas positivas y negativas, y con pruebas manuales locales con usuarios reales (ver «Pruebas manuales locales y cierre del Checkpoint 7»);
- rutas no públicas que exigen autenticación;
- usuarios inactivos y permisos insuficientes rechazados;
- secretos ausentes del código y de los logs;
- análisis de dependencias con 0 críticos y 0 altos;
- catálogo, matriz y aprovisionamiento del primer administrador aprobados y ejecutados;
- riesgo residual aprobado para uso local.

**No se habilitan despliegues ni exposición fuera del entorno local** hasta resolver los bloqueos indicados en «Pendientes y limitaciones». La revisión humana final queda para el cierre del MVP. Todo el trabajo del incremento sigue pendiente de confirmar en Git por el responsable.

*Antecedente histórico:* hasta las pruebas manuales del 2026-10-08, el checkpoint seguía abierto por la creación del primer administrador, la prueba del 403 con un usuario real y la revisión humana, entonces requerida para el cierre.

*Antecedente histórico:* hasta el 2026-10-08 la autorización funcional estaba bloqueada por el catálogo, la matriz y el aprovisionamiento del primer administrador, entonces pendientes de negocio.

**Implementado**

- **Spring Security solo en `infrastructure`** (`com.solgases.infrastructure.security`). La capa de aplicación define puertos y casos de uso sin tipos de Spring Security: `AuthenticateUserUseCase`, `GetUserAccessUseCase`, `CredentialPersistencePort` y `PasswordHashVerifier`. El dominio no cambia.
- **Credenciales locales separadas:** entidad `UserCredentialJpaEntity`, tabla `tbl_user_credential` (`user_id` como clave primaria y FK `fk_user_credential_user` hacia `tbl_user`, `password_hash`, `created_at`, `updated_at`). `domain.User` no almacena credenciales. No hay registro público, recuperación de contraseña ni ninguna operación REST para crear o cambiar credenciales.
- **Argon2id:** `Argon2PasswordEncoder` de Spring Security con sal de 16 bytes, hash de 32 bytes y los parámetros mínimos de OWASP (19 MiB = 19456 KiB, 2 iteraciones, paralelismo 1), configurables en `solgases.security.argon2.*`. La aplicación no arranca con valores inferiores. Medición en la máquina de desarrollo local: unos 32 ms por hash; el ajuste en el entorno objetivo queda para el Incremento 8. La implementación requiere `org.bouncycastle:bcprov-jdk18on` (`1.86`), que Spring Security usa para Argon2 y Spring Boot no gestiona.
- **Autenticación:** `POST /api/auth/token`, la única operación pública. Usuario desconocido, contraseña incorrecta, usuario inactivo o sin credencial reciben el mismo 401 («Invalid username or password»); para un usuario desconocido se realiza una verificación Argon2 equivalente, para no revelar su existencia por el tiempo de respuesta.
- **JWT HS256:** token de acceso de 15 minutos sin refresh token, firmado con la clave de `solgases.security.jwt.secret` (variable `JWT_SECRET`, Base64, al menos 256 bits). La aplicación no arranca sin clave, con una clave mal codificada o demasiado corta, y el mensaje nunca la muestra. El token solo contiene `sub` (id del usuario), `iat` y `exp`: no incluye roles ni permisos.
- **Estado y permisos en cada solicitud:** `CurrentUserAuthenticationConverter` consulta el usuario en cada solicitud protegida; un usuario inactivo o inexistente recibe 401, y las autoridades son las claves internas estables de sus permisos actuales. Las desactivaciones y los cambios de permisos se aplican a la solicitud siguiente.
- **Denegación por defecto:** `PermissionMatrixAuthorizationManager` concede una solicitud solo si la primera regla de la matriz que coincide exige una clave de permiso que el usuario tiene; lo que no está en la matriz se deniega. Swagger/OpenAPI (`/v3/api-docs`, `/v3/api-docs/**`, `/swagger-ui.html`, `/swagger-ui/**`) es público solo con `GET` y solo donde springdoc lo habilita (`local`, `dev` y `qa`); en `prd` está desactivado y esas rutas responden 401. *Antecedente histórico:* antes del 2026-10-08 Swagger requería autenticación y permiso. Solo se permite sin autenticación el despacho interno de errores (`DispatcherType.ERROR`); una solicitud directa a `/error` sigue denegada.
- **401 y 403 en `ProblemDetail`** (`application/problem+json`, §9), con mensajes genéricos que no repiten tokens ni causas. El 401 incluye `WWW-Authenticate: Bearer`. `GlobalExceptionHandler` traduce además `AuthenticationException` y `AccessDeniedException` lanzadas dentro de controladores a 401/403, para que no acaben como 500.
- **CORS deshabilitado** y sin configuración de orígenes. CSRF deshabilitado porque la API no usa cookies ni sesiones (política `STATELESS`).
- **OpenAPI y Postman:** esquema `bearerAuth` global; las operaciones protegidas documentan 401/403 y la de token se marca como pública. La colección regenerada usa autenticación Bearer con la variable `accessToken`, vacía en el environment; el ejemplo del cuerpo de login usa `<password>` como marcador.
- **OWASP Dependency-Check** `12.2.2` en `pluginManagement`, con NVD como fuente, `failBuildOnCVSS = 9` (los críticos bloquean), clave de NVD leída de la variable de entorno `NVD_API_KEY` y excepciones aprobadas en `dependency-check-suppressions.xml` (vacío). No se aplican actualizaciones automáticas. La versión `13.0.0` existe, pero no tenía notas de versión publicadas al consultarla, por lo que se fijó `12.2.2`.

**Catálogo, matriz y credenciales (aprobado e implementado el 2026-10-08)**

- **Permisos de negocio** (`InitialRolePermissionCatalog`): `CATALOG_READ`, `CATALOG_WRITE`, `INVENTORY_READ`, `INVENTORY_MOVE`, `INVENTORY_ADJUST`, `USER_READ`, `USER_WRITE`, `ACCESS_READ` y `ACCESS_WRITE`. Son nueve: lectura y escritura por área, con movimientos y ajustes de inventario separados.
  - Al crearse, el código de cada permiso y el nombre de cada rol coinciden con su clave interna. Ambos se pueden editar después; la clave no cambia.
  - **Corrección:** la propuesta previa a la aprobación hablaba de «11 permisos», pero enumeraba 10 (los nueve de negocio más `API_DOCS_READ`). `API_DOCS_READ` se descartó al decidir que la documentación es pública por perfil. El catálogo aprobado tiene **9 permisos de negocio**.
- **Roles**, cargados de forma idempotente al arrancar, antes de cualquier otro *runner*:
  - **`ADMIN`:** los nueve permisos. Es el único rol con `USER_WRITE` y `ACCESS_WRITE`.
  - **`VIEWER`:** `CATALOG_READ` e `INVENTORY_READ`.
  - Como establece el Incremento 5, un rol recibe sus permisos semilla solo cuando se crea; si ya existe, no se modifica.
  - No se añade `INVENTORY_OPERATOR`.
- **Matriz** (`EndpointPermissionMatrixConfiguration`, 38 operaciones protegidas, solo patrones exactos):

  | Operaciones | Permiso |
  |---|---|
  | `GET` de listado y detalle en `/api/categories`, `/api/units-of-measure` y `/api/products` | `CATALOG_READ` |
  | `POST`, `PUT /{id}`, `PATCH /{id}/activate` y `/{id}/deactivate` de esos tres recursos | `CATALOG_WRITE` |
  | `GET /api/products/{productId}/inventory`, `…/movements` y `…/movements/{movementId}` | `INVENTORY_READ` |
  | `POST …/inventory/entries` y `…/inventory/exits` | `INVENTORY_MOVE` |
  | `POST …/inventory/adjustments` | `INVENTORY_ADJUST` |
  | `GET /api/users` y `/api/users/{id}` | `USER_READ` |
  | `POST /api/users`, `PUT /{id}`, `PATCH /{id}/activate` y `/{id}/deactivate` | `USER_WRITE` |
  | `GET` de listado y detalle en `/api/roles` y `/api/permissions` | `ACCESS_READ` |
  | `POST` y `PUT /{id}` de roles y permisos | `ACCESS_WRITE` |
  | `POST /api/auth/token` | Pública (fuera de la matriz) |
  | Swagger UI y OpenAPI | Públicas por perfil (fuera de la matriz) |
  | Cualquier otra ruta o método | Denegada |
- **Comando local de credenciales** (`com.solgases.infrastructure.cli`), siempre sin servidor web:
  - **`create-first-admin`:** crea un usuario activo con el rol `ADMIN` y su credencial. Se niega si ya existe un usuario **activo** con `ADMIN`.
  - **`provision-credential`:** guarda la primera credencial de un usuario existente que no la tenga. **Nunca sobrescribe** una credencial existente; informa de que hace falta un flujo de cambio de contraseña, que no existe.
  - **Contraseña:** se lee dos veces del terminal sin mostrarla (`Console.readPassword`), nunca por argumentos, propiedades ni variables de entorno. Solo se guarda el hash Argon2id y las copias en memoria se borran al terminar.
  - **Logs:** registran el id del usuario, nunca el `username` ni la contraseña.
  - **Sin credenciales predeterminadas** y sin ruta REST de alta.
  - *Antecedente:* al implementarse solo se probó con datos sintéticos en H2. **Actualización (2026-10-08):** se ejecutó de forma autorizada contra la base local para crear el administrador inicial y la credencial de `viewer-test` (ver «Pruebas manuales locales y cierre del Checkpoint 7»).
- **Corrección del modo sin servidor web (2026-10-08):**
  - **Incidente:** el primer intento manual de `create-first-admin` contra la base local falló al arrancar el contexto, porque `SecurityConfiguration` exigía un bean `HttpSecurity`, que Spring Security solo ofrece en aplicaciones web servlet.
  - **Estado de la base tras el fallo** (verificado en solo lectura): sin cambios respecto al estado documentado tras el corte, con 11 tablas y 0 usuarios, credenciales, roles y permisos. El esquema coincide en todos los tipos del inventario.
  - **Corrección:**
    - `SecurityConfiguration` solo se carga en la aplicación web servlet (`@ConditionalOnWebApplication`);
    - el registro de `SecurityProperties` pasó a `SecurityPropertiesConfiguration`, activa en ambos modos, porque el comando necesita Argon2id y la clave JWT;
    - `SolgasesApplication.application(args)` decide el tipo de aplicación.

    La invocación del comando no cambia.
  - **Prueba:** `CredentialCommandStartupTest` arranca la aplicación completa sin servidor web, con las propiedades del comando, H2 y una petición de contraseña simulada. Comprueba que no hay servidor web ni `SecurityFilterChain`, que el catálogo se carga antes del comando y que se crea un único administrador activo con credencial Argon2id que autentica. Antes de corregir reproducía el mismo error.
  - `mvn -B clean verify`: `BUILD SUCCESS` con **468 pruebas** (11 + 103 + 354).
- **Política de contraseñas (aprobada e implementada el 2026-10-08)** (`PasswordPolicy`, capa de aplicación):
  - **Longitud:** mínimo 15 y máximo 128 caracteres, contados como **puntos de código Unicode** y no como unidades UTF-16. Un carácter fuera del plano básico, como un emoji, cuenta como 1.
  - **Contenido:** se admite cualquier carácter, espacios y Unicode incluidos, sin reglas de composición. El valor se usa exactamente como se introdujo, sin recortarlo, normalizarlo ni truncarlo.
  - **Contraseñas nuevas** (`create-first-admin` y `provision-credential`): si la longitud no cumple, se rechaza antes de calcular Argon2id y sin escribir nada. El mensaje solo indica los límites, nunca el valor. La petición y la confirmación siguen siendo interactivas y ocultas, nunca se aceptan por argumentos, propiedades ni variables de entorno, no se registran en los logs y los buffers del comando se borran al terminar, también cuando falla.
  - **Autenticación:** una contraseña de más de 128 puntos de código recibe el mismo 401 genérico («Invalid username or password»), sin consultar la base ni calcular Argon2. El mínimo no se aplica al iniciar sesión.
  - **Cambio de validación en el login:** el campo `password` de `POST /api/auth/token` pasó de `@NotBlank` a `@NotEmpty`. Una contraseña formada solo por espacios es válida según la política, y con `@NotBlank` habría recibido 400. Una contraseña vacía sigue dando 400.
  - **Limitación:** el borrado de buffers cubre los arrays de caracteres del comando. La librería de hashing y la petición REST, cuya contraseña llega como `String` inmutable, generan copias internas que el proyecto no puede borrar.
  - **Pruebas:**
    - límites de 14, 15, 64, 128 y 129 caracteres, en ASCII y con caracteres de dos unidades UTF-16;
    - espacios y Unicode, conservados tal cual (una variante recortada no autentica);
    - confirmación distinta;
    - rechazo antes del hash y sin escrituras;
    - 401 genérico con 129 caracteres, 129 emojis y 100.000 caracteres;
    - inicio de sesión válido con una contraseña de 15 espacios.

    `mvn -B clean verify`: `BUILD SUCCESS` con **465 pruebas** (11 + 103 + 351), sin fallos.
- **Pruebas añadidas:**
  - catálogo;
  - las 38 parejas operación–permiso, más la denegación por defecto y las peticiones sin autenticar;
  - cobertura de todas las operaciones REST reales con la aplicación en marcha;
  - carga idempotente;
  - Swagger por perfil (`local`, `dev`, `qa` y `prd`);
  - casos de uso y comando de credenciales: creación, rechazo del segundo administrador, administrador inactivo, provisión sin sobrescritura, confirmación distinta, argumentos obligatorios, rechazo con servidor web y borrado de la contraseña.

  `mvn -B clean verify`: `BUILD SUCCESS` con 441 pruebas (11 + 83 + 347), sin fallos. Con la política de contraseñas son 465.
- **DOMPurify (aceptación temporal):** se aceptan GHSA-6688-9rhm-gjv2 y GHSA-p98j-92pf-mc4p (bajos, DOMPurify 3.4.13 dentro de `swagger-ui` 5.32.14), sin supresión ni parche del bundle.
  - **Alcance:** Swagger UI pública en `local`, `dev` y `qa`, y desactivada en `prd`. QA seguirá restringido a la red del equipo.
  - **Revisión:** cuando se publique un WebJar de `swagger-ui` con DOMPurify 3.4.16 o posterior. A 2026-10-08, el último WebJar (5.33.1) y `swagger-ui` 5.33.1 en npm siguen incluyendo la 3.4.13.
- **Límite de intentos de autenticación:** aplazado a un incremento posterior. **Debe resolverse antes de exponer la API fuera del entorno local.**
- **`username`:** se mantiene que no distinga mayúsculas ni acentos. La colación real de `tbl_user.username` en la base local es `utf8mb4_0900_ai_ci`, con índice único (consulta de solo lectura, 2026-10-08). No se alteró el esquema.

**Pruebas manuales locales y cierre del Checkpoint 7 (2026-10-08)**, con la base MySQL 8.4.12 local y la aplicación con el perfil `local`. Las contraseñas se introdujeron siempre de forma oculta y los tokens solo existieron en memoria; ni unas ni otros se imprimieron, guardaron o registraron:

| Paso | Resultado |
|---|---|
| Creación del administrador inicial con `create-first-admin`, tras corregir el modo sin servidor web | Correcta. Se cargó el catálogo: 9 permisos y 2 roles (`ADMIN` con 9 permisos, `VIEWER` con 2). Queda 1 usuario activo con `ADMIN` y 1 credencial Argon2id con parámetros m=19456, t=2, p=1 |
| Login del administrador | 200 |
| `GET /api/roles`, `/api/permissions`, `/api/categories` y `/api/users` con token de administrador | 200 en todas |
| `GET /api/roles` sin token | 401 |
| Usuario de prueba `viewer-test` | Creado activo mediante `POST /api/users` con el rol `VIEWER`; credencial con `provision-credential` |
| Login de `viewer-test` | 200 |
| `GET /api/categories` y `/api/products` con token de `viewer-test` | 200 |
| `GET /api/users` y `/api/roles` con token de `viewer-test` | **403** |
| Recuentos antes de desactivar `viewer-test` | 2 usuarios activos, 2 credenciales, 1 administrador activo y 1 usuario activo con `VIEWER` |
| Desactivación de `viewer-test` con `PATCH /api/users/{id}/deactivate` (administrador) | 200, `active = false`. Se conserva como evidencia de prueba |
| **Recuentos finales** (solo lectura) | 2 usuarios: 1 activo, el administrador, y 1 inactivo, `viewer-test`, que conserva su rol `VIEWER` y su credencial. 2 credenciales, 9 permisos, 2 roles, 1 administrador activo, 0 usuarios activos con `VIEWER`; 11 tablas sin cambios de esquema |

Después de cada prueba la aplicación se detuvo de forma ordenada: «Graceful shutdown complete», sin líneas `ERROR` ni `WARN` y sin tokens en el log. Los scripts y logs temporales, que estaban fuera del repositorio, se borraron. La última ejecución de `mvn -B clean verify` sobre el código vigente terminó con `BUILD SUCCESS` y 468 pruebas; después no se cambió código.

**Comandos**

```bash
# Compilación y pruebas (incluidas las de seguridad con H2)
mvn -B clean verify

# Análisis de dependencias (NVD). La clave se toma de NVD_API_KEY en el entorno; sin clave la descarga es muy lenta
mvn -B compile org.owasp:dependency-check-maven:aggregate

# Comandos locales de credenciales (requieren autorización expresa antes de ejecutarlos contra una base real).
# Se ejecutan desde la raíz del repositorio con el perfil local (.env); arrancan sin servidor web, piden la
# contraseña dos veces sin mostrarla y terminan. <username> y <nombre visible> son marcadores.
java -jar infrastructure/target/infrastructure-0.0.1-SNAPSHOT.jar --spring.profiles.active=local \
  --solgases.credentials.command=create-first-admin \
  --solgases.credentials.username='<username>' --solgases.credentials.display-name='<nombre visible>'
java -jar infrastructure/target/infrastructure-0.0.1-SNAPSHOT.jar --spring.profiles.active=local \
  --solgases.credentials.command=provision-credential --solgases.credentials.username='<username>'
```

El informe se genera en `target/dependency-check-report.html` y `.json` (no versionados).

**Estado vigente de base de datos y dependencias (tras el corte local del 2026-10-08):**

- **Base de datos local:** `solgases-mysql` funciona con Oracle MySQL 8.4.12 (imagen fijada por digest), publicado solo en `127.0.0.1:3307` y con el volumen `solgases-mysql84-data`. El volumen MySQL 8.0 se conserva intacto y desconectado como copia de reversión. El detalle está en «Resultado del corte local».
- **Dependencias fijadas en el `pom.xml` raíz:** Connector/J `26.7.0` (`mysql.version`, que sustituye a la 9.7.0 gestionada por Spring Boot) y Tomcat `11.0.26` (`tomcat.version`).
- **Pruebas:** `mvn -B clean verify` termina con `BUILD SUCCESS` y 468 pruebas aprobadas (11 + 103 + 354), sin fallos, errores ni omitidas, tras corregir el modo sin servidor web del comando. Con la política de contraseñas eran 465 (11 + 103 + 351). Antes eran 441 (11 + 83 + 347), con el catálogo, la matriz y el comando de credenciales, y tras el corte 327 (11 + 75 + 241).
- **Dependency-Check** (última ejecución tras el corte, con la base NVD actualizada): 68 dependencias, 0 críticos, 0 altos, 0 medios y 2 bajos. Los dos bajos son GHSA-6688-9rhm-gjv2 y GHSA-p98j-92pf-mc4p, de DOMPurify incluido en `swagger-ui` 5.32.14. Connector/J 26.7.0 no tiene hallazgos y no hay supresiones. OSS Index no se consultó por falta de credenciales.
- **Checkpoint 7:** cerrado para el alcance local el 2026-10-08, con pruebas manuales de administrador y `VIEWER` (200, 401 y 403 según lo esperado), el riesgo residual aceptado solo para uso local y la revisión humana final aplazada al cierre del MVP. No se habilita ningún despliegue fuera de local.

  Los hallazgos altos de Connector/J 9.7.0 ya no aplican.

Los párrafos marcados como *antecedente histórico* se conservan por trazabilidad: describen análisis y evaluaciones anteriores al corte.

*Antecedente histórico (anterior al corte del 2026-10-08; no refleja el estado vigente).* **Resultado del primer análisis de dependencias (2026-10-08):** OWASP Dependency-Check 12.2.2 examinó 68 dependencias tras descargar la base NVD inicial sin API key; tardó aproximadamente 2 h 38 min. Maven terminó con `BUILD FAILURE` y código 1 por `failBuildOnCVSS = 9`. Al contar identificadores únicos, el JSON contiene 17 hallazgos: 4 críticos, 7 altos, 4 medios y 2 bajos. El resumen de texto tenía 19 entradas porque los dos avisos GHSA aparecen duplicados en dos bundles de Swagger UI. CVE-2026-65905 aparece una sola vez. Los informes se guardaron fuera de `target/` antes de limpiar el proyecto; los informes nuevos de `target/` no se versionan.

*Antecedente histórico (anterior al corte del 2026-10-08; no refleja el estado vigente).* **Triage del primer análisis:** Dependency-Check reportó CVE-2026-65637 como crítico (CVSS 9.8), usando una métrica secundaria de CISA-ADP registrada en NVD; no hay métrica primaria de NIST. Apache clasifica su propio aviso como Moderate y señala la corrección en Tomcat 11.0.25. Esta diferencia refleja escalas/fuentes distintas, no una discrepancia en el identificador CVE. Apache también clasifica como Low o Important otros CVE de Tomcat que el análisis puntuó como críticos. No se añadieron supresiones. Los dos GHSA de DOMPurify son Low y afectan a la versión 3.4.13 incluida dentro de `swagger-ui` 5.32.14; el bundle utilizado por Swagger UI la carga. La inspección del bundle no encontró que se active la opción `IN_PLACE`; esto es una inferencia de inspección estática, no una prueba dinámica. Las versiones WebJar consultadas tampoco incluían la corrección DOMPurify 3.4.16. Swagger UI está protegido por autenticación y la matriz de permisos vacía, por lo que actualmente no es accesible a usuarios autenticados.

**Remediación de Tomcat:** se fijó `tomcat.version` en `11.0.26` en el `pom.xml` raíz. La resolución efectiva confirmó `tomcat-embed-core`, `tomcat-embed-el` y `tomcat-embed-websocket` en 11.0.26. `mvn -B clean verify` terminó con `BUILD SUCCESS`: 327 pruebas, 0 fallos, 0 errores y 0 omitidas.

*Antecedente histórico (anterior al corte del 2026-10-08; no refleja el estado vigente).* **Connector/J:** se probó la resolución de `26.7.0`, pero no se adoptó porque la tabla específica de compatibilidad del fabricante indica MySQL Server 8.4 o posterior, mientras el proyecto usa MySQL 8.0.46. La versión efectiva permanece en 9.7.0. El segundo análisis aún reporta en esa dependencia dos hallazgos altos (CVE-2026-60586, CVSS 7.7; CVE-2026-60623, CVSS 7.1) y dos medios (CVE-2026-60624 y CVE-2026-61082, CVSS 6.5). No se probó conexión real entre el proyecto y un servidor MySQL con Connector/J 26.7.0.

*Antecedente histórico (anterior al corte del 2026-10-08; no refleja el estado vigente).* **Segundo análisis de dependencias (2026-10-08):** `mvn -B compile org.owasp:dependency-check-maven:aggregate` terminó con `BUILD SUCCESS` (código 0) y examinó 68 dependencias reutilizando la base NVD local, sin descargar una actualización nueva. Los hallazgos únicos bajaron de 17 a 6: 0 críticos, 2 altos, 2 medios y 2 bajos. Se resolvieron 11 hallazgos de Tomcat; no aparecieron hallazgos nuevos. Persisten los cuatro CVE de Connector/J indicados arriba y los dos GHSA bajos de DOMPurify. El analizador de Sonatype OSS Index estuvo deshabilitado por falta de credenciales; este resultado cubre NVD, no OSS Index. La base NVD usada se actualizó durante el primer análisis, aproximadamente a las 04:30 UTC del 2026-10-08. Los informes se conservaron solo como archivos locales y no deben incluirse en el repositorio.

*Antecedente histórico (anterior al corte del 2026-10-08; no refleja el estado vigente).* **Estado del triage y decisiones pendientes:** el resultado satisface el criterio de cero hallazgos críticos, pero el Checkpoint 7 sigue abierto por los hallazgos altos de Connector/J. Debe decidirse entre actualizar MySQL a 8.4 o superior para poder evaluar Connector/J 26.7.0, probar otra versión compatible con MySQL 8.0 y repetir el análisis, o aprobar una excepción de riesgo documentada. No se aprobaron excepciones ni supresiones. Los GHSA bajos quedaron triageados como dependencias realmente empaquetadas, sin corrección compatible identificada en las versiones WebJar revisadas; su riesgo residual debe considerarse en el cierre. No se configuró clave NVD ni se mostraron secretos.

*Antecedente histórico (anterior al corte del 2026-10-08; no refleja el estado vigente).* **Evaluación temporal de Connector/J 8.4.0 (2026-10-08), distinta del análisis de 9.7.0:** se evaluó 8.4.0 como candidato para MySQL Server 8.0.46 sin fijarlo en ningún POM; solo se usó la propiedad temporal `-Dmysql.version=8.4.0`. La versión efectiva del proyecto sigue siendo 9.7.0.

- **Resolución:** `dependency:tree` con la propiedad temporal resolvió `com.mysql:mysql-connector-j:jar:8.4.0:runtime`.
- **Compatibilidad oficial:** las notas de versión de Oracle de Connector/J 8.4.0 (2024-04-30) indican que «can be used against MySQL Server version 8.0 and later», por lo que declara compatibilidad con 8.0.46.
- **Mantenimiento:** esas mismas notas la presentaban en su publicación como versión GA «recommended for use on production systems». Desde entonces Oracle no ha publicado versiones 8.4.x posteriores (8.4.0 es la única de la serie en las notas y en Maven Central), y la guía vigente de Connector/J solo documenta la serie 26.7. No se encontró una declaración oficial de que 8.4.0 siga mantenida.
- **Avisos de seguridad:** el Critical Patch Update de Oracle de julio de 2026 lista CVE-2026-60586, CVE-2026-60623, CVE-2026-60624 y CVE-2026-61082 con «Supported Versions Affected: 9.7.0-9.7.1», y NVD registra solo 9.7.0 y 9.7.1 como configuraciones afectadas. El mismo aviso advierte que las versiones sin soporte Premier o Extended «are not tested for the presence of vulnerabilities» y que «it is likely that earlier versions of affected releases are also affected». Por tanto, que 8.4.0 no figure como afectada no demuestra que no lo esté.
- **Pruebas:** `mvn -B -Dmysql.version=8.4.0 clean verify` terminó con `BUILD SUCCESS` y 327 pruebas sin fallos, errores ni omitidas. Esas pruebas usan H2 y no ejercitan el driver MySQL.
- **Dependency-Check con la propiedad temporal:** `BUILD SUCCESS`, 68 dependencias; reutilizó la base NVD local sin actualizarla («Skipping the NVD API Update as it was completed within the last 240 minutes»), es decir, con los datos de la actualización del primer análisis del 2026-10-08. Hallazgos únicos: 0 críticos, 0 altos, 0 medios y 2 bajos (los GHSA de DOMPurify en Swagger UI). Los cuatro CVE de 9.7.0 no aparecen para 8.4.0, porque las fuentes solo listan 9.7.0–9.7.1 como afectadas; no es una verificación de que 8.4.0 esté libre de ellos. OSS Index siguió deshabilitado por falta de credenciales.
- **Conexión con MySQL:** una comprobación JDBC de solo lectura con el driver 8.4.0 contra la instancia local dedicada del proyecto (`solgases-mysql`, MySQL 8.0.46) conectó correctamente en modo de solo lectura y consultó versiones y metadatos. El programa incluía además un intento de `CREATE TEMPORARY TABLE` para comprobar el modo de solo lectura; el propio driver lo rechazó antes de enviarlo al servidor (SQLState `S1009`), por lo que no se ejecutó ninguna escritura. No se probó la aplicación completa contra MySQL con 8.4.0.
- **Conclusión:** 8.4.0 es compatible según Oracle con MySQL 8.0 y elimina los hallazgos del informe, pero no hay constancia de que siga mantenida ni de que esté libre de los CVE de 9.7.0, ya que Oracle no evalúa versiones sin soporte. No se adopta. La decisión sigue abierta entre: (a) mantener 9.7.0 con una excepción de riesgo aprobada para los dos altos; (b) actualizar MySQL a 8.4 o posterior y evaluar Connector/J 26.7.0; (c) adoptar 8.4.0 aceptando de forma explícita el riesgo de una versión sin mantenimiento confirmado y sin evaluación de Oracle frente a esos CVE.

*Antecedente histórico (anterior al corte del 2026-10-08; no refleja el estado vigente).* **Evaluación temporal de MySQL 8.4.11 con Connector/J 26.7.0 (2026-10-08), distinta de los análisis de 9.7.0 y 8.4.0:** prueba aislada y reversible; no se actualizó la base existente ni se fijó 26.7.0 en ningún POM.

- **Fuentes oficiales:** las notas de Oracle de Connector/J 26.7.0 (2026-07-29) la describen como «new GA release», que «supersedes 9.7 and is recommended for use on production systems» y que «can be used against MySQL Server version 8.4 and later»; no incluyen notas de seguridad. El Critical Patch Update de julio de 2026 marca MySQL Server 8.4.0–8.4.10 como afectado y los cuatro CVE de Connector/J en 9.7.0–9.7.1. Al volver a consultar NVD (última modificación de esos CVE: 2026-09-03), las configuraciones afectadas siguen siendo solo 9.7.0 y 9.7.1.
- **Entorno temporal:** imagen oficial `mysql:8.4.11` fijada por digest (`sha256:6ea90827…`), la versión de parche más reciente con imagen oficial y posterior al rango afectado del CPU de julio (Oracle ya publicó 8.4.12, aún sin imagen oficial). Se usó un proyecto Compose exclusivo, el puerto `127.0.0.1:3318`, un volumen propio y credenciales temporales aleatorias en archivos con permisos restringidos fuera del repositorio. Una consulta de solo lectura confirmó `@@version = 8.4.11` (MySQL Community Server - GPL).
- **Resolución:** `dependency:tree -Dmysql.version=26.7.0` resolvió `com.mysql:mysql-connector-j:jar:26.7.0:runtime`.
- **Pruebas:** `mvn -B -Dmysql.version=26.7.0 clean verify` terminó con `BUILD SUCCESS` y 327 pruebas sin fallos, errores ni omitidas; usan H2, no el driver MySQL.
- **Conexión JDBC de solo lectura:** el driver `mysql-connector-j-26.7.0` conectó con MySQL 8.4.11 en una sesión de solo lectura (`@@transaction_read_only = 1`) y con TLS 1.3. El programa solo ejecutó consultas.
- **Arranque de la aplicación:** el jar construido con 26.7.0 arrancó con el perfil `dev` apuntando solo al MySQL temporal, desde un directorio sin `.env` y con credenciales y clave JWT temporales pasadas al proceso. Hibernate creó 11 tablas solo en esa base, sin filas de negocio; el log no tuvo líneas `WARN` ni `ERROR`, y la aplicación se detuvo de forma ordenada. No se ejecutaron operaciones REST ni escrituras de negocio.
- **Dependency-Check con la propiedad temporal:** `BUILD SUCCESS`, 68 dependencias. Esta vez actualizó la base NVD local (2.077 registros nuevos, finalizada el 2026-10-08 a las 08:49). Hallazgos únicos: 0 críticos, 0 altos, 0 medios y 2 bajos (los GHSA de DOMPurify en Swagger UI). OSS Index siguió deshabilitado por falta de credenciales.
- **Contraste con Oracle/NVD:** que los cuatro CVE no aparezcan es coherente con que Oracle y NVD solo listen 9.7.0–9.7.1 como afectadas y con que 26.7.0 sea la versión GA que sustituye a 9.7. Aun así, las notas de 26.7.0 no declaran explícitamente la corrección de esos CVE, por lo que el resultado no se registra como verificación positiva de que estén corregidos.
- **Limpieza:** se eliminaron solo el contenedor, el volumen, la red y los archivos temporales de esta evaluación. La imagen `mysql:8.4.11` no se borró porque ya existía (mismo digest que la etiqueta `mysql:8.4`, usada por un contenedor de otro proyecto). La instancia `solgases-mysql` (MySQL 8.0.46) quedó idéntica a su estado previo: mismo contenedor, hora de arranque, imagen, volumen, puertos, versión y 10 tablas.
- **Límites:** no se migraron datos ni se actualizó la base del proyecto; no se probaron operaciones de la API contra MySQL 8.4; las pruebas automáticas no ejercitan el driver; OSS Index no se consultó.
- **Estado de la decisión:** MySQL 8.4.11 con Connector/J 26.7.0 es la ruta respaldada por Oracle para salir de la serie 9.7 afectada, y la prueba aislada fue satisfactoria. Adoptarla requiere actualizar MySQL del proyecto a 8.4, lo que necesita decisión y un plan de migración de datos, y no se ha aprobado. La versión efectiva permanece en 9.7.0, con sus dos hallazgos altos pendientes de decisión.

**Imagen Oracle MySQL 8.4.12: privilegio `PROXY` de `root@%` y healthcheck de Compose (2026-10-08):** hallazgos de la validación desechable con la imagen `container-registry.oracle.com/mysql/community-server:8.4.12` (digest `sha256:7dcc4add…885be`). No se ha hecho el corte local ni se ha adoptado Connector/J 26.7.0; la instancia `solgases-mysql` (MySQL 8.0.46) no se modificó.

- **Privilegio `PROXY` de `root@%`: observado solo en la prueba desechable; aceptado como comportamiento de la imagen y fuera de la configuración del corte.**
  - **Hecho:** en la instancia 8.4.12 desechable, `root@'%'` tenía `GRANT PROXY ON ''@'' TO 'root'@'%' WITH GRANT OPTION`. En la instancia 8.0.46 desechable, creada con la imagen oficial `mysql:8.0` y restaurada desde el mismo volcado, ese privilegio solo lo tenía `root@localhost`.
  - **Origen:** el script de entrada de la imagen de Oracle concede ese privilegio a la cuenta `root@${MYSQL_ROOT_HOST}`. Se comprobó en el `/entrypoint.sh` de la imagen 8.4.12 y en el `docker-entrypoint.sh` de 8.4 que Oracle publica en el repositorio `mysql/mysql-docker`.
  - **Significado:** según la documentación de MySQL 8.4 sobre usuarios proxy, permite configurar usuarios proxy para cualquier cuenta y delegar esa capacidad en otras cuentas. `root@'%'` ya tiene `ALL ON *.* WITH GRANT OPTION`. Los usuarios proxy solo funcionan con autenticación PAM o Windows, o con los plugins obsoletos `mysql_native_password` y `sha256_password` si se activa `check_proxy_users`. Todas las cuentas inventariadas usan `caching_sha2_password`, que la documentación no incluye entre esos plugins. No se configuró ningún usuario proxy.
  - **Condición de la prueba:** la instancia desechable definió `MYSQL_ROOT_HOST=%` para reproducir la cuenta `root@%` de la instancia original. Según la documentación de la imagen, sin esa variable solo se crea `root@'localhost'`. El privilegio observado corresponde, por tanto, a esa prueba.
  - **Decisión (resuelta):** se acepta como comportamiento de la imagen Oracle en el entorno local. La aplicación no lo necesita: se conecta con su propio usuario, no con root. **La configuración propuesta para el corte omite `MYSQL_ROOT_HOST`**, así que no se crea `root@%` ni existirá ese privilegio adicional. No se aplica a QA ni a producción, cuya configuración de cuentas sigue pendiente.
- **Healthcheck de Compose: prueba con el comando exacto del proyecto contra la imagen 8.4.12.**
  - **Entorno:** instancia desechable propia, con su volumen, credenciales aleatorias temporales, sin puertos publicados y con la misma definición de healthcheck que `docker-compose.yml`: `mysqladmin ping -h localhost`, `interval` 10 s, `timeout` 5 s, `retries` 10. La validación anterior de 8.4.12 había usado otra variante, por TCP a `127.0.0.1`, que no es la del proyecto.
  - **Qué comprueba:** `-h localhost` se conecta por el socket Unix y el comando no lleva credenciales. Según la documentación de `mysqladmin`, `ping` devuelve 0 si el servidor está en ejecución, «incluso en caso de un error como `Access denied`». En la prueba devolvió 0 con el servidor listo y la autenticación de root rechazada. La instancia original 8.0.46 muestra lo mismo en su historial de salud. Por tanto, `healthy` solo indica que un `mysqld` responde en el socket. No garantiza que las credenciales de la aplicación funcionen, que exista la base de datos ni que el puerto TCP esté disponible. El comando no usa la cuenta `healthchecker` ni `healthcheck.cnf`.
  - **Durante la inicialización (volumen nuevo):** se ejecutó el mismo comando con `docker exec` cada 0,6 s aproximadamente, a la vez que se registraba el estado de Docker. Tiempos en UTC:

    | Momento | Fase según el log | Resultado del comando | Estado Docker |
    |---|---|---|---|
    | 19:37:31,9–34,9 | `mysqld --initialize` en curso | código 1, sin servidor | `starting` |
    | 19:37:35,4 | servidor temporal listo (`port: 0`, solo socket) | — | `starting` |
    | 19:37:35,5–36,6 | servidor temporal; inicialización sin terminar | **código 0** (`mysqld is alive`) | `starting` |
    | 19:37:37,2–37,8 | el servidor temporal se detiene | código 1 | `starting` |
    | 19:37:37,9 | `MySQL init process done. Ready for start up.` | — | `starting` |
    | 19:37:38,5 | servidor definitivo listo (`port: 3306`) | código 0 (`Access denied`) | `starting` |
    | ≈19:37:42 | primer sondeo de Docker | código 0 | `healthy` |
  - **Resultado durante la inicialización:** el comando del proyecto **sí devuelve éxito antes de que termine la inicialización**, mientras responde el servidor temporal. En esta ejecución Docker no marcó `healthy` antes de tiempo, porque su primer sondeo llegó unos 10 s después del arranque, cuando ya estaba el servidor definitivo; la ventana observada duró unos 1,2 s. No se probaron inicializaciones más largas, por ejemplo con scripts en `docker-entrypoint-initdb.d`, discos lentos o volcados grandes. En esos casos, que Docker marque `healthy` antes de tiempo no está descartado.
  - **Reinicio con el volumen ya inicializado:** no hubo fase de inicialización y el servidor quedó listo en menos de 1 s. El comando devolvió 0 y Docker volvió a `healthy` en el primer sondeo, unos 10 s después.
  - **Limitaciones:**
    - una sola ejecución y en esta máquina;
    - la fase del servidor se dedujo del log de la imagen;
    - cada sondeo deja un intento fallido de autenticación de `root@localhost`. Las conexiones por socket no pasan por la caché de hosts ni cuentan para `max_connect_errors`.
  - **Decisión (resuelta): se conserva el healthcheck actual**, `mysqladmin ping -h localhost`, sin cambios en `docker-compose.yml`.
    - Su estado `healthy` solo confirma que MySQL responde por socket. No valida credenciales, base de datos ni disponibilidad TCP.
    - La validación de un futuro corte a MySQL 8.4.12 deberá incluir una comprobación independiente de la conexión de la aplicación, sin depender del estado `healthy`.
    - Resultado observado que se conserva: durante la inicialización, el comando sí respondió con éxito ante el servidor temporal. En la ventana de inicialización probada, Docker no llegó a marcar `healthy`. No se ha probado con inicializaciones más largas.

Fuentes: [`mysqladmin`](https://dev.mysql.com/doc/refman/8.4/en/mysqladmin.html), [usuarios proxy](https://dev.mysql.com/doc/refman/8.4/en/proxy-users.html), [caché de hosts](https://dev.mysql.com/doc/refman/8.4/en/host-cache.html) y [`docker-entrypoint.sh` de MySQL 8.4 en `mysql/mysql-docker`](https://github.com/mysql/mysql-docker/blob/main/mysql-server/8.4/docker-entrypoint.sh).

**Plan de corte local a MySQL 8.4.12 con Connector/J 26.7.0 (aprobado y ejecutado el 2026-10-08, hora local):** este plan recoge las decisiones aprobadas y se ejecutó tras la autorización expresa del corte. Los resultados observados están en «Resultado del corte local». El texto del plan se conserva como referencia para la reversión.

- **Decisiones aprobadas (aplicadas en el corte):**
  1. Omitir `MYSQL_ROOT_HOST`. Root solo existirá como `root@'localhost'`, sin `root@%` y sin el privilegio `PROXY` adicional.
  2. Eliminar la cuenta `healthchecker@localhost` de la nueva instancia después de inicializarla y de verificar el servicio.
  3. Publicar el puerto solo en `127.0.0.1`.
  4. Conservar intacto y desconectado el volumen MySQL 8.0 (`solgases_solgases-mysql-data`) hasta validar el corte y cerrar el incremento. Su limpieza se decidirá aparte.
  5. El Upgrade Checker ya evaluó específicamente 8.4.12 (ver «Ya comprobado»), así que no se repite.
  6. Adoptar Connector/J 26.7.0 junto con el corte. La dependencia solo se cambia cuando se autorice ejecutar el corte. Según la [tabla oficial de compatibilidad de Connector/J](https://dev.mysql.com/doc/connector-j/en/connector-j-versions.html), la 26.7 admite MySQL 8.4 y posteriores; esa tabla no documenta la 9.7.0 con 8.4.
  7. Conservar sin cambios el healthcheck `mysqladmin ping -h localhost`. Solo confirma que MySQL responde por socket, así que la conexión de la aplicación se valida por separado.
- **Ya comprobado (2026-10-08), en instancias desechables:**
  - **Upgrade Checker:** `util.checkForServerUpgrade` de MySQL Shell 26.7.1, desde la imagen Oracle 8.4.12 fijada por digest, contra la instancia 8.0.46. La salida indica «will now be checked for compatibility issues for upgrade to MySQL 8.4.12». Resultado:
    - 0 errores;
    - 24 advertencias de cambios de valores por defecto, que afectan al rendimiento y la replicación;
    - 2 avisos de `SET_USER_ID` en cuentas root.

    Ninguno es una condición de parada.
  - **Respaldo y restauración:** el volcado lógico con `mysqldump` 8.0.46 se restauró sin errores en 8.0.46 y en 8.4.12. Esquema, datos, cuentas de la aplicación y sus permisos quedaron idénticos. Las diferencias de root fueron privilegios dinámicos propios de cada versión y el `PROXY` de la prueba con `MYSQL_ROOT_HOST=%`.
  - **Aplicación con Connector/J 26.7.0** (solo con `-Dmysql.version=26.7.0`):
    - `clean verify` con 327 pruebas sin fallos;
    - arranque sin `ERROR` ni `WARN`, con creación de `tbl_user_credential`;
    - persistencia sintética: 1062 por SKU duplicado y 1452 por FK inválida;
    - REST: 401 y 403 en formato ProblemDetail;
    - Dependency-Check sin hallazgos en el driver. NVD no se actualizó en esa ejecución y OSS Index estaba deshabilitado.
  - **Cuenta `healthchecker`:** al eliminarla, la instancia siguió `healthy` y la cuenta no reapareció al reiniciar.
  - **Sin `MYSQL_ROOT_HOST`:** la imagen inicializó correctamente y quedó `healthy` con el healthcheck del proyecto.
- **No comprobado (pendiente):**
  - una restauración completa y el arranque de la aplicación con la configuración exacta del corte, que no define `MYSQL_ROOT_HOST` y publica el puerto en loopback;
  - la eliminación de `healthchecker` en una instancia permanente;
  - la copia en frío del volumen 8.0;
  - la reversión completa;
  - el 403 con un usuario real, que no es posible mientras no se apruebe el aprovisionamiento del primer administrador.
- **Precondiciones** (si falla alguna, no se empieza):
  1. Autorización expresa del corte.
  2. La aplicación local está detenida. Si está en marcha, se consulta antes de detenerla.
  3. `solgases-mysql` está `healthy`. Se registran su id, imagen (`mysql:8.0`, `sha256:7dcddc01…`), volumen, puertos, versión 8.0.46 y número de tablas.
  4. El digest de la imagen Oracle sigue siendo `sha256:7dcc4add…885be` y no hay avisos de Oracle nuevos que afecten a 8.4.12.
  5. En `.env` existen `JWT_SECRET` y las variables `DB_*`. Solo se verifica que existen, sin leer ni mostrar su valor. `DB_HOST` debe apuntar al bucle local (`localhost` o `127.0.0.1`), porque el puerto solo se publicará en `127.0.0.1`; se comprueba sin mostrar el valor.
  6. Ninguna herramienta local depende de entrar como root desde fuera del contenedor, porque después del corte no existirá `root@%`.
  7. Directorio de trabajo fuera del repositorio, creado con `umask 077`. Los cambios de los POM y del resto de archivos del Incremento 7, aún sin commit, se conservan.
- **Respaldo:**
  1. **Inventario de referencia**, de solo lectura y como root dentro del contenedor: variables, esquema, columnas, índices, FK, restricciones y DDL normalizado; recuentos y hashes por tabla; cuentas con su plugin, roles y permisos. Los scripts de la validación se borraron, así que hay que volver a escribirlos con las mismas consultas.
  2. **Volcado lógico final** con `mysqldump` 8.0.46 dentro del contenedor: `--single-transaction --skip-lock-tables --routines --triggers --events --set-gtid-purged=OFF --no-tablespaces --hex-blob`. Archivo con permisos `600` y su `sha256`.
  3. **Copia en frío del volumen 8.0:** con el servidor ya detenido, un `tar` del volumen montado en solo lectura en un contenedor temporal. No se ha probado.
- **Corte** (pasos futuros, no ejecutados):
  1. `docker compose stop mysql`. **Nunca `down -v`.**
  2. Cambios en `docker-compose.yml`. El healthcheck no cambia y no se añade `MYSQL_ROOT_HOST`:
     ```yaml
     image: container-registry.oracle.com/mysql/community-server:8.4.12@sha256:7dcc4add9183664de3a214daf85a50c3ba6cccfd7534f700b6561bf5b41885be
     ports:
       - "127.0.0.1:${DB_PORT:-3307}:3306"
     volumes:
       - solgases-mysql84-data:/var/lib/mysql
     # top-level volumes: declare only solgases-mysql84-data; the 8.0 volume stays outside Compose, so down -v cannot delete it
     ```
  3. `docker compose up -d mysql`. Compose vuelve a crear el contenedor. La instancia 8.4 nunca monta el volumen 8.0: actualizarlo en el mismo volumen impediría volver a 8.0.
  4. Esperar a `healthy`, que solo indica que MySQL responde por socket. Después, comprobar por separado la conexión TCP en `127.0.0.1:${DB_PORT}` con el usuario de la aplicación y `SELECT 1`, leyendo las credenciales dentro del contenedor.
  5. Eliminar `healthchecker@localhost` y comprobar que la instancia sigue `healthy` y que la cuenta no reaparece tras reiniciar.
  6. Restaurar el volcado final y compararlo con el inventario. Solo se aceptan las diferencias de privilegios dinámicos de root propias de 8.4. No debe aparecer `root@%` ni `PROXY` en `root@%`.
  7. Cambiar `mysql.version` a `26.7.0` en `pom.xml`, editando solo esa línea. `dependency:tree` debe resolver la 26.7.0 y `mvn -B clean verify` debe terminar correctamente.
- **Verificación posterior:**
  - **Contenedor:**
    - versión 8.4.12;
    - puerto publicado solo en `127.0.0.1`;
    - montado únicamente el volumen nuevo;
    - el volumen 8.0 sigue existiendo y no está montado.
  - **Cuentas:** solo `root@localhost` y el usuario de la aplicación con el plugin y los permisos esperados, sin `healthchecker`.
  - **Aplicación** con el perfil `local`:
    - arranca sin `ERROR`;
    - `performance_schema.session_connect_attrs` muestra conexiones «MySQL Connector/J 26.7.0»;
    - `tbl_user_credential` creada (InnoDB, `utf8mb4_0900_ai_ci`, con su FK).
  - **Datos:** recuentos y hashes iguales al inventario, y colación `utf8mb4_0900_ai_ci` en `tbl_product.sku` y `tbl_unit_of_measure.code`.
  - **Pruebas rápidas por REST, sin escrituras:** 401 sin token y 401 en `/api/auth/token` con un usuario desconocido, en `application/problem+json`.
  - **Dependency-Check** con la dependencia definitiva, indicando si NVD se actualizó y si OSS Index está deshabilitado.
- **Criterios de parada** (cualquiera lleva a la reversión):
  - fallo del inventario, del volcado, del `sha256` o de la copia del volumen;
  - la imagen no coincide con el digest;
  - la instancia no llega a `healthy` o falla la comprobación independiente de conexión;
  - tras eliminar `healthchecker`, la instancia deja de estar `healthy` o la cuenta reaparece;
  - errores de restauración, o diferencias fuera de las aceptadas;
  - aparece `root@%`;
  - el puerto no queda limitado a `127.0.0.1`;
  - falla `clean verify`, la aplicación arranca con `ERROR`, no conecta con la 26.7.0 o el REST responde distinto de lo esperado;
  - cualquier conexión o escritura no prevista.
- **Reversión:**
  1. Detener la aplicación y ejecutar `docker compose stop mysql`. El volumen 8.4 se conserva para analizar la causa.
  2. Restaurar en `docker-compose.yml` la imagen `mysql:8.0` (preferiblemente fijada a `sha256:7dcddc01…`), el volumen `solgases-mysql-data` y su declaración. El puerto en loopback puede conservarse; cualquier otro cambio de la configuración original requiere decisión.
  3. Ejecutar `docker compose up -d mysql` y verificar 8.0.46, `healthy`, 10 tablas y la coincidencia con el inventario.
  4. Quitar del `pom.xml` raíz solo la propiedad `<mysql.version>` (y su comentario), lo que devuelve la versión gestionada por Spring Boot (9.7.0). Conservar los demás cambios y no usar `git checkout`, porque se perderían los cambios del Incremento 7 aún sin commit.
  5. Los datos escritos en 8.4 después del corte solo pueden pasarse a 8.0 con un volcado lógico, y solo si no usan funciones exclusivas de 8.4.
  6. Si el volumen 8.0 estuviera dañado, último recurso: la copia en frío.
  7. Ningún volumen se elimina sin una aprobación aparte.

**Resultado del corte local (ejecutado el 2026-10-08 entre las 19:40 y las 19:50, hora local; 00:40–00:50 UTC del 2026-10-09):** solo la instancia local `solgases-mysql`; QA y producción no se tocaron. No se cumplió ningún criterio de parada y no hizo falta revertir.

- **Precondiciones verificadas:**
  - `JWT_SECRET` y las variables `DB_*` existen en `.env` (solo presencia) y `DB_HOST` apunta al bucle local;
  - `.env` sigue ignorado y sin seguimiento;
  - la aplicación estaba detenida;
  - la instancia original estaba `healthy`: MySQL 8.0.46, imagen `sha256:7dcddc01…`, 10 tablas;
  - la etiqueta Oracle `8.4.12` sigue apuntando a `sha256:7dcc4add…885be`;
  - en el índice de avisos de Oracle no hay nada posterior al CSPU de septiembre de 2026 (rev. 1).
- **Respaldo (verificado antes de modificar nada):**
  - **Inventario de referencia:** 10 tablas, 53 columnas, 26 entradas de índice, 8 FK, 27 restricciones y 0 filas.
  - **Volcado lógico:** `mysqldump` 8.0.46 terminó con código 0 y sin errores. 10.713 bytes, 10 `CREATE TABLE`, 0 `INSERT` (coherente con 0 filas); `sha256` registrado y verificado.
  - **Copia en frío del volumen 8.0:** con el servidor detenido de forma ordenada, `tar` de 178 archivos regulares y 1 enlace simbólico (`mysql.sock`). `tar --compare` contra el volumen fue correcto y se guardaron el `sha256` de cada archivo y el de la copia. El inventario, el volcado y la copia están fuera del repositorio, con permisos restringidos.
- **Corte:**
  - **Compose:** `docker-compose.yml` cambió exactamente como dice el plan: imagen Oracle fijada por digest, puerto `127.0.0.1:${DB_PORT:-3307}:3306` y volumen nuevo `solgases-mysql84-data`. No se añadió `MYSQL_ROOT_HOST` y el healthcheck no cambió. El volumen 8.0 dejó de estar declarado en Compose.
  - **Arranque:** `docker compose up -d mysql` recreó el contenedor, como estaba aprobado; el volumen 8.0 se conservó. Quedó `healthy` unos 24 s después; el servidor definitivo ya estaba listo antes.
  - **Conexión autenticada por TCP**, comprobada aparte a través de `127.0.0.1:3307` con el usuario de la aplicación: `SELECT 1` correcto, versión 8.4.12, cuenta con host `%`, TLSv1.3.
  - **`healthchecker`:** existía, sin ninguna conexión. `DROP USER` terminó con código 0. La instancia siguió `healthy` sin fallos, y tras reiniciarla volvió a `healthy` sin la cuenta y sin repetir la inicialización. No existe ninguna cuenta root distinta de `root@localhost`.
  - **Restauración:** el `sha256` del volcado coincidió y la restauración terminó con código 0 y sin errores.
  - **Comparación con el inventario:** idénticos variables, esquema, tablas, columnas, índices, FK, restricciones, *checks*, objetos, datos, DDL normalizado, roles y permisos del usuario de la aplicación. Las únicas diferencias son las aceptadas:
    - falta `root@%`, por decisión;
    - privilegios dinámicos de `root@localhost` propios de cada versión: `SET_USER_ID` solo en 8.0; `ALLOW_NONEXISTENT_DEFINER`, `FLUSH_PRIVILEGES`, `OPTIMIZE_LOCAL_TABLE`, `SET_ANY_DEFINER` y `TRANSACTION_GTID_TAG` solo en 8.4.

    `PROXY` sigue solo en `root@localhost`.
- **Connector/J 26.7.0:**
  - se añadió `<mysql.version>26.7.0</mysql.version>` a `<properties>` del `pom.xml` raíz, con un comentario; la propiedad no existía y la 9.7.0 venía del padre de Spring Boot;
  - `dependency:tree` resuelve `com.mysql:mysql-connector-j:jar:26.7.0:runtime`;
  - `mvn -B clean verify`: `BUILD SUCCESS`, 327 pruebas (11 + 75 + 241) sin fallos ni errores;
  - el jar incluye `mysql-connector-j-26.7.0.jar`.
- **Verificación posterior:**
  - **Aplicación** (jar, perfil `local`, `.env`, puerto 8080): arrancó en 5,7 s sin líneas `ERROR` ni `WARN` y se detuvo de forma ordenada, sin conexiones abiertas.
  - **Driver:** 10 conexiones del usuario de la aplicación identificadas como «MySQL Connector/J 26.7.0», todas con TLS.
  - **Esquema y datos:** Hibernate creó solo `tbl_user_credential` (InnoDB, `utf8mb4_0900_ai_ci`, FK `fk_user_credential_user` → `tbl_user`). Todo lo demás del esquema y los datos quedó igual que tras la restauración: 11 tablas y 0 filas. `tbl_product.sku` y `tbl_unit_of_measure.code` conservan `utf8mb4_0900_ai_ci`.
  - **REST (sin escrituras):** sin token, 401; `POST /api/auth/token` con un usuario desconocido, 401; token con firma inválida, 401. Todas en `application/problem+json`.
- **Dependency-Check:** `compile org.owasp:dependency-check-maven:aggregate` terminó con código 0 en unos 90 s.
  - Esta vez **actualizó NVD** con 1.947 registros; última modificación 2026-10-09 00:17 UTC.
  - Revisó 68 dependencias: 0 críticos, 0 altos, 0 medios y 2 bajos (los GHSA de DOMPurify en Swagger UI).
  - `mysql-connector-j-26.7.0.jar`: 0 hallazgos.
  - OSS Index siguió deshabilitado por falta de credenciales.
- **Estado final:**
  - `solgases-mysql` corre con MySQL 8.4.12, `healthy`, publicado solo en `127.0.0.1:3307` y montando únicamente `solgases_solgases-mysql84-data`;
  - **el volumen 8.0 `solgases_solgases-mysql-data` está intacto:** misma fecha de creación, ningún contenedor lo usa, y la comprobación final del `sha256` de cada archivo y `tar --compare` contra la copia en frío fueron correctas;
  - la versión efectiva de Connector/J pasa a ser 26.7.0, en lugar de la 9.7.0 indicada en los análisis anteriores.
- **Pendiente o no verificado:**
  - la reversión no se había ensayado. *Actualización:* se ensayó el 2026-10-08 en recursos desechables; ver «Resultado del ensayo de reversión»;
  - el 403 con un usuario real, que depende del aprovisionamiento del primer administrador;
  - la conexión de DBeaver con el usuario de la aplicación, que hará el desarrollador;
  - cuándo eliminar el volumen 8.0 y la copia en frío, que requiere una decisión aparte tras cerrar el incremento;
  - QA y producción;
  - el análisis con OSS Index.

**Resultado del ensayo de reversión (2026-10-08, terminado hacia las 20:33 hora local; solo recursos desechables):** se siguió la reversión del plan de corte sin tocar la instancia activa ni los respaldos.

- **Comprobaciones previas:**
  - `SHA256SUMS` de `~/Backups/Solgases/cutover-2026-10-08/`: 20 de 20 correctos.
  - Instancia activa en solo lectura: `solgases-mysql`, MySQL 8.4.12, `healthy`, solo en `127.0.0.1:3307`, volumen `solgases_solgases-mysql84-data`, 11 tablas, 0 filas, sin `root@%` ni `healthchecker`. Su inventario coincide con el registrado tras el corte.
  - El volumen 8.0 original no lo usaba ningún contenedor.
  - Los nombres `solgases-rbk*` y los puertos 3321, 3322 y 18082 estaban libres.
- **Fase A, restauración de la copia en frío:**
  - **Restauración:** volumen desechable `solgases-rbk80-data`, extraído de `vol80-cold.tar` con el directorio de respaldos montado en solo lectura. Los 178 archivos coinciden con `vol80.sha256`.
  - **Arranque:** proyecto Compose `solgases-rbk`, derivado de `docker-compose.yml.before`, con la imagen `mysql:8.0` fijada por digest, el puerto `127.0.0.1:3321` y el mismo healthcheck. Las credenciales se leyeron de `.env` a través de Compose, sin mostrarlas.
  - **Resultado:** MySQL 8.0.46 llegó a `healthy` sin volver a inicializarse. El inventario fue idéntico al anterior al corte en los 17 tipos comparados y en el DDL: 10 tablas, cuentas, permisos y datos.
- **Fase B, aplicación actual con Connector/J 9.7.0** (copia temporal del proyecto, sin `.env` ni `target/`, en la que solo se quitó `<mysql.version>` y su comentario del `pom.xml`):
  - **Build:** `dependency:tree` resolvió `mysql-connector-j:jar:9.7.0:runtime`. `mvn -B clean verify` terminó con `BUILD SUCCESS`, 441 pruebas sin fallos, y el jar incluye `mysql-connector-j-9.7.0.jar`.
  - **Arranque:** perfil `local`, puerto 18082 y URL de datos fijada a `127.0.0.1:3321`. Arrancó sin `ERROR` ni `WARN`.
  - **Conexiones:** 10 conexiones del usuario de la aplicación identificadas como «MySQL Connector/J 9.7.0». La instancia activa no recibió conexiones nuevas: el total del usuario de la aplicación siguió en 10.
  - **Esquema y datos:** Hibernate creó `tbl_user_credential` (InnoDB, `utf8mb4_0900_ai_ci`) y la carga inicial creó los 9 permisos y los 2 roles. No hubo usuarios ni credenciales.
  - **REST:** sin token, 401; usuario desconocido en `/api/auth/token`, 401; token con firma inválida, 401; `GET /v3/api-docs` público en `local`, 200. La aplicación se detuvo de forma ordenada.
- **Fase C, datos actuales de 8.4 en una 8.0.46 desechable:**
  - **Volcado:** `mysqldump` 8.4.12 de solo lectura de la instancia activa: código 0, 11 `CREATE TABLE`, 0 `INSERT`.
  - **Restauración:** en el proyecto `solgases-rbk-fwd`, con MySQL 8.0.46, credenciales temporales aleatorias y `127.0.0.1:3322`. Terminó con código 0 y sin errores.
  - **Resultado:** idénticos al inventario activo el esquema, las tablas, columnas, índices, FK, restricciones, *checks*, objetos, datos y DDL, incluida `tbl_user_credential`.
- **Tiempos medidos:**

  | Paso | Duración |
  |---|---|
  | Restauración del volumen desde la copia en frío | 1,0 s |
  | Arranque de 8.0.46 hasta `healthy` | 10,8 s |
  | `clean verify` con 9.7.0 | 37 s |
  | Arranque de la aplicación | 6,1 s |
  | Fase C (volcado, instancia nueva, restauración) | 11,5 s |

  La parte automatizable de la reversión tardó **unos 55 s** sin la fase C. A eso se suman la edición manual de `docker-compose.yml` y del `pom.xml`, y detener la instancia activa, que no se midieron porque no se ejecutaron sobre recursos reales. Si el volumen 8.0 original sigue intacto, como ahora, la reversión real no necesita restaurar la copia en frío.
- **Limpieza y estado final:**
  - eliminados solo los contenedores, redes y volúmenes `solgases-rbk*` y el directorio temporal: la copia del proyecto, logs y el volcado de la fase C;
  - nunca se usó `down -v`;
  - `SHA256SUMS` seguía correcto (20 de 20);
  - la instancia activa quedó igual: mismo id, hora de arranque, imagen, puerto y volumen, y `healthy`;
  - el volumen 8.0 original sigue sin usar por ningún contenedor y su contenido coincide archivo a archivo con `vol80.sha256`, comprobado montándolo en solo lectura.
- **Limitaciones:**
  - las bases tienen 0 filas de negocio, así que la comparación de datos es trivial;
  - no se ensayó la edición real de `docker-compose.yml` ni del `pom.xml` del repositorio, solo sus equivalentes en copias temporales;
  - no se probaron operaciones REST autenticadas, porque no hay usuarios reales;
  - el ensayo usó la copia en frío, no el volumen 8.0 original, que no se montó en escritura;
  - la fase C demuestra que el esquema actual de 8.4 se carga en 8.0, pero no cubre funciones exclusivas de 8.4, que hoy no se usan;
  - la copia temporal del proyecto incluyó al principio carpetas ocultas de herramientas locales (`.aws`, `.codex`, `.agents`). Se borraron de la copia sin leerlas antes de compilar, y los originales no se tocaron.

Fuentes: [avisos de seguridad de Apache Tomcat 11](https://tomcat.apache.org/security-11), [tabla de compatibilidad de Connector/J](https://dev.mysql.com/doc/connector-j/en/connector-j-versions.html), [guía oficial de Connector/J](https://dev.mysql.com/doc/connector-j/en/), [notas de Connector/J 8.4.0](https://dev.mysql.com/doc/relnotes/connector-j/en/news-8-4-0.html), [notas de Connector/J 26.7.0](https://dev.mysql.com/doc/relnotes/connector-j/en/news-26-7-0.html), [notas de MySQL 8.4.11](https://dev.mysql.com/doc/relnotes/mysql/8.4/en/news-8-4-11.html), [Oracle Critical Patch Update de julio de 2026](https://www.oracle.com/security-alerts/cpujul2026.html), [GHSA-6688-9rhm-gjv2](https://github.com/cure53/DOMPurify/security/advisories/GHSA-6688-9rhm-gjv2) y [GHSA-p98j-92pf-mc4p](https://github.com/cure53/DOMPurify/security/advisories/GHSA-p98j-92pf-mc4p).

**Matriz endpoint–permiso:** desde el 2026-10-08 contiene las 38 reglas aprobadas (ver «Catálogo, matriz y credenciales»). *Antecedente histórico (anterior a las decisiones del 2026-10-08).* `EndpointPermissionMatrixConfiguration` definía una matriz **vacía** mientras el catálogo y la matriz reales no estén aprobados. Por tanto, en la aplicación actual cualquier usuario autenticado recibe 403 en todas las rutas protegidas, incluido Swagger. Las pruebas usan una matriz con claves ficticias (`TEST_*`) definida solo en el código de prueba; no constituye ni anticipa el catálogo real.

**Pendientes y limitaciones**

| Punto | Estado |
|---|---|
| Catálogo de roles y permisos | **Implementado (2026-10-08):** 9 permisos de negocio; roles `ADMIN` y `VIEWER`. `INVENTORY_OPERATOR` sigue pendiente de definición del negocio |
| Matriz endpoint–permiso | **Implementada:** 38 operaciones protegidas con denegación por defecto. Swagger/OpenAPI público en `local`, `dev` y `qa` y desactivado en `prd` |
| Aprovisionamiento del primer administrador | **Ejecutado (2026-10-08):** `create-first-admin` creó el administrador inicial en la base local (1 administrador activo con credencial Argon2id). Mecanismo local sin servidor web ni credenciales predeterminadas. *Antecedente:* tras implementarse quedó pendiente de una ejecución autorizada |
| Credenciales de otros usuarios | Comando local `provision-credential` para usuarios sin credencial; no sobrescribe. No hay API administrativa de credenciales ni cambio de contraseña (pendientes para un incremento posterior) |
| Política de contraseñas | **Aprobada e implementada (2026-10-08):** de 15 a 128 caracteres contados como puntos de código Unicode, sin reglas de composición; ver «Política de contraseñas». *Antecedente histórico:* hasta entonces no había política aprobada y el comando solo rechazaba contraseñas vacías |
| Riesgos de administración detectados (sin reglas nuevas) | **Aceptado solo para uso local (2026-10-08):** el último administrador activo puede desactivarse o perder el rol `ADMIN`, lo que bloquearía la administración (en local, `create-first-admin` vuelve a estar disponible si no queda ningún administrador activo). `USER_WRITE` permite asignar cualquier rol, incluido `ADMIN`, por eso solo lo tiene `ADMIN`. **Debe revisarse y resolverse antes de desplegar fuera de local** |
| Orígenes CORS | Pendiente; CORS deshabilitado |
| Clave JWT por entorno | Debe proporcionarse fuera del repositorio (`JWT_SECRET`); `.env.example` incluye la variable vacía. Rotación de claves, emisor y audiencia no se han definido ni implementado |
| Coste de Argon2id en el entorno objetivo | Mínimo OWASP medido solo en la máquina local; ajustar en el Incremento 8 |
| Análisis de dependencias | **Vigente (tras el corte del 2026-10-08):** Connector/J 26.7.0 y Tomcat 11.0.26, con la base NVD actualizada: 0 críticos, 0 altos, 0 medios y 2 bajos (GHSA de DOMPurify en `swagger-ui` 5.32.14, **aceptados temporalmente el 2026-10-08** hasta que exista un WebJar corregido). OSS Index no se consultó por falta de credenciales. *Antecedente histórico:* los análisis anteriores con Connector/J 9.7.0 reportaron 2 altos y 2 medios en el driver. Se evaluaron temporalmente 8.4.0 (no adoptada) y 26.7.0 con MySQL 8.4.11 y 8.4.12; ver los párrafos históricos |
| Healthcheck MySQL de Compose | **Resuelto:** se conserva `mysqladmin ping -h localhost`. `healthy` solo confirma que MySQL responde por socket; no valida credenciales, base de datos ni TCP. En 8.4.12 el comando respondió durante la inicialización, pero en la prueba Docker no llegó a marcar `healthy` antes de tiempo. El corte deberá comprobar aparte la conexión de la aplicación |
| Privilegio `PROXY` de `root@%` (imagen Oracle 8.4.12) | **Resuelto:** observado solo en la prueba desechable con `MYSQL_ROOT_HOST=%`. Se acepta como comportamiento de la imagen; el corte omite esa variable y no crea `root@%` |
| Corte local a MySQL 8.4.12 y Connector/J 26.7.0 | **Ejecutado y verificado (2026-10-08).** MySQL 8.4.12 local en `127.0.0.1:3307` y Connector/J 26.7.0. Volumen 8.0 conservado intacto y desconectado; su limpieza se decidirá aparte. Reversión documentada y **ensayada el 2026-10-08** en recursos desechables (≈55 s de pasos automatizables) |
| Límite de intentos de autenticación | Aplazado a un incremento posterior. **Debe resolverse antes de exponer la API fuera del entorno local** |
| Colación de `tbl_user.username` | **Política aprobada:** no distingue mayúsculas ni acentos. Colación real confirmada en la base local: `utf8mb4_0900_ai_ci`, con índice único. Sin cambios de esquema |
| Ensayo de reversión en recursos desechables | **Ejecutado (2026-10-08):** restauración desde la copia en frío, aplicación con Connector/J 9.7.0 sobre MySQL 8.0.46 y paso de los datos actuales de 8.4 a 8.0, todo satisfactorio. Ver «Resultado del ensayo de reversión» y sus limitaciones |
| Prueba del 403 con un usuario real | **Hecha (2026-10-08):** `viewer-test` (`VIEWER`) obtuvo 200 en categorías y productos y 403 en usuarios y roles |
| Usuario de prueba `viewer-test` | **Desactivado (2026-10-08)** por la API y conservado como evidencia de prueba, con su rol y su credencial. Inactivo, no puede autenticarse |
| Errores del comando local de credenciales | **Limitación aceptada para uso local:** si `create-first-admin` o `provision-credential` fallan, el mensaje puede incluir el nombre de usuario introducido, por ejemplo cuando ya existe. Nunca incluye la contraseña |
| Riesgos residuales | **Aceptados solo para uso local (2026-10-08).** Bloquean exponer o desplegar fuera de local hasta resolverse: límite de intentos de autenticación, riesgo del último administrador, orígenes CORS, rotación, emisor y audiencia de la clave JWT, coste de Argon2id en el entorno objetivo y configuración de cuentas de QA y producción. Otros riesgos aceptados: DOMPurify (temporal), healthcheck por socket, copias de contraseñas en memoria fuera del comando, errores del comando con el nombre de usuario y OSS Index sin consultar |
| Pendientes acordados | Ajuste del coste de Argon2id (Incremento 8), OSS Index (requiere credenciales), limpieza del volumen MySQL 8.0 y de la copia en frío (decisión aparte) |
| Revisión humana de pruebas y cambios asistidos por IA | **Aplazada al cierre del MVP** (decisión del 2026-10-08); no es requisito para cerrar el Checkpoint 7 |

---

## Incremento 8 — DevOps: CI/CD, Docker y despliegue

### Objetivo

Preparar la aplicación para entrega repetible mediante Git, Jenkins, contenedores Docker, despliegues automatizados y observabilidad básica.

### Alcance

- Definir flujo Git acordado (ramas, pull requests/revisión y protección de ramas) e integrarlo con un pipeline declarativo de Jenkins (`Jenkinsfile`).
- Automatizar compilación, pruebas, cobertura JaCoCo, análisis SonarQube y empaquetado Maven; detener la promoción ante fallos de pruebas o Quality Gate.
- Crear una imagen Docker reproducible, preferiblemente multi-stage y no ejecutada como root; excluir secretos y archivos locales; etiquetar artefactos con versión y commit.
- Usar IA para proponer Dockerfile, Jenkinsfile y scripts operativos; toda salida se revisa antes de integrarla, y nunca se proporcionan secretos o credenciales a la herramienta.
- Definir publicación de artefactos/imágenes en un registro autorizado y uso del almacén de credenciales de Jenkins.
- Automatizar configuración y despliegue por ambiente, smoke tests, verificación de salud y estrategia de rollback.
- Mantener los perfiles `local`, `dev`, `qa` y `prd`, separando configuración y secretos de los artefactos.
- Configurar logs estructurados con datos útiles sin credenciales o tokens; definir monitoreo básico de disponibilidad, salud y errores. Elegir Actuator u otra alternativa tras revisar exposición y seguridad.
- Documentar operación, diagnóstico, promoción y recuperación.

### Objetivo de despliegue: WebLogic

WebLogic es el destino preferido por el responsable del proyecto, y Jenkins es la herramienta CI/CD preferida. El despliegue WebLogic queda condicionado a una prueba técnica de compatibilidad antes de convertirlo en ruta obligatoria: el repositorio usa Spring Boot 4.1.1, que requiere contenedor Servlet 6.1+ para despliegue tradicional; la documentación de Oracle para WebLogic 15c (15.1.1) declara compatibilidad Jakarta EE 9.1 / Servlet 5.0. Por tanto, esa versión no satisface el requisito de contenedor documentado por Spring Boot 4.1.1. La primera tarea será verificar la versión concreta de WebLogic disponible y desplegar un WAR mínimo de prueba. Si no es compatible, el responsable decidirá entre una versión/plataforma compatible con Spring Boot 4.1.1 o un cambio aprobado de versión/stack; no bajar Spring Boot ni sustituir WebLogic por iniciativa propia. Referencias: [requisitos Spring Boot 4.1.1](https://docs.spring.io/spring-boot/system-requirements.html), [compatibilidad WebLogic 15c](https://docs.oracle.com/en/middleware/standalone/weblogic-server/15.1.1/intro/compatibility.html).

JaCoCo (`0.8.15`, con soporte oficial de Java 25 desde 0.8.14), SonarScanner for Maven (`5.8.0.7211`) y SonarQube Community Build (`26.9.0.129388`, con SonarJava `8.41.0.47177`) quedaron fijados en el Incremento 6; el pipeline debe reutilizar esas versiones o justificar su cambio. Antes de establecer un Quality Gate obligatorio, decidir la edición y el alojamiento de SonarQube para CI y confirmar que la versión elegida mantiene el soporte del analizador para Java 25. Jenkins debe ejecutarse en una versión con soporte para Java 25 o configurarse con una JVM soportada para el controlador y un JDK 25 para compilar; comprobar compatibilidad de plugins requeridos. Referencias: [cambios JaCoCo](https://www.jacoco.org/jacoco/trunk/doc/changes.html), [política Java de Jenkins](https://www.jenkins.io/doc/book/platform-information/support-policy-java/), [analizador Java de SonarQube Community Build](https://docs.sonarsource.com/sonarqube-community-build/analyzing-source-code/languages/java).

### Decisiones pendientes

- Versión/licencia/entorno concreto de WebLogic, SO/JDK certificados, modalidad WAR y conexión/credenciales JNDI o JDBC.
- Servidor Jenkins, versión, agentes, plugins permitidos, hosting Git y estrategia de ramas.
- Registro de imágenes/artefactos, ambientes y credenciales; nunca incluir secretos en el `Jenkinsfile` o imagen.
- Umbrales del Quality Gate heredados del Incremento 6, aprobación de promoción, ventanas de despliegue y política de rollback.
- Herramienta/alcance de métricas, health checks, almacenamiento y centralización de logs.

### Checkpoint 8

Un cambio integrado en Git ejecuta pipeline reproducible con compilación, pruebas, JaCoCo y SonarQube; la imagen Docker puede reconstruirse y no contiene secretos; el artefacto se publica en el registro aprobado; el despliegue automatizado y smoke tests pasan en un ambiente autorizado; logs y health checks aportan diagnóstico; la compatibilidad WebLogic queda demostrada con la versión elegida o se registra una decisión explícita de cambio de destino/stack.

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
- ~~Política técnica de credenciales y JWT (Incremento 7).~~ **Resuelto:** autenticación local, hash Argon2id separado de `domain.User`, JWT HS256 de 15 minutos sin refresh tokens y verificación actual de estado/permisos en cada solicitud. El catálogo de roles y permisos, la matriz endpoint–permiso y el aprovisionamiento inicial del administrador se aprobaron e implementaron el 2026-10-08, y el administrador inicial existe en la base local (ver Incremento 7).
- Orígenes CORS autorizados para los clientes (Incremento 7); CORS permanece deshabilitado mientras no se definan.
- Versión concreta de WebLogic y compatibilidad con Spring Boot 4.1.1/Servlet 6.1+ (Incremento 8).
- Hosting Git, estrategia de ramas, instancia/agentes/plugins de Jenkins, registro de imágenes, ambientes y política de despliegue/rollback (Incremento 8).
- ~~Versión exacta de SonarQube/analyzer y soporte de Java 25 (Incremento 6).~~ **Resuelto:** Community Build `26.9.0.129388` con SonarJava `8.41.0.47177`, documentado en el Incremento 6. Sigue pendiente: condiciones obligatorias de promoción del Quality Gate (Incremento 8).
- Enfoque de contenedorización y monitoreo de aplicación, incluyendo selección y exposición segura de health checks/logs (Incremento 8).
- Detalle adicional de la granularidad de permisos, si resulta necesario para las operaciones aprobadas.
- ~~Formato estándar de errores.~~ **Resuelto:** `ProblemDetail` (`application/problem+json`) para la API actual; las respuestas 401/403 del Incremento 7 seguirán el mismo formato (ver §9).
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
3. Tratar las decisiones aprobadas en este documento como autorización suficiente para implementarlas; no detenerse para volver a presentarlas para aprobación.
4. Identificar decisiones pendientes y distinguirlas de las aprobadas.
5. No inventar requisitos ni implementar funcionalidades fuera del alcance.
6. Si una decisión pendiente bloquea un elemento no esencial, continuar con el resto del alcance y reportar el bloqueo de ese elemento. Solicitar aclaración solo si impide implementar el núcleo aprobado de forma segura o correcta.

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
