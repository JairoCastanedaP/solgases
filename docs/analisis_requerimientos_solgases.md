# SOLGASES APP — Análisis de Requerimientos

## 1. Contexto

SOLGASES es una empresa de seguridad industrial ubicada en Sogamoso, Boyacá, Colombia. Su actividad contempla la comercialización de elementos de protección personal para empresas, la recarga de extintores, reparación de equipos de soldadura y comercialización de gases industriales, entre ellos oxígeno y acetileno.

El proyecto SOLGASES APP busca construir progresivamente una plataforma web para apoyar la gestión de la operación y la interacción con clientes.

El primer alcance será exclusivamente backend, desarrollado en Java y bajo los lineamientos técnicos suministrados para el curso de desarrollo asistido por IA.

## 2. Objetivo general

Construir una solución que permita gestionar el catálogo e inventario de productos y servicios de SOLGASES, administrar usuarios y roles, exponer APIs REST y preparar la plataforma para futuras capacidades de atención al cliente mediante chatbot e integración con WhatsApp.

## 3. Alcance general esperado

La solución completa contempla, de manera progresiva:

- Gestión de usuarios.
- Gestión de roles y permisos.
- Gestión de productos.
- Gestión de categorías.
- Gestión de inventario.
- Consulta del catálogo.
- Gestión de servicios ofrecidos por la empresa.
- Chatbot para recomendaciones de productos.
- Integración mediante botón de WhatsApp.
- APIs REST para consumo por clientes web u otros consumidores.
- Documentación y pruebas de la API.

El MVP 1 se limita al backend y no contempla una interfaz web.

## 4. Actores

### 4.1 Administrador

Usuario con permisos para administrar información maestra, usuarios, roles y operación del inventario, de acuerdo con los permisos que finalmente se definan.

### 4.2 Usuario interno

Empleado o colaborador que utiliza las funciones del sistema que le sean autorizadas mediante su rol.

### 4.3 Cliente

Persona o empresa que consulta productos y, en fases posteriores, podrá recibir recomendaciones y comunicarse con SOLGASES.

### 4.4 Sistema

Componente backend responsable de aplicar reglas de negocio, persistir información y exponer APIs.

### 4.5 Servicios externos

En fases posteriores pueden incluir un proveedor de WhatsApp y un proveedor/modelo de IA para el chatbot. La selección concreta queda pendiente.

## 5. Requerimientos funcionales

### RF-01 — Gestión de usuarios

El sistema deberá permitir gestionar usuarios internos.

Como mínimo deberá contemplarse:

- Crear usuario.
- Consultar usuario.
- Actualizar usuario.
- Activar/desactivar usuario.
- Asociar usuario a uno o más roles, si esta decisión se confirma.

**Decisiones pendientes:** campos obligatorios del usuario, política de contraseñas, autenticación y si un usuario puede tener múltiples roles.

### RF-02 — Gestión de roles y permisos

El sistema deberá permitir definir roles y controlar las operaciones disponibles para cada rol.

Se recomienda separar conceptualmente:

- Usuario.
- Rol.
- Permiso.

La granularidad definitiva de permisos queda pendiente de definición.

### RF-03 — Gestión de categorías

El sistema deberá permitir organizar los productos mediante categorías.

Ejemplos iniciales:

- Elementos de protección personal.
- Gases industriales.
- Equipos de soldadura.
- Extintores.
- Otros productos.

Los ejemplos anteriores son una propuesta inicial y deben validarse con SOLGASES.

### RF-04 — Gestión de productos

El sistema deberá permitir registrar y administrar productos.

Como información potencial se considera:

- Identificador.
- Nombre.
- Descripción.
- Categoría.
- Marca.
- Referencia.
- Estado.
- Precio, si corresponde al alcance.
- Unidad de medida.
- Información de inventario.
- Datos específicos para comercialización.

Los campos definitivos deben levantarse con el negocio.

### RF-05 — Gestión de inventario

El sistema deberá permitir controlar existencias de productos.

Se deberá definir, como mínimo:

- Existencia actual.
- Entrada de inventario.
- Salida de inventario.
- Ajuste de inventario.
- Motivo del movimiento.
- Fecha del movimiento.
- Usuario responsable.

**Decisión pendiente:** determinar si se requiere manejo de lotes, fechas de vencimiento, números de serie o trazabilidad específica para determinados productos.

### RF-06 — Consulta de productos

El backend deberá proporcionar APIs para consultar productos y permitir, progresivamente:

- Consulta individual.
- Listado.
- Filtrado.
- Búsqueda.
- Consulta por categoría.
- Consulta por disponibilidad.

### RF-07 — Gestión de servicios

La solución deberá contemplar los servicios que presta SOLGASES, tales como:

- Recarga de extintores.
- Reparación/arreglo de equipos de soldadura.

