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
- Implementar autenticación JWT para usuarios internos. El modelo `User` no almacenará credenciales; la estrategia de credenciales y su persistencia deben definirse separadamente y revisarse antes de implementarse.
- Aplicar denegación por defecto y proteger explícitamente rutas y operaciones mediante una matriz de políticas aprobada.
- Resolver autoridades a partir de permisos con claves internas estables; no basar políticas duraderas en nombres editables de roles ni códigos editables de permisos.
- Impedir autenticación de usuarios inactivos y definir cómo se reflejan desactivaciones, cambios de roles/permisos y tokens emitidos.
- Proteger secretos de firma y credenciales mediante configuración externa; no guardar secretos en el repositorio, imágenes Docker, logs ni colección Postman.
- Responder los errores de autenticación (401) y autorización (403) con el formato estándar de errores del proyecto, `ProblemDetail` (`application/problem+json`), definido en §9.
- Añadir pruebas de autenticación/autorización, expiración y rechazo de JWT inválidos, usuario inactivo, acceso anónimo, concesiones y denegaciones.
- Revisar dependencias y resultados de análisis de vulnerabilidades; usar IA para apoyar el triage, sin aceptar ni aplicar automáticamente cambios de seguridad sin revisión.
- Configurar CORS solo cuando se conozcan los orígenes de los clientes que se autorizarán.

### Decisiones pendientes antes de cerrar el diseño

- Credenciales: autenticación local o proveedor externo; política de contraseña, almacenamiento/hash y recuperación. No agregar la contraseña a `domain.User`.
- Rutas públicas y protegidas, matriz endpoint–permiso, roles/permisos reales y catálogo semilla, que sigue pendiente del negocio.
- Algoritmo/gestión de claves de firma, emisor/audiencia, duración de access token, claims y estrategia de rotación.
- Si habrá refresh tokens, revocación, invalidación inmediata al desactivar/cambiar permisos y CORS. No implementar refresh tokens por defecto.
- Herramienta/edición para análisis de dependencias y vulnerabilidades, fuentes de datos, credenciales de acceso y criterios de aceptación de hallazgos. Esta decisión se toma en este incremento; no bloquea el Incremento 6.

### Checkpoint 7

La autenticación y las políticas aprobadas se verifican con pruebas positivas y negativas; rutas no declaradas públicas requieren autenticación; usuario inactivo y permisos insuficientes son rechazados; secretos no aparecen en código ni logs; el análisis de dependencias/vulnerabilidades se ejecuta y sus excepciones quedan documentadas. No habilitar despliegues no locales hasta superar este checkpoint y aprobar el riesgo residual.

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
- Origen y política de credenciales, ciclo de vida de JWT y matriz de políticas por endpoint (Incremento 7).
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
