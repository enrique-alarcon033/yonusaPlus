package com.yonusa.cercasyonusaplus.mqtt;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.yonusa.cercasyonusaplus.utilities.catalogs.Mqtt_CMD;


//import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;

public class Publisher {

    String clientId = MqttClient.generateClientId();
    MqttAndroidClient client;
    SharedPreferences prefs;

    public void SendMessage(Context context, final String msn, String mac){
        prefs = context.getSharedPreferences("Datos_usuario",Context.MODE_PRIVATE);


        String mqttHost = prefs.getString("MQTT_SERVER", "");
        String mqttPort = prefs.getString("MQTT_PORT", "");
        String mqttPasswrd = prefs.getString("MQTT_PWD", "");
        String mqttUser = prefs.getString("MQTT_USER", "");

            client = new MqttAndroidClient(context, "tcp://"+mqttHost+":"+mqttPort, clientId, Ack.AUTO_ACK);
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(true);
            mqttConnectOptions.setPassword(mqttPasswrd.toCharArray());
            mqttConnectOptions.setUserName(mqttUser);
            client.connect(mqttConnectOptions).setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    try {
                        MqttMessage message = new MqttMessage(msn.getBytes());
                        Log.e("MQTT", message.toString());
                        message.setQos(1);
                        client.publish(Mqtt_CMD.GENERAL_TOPIC.concat(mac), message);
                      //  client.disconnect();
                    } catch (Exception ex) {
                        Log.e("MQTT", ex.getMessage());
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    exception.printStackTrace();
                }
            });

    }

}
