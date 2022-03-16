package de.thkoeln.syp.team17.backend.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class DeviceGroupUserKey implements Serializable {

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "device_group_id", nullable = false)
    private long deviceGroupId;

}
