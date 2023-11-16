package org.apache.seatunnel.connectors.seatunnel.mqtt.source;

import org.apache.seatunnel.api.serialization.DeserializationSchema;
import org.apache.seatunnel.api.source.Collector;
import org.apache.seatunnel.api.table.type.SeaTunnelRow;
import org.apache.seatunnel.common.utils.JsonUtils;
import org.apache.seatunnel.connectors.seatunnel.common.source.AbstractSingleSplitReader;
import org.apache.seatunnel.connectors.seatunnel.common.source.SingleSplitReaderContext;
import org.apache.seatunnel.connectors.seatunnel.mqtt.client.MqttClientProvider;
import org.apache.seatunnel.connectors.seatunnel.mqtt.config.JsonField;
import org.apache.seatunnel.connectors.seatunnel.mqtt.config.MqttParameter;
import org.apache.seatunnel.connectors.seatunnel.mqtt.exception.MqttConnectorErrorCode;
import org.apache.seatunnel.connectors.seatunnel.mqtt.exception.MqttConnectorException;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

@Slf4j
@Setter
public class MqttSourceReader extends AbstractSingleSplitReader<SeaTunnelRow> {
    protected final SingleSplitReaderContext context;
    protected final MqttParameter mqttParameter;
    protected MqttClientProvider mqttClient;
    private final DeserializationCollector deserializationCollector;
    private static final Option[] DEFAULT_OPTIONS = {
        Option.SUPPRESS_EXCEPTIONS, Option.ALWAYS_RETURN_LIST, Option.DEFAULT_PATH_LEAF_TO_NULL
    };
    private JsonPath[] jsonPaths;
    private final JsonField jsonField;
    private final String contentJson;
    private final Configuration jsonConfiguration =
            Configuration.defaultConfiguration().addOptions(DEFAULT_OPTIONS);
    private boolean noMoreElementFlag = true;
    private volatile boolean running = false;

    private static final long THREAD_WAIT_TIME = 500L;

    private Queue<String> dataList = new ArrayDeque<>();

    public void addData(String data) {
        dataList.add(data);
    }

    public MqttSourceReader(
            MqttParameter httpParameter,
            SingleSplitReaderContext context,
            DeserializationSchema<SeaTunnelRow> deserializationSchema,
            JsonField jsonField,
            String contentJson) {
        this.context = context;
        this.mqttParameter = httpParameter;
        this.deserializationCollector = new DeserializationCollector(deserializationSchema);
        this.jsonField = jsonField;
        this.contentJson = contentJson;
    }

    @Override
    public void open() throws Exception {
        mqttClient = new MqttClientProvider(mqttParameter);
    }

    @Override
    public void close() throws IOException {
        if (Objects.nonNull(mqttClient)) {
            mqttClient.close();
        }
    }

    @Override
    public void pollNext(Collector<SeaTunnelRow> output) throws Exception {
        if (dataList.isEmpty()) return;

        try {
            while (!dataList.isEmpty()) {
                String data = dataList.poll();
                collect(output, data);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void collect(Collector<SeaTunnelRow> output, String data) throws IOException {
        if (contentJson != null) {
            data = JsonUtils.stringToJsonNode(getPartOfJson(data)).toString();
        }
        if (jsonField != null) {
            this.initJsonPath(jsonField);
            data = JsonUtils.toJsonNode(parseToMap(decodeJSON(data), jsonField)).toString();
        }

        deserializationCollector.collect(data.getBytes(), output);
    }

    private List<Map<String, String>> parseToMap(List<List<String>> datas, JsonField jsonField) {
        List<Map<String, String>> decodeDatas = new ArrayList<>(datas.size());
        String[] keys = jsonField.getFields().keySet().toArray(new String[] {});

        for (List<String> data : datas) {
            Map<String, String> decodeData = new HashMap<>(jsonField.getFields().size());
            final int[] index = {0};
            data.forEach(
                    field -> {
                        decodeData.put(keys[index[0]], field);
                        index[0]++;
                    });
            decodeDatas.add(decodeData);
        }

        return decodeDatas;
    }

    private List<List<String>> decodeJSON(String data) {
        ReadContext jsonReadContext = JsonPath.using(jsonConfiguration).parse(data);
        List<List<String>> results = new ArrayList<>(jsonPaths.length);
        for (JsonPath path : jsonPaths) {
            List<String> result = jsonReadContext.read(path);
            results.add(result);
        }
        for (int i = 1; i < results.size(); i++) {
            List<?> result0 = results.get(0);
            List<?> result = results.get(i);
            if (result0.size() != result.size()) {
                throw new MqttConnectorException(
                        MqttConnectorErrorCode.FIELD_DATA_IS_INCONSISTENT,
                        String.format(
                                "[%s](%d) and [%s](%d) the number of parsing records is inconsistent.",
                                jsonPaths[0].getPath(),
                                result0.size(),
                                jsonPaths[i].getPath(),
                                result.size()));
            }
        }

        return dataFlip(results);
    }

    private String getPartOfJson(String data) {
        ReadContext jsonReadContext = JsonPath.using(jsonConfiguration).parse(data);
        return JsonUtils.toJsonString(jsonReadContext.read(JsonPath.compile(contentJson)));
    }

    private List<List<String>> dataFlip(List<List<String>> results) {

        List<List<String>> datas = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            List<String> result = results.get(i);
            if (i == 0) {
                for (Object o : result) {
                    String val = o == null ? null : o.toString();
                    List<String> row = new ArrayList<>(jsonPaths.length);
                    row.add(val);
                    datas.add(row);
                }
            } else {
                for (int j = 0; j < result.size(); j++) {
                    Object o = result.get(j);
                    String val = o == null ? null : o.toString();
                    List<String> row = datas.get(j);
                    row.add(val);
                }
            }
        }
        return datas;
    }

    private void initJsonPath(JsonField jsonField) {
        jsonPaths = new JsonPath[jsonField.getFields().size()];
        for (int index = 0; index < jsonField.getFields().keySet().size(); index++) {
            jsonPaths[index] =
                    JsonPath.compile(
                            jsonField.getFields().values().toArray(new String[] {})[index]);
        }
    }
}
