package org.apache.seatunnel.connectors.seatunnel.mqtt.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.seatunnel.common.utils.JsonUtils;
import org.apache.seatunnel.connectors.seatunnel.mqtt.config.MqttParameter;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;

@Slf4j
public class MqttClientProvider implements AutoCloseable{

    private final MqttClient mqttClient;

    public MqttClientProvider(MqttParameter mqttParameter) {
        try {
            this.mqttClient = new MqttClient(
                    mqttParameter.getBrokerUrl(), mqttParameter.getClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(mqttParameter.getUsername());
            options.setPassword(mqttParameter.getPassword().toCharArray());
            options.setConnectionTimeout(60);
            options.setKeepAliveInterval(60);
            this.mqttClient.connect(options);
        } catch (MqttException e) {
            log.error("broker connected failed", e);
            throw new RuntimeException("mqtt client init error");
        }
    }

    public void publish(String topic, Object content) throws MqttException {
        String jsonString = JsonUtils.toJsonString(content);
        MqttMessage message = new MqttMessage(jsonString.getBytes());
        message.setQos(2);
        mqttClient.publish(topic, message);
    }

    @Override
    public void close() throws IOException {
        try {
            this.mqttClient.close();
        } catch (MqttException exception) {
            throw new IOException(exception.getMessage());
        }
    }
}
