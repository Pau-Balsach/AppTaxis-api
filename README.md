# AppTaxis API

API REST para la gestión de conductores y viajes de la flota de taxis.  
Documentación interactiva disponible en `/swagger-ui.html`.

## Endpoints

### Conductores
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/conductores` | Listar todos |
| GET | `/api/conductores/{id}` | Buscar por ID |
| POST | `/api/conductores` | Registrar nuevo |
| PUT | `/api/conductores/{id}` | Editar nombre |
| DELETE | `/api/conductores/{id}` | Eliminar |

### Viajes
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/viajes` | Listar todos |
| GET | `/api/viajes/conductor/{id}` | Viajes de un conductor |
| GET | `/api/viajes/{uuid}` | Buscar por UUID |
| POST | `/api/viajes/conductor/{id}` | Crear viaje |
| PUT | `/api/viajes/{uuid}` | Editar viaje |
| DELETE | `/api/viajes/{uuid}` | Eliminar viaje |

## Variables de entorno requeridas

| Variable | Descripción |
|---|---|
| `DB_URL` | URL JDBC de Supabase |
| `DB_USER` | Usuario de la BD |
| `DB_PASSWORD` | Contraseña de la BD |
| `PORT` | Puerto (por defecto 8080) |

## Licencia
MIT
