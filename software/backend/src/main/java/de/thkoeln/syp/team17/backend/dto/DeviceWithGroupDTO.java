package de.thkoeln.syp.team17.backend.dto;


import de.thkoeln.syp.team17.backend.entities.Device;
import de.thkoeln.syp.team17.backend.entities.enums.DeviceGroupRole;
import lombok.Getter;

@Getter
public class DeviceWithGroupDTO extends DeviceDTO {

    private DeviceGroupRole role;

    public DeviceWithGroupDTO(Device device, DeviceGroupRole role) {
        super(device);
        this.role = role;
    }

}
