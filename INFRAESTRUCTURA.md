# Infraestructura de Desarrollo — SGVA

Este documento describe el entorno local necesario para ejecutar, analizar y
evaluar el proyecto SGVA, fuera del código fuente versionado. Ninguno de estos
servicios se ejecuta dentro de la aplicación Spring Boot; son dependencias
externas que corren en contenedores Podman sobre Fedora.

---

## 1. SonarQube (análisis estático — todas las versiones)

Usado desde V1 para medir el Ratio de Deuda Técnica y el Maintainability
Rating de cada iteración (`logs/V*/P-XXX.md`).

**Nombre real del contenedor:** `sonarqube` (no `sgva-sonarqube`).
**Imagen confirmada:** `docker.io/library/sonarqube:community`.

Los volúmenes se crearon como volúmenes anónimos de Podman (no rutas de host
ni volúmenes nombrados explícitamente), montados en:
`/opt/sonarqube/logs`, `/opt/sonarqube/temp`, `/opt/sonarqube/data`,
`/opt/sonarqube/extensions`.

**Comando de referencia para recrear el contenedor** (equivalente funcional,
usando volúmenes nombrados para mayor portabilidad que los anónimos originales):

```bash
podman run -d --name sonarqube \
  -p 9000:9000 \
  -v sonarqube-data:/opt/sonarqube/data \
  -v sonarqube-extensions:/opt/sonarqube/extensions \
  -v sonarqube-logs:/opt/sonarqube/logs \
  -v sonarqube-temp:/opt/sonarqube/temp \
  sonarqube:community
```

**Verificar detalles del contenedor actual en cualquier momento:**
```bash
podman inspect sonarqube --format '{{.Config.Image}}'
podman inspect sonarqube --format '{{json .HostConfig.Binds}}'
```

**Verificar que está arriba:**
```bash
curl http://localhost:9000/api/system/status
# Esperado: {"status":"UP"}
```

**Acceso:** http://localhost:9000 (usuario/contraseña inicial `admin`/`admin`,
con cambio obligatorio en el primer login).

**Comandos de uso frecuente:**
```bash
podman ps -a              # ver si el contenedor existe/está corriendo
podman start sonarqube    # si existe pero está detenido
```

**Ejecutar análisis sobre el proyecto** (desde la raíz de SGVA):
```bash
./gradlew sonar
```

**Nota:** el proyecto usa la base de datos embebida de SonarQube ("H2/File
System"), válida solo para evaluación local — así lo advierte la propia UI.
No usar en un entorno compartido o de producción.

---

## 2. PostgreSQL (V3 en adelante — sustitución de persistencia)

Introducido en V3 para probar la sustitución del adaptador de persistencia en
memoria (`AcademicDataRepositoryEnMemoria`) por un motor relacional real, sin
alterar el puerto `AcademicDataRepository` ni las entidades de dominio
(`DESIGN_SGVA.md` §4, Fase 4 - Versión 3 del anteproyecto).

**Crear el contenedor:**
```bash
podman run -d --name sgva-postgres \
  -e POSTGRES_DB=sgva \
  -e POSTGRES_USER=sgva \
  -e POSTGRES_PASSWORD=sgva_dev_password \
  -p 5432:5432 \
  postgres:16
```

**Verificar que está arriba:**
```bash
podman exec -it sgva-postgres psql -U sgva -d sgva -c "\dt"
```
Salida esperada antes de que exista cualquier tabla: `Did not find any relations.`
(confirmado — contenedor creado y verificado el 12 de septiembre de 2026,
imagen `docker.io/library/postgres:16`).

**Comandos de uso frecuente:**
```bash
podman ps -a
podman start sgva-postgres
podman stop sgva-postgres
```

**Credenciales de desarrollo local** (no usar en ningún entorno real):
- Host: `localhost:5432`
- Base de datos: `sgva`
- Usuario: `sgva`
- Contraseña: `sgva_dev_password`

> ⚠️ Estas credenciales están pensadas únicamente para desarrollo local en
> esta máquina. Si se externalizan a variables de entorno en
> `application.yml` (decisión a tomar en V3), documentar aquí el nombre de
> las variables usadas.

---

## 3. Arrancar la aplicación

```bash
./gradlew bootRun
```

Antes de V3: usa el adaptador en memoria por defecto, no requiere Postgres
corriendo.

Desde V3: requiere `sgva-postgres` activo (ver sección 2) para que la
aplicación levante correctamente, salvo que se mantenga un perfil de Spring
alternativo para pruebas rápidas sin base de datos real (a definir en el
prompt de V3).

---

## 4. Checklist rápido antes de una sesión de trabajo

```bash
podman ps    # confirmar que sonarqube y (desde V3) sgva-postgres están "Up"
```

Si alguno no aparece como `Up`:
```bash
podman start sonarqube
podman start sgva-postgres   # solo si ya se creó (V3+)
```
