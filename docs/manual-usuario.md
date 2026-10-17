# Manual de usuario

**Sistema de Gestión de Órdenes de Trabajo – Taller El Último Arranque**

## 1. ¿Para qué sirve?

El sistema permite al taller registrar clientes, vehículos, mecánicos, repuestos y servicios, abrir órdenes de trabajo, controlar el inventario de repuestos, emitir el comprobante de reparación y consultar reportes.

## 2. Antes de empezar

1. Oracle Database XE debe estar encendido (servicios `OracleServiceXE` y `OracleOraDB21Home1TNSListener` en ejecución).
2. Las tablas deben estar creadas (ver el manual técnico).
3. Abrir el programa ejecutando `Main` desde IntelliJ IDEA. Si no hay conexión con la base de datos, el programa muestra un mensaje de error y se cierra.

## 3. Pantalla inicial

Al abrir el programa aparece una ventana con el dibujo de una llanta y cuatro botones:

| Botón | Qué hace |
|---|---|
| **Catálogos** | Abre el menú de Clientes, Vehículos, Mecánicos, Repuestos y Servicios |
| **Órdenes** | Abre el listado de órdenes de trabajo |
| **Reportes** | Abre el menú de los 5 reportes |
| **Salir** | Cierra el sistema (pide confirmación) |

Todas las pantallas se abren una encima de otra y hay que cerrar la de arriba para volver a la anterior.

## 4. Catálogos

Al pulsar **Catálogos** aparece un menú con los botones **Clientes, Vehículos, Mecánicos, Repuestos, Servicios** y **Volver**.

### 4.1 Listados

Cada catálogo abre un listado con una tabla. Botones de la parte inferior:

| Botón | Función |
|---|---|
| **Nuevo** | Abre el formulario para registrar un elemento nuevo |
| **Editar** | Abre el formulario con el elemento seleccionado. También se puede hacer **doble clic** sobre una fila |
| **Exportar HTML** | Guarda lo que muestra la tabla en un archivo HTML que se abre en el navegador |
| **Cerrar** | Regresa a la pantalla anterior |

Para editar primero se debe seleccionar una fila de la tabla; si no, el sistema avisa.

### 4.2 Filtros

- **Vehículos:** elegir un cliente en la lista y pulsar **Filtrar** para ver solo sus vehículos. Elegir "Todos" para ver todos.
- **Repuestos:** elegir "Agotados" y pulsar **Filtrar** para ver solo los repuestos con cantidad 0.
- **Órdenes:** elegir un estado y pulsar **Filtrar**.

### 4.3 Formulario de registro y edición

- Se abre en una ventana aparte. Al **registrar**, todos los campos están vacíos.
- Al **editar**, los datos aparecen cargados y el identificador (o la placa) no se puede cambiar.
- Botones: **Guardar**, **Eliminar** (solo aparece al editar) y **Cancelar**.
- Si falta un dato obligatorio o hay un error (por ejemplo identificador repetido), aparece un mensaje y el formulario sigue abierto para corregir.
- Al editar un cliente o un mecánico, arriba del formulario se muestra un resumen con sus datos.

### 4.4 Reglas que debe conocer

| Situación | Resultado |
|---|---|
| Registrar un identificador o placa que ya existe | El sistema lo rechaza |
| Eliminar un cliente que tiene vehículos | No se permite |
| Eliminar un vehículo o mecánico que tiene órdenes | No se permite |
| Eliminar un servicio usado en órdenes | No se permite |
| Precio, costo o cantidad negativos | No se permiten |
| Año del vehículo fuera del rango 1950 – año actual + 1 | No se permite |
| Repuestos | No se pueden eliminar |

### 4.5 Repuestos: cantidad y abastecimiento

- La **cantidad inicial** solo se escribe al registrar el repuesto. Al editar, la cantidad aparece bloqueada.
- Para aumentar el inventario, seleccionar el repuesto y pulsar **Abastecer**, escribir la cantidad y aceptar. El sistema guarda el abastecimiento con su fecha.

## 5. Órdenes de trabajo

Al pulsar **Órdenes** aparece el listado de órdenes (la más reciente primero). Botones: **Nueva orden**, **Abrir orden**, **Exportar HTML**, **Cerrar**, y el filtro por estado.

### 5.1 Crear una orden (recepción del vehículo)

