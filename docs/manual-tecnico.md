# Manual técnico

**Sistema de Gestión de Órdenes de Trabajo – Taller El Último Arranque**

## 1. Tecnologías

| Elemento | Detalle |
|---|---|
| Lenguaje | Java 21 o superior (probado con JDK 25) |
| Interfaz | Swing |
| Base de datos | Oracle Database 21c Express Edition, servicio `XEPDB1` |
| Acceso a datos | JDBC con el driver `ojdbc11` (Maven) |
| IDE | IntelliJ IDEA Community |
| Control de versiones | Git |

## 2. Instalación y puesta en marcha

1. Instalar JDK, IntelliJ IDEA y Oracle XE 21c.
2. Crear el usuario de la aplicación ejecutando `sql/00_crear_usuario.sql` como administrador (usuario `taller`, clave `taller123`).
3. Con el usuario `taller` ejecutar `sql/01_crear_tablas.sql` y `sql/02_datos_prueba.sql`. `sql/99_borrar_tablas.sql` elimina las tablas para empezar de cero.
4. Abrir la carpeta `codigo` en IntelliJ. Maven descarga el driver de Oracle.
5. Ejecutar la clase `com.taller.Main`.

La conexión se configura en `src/main/resources/db.properties` (`db.url`, `db.usuario`, `db.clave`).

## 3. Arquitectura

Tres capas simples:

```
vista (Swing)  →  dao (JDBC)  →  bd.ConexionBD  →  Oracle
        ↘ modelo (clases de datos) ↙
```

| Paquete | Contenido |
|---|---|
| `com.taller` | `Main` |
| `com.taller.bd` | `ConexionBD`: entrega una conexión nueva en cada consulta |
| `com.taller.excepciones` | `ValidacionException` (errores del usuario o reglas de negocio) y `DatosException` (errores de la base de datos) |
| `com.taller.modelo` | Clases de datos con sus validaciones |
| `com.taller.dao` | Una clase por entidad con las consultas SQL |
| `com.taller.util` | `Comprobante` y `ExportadorTabla` |
| `com.taller.vista` | Pantallas |

Convenciones: código en español, sin comentarios, atributos `private`, todas las consultas con `PreparedStatement` y recursos cerrados con `try-with-resources`. El dinero usa `BigDecimal`.

## 4. Modelo de datos

El diagrama de clases UML y el diagrama entidad-relación están en `docs/diagramas.md`.

| Tabla | Descripción |
|---|---|
| `cliente`, `mecanico`, `vehiculo` | Personas y vehículos (el vehículo pertenece a un cliente) |
| `repuesto` | Catálogo con precio y existencia |
| `abastecimiento` | Historial de entradas de inventario |
| `servicio` | Catálogo de servicios con costo de mano de obra |
| `orden_trabajo` | Orden con estado, fechas y total |
| `orden_servicio`, `orden_repuesto` | Detalle de cada orden |

Detalles importantes: los identificadores de órdenes y detalles son columnas de identidad; hay restricciones `CHECK` (cantidades y precios no negativos, estados válidos); `orden_servicio.costo` y `orden_repuesto.precio_unitario` guardan el precio del momento, así que cambiar el catálogo después no altera órdenes anteriores.

## 5. Programación orientada a objetos

| Concepto | Dónde se aplica |
|---|---|
| **Herencia** | `Persona` (abstracta) → `Cliente`, `Mecanico`. `DetalleOrden` (abstracta) → `DetalleServicio`, `DetalleRepuesto`. `ListadoBase` (abstracta) → los 6 listados. `ReporteBase` (abstracta) → los 5 reportes |
| **Polimorfismo** | `mostrarInformacion()` en `Persona` (los formularios de cliente y mecánico lo llaman con una variable de tipo `Persona`). `getSubtotal()` en `DetalleOrden`, usado por `OrdenTrabajo.calcularTotal()` |
| **Encapsulamiento** | Todos los atributos son `private` con getters y setters; los setters validan y lanzan `ValidacionException` |
| **Composición / ArrayList** | `OrdenTrabajo` crea y contiene un `ArrayList<DetalleOrden>` |
| **Enumeración** | `EstadoOrden` |
| **Excepciones propias** | `ValidacionException`, `DatosException` |

