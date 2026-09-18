# PageTurner 2.0

Aplicación de escritorio para la gestión de una librería académica: catálogo de libros, registro de clientes, ventas y reservas. Construida en Java 21 + JavaFX 21 con persistencia en archivos JSON.

Nació de un examen universitario (T1 de Técnicas de Programación Orientada a Objetos, UPN) donde modelé el dominio en UML. En vez de dejarlo en el papel, lo llevé a una aplicación funcional como ejercicio de arquitectura en capas y principios SOLID.

---

## Capturas

> Pendiente — agrega las imágenes en `docs/` y reemplaza estos marcadores.

| Dashboard | Catálogo de libros |
|---|---|
| ![Dashboard](docs/dashboard.png) | ![Libros](docs/libros.png) |

| Ventas | Reservas |
|---|---|
| ![Ventas](docs/ventas.png) | ![Reservas](docs/reservas.png) |

---

## Stack

| Tecnología | Versión | Rol |
|---|---|---|
| Java | 21 | Lenguaje principal |
| JavaFX | 21.0.6 | UI declarativa (FXML + CSS) |
| Maven | wrapper incluido | Build y gestión de dependencias |
| Jackson Databind | 2.18.2 | Serialización / deserialización JSON |
| Jackson JSR310 | 2.18.2 | Soporte para `LocalDate` en JSON |
| IntelliJ IDEA | — | IDE de desarrollo |

Sin base de datos. Los datos se persisten en archivos `.json` en el directorio home del usuario.

---

## Arquitectura

La aplicación sigue una arquitectura en capas estricta. Cada capa solo conoce a la capa inmediatamente inferior, nunca salta niveles.

```
┌─────────────────────────────────────────┐
│              UI (JavaFX)                │  Controllers + FXML
│  MainController, LibroController, ...   │
└─────────────────┬───────────────────────┘
                  │  llama métodos de servicio
┌─────────────────▼───────────────────────┐
│             Service Layer               │  Reglas de negocio
│  LibroService, VentaService, ...        │
└─────────────────┬───────────────────────┘
                  │  usa interfaces (contratos)
┌─────────────────▼───────────────────────┐
│          Repository (interfaz)          │  Contrato de acceso a datos
│  LibroRepository, ClienteRepository ... │
└─────────────────┬───────────────────────┘
                  │  implementado por
┌─────────────────▼───────────────────────┐
│       Repository JSON (impl.)           │  Lectura/escritura de archivos
│  LibroRepositoryJson, ...               │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│          Archivos JSON                  │  ~/PageTurner2/*.json
└─────────────────────────────────────────┘

        ← — — — — — — — — — — — →
                  Model
       (Libro, Cliente, Venta, Reserva)
          Transversal a todas las capas
```

### Flujo de una venta (ejemplo end-to-end)

1. El usuario llena el formulario en `VentaController` y pulsa "Registrar".
2. El controller llama `ventaService.vender(isbn, cliente, cantidad)`.
3. `VentaService` busca el libro vía `libroRepository.buscarPorIsbn(isbn)`.
4. Si el libro existe y el stock alcanza, llama `libro.descontarStock(cantidad)`.
5. Persiste el libro actualizado: `libroRepository.guardar(libro)`.
6. Crea el objeto `Venta` y lo persiste: `ventaRepository.guardar(venta)`.
7. Si cualquier paso falla, se lanza una excepción que viaja hasta el controller, que la muestra con `e.getMessage()` en una etiqueta de error.

---

## Estructura del proyecto

