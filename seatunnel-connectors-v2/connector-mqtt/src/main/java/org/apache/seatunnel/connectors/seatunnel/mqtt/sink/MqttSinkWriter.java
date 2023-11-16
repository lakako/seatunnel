package org.apache.seatunnel.connectors.seatunnel.mqtt.sink;

import org.apache.seatunnel.api.serialization.SerializationSchema;
import org.apache.seatunnel.api.table.type.SeaTunnelRow;
import org.apache.seatunnel.api.table.type.SeaTunnelRowType;
import org.apache.seatunnel.connectors.seatunnel.common.sink.AbstractSinkWriter;
import org.apache.seatunnel.connectors.seatunnel.mqtt.client.MqttClientProvider;
import org.apache.seatunnel.connectors.seatunnel.mqtt.config.MqttParameter;
import org.apache.seatunnel.format.json.JsonSerializationSchema;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class MqttSinkWriter extends AbstractSinkWriter<SeaTunnelRow, Void> {
    protected final MqttClientProvider mqttClient;
    protected final SeaTunnelRowType seaTunnelRowType;
    protected final MqttParameter mqttParameter;
    protected final SerializationSchema serializationSchema;

    public MqttSinkWriter(SeaTunnelRowType seaTunnelRowType, MqttParameter mqttParameter) {
        this(seaTunnelRowType, mqttParameter, new JsonSerializationSchema(seaTunnelRowType));
    }

    public MqttSinkWriter(
            SeaTunnelRowType seaTunnelRowType,
            MqttParameter httpParameter,
            SerializationSchema serializationSchema) {
        this.seaTunnelRowType = seaTunnelRowType;
        this.mqttParameter = httpParameter;
        this.mqttClient = new MqttClientProvider(httpParameter);
        this.serializationSchema = serializationSchema;
    }

    @Override
    public void write(SeaTunnelRow element) throws IOException {
        byte[] serialize = serializationSchema.serialize(element);
        String body = new String(serialize);
        try {
            mqttClient.publish(mqttParameter.getTopic(), body);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException {
        if (Objects.nonNull(mqttClient)) {
            mqttClient.close();
        }
    }
}
