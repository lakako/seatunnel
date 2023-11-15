package org.apache.seatunnel.connectors.seatunnel.mqtt.config;

import lombok.Getter;
import org.apache.seatunnel.api.configuration.Option;
import org.apache.seatunnel.api.configuration.Options;

public class MqttConfig {
    public static final Option<String> BROKER_URL =
            Options.key("broker_url")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("Mqtt broker url");

    public static final Option<String> USERNAME =
            Options.key("username")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("broker username");

    public static final Option<String> PASSWORD =
            Options.key("password")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("broker password");

    public static final Option<String> TOPIC =
            Options.key("topic")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("mqtt topic");

    public static final Option<String> CLIENT_ID =
            Options.key("client_id")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("mqtt client id");

    public static final Option<JsonField> JSON_FIELD =
            Options.key("json_field")
                    .objectType(JsonField.class)
                    .noDefaultValue()
                    .withDescription(
                            "SeaTunnel json field.When partial json data is required, this parameter can be configured to obtain data");
    public static final Option<String> CONTENT_FIELD =
            Options.key("content_field")
                    .stringType()
                    .noDefaultValue()
                    .withDescription(
                            "SeaTunnel content field.This parameter can get some json data, and there is no need to configure each field separately.");

}
