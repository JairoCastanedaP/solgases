# ADR-0005: Java 21 como versión actual del proyecto

- **Estado:** Aceptado para el estado actual; requisito académico por confirmar
- **Fecha:** 2026-10-21

## Contexto
El POM, los lineamientos del repositorio y la imagen de Jenkins usan Java 21; WebLogic 15.1.1 está configurado con JDK 21. La rúbrica final menciona Java 25.

## Decisión
Mantener Java 21 para el código y el despliegue actual hasta confirmar compatibilidad de WebLogic y criterio de evaluación. No introducir APIs posteriores a Java 21 mientras el POM y lineamientos lo establezcan.

## Consecuencias
- La documentación debe describir Java 21 como estado presente.
- La mención de Java 25 se registra como discrepancia académica pendiente de confirmación, no como decisión de migración.
- Una futura actualización requiere validar certificación de WebLogic, dependencias y entorno de Jenkins.
