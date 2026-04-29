# API REST — Gestor de Torneos (backend)

Documentación generada a partir del código en `backend/`. Todas las rutas asumen el prefijo donde esté desplegado el servicio Spring Boot (por ejemplo `http://localhost:8080`).

---

## Formato de errores

Las excepciones gestionadas por `GlobalExceptionHandler` devuelven JSON con esta forma:

```json
{
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Torneo no encontrado con id: 99",
  "path": "/api/torneos/99"
}
```

| Código HTTP | Campo `error`     | Cuándo |
|-------------|-------------------|--------|
| 400         | `BAD_REQUEST`     | Validación o reglas de negocio que rechazan la petición |
| 404         | `NOT_FOUND`       | Recurso no encontrado |
| 409         | `CONFLICT`        | Conflicto (duplicado, estado incompatible, etc.) |
| 500         | `INTERNAL_ERROR`  | Cualquier otra excepción no capturada de forma específica |

Los mensajes concretos dependen del servicio (texto en español en la mayoría de los casos).

---

## Autenticación (`/api/auth`)

### POST `/api/auth/login`

- **Descripción:** Inicia sesión comprobando email y contraseña (sin JWT en el estado actual del proyecto).
- **Body JSON:**

```json
{
  "email": "usuario@ejemplo.com",
  "password": "secreto"
}
```

- **Respuesta 200:** `UsuarioResponse` (sin `passwordHash`).

```json
{
  "id": 1,
  "nombre": "Ana",
  "email": "usuario@ejemplo.com",
  "fechaRegistro": "2026-04-26T10:00:00+02:00",
  "rol": "USUARIO",
  "activo": true
}
```

- **Errores:** `400` credenciales incorrectas; otros según fallos internos (`500`).

### POST `/api/auth/register`

- **Descripción:** Registra un usuario. El rol es opcional; por defecto se usa `USUARIO`.
- **Body JSON:**

```json
{
  "nombre": "Ana",
  "email": "usuario@ejemplo.com",
  "password": "secreto",
  "rol": "USUARIO"
}
```

- **Respuesta 200:** `UsuarioResponse`.
- **Errores:** `409` si el email ya existe; `500` u otros no tipados.

### GET `/api/auth/me`

- **Descripción:** Obtiene el usuario por email (query param).
- **Query:** `email` (obligatorio), ej. `?email=usuario@ejemplo.com`
- **Respuesta 200:** `UsuarioResponse`.
- **Errores:** `404` si no hay usuario con ese email.

---

## Usuarios (`/api/usuarios`)

### POST `/api/usuarios`

- **Descripción:** Crea un usuario (administración / alta directa). Rol opcional; por defecto `USUARIO`; `activo=true`; `fechaRegistro` en servidor.
- **Body JSON:**

```json
{
  "nombre": "Ana",
  "email": "ana@ejemplo.com",
  "password": "secreto",
  "rol": "USUARIO"
}
```

- **Respuesta 200:** `UsuarioResponse`.
- **Errores:** `400` campos obligatorios vacíos; `409` email ya registrado.

### GET `/api/usuarios`

- **Descripción:** Lista todos los usuarios.
- **Body:** no.
- **Respuesta 200:** array de `UsuarioResponse`.

### GET `/api/usuarios/{id}`

- **Descripción:** Obtiene un usuario por id.
- **Respuesta 200:** `UsuarioResponse`.
- **Errores:** `404` usuario no encontrado.

### PATCH `/api/usuarios/{id}/desactivar`

- **Descripción:** Pone `activo=false` (no borra el registro).
- **Body:** no.
- **Respuesta 200:** `UsuarioResponse`.
- **Errores:** `404` si el id no existe.

### PATCH `/api/usuarios/{id}/activar`

- **Descripción:** Pone `activo=true`.
- **Body:** no.
- **Respuesta 200:** `UsuarioResponse`.
- **Errores:** `404` si el id no existe.

**Valores típicos de `rol`:** `ADMIN`, `ORGANIZADOR`, `USUARIO` (enum `RolUsuario`).

---

## Torneos (`/api/torneos`)

### GET `/api/torneos`

- **Descripción:** Lista torneos ordenados por fecha de creación descendente.
- **Respuesta 200:** array de `TorneoResponse`.

```json
[
  {
    "id": 1,
    "nombre": "Open Primavera",
    "descripcion": "Torneo relámpago",
    "fechaInicio": "2026-05-01T10:00:00+02:00",
    "fechaFin": "2026-05-01T18:00:00+02:00",
    "estado": "ABIERTO",
    "formato": "ELIMINATORIA",
    "nivel": "INTERMEDIO",
    "plazasMaximas": 32,
    "numRondas": 5,
    "costeInscripcion": 10.00,
    "fechaCreacion": "2026-04-20T12:00:00+02:00",
    "creadorId": 3,
    "creadorNombre": "Organizador"
  }
]
```

### GET `/api/torneos/{id}`

