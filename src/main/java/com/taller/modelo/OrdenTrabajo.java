package com.taller.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

public class OrdenTrabajo {

    private int idOrden;
    private Vehiculo vehiculo;
    private Cliente cliente;
    private Mecanico mecanico;
    private String descripcionProblema;
    private EstadoOrden estado;
    private LocalDate fechaCreacion;
    private LocalDate fechaFinalizacion;
    private BigDecimal total = BigDecimal.ZERO;
    private final ArrayList<DetalleOrden> detalles = new ArrayList<>();

    public int getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Mecanico getMecanico() {
        return mecanico;
    }

    public void setMecanico(Mecanico mecanico) {
        this.mecanico = mecanico;
    }

    public String getDescripcionProblema() {
        return descripcionProblema;
    }

    public void setDescripcionProblema(String descripcionProblema) {
        this.descripcionProblema = descripcionProblema;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void setFechaFinalizacion(LocalDate fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void agregarDetalle(DetalleOrden detalle) {
        detalles.add(detalle);
    }

    public ArrayList<DetalleOrden> getDetalles() {
        return detalles;
    }

    public ArrayList<DetalleServicio> getServicios() {
        ArrayList<DetalleServicio> servicios = new ArrayList<>();
        for (DetalleOrden d : detalles) {
            if (d instanceof DetalleServicio) {
                servicios.add((DetalleServicio) d);
            }
        }
        return servicios;
    }

    public ArrayList<DetalleRepuesto> getRepuestos() {
        ArrayList<DetalleRepuesto> repuestos = new ArrayList<>();
        for (DetalleOrden d : detalles) {
            if (d instanceof DetalleRepuesto) {
                repuestos.add((DetalleRepuesto) d);
            }
        }
        return repuestos;
    }

    public BigDecimal calcularTotalManoObra() {
        BigDecimal suma = BigDecimal.ZERO;
        for (DetalleServicio d : getServicios()) {
            suma = suma.add(d.getSubtotal());
        }
        return suma;
    }

    public BigDecimal calcularTotalRepuestos() {
        BigDecimal suma = BigDecimal.ZERO;
        for (DetalleRepuesto d : getRepuestos()) {
            suma = suma.add(d.getSubtotal());
        }
        return suma;
    }

    public BigDecimal calcularTotal() {
        BigDecimal suma = BigDecimal.ZERO;
        for (DetalleOrden d : detalles) {
            suma = suma.add(d.getSubtotal());
        }
        return suma;
    }

    public boolean esModificable() {
        return estado == EstadoOrden.PENDIENTE || estado == EstadoOrden.EN_PROCESO;
    }
}
