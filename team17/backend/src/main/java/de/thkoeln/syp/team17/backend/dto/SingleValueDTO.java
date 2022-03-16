package de.thkoeln.syp.team17.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;

@AllArgsConstructor
@Getter
public class SingleValueDTO implements Serializable {

    private Instant time;

    private String identifier;

    private String name;

    private Double value;

}
