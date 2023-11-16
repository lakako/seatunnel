package org.apache.seatunnel.connectors.seatunnel.mqtt.state;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Properties;

@Data
@AllArgsConstructor
public class MqttCommitInfo implements Serializable {

    private final String transactionId;
    private final Properties kafkaProperties;
    private final long producerId;
    private final short epoch;
}
