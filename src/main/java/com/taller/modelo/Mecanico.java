package com.taller.modelo;

import com.taller.excepciones.ValidacionException;

public class Mecanico extends Persona {

    private String especialidad;

    public Mecanico(String id, String nombre, String telefono, String especialidad) throws ValidacionException {
        super(id, nombre, telefono);
        setEspecialidad(especialidad);
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad == null ? "" : especialidad.trim();
    }

    @Override
    public String mostrarInformacion() {
        return "Mecánico: " + getNombre() + " | ID: " + getId() + " | Especialidad: " + especialidad;
    }
}
