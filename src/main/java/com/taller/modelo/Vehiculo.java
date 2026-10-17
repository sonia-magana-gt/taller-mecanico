package com.taller.modelo;

import com.taller.excepciones.ValidacionException;

import java.time.Year;

public class Vehiculo {

    private String placa;
    private String marca;
    private String modelo;
    private int anio;
    private String color;
    private String idCliente;

    public Vehiculo(String placa, String marca, String modelo, int anio, String color, String idCliente)
            throws ValidacionException {
        setPlaca(placa);
        setMarca(marca);
        setModelo(modelo);
        setAnio(anio);
        setColor(color);
        setIdCliente(idCliente);
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) throws ValidacionException {
        if (placa == null || placa.trim().isEmpty()) {
            throw new ValidacionException("La placa es obligatoria");
        }
        this.placa = placa.trim().toUpperCase();
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) throws ValidacionException {
        if (marca == null || marca.trim().isEmpty()) {
            throw new ValidacionException("La marca es obligatoria");
        }
        this.marca = marca.trim();
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) throws ValidacionException {
        if (modelo == null || modelo.trim().isEmpty()) {
            throw new ValidacionException("El modelo es obligatorio");
        }
        this.modelo = modelo.trim();
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) throws ValidacionException {
        int maximo = Year.now().getValue() + 1;
        if (anio < 1950 || anio > maximo) {
            throw new ValidacionException("El año debe estar entre 1950 y " + maximo);
        }
        this.anio = anio;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color == null ? "" : color.trim();
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) throws ValidacionException {
        if (idCliente == null || idCliente.trim().isEmpty()) {
            throw new ValidacionException("El cliente es obligatorio");
        }
        this.idCliente = idCliente.trim();
    }

    @Override
    public String toString() {
        return placa + " - " + marca + " " + modelo;
    }
}
