//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.vehiculos;

// Clase modelo para representar un vehículo
public class Vehiculo {
    private String correoUsuario;
    private String placa;
    private String marca;
    private String modelo;
    private String anoFabricacion;
    private String tipoCombustible;
    private String cilindraje;
    private String tipoCarroceria;
    private String transmision;
    private String traccion;
    private int numeroAirbags;
    private boolean actual;

    // Constructor vacío
    public Vehiculo() {
    }

    // Constructor completo
    public Vehiculo(String correoUsuario, String placa, String marca, String modelo, 
                   String anoFabricacion, String tipoCombustible, String cilindraje,
                   String tipoCarroceria, String transmision, String traccion, 
                   int numeroAirbags, boolean actual) {
        this.correoUsuario = correoUsuario;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabricacion = anoFabricacion;
        this.tipoCombustible = tipoCombustible;
        this.cilindraje = cilindraje;
        this.tipoCarroceria = tipoCarroceria;
        this.transmision = transmision;
        this.traccion = traccion;
        this.numeroAirbags = numeroAirbags;
        this.actual = actual;
    }

    // Getters y Setters
    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getAnoFabricacion() {
        return anoFabricacion;
    }

    public void setAnoFabricacion(String anoFabricacion) {
        this.anoFabricacion = anoFabricacion;
    }

    public String getTipoCombustible() {
        return tipoCombustible;
    }

    public void setTipoCombustible(String tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
    }

    public String getCilindraje() {
        return cilindraje;
    }

    public void setCilindraje(String cilindraje) {
        this.cilindraje = cilindraje;
    }

    public String getTipoCarroceria() {
        return tipoCarroceria;
    }

    public void setTipoCarroceria(String tipoCarroceria) {
        this.tipoCarroceria = tipoCarroceria;
    }

    public String getTransmision() {
        return transmision;
    }

    public void setTransmision(String transmision) {
        this.transmision = transmision;
    }

    public String getTraccion() {
        return traccion;
    }

    public void setTraccion(String traccion) {
        this.traccion = traccion;
    }

    public int getNumeroAirbags() {
        return numeroAirbags;
    }

    public void setNumeroAirbags(int numeroAirbags) {
        this.numeroAirbags = numeroAirbags;
    }

    public boolean isActual() {
        return actual;
    }

    public void setActual(boolean actual) {
        this.actual = actual;
    }

    @Override
    public String toString() {
        return marca + " " + modelo + " (" + placa + ")";
    }
}
