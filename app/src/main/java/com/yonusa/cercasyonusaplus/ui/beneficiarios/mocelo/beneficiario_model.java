package com.yonusa.cercasyonusaplus.ui.beneficiarios.mocelo;


/**
 * Created by ExpoCode Tech http://expocodetech.com
 */

public class beneficiario_model {


    private String IdBeneficiario;
    private String nombre;
    private String apellidos;
    private String email;
    private String rfc;
    private String UsuarioId;

    public String getIdBeneficiario() {
        return IdBeneficiario;
    }

    public void setIdBeneficiario(String idBeneficiario) {
        IdBeneficiario = idBeneficiario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getUsuarioId() {
        return UsuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        UsuarioId = usuarioId;
    }
}
