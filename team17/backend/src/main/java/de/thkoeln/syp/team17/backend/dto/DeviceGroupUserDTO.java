package de.thkoeln.syp.team17.backend.dto;

import de.thkoeln.syp.team17.backend.entities.DeviceGroupUser;
import de.thkoeln.syp.team17.backend.entities.DeviceGroupUserKey;
import de.thkoeln.syp.team17.backend.entities.enums.DeviceGroupRole;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;

@Getter
public class DeviceGroupUserDTO implements Serializable {

    private DeviceGroupUserKey id;
    private DeviceGroupRole role;
    private UserDTO user;
    private Instant updatedAt;
    private Instant createdAt;

    public DeviceGroupUserDTO(DeviceGroupUser dgu) {
        id = dgu.getId();
        role = dgu.getRole();
        user = new UserDTO(dgu.getUser());
        updatedAt = dgu.getUpdatedAt();
        createdAt = dgu.getCreatedAt();
    }
}