```
PageTurner2/
├── src/
│   └── main/
│       ├── java/
│       │   ├── module-info.java                  ← declaración del módulo JPMS
│       │   └── com/jhonas/pageturner2/
│       │       ├── PageTurnerApp.java             ← punto de entrada, wiring de dependencias
│       │       ├── Launcher.java                  ← wrapper para jpackage
│       │       ├── model/
│       │       │   ├── Libro.java
│       │       │   ├── Cliente.java
│       │       │   ├── Venta.java
│       │       │   └── Reserva.java
│       │       ├── repository/
│       │       │   ├── LibroRepository.java       ← interfaz (contrato)
│       │       │   ├── ClienteRepository.java
│       │       │   ├── VentaRepository.java
│       │       │   ├── ReservaRepository.java
│       │       │   ├── LibroRepositoryJson.java   ← implementación concreta
│       │       │   ├── ClienteRepositoryJson.java
│       │       │   ├── VentaRepositoryJson.java
│       │       │   └── ReservaRepositoryJson.java
│       │       ├── service/
│       │       │   ├── LibroService.java
│       │       │   ├── ClienteService.java
│       │       │   ├── VentaService.java
│       │       │   └── ReservaService.java
│       │       └── ui/
│       │           ├── MainController.java        ← navegación entre pantallas
│       │           ├── DashboardController.java
│       │           ├── LibroController.java
│       │           ├── ClienteController.java
│       │           ├── VentaController.java
│       │           └── ReservaController.java
│       └── resources/
│           └── com/jhonas/pageturner2/
│               ├── view/
│               │   ├── main-view.fxml
│               │   ├── dashboard-view.fxml
│               │   ├── libro-view.fxml
│               │   ├── cliente-view.fxml
│               │   ├── venta-view.fxml
│               │   └── reserva-view.fxml
│               └── css/
│                   └── styles.css
├── data/                                          ← datos de prueba (opcional)
├── dist/                                          ← salida de jpackage (gitignored)
├── pom.xml
└── README.md
```

---

## Modelo de dominio

### `Libro`
- Campos: `titulo`, `autor`, `isbn` (clave de negocio), `precio`, `stock`.
- Lógica propia: `descontarStock(int)` y `aumentarStock(int)`. Ambos validan la cantidad y lanzan `IllegalArgumentException` si es inválida. Esto garantiza que el stock nunca quede en un estado inconsistente, independientemente de quién llame al método.

### `Cliente`
- Campos identificativos del cliente (nombre, email, etc.).
- CRUD completo: se pueden crear, leer, actualizar y eliminar clientes.

### `Venta`
- Apunta a un `Libro` y a un `Cliente`. Incluye fecha (`LocalDate`) y cantidad.
- Registro histórico: una vez creada, no se modifica ni elimina.
- `calcularTotal()` devuelve `precio * cantidad`.

### `Reserva`
- Apunta a un `Libro` y a un `Cliente`. Solo se puede crear cuando el stock del libro es 0.
- Registro histórico igual que `Venta`.

**Nota sobre referencias unidireccionales:** `Venta` apunta a `Libro`, pero `Libro` no tiene una lista de ventas. `Reserva` apunta a `Libro`, pero `Libro` no tiene lista de reservas. Esto sigue la dirección de las flechas del diagrama UML original y evita referencias circulares que causarían `StackOverflowError` al serializar con Jackson.

---

## Reglas de negocio implementadas

| Regla | Dónde se valida | Excepción |
|---|---|---|
| No vender si stock insuficiente | `Libro.descontarStock()` | `IllegalArgumentException` |
| No vender cantidad ≤ 0 | `VentaService.vender()` | `IllegalArgumentException` |
| No vender sin cliente | `VentaService.vender()` | `IllegalArgumentException` |
| No reservar si hay stock disponible | `ReservaService.reservar()` | `IllegalStateException` |
| No reservar sin cliente | `ReservaService.reservar()` | `IllegalArgumentException` |
| No reservar ISBN inexistente | `ReservaService.reservar()` | `IllegalArgumentException` |
| Stock y venta se persisten en orden atómico | `VentaService.vender()` | — |

La última regla es importante: `VentaService` actualiza el stock del libro **antes** de guardar la venta. Si la actualización del stock fallara, la venta nunca se registraría. El orden no es arbitrario.

---

## Cómo ejecutar en desarrollo

### Requisitos previos
- JDK 21
- Maven (o usar el wrapper `./mvnw` incluido)
- IntelliJ IDEA (recomendado) u otro IDE con soporte para módulos JPMS

### Desde IntelliJ IDEA
1. Abre el proyecto (`File → Open → selecciona la carpeta raíz`).
2. Maven descargará las dependencias automáticamente.
3. Ejecuta la clase `Launcher` (no `PageTurnerApp` directamente, porque `Application.launch()` requiere que la clase no extienda `Application` en el classpath de arranque cuando se usa con ciertos launchers).

### Desde la terminal
```bash
./mvnw javafx:run
```

> Si usas Windows sin bash: `mvnw.cmd javafx:run`

---

## Cómo empaquetar

### Paso 1 — Compilar y copiar dependencias

```bash
mvn clean package
mvn dependency:copy-dependencies -DoutputDirectory=target/libs
```

Estos comandos compilan el proyecto, generan el `.jar` en `target/`, y copian todas las dependencias (JavaFX, Jackson, etc.) en `target/libs/`.