- **Descripción:** Detalle de un torneo.
- **Respuesta 200:** objeto `TorneoResponse`.
- **Errores:** `404` torneo no encontrado.

### POST `/api/torneos`

- **Descripción:** Crea un torneo en estado `PENDIENTE` con `fechaCreacion` actual.
- **Body JSON** (`CrearTorneoRequest`):

```json
{
  "nombre": "Open Primavera",
  "descripcion": "Opcional",
  "fechaInicio": "2026-05-01T10:00:00+02:00",
  "fechaFin": "2026-05-01T18:00:00+02:00",
  "formato": "ELIMINATORIA",
  "nivel": "INTERMEDIO",
  "plazasMaximas": 32,
  "numRondas": 5,
  "costeInscripcion": 10.00,
  "creadorId": 3
}
```

- **Respuesta 200:** `TorneoResponse`.
- **Errores:** `400` validaciones (fechas, plazas, rondas, coste, nombre, creador inactivo, etc.); `404` creador no encontrado.

**Enums útiles:** `EstadoTorneo` (al crear suele ser `PENDIENTE`), `FormatoTorneo` (`ELIMINATORIA`, `SUIZO`, `LIGA`), `NivelTorneo` (`PRINCIPIANTE`, `INTERMEDIO`, `AVANZADO`).

### PUT `/api/torneos/{id}`

- **Descripción:** Actualiza datos editables del torneo. Actualización **parcial**: solo se aplican campos presentes (no `null`) en el body. No modifica creador ni estado.
- **Body JSON** (`ActualizarTorneoRequest`): cualquier subconjunto de

```json
{
  "nombre": "Nuevo nombre",
  "descripcion": "Texto o cadena vacía",
  "fechaInicio": "2026-05-01T10:00:00+02:00",
  "fechaFin": "2026-05-01T18:00:00+02:00",
  "formato": "SUIZO",
  "nivel": "AVANZADO",
  "plazasMaximas": 16,
  "numRondas": 4,
  "costeInscripcion": 0
}
```

- **Respuesta 200:** `TorneoResponse`.
- **Errores:** `400` sin campos para actualizar, nombre vacío, plazas/rondas ≤ 0, coste negativo, `fechaFin` anterior a `fechaInicio`; `404` torneo no encontrado.

### PATCH `/api/torneos/{id}/estado`

- **Descripción:** Cambia el estado del torneo según reglas de negocio (`validarCambioEstado`).
- **Body JSON:**

```json
{
  "estado": "ABIERTO"
}
```

- **Respuesta 200:** `TorneoResponse`.
- **Errores:** `400` estado nulo o transición inválida; `409` torneo ya terminal o mismo estado.

### PATCH `/api/torneos/{id}/abrir`

- **Descripción:** Fija estado `ABIERTO` (mismas validaciones que cambiar estado a abierto).
- **Body:** no.
- **Respuesta 200:** `TorneoResponse`.
- **Errores:** `400`, `409`, `404` según reglas.

### PATCH `/api/torneos/{id}/cerrar`

- **Descripción:** Estado `CERRADO`.
- **Body:** no.
- **Respuesta 200:** `TorneoResponse`.

### PATCH `/api/torneos/{id}/iniciar`

- **Descripción:** Estado `EN_CURSO` (requiere entre otras cosas ≥2 inscritos en `INSCRITO` y estado previo `ABIERTO` o `CERRADO`).
- **Body:** no.
- **Respuesta 200:** `TorneoResponse`.

### PATCH `/api/torneos/{id}/finalizar`

- **Descripción:** Estado `FINALIZADO` (desde `EN_CURSO`).
- **Body:** no.
- **Respuesta 200:** `TorneoResponse`.

### PATCH `/api/torneos/{id}/cancelar`

- **Descripción:** Estado `CANCELADO`.
- **Body:** no.
- **Respuesta 200:** `TorneoResponse`.

---

## Inscripciones

### POST `/api/torneos/{torneoId}/inscripciones`

- **Descripción:** Inscribe un usuario en un torneo (crea `Inscripcion` y clasificación inicial si aplica).
- **Body JSON:**

```json
{
  "usuarioId": 5
}
```

- **Respuesta 200:** `InscripcionResponse`.

```json
{
  "id": 10,
  "estado": "INSCRITO",
  "fechaInscripcion": "2026-04-26T11:00:00+02:00",
  "usuarioId": 5,
  "usuarioNombre": "Ana",
  "torneoId": 1
}
```

- **Errores:** `404` torneo o usuario; `400` torneo no abierto, usuario inactivo; `409` ya inscrito, sin plazas.

### GET `/api/torneos/{torneoId}/inscripciones`

- **Descripción:** Lista inscripciones del torneo.
- **Respuesta 200:** array de `InscripcionResponse`.
- **Errores:** `404` torneo no encontrado.

### PATCH `/api/inscripciones/{id}/retiro`

- **Descripción:** Marca la inscripción como `RETIRADO`.
- **Body:** no.
- **Respuesta 200:** `InscripcionResponse`.
- **Errores:** `404` inscripción no encontrada; `409` si ya estaba retirada.

