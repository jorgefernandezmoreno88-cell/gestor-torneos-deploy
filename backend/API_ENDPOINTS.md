# API Endpoints - Gestor de Torneos

Documentacion de endpoints REST del backend Spring Boot (`com.jorge.gestorTorneos`) basada en los controladores actuales.

Base URL local habitual: `http://localhost:8080`

## Formato general de errores

Las excepciones manejadas por `GlobalExceptionHandler` devuelven un JSON similar a:

```json
{
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Mensaje de error",
  "path": "/api/..."
}
```

Errores frecuentes por endpoint:
- `400 BAD_REQUEST`
- `404 NOT_FOUND`
- `409 CONFLICT`
- `500 INTERNAL_SERVER_ERROR`

---

## 1) Usuarios

### POST `/api/auth/register`
- **Descripcion:** registro de usuario (autenticacion basica).
- **Body JSON:**
```json
{
  "nombre": "Ana",
  "email": "ana@mail.com",
  "password": "123456"
}
```
- **Respuesta simplificada:**
```json
{
  "id": 1,
  "nombre": "Ana",
  "email": "ana@mail.com",
  "activo": true
}
```
- **Errores posibles:** `400`, `409`.

### POST `/api/auth/login`
- **Descripcion:** login por email/password.
- **Body JSON:**
```json
{
  "email": "ana@mail.com",
  "password": "123456"
}
```
- **Respuesta simplificada:** `UsuarioResponse`.
- **Errores posibles:** `400`, `404`.

### GET `/api/auth/me?email={email}`
- **Descripcion:** consulta de usuario por email.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** `UsuarioResponse`.
- **Errores posibles:** `400`, `404`.

### POST `/api/usuarios`
- **Descripcion:** crear usuario desde modulo de usuarios.
- **Body JSON:**
```json
{
  "nombre": "Pepe",
  "email": "pepe@mail.com",
  "password": "clave",
  "rol": "USUARIO"
}
```
- **Respuesta simplificada:** `UsuarioResponse`.
- **Errores posibles:** `400`, `409`.

### GET `/api/usuarios`
- **Descripcion:** listar usuarios.
- **Body JSON:** no aplica.
- **Respuesta simplificada:**
```json
[
  { "id": 1, "nombre": "Ana", "email": "ana@mail.com", "activo": true }
]
```
- **Errores posibles:** `500`.

### GET `/api/usuarios/{id}`
- **Descripcion:** obtener un usuario por id.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** `UsuarioResponse`.
- **Errores posibles:** `404`.

### PATCH `/api/usuarios/{id}/desactivar`
- **Descripcion:** desactivar usuario.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** `UsuarioResponse` con `activo=false`.
- **Errores posibles:** `404`, `409`.

### PATCH `/api/usuarios/{id}/activar`
- **Descripcion:** activar usuario.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** `UsuarioResponse` con `activo=true`.
- **Errores posibles:** `404`, `409`.

---

## 2) Torneos

### GET `/api/torneos`
- **Descripcion:** listar torneos.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** arreglo de `TorneoResponse`.
- **Errores posibles:** `500`.

### GET `/api/torneos/{id}`
- **Descripcion:** obtener torneo por id.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** `TorneoResponse`.
- **Errores posibles:** `404`.

### POST `/api/torneos`
- **Descripcion:** crear torneo.
- **Body JSON:**
```json
{
  "nombre": "Open Primavera",
  "descripcion": "Torneo suizo",
  "fechaInicio": "2026-05-01T10:00:00Z",
  "fechaFin": "2026-05-01T20:00:00Z",
  "formato": "SUIZO",
  "nivel": "AMATEUR",
  "plazasMaximas": 16,
  "numRondas": 5,
  "costeInscripcion": 10.0,
  "creadorId": 1
}
```
- **Respuesta simplificada:** `TorneoResponse`.
- **Errores posibles:** `400`, `404`.

