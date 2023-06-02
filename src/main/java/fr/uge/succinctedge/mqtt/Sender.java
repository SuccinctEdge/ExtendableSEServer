package fr.uge.succinctedge.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Sender {
    private final MqttClient client = new MqttClient("tcp://localhost:1883",new Object().toString());

    public Sender() throws MqttException {
        client.connect();
    }


    public void sendMessage(String channel, String message) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(message.getBytes());
        client.publish(channel, mqttMessage);
    }

    public void disconnect() throws MqttException {
        client.disconnect();
    }
}
