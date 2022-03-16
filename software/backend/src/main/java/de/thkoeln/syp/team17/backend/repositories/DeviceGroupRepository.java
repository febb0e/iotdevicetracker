package de.thkoeln.syp.team17.backend.repositories;

import de.thkoeln.syp.team17.backend.entities.DeviceGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DeviceGroupRepository extends CrudRepository<DeviceGroup, Long> {

    List<DeviceGroup> findAll();

    Page<DeviceGroup> findAll(Pageable pageable);

}
