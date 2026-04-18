# AppTaxis API

API REST para la gestión de conductores, viajes y clientes de una flota de taxis.

**Swagger UI:** https://apptaxis-api-production.up.railway.app/swagger-ui/index.html#/

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
| GET | `/conductores` | Lista todos los conductores de tu cuenta |
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
La matrícula debe seguir el formato `1234ABC` (4 números + 3 letras mayúsculas).

**PUT `/conductores/{id}`** — parámetro query:
```
PUT /conductores/1?nuevoNombre=Pedro López
```

---

### Viajes

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/viajes` | Lista todos los viajes de tu cuenta |
| GET | `/viajes/{id}` | Busca un viaje por UUID |
| GET | `/viajes/conductor/{conductorId}` | Lista los viajes de un conductor ordenados por fecha y hora |
| POST | `/viajes/conductor/{conductorId}` | Crea un viaje asignado a un conductor |
| PUT | `/viajes/{id}` | Edita un viaje existente |
| DELETE | `/viajes/{id}` | Elimina un viaje |

**POST `/viajes/conductor/{conductorId}`** — body:
```json
{
  "dia": "2025-04-15",
  "hora": "09:30",
  "puntorecogida": "Calle Mayor 1, Barcelona",
  "puntodejada": "Aeropuerto T1, Barcelona",
  "telefonocliente": "600123456",
  "cliente": { "id": 3 }
}
```
El campo `cliente.id` es opcional. Si se incluye, el viaje queda vinculado a ese cliente y `telefonocliente` se autocompleta con el teléfono del cliente si no se envía.

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

---

## Estructura de datos

### Conductor
```json
{
  "id": 1,
  "nombre": "Juan García",
  "matricula": "1234ABC"
}
```

### Cliente
```json
{
  "id": 1,
  "nombre": "María López",
  "telefono": "600987654",
  "email": "maria@email.com",
  "notas": "Cliente habitual aeropuerto"
}
```

### Viaje
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "dia": "2025-04-15",
  "hora": "09:30:00",
  "horaFinalizacion": "10:15:00",
  "puntorecogida": "Calle Mayor 1, Barcelona",
  "puntodejada": "Aeropuerto T1, Barcelona",
  "telefonocliente": "600987654",
  "conductor": {
    "id": 1,
    "nombre": "Juan García",
    "matricula": "1234ABC"
  },
  "cliente": {
    "id": 1,
    "nombre": "María López",
    "telefono": "600987654"
  }
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
  id               UUID PRIMARY KEY,
  dia              DATE,
  hora             TIME,
  hora_finalizacion TIME,
  puntorecogida    VARCHAR,
  puntodejada      VARCHAR,
  telefonocliente  VARCHAR,
  conductor_id     INTEGER REFERENCES conductores(id),
  cliente_id       INTEGER REFERENCES clientes(id)  -- nullable
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
src/main/java/com/apptaxis/api/
├── AppTaxisApiApplication.java       ← Punto de entrada Spring Boot
├── SwaggerConfig.java                ← Configuración OpenAPI / Swagger
├── controller/
│   ├── ConductorController.java      ← Endpoints /conductores
│   ├── ViajeController.java          ← Endpoints /viajes
│   └── ClienteController.java        ← Endpoints /clientes
├── service/
│   ├── ConductorService.java         ← Lógica de negocio conductores
│   ├── ViajeService.java             ← Lógica de negocio viajes
│   └── ClienteService.java           ← Lógica de negocio clientes
├── repository/
│   ├── ApiKeyRepository.java         ← Acceso a api_keys
│   ├── ConductorRepository.java      ← Acceso a conductores
│   ├── ViajeRepository.java          ← Acceso a viajes
│   └── ClienteRepository.java        ← Acceso a clientes
├── model/
│   ├── ApiKey.java                   ← Entidad api_keys
│   ├── Conductor.java                ← Entidad conductores
│   ├── Viaje.java                    ← Entidad viajes
│   └── Cliente.java                  ← Entidad clientes
├── security/
│   ├── ApiKeyFilter.java             ← Filtro HTTP que valida X-API-Key
│   └── SecurityUtils.java            ← Hash SHA-256
├── dto/
│   └── ApiResponse.java              ← Wrapper genérico de respuestas
└── exception/
    ├── GlobalExceptionHandler.java   ← Manejo centralizado de errores
    ├── ResourceNotFoundException.java
    └── BadRequestException.java
```

---

## Despliegue en Railway

El proyecto está desplegado en Railway conectado a Supabase como base de datos PostgreSQL.

Variables de entorno configuradas en Railway:

| Variable | Descripción |
|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC de Supabase con `?prepareThreshold=0` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la BD |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la BD |

El redeploy es automático en cada push a la rama principal.
