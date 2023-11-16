package org.apache.seatunnel.connectors.seatunnel.mqtt.source;

import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.api.source.SeaTunnelSource;
import org.apache.seatunnel.api.table.factory.Factory;
import org.apache.seatunnel.api.table.factory.TableSourceFactory;
import org.apache.seatunnel.connectors.seatunnel.mqtt.config.MqttConfig;

import com.google.auto.service.AutoService;

@AutoService(Factory.class)
public class MqttSourceFactory implements TableSourceFactory {
    @Override
    public String factoryIdentifier() {
        return "Mqtt";
    }

    @Override
    public OptionRule optionRule() {
        return getMqttBuilder().build();
    }

    public OptionRule.Builder getMqttBuilder() {
        return OptionRule.builder()
                .required(MqttConfig.BROKER_URL)
                .required(MqttConfig.CLIENT_ID)
                .required(MqttConfig.TOPIC)
                .optional(MqttConfig.USERNAME)
                .optional(MqttConfig.PASSWORD);
    }

    @Override
    public Class<? extends SeaTunnelSource> getSourceClass() {
        return MqttSource.class;
    }
}