1. Pulsar **Nueva orden**.
2. Escribir el identificador del cliente y pulsar **Buscar**.
   - Si el cliente no existe, el sistema pregunta si desea registrarlo; al aceptar pide nombre, teléfono y dirección.
3. Elegir el vehículo en la lista. Si el vehículo no está, pulsar **Registrar vehículo** y llenar los datos.
4. Elegir el mecánico y escribir la descripción del problema.
5. Pulsar **Crear orden**. El sistema muestra el número de orden y la deja en estado **PENDIENTE**.

### 5.2 Estados de una orden

```
PENDIENTE → EN PROCESO → FINALIZADA
     \            \
      → CANCELADA  → CANCELADA
```

### 5.3 Trabajar en una orden

Seleccionar la orden y pulsar **Abrir orden** (o doble clic). La ventana muestra los datos de la orden, los servicios, los repuestos y los totales. Los botones se habilitan según el estado:

| Estado | Qué se puede hacer |
|---|---|
| PENDIENTE | **Iniciar** o **Cancelar orden** |
| EN PROCESO | Agregar y quitar servicios y repuestos, **Finalizar**, **Cancelar orden** |
| FINALIZADA | Solo **Ver comprobante** |
| CANCELADA | Solo consultar |

**Agregar un servicio:** elegirlo en la lista y pulsar **Agregar servicio**. Para quitarlo, seleccionarlo en la tabla y pulsar **Quitar servicio**.

**Agregar un repuesto:** elegir el repuesto, indicar la cantidad y pulsar **Agregar repuesto**.
- Si no hay suficiente existencia, el sistema avisa "Inventario insuficiente" y no cambia nada.
- Si hay existencia, se descuenta del inventario automáticamente.
- Al **quitar** un repuesto, la cantidad regresa al inventario.

**Finalizar:** el sistema muestra un resumen y pide confirmación. Una orden no puede finalizarse si no tiene al menos un servicio. Al terminar se muestra el comprobante.

**Cancelar:** pide confirmación y devuelve al inventario todos los repuestos de la orden.

### 5.4 Comprobante

Muestra cliente, vehículo, mecánico, servicios, repuestos, total de mano de obra, total de repuestos y **total a pagar**. El botón **Guardar como HTML** crea un archivo para abrirlo en el navegador o imprimirlo.

## 6. Reportes

Al pulsar **Reportes** aparece un menú, igual al de Catálogos, con un botón por reporte y el botón **Volver**. Cada botón abre el reporte en su propia pantalla modal.

### 6.1 Cómo usar un reporte

1. Elegir el reporte en el menú.
2. Llenar el parámetro de la parte superior, si lo tiene.
3. Pulsar **Generar**. El resultado aparece en la tabla.
4. Si lo desea, pulsar **Exportar HTML** para guardar lo que muestra la tabla en un archivo que se abre en el navegador.
5. Pulsar **Cerrar** para volver al menú de reportes.

Si se pulsa **Exportar HTML** antes de generar el reporte, el sistema avisa "Primero genere el reporte".

### 6.2 Los reportes

| Botón del menú | Parámetro | Qué muestra |
|---|---|---|
| **Órdenes por estado** | Estado (o "TODAS") | Número, fecha, placa, vehículo, cliente, mecánico, estado y total |
| **Reparaciones por vehículo** | Placa del vehículo (obligatoria) | Historial de órdenes del vehículo |
| **Repuestos con bajo inventario** | Ninguno | Repuestos con existencia de 5 o menos; se genera solo al abrir |
| **Reparaciones por mecánico** | Mecánico (o "Todos") | Órdenes atendidas por cada mecánico |
| **Ingresos por reparaciones** | Fecha inicial y final en formato `AAAA-MM-DD` | Órdenes finalizadas en el rango, con mano de obra, repuestos y una fila final con el **total general** |

## 7. Mensajes frecuentes

| Mensaje | Qué hacer |
|---|---|
| "No se pudo conectar a Oracle" | Encender Oracle y revisar `db.properties` |
| "Seleccione un registro de la tabla" | Hacer clic en una fila antes de pulsar Editar |
| "Ya existe un ... con ese identificador" | Usar otro identificador |
| "Inventario insuficiente. Disponible: N" | Pedir una cantidad menor o abastecer el repuesto |
| "No se puede finalizar una orden sin servicios registrados" | Agregar al menos un servicio |
| "Formato de fecha inválido" | Escribir la fecha como `2026-10-17` |
| "Primero genere el reporte" | Pulsar **Generar** antes de exportar |
