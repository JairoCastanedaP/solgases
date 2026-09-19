# SOLGASES APP — Prompt inicial para Claude Code

## Objetivo

Este archivo contiene el prompt inicial para comenzar el desarrollo del MVP 1 de SOLGASES APP utilizando Claude Code.

El propósito de esta primera interacción NO es implementar todo el MVP 1.

La primera tarea consiste en analizar el contexto, la documentación y el repositorio existente, y presentar una propuesta de trabajo antes de modificar código.

---

# PROMPT

Estoy desarrollando SOLGASES APP como proyecto de un curso de desarrollo de software asistido por IA.

Quiero que trabajemos de manera incremental y controlada.

Antes de modificar o crear código, debes analizar el proyecto y la documentación existente.

## 1. Contexto del proyecto

SOLGASES es una empresa de seguridad industrial ubicada en Sogamoso, Boyacá, Colombia.

La solución que se desarrollará progresivamente busca apoyar:

- Gestión de productos.
- Gestión de inventario.
- Gestión de usuarios.
- Gestión de roles y permisos.
- Catálogo de productos.
- Servicios de la empresa.
- Recomendaciones mediante chatbot.
- Integración con WhatsApp.

Sin embargo, en esta primera etapa trabajaremos únicamente en el backend correspondiente al MVP 1.

## 2. Documentación obligatoria

Antes de realizar cualquier implementación, debes leer y considerar estos archivos:

```text
docs/lineamientos.md
docs/analisis_requerimientos_solgases.md
docs/roadmap_mvps_solgases.md
docs/mvp1.md
```

Debes tratar cada documento según su propósito:

- `lineamientos.md`: reglas técnicas obligatorias.
- `analisis_requerimientos_solgases.md`: requerimientos y reglas de negocio.
- `roadmap_mvps_solgases.md`: visión y evolución completa del proyecto.
- `mvp1.md`: alcance específico de la primera etapa.

No debes sustituir estos documentos por suposiciones propias.

## 3. Revisión del repositorio

Después de leer la documentación:

1. Inspecciona la estructura actual del repositorio.
2. Identifica si ya existe código.
3. Identifica la configuración existente.
4. Identifica tecnologías y dependencias existentes.
5. Identifica archivos relevantes.
6. Determina qué está implementado y qué no.
7. Detecta posibles inconsistencias entre el repositorio y la documentación.

No asumas que el repositorio está vacío.

## 4. No implementar todavía

En esta primera interacción NO debes:

- Crear entidades.
- Crear controllers.
- Crear services.
- Crear repositories.
- Modificar el modelo de datos.
- Implementar CRUD.
- Implementar autenticación.
- Implementar inventario.
- Implementar funcionalidades del MVP 2 o posteriores.

Primero quiero revisar tu análisis.

## 5. Analiza la arquitectura

Con base en los documentos y el repositorio, analiza:

- Arquitectura propuesta.
- Organización por features.
- Módulos iniciales.
- Entidades conceptuales.
- Relaciones principales.
- Capas necesarias.
- Dependencias.
- Riesgos técnicos.
- Decisiones que todavía requieren definición.

No conviertas automáticamente una propuesta en una decisión definitiva.

## 6. Identifica decisiones pendientes

Quiero que hagas una sección específica llamada:

`DECISIONES PENDIENTES`

Allí debes listar cualquier decisión de arquitectura, negocio o implementación que necesite confirmación antes de codificar.

No inventes respuestas para esas decisiones.

## 7. Propón la estrategia incremental

Propón cómo dividirías la implementación del MVP 1 en incrementos pequeños.

Como mínimo considera:

```text
Incremento 0 — Análisis y arquitectura
Incremento 1 — Bootstrap
Incremento 2 — Category
Incremento 3 — Product
Incremento 4 — Inventory
Incremento 5 — User / Role / Permission
Incremento 6 — Calidad y documentación
```

Puedes proponer modificaciones si encuentras una razón técnica clara, pero debes explicarlas antes de implementarlas.

## 8. Checkpoints

Cada incremento deberá terminar con un checkpoint.

Después de cada checkpoint:

- Compila el proyecto.
- Ejecuta las pruebas relevantes.
- Reporta archivos creados/modificados.
- Reporta decisiones tomadas.
- Reporta problemas pendientes.
- Detente para permitir revisión antes de continuar, salvo que posteriormente te indique explícitamente que continúes.

## 9. Reglas técnicas

Debes respetar estrictamente `docs/lineamientos.md`.

Entre otras reglas:

- Java 25.
- Spring Boot 4.1.1.
- Maven 3.9+.
- Spring Data JPA.
- MySQL.
- REST APIs.
- JUnit 5.
- Bean Validation.
- OpenAPI.
- Postman.
- Constructor injection.
- DTOs.
- No exponer entidades directamente.
- Acceso a datos mediante repositories.
- Lógica de negocio fuera de controllers.
- SLF4J para logging.
- Código fuente en inglés.

No agregues frameworks o dependencias adicionales sin justificar la necesidad y solicitar autorización cuando corresponda.

## 10. Regla sobre requisitos

NO INVENTES REQUISITOS.

Si encuentras una información que no está definida:

1. Identifícala.
2. Explica por qué es necesaria.
3. Propón alternativas si es útil.
4. Espera decisión cuando corresponda.

No conviertas una sugerencia en una regla de negocio.

## 11. Regla sobre alcance

El MVP 1 es exclusivamente backend.

No implementes:

- Frontend.
- Chatbot.
- IA.
- WhatsApp.
- Aplicación móvil.
- Analítica avanzada.
- Funcionalidades de MVP posteriores.

Si detectas algo que podría ser útil para una fase posterior, documenta la sugerencia pero no la implementes.

## 12. Resultado esperado de esta primera interacción

Al terminar esta primera interacción quiero recibir exclusivamente un informe estructurado con:

### A. Estado actual del repositorio

Qué encontraste.

### B. Documentación analizada

Qué documentos fueron revisados.

### C. Arquitectura propuesta

Cómo propones organizar el backend.

### D. Features iniciales

Qué features consideras necesarias para el MVP 1.

### E. Modelo conceptual

Entidades y relaciones que propones.

### F. Decisiones pendientes

Todo aquello que necesita confirmación.

### G. Riesgos

Problemas técnicos o de requisitos que identificaste.

### H. Plan incremental

Orden propuesto de implementación.

### I. Primer checkpoint

Qué debería implementarse primero después de mi autorización.

No modifiques archivos todavía.

No escribas código todavía.

Espera mi aprobación antes de comenzar el primer incremento.
