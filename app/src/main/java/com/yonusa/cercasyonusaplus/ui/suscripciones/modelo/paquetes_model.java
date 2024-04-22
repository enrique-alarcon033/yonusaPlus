package com.yonusa.cercasyonusaplus.ui.suscripciones.modelo;


/**
 * Created by ExpoCode Tech http://expocodetech.com
 */

public class paquetes_model {


    private String PriceId;
    private String Name;
    private String Currency;
    private String Recurrency;
    private String Monto;

    public String getPriceId() {
        return PriceId;
    }

    public void setPriceId(String priceId) {
        PriceId = priceId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getRecurrency() {
        return Recurrency;
    }

    public void setRecurrency(String recurrency) {
        Recurrency = recurrency;
    }

    public String getMonto() {
        return Monto;
    }

    public void setMonto(String monto) {
        Monto = monto;
    }
}
