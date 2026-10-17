package com.taller.modelo;

import com.taller.excepciones.ValidacionException;

public class Cliente extends Persona {

    private String direccion;

    public Cliente(String id, String nombre, String telefono, String direccion) throws ValidacionException {
        super(id, nombre, telefono);
        setDireccion(direccion);
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion == null ? "" : direccion.trim();
    }

    @Override
    public String mostrarInformacion() {
        return "Cliente: " + getNombre() + " | ID: " + getId() + " | Tel: " + getTelefono()
                + " | Dirección: " + direccion;
    }
}
