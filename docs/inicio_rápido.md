# Inicio rápido de SOLGASES

Guía para levantar los servicios después de reiniciar el computador y demostrar la aplicación desplegada en WebLogic.

## 1. Iniciar la máquina virtual

Desde VirtualBox, inicia la máquina virtual Red Hat y espera a que termine de arrancar. Desde Ubuntu, verifica el acceso:

```bash
ssh jairo@192.168.122.148 'hostname'
```

## 2. Iniciar los contenedores Docker

En una terminal de Ubuntu:

```bash
sudo systemctl start docker
docker start student-mysql
docker start solgases-sonarqube
docker start solgases-jenkins
docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'
```

`student-mysql` es el nombre del contenedor MySQL; la base de datos utilizada por SOLGASES se llama `solgases`. Jenkins debe quedar disponible en `http://localhost:8080` y SonarQube en `http://localhost:9000`.

Si MySQL publica el puerto `3307` en Ubuntu, puedes comprobarlo así:

```bash
timeout 3 bash -c '</dev/tcp/127.0.0.1/3307' \
  && echo "MySQL disponible" \
  || echo "Revisa el contenedor y el puerto de MySQL"
```

## 3. Iniciar WebLogic en Red Hat

Conéctate a la VM:

```bash
ssh jairo@192.168.122.148
```

Comprueba si WebLogic ya está escuchando en el puerto `7001`:

```bash
ss -ltn | grep ':7001'
```

Si no aparece el puerto, inicia el AdminServer en segundo plano:

```bash
nohup /home/jairo/oracle/domains/lab_domain/bin/startWebLogic.sh \
  > /home/jairo/oracle/domains/lab_domain/logs/startup.out 2>&1 &
```

Revisa el registro de inicio:

```bash
tail -f /home/jairo/oracle/domains/lab_domain/logs/startup.out
```

Cuando WebLogic indique que el servidor está en estado `RUNNING`, pulsa `Ctrl+C` para salir del seguimiento del registro. Confirma que el puerto quedó abierto:

```bash
ss -ltn | grep ':7001'
```

El inicio del dominio carga el perfil `dev` y la clave JWT desde `/home/jairo/.solgases-jwt.env`. Conserva ese archivo privado; no muestres ni copies su contenido en registros o comandos compartidos.

## 4. Ejecutar el pipeline de Jenkins

Abre `http://localhost:8080`, entra al trabajo **solgases-ci** y selecciona **Construir ahora**. En **Stage View**, comprueba las etapas de compilación, pruebas, análisis y despliegue. Con la configuración actual, SonarQube puede dejar el resultado general inestable por el Quality Gate, aunque la etapa de despliegue termine correctamente.

## 5. Demostrar la aplicación

Desde Ubuntu, abre Swagger:

```text
http://192.168.122.148:7001/infrastructure/swagger-ui/index.html
```

La especificación OpenAPI debe estar disponible en:

```text
http://192.168.122.148:7001/infrastructure/v3/api-docs
```

En Swagger, expande un endpoint, pulsa **Try it out** y después **Execute** para probar la API.

También puedes verificar desde la VM que Swagger y OpenAPI responden con HTTP 200:

```bash
curl -sS -o /dev/null -w 'Swagger: HTTP %{http_code}\n' \
  http://127.0.0.1:7001/infrastructure/swagger-ui/index.html

curl -sS -o /dev/null -w 'API docs: HTTP %{http_code}\n' \
  http://127.0.0.1:7001/infrastructure/v3/api-docs
```
