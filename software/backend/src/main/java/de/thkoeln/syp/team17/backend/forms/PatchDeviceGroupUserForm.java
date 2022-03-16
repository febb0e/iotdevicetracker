package de.thkoeln.syp.team17.backend.forms;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
public class PatchDeviceGroupUserForm {

    @NotNull
    @NotEmpty
    private String role;

}
