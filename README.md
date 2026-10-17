# Taller El Último Arranque

Sistema de gestión de órdenes de trabajo para un taller mecánico. Java + Swing + Oracle Database (JDBC).

## Integrantes
- Sonia
- Brian
- Mynor
- Alan
- Charlie

## Requisitos
- JDK 21 o superior
- Oracle Database 21c Express Edition (servicio XEPDB1)
- IntelliJ IDEA

## Base de datos
1. Ejecutar `sql/00_crear_usuario.sql` como administrador.
2. Ejecutar `sql/01_crear_tablas.sql` y `sql/02_datos_prueba.sql` con el usuario `taller` (clave `taller123`).
3. `sql/99_borrar_tablas.sql` reinicia las tablas.

## Ejecución
Abrir la carpeta en IntelliJ, esperar a que Maven descargue las dependencias y ejecutar `com.taller.Main`.