## 6. Estructura de la interfaz

```
VentanaPrincipal (botones + IconoLlanta)
 ├── DialogoCatalogos
 │    ├── ListadoClientes   → FormularioCliente
 │    ├── ListadoVehiculos  → FormularioVehiculo
 │    ├── ListadoMecanicos  → FormularioMecanico
 │    ├── ListadoRepuestos  → FormularioRepuesto
 │    └── ListadoServicios  → FormularioServicio
 ├── ListadoOrdenes → DialogoNuevaOrden / DialogoDetalleOrden → Comprobante
 └── DialogoReportes (menú)
      ├── ReporteOrdenesPorEstado
      ├── ReporteReparacionesVehiculo
      ├── ReporteBajoInventario
      ├── ReporteReparacionesMecanico
      └── ReporteIngresos
```

- Todas las ventanas secundarias son `JDialog` modales.
- `ListadoBase` contiene lo común de los listados: tabla, botones **Nuevo**, **Editar**, **Exportar HTML** y **Cerrar**, y doble clic para editar. Cada listado hijo implementa tres métodos: `cargarDatos()`, `nuevo()` y `editar(fila)`, y agrega sus filtros en `panelFiltros`.
- `DialogoReportes` es un menú modal igual a `DialogoCatalogos`: un botón por reporte y **Volver**.
- `ReporteBase` contiene lo común de los reportes: panel de parámetros arriba (`panelFiltros`), tabla en el centro y botones **Generar**, **Exportar HTML** y **Cerrar**. Cada reporte hijo agrega sus parámetros y implementa un solo método, `generar()`, que devuelve el `DefaultTableModel` que pide a `ReporteDAO`. La base captura `ValidacionException`, `DatosException` y `DateTimeParseException` y muestra el mensaje. `ReporteBajoInventario` activa `generarAlAbrir` porque no tiene parámetros.
- Los formularios reciben el objeto a editar o `null` si es un registro nuevo. Con un objeto existente se bloquea el identificador y aparece el botón **Eliminar**.
- `UtilVista` reúne los mensajes (`JOptionPane`), el modelo de tabla no editable y la lectura de números.

## 7. Transacciones y reglas de negocio

`OrdenDAO` ejecuta cada operación que modifica una orden en una transacción:

```
con.setAutoCommit(false);
   verificar estado (SELECT ... FOR UPDATE)
   operaciones SQL
con.commit();           si todo salió bien
con.rollback();         si ocurre cualquier error
```

El método privado `ejecutarTransaccion` concentra este esquema para que `commit` y `rollback` estén en un solo lugar.

| Operación | Comportamiento |
|---|---|
| `agregarRepuesto` | Bloquea la fila del repuesto, valida existencia suficiente, descuenta el inventario, inserta el detalle y recalcula el total |
| `quitarRepuesto` | Devuelve la cantidad al inventario, borra el detalle y recalcula |
| `finalizar` | Solo desde EN_PROCESO y con al menos un servicio; fija la fecha de finalización |
| `cancelar` | Desde PENDIENTE o EN_PROCESO; devuelve los repuestos al inventario |
| `RepuestoDAO.abastecer` | Suma la cantidad e inserta el registro de abastecimiento en una sola transacción |

Cada método valida el estado de la orden en la base de datos, además de los botones deshabilitados de la pantalla.

Otras reglas: no se eliminan clientes con vehículos, vehículos ni mecánicos con órdenes, ni servicios usados en órdenes; los repuestos no se eliminan.

## 8. Reportes y exportación

`ReporteDAO` devuelve un `DefaultTableModel` ya lleno. El método privado `ejecutar(sql, columnas, parametros)` prepara la consulta, asigna los parámetros y arma la tabla.

