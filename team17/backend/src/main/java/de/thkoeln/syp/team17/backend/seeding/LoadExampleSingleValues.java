package de.thkoeln.syp.team17.backend.seeding;

import de.thkoeln.syp.team17.backend.dto.MultiValueDTO;
import de.thkoeln.syp.team17.backend.dto.SingleValueDTO;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class LoadExampleSingleValues {

    private LoadExampleSingleValues() {

    }

    public static List<SingleValueDTO> getSingle(int dataAmount, double startValue, double maxDelta) {
        SingleValueDTO[] list = new SingleValueDTO[dataAmount];
        Instant time = Instant.now();
        double value = startValue;
        for (int i = 0; i < dataAmount; i++) {
            list[i] = new SingleValueDTO(time, "00:00:00:00:00:00", "ExampleMetric", value);
            time = time.minusSeconds(10L);
            value = value + (Math.random() * (maxDelta * 2) - maxDelta);
        }
        return Arrays.asList(list);
    }

    public static List<MultiValueDTO> getGeoData(int dataAmount, Location startPoint, double maxDelta) {
        MultiValueDTO[] list = new MultiValueDTO[dataAmount];
        Instant time = Instant.now();
        Location point = startPoint;
        for (int i = 0; i < dataAmount; i++) {
            list[i] = new MultiValueDTO(time, "00:00:00:00:00:00", "ExamplePositionMetric", point.getLatitude(), point.getLongitude());
            time = time.minusSeconds(10L);
            double langDelta = ((Math.random()) * (maxDelta * 2 - (maxDelta/2))) - maxDelta + (maxDelta/2);
            double longDelta = ((Math.random()) * (maxDelta * 2 - (maxDelta/2))) - maxDelta + (maxDelta/2);
            point = new Location(point.getLatitude() + langDelta, point.getLongitude() + longDelta);
        }
        return Arrays.asList(list);
    }
}
