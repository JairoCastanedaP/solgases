# ADR-0004: Jenkins como CI/CD y despliegue a WebLogic por SSH/WLST

- **Estado:** Aceptado
- **Fecha:** 2026-10-21

## Contexto
El proyecto requiere automatizar integración, análisis estático y despliegue del WAR en una VM Red Hat con WebLogic.

## Decisión
Jenkins ejecuta el `Jenkinsfile`: checkout, Maven, pruebas, empaquetado/cobertura, análisis SonarQube, evaluación de Quality Gate y despliegue. El agente usa SSH/SCP con credencial de Jenkins y verificación estricta de host; WLST ejecuta el redeploy de la aplicación `solgases` al `AdminServer`.

## Consecuencias
- Jenkins necesita conectividad al host, acceso al WAR y herramientas configuradas.
- WLST usa archivos seguros de usuario/clave existentes únicamente en la VM.
- La clave JWT se configura en el entorno de WebLogic, separada del repositorio y del artefacto.
- El Quality Gate está en modo no bloqueante: un resultado distinto de OK marca el build `UNSTABLE`, pero el pipeline sigue al despliegue. Esto no equivale a aprobar la calidad; cualquier cambio de política requiere decisión del equipo.
- La aplicación queda en el contexto `/infrastructure`.

## Evidencia
`Jenkinsfile`, `deployment/weblogic_deploy.py`, `docs/DevOps.md` y ejecución observada del pipeline.