---

### Paso 2a — Instalador `.exe` (requiere WiX Toolset v3.11)

> **Importante:** Usa WiX Toolset **v3.11**, no la v4. Las versiones 4.x cambiaron la interfaz de línea de comandos y `jpackage` no es compatible con ellas todavía.

```bash
jpackage ^
  --type exe ^
  --name PageTurner2 ^
  --dest dist ^
  --module-path "target/classes;target/libs" ^
  --module com.jhonas.pageturner2/com.jhonas.pageturner2.PageTurnerApp ^
  --app-version 1.0 ^
  --vendor "Jhonas" ^
  --win-shortcut ^
  --win-menu ^
  --win-dir-chooser
```

**Qué hace cada flag:**

| Flag | Descripción |
|---|---|
| `--type exe` | Genera un instalador `.exe` con NSIS/WiX |
| `--name PageTurner2` | Nombre del ejecutable y del acceso directo |
| `--dest dist` | Carpeta de salida |
| `--module-path` | Ruta al bytecode compilado y a las dependencias |
| `--module` | Módulo principal y clase de arranque (`módulo/clase`) |
| `--app-version` | Versión que aparece en "Programas y características" |
| `--vendor` | Fabricante que aparece en el instalador |
| `--win-shortcut` | Crea un acceso directo en el escritorio |
| `--win-menu` | Agrega entrada al menú Inicio |
| `--win-dir-chooser` | Permite al usuario elegir el directorio de instalación |

> Tip de depuración: agrega `--win-console` para que la app abra una consola junto a la ventana. Útil para ver stack traces sin tener que revisar logs.

---

### Paso 2b — Versión portable (sin WiX)

Si no tienes WiX instalado o solo quieres una carpeta autocontenida:

```bash
jpackage ^
  --type app-image ^
  --name PageTurner2 ^
  --dest dist ^
  --module-path "target/classes;target/libs" ^
  --module com.jhonas.pageturner2/com.jhonas.pageturner2.PageTurnerApp ^
  --app-version 1.0 ^
  --vendor "Jhonas"
```

`--type app-image` genera una carpeta `dist/PageTurner2/` con el JRE embebido y el ejecutable. Se puede copiar a cualquier máquina Windows sin instalar Java.

---

## Dónde se guardan los datos

Los archivos JSON se guardan en:

```
C:\Users\<tu-usuario>\PageTurner2\
├── libros.json
├── clientes.json
├── ventas.json
└── reservas.json
```

La ruta se construye con `System.getProperty("user.home") + "/PageTurner2/"`. Esto es intencional: si la app se instala en `C:\Program Files\PageTurner2\`, Windows no permite escribir archivos en esa carpeta sin permisos de administrador. Usando el home del usuario, la escritura siempre funciona sin elevar privilegios.

---

## Decisiones de diseño

### 1. Inyección de dependencias manual por constructor

Los servicios no crean sus propios repositorios. Los reciben como parámetros:

```java
// En VentaService:
public VentaService(VentaRepository ventaRepository, LibroRepository libroRepository) {
    this.ventaRepository = ventaRepository;
    this.libroRepository = libroRepository;
}
```

`PageTurnerApp` es el único lugar del programa que instancia las implementaciones concretas (`LibroRepositoryJson`, etc.) y las inyecta:

```java
LibroRepositoryJson libroRepo = new LibroRepositoryJson();

