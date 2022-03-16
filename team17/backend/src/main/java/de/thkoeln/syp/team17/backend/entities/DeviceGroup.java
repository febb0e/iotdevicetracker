package de.thkoeln.syp.team17.backend.entities;

import de.thkoeln.syp.team17.backend.entities.enums.DeviceGroupRole;
import de.thkoeln.syp.team17.backend.entities.enums.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "device_group", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class DeviceGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "device_group_generator")
    @SequenceGenerator(name = "device_group_generator", sequenceName = "device_group_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @OneToMany(mappedBy = "deviceGroup", orphanRemoval = true)
    private List<Device> devices = new ArrayList<>();

    @OneToMany(mappedBy = "deviceGroup", orphanRemoval = true)
    private List<DeviceGroupUser> users = new ArrayList<>();

    public DeviceGroupRole getUserRole(User user) {
        if (user.getEffectiveRole() == UserRole.SYSADMIN) {
            return DeviceGroupRole.ADMIN;
        }
        Optional<DeviceGroupUser> deviceGroupUser = users.stream()
            .filter(dgu -> dgu.getId().getUserId() == user.getId())
            .findFirst();

        if (deviceGroupUser.isEmpty()) {
            return null;
        }

        return deviceGroupUser.get().getRole();
    }

    public boolean userHasPermission(User user) {
        return getUserRole(user) != null;
    }

    public boolean userHasPermission(User user, DeviceGroupRole requiredRole) {
        return getUserRole(user) == requiredRole;
    }

}
