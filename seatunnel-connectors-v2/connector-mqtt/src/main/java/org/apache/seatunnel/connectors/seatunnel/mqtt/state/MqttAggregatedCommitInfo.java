package org.apache.seatunnel.connectors.seatunnel.mqtt.state;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class MqttAggregatedCommitInfo implements Serializable {
    List<MqttCommitInfo> commitInfos;
}
