# ADR-0002: MySQL y DataSource JNDI en WebLogic

- **Estado:** Aceptado
- **Fecha:** 2026-10-21

## Contexto
La aplicación debe funcionar localmente con Docker y en WebLogic 15.1.1, donde conviene centralizar la conexión de producción en el servidor de aplicaciones.

## Decisión
Usar MySQL. Localmente la URL se parametriza con `DB_HOST`, `DB_PORT` y `DB_NAME`; en WebLogic la aplicación consume el DataSource `jdbc/solgases` (`SolgasesDS`). Las credenciales se mantienen en el entorno correspondiente y no se incorporan al WAR.

## Consecuencias
- El perfil de despliegue en WebLogic requiere que exista y esté dirigido el DataSource JNDI antes de desplegar.
- El entorno local depende de los valores de `.env` y del Compose del proyecto.
- Cambios de esquema y credenciales deben coordinarse con el despliegue de la aplicación.

## Evidencia
`infrastructure/src/main/resources/application.yml`, `docker-compose.yml` y configuración verificada de WebLogic.
