package de.thkoeln.syp.team17.backend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class MultiValueDTO implements Serializable {
    private Instant time;

    private String identifier;

    private String name;

    private List<Double> values;


    public MultiValueDTO(Instant time, String identifier, String name, Double value) {
        this.time = time;
        this.identifier = identifier;
        this.name = name;
        values = new ArrayList<>();
        values.add(value);
    }

    public MultiValueDTO(Instant time, String identifier, String name, Double... values) {
        this.time = time;
        this.identifier = identifier;
        this.name = name;
        this.values = Arrays.asList(values);
    }

    public void addValue(Object value) {
        values.add((Double)value);
    }

}