El alcance exacto de la gestión de servicios queda pendiente de validación. No se debe asumir que estos servicios requieren agenda, órdenes de trabajo o facturación hasta que el negocio lo confirme.

### RF-08 — Chatbot de recomendaciones

En una fase posterior, el sistema deberá permitir que un cliente consulte productos mediante lenguaje natural y reciba recomendaciones.

El chatbot deberá basar sus respuestas en información disponible y autorizada del catálogo.

**Decisiones pendientes:**

- Proveedor/modelo de IA.
- Estrategia de recuperación de información.
- Información que podrá consultar la IA.
- Límites de las recomendaciones.
- Manejo de consultas sin coincidencias.
- Protección de información sensible.
- Costos y límites del proveedor.

### RF-09 — Integración con WhatsApp

En una fase posterior deberá existir un mecanismo para que el cliente pueda iniciar comunicación con SOLGASES mediante WhatsApp.

El requisito inicial planteado es un botón que redirija al canal de WhatsApp de la empresa.

**Decisión pendiente:** definir si se requiere únicamente redirección mediante enlace o una integración programática mediante la API de WhatsApp.

## 6. Requerimientos no funcionales

Los siguientes requisitos técnicos se derivan directamente de los lineamientos entregados:

### RNF-01 — Plataforma

- Java 25.
- Spring Boot 4.1.1.
- Spring Framework 7.
- Jakarta EE.
- Maven 3.9+.
- Spring Data JPA.
- MySQL.
- APIs REST.
- JUnit 5.
- Bean Validation.
- Swagger/OpenAPI mediante springdoc-openapi.
- Postman.

### RNF-02 — Arquitectura

El proyecto deberá utilizar estructura Maven estándar y organizar los paquetes por funcionalidad, manteniendo dentro de cada feature sus capas de controller, service, repository, entity y dto cuando sean necesarias. fileciteturn0file0L30-L44

### RNF-03 — Diseño

Se deberán aplicar principios SOLID, mantener clases cohesionadas, evitar abstracciones innecesarias y no introducir patrones de diseño de forma prematura. fileciteturn0file0L46-L56

### RNF-04 — APIs

Los controladores deberán centrarse en aspectos HTTP y la lógica de negocio deberá permanecer en la capa de servicio. Las solicitudes deberán validarse mediante Bean Validation. fileciteturn0file0L68-L75

### RNF-05 — Persistencia

El acceso a MySQL deberá realizarse mediante Spring Data JPA y repositories. Las entidades de persistencia no deberán exponerse directamente por REST; se deberán utilizar DTOs. fileciteturn0file0L77-L82

### RNF-06 — Configuración

Los secretos no deberán almacenarse en código ni en archivos versionados. Se deberán utilizar variables de entorno u otro mecanismo externo. Se utilizarán perfiles `local`, `dev`, `qa` y `prd`. fileciteturn0file0L84-L93

### RNF-07 — Pruebas

Las pruebas unitarias deberán utilizar JUnit 5. Las pruebas unitarias deberán mantenerse aisladas de base de datos y red; `@SpringBootTest` se reservará para pruebas de integración que realmente lo requieran. fileciteturn0file0L96-L100

### RNF-08 — Documentación de API

Todos los endpoints deberán documentarse mediante OpenAPI/Swagger, incluyendo resumen, parámetros, cuerpos de petición, respuestas y códigos de error. fileciteturn0file0L102-L108

### RNF-09 — Postman

Al finalizar el backend deberá generarse una colección Postman basada en la especificación OpenAPI, organizada por funcionalidad y sin credenciales reales. fileciteturn0file0L110-L119

### RNF-10 — Desarrollo asistido por IA

Antes de cambios significativos se deberá inspeccionar el código existente, explicar los cambios, identificar supuestos y no inventar requisitos. Después de los cambios se deberá compilar, ejecutar las pruebas y reportar archivos modificados y problemas pendientes. fileciteturn0file0L122-L139

## 7. Reglas de negocio iniciales

Estas reglas deben considerarse propuestas de trabajo hasta ser validadas con SOLGASES.

### RN-01 — Estado de producto

Un producto deberá tener un estado que permita distinguir, como mínimo, productos activos de productos que ya no se comercializan.

### RN-02 — Inventario no negativo

Se propone impedir que una operación produzca existencias negativas, salvo que el negocio determine expresamente otro comportamiento.

### RN-03 — Trazabilidad

Cada movimiento de inventario deberá registrar el producto, tipo de movimiento, cantidad, fecha y usuario responsable.

### RN-04 — Identificación

Cada producto deberá poseer un identificador único.

### RN-05 — Separación de responsabilidades

La creación y modificación de información de catálogo e inventario deberá estar restringida según permisos.

### RN-06 — Integridad

Los datos obligatorios deberán validarse antes de persistirse.

### RN-07 — Catálogo vs. inventario

