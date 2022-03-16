package de.thkoeln.syp.team17.backend.dto;

import de.thkoeln.syp.team17.backend.entities.Device;
import lombok.Getter;

import java.time.Instant;

@Getter
public class DeviceDTO {

    private long id;
    private String identifier;
    private String name;
    private Instant updatedAt;
    private Instant createdAt;

    public DeviceDTO(Device device) {
        id = device.getId();
        identifier = device.getIdentifier();
        name = device.getName();
        updatedAt = device.getUpdatedAt();
        createdAt = device.getCreatedAt();
    }

}
