# ADR-0006: Automatización de reglas arquitectónicas

- **Estado:** Aceptado e implementado
- **Fecha:** 2026-10-21

## Contexto
La arquitectura define dependencias hacia el dominio y módulos separados, pero no se encontró ArchUnit ni otra suite que verifique automáticamente todas las reglas internas. La rúbrica menciona ArchUnit.

## Propuesta original
Evaluar pruebas ArchUnit para impedir dependencias de `domain` hacia frameworks/infraestructura, hacer cumplir límites entre paquetes y revisar dependencias permitidas entre módulos. Antes de aceptar, acordar reglas concretas y compatibilidad con la estructura Maven actual.

## Decisión
Se integra ArchUnit (`com.tngtech.archunit:archunit-junit5`, versión gestionada en el POM padre con la propiedad `archunit.version`) como dependencia de prueba del módulo `infrastructure`, el único cuyo classpath de pruebas contiene los tres módulos. Las reglas están en `infrastructure/src/test/java/com/solgases/infrastructure/architecture/CleanArchitectureTest.java`, se ejecutan con JUnit 5 y analizan solo las clases de producción bajo `com.solgases` (se excluyen clases de prueba y, con ello, bibliotecas de prueba).

| Regla | Qué bloquea |
|---|---|
| `domainDoesNotDependOnApplicationOrInfrastructure` | Clases de `com.solgases.domain..` que dependan de `com.solgases.application..` o `com.solgases.infrastructure..`. |
| `domainDependsOnlyOnItselfAndJavaPackages` | Cualquier dependencia de `com.solgases.domain..` fuera de `com.solgases.domain..` y de los paquetes `java.*` (`java..`): Spring, Jakarta Persistence, Hibernate, Jackson, Lombok o cualquier otra biblioteca, y también paquetes del JDK fuera de `java.*`, como `javax.*`, `jdk.*` o `sun.*`. |
| `applicationDoesNotDependOnInfrastructure` | Clases de `com.solgases.application..` que dependan de `com.solgases.infrastructure..`. |
| `dependenciesPointTowardsTheDomain` | Expresa las direcciones permitidas entre capas: `infrastructure → application → domain` e `infrastructure → domain`. Bloquea que `infrastructure` sea usada por otra capa, que `application` sea usada por `domain` y que `domain` sea usado desde fuera de `application`/`infrastructure`. Solo considera dependencias entre las tres capas y falla si alguna capa queda vacía. |

Las reglas se basan en paquetes, no en módulos Maven: además de respaldar lo que ya impiden los POM, detectan clases ubicadas en un paquete de capa equivocado dentro de otro módulo y protegen frente a cambios futuros en las dependencias de los POM.

## Excepción vigente: `spring-tx` en `application`
`application` depende de `spring-tx` para usar `@Transactional` como frontera transaccional de los casos de uso (`docs/lineamientos.md`). Es una excepción conocida y aceptada: ninguna regla restringe hoy las dependencias de `application` hacia frameworks, y esta decisión no refactoriza las transacciones. Si se quisiera restringir `application` a `domain`, `java.*` y `org.springframework.transaction.annotation`, sería una regla adicional a acordar.

## Ejecución
Las reglas se ejecutan en la fase `test` de Maven (`mvn -B test`, etapa `Tests` del `Jenkinsfile`) sin configuración adicional de Surefire ni del pipeline; sus resultados aparecen en `infrastructure/target/surefire-reports/` y los publica el paso `junit` de Jenkins. Para ejecutarlas de forma aislada:

```bash
mvn -B test -pl infrastructure -am -Dtest=CleanArchitectureTest -Dsurefire.failIfNoSpecifiedTests=false
```

## Consecuencias
- Las violaciones de dependencias entre capas fallan el build durante Maven CI.
- Las reglas quedan explícitas y revisables junto al código.
- Costo de mantener las reglas cuando cambien paquetes o se acuerden nuevas excepciones.
