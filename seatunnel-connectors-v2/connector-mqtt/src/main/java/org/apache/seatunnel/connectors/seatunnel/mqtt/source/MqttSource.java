package org.apache.seatunnel.connectors.seatunnel.mqtt.source;

import org.apache.seatunnel.shade.com.typesafe.config.Config;
import org.apache.seatunnel.shade.com.typesafe.config.ConfigRenderOptions;

import org.apache.seatunnel.api.common.JobContext;
import org.apache.seatunnel.api.common.PrepareFailException;
import org.apache.seatunnel.api.common.SeaTunnelAPIErrorCode;
import org.apache.seatunnel.api.serialization.DeserializationSchema;
import org.apache.seatunnel.api.source.Boundedness;
import org.apache.seatunnel.api.source.SeaTunnelSource;
import org.apache.seatunnel.api.table.catalog.CatalogTableUtil;
import org.apache.seatunnel.api.table.catalog.schema.TableSchemaOptions;
import org.apache.seatunnel.api.table.type.SeaTunnelRow;
import org.apache.seatunnel.api.table.type.SeaTunnelRowType;
import org.apache.seatunnel.common.config.CheckConfigUtil;
import org.apache.seatunnel.common.config.CheckResult;
import org.apache.seatunnel.common.constants.JobMode;
import org.apache.seatunnel.common.constants.PluginType;
import org.apache.seatunnel.common.utils.JsonUtils;
import org.apache.seatunnel.connectors.seatunnel.common.source.AbstractSingleSplitReader;
import org.apache.seatunnel.connectors.seatunnel.common.source.AbstractSingleSplitSource;
import org.apache.seatunnel.connectors.seatunnel.common.source.SingleSplitReaderContext;
import org.apache.seatunnel.connectors.seatunnel.mqtt.config.JsonField;
import org.apache.seatunnel.connectors.seatunnel.mqtt.config.MqttConfig;
import org.apache.seatunnel.connectors.seatunnel.mqtt.config.MqttParameter;
import org.apache.seatunnel.connectors.seatunnel.mqtt.exception.MqttConnectorException;
import org.apache.seatunnel.format.json.JsonDeserializationSchema;

import com.google.auto.service.AutoService;

@AutoService(SeaTunnelSource.class)
public class MqttSource extends AbstractSingleSplitSource<SeaTunnelRow> {
    protected final MqttParameter mqttParameter = new MqttParameter();
    protected SeaTunnelRowType rowType;
    protected JsonField jsonField;
    protected String contentField;
    protected JobContext jobContext;
    protected DeserializationSchema<SeaTunnelRow> deserializationSchema;

    @Override
    public String getPluginName() {
        return "Mqtt";
    }

    @Override
    public Boundedness getBoundedness() {
        return JobMode.BATCH.equals(jobContext.getJobMode())
                ? Boundedness.BOUNDED
                : Boundedness.UNBOUNDED;
    }

    @Override
    public void prepare(Config pluginConfig) throws PrepareFailException {
        CheckResult result =
                CheckConfigUtil.checkAllExists(pluginConfig, MqttConfig.BROKER_URL.key());
        if (!result.isSuccess()) {
            throw new MqttConnectorException(
                    SeaTunnelAPIErrorCode.CONFIG_VALIDATION_FAILED,
                    String.format(
                            "PluginName: %s, PluginType: %s, Message: %s",
                            getPluginName(), PluginType.SOURCE, result.getMsg()));
        }
        this.mqttParameter.buildWithConfig(pluginConfig);
        buildSchemaWithConfig(pluginConfig);
    }

    @Override
    public AbstractSingleSplitReader<SeaTunnelRow> createReader(
            SingleSplitReaderContext readerContext) throws Exception {
        return new MqttSourceReader(
                this.mqttParameter,
                readerContext,
                this.deserializationSchema,
                jsonField,
                contentField);
    }

    protected void buildSchemaWithConfig(Config pluginConfig) {
        if (pluginConfig.hasPath(TableSchemaOptions.SCHEMA.key())) {
            this.deserializationSchema = new JsonDeserializationSchema(false, false, rowType);
            if (pluginConfig.hasPath(MqttConfig.JSON_FIELD.key())) {
                jsonField = getJsonField(pluginConfig.getConfig(MqttConfig.JSON_FIELD.key()));
            }
            if (pluginConfig.hasPath(MqttConfig.CONTENT_FIELD.key())) {
                contentField = pluginConfig.getString(MqttConfig.CONTENT_FIELD.key());
            }
        } else {
            this.rowType = CatalogTableUtil.buildSimpleTextSchema();
            this.deserializationSchema = new SimpleTextDeserializationSchema(this.rowType);
        }
    }

    private JsonField getJsonField(Config jsonFieldConf) {
        ConfigRenderOptions options = ConfigRenderOptions.concise();
        return JsonField.builder()
                .fields(JsonUtils.toMap(jsonFieldConf.root().render(options)))
                .build();
    }
}
