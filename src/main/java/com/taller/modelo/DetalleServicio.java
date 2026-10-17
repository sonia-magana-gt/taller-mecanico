package com.taller.modelo;

import java.math.BigDecimal;

public class DetalleServicio extends DetalleOrden {

    private Servicio servicio;
    private BigDecimal costo;

    public DetalleServicio(Servicio servicio, BigDecimal costo) {
        this.servicio = servicio;
        this.costo = costo;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    @Override
    public BigDecimal getSubtotal() {
        return costo;
    }

    @Override
    public String getDescripcion() {
        return servicio.getDescripcion();
    }

    @Override
    public String getTipo() {
        return "Servicio";
    }
}
