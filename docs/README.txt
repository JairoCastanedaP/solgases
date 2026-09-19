SOLGASES APP
Documentación del proyecto
==========================

Esta carpeta contiene la documentación base utilizada para analizar, planificar y desarrollar SOLGASES APP.

La documentación está separada por responsabilidad para evitar mezclar requisitos de negocio, reglas técnicas, planificación del proyecto y ejecución de un MVP concreto.

ARCHIVOS
--------

1. lineamientos.md

Contiene los lineamientos técnicos y de desarrollo que deben respetarse durante la construcción del proyecto.

Define, entre otros aspectos:

- Lenguaje y versión de Java.
- Versión de Spring Boot.
- Tecnologías permitidas.
- Estructura del proyecto.
- Organización por features.
- Principios SOLID.
- Convenciones de desarrollo.
- Uso de DTOs.
- Persistencia con Spring Data JPA y MySQL.
- Configuración y manejo de secretos.
- Pruebas.
- Documentación OpenAPI.
- Colección Postman.
- Reglas para el desarrollo asistido por IA.

Este documento representa las reglas técnicas de referencia del proyecto.


2. analisis_requerimientos_solgases.md

Contiene el análisis de los requerimientos de SOLGASES APP.

Incluye:

- Contexto del negocio.
- Objetivo general.
- Alcance general.
- Actores.
- Requerimientos funcionales.
- Requerimientos no funcionales.
- Reglas de negocio iniciales.
- Modelo conceptual.
- Criterios de aceptación de alto nivel.
- Decisiones pendientes.
- Supuestos.
- Recomendaciones técnicas adicionales.

Este documento responde principalmente a la pregunta:

"¿Qué necesita SOLGASES APP?"


3. roadmap_mvps_solgases.md

Contiene la propuesta de evolución del sistema mediante MVPs.

Define la división progresiva del proyecto, comenzando por el backend y evolucionando posteriormente hacia:

- Backend.
- Frontend administrativo.
- Catálogo público.
- Chatbot.
- WhatsApp.
- Gestión avanzada de operación.
- Analítica y reportes.

Este documento responde principalmente a la pregunta:

"¿Cómo vamos a construir progresivamente la solución completa?"


4. mvp1.md

Contiene la definición específica del MVP 1.

Es el documento de trabajo utilizado para delimitar qué se construirá en la primera etapa del proyecto.

El MVP 1 está enfocado exclusivamente en backend y contempla, de acuerdo con la documentación actual:

- Base del proyecto.
- Arquitectura.
- Usuarios.
- Roles y permisos.
- Categorías.
- Productos.
- Inventario.
- Validaciones.
- Manejo de errores.
- Pruebas.
- OpenAPI.
- Postman.

También establece:

- Alcance.
- Fuera de alcance.
- Orden de implementación.
- Dependencias.
- Criterios de aceptación.
- Checkpoints.
- Decisiones pendientes.

Este documento responde principalmente a la pregunta:

"¿Qué vamos a construir ahora?"


5. prompt_inicial_mvp1.md

Contiene el prompt inicial que se utilizará para comenzar el trabajo con Claude Code.

Su objetivo no es solicitar inmediatamente la implementación completa del MVP 1.

La primera interacción debe utilizarse para que Claude Code:

1. Lea la documentación.
2. Comprenda el contexto.
3. Revise el estado actual del repositorio.
4. Analice el alcance del MVP 1.
5. Identifique supuestos, inconsistencias y decisiones pendientes.
6. Proponga una estrategia inicial.
7. Espere autorización antes de modificar o generar código.

Este enfoque permite desarrollar el MVP 1 progresivamente y revisar cada incremento antes de continuar.


ORDEN RECOMENDADO DE LECTURA
----------------------------

Para comprender el proyecto:

1. lineamientos.md
2. analisis_requerimientos_solgases.md
3. roadmap_mvps_solgases.md
4. mvp1.md
5. prompt_inicial_mvp1.md

Para iniciar el desarrollo con Claude Code:

1. Claude Code debe leer los cuatro documentos de referencia.
2. Debe analizar el repositorio existente.
3. Debe presentar su propuesta inicial.
4. El equipo debe revisar la propuesta.
5. Solo después se autoriza la implementación.


RELACIÓN ENTRE LOS DOCUMENTOS
-----------------------------

lineamientos.md
    |
    | define las reglas técnicas
    v
analisis_requerimientos_solgases.md
    |
    | define las necesidades del negocio
    v
roadmap_mvps_solgases.md
    |
    | define la evolución completa
    v
mvp1.md
    |
    | define el alcance actual
    v
prompt_inicial_mvp1.md
    |
    | inicia la interacción con Claude Code
    v
IMPLEMENTACIÓN INCREMENTAL


PRINCIPIO DE TRABAJO
--------------------

La documentación debe utilizarse como fuente de contexto para el desarrollo, pero no debe interpretarse como autorización para implementar todo de una sola vez.

El MVP 1 se desarrollará mediante incrementos pequeños.

Cada incremento deberá seguir, como mínimo, este ciclo:

REQUERIMIENTO
    ->
ANÁLISIS
    ->
DISEÑO
    ->
IMPLEMENTACIÓN
    ->
COMPILACIÓN
    ->
PRUEBAS
    ->
REVISIÓN
    ->
APROBACIÓN
    ->
SIGUIENTE INCREMENTO

Cuando exista una decisión de arquitectura o negocio que no esté definida en los documentos, deberá identificarse como decisión pendiente en lugar de inventar un requisito.
