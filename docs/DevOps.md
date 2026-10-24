# Guía DevOps de SOLGASES

## Propósito

Resumen de la implementación CI/CD del laboratorio, sus servicios, configuraciones, secretos, pipeline y verificaciones. La instalación probada demuestra despliegue en la VM y respuestas HTTP; no equivale a certificación de producción.

Para levantar los servicios después de reiniciar el equipo, consultar también [inicio_rápido.md](inicio_r%C3%A1pido.md).

## Arquitectura

    Ubuntu (anfitrión)
    ├── Docker: Jenkins LTS/JDK 21 → http://localhost:8080
    ├── Docker: SonarQube Community → http://localhost:9000
    ├── Docker: MySQL SOLGASES → 192.168.122.1:3307
    └── VM Red Hat 9.8: WebLogic 15.1.1/AdminServer :7001
        ├── SolgasesDS (JNDI jdbc/solgases) → MySQL
        └── WAR solgases → contexto /infrastructure

Repositorio: https://github.com/JairoCastanedaP/solgases.git. El job solgases-ci trabaja con develop y también permite ejecución manual mediante Construir ahora.

Flujo general: Jenkins obtiene el código, compila, ejecuta pruebas, empaqueta el WAR, publica análisis/cobertura en SonarQube y lo despliega en WebLogic por SSH y WLST.

## Componentes y versiones conocidas

| Componente | Configuración |
| --- | --- |
| Aplicación | Maven multimódulo: domain, application, infrastructure |
| Java | Java 21 en el proyecto; imagen Jenkins lts-jdk21 |
| Maven | Herramienta global de Jenkins “Maven 3.9” |
| SonarQube | sonarqube:26.9.0.129388-community |
| Sonar Maven Scanner | 5.8.0.7211 |
| JaCoCo | 0.8.15, reporte agregado en infrastructure/target/site/jacoco-aggregate |
| MySQL de docker-compose.yml | Oracle MySQL Community Server 8.4.12, host 192.168.122.1:3307 |
| VM | Red Hat Enterprise Linux 9.8 |
| WebLogic | 15.1.1, AdminServer en puerto 7001 |
| Java VM | JDK 21.0.12.1, según la documentación del proyecto |

Jenkins se administra como contenedor preexistente; docker-compose.sonar.yml define SonarQube y docker-compose.yml define la base SOLGASES. Durante el laboratorio también se encontró el contenedor antiguo student-mysql de otro proyecto (MySQL 8.0). student-mysql es nombre de contenedor, no de base de datos. Antes de iniciar/recrear MySQL, revisar docker ps -a y el datasource para no crear conflicto de puerto ni apuntar a una instancia distinta.

## Jenkins

### Instalación y job

- Contenedor jenkins/jenkins:lts-jdk21, interfaz publicada en 127.0.0.1:8080. El directorio /var/jenkins_home debe persistir: aloja jobs, plugins, herramientas y credenciales.
- El pipeline usa agent any en el controlador/agente disponible. El Jenkinsfile lo define como laboratorio inicial de un solo usuario; para PR de fuentes no confiables se debe usar agente dedicado.
- Job Pipeline: solgases-ci; SCM Git, rama develop.
- Poll SCM configurado como H/5 * * * * (aproximadamente cada cinco minutos; H distribuye el minuto). Construir ahora también ejecuta el job.
- Maven global debe llamarse exactamente Maven 3.9; Java 21 lo provee la imagen.

### Plugins relevantes

- SonarQube Scanner for Jenkins (en la captura se veía 2.19.0): withSonarQubeEnv y espera del Quality Gate.
- SSH Agent: carga la credencial temporalmente durante el despliegue.
- Pipeline, Git, Credentials, JUnit y publicación de artefactos: capacidades usadas por el Jenkinsfile.
- Las versiones pueden variar al actualizar; verificar en Administrar Jenkins → Plugins.

### SonarQube y credenciales Jenkins

En Administrar Jenkins → System se configuró SonarQube SOLGASES con URL interna http://solgases-sonarqube:9000. Jenkins y SonarQube son contenedores diferentes: se conectó solgases-sonarqube a la red Docker jenkins_default para resolución DNS. localhost:9000 sirve al navegador del anfitrión; dentro de Jenkins, localhost es el propio contenedor.

La credencial de token registrada en Jenkins tiene ID sonarqube-solgases-token. La credencial SSH es weblogic-vm-ssh; el archivo secreto de host conocido es weblogic-known-hosts. No copiar los valores secretos a Git, consola, capturas o chat.

Diagnóstico de red:

    docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'
    docker network inspect jenkins_default
    docker exec solgases-jenkins getent hosts solgases-sonarqube

## SonarQube

Definición en docker-compose.sonar.yml: contenedor solgases-sonarqube, Community 26.9.0.129388; interfaz host 127.0.0.1:9000; volúmenes persistentes para datos, extensiones y logs; healthcheck contra /api/system/status.

