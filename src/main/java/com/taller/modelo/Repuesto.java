package com.taller.modelo;

import com.taller.excepciones.ValidacionException;

import java.math.BigDecimal;

public class Repuesto {

    private String id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int cantidad;

    public Repuesto(String id, String nombre, String descripcion, BigDecimal precio, int cantidad)
            throws ValidacionException {
        setId(id);
        setNombre(nombre);
        setDescripcion(descripcion);
        setPrecio(precio);
        setCantidad(cantidad);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidacionException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidacionException("El identificador del repuesto es obligatorio");
        }
        this.id = id.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) throws ValidacionException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidacionException("El nombre del repuesto es obligatorio");
        }
        this.nombre = nombre.trim();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion == null ? "" : descripcion.trim();
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) throws ValidacionException {
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacionException("El precio no puede ser negativo");
        }
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) throws ValidacionException {
        if (cantidad < 0) {
            throw new ValidacionException("La cantidad no puede ser negativa");
        }
        this.cantidad = cantidad;
    }

    public boolean estaAgotado() {
        return cantidad == 0;
    }

    @Override
    public String toString() {
        return id + " - " + nombre + " (existencia: " + cantidad + ")";
    }
}
