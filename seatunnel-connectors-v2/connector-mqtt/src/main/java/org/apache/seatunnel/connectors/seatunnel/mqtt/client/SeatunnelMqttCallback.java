package org.apache.seatunnel.connectors.seatunnel.mqtt.client;

import org.apache.seatunnel.connectors.seatunnel.mqtt.source.MqttSourceReader;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SeatunnelMqttCallback implements MqttCallback {
    private final String patternString;

    public SeatunnelMqttCallback(String topic) {
        this.patternString = topic;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.error("lost connection");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.debug("topic: {}, message: {}", topic, mqttMessage);
//        Pattern pattern = Pattern.compile(patternString);
//        Matcher matcher = pattern.matcher(topic);
//        boolean matches = matcher.matches();

//        if (patternString.equals(topic)) {
            String s = new String(mqttMessage.getPayload());
            MqttSourceReader.addData(s);
//        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
}