### PUT `/api/torneos/{id}`
- **Descripcion:** actualizar datos de torneo.
- **Body JSON:** `ActualizarTorneoRequest`.
- **Respuesta simplificada:** `TorneoResponse`.
- **Errores posibles:** `400`, `404`, `409`.

### PATCH `/api/torneos/{id}/estado`
- **Descripcion:** cambio de estado generico.
- **Body JSON:**
```json
{
  "estado": "EN_CURSO"
}
```
- **Respuesta simplificada:** `TorneoResponse`.
- **Errores posibles:** `400`, `404`, `409`.

### PATCH `/api/torneos/{id}/abrir`
### PATCH `/api/torneos/{id}/cerrar`
### PATCH `/api/torneos/{id}/iniciar`
### PATCH `/api/torneos/{id}/finalizar`
### PATCH `/api/torneos/{id}/cancelar`
- **Descripcion:** shortcuts de cambio de estado.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** `TorneoResponse`.
- **Errores posibles:** `400`, `404`, `409`.

---

## 3) Inscripciones

### POST `/api/torneos/{torneoId}/inscripciones`
- **Descripcion:** inscribir usuario en torneo.
- **Body JSON:**
```json
{
  "usuarioId": 5
}
```
- **Respuesta simplificada:** `InscripcionResponse`.
- **Errores posibles:** `400`, `404`, `409`.

### GET `/api/torneos/{torneoId}/inscripciones`
- **Descripcion:** listar inscripciones del torneo.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** arreglo de `InscripcionResponse`.
- **Errores posibles:** `404`.

### PATCH `/api/inscripciones/{id}/retiro`
- **Descripcion:** marcar retiro de una inscripcion.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** `InscripcionResponse`.
- **Errores posibles:** `404`, `409`.

### PATCH `/api/inscripciones/{inscripcionId}/retirar`
- **Descripcion:** retirar una inscripcion.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** `InscripcionResponse`.
- **Errores posibles:** `404`, `409`.

### PATCH `/api/inscripciones/{inscripcionId}/cancelar`
- **Descripcion:** cancelar una inscripcion.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** `InscripcionResponse`.
- **Errores posibles:** `404`, `409`.

---

## 4) Clasificacion

### GET `/api/torneos/{torneoId}/clasificacion`
- **Descripcion:** devuelve clasificacion ordenada (puntos, buchholz, victorias) con posiciones recalculadas.
- **Body JSON:** no aplica.
- **Respuesta simplificada:**
```json
[
  {
    "usuarioId": 5,
    "posicion": 1,
    "puntos": 9,
    "victorias": 3,
    "empates": 0,
    "derrotas": 1,
    "desempateBuchholz": 18.0
  }
]
```
- **Errores posibles:** `404`.

---

## 5) Rondas

### GET `/api/torneos/{torneoId}/rondas`
- **Descripcion:** listar rondas del torneo ordenadas por numero ASC.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** arreglo de `RondaResponse`.
- **Errores posibles:** `404`.

### POST `/api/torneos/{torneoId}/rondas`
- **Descripcion:** crear ronda manual con numero.
- **Body JSON:**
```json
{
  "numero": 2
}
```
- **Respuesta simplificada:** `RondaResponse`.
- **Errores posibles:** `400`, `404`, `409`.

### POST `/api/torneos/{torneoId}/generar-ronda-inicial`
- **Descripcion:** generar ronda 1 y partidas iniciales.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** `GenerarRondaInicialResponse` (ronda + partidas).
- **Errores posibles:** `400`, `404`, `409`.

### POST `/api/torneos/{torneoId}/generar-siguiente-ronda`
- **Descripcion:** generar siguiente ronda usando clasificacion y reglas actuales (incluye BYE).
- **Body JSON:** no aplica.
- **Respuesta simplificada:** `GenerarRondaInicialResponse` (ronda + partidas).
- **Errores posibles:** `400`, `404`, `409`.

### PATCH `/api/rondas/{rondaId}/iniciar`
- **Descripcion:** pasar ronda `PENDIENTE` a `EN_CURSO`.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** `RondaResponse`.
- **Errores posibles:** `400`, `404`.

