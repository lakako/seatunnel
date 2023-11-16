package org.apache.seatunnel.connectors.seatunnel.mqtt.state;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Properties;

@Data
@AllArgsConstructor
public class MqttSinkState implements Serializable {

    private final String transactionId;
    private final String transactionIdPrefix;
    private final long checkpointId;
    private final Properties kafkaProperties;
}
