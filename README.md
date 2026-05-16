
<h1 align="center">🌿 GreenHarmony — Sistema de Gestión de Vivero</h1>

<p align="center">
  <a href="#español">🇨🇷 Español</a> &nbsp;|&nbsp; <a href="#english">🇺🇸 English</a>
</p>

---

<a name="español"></a>

## 🇨🇷 Español

### Descripción

**GreenHarmony** es una aplicación web de gestión integral para un vivero, desarrollada con **Spring Boot** y **Thymeleaf**. El sistema permite administrar todos los aspectos operativos del negocio desde un panel centralizado, con roles diferenciados para administradores y clientes.

---

### ✨ Funcionalidades principales

#### Panel Administrador
| Módulo | Descripción |
|---|---|
| 🌿 **Plantas** | Catálogo e inventario de plantas con imágenes y precios |
| 🌱 **Cultivos** | Seguimiento del estado de cultivos activos |
| 🧪 **Insumos** | Control de inventario de insumos y materiales |
| 👥 **Usuarios** | Gestión de clientes y administradores del sistema |
| 🛒 **Compras** | Registro y seguimiento de compras realizadas |
| 💰 **Ventas** | Historial completo de ventas |
| 📅 **Visitas** | Agenda de visitas con validación de disponibilidad por horario |
| 🚚 **Entregas** | Control y seguimiento de entregas |
| 📋 **Quejas y Sugerencias** | Gestión de retroalimentación de clientes |
| 🏷️ **Promociones** | Administración de descuentos y ofertas activas |
| 📦 **Empaques** | Control de stock de empaques |
| 🗺️ **Zonas** | Gestión de zonas internas del vivero |
| ✅ **Tareas** | Seguimiento de tareas del personal |

#### Portal Cliente
- 📅 Solicitar visitas al vivero (con estado Pendiente/Confirmada/Cancelada)
- 🗓️ Consultar el historial y estado de sus visitas
- 🌿 Explorar el catálogo de plantas disponibles
- 🏷️ Ver promociones activas
- 📋 Enviar quejas y sugerencias

---

### 🛠️ Tecnologías utilizadas

- **Java 17+** — Lenguaje principal
- **Spring Boot** — Framework backend (MVC, Data JPA, Security)
- **Thymeleaf** — Motor de plantillas HTML
- **Spring Data JPA / Hibernate** — Persistencia de datos
- **MySQL** — Base de datos relacional
- **CSS personalizado** — Diseño responsivo propio (sin frameworks externos)
- **JavaScript vanilla** — Interactividad en el cliente

---

### 🏗️ Arquitectura

```
vivero/
├── controller/       # Controladores Spring MVC (uno por módulo)
├── domain/           # Entidades JPA (Visita, Usuario, Planta, etc.)
├── repository/       # Interfaces JpaRepository
├── services/         # Lógica de negocio + interfaz CRUD genérica
└── resources/
    ├── static/
    │   ├── css/      # Hoja de estilos global
    │   ├── js/       # Scripts del cliente
    │   └── images/   # Recursos gráficos
    └── templates/
        ├── fragmentos.html   # Sidebars reutilizables (admin / cliente)
        ├── visitas/
        ├── plantas/
        └── ...               # Una carpeta por módulo
```

---

### ⚙️ Instalación y configuración

#### Requisitos previos
- Java 17 o superior
- Maven 3.8+
- MySQL 8+

#### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/greenharmony.git
cd greenharmony

# 2. Crear la base de datos
mysql -u root -p -e "CREATE DATABASE vivero;"

# 3. Configurar credenciales en application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/vivero
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update

# 4. Compilar y ejecutar
mvn spring-boot:run
```

#### Acceso
Abrí el navegador en `http://localhost:8080`

---

### 🔐 Roles del sistema

| Rol | Acceso |
|---|---|
| **Administrador** | Panel completo de gestión de todos los módulos |
| **Cliente** | Portal reducido: visitas, catálogo, promociones y quejas |

---

### 📸 Capturas de pantalla

> _Agregá aquí imágenes del panel admin, portal cliente y formulario de visitas._

---

<a name="english"></a>

## 🇺🇸 English

### Description

**GreenHarmony** is a full-featured web management system for a plant nursery, built with **Spring Boot** and **Thymeleaf**. It provides a centralized dashboard to manage all business operations, with separate roles for administrators and customers.

---

### ✨ Key Features

#### Admin Panel
| Module | Description |
|---|---|
| 🌿 **Plants** | Plant catalog and inventory with images and pricing |
| 🌱 **Crops** | Active crop tracking and status management |
| 🧪 **Supplies** | Supply and materials inventory control |
| 👥 **Users** | Customer and administrator account management |
| 🛒 **Purchases** | Purchase registration and tracking |
| 💰 **Sales** | Complete sales history |
| 📅 **Visits** | Visit scheduling with time-slot availability validation |
| 🚚 **Deliveries** | Delivery control and tracking |
| 📋 **Complaints & Suggestions** | Customer feedback management |
| 🏷️ **Promotions** | Active discounts and offers management |
| 📦 **Packaging** | Packaging stock control |
| 🗺️ **Zones** | Nursery internal zones management |
| ✅ **Tasks** | Staff task tracking |

#### Customer Portal
- 📅 Request nursery visits (with Pending/Confirmed/Cancelled status)
- 🗓️ View visit history and current status
- 🌿 Browse available plant catalog
- 🏷️ View active promotions
- 📋 Submit complaints and suggestions

---

### 🛠️ Tech Stack

- **Java 17+** — Primary language
- **Spring Boot** — Backend framework (MVC, Data JPA, Security)
- **Thymeleaf** — HTML template engine
- **Spring Data JPA / Hibernate** — Data persistence
- **MySQL** — Relational database
- **Custom CSS** — Responsive design without external frameworks
- **Vanilla JavaScript** — Client-side interactivity

---

### 🏗️ Architecture

```
vivero/
├── controller/       # Spring MVC controllers (one per module)
├── domain/           # JPA entities (Visita, Usuario, Planta, etc.)
├── repository/       # JpaRepository interfaces
├── services/         # Business logic + generic CRUD interface
└── resources/
    ├── static/
    │   ├── css/      # Global stylesheet
    │   ├── js/       # Client-side scripts
    │   └── images/   # Graphic assets
    └── templates/
        ├── fragmentos.html   # Reusable sidebars (admin / client)
        ├── visitas/
        ├── plantas/
        └── ...               # One folder per module
```

---

### ⚙️ Setup & Installation

#### Prerequisites
- Java 17 or higher
- Maven 3.8+
- MySQL 8+

#### Steps

```bash
# 1. Clone the repository
git clone https://github.com/your-username/greenharmony.git
cd greenharmony

# 2. Create the database
mysql -u root -p -e "CREATE DATABASE vivero;"

# 3. Set credentials in application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/vivero
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

# 4. Build and run
mvn spring-boot:run
```

#### Access
Open your browser at `http://localhost:8080`

---

### 🔐 System Roles

| Role | Access |
|---|---|
| **Administrator** | Full management panel across all modules |
| **Customer** | Reduced portal: visits, catalog, promotions, and complaints |

---

### 📸 Screenshots

> _Add screenshots of the admin panel, customer portal, and visit form here._

---

<p align="center">
  Desarrollado con 🌱 para el vivero GreenHarmony &nbsp;·&nbsp; Built with 🌱 for GreenHarmony Nursery
</p>
