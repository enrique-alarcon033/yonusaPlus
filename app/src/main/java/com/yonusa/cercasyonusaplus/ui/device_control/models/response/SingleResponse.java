package com.yonusa.cercasyonusaplus.ui.device_control.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SingleResponse {

    @SerializedName("codigo")
    @Expose
    private Integer codigo;
    @SerializedName("codigoEspecifico")
    @Expose
    private Integer codigoEspecifico;
    @SerializedName("mensaje")
    @Expose
    private String mensaje;

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getCodigoEspecifico() {
        return codigoEspecifico;
    }

    public void setCodigoEspecifico(Integer codigoEspecifico) {
        this.codigoEspecifico = codigoEspecifico;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}