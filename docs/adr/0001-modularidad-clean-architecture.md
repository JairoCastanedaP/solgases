# ADR-0001: Modularidad con Clean Architecture

- **Estado:** Aceptado
- **Fecha:** 2026-10-21

## Contexto
SOLGASES necesita separar reglas de negocio, casos de uso y detalles técnicos para limitar el acoplamiento y permitir pruebas aisladas.

## Decisión
Organizar el Maven reactor en `domain`, `application` e `infrastructure`. Las dependencias apuntan hacia el dominio: `application` depende de `domain`; `infrastructure` integra ambas capas mediante puertos y adaptadores.

## Consecuencias
- El dominio no debe depender de Spring, JPA, HTTP ni WebLogic.
- Casos de uso y puertos pertenecen a `application`.
- REST, persistencia y seguridad pertenecen a `infrastructure`.
- La estructura Maven impone algunos límites; las dependencias entre los paquetes de capa se verifican además con ArchUnit (ADR-0006).

## Evidencia
`pom.xml`, POM de módulos y organización de paquetes bajo `domain/src`, `application/src` e `infrastructure/src`.
