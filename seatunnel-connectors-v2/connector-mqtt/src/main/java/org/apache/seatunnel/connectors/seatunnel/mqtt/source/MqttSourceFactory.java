package org.apache.seatunnel.connectors.seatunnel.mqtt.source;

import com.google.auto.service.AutoService;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.api.source.SeaTunnelSource;
import org.apache.seatunnel.api.table.factory.Factory;
import org.apache.seatunnel.api.table.factory.TableSourceFactory;

@AutoService(Factory.class)
public class MqttSourceFactory  implements TableSourceFactory {
    @Override
    public String factoryIdentifier() {
        return null;
    }

    @Override
    public OptionRule optionRule() {
        return null;
    }

    @Override
    public Class<? extends SeaTunnelSource> getSourceClass() {
        return null;
    }
}
