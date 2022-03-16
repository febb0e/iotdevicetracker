package de.thkoeln.syp.team17.backend.dto;

import de.thkoeln.syp.team17.backend.entities.DeviceGroup;
import de.thkoeln.syp.team17.backend.entities.enums.DeviceGroupRole;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;

@Getter
public class DeviceGroupDTO implements Serializable {

    private Long id;
    private String name;
    private Instant updatedAt;
    private Instant createdAt;
    private Integer amountOfDevices;
    private DeviceGroupRole role;

    public DeviceGroupDTO(DeviceGroup deviceGroup, DeviceGroupRole role) {
        id = deviceGroup.getId();
        name = deviceGroup.getName();
        updatedAt = deviceGroup.getUpdatedAt();
        createdAt = deviceGroup.getCreatedAt();
        amountOfDevices = deviceGroup.getDevices().size();
        this.role = role;
    }

}
