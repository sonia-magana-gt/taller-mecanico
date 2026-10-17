INSERT INTO cliente VALUES ('1001', 'Juan Pérez', '5555-1111', 'Zona 1, Ciudad de Guatemala');
INSERT INTO cliente VALUES ('1002', 'María López', '5555-2222', 'Zona 10, Ciudad de Guatemala');
INSERT INTO cliente VALUES ('1003', 'Carlos Ramírez', '5555-3333', 'Mixco, Guatemala');

INSERT INTO mecanico VALUES ('M01', 'Pedro Gómez', '4444-1111', 'Motor');
INSERT INTO mecanico VALUES ('M02', 'Luis Hernández', '4444-2222', 'Frenos y suspensión');
INSERT INTO mecanico VALUES ('M03', 'Ana Morales', '4444-3333', 'Electricidad');

INSERT INTO vehiculo VALUES ('P123ABC', 'Toyota', 'Corolla', 2018, 'Blanco', '1001');
INSERT INTO vehiculo VALUES ('P456DEF', 'Honda', 'Civic', 2020, 'Rojo', '1001');
INSERT INTO vehiculo VALUES ('P789GHI', 'Nissan', 'Sentra', 2015, 'Gris', '1002');
INSERT INTO vehiculo VALUES ('P321JKL', 'Mazda', 'CX-5', 2022, 'Negro', '1003');

INSERT INTO repuesto VALUES ('R01', 'Filtro de aceite', 'Filtro universal', 45, 20);
INSERT INTO repuesto VALUES ('R02', 'Aceite 10W-40', 'Galón de aceite', 180, 15);
INSERT INTO repuesto VALUES ('R03', 'Pastillas de freno', 'Juego delantero', 250, 4);
INSERT INTO repuesto VALUES ('R04', 'Bujía', 'Bujía de platino', 35, 3);
INSERT INTO repuesto VALUES ('R05', 'Batería', 'Batería 12V', 850, 0);
INSERT INTO repuesto VALUES ('R06', 'Faja de distribución', 'Faja reforzada', 300, 8);

INSERT INTO servicio VALUES ('S01', 'Cambio de aceite', 100);
INSERT INTO servicio VALUES ('S02', 'Revisión de frenos', 150);
INSERT INTO servicio VALUES ('S03', 'Alineación', 125);
INSERT INTO servicio VALUES ('S04', 'Reparación de motor', 1500);

INSERT INTO orden_trabajo (placa, id_mecanico, descripcion_problema, estado, total)
VALUES ('P789GHI', 'M01', 'El motor hace ruido al encender', 'PENDIENTE', 0);

INSERT INTO orden_trabajo (placa, id_mecanico, descripcion_problema, estado, fecha_finalizacion, total)
VALUES ('P123ABC', 'M02', 'Ruido al frenar', 'FINALIZADA', SYSDATE, 650);
INSERT INTO orden_servicio (id_orden, id_servicio, costo)
VALUES ((SELECT MAX(id_orden) FROM orden_trabajo), 'S02', 150);
INSERT INTO orden_repuesto (id_orden, id_repuesto, cantidad, precio_unitario, subtotal)
VALUES ((SELECT MAX(id_orden) FROM orden_trabajo), 'R03', 2, 250, 500);

COMMIT;
