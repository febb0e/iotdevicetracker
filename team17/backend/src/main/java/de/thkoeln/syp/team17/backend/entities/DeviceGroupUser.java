package de.thkoeln.syp.team17.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.thkoeln.syp.team17.backend.entities.enums.DeviceGroupRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "device_group_user", schema = "public")
@Setter
@Getter
public class DeviceGroupUser implements Serializable {

    @EmbeddedId
    @Setter(AccessLevel.PROTECTED)
    private DeviceGroupUserKey id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @MapsId("deviceGroupId")
    @JoinColumn(name = "device_group_id", nullable = false)
    private DeviceGroup deviceGroup;

    @ManyToOne(optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated
    @Column(name = "role", nullable = false)
    private DeviceGroupRole role;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    public DeviceGroupUser() {
        id = new DeviceGroupUserKey();
    }

}
