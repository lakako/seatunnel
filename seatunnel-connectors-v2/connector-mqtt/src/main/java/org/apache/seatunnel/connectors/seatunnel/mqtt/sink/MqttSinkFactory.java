package org.apache.seatunnel.connectors.seatunnel.mqtt.sink;

import com.google.auto.service.AutoService;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.api.table.factory.Factory;
import org.apache.seatunnel.api.table.factory.TableSinkFactory;
import org.apache.seatunnel.connectors.seatunnel.mqtt.config.MqttConfig;

@AutoService(Factory.class)
public class MqttSinkFactory implements TableSinkFactory {
    @Override
    public String factoryIdentifier() {
        return "Mqtt";
    }

    @Override
    public OptionRule optionRule() {
        return OptionRule.builder()
                .required(MqttConfig.BROKER_URL)
                .optional(MqttConfig.USERNAME)
                .optional(MqttConfig.PASSWORD)
                .build();
    }
}
