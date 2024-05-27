package com.yonusa.cercasyonusaplus.ui.device_control.models;


/**
 * Created by ExpoCode Tech http://expocodetech.com
 */

public class eventos_model {


    private String HistorialId;
    private String TextoMostrar;
    private String Nombre;
    private String Alarcon;

    public String getHistorialId() {
        return HistorialId;
    }

    public void setHistorialId(String historialId) {
        HistorialId = historialId;
    }

    public String getTextoMostrar() {
        return TextoMostrar;
    }

    public void setTextoMostrar(String textoMostrar) {
        TextoMostrar = textoMostrar;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getAlarcon() {
        return Alarcon;
    }

    public void setAlarcon(String alarcon) {
        Alarcon = alarcon;
    }

    public String getFechaRegistro() {
        return FechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        FechaRegistro = fechaRegistro;
    }

    private String FechaRegistro;


}