La existencia de un producto en el catálogo no deberá interpretarse automáticamente como disponibilidad comercial. La disponibilidad deberá derivarse del estado definido por el negocio y/o del inventario.

### RN-08 — IA

El chatbot no deberá inventar características, disponibilidad o condiciones comerciales. Sus respuestas deberán estar fundamentadas en información disponible para el sistema y deberá poder indicar cuando no dispone de información suficiente.

## 8. Modelo conceptual inicial

Las entidades o conceptos candidatos son:

- User.
- Role.
- Permission.
- Product.
- Category.
- Inventory.
- InventoryMovement.
- Service.

Entidades futuras:

- Customer.
- Conversation.
- Recommendation.
- WhatsAppIntegration.

Esta lista no constituye todavía un modelo de datos definitivo.

## 9. Criterios de aceptación de alto nivel para MVP 1

El MVP 1 podrá considerarse funcional cuando:

1. La aplicación arranque correctamente con Java 25 y Spring Boot 4.1.1.
2. La estructura del proyecto siga los lineamientos establecidos.
3. La aplicación pueda conectarse a MySQL.
4. Existan APIs REST para las funcionalidades incluidas en el MVP.
5. Las entradas inválidas sean rechazadas mediante validación.
6. Las entidades no sean expuestas directamente por los endpoints.
7. Existan pruebas unitarias relevantes.
8. La API esté documentada mediante OpenAPI.
9. Exista una colección Postman correspondiente a los endpoints implementados.
10. El proyecto compile y las pruebas pertinentes se ejecuten satisfactoriamente.

## 10. Decisiones pendientes con el negocio

Antes de implementar funcionalidades avanzadas conviene validar:

- Qué productos y categorías existen realmente.
- Qué atributos requiere cada tipo de producto.
- Cómo se manejan unidades de medida.
- Cómo se controla el inventario actualmente.
- Si existen productos con lotes, seriales o vencimiento.
- Quién puede modificar inventario.
- Qué roles existen.
- Si habrá clientes registrados.
- Política de precios.
- Manejo de proveedores.
- Manejo de servicios y órdenes de trabajo.
- Reglas para gases industriales y productos que tengan condiciones especiales de comercialización.
- Reglas de disponibilidad.
- Información que podrá utilizar el chatbot.
- Canal exacto de WhatsApp.
- Necesidad real de integración API con WhatsApp.

## 11. Recomendaciones sobre `lineamientos.md`

El documento actual constituye una buena base para el MVP backend. Estas recomendaciones son **propuestas adicionales**, no requisitos que ya estén definidos.

### 11.1 Agregar una política de manejo de errores

Definir un formato estándar para respuestas de error, por ejemplo:

- timestamp.
- status.
- code.
- message.
- path.
- validationErrors, cuando aplique.

También conviene centralizar el manejo mediante un mecanismo equivalente a `@RestControllerAdvice`.

### 11.2 Definir convenciones REST

Documentar convenciones para:

- nombres de recursos.
- códigos HTTP.
- paginación.
- filtros.
- ordenamiento.
- versionado de API.
- formato de errores.

### 11.3 Definir estrategia de migraciones de base de datos

Se recomienda incorporar una herramienta de migraciones, por ejemplo Flyway, pero esta decisión debe aprobarse porque `lineamientos.md` indica que no se deben introducir frameworks adicionales salvo solicitud expresa. Por tanto, no debe agregarse automáticamente.

### 11.4 Definir auditoría

Para inventario resulta recomendable establecer quién creó/modificó registros y cuándo. Para movimientos de inventario, la trazabilidad debería ser obligatoria.

### 11.5 Definir transacciones

Documentar cuándo una operación de negocio debe ejecutarse dentro de una transacción, especialmente movimientos de inventario.

### 11.6 Definir seguridad

El documento actual define usuarios y roles como necesidad funcional, pero todavía no especifica el mecanismo de autenticación/autorización. Antes de implementar seguridad se debería decidir el mecanismo, gestión de credenciales, expiración de sesiones/tokens y permisos.

### 11.7 Revisar la versión de Java

El archivo identifica Java 25 como LTS. Para el proyecto del curso se deberá mantener exactamente la versión requerida por el docente y verificar la compatibilidad concreta de Spring Boot 4.1.1 y las dependencias seleccionadas.

### 11.8 Calidad de código

Podría añadirse posteriormente una política para formato, análisis estático, convenciones de nombres, cobertura mínima y estrategia de integración continua. No son necesarias para iniciar el MVP 1 si el curso todavía no las exige.

## 12. Supuestos

- El backend será la primera pieza implementada.
- La interfaz web se desarrollará en un MVP posterior.
- El chatbot no forma parte del MVP 1.
- WhatsApp no forma parte del MVP 1.
- Los requisitos de negocio todavía requieren validación con SOLGASES.
- Los nombres de entidades y atributos son conceptuales hasta completar el diseño técnico.
