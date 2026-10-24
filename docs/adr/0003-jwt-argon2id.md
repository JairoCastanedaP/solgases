# ADR-0003: JWT stateless y contraseñas con Argon2id

- **Estado:** Aceptado para el MVP actual
- **Fecha:** 2026-10-21

## Contexto
La API requiere autenticar clientes y autorizar operaciones por permisos sin mantener sesión HTTP en el servidor.

## Decisión
Emitir JWT firmado con HS256, duración de 15 minutos y sin refresh token. La clave se obtiene de `JWT_SECRET` (Base64, al menos 256 bits) y no se guarda en el repositorio. Los filtros validan JWT; la autorización consulta los permisos vigentes en cada solicitud. Guardar contraseñas con Argon2id usando los parámetros configurados por la aplicación.

## Consecuencias
- Si el secreto falta o no es válido, la aplicación debe fallar al iniciar o emitir tokens.
- Al no existir refresh token, el usuario debe autenticarse de nuevo al expirar.
- Consultar permisos en cada solicitud refleja cambios de autorización sin esperar a que expire el token.
- La rúbrica menciona BCrypt y existe una discrepancia que se debe aclarar con el docente; esta ADR refleja la implementación actual, no una migración aprobada.

## Evidencia
Configuración `solgases.security.jwt`, `JWT_SECRET`, servicio de autenticación y pruebas de integración de seguridad.
