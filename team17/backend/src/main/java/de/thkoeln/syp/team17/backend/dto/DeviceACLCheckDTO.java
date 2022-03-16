package de.thkoeln.syp.team17.backend.dto;

import lombok.Getter;

@Getter
public class DeviceACLCheckDTO {

    private String topic;
    private String clientid;
    // 1 is read, 2 is write, 3 is readwrite, 4 is subscribe
    private Integer acc;

}
