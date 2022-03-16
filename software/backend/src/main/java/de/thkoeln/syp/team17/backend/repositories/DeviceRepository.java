package de.thkoeln.syp.team17.backend.repositories;

import de.thkoeln.syp.team17.backend.entities.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends CrudRepository<Device, Long> {

    List<Device> findAll();

    Page<Device> findAll(Pageable pageable);

    List<Device> findByDeviceGroupId(long deviceGroup);

    Optional<Device> findByIdentifier(String identifier);

}
