package org.apache.seatunnel.connectors.seatunnel.mqtt.config;

import org.apache.seatunnel.shade.com.typesafe.config.Config;

import lombok.Data;
import scala.Serializable;

@Data
public class MqttParameter implements Serializable {
    private String brokerUrl;
    private String topic;
    private String username;
    private String password;
    private String clientId;

    public void buildWithConfig(Config pluginConfig) {
        this.setBrokerUrl(pluginConfig.getString(MqttConfig.BROKER_URL.key()));
        this.setTopic(pluginConfig.getString(MqttConfig.TOPIC.key()));
        this.setClientId(pluginConfig.getString(MqttConfig.CLIENT_ID.key()));
//        this.setUsername(pluginConfig.getString(MqttConfig.USERNAME.key()));
//        this.setPassword(pluginConfig.getString(MqttConfig.PASSWORD.key()));
    }
}
