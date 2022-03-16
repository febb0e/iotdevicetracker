package de.thkoeln.syp.team17.backend.services;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.extern.apachecommons.CommonsLog;

import java.util.List;
import java.util.Objects;

@Builder
@CommonsLog
@Getter
public class DeviceMultiQuery {
    @NonNull
    private String start;
    private String stop;

    @NonNull
    private String identifier;

    @Singular("field")
    private List<String> fields;

    private String interval;

    private String aggregationMethod;

    DeviceMultiQuery(@NonNull String start, String stop, @NonNull String identifier, List<String> fields, String interval, String aggregationMethod) {
        this.start = start;
        this.stop = Objects.requireNonNullElse(stop, "0h");
        this.identifier = identifier;
        this.fields = fields;
        this.interval = Objects.requireNonNullElse(interval, "1m");
        this.aggregationMethod = Objects.requireNonNullElse(aggregationMethod, "mean");
    }
}
