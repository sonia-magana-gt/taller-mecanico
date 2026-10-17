CREATE TABLE cliente (
    id_cliente   VARCHAR2(20)  PRIMARY KEY,
    nombre       VARCHAR2(100) NOT NULL,
    telefono     VARCHAR2(20),
    direccion    VARCHAR2(200)
);

CREATE TABLE mecanico (
    id_mecanico  VARCHAR2(20)  PRIMARY KEY,
    nombre       VARCHAR2(100) NOT NULL,
    telefono     VARCHAR2(20),
    especialidad VARCHAR2(100)
);

CREATE TABLE vehiculo (
    placa        VARCHAR2(15)  PRIMARY KEY,
    marca        VARCHAR2(50)  NOT NULL,
    modelo       VARCHAR2(50)  NOT NULL,
    anio         NUMBER(4),
    color        VARCHAR2(30),
    id_cliente   VARCHAR2(20)  NOT NULL,
    CONSTRAINT fk_vehiculo_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);

CREATE TABLE repuesto (
    id_repuesto  VARCHAR2(20)  PRIMARY KEY,
    nombre       VARCHAR2(100) NOT NULL,
    descripcion  VARCHAR2(200),
    precio       NUMBER(10,2)  NOT NULL CHECK (precio >= 0),
    cantidad     NUMBER(8)     NOT NULL CHECK (cantidad >= 0)
);

CREATE TABLE abastecimiento (
    id_abastecimiento NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_repuesto  VARCHAR2(20)  NOT NULL,
    cantidad     NUMBER(8)     NOT NULL CHECK (cantidad > 0),
    fecha        DATE          DEFAULT SYSDATE NOT NULL,
    CONSTRAINT fk_abast_repuesto FOREIGN KEY (id_repuesto) REFERENCES repuesto(id_repuesto)
);

CREATE TABLE servicio (
    id_servicio      VARCHAR2(20)  PRIMARY KEY,
    descripcion      VARCHAR2(100) NOT NULL,
    costo_mano_obra  NUMBER(10,2)  NOT NULL CHECK (costo_mano_obra >= 0)
);

CREATE TABLE orden_trabajo (
    id_orden             NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    placa                VARCHAR2(15)  NOT NULL,
    id_mecanico          VARCHAR2(20)  NOT NULL,
    descripcion_problema VARCHAR2(500) NOT NULL,
    estado               VARCHAR2(15)  DEFAULT 'PENDIENTE' NOT NULL
                         CHECK (estado IN ('PENDIENTE','EN_PROCESO','FINALIZADA','CANCELADA')),
    fecha_creacion       DATE          DEFAULT SYSDATE NOT NULL,
    fecha_finalizacion   DATE,
    total                NUMBER(12,2)  DEFAULT 0 NOT NULL,
    CONSTRAINT fk_orden_vehiculo FOREIGN KEY (placa) REFERENCES vehiculo(placa),
    CONSTRAINT fk_orden_mecanico FOREIGN KEY (id_mecanico) REFERENCES mecanico(id_mecanico)
);

CREATE TABLE orden_servicio (
    id_detalle   NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_orden     NUMBER        NOT NULL,
    id_servicio  VARCHAR2(20)  NOT NULL,
    costo        NUMBER(10,2)  NOT NULL CHECK (costo >= 0),
    CONSTRAINT fk_os_orden    FOREIGN KEY (id_orden) REFERENCES orden_trabajo(id_orden),
    CONSTRAINT fk_os_servicio FOREIGN KEY (id_servicio) REFERENCES servicio(id_servicio)
);

CREATE TABLE orden_repuesto (
    id_detalle      NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_orden        NUMBER        NOT NULL,
    id_repuesto     VARCHAR2(20)  NOT NULL,
    cantidad        NUMBER(8)     NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMBER(10,2)  NOT NULL CHECK (precio_unitario >= 0),
    subtotal        NUMBER(12,2)  NOT NULL CHECK (subtotal >= 0),
    CONSTRAINT fk_or_orden    FOREIGN KEY (id_orden) REFERENCES orden_trabajo(id_orden),
    CONSTRAINT fk_or_repuesto FOREIGN KEY (id_repuesto) REFERENCES repuesto(id_repuesto)
);
