package com.taller.modelo;

import com.taller.excepciones.ValidacionException;

import java.math.BigDecimal;

public class Servicio {

    private String id;
    private String descripcion;
    private BigDecimal costoManoObra;

    public Servicio(String id, String descripcion, BigDecimal costoManoObra) throws ValidacionException {
        setId(id);
        setDescripcion(descripcion);
        setCostoManoObra(costoManoObra);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidacionException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidacionException("El identificador del servicio es obligatorio");
        }
        this.id = id.trim();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) throws ValidacionException {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new ValidacionException("La descripción del servicio es obligatoria");
        }
        this.descripcion = descripcion.trim();
    }

    public BigDecimal getCostoManoObra() {
        return costoManoObra;
    }

    public void setCostoManoObra(BigDecimal costoManoObra) throws ValidacionException {
        if (costoManoObra == null || costoManoObra.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacionException("El costo de mano de obra no puede ser negativo");
        }
        this.costoManoObra = costoManoObra;
    }

    @Override
    public String toString() {
        return descripcion + " (Q" + costoManoObra + ")";
    }
}
