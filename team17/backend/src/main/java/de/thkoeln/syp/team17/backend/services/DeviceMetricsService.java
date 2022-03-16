package de.thkoeln.syp.team17.backend.services;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxTable;
import de.thkoeln.syp.team17.backend.dto.MultiValueDTO;
import de.thkoeln.syp.team17.backend.seeding.LoadExampleSingleValues;
import de.thkoeln.syp.team17.backend.seeding.Location;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeviceMetricsService {

    private static final String VALUE_FIELD = "_value";

    private final QueryApi queryApi;

    public DeviceMetricsService(InfluxDBClient client) {
        this.queryApi = client.getQueryApi();
    }

    protected String getMetricQuery(DeviceMultiQuery query) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("from(bucket:\"iotdt\")")
                    .append(String.format("|> range(start: %s, stop: %s)", query.getStart(), query.getStop()))
                    .append("|> filter(fn: (r) =>")
                    .append("   r._measurement == \"metrics\" and")
                    .append(String.format("   r.identifier == \"%s\" and", query.getIdentifier()));
        for (Iterator<String> fields = query.getFields().iterator(); fields.hasNext(); ) {
            queryString.append(String.format("   r._field == \"%s\" ", fields.next()));
            if (fields.hasNext()) {
                queryString.append("or");
            }
        }
        queryString.append(")");
        queryString.append(String.format("|> aggregateWindow(every: %s, fn: %s )", query.getInterval(), query.getAggregationMethod()));
        return queryString.toString();
    }

    protected String getMetricFieldsQuery(DeviceMultiQuery query) {
        return "from(bucket:\"iotdt\")" +
                String.format("|> range(start: %s, stop: %s)", query.getStart(), query.getStop()) +
                "|> filter(fn: (r) =>" +
                "r._measurement == \"metrics\" and " +
                String.format("r.identifier == \"%s\")", query.getIdentifier()) +
                "|> keep(columns: [\"_field\"])" +
                "|> group()" +
                "|> distinct(column: \"_field\")" +
                "|> limit(n: 200)" +
                "|> sort()";
    }

    public List<String> getFieldsOf(DeviceMultiQuery deviceMultiQuery) {
        List<String> output = new LinkedList<>();
        List<FluxTable> fields = queryApi.query(getMetricFieldsQuery(deviceMultiQuery));
        fields.forEach(fluxTable ->
                fluxTable.getRecords().forEach(fluxRecord -> output.add(String.valueOf(fluxRecord.getValueByKey(VALUE_FIELD))))
        );
        return output;
    }

    public List<MultiValueDTO> getMetricsOf(DeviceMultiQuery deviceMultiQuery) {
        HashMap<Instant, MultiValueDTO> metrics = new HashMap<>();
        List<FluxTable> fields = queryApi.query(getMetricQuery(deviceMultiQuery));
        for (FluxTable field : fields) {
            field.getRecords()
                    .forEach(fluxRecord -> {
                                if (metrics.containsKey(fluxRecord.getTime())) {
                                    metrics.get(fluxRecord.getTime()).addValue(fluxRecord.getValueByKey(VALUE_FIELD));
                                } else {
                                    metrics.put(fluxRecord.getTime(), new MultiValueDTO(
                                                    fluxRecord.getTime(),
                                                    String.valueOf(fluxRecord.getValueByKey("topic")),
                                                    fluxRecord.getField(),
                                                    (Double) fluxRecord.getValueByKey(VALUE_FIELD)
                                            )
                                    );
                                }
                            }
                    );
        }
        return metrics.values().stream().sorted(Comparator.comparing(MultiValueDTO::getTime)).collect(Collectors.toList());
    }

    public List<MultiValueDTO> getMockMetricsOf() {
        //Extract all Record entries received from the fluxquery
        List<MultiValueDTO> metrics = LoadExampleSingleValues.getGeoData(100, new Location(50.93446149714275, 6.98777619946928), 0.01);
        metrics.sort(Comparator.comparing(MultiValueDTO::getTime));
        return metrics;
    }

}