LibroService libroService    = new LibroService(libroRepo);
VentaService ventaService    = new VentaService(new VentaRepositoryJson(), libroRepo);
ReservaService reservaService = new ReservaService(new ReservaRepositoryJson(), libroRepo);
```

**Por qué:** si mañana quisieras cambiar la persistencia a H2, solo cambiarías cuatro líneas en `PageTurnerApp`. El resto del código no sabría que algo cambió porque solo habla con interfaces. También se nota que `libroRepo` se comparte entre `VentaService` y `ReservaService`: ambos necesitan tocar el mismo stock, y con una sola instancia eso está garantizado.

---

### 2. Interfaces como contratos de repositorio

Cada repositorio tiene su interfaz (`LibroRepository`, `ClienteRepository`, etc.) y su implementación (`LibroRepositoryJson`, etc.). Los servicios solo dependen de la interfaz:

```java
// LibroService habla con la interfaz, no con la implementación:
private final LibroRepository libroRepository;
```

**Por qué:** desacopla la lógica de negocio del mecanismo de persistencia. Se podría crear `LibroRepositoryH2` o `LibroRepositoryMock` para tests sin tocar `LibroService`.

---

### 3. Excepciones en lugar de booleanos

Los métodos de validación lanzan excepciones con mensajes descriptivos en lugar de devolver `true`/`false`:

```java
// En Libro.descontarStock():
if (cantidad <= 0 || cantidad > stock) {
    throw new IllegalArgumentException("Cantidad inválida: " + cantidad);
}
```

**Por qué:** el mensaje de error viaja automáticamente desde el modelo hasta la UI con `e.getMessage()`. Si devolviéramos un booleano, la UI no sabría *por qué* falló y no podría mostrar un mensaje útil al usuario. Con excepciones, el mensaje descriptivo está en la capa que tiene el contexto para redactarlo correctamente.

---

### 4. `LocalDate` en lugar de `java.util.Date`

Las fechas de venta y reserva usan `java.time.LocalDate`. Para que Jackson sepa serializarlas, se registra el módulo `JavaTimeModule`:

```java
mapper.registerModule(new JavaTimeModule());
mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
```

**Por qué:** `java.util.Date` es mutable, imprecisa (mezcla fecha y hora en una zona horaria ambigua) y está deprecada en uso moderno. `LocalDate` es inmutable, clara y parte de la API `java.time` introducida en Java 8. `WRITE_DATES_AS_TIMESTAMPS = false` produce fechas legibles (`"2025-09-17"`) en el JSON en lugar de números de época.

---

### 5. `opens` en `module-info.java`

El sistema de módulos de Java (JPMS) bloquea el acceso por reflexión por defecto. Jackson y JavaFX lo necesitan para leer propiedades de las clases:

```java
// Jackson necesita acceder al modelo por reflexión para serializar/deserializar
opens com.jhonas.pageturner2.model to com.fasterxml.jackson.databind, javafx.base;

// JavaFX FXML necesita acceder a los controllers por reflexión para inyectar @FXML
opens com.jhonas.pageturner2.ui to javafx.fxml;
```

**Por qué:** sin estos `opens`, la app arranca pero falla en tiempo de ejecución con `InaccessibleObjectException` al intentar serializar un `Libro` o cargar un FXML. La directiva `opens` es más restrictiva que `exports`: permite reflexión solo al módulo especificado, no lo expone a todo el sistema.

---

## Mejoras pendientes

- [ ] **Migrar persistencia a H2** — reemplazar las cuatro implementaciones JSON por implementaciones JDBC/H2. Gracias a las interfaces de repositorio, el cambio no afecta a los servicios ni a la UI. Solo se necesita crear `LibroRepositoryH2`, etc., y actualizar el wiring en `PageTurnerApp`.

- [ ] **Estilizar los diálogos de confirmación** — los `Alert` de JavaFX heredan el estilo del sistema operativo por defecto. Para aplicarles `styles.css`, habría que inyectar la hoja de estilos al `DialogPane`:
  ```java
  alert.getDialogPane().getStylesheets().add(/* url de styles.css */);
  ```

- [ ] **Agregar ícono a la app empaquetada** — `jpackage` acepta el flag `--icon ruta/icono.ico` (formato `.ico` en Windows). El ícono aparece en el ejecutable, en el acceso directo del escritorio y en la barra de tareas.

- [ ] **Refactorizar repositorios JSON a `JsonStorage<T>` genérico** — los cuatro repositorios JSON (`LibroRepositoryJson`, `ClienteRepositoryJson`, etc.) tienen la misma estructura: leer archivo, deserializar lista, modificar, escribir. Extraer esa lógica a una clase genérica:
  ```java
  class JsonStorage<T> {
      private final File archivo;
      private final TypeReference<List<T>> typeRef;
      // leer(), escribir(), etc.
  }
  ```
  Reduciría el código duplicado significativamente y centralizaría la configuración del `ObjectMapper`.

---

## Contexto académico

Este proyecto surgió del T1 de la asignatura **Técnicas de Programación Orientada a Objetos** de la Universidad Privada del Norte (UPN). El examen pedía modelar un sistema de librería en UML (diagrama de clases con herencia, composición y relaciones de dependencia). La decisión de implementarlo como aplicación funcional fue personal, como ejercicio para aplicar los conceptos de la asignatura en código real.

---

*Jhonas — PageTurner 2.0 — Java 21 + JavaFX 21*