**Estados de inscripción** (referencia): `INSCRITO`, `ELIMINADO`, `RETIRADO`, `CANCELADO`.

---

## Clasificación

### GET `/api/torneos/{torneoId}/clasificacion`

- **Descripción:** Tabla de clasificación del torneo (orden por puntos y desempate Buchholz).
- **Respuesta 200:** array de `ClasificacionResponse`.

```json
[
  {
    "id": 1,
    "puntos": 9,
    "posicion": 1,
    "victorias": 3,
    "derrotas": 0,
    "empates": 0,
    "partidasJugadas": 3,
    "desempateBuchholz": 0.0000,
    "usuarioId": 5,
    "usuarioNombre": "Ana",
    "torneoId": 1
  }
]
```

- **Errores:** `404` si el torneo no existe.

---

## Rondas

### GET `/api/torneos/{torneoId}/rondas`

- **Descripción:** Lista rondas del torneo por número ascendente.
- **Respuesta 200:** array de `RondaResponse`.

```json
[
  {
    "id": 2,
    "numero": 1,
    "estado": "EN_CURSO",
    "fechaCreacion": "2026-04-26T09:00:00+02:00",
    "torneoId": 1
  }
]
```

- **Errores:** `404` torneo no encontrado.

### POST `/api/torneos/{torneoId}/rondas`

- **Descripción:** Crea una ronda (torneo en `EN_CURSO`, validaciones de número y concurrencia).
- **Body JSON:**

```json
{
  "numero": 1
}
```

- **Respuesta 200:** `RondaResponse`.
- **Errores:** `400` estado del torneo o número inválido; `409` número duplicado u otra ronda en curso; `404` torneo.

### PATCH `/api/torneos/{torneoId}/rondas/{rondaId}/finalizar`

- **Descripción:** Pasa la ronda a `FINALIZADA` si no quedan partidas `PENDIENTE`.
- **Body:** no.
- **Respuesta 200:** `RondaResponse`.
- **Errores:** `404` ronda; `400` ronda no del torneo o no en curso; `409` partidas pendientes.

**Estados de ronda:** `PENDIENTE`, `EN_CURSO`, `FINALIZADA`.

---

## Partidas

### GET `/api/torneos/{torneoId}/partidas`

- **Descripción:** Partidas del torneo ordenadas por fecha.
- **Respuesta 200:** array de `PartidaResponse`.

```json
[
  {
    "id": 20,
    "resultado": "1-0",
    "fechaPartida": "2026-04-26T12:00:00+02:00",
    "estado": "JUGADA",
    "torneoId": 1,
    "rondaId": 2,
    "usuario1Id": 5,
    "usuario1Nombre": "Ana",
    "usuario2Id": 6,
    "usuario2Nombre": "Luis",
    "ganadorId": 5,
    "ganadorNombre": "Ana"
  }
]
```

- **Errores:** `404` torneo no encontrado.

### GET `/api/rondas/{rondaId}/partidas`

- **Descripción:** Partidas de una ronda.
- **Respuesta 200:** array de `PartidaResponse`.
- **Errores:** `404` ronda no encontrada.

### PATCH `/api/partidas/{id}/resultado`

- **Descripción:** Registra resultado y opcionalmente ganador; pasa la partida a `JUGADA`.
- **Body JSON:**

```json
{
  "resultado": "1-0",
  "ganadorId": 5
}
```

(`ganadorId` opcional.)

- **Respuesta 200:** `PartidaResponse`.
- **Errores:** `404` partida o ganador; `400` partida cancelada, resultado vacío, ganador que no participa.

### PATCH `/api/partidas/{id}/validar`

- **Descripción:** Marca la partida como `VALIDADA` y recalcula clasificación.
- **Body:** no.
- **Respuesta 200:** `PartidaResponse`.
- **Errores:** `404` partida; `400` sin resultado (`PENDIENTE`); `409` ya validada; `404` en recálculo de clasificación si faltan filas.

**Estados de partida:** `PENDIENTE`, `JUGADA`, `VALIDADA`, `CANCELADA`.

---

## Pendientes

Endpoints o capacidades **no** presentes en los controladores actuales (referencia para evolución; no forman parte de la API implementada):

- **Usuarios:** no hay `PUT`/`PATCH` genérico para editar nombre, email o contraseña; no hay `DELETE`.
- **Torneos:** no hay `DELETE`.
- **Inscripciones:** no hay listado global ni `GET` por id de inscripción.
- **Rondas:** no hay `GET /api/rondas/{id}` ni eliminación de ronda.
- **Partidas:** no hay creación explícita de partidas por API en los controladores revisados (solo listado, resultado y validar).
- **Seguridad:** no hay JWT ni anotaciones de permisos en controladores; `/api/auth/me` usa query `email` en claro.

Si añades nuevas rutas, conviene actualizar este archivo para mantenerlo alineado con el código.