| Pantalla | Método de `ReporteDAO` | Parámetro |
|---|---|---|
| `ReporteOrdenesPorEstado` | `ordenesPorEstado(EstadoOrden)` | Combo de estado (`null` = todas) |
| `ReporteReparacionesVehiculo` | `reparacionesPorVehiculo(String placa)` | Campo de texto |
| `ReporteBajoInventario` | `repuestosBajoInventario()` | Ninguno |
| `ReporteReparacionesMecanico` | `reparacionesPorMecanico(String id)` | Combo de mecánico (`null` = todos) |
| `ReporteIngresos` | `ingresos(LocalDate, LocalDate)` | Dos fechas `AAAA-MM-DD`; agrega la fila "TOTAL GENERAL" |

`ExportadorTabla.aHtml` genera el archivo HTML (UTF-8) a partir de cualquier `JTable`; lo usan `ListadoBase` y `ReporteBase`. `Comprobante` genera el comprobante en texto y en HTML.

## 9. Archivos del proyecto

```
codigo/
├── pom.xml  .gitignore  README.md
├── docs/            diagramas.md, manual-usuario.md, manual-tecnico.md
├── sql/             00_crear_usuario, 01_crear_tablas, 02_datos_prueba, 99_borrar_tablas
└── src/main/
    ├── resources/db.properties
    └── java/com/taller/  (paquetes descritos arriba)
```

## 10. Reparto de responsabilidades del equipo

| Integrante | Responsabilidad | Archivos |
|---|---|---|
| **Sonia** (integradora) | Infraestructura, integración, pantalla inicial, órdenes (creación) y comprobante. Realiza el commit final | `pom.xml`, `.gitignore`, `README.md`, `Main`, `ConexionBD`, `db.properties`, `ValidacionException`, `DatosException`, `UtilVista`, `VentanaPrincipal`, `IconoLlanta`, `DialogoCatalogos`, `ListadoBase`, `ListadoOrdenes`, `DialogoNuevaOrden`, `Comprobante` |
| **Brian** | Base de datos, reportes, exportación y diagramas | Carpeta `sql/`, `ReporteDAO`, `DialogoReportes`, `ReporteBase`, `ReporteOrdenesPorEstado`, `ReporteReparacionesVehiculo`, `ReporteBajoInventario`, `ReporteReparacionesMecanico`, `ReporteIngresos`, `ExportadorTabla`, `docs/diagramas.md` |
| **Mynor** | Personas y servicios | `Persona`, `Cliente`, `Mecanico`, `Servicio`, `ClienteDAO`, `MecanicoDAO`, `ServicioDAO`, `ListadoClientes`, `FormularioCliente`, `ListadoMecanicos`, `FormularioMecanico`, `ListadoServicios`, `FormularioServicio` |
| **Alan** | Vehículos e inventario | `Vehiculo`, `Repuesto`, `VehiculoDAO`, `RepuestoDAO` (incluye el abastecimiento transaccional), `ListadoVehiculos`, `FormularioVehiculo`, `ListadoRepuestos`, `FormularioRepuesto` |
| **Charlie** | Núcleo de las órdenes de trabajo | `EstadoOrden`, `DetalleOrden`, `DetalleServicio`, `DetalleRepuesto`, `OrdenTrabajo`, `OrdenDAO` (transacciones), `DialogoDetalleOrden` |

Documentación (`manual-usuario.md` y `manual-tecnico.md`): revisada entre todo el equipo.

## 11. Pruebas realizadas

- Conexión a Oracle y consultas de todos los DAO.
- Agregar un repuesto descuenta el inventario; pedir más de la existencia se rechaza sin modificar nada; cancelar la orden devuelve los repuestos; una orden cancelada no admite cambios.
- Abastecimiento actualiza la existencia y el listado de agotados.
- Los cinco reportes se abren como pantallas modales desde el menú de reportes y generan datos con la información de prueba.
- Las pantallas principales abren correctamente y muestran los datos de la base.
