package de.thkoeln.syp.team17.backend.forms;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
public class CreateDeviceForm {

    @NotNull
    @NotEmpty
    private String identifier;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private Long deviceGroupId;

}
