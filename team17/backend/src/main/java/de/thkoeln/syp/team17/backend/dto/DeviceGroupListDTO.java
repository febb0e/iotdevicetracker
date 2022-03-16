package de.thkoeln.syp.team17.backend.dto;

import de.thkoeln.syp.team17.backend.entities.DeviceGroup;
import de.thkoeln.syp.team17.backend.entities.enums.DeviceGroupRole;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DeviceGroupListDTO {

    private long totalDeviceGroups;
    private int totalPages;
    private List<DeviceGroupDTO> deviceGroups;

    public DeviceGroupListDTO(List<DeviceGroupDTO> deviceGroups, int totalPages, long totalDeviceGroups) {
        this.totalDeviceGroups = totalDeviceGroups;
        this.totalPages = totalPages;
        this.deviceGroups = deviceGroups;
    }

    public DeviceGroupListDTO(Page<DeviceGroup> deviceGroupPage) {
        totalDeviceGroups = deviceGroupPage.getTotalElements();
        totalPages = deviceGroupPage.getTotalPages();
        deviceGroups = deviceGroupPage.stream().map(dg -> new DeviceGroupDTO(dg, DeviceGroupRole.ADMIN)).collect(Collectors.toList());
    }

}
