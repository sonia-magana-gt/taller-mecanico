package com.taller.modelo;

public enum EstadoOrden {
    PENDIENTE("PENDIENTE"),
    EN_PROCESO("EN PROCESO"),
    FINALIZADA("FINALIZADA"),
    CANCELADA("CANCELADA");

    private final String texto;

    EstadoOrden(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    @Override
    public String toString() {
        return texto;
    }
}
