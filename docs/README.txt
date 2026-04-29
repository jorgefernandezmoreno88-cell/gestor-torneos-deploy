#  Gestor de Torneos

Aplicación web para la gestión de torneos de juegos (boardgames, rol, etc.), que permite crear torneos, gestionar inscripciones, generar rondas y registrar resultados.

---

##  Tecnologías

### Backend

* Java + Spring Boot
* JPA / Hibernate
* MySQL

### Frontend

* Astro
* Bootstrap

---

##  Estructura del proyecto

```
gestor-torneos/
├── backend/        # API REST (Spring Boot)
├── frontend/       # Interfaz web (Astro)
├── db/             # Scripts de base de datos
├── docs/           # Documentación del proyecto
└── README.md
```

---

## Cómo ejecutar el proyecto

### 1. Clonar repositorio

```
git clone https://github.com/TU-USUARIO/gestor-torneos.git
cd gestor-torneos
```

---

### 2. Backend (Spring Boot)

* Abrir en Spring Tools / Eclipse
* Importar como proyecto Maven
* Ejecutar la aplicación (`Run as Spring Boot App`)

---

### 3. Frontend (Astro)

```
cd frontend
npm install
npm run dev
```

 Acceder en: http://localhost:4321

---

### 4. Base de datos

* Crear base de datos en MySQL
* Importar archivo:

```
/db/schema.sql
```

---

## 🔐 Configuración

Crear archivo `.env` en el backend con:

```
DB_URL=jdbc:mysql://localhost:3306/gestor_torneos
DB_USER=root
DB_PASS=
```

---

## 📌 Funcionalidades principales

* Registro e inicio de sesión de usuarios
* Creación y gestión de torneos
* Inscripción de jugadores
* Generación de rondas
* Registro de resultados
* Clasificación automática

---

## 📈 Posibles mejoras

* Autenticación JWT
* Dashboard con gráficos (Chart.js)
* Búsqueda semántica con IA
* Generación de PDFs con estadísticas

---

## 👤 Autor

Jorge Fernández