### PATCH `/api/rondas/{rondaId}/finalizar`
- **Descripcion:** finalizar ronda si todas las partidas estan `VALIDADA`.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** `RondaResponse`.
- **Errores posibles:** `400`, `404`, `409`.

### PATCH `/api/torneos/{torneoId}/rondas/{rondaId}/finalizar`
- **Descripcion:** variante de finalizar validando tambien pertenencia de la ronda al torneo.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** `RondaResponse`.
- **Errores posibles:** `400`, `404`, `409`.

---

## 6) Partidas

### GET `/api/torneos/{torneoId}/partidas`
- **Descripcion:** listar partidas del torneo.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** arreglo de `PartidaResponse`.
- **Errores posibles:** `404`.

### GET `/api/rondas/{rondaId}/partidas`
- **Descripcion:** listar partidas de una ronda.
- **Body JSON:** no aplica.
- **Respuesta simplificada:** arreglo de `PartidaResponse`.
- **Errores posibles:** `404`.

### PATCH `/api/partidas/{partidaId}/resultado`
- **Descripcion:** registrar resultado y validar partida en una sola operacion.
- **Body JSON:**
```json
{
  "resultado": "1-0",
  "ganadorId": 5,
  "empate": false
}
```
- **Respuesta simplificada:** `PartidaResponse` con `estado=VALIDADA`.
- **Errores posibles:** `400`, `404`.

### PATCH `/api/partidas/{id}/validar`
- **Descripcion:** validar una partida ya jugada (flujo legacy/alternativo).
- **Body JSON:** no aplica.
- **Respuesta simplificada:** `PartidaResponse`.
- **Errores posibles:** `400`, `404`, `409`.

---

## 7) Resumen / Dashboard

### GET `/api/torneos/{id}/resumen`
- **Descripcion:** KPIs globales del torneo para dashboard.
- **Body JSON:** no aplica.
- **Respuesta simplificada:**
```json
{
  "torneoId": 1,
  "nombreTorneo": "Open Primavera",
  "estado": "EN_CURSO",
  "totalInscritos": 12,
  "plazasDisponibles": 4,
  "totalRondas": 3,
  "rondasFinalizadas": 2,
  "totalPartidas": 18,
  "partidasValidadas": 15,
  "porcentajeOcupacion": 75.0,
  "porcentajePartidasValidadas": 83.33,
  "mediaPuntosPorJugador": 4.50,
  "totalVictorias": 15,
  "totalEmpates": 6,
  "totalDerrotas": 15,
  "jugadoresConBye": 1,
  "torneoFinalizado": false,
  "liderId": 5,
  "liderNombre": "Ana",
  "liderPuntos": 7
}
```
- **Errores posibles:** `404`.

---

## 8) Enfrentamientos

### GET `/api/torneos/{torneoId}/enfrentamientos?usuario1Id={u1}&usuario2Id={u2}`
- **Descripcion:** listar historial de partidas entre dos usuarios en el torneo (sin importar orden).
- **Body JSON:** no aplica.
- **Respuesta simplificada:** arreglo de `PartidaResponse`.
- **Errores posibles:** `400`, `404`.

### GET `/api/torneos/{torneoId}/enfrentamientos/existe?usuario1Id={u1}&usuario2Id={u2}`
- **Descripcion:** indicar si ya se enfrentaron y cuantas veces.
- **Body JSON:** no aplica.
- **Respuesta simplificada:**
```json
{
  "torneoId": 1,
  "usuario1Id": 3,
  "usuario2Id": 9,
  "existe": true,
  "totalEnfrentamientos": 2
}
```
- **Errores posibles:** `400`, `404`.

---

## Endpoints de prueba

No se detectaron endpoints de prueba en `src/test/java` para controladores.

---

## Convencion DTO (migracion gradual)

Todo DTO nuevo debe ir en `com.jorge.gestorTorneos.dto.request` o `com.jorge.gestorTorneos.dto.response`.  
El paquete `model.dto` se mantiene temporalmente por compatibilidad y se migrara gradualmente.

