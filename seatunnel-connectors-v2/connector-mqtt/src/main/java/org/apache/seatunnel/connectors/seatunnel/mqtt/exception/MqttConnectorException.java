package org.apache.seatunnel.connectors.seatunnel.mqtt.exception;

import org.apache.seatunnel.common.exception.SeaTunnelErrorCode;
import org.apache.seatunnel.common.exception.SeaTunnelRuntimeException;

public class MqttConnectorException extends SeaTunnelRuntimeException {
    public MqttConnectorException(SeaTunnelErrorCode seaTunnelErrorCode, String errorMessage) {
        super(seaTunnelErrorCode, errorMessage);
    }


}
