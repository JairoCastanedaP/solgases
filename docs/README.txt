SOLGASES APP
Índice de la documentación (carpeta docs/)
==========================================

Esta carpeta reúne la documentación de SOLGASES APP: una API backend
(Java 21, Spring Boot 3.5.16, MySQL) para gestionar catálogo, inventario,
usuarios, roles y permisos de SOLGASES, empresa de seguridad industrial de
Sogamoso, Boyacá (Colombia).

Las rutas de este índice son relativas a la carpeta docs/. En GitHub:

  https://github.com/JairoCastanedaP/solgases/tree/develop/docs

La presentación general del repositorio está en el README.md de la raíz
(../README.md).


DOS TIPOS DE DOCUMENTO
----------------------

[PLAN]  Alcance y planificación: qué se pidió, qué reglas se aplican y qué
        se decidió construir. Pueden contener historia y decisiones
        pendientes.

[IMPL]  Implementación actual: cómo está construida y desplegada la
        solución hoy. Si un documento [PLAN] y uno [IMPL] no coinciden,
        el código y los documentos [IMPL] describen el estado vigente.


1. ENTENDER EL PROYECTO
-----------------------

[PLAN] analisis_requerimientos_solgases.md
       Contexto del negocio, actores, requerimientos funcionales y no
       funcionales, reglas de negocio, supuestos y decisiones pendientes.
       Responde: "¿Qué necesita SOLGASES APP?"

[PLAN] lineamientos.md
       Estándares técnicos obligatorios: stack, estructura en módulos
       domain / application / infrastructure, persistencia, secretos,
       pruebas, OpenAPI, Postman y reglas de desarrollo asistido por IA.
       Es la fuente de verdad técnica del proyecto.

[PLAN] roadmap_mvps_solgases.md
       Evolución por MVP: backend, frontend administrativo, catálogo
       público, chatbot, WhatsApp, operación y analítica.
       Responde: "¿Cómo se construirá la solución completa?"

[PLAN] mvp1.md
       Alcance del MVP 1 (solo backend) dividido en incrementos, con
       criterios de aceptación, checkpoints y decisiones aprobadas o
       pendientes. También registra el estado y la verificación de cada
       incremento, por lo que es el documento más detallado del proyecto.
       Responde: "¿Qué se construye ahora y en qué estado está?"

[PLAN] prompt_inicial_mvp1.md
       REFERENCIA HISTÓRICA. Conserva el contexto y el prompt con que se
       inició el trabajo con Claude Code en el MVP 1 (analizar antes de
       implementar). No es una instrucción vigente ni sirve para reiniciar
       el desarrollo; los ciclos actuales se rigen por ../CLAUDE.md,
       lineamientos.md y mvp1.md.


2. CONOCER LA SOLUCIÓN
----------------------

[IMPL] architecture.md
       Arquitectura implementada: contexto, módulos y dependencias, flujo
       de una solicitud, seguridad (JWT, Argon2id, permisos), perfiles,
       despliegue, pipeline y decisiones pendientes de validar. Incluye
       diagramas Mermaid.

[IMPL] data-model.md
       Modelo de datos vigente: tablas, relaciones y restricciones de
       catálogo, inventario, usuarios, roles, permisos y credenciales.

[IMPL] adr/
       Registros de decisiones de arquitectura (ADR):
         adr/0001-modularidad-clean-architecture.md
         adr/0002-mysql-y-datasource-jndi.md
         adr/0003-jwt-argon2id.md
         adr/0004-jenkins-sonarqube-weblogic.md
         adr/0005-java-21-en-webLogic.md
         adr/0006-limites-arquitectonicos-automatizados.md


3. EJECUTAR Y PROBAR
--------------------

[IMPL] inicio_rápido.md
       Pasos para levantar la VM, los contenedores (MySQL, SonarQube,
       Jenkins) y WebLogic, ejecutar el pipeline y abrir Swagger UI.
       En GitHub: inicio_r%C3%A1pido.md

[IMPL] openapi.json
       Especificación OpenAPI del contrato HTTP de la API.

[IMPL] postman/
       Colección y entorno de Postman generados a partir de OpenAPI, sin
       credenciales reales:
         postman/solgases.postman_collection.json
         postman/solgases.postman_environment.json
         postman/generate_postman_collection.py  (generador)


4. REVISAR LA ENTREGA
---------------------

[IMPL] DevOps.md
       Implementación de CI/CD: Jenkins, SonarQube, MySQL, WebLogic en la
       VM Red Hat, etapas del pipeline, despliegue por SSH/WLST,
       diagnóstico, límites y evidencia resumida (incluido el estado del
       Quality Gate, con el alcance exacto de cada métrica).

       presentacion_solgases_mvp1.pptx
       Presentación del MVP 1 (PowerPoint; en GitHub se descarga, no se
       visualiza en línea).


ORDEN DE LECTURA SUGERIDO
-------------------------

Para una visión rápida (por ejemplo, revisión técnica o reclutamiento):

  1. ../README.md
  2. architecture.md
  3. adr/
  4. DevOps.md

Para trabajar en el proyecto:

  1. lineamientos.md
  2. analisis_requerimientos_solgases.md
  3. roadmap_mvps_solgases.md
  4. mvp1.md
  5. architecture.md, data-model.md y adr/
  6. inicio_rápido.md


PRINCIPIO DE TRABAJO
--------------------

El MVP 1 se desarrolla en incrementos pequeños. Cada incremento sigue el
ciclo:

  REQUERIMIENTO -> ANÁLISIS -> DISEÑO -> IMPLEMENTACIÓN -> COMPILACIÓN
  -> PRUEBAS -> REVISIÓN -> APROBACIÓN -> SIGUIENTE INCREMENTO

Cuando una decisión de arquitectura o de negocio no está definida en los
documentos, se registra como decisión pendiente en lugar de inventar un
requisito.
