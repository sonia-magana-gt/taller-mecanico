package com.taller.modelo;

import java.math.BigDecimal;

public class DetalleRepuesto extends DetalleOrden {

    private Repuesto repuesto;
    private int cantidad;
    private BigDecimal precioUnitario;

    public DetalleRepuesto(Repuesto repuesto, int cantidad, BigDecimal precioUnitario) {
        this.repuesto = repuesto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Repuesto getRepuesto() {
        return repuesto;
    }

    public void setRepuesto(Repuesto repuesto) {
        this.repuesto = repuesto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    @Override
    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    @Override
    public String getDescripcion() {
        return repuesto.getNombre() + " x" + cantidad;
    }

    @Override
    public String getTipo() {
        return "Repuesto";
    }
}
