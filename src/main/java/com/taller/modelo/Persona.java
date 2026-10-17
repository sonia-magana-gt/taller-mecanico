package com.taller.modelo;

import com.taller.excepciones.ValidacionException;

public abstract class Persona {

    private String id;
    private String nombre;
    private String telefono;

    public Persona(String id, String nombre, String telefono) throws ValidacionException {
        setId(id);
        setNombre(nombre);
        setTelefono(telefono);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidacionException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidacionException("El identificador es obligatorio");
        }
        this.id = id.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) throws ValidacionException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidacionException("El nombre es obligatorio");
        }
        this.nombre = nombre.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono == null ? "" : telefono.trim();
    }

    public abstract String mostrarInformacion();

    @Override
    public String toString() {
        return id + " - " + nombre;
    }
}
