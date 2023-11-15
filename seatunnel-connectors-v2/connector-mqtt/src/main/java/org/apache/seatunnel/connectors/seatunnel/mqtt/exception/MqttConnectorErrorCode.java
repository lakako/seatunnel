package org.apache.seatunnel.connectors.seatunnel.mqtt.exception;

import org.apache.seatunnel.common.exception.SeaTunnelErrorCode;

public enum MqttConnectorErrorCode implements SeaTunnelErrorCode {
    FIELD_DATA_IS_INCONSISTENT("MQTT-01", "The field data is inconsistent");

    private final String code;
    private final String description;

    MqttConnectorErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
