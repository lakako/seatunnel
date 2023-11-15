package org.apache.seatunnel.connectors.seatunnel.mqtt.client;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SeatunnelMqttCallback implements MqttCallback {
    String patternString = "test";
    @Override
    public void connectionLost(Throwable throwable) {
      log.error("lost connection");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.debug("topic: {}, message: {}", topic, mqttMessage);
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(topic);
        boolean matches = matcher.matches();

        if (matches) {
            String s = new String(mqttMessage.getPayload());

        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
