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

Clean Architecture en tres módulos Maven, de acuerdo con `docs/lineamientos.md` y el modelo F2/02 del curso:

```text
domain/          # Modelos y reglas de negocio sin dependencias de frameworks
application/     # Casos de uso, puertos, DTOs de aplicación y excepciones
infrastructure/  # Adaptadores REST y de persistencia, mappers y configuración Spring
```

Las dependencias apuntan hacia el dominio: `infrastructure → application → domain`. Los controladores REST llaman puertos de entrada de la aplicación; los adaptadores de persistencia implementan sus puertos de salida. La estructura interna se rige por `docs/lineamientos.md`.

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

- Pruebas unitarias de casos de uso.
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
- Autenticación y autorización del Incremento 7 antes de exponer el frontend fuera de desarrollo local.

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

Una posible separación futura, respetando los módulos y límites de dependencias del proyecto:

```text
application/
└── casos de uso y puertos
infrastructure/
└── adaptador de integración con IA
```

El adaptador de IA debería consultar información mediante puertos y casos de uso controlados, en lugar de acceder directamente a las entidades JPA. Su ubicación definitiva se definirá al aprobar ese MVP.

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

La autorización completa queda para una etapa posterior a la gestión administrativa User / Role / Permission y requiere una decisión específica. Hasta entonces el Incremento 5 se usa únicamente en local; no se simula esa limitación con un guard basado en perfil técnico. Consulta `mvp1.md` para las decisiones aprobadas del Incremento 5.

**Estado del Incremento 5: implementado y verificado (Checkpoint 5).** Administración persistente de usuarios, roles y permisos, sus relaciones, carga inicial idempotente sin datos semilla inventados y pruebas automatizadas. `mvn -B clean verify` pasó con 251 pruebas, y la verificación contra MySQL confirmó el esquema, las claves foráneas, las restricciones únicas, el comportamiento de `updatedAt` y los códigos HTTP. El catálogo semilla permanece vacío por una decisión pendiente del negocio, por lo que no se probó en MySQL la repetición de una carga con catálogo no vacío. La autenticación, autorización efectiva, JWT, refresh tokens, CORS y auditoría administrativa general no formaron parte de este incremento. Consulta el Checkpoint 5 en `docs/mvp1.md` para el detalle.

---

# Incrementos técnicos transversales

Estos incrementos complementan el MVP 1 y preparan su evolución. El detalle de alcance y checkpoints está en `docs/mvp1.md`.

## Incremento 6 — Automated Testing & Code Quality

Completar estrategia de pruebas, cobertura con JaCoCo, análisis estático y Quality Gate con SonarQube/SonarScanner, refactorización incremental revisada por personas, OpenAPI/Postman, y revisión de errores, logs, configuración y secretos. La línea base de cobertura es informativa; el Quality Gate será report-only, sin umbrales ni bloqueo de compilación al inicio.

**Estado: en curso.** Implementados y verificados: JaCoCo (reportes por módulo y agregado), línea base de cobertura (93,9 % de líneas en el agregado, 285 pruebas), pruebas JPA con H2 para Producto, Unidad de Medida e Inventario, colección Postman con ejemplos genéricos, revisión de errores, OpenAPI, logs, configuración y secretos, y un servidor SonarQube Community Build 26.9 local. El proyecto `solgases` está creado para la rama `feature/clean-architecture`; su token de análisis, limitado a ese proyecto y con vencimiento de 30 días, se guarda en `.sonar.env`, ignorado por Git y sin seguimiento; `.env` ya no contiene `SONAR_TOKEN`. El analizador SonarJava 8.41.0.47177 declara soporte de Java 25; la confirmación empírica queda para el primer análisis. Pendientes: ejecutar el análisis SonarQube manual, revisar sus hallazgos de máxima severidad y completar la revisión humana. El Quality Gate es informativo. Jenkins/CI se difiere al Incremento 8 y el análisis de dependencias al Incremento 7. Detalle en `docs/mvp1.md`.

## Incremento 7 — Securing Modern Applications

Integrar Spring Security, autenticación local con JWT y autorización por políticas/permisos estables, con pruebas de accesos permitidos y denegados, análisis de dependencias con OWASP Dependency-Check/Maven usando NVD y gestión segura de secretos. Se aprobaron Argon2id para las contraseñas, JWT HS256 con access token de 15 minutos y sin refresh tokens, consulta del estado y permisos actuales en cada solicitud protegida, y CORS deshabilitado hasta conocer los orígenes permitidos. Hallazgos críticos bloquean el cierre; los altos requieren triage y remediación o excepción aprobada. El catálogo de roles y permisos, la matriz endpoint–permiso y el aprovisionamiento del primer administrador no se inventaron: se aprobaron el 2026-10-08, antes de implementarlos. Consulta `docs/mvp1.md` para las decisiones y el Checkpoint 7.

