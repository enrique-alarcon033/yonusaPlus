package com.yonusa.cercaspaniagua.libreria;

import android.os.Binder;

class MqttServiceBinder extends Binder {

    private MqttService mqttService;
    private String activityToken;

    MqttServiceBinder(MqttService mqttService) {
        this.mqttService = mqttService;
    }

    /**
     * @return a reference to the Service
     */
    public MqttService getService() {
        return mqttService;
    }

    void setActivityToken(String activityToken) {
        this.activityToken = activityToken;
    }

    /**
     * @return the activityToken provided when the Service was started
     */
    public String getActivityToken() {
        return activityToken;
    }

}