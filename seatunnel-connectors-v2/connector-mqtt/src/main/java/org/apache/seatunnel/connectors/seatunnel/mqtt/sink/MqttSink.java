package org.apache.seatunnel.connectors.seatunnel.mqtt.sink;

import com.google.auto.service.AutoService;
import org.apache.seatunnel.api.common.PrepareFailException;
import org.apache.seatunnel.api.common.SeaTunnelAPIErrorCode;
import org.apache.seatunnel.api.sink.SeaTunnelSink;
import org.apache.seatunnel.api.sink.SinkWriter;
import org.apache.seatunnel.api.table.type.SeaTunnelRow;
import org.apache.seatunnel.api.table.type.SeaTunnelRowType;
import org.apache.seatunnel.common.config.CheckConfigUtil;
import org.apache.seatunnel.common.config.CheckResult;
import org.apache.seatunnel.common.constants.PluginType;
import org.apache.seatunnel.connectors.seatunnel.common.sink.AbstractSimpleSink;
import org.apache.seatunnel.connectors.seatunnel.common.sink.AbstractSinkWriter;
import org.apache.seatunnel.connectors.seatunnel.mqtt.config.MqttConfig;
import org.apache.seatunnel.connectors.seatunnel.mqtt.config.MqttParameter;
import org.apache.seatunnel.connectors.seatunnel.mqtt.exception.MqttConnectorException;
import org.apache.seatunnel.shade.com.typesafe.config.Config;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@AutoService(SeaTunnelSink.class)
public class MqttSink extends AbstractSimpleSink<SeaTunnelRow, Void> {
    protected final MqttParameter mqttParameter = new MqttParameter();
    protected SeaTunnelRowType seaTunnelRowType;
    protected Config pluginConfig;

    @Override
    public String getPluginName() {
        return "Mqtt";
    }

    @Override
    public void prepare(Config pluginConfig) throws PrepareFailException {
        this.pluginConfig = pluginConfig;
        CheckResult result = CheckConfigUtil.checkAllExists(pluginConfig, MqttConfig.BROKER_URL.key());
        if (!result.isSuccess()) {
            throw new MqttConnectorException(
                    SeaTunnelAPIErrorCode.CONFIG_VALIDATION_FAILED,
                    String.format(
                            "PluginName: %s, PluginType: %s, Message: %s",
                            getPluginName(), PluginType.SINK, result.getMsg()));
        }
        mqttParameter.setBrokerUrl(pluginConfig.getString(MqttConfig.BROKER_URL.key()));
    }

    @Override
    public void setTypeInfo(SeaTunnelRowType seaTunnelRowType) {
        this.seaTunnelRowType = seaTunnelRowType;
    }

    @Override
    public AbstractSinkWriter<SeaTunnelRow, Void> createWriter(SinkWriter.Context context)
            throws IOException {
        return new MqttSinkWriter(seaTunnelRowType, mqttParameter);
    }
}
