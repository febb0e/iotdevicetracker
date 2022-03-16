package de.thkoeln.syp.team17.backend.repositories;

import de.thkoeln.syp.team17.backend.entities.DeviceGroupUser;
import de.thkoeln.syp.team17.backend.entities.DeviceGroupUserKey;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceGroupUserRepository extends CrudRepository<DeviceGroupUser, DeviceGroupUserKey> {

    List<DeviceGroupUser> findDeviceGroupUsersByIdDeviceGroupId(long deviceGroupId);

    List<DeviceGroupUser> findDeviceGroupUsersByIdUserId(long userId);

    Optional<DeviceGroupUser> findDeviceGroupUserByIdDeviceGroupIdAndIdUserId(long deviceGroupId, long userId);

    List<DeviceGroupUser> findAll();

    boolean existsByIdDeviceGroupIdAndIdUserId(long deviceGroupId, long userId);


}