El token se generó en SonarQube y se guardó como credencial Jenkins. El análisis local usa SONAR_HOST_URL/SONAR_TOKEN desde .sonar.env. .env, .env.* y .sonar.env están ignorados por Git. Nunca imprimir sus contenidos.

Clave de proyecto Maven: solgases. JaCoCo agregado se reporta desde infrastructure/target/site/jacoco-aggregate/jacoco.xml. En análisis recientes Quality Gate fue ERROR por incidencias en código nuevo (la UI mostró 31 incidencias nuevas y cero requeridas). Por decisión del usuario no se cambian ahora las reglas. Jenkins marca UNSTABLE si el estado no es OK y continúa al despliegue; por eso Quality Gate puede verse amarillo, Deploy verde y el resultado general inestable.

## MySQL y datasource WebLogic

El docker-compose.yml versionado define servicio mysql/contenedor solgases-mysql, Oracle MySQL 8.4.12 fijado por digest, volumen solgases-mysql84-data y publicación 192.168.122.1:3307 → contenedor:3306. El Compose lee DB_ROOT_PASSWORD, DB_USERNAME, DB_PASSWORD y opcionalmente DB_NAME/DB_PORT desde .env; base/usuario por defecto: solgases. No documentar valores de contraseñas.

La VM usa la puerta de enlace 192.168.122.1 para alcanzar el Docker host; conexión TCP a puerto 3307 fue comprobada. También apareció el contenedor histórico student-mysql. Confirmar qué instancia usa el datasource antes de arrancar, borrar o migrar datos.

En WebLogic se creó datasource genérico SolgasesDS con JNDI jdbc/solgases, base solgases, usuario solgases y host/puerto accesibles desde la VM (192.168.122.1:3307 para el Compose versionado). La prueba de conexión en la consola WebLogic tuvo éxito.

La configuración dev de infrastructure usa:

    spring:
      datasource:
        jndi-name: jdbc/solgases

No usar localhost:3306 en la aplicación desplegada: para WebLogic, localhost es la VM. Mantener credenciales de DB fuera del YAML y del repositorio.

## WebLogic y VM Red Hat

- Dominio: /home/jairo/oracle/domains/lab_domain.
- AdminServer: t3://localhost:7001 dentro de la VM; desde Ubuntu, VM 192.168.122.148:7001.
- WLST: /home/jairo/Oracle/Middleware/Oracle_Home/oracle_common/common/bin/wlst.sh.
- Arranque: /home/jairo/oracle/domains/lab_domain/bin/startWebLogic.sh.
- La VM no siempre inicia WebLogic automáticamente; consultar docs/inicio_rápido.md para arrancarlo y comprobar RUNNING.

setUserOverrides.sh en el dominio activa el perfil dev y carga /home/jairo/.solgases-jwt.env, que exporta JWT_SECRET. Este secreto Base64 de HS256 debe tener al menos 256 bits. Sin él, el arranque falla con “The JWT signing key is not configured (solgases.security.jwt.secret)”. Verificar solo existencia/permisos con ls -l; no imprimirlo ni guardarlo en Git.

El perfil dev usa JNDI jdbc/solgases, habilita Swagger/OpenAPI y configura Hibernate ddl-auto: update. Es una configuración de laboratorio; producción requiere evaluar migraciones, perfil y seguridad.

Credenciales WLST del usuario administrador, cifradas en archivos locales a la VM:

- /home/jairo/.wlst/solgases-userconfig.secure
- /home/jairo/.wlst/solgases-userkey.secure

Tienen permisos 600 y Jenkins no almacena la contraseña WebLogic. WLST se conecta dentro de la VM con t3://localhost:7001 cuando AdminServer ya está RUNNING. T3 no cifra el tráfico; usar solo en la red aislada del laboratorio, no como configuración productiva.

## Pipeline por etapas

Implementación en [Jenkinsfile](../Jenkinsfile).

| Etapa | Acción |
| --- | --- |
| Declarative: Checkout SCM | Jenkins recupera la revisión configurada. |
| Declarative: Tool Install | Prepara Maven 3.9 y Java 21. |
| Checkout | Ejecuta checkout scm explícito. |
| Compile | Muestra versión Java; mvn -B clean -DskipTests compile. |
| Tests | mvn -B test. |
| Package and coverage | mvn -B -DskipTests verify; genera WAR y reporte agregado JaCoCo. |
| SonarQube analysis | Scanner Maven dentro de withSonarQubeEnv('SonarQube SOLGASES'). |
| Quality Gate | Espera máximo 10 minutos; estado distinto de OK marca UNSTABLE sin abortar. |
| Deploy to WebLogic | SSH Agent copia WAR/helper WLST y ejecuta despliegue. |
| Declarative: Post Actions | Publica JUnit si existe, archiva WAR/cobertura y registra fingerprints. |

