# 📅 Api Mes A Mes

## 📜 Descripción
La API "Mes A Mes" permite a los usuarios llevar un **seguimiento detallado de su ciclo menstrual**.

Además, ofrece funcionalidades como:
+ Registro y gestión de usuarios.
+ Seguimiento diario del ciclo. 
+ Listado de síntomas y estados emocionales predefinidos.
+ Configuración de preferencias y recordatorios.

### 🛠️ **Tecnologías Utilizadas**
- **Backend:** Kotlin, Spring Boot
- **Base de Datos:** MongoDB
- **Autenticación:** JWT

---

## 📂 **Estructura de la Base de Datos**

### 🏷️ **Usuarios**
| Campo               | Tipo          | Descripción                                         |
|---------------------|---------------|-----------------------------------------------------|
| `id`                | String        | Identificador único, correo electróico del usuario. |
| `name`              | String        | Nombre del usuario                                  |
| `username`          | String        | Nombre de usuario                                   |
| `password`          | String (hash) | Contraseña encriptada                               |
| `registration_date` | Date          | Fecha en la que se registró                         |

### 📆 **SeguimientoDiario**
| Campo              | Tipo   | Descripción                                            |
|--------------------|--------|--------------------------------------------------------|
| `id`               | Int    | ID único del seguimiento                               |
| `user`             | User   | Usuario que realiza el seguimiento                     |
| `date`             | Date   | Fecha del seguimiento                                  |
| `cycle_phase`      | String | Fase del ciclo (Menstruación, Ovulación, etc.)         |
| `menstrual_flow`   | String | Intensidad del flujo                                   |
| `pysical_activity` | String | Actividad física realizada                             |
| `sexual_activity`  | String | Actividad física realizada                             |
| `symptoms`         | JSON   | Lista de síntomas registrados                          |
| `feelings`         | JSON   | Lista de estados emocionales                           |
| `notes`            | String | Notas opcionales                                       |

//sujeto a cambios
### 💠 **Otras Tablas Relacionadas**
- `fases_ciclo`: Contiene las distintas fases del ciclo.
- `sintomas`: Lista de síntomas predefinidos.
- `sentimientos`: Lista de estados emocionales predefinidos.
- `flujo_menstrual`: Tipos de flujo (Leve, Moderado, Abundante, etc.).

---

## 📡 **Endpoints de la API**

### **👤 Usuarios**
| Método   | Endpoint                   | Descripción                                     |
|----------|----------------------------|-------------------------------------------------|
| `GET`    | `/api/list-users/{email}`  | Obtiene un usuario por su ID.                   |
| `GET`    | `/api/list-users`          | Obtiene todos los usuarios de la base de datos. |
| `POST`   | `/api/register`            | Registra un nuevo usuario.                      |
| `POST`   | `/api/login`               | Registra un nuevo usuario.                      |
| `PUT`    | `/api/user-update`         | Actualiza la información de un usuario.         |
| `DELETE` | `/api/user-delete/{email}` | Elimina un usuario.                             |

---

### **📅 Seguimiento Diario**
| Método   | Endpoint                   | Descripción                           |
|----------|----------------------------|---------------------------------------|
| `GET`    | `/api/follow-ups/{userId}` | Obtiene el historial de seguimiento.  |
| `POST`   | `/api/folow-ups`           | Registra un nuevo seguimiento diario. |
| `DELETE` | `/api/follow-ups/{id}`     | Elimina un registro de seguimiento.   |

### **🔄 Fases del Ciclo**
| Método  | Endpoint            | Descripción                          |
|---------|---------------------|--------------------------------------|
| `GET`   | `/api/cycle-phases` | Lista las fases del ciclo menstrual. |

### **🤕 Síntomas**
| Método | Endpoint        | Descripción                   |
|--------|-----------------|-------------------------------|
| `GET`  | `/api/symptoms` | Obtiene la lista de síntomas. |

### **😊 Estados Emocionales**
| Método | Endpoint                | Descripción                    |
|--------|-------------------------|--------------------------------|
| `GET`  | `/api/emotional-states` | Obtiene la lista de emociones. |

### **🔔 Notificaciones**
| Método | Endpoint                         | Descripción                            |
|--------|----------------------------------|----------------------------------------|
| `GET`  | `/api/notifications/{usuarioId}` | Obtiene los recordatorios del usuario. |

---

## 🚨 **Manejo de Errores**
| Código | Mensaje                 | Descripción                                 |
|--------|-------------------------|---------------------------------------------|
| `400`  | `Bad Request`           | Datos inválidos en la solicitud.            |
| `401`  | `Unauthorized`          | Token de autenticación inválido o faltante. |
| `403`  | `Forbidden`             | No tienes permisos para esta acción.        |
| `404`  | `Not Found`             | Recurso no encontrado.                      |
| `500`  | `Internal Server Error` | Error interno del servidor.                 |

---

## 🔐 **Seguridad y Autenticación**
- Se usa **JWT (JSON Web Token)** para la autenticación.
- Cada solicitud protegida requiere un **Bearer Token** en los headers.
- Se utiliza **BCrypt** para almacenar contraseñas en la base de datos.

---

## 🔄 **Lógica de Negocio**
1. **Registro de Usuarios:**
    - Un usuario se registra con nombre, correo y contraseña encriptada.
2. **Seguimiento Diario:**
    - Cada día, el usuario puede registrar síntomas, emociones y otros datos.
3. **Cálculo de Fase del Ciclo:**
    - La API puede predecir la fase en función de los datos previos.
4. **Notificaciones:**
    - Se pueden configurar recordatorios para días clave del ciclo.

---