package fr.uge.succinctedge.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.uge.succinctedge.signal.ReturnSignal;
import org.eclipse.paho.client.mqttv3.*;

import java.sql.Time;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Receiver implements MqttCallback {

    private final Queue<String> buffer =  new LinkedList<String>();
    Sender querySender = new Sender();
    private final MqttClient mqttClient = new MqttClient("tcp://localhost:1883", "SEServer");

    public Receiver() throws MqttException {
        mqttClient.connect();
        mqttClient.setCallback(this);
        mqttClient.subscribe("anomaly");
    }

    public ReturnSignal getSignal() throws MqttException, JsonProcessingException {
        while(buffer.size() == 0) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return ReturnSignal.readSignal(buffer.remove());
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection Lost " + cause);
        try {
            mqttClient.connect();
            mqttClient.setCallback(this);
            mqttClient.subscribe("anomaly");
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        buffer.add(message.toString());
        // Client serveur latence
        //System.out.println(System.currentTimeMillis() - Double.parseDouble(message.toString().split(",")[0]) + "ms");
        //querySender.sendMessage("latence", message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
