package de.thkoeln.syp.team17.backend.dto;

import de.thkoeln.syp.team17.backend.entities.Device;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DeviceListDTO {

    private long totalDevices;
    private int totalPages;
    private List<DeviceDTO> devices;

    public DeviceListDTO(Page<Device> devicePage) {
        totalDevices = devicePage.getTotalElements();
        totalPages = devicePage.getTotalPages();
        devices = devicePage.stream().map(DeviceDTO::new).collect(Collectors.toList());
    }

}
