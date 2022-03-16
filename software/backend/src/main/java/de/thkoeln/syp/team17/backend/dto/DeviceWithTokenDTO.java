package de.thkoeln.syp.team17.backend.dto;


import de.thkoeln.syp.team17.backend.entities.Device;
import de.thkoeln.syp.team17.backend.entities.enums.DeviceGroupRole;
import lombok.Getter;

@Getter
public class DeviceWithTokenDTO extends DeviceWithGroupDTO {

    private String token;

    public DeviceWithTokenDTO(Device device, DeviceGroupRole role, String token) {
        super(device, role);
        this.token = token;
    }

}