**Estado: Checkpoint 7 cerrado para el alcance local (2026-10-08).**

- **Autenticación:**
  - credenciales locales separadas con Argon2id;
  - `POST /api/auth/token` como única operación de negocio pública;
  - JWT HS256 de 15 minutos sin refresh tokens;
  - estado y permisos actuales consultados en cada solicitud;
  - política de contraseñas de 15 a 128 puntos de código Unicode, sin reglas de composición.
- **Autorización:** 9 permisos de negocio, roles `ADMIN` y `VIEWER`, y matriz de 38 operaciones con denegación por defecto y 401/403 en `ProblemDetail`. Swagger/OpenAPI es público en `local`, `dev` y `qa` y está desactivado en `prd`; CORS sigue deshabilitado.
- **Credenciales:** comando local sin servidor web para crear el primer administrador y provisionar credenciales, sin credenciales predeterminadas ni ruta pública de alta.
- **Base de datos y dependencias:**
  - la base local se migró a MySQL 8.4.12 y Connector/J 26.7.0, con la reversión ensayada en recursos desechables;
  - Dependency-Check (NVD): 0 críticos, 0 altos, 0 medios y 2 bajos de DOMPurify en Swagger UI, aceptados temporalmente;
  - OSS Index no se consultó.
- **Verificación:**
  - `mvn -B clean verify`: 468 pruebas sin fallos;
  - pruebas manuales locales con el administrador (login 200, consultas protegidas 200, sin token 401) y con `viewer-test` de rol `VIEWER` (200 en categorías y productos, 403 en usuarios y roles);
  - `viewer-test` quedó desactivado y se conserva como evidencia.
- **Riesgos residuales:** aceptados solo para uso local. Bloquean exponer o desplegar fuera de local hasta resolverse: límite de intentos de autenticación, riesgo del último administrador, orígenes CORS, gestión de la clave JWT, coste de Argon2id en el entorno objetivo y configuración de QA y producción.
- **Revisión humana final:** se hará al cierre del MVP.

*Antecedente histórico:* antes del 2026-10-08 el incremento usaba MySQL 8.0.46 con Connector/J 9.7.0, que tenía hallazgos altos, y la autorización funcional estaba bloqueada con la matriz vacía. Detalle en `docs/mvp1.md`.

## Incremento 8 — DevOps: CI/CD, Docker y despliegue

Integrar Git con un pipeline Jenkins que ejecute build, pruebas, cobertura y análisis SonarQube; crear y publicar imágenes Docker sin secretos; automatizar despliegue, smoke tests, rollback, logs y monitoreo básico. WebLogic es el destino preferido, sujeto a una prueba previa: Spring Boot 4.1.1 requiere Servlet 6.1+, mientras que la documentación de WebLogic 15c (15.1.1) declara Jakarta EE 9.1 / Servlet 5.0. La versión de WebLogic disponible debe confirmarse antes de definir el WAR como ruta de despliegue obligatoria. Ver [detalle y referencias de compatibilidad](mvp1.md#incremento-8--devops-ci-cd-docker-y-despliegue).

## 2. Mantener el dominio preparado para crecer

No introducir todavía entidades de chatbot, WhatsApp o recomendaciones en el MVP 1 solo porque aparecen en el alcance futuro.

## 3. Separar catálogo e inventario

Un producto representa qué vende SOLGASES.

El inventario representa cuántas unidades existen y cómo han cambiado las existencias.

Esta separación facilitará la evolución posterior.

## 4. Diseñar pensando en trazabilidad de inventario

Los movimientos de inventario deben conservar la trazabilidad definida para ese incremento. Esto no implica implementar una auditoría administrativa general en el Incremento 5.

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
- estrategia general de migraciones de esquema (distinta de restablecer datos de desarrollo);
- estrategia de autenticación/autorización;
- auditoría administrativa general (no incluida en el Incremento 5; conservar la trazabilidad requerida para inventario);
- transacciones;
- versionado de API;
- estándares de calidad y análisis estático.

Estas recomendaciones no deben incorporarse como requisitos obligatorios del MVP 1 hasta que sean aprobadas por el docente o por el responsable del proyecto.