Opciones: timestamps, disableConcurrentBuilds y conservar los últimos diez builds. Post Actions archiva infrastructure/target/infrastructure-0.0.1-SNAPSHOT.war y coverage JaCoCo.

### Despliegue

1. Comprueba que exista infrastructure/target/infrastructure-0.0.1-SNAPSHOT.war.
2. Copia el WAR a /home/jairo/deploy/solgases/infrastructure.war y el helper deployment/weblogic_deploy.py a deploy.py.
3. Conecta como jairo por SSH con BatchMode y StrictHostKeyChecking; el fingerprint viene de weblogic-known-hosts.
4. WLST usa archivos secure y t3://localhost:7001.
5. Despliega con nombre solgases a AdminServer; llama deploy si no existe o redeploy si ya está registrado.
6. El helper comprueba estado completed y devuelve fallo a Jenkins si no se completa.

Helper: [deployment/weblogic_deploy.py](../deployment/weblogic_deploy.py). Variables WLS_USER_CONFIG, WLS_USER_KEY, WLS_ADMIN_URL, WLS_APP_NAME, WLS_APP_TARGET y WLS_WAR_PATH.

## URLs y evidencia funcional

- Swagger UI: http://192.168.122.148:7001/infrastructure/swagger-ui/index.html
- OpenAPI JSON: http://192.168.122.148:7001/infrastructure/v3/api-docs
- Ambas rutas devolvieron HTTP 200 desde la VM; Swagger se abrió desde Ubuntu. El contexto correcto es /infrastructure; /solgases e /infrastructure-0.0.1-SNAPSHOT devolvieron 404.
- Para demo: en Swagger expandir endpoint, Try it out y Execute. Si se requiere autorización, usar el flujo de autenticación de la API para obtener JWT y luego Authorize.

Una UI Swagger accesible no demuestra por sí sola que DB y todas las operaciones funcionen; ejecutar una operación real y revisar su respuesta.

## Diagnóstico rápido

| Síntoma | Revisar |
| --- | --- |
| Jenkins no conecta a Sonar | Estado/healthcheck, red jenkins_default y DNS solgases-sonarqube. |
| Quality Gate ERROR | Incidencias de código nuevo; decisión actual permite continuar como UNSTABLE. |
| ssh-add pide passphrase | Se sustituyó la llave inicial por llave de laboratorio sin passphrase; confirmar ID weblogic-vm-ssh. No reutilizar fuera del lab. |
| SSH dice host desconocido | Actualizar weblogic-known-hosts solo después de verificar fingerprint; no desactivar host checking. |
| Puerto WebLogic 7001 cerrado | AdminServer apagado; iniciar dominio y revisar su log. |
| WLST no autentica | Ejecutar dentro de VM y revisar que existan ambos archivos secure y sean legibles por jairo. |
| Error de JWT | Revisar carga del archivo .solgases-jwt.env en setUserOverrides.sh y reiniciar dominio si cambió entorno. |
| Error de datasource | Probar datasource en WebLogic; confirmar JNDI jdbc/solgases y conectividad hacia 192.168.122.1:3307. |
| Swagger 404 | Usar contexto /infrastructure; comprobar estado del deployment y logs. |
| UI carga pero API falla | Probar endpoint; revisar autenticación, datasource y logs WebLogic. |

Diagnóstico sin valores secretos:

    # Ubuntu
    docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'
    curl -fsS http://127.0.0.1:9000/api/system/status

    # VM
    ss -ltn | grep ':7001'
    timeout 3 bash -c '</dev/tcp/192.168.122.1/3307' && echo "MySQL accesible"
    curl -sS -o /dev/null -w 'Swagger HTTP %{http_code}\n' \
      http://127.0.0.1:7001/infrastructure/swagger-ui/index.html

## Límites y siguientes pasos

- Polling SCM y Construir ahora se usan actualmente; webhook de GitHub puede evaluarse después.
- Despliegue dirigido a develop y AdminServer. No hay aprobación multiambiente, rollback automático, blue/green ni promoción a producción.
- El Quality Gate no bloquea despliegue por decisión del usuario; acordar umbrales antes de convertirlo en política de liberación.
- Jenkins/SonarQube están limitados a localhost del anfitrión; WebLogic usa HTTP/T3 en la red privada. Aislar la red. Un entorno compartido requiere TLS, controles de acceso, agente dedicado y rotación de secretos.
- No se da por configurado el inicio automático de Docker, VM o AdminServer; inicio_rápido.md documenta el procedimiento manual.
- No guardar tokens, contraseñas, llaves privadas, archivos .secure, JWT_SECRET ni valores .env en Git, capturas o logs.

## Evidencia resumida

Deploy to WebLogic terminó verde tras corregir la carga de JWT_SECRET y configurar la llave SSH. Swagger y OpenAPI respondieron HTTP 200 en /infrastructure. Quality Gate puede quedar amarillo/inestable por ERROR de SonarQube; revisar por separado el resultado del build, el deployment y una operación funcional.
