# AppTaxis API

API REST para la gestión de conductores, viajes y clientes de una flota de taxis.

**Versión:** 1.1.0 — OAS 3.0

**Swagger UI:** https://apptaxis-api.onrender.com/swagger-ui/index.html

**Base URL:** `https://apptaxis-api.onrender.com`

---

## Autenticación

Todos los endpoints requieren el header `X-API-Key` con una clave válida asignada a tu cuenta.

```
X-API-Key: tu_clave_aquí
```

Cada clave solo permite acceder a los datos de su propio cliente — no puedes ver ni modificar datos de otras cuentas. Si la clave falta o es inválida, la API devuelve `401 Unauthorized`.

---

## Endpoints

### Conductores

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/conductores` | Lista todos los conductores del cliente autenticado |
| GET | `/conductores/{id}` | Busca un conductor por ID |
| POST | `/conductores` | Registra un nuevo conductor |
| PUT | `/conductores/{id}` | Edita el nombre de un conductor |
| DELETE | `/conductores/{id}` | Elimina un conductor |

**POST `/conductores`** — body:
```json
{
  "nombre": "Juan García",
  "matricula": "1234ABC"
}
```
La matrícula debe seguir el formato `1234ABC` (4 números + 3 letras mayúsculas). El conductor queda vinculado al cliente autenticado.

**PUT `/conductores/{id}`** — parámetro query:
```
PUT /conductores/1?nuevoNombre=Pedro López
```

**Esquema Conductor:**
```json
{
  "id": 0,
  "nombre": "string",
  "matricula": "string",
  "cond_admin": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
}
```

---

### Viajes

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/viajes` | Lista todos los viajes del cliente autenticado |
| GET | `/viajes/{id}` | Busca un viaje por UUID |
| GET | `/viajes/conductor/{conductorId}` | Lista los viajes de un conductor ordenados por fecha y hora |
| POST | `/viajes/conductor/{conductorId}` | Crea un viaje asignado a un conductor |
| PUT | `/viajes/{id}` | Edita un viaje existente |
| DELETE | `/viajes/{id}` | Elimina un viaje |

**POST `/viajes/conductor/{conductorId}`** — body:
```json
{
  "dia": "2025-04-15",
  "diaFin": "2025-04-15",
  "hora": "09:30:00",
  "horaFinalizacion": "10:15:00",
  "puntorecogida": "Calle Mayor 1, Barcelona",
  "puntodejada": "Aeropuerto T1, Barcelona",
  "telefonocliente": "600123456",
  "cliente": { "id": 3 }
}
```

El campo `diaFin` permite registrar viajes que cruzan la medianoche — si el viaje termina al día siguiente, `diaFin` será distinto de `dia`. El campo `cliente.id` es opcional.

**PUT `/viajes/{id}`** — mismo body que POST, incluyendo `conductor` y `cliente`:
```json
{
  "dia": "2025-04-15",
  "diaFin": "2025-04-15",
  "hora": "09:30:00",
  "horaFinalizacion": "10:15:00",
  "puntorecogida": "Calle Mayor 1, Barcelona",
  "puntodejada": "Aeropuerto T1, Barcelona",
  "telefonocliente": "600123456",
  "conductor": { "id": 1 },
  "cliente": { "id": 3 }
}
```

**Esquema Viaje:**
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "dia": "2026-04-19",
  "diaFin": "2026-04-19",
  "hora": "09:30:00",
  "horaFinalizacion": "10:15:00",
  "puntorecogida": "string",
  "puntodejada": "string",
  "telefonocliente": "string",
  "conductor": {
    "id": 0,
    "nombre": "string",
    "matricula": "string",
    "cond_admin": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
  },
  "cliente": {
    "id": 0,
    "nombre": "string",
    "telefono": "string",
    "email": "string",
    "notas": "string",
    "adminId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
  }
}
```

---

### Clientes

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/clientes` | Lista todos los clientes. Acepta `?q=texto` para buscar por nombre o teléfono |
| GET | `/clientes/{id}` | Busca un cliente por ID |
| POST | `/clientes` | Crea un nuevo cliente |
| PUT | `/clientes/{id}` | Edita un cliente existente |
| DELETE | `/clientes/{id}` | Elimina un cliente |
| GET | `/clientes/{id}/viajes` | Historial de viajes del cliente ordenado por fecha descendente |

**POST `/clientes`** — body:
```json
{
  "nombre": "María López",
  "telefono": "600987654",
  "email": "maria@email.com",
  "notas": "Cliente habitual aeropuerto"
}
```
Los campos `email` y `notas` son opcionales.

**GET `/clientes?q=maria`** — busca clientes cuyo nombre o teléfono contengan el texto.

**Esquema Cliente:**
```json
{
  "id": 0,
  "nombre": "string",
  "telefono": "string",
  "email": "string",
  "notas": "string",
  "adminId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
}
```

---

## Esquema de base de datos

```sql
-- Administradores (gestionados por Supabase Auth)
admins (
  id          UUID PRIMARY KEY,
  email       VARCHAR
)

-- Claves de acceso a la API
api_keys (
  id             SERIAL PRIMARY KEY,
  nombre_cliente VARCHAR,
  key_hash       VARCHAR,   -- SHA-256 de la clave real
  activa         BOOLEAN,
  admin_id       UUID REFERENCES admins(id)
)

-- Conductores
conductores (
  id         SERIAL PRIMARY KEY,
  nombre     VARCHAR,
  matricula  VARCHAR,
  cond_admin UUID REFERENCES admins(id)
)

-- Clientes
clientes (
  id       SERIAL PRIMARY KEY,
  nombre   VARCHAR NOT NULL,
  telefono VARCHAR NOT NULL,
  email    VARCHAR,
  notas    TEXT,
  admin_id UUID REFERENCES admins(id)
)

-- Viajes
viajes (
  id                UUID PRIMARY KEY,
  dia               DATE,
  dia_fin           DATE,          -- nullable, distinto de dia si cruza medianoche
  hora              TIME,
  hora_finalizacion TIME,
  puntorecogida     VARCHAR,
  puntodejada       VARCHAR,
  telefonocliente   VARCHAR,
  conductor_id      INTEGER REFERENCES conductores(id),
  cliente_id        INTEGER REFERENCES clientes(id)  -- nullable
)
```

---

## Ejecutar con Docker

### Requisitos
- Docker y Docker Compose instalados
- Una base de datos PostgreSQL accesible (Supabase o local)

### Variables de entorno

Crea un archivo `.env` en la raíz del proyecto:

```env
DB_URL=jdbc:postgresql://host:5432/postgres?sslmode=require&prepareThreshold=0
DB_USER=tu_usuario
DB_PASSWORD=tu_contraseña
SERVER_PORT=8080
```

### Dockerfile

```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/apptaxis-api-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml

```yaml
version: '3.8'
services:
  api:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=${DB_URL}
      - SPRING_DATASOURCE_USERNAME=${DB_USER}
      - SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
      - SERVER_PORT=${SERVER_PORT}
    restart: unless-stopped
```

### Comandos

```bash
# 1. Compilar el proyecto
mvn clean package -DskipTests

# 2. Construir la imagen
docker build -t apptaxis-api .

# 3. Arrancar con docker-compose
docker-compose up -d

# 4. Ver logs
docker-compose logs -f

# 5. Parar
docker-compose down
```

La API estará disponible en `http://localhost:8080` y el Swagger en `http://localhost:8080/swagger-ui/index.html`.

---

## Estructura del proyecto

```
src/main/java/
├── com/apptaxis/api/
│   ├── AppTaxisApiApplication.java       ← Punto de entrada Spring Boot
│   ├── SwaggerConfig.java                ← Configuración OpenAPI / Swagger
│   ├── controller/
│   │   ├── ClienteController.java        ← Endpoints /clientes
│   │   ├── ConductorController.java      ← Endpoints /conductores
│   │   └── ViajeController.java          ← Endpoints /viajes
│   ├── dto/
│   │   └── ApiResponse.java              ← Wrapper genérico de respuestas
│   ├── exception/
│   │   ├── BadRequestException.java      ← Excepción de petición incorrecta
│   │   ├── GlobalExceptionHandler.java   ← Manejo centralizado de errores
│   │   └── ResourceNotFoundException.java← Excepción de recurso no encontrado
│   ├── model/
│   │   ├── ApiKey.java                   ← Entidad api_keys
│   │   ├── Cliente.java                  ← Entidad clientes
│   │   ├── Conductor.java                ← Entidad conductores
│   │   └── Viaje.java                    ← Entidad viajes
│   ├── repository/
│   │   ├── ApiKeyRepository.java         ← Acceso a api_keys
│   │   ├── ClienteRepository.java        ← Acceso a clientes
│   │   ├── ConductorRepository.java      ← Acceso a conductores
│   │   └── ViajeRepository.java          ← Acceso a viajes
│   └── service/
│       ├── ClienteService.java           ← Lógica de negocio clientes
│       ├── ConductorService.java         ← Lógica de negocio conductores
│       └── ViajeService.java             ← Lógica de negocio viajes
└── security/
    ├── ApiKeyFilter.java                 ← Filtro HTTP que valida X-API-Key
    └── SecurityUtils.java                ← Hash SHA-256
```

---

## Despliegue en Render

El proyecto está desplegado en Render conectado a Supabase como base de datos PostgreSQL.

Variables de entorno configuradas en Render:

| Variable | Descripción |
|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC de Supabase con `?prepareThreshold=0` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la BD |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la BD |

El redeploy es automático en cada push a la rama principal.
