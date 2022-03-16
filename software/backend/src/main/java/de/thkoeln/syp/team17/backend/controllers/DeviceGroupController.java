package de.thkoeln.syp.team17.backend.controllers;

import de.thkoeln.syp.team17.backend.dto.DeviceGroupDTO;
import de.thkoeln.syp.team17.backend.dto.DeviceGroupListDTO;
import de.thkoeln.syp.team17.backend.entities.DeviceGroup;
import de.thkoeln.syp.team17.backend.entities.DeviceGroupUser;
import de.thkoeln.syp.team17.backend.entities.User;
import de.thkoeln.syp.team17.backend.entities.enums.DeviceGroupRole;
import de.thkoeln.syp.team17.backend.entities.enums.UserRole;
import de.thkoeln.syp.team17.backend.exceptions.ValidationException;
import de.thkoeln.syp.team17.backend.forms.CreateDeviceGroupForm;
import de.thkoeln.syp.team17.backend.forms.PatchDeviceGroupForm;
import de.thkoeln.syp.team17.backend.repositories.DeviceGroupRepository;
import de.thkoeln.syp.team17.backend.repositories.DeviceGroupUserRepository;
import de.thkoeln.syp.team17.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(value = "/device-groups", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceGroupController {

    private static final String MISSING_DEVICE_GROUP = "Unable to find DeviceGroup with id:";

    @Autowired
    private DeviceGroupRepository deviceGroupRepository;

    @Autowired
    private DeviceGroupUserRepository deviceGroupUserRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get a list of DeviceGroups
     * @param principal User-details
     * @param all Determines whether only accessible DeviceGroups should be returned or all
     * @param page Determines which part of devices you receive if the amount exceeds 25, when omitted a value of 1 is assumed
     * @return A list of DeviceGroups split into pages of size of 25 per page
     */
    @GetMapping
    public ResponseEntity<DeviceGroupListDTO> getDeviceGroups(@AuthenticationPrincipal Principal principal, @RequestParam(required = false) boolean all, @RequestParam(required = false) Integer page) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        if (all) {
            if (user.getEffectiveRole() != UserRole.SYSADMIN) {
                throw new AccessDeniedException("You are not allowed to view all device groups");
            }
            if (page == null) page = 1;

            Pageable pageable = PageRequest.of(page - 1, 25);
            Page<DeviceGroup> deviceGroupPage = deviceGroupRepository.findAll(pageable);
            return ResponseEntity.ok(new DeviceGroupListDTO(deviceGroupPage));
        }

        List<DeviceGroupDTO> dtoList = deviceGroupUserRepository.findDeviceGroupUsersByIdUserId(user.getId())
                .stream()
                .map(dgu -> new DeviceGroupDTO(dgu.getDeviceGroup(), dgu.getRole()))
                .collect(Collectors.toList());

        if (page == null) page = 1;
        Pageable pageable = PageRequest.of(page - 1, 25);

        return ResponseEntity.ok(new DeviceGroupListDTO(dtoList, pageable.getPageNumber(), dtoList.size()));
    }

    /**
     * Create a DeviceGroup
     * @param principal User-details
     * @param form Details about the DeviceGroup
     * @param bindingResult validationErrors
     * @return Created DeviceGroup
     */
    @PostMapping
    @Transactional
    public ResponseEntity<DeviceGroupDTO> createDeviceGroup(@AuthenticationPrincipal Principal principal, @RequestBody @Valid CreateDeviceGroupForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors());
        }

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // Creat the device group.
        DeviceGroup deviceGroup = new DeviceGroup();
        deviceGroup.setName(form.getName());
        deviceGroupRepository.save(deviceGroup);

        // Add the user that created the group as an admin.
        DeviceGroupUser deviceGroupUser = new DeviceGroupUser();
        deviceGroupUser.setUser(user);
        deviceGroupUser.setDeviceGroup(deviceGroup);
        deviceGroupUser.setRole(DeviceGroupRole.ADMIN);
        deviceGroupUserRepository.save(deviceGroupUser);

        return ResponseEntity
                .status(CREATED)
                .body(new DeviceGroupDTO(deviceGroup, deviceGroupUser.getRole()));
    }

    /**
     * Get DeviceGroup
     * @param principal User-Details
     * @param deviceGroupId Internal ID of the DeviceGroup
     * @return DeviceGroup
     */
    @GetMapping("/{device_group_id}")
    public ResponseEntity<DeviceGroupDTO> getDeviceGroup(@AuthenticationPrincipal Principal principal, @PathVariable("device_group_id") long deviceGroupId) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        DeviceGroup deviceGroup = deviceGroupRepository.findById(deviceGroupId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, MISSING_DEVICE_GROUP + deviceGroupId));

        if (!deviceGroup.userHasPermission(user)) {
            throw new AccessDeniedException("You don't have access to this device group");
        }

        return ResponseEntity
                .ok(new DeviceGroupDTO(deviceGroup, deviceGroup.getUserRole(user)));
    }

    /**
     * Edit DeviceGroup
     * @param principal User-Details
     * @param form Changes for the DeviceGroup
     * @param deviceGroupId Internal ID of the DeviceGroup
     * @param bindingResult validationErrors
     * @return Updated DeviceGroup
     */
    @PatchMapping("/{device_group_id}")
    @Transactional
    public ResponseEntity<DeviceGroupDTO> patchDeviceGroup(@AuthenticationPrincipal Principal principal, @RequestBody @Valid PatchDeviceGroupForm form, @PathVariable("device_group_id") long deviceGroupId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors());
        }

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        DeviceGroup deviceGroup = deviceGroupRepository.findById(deviceGroupId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, MISSING_DEVICE_GROUP + deviceGroupId));

        if (!deviceGroup.userHasPermission(user, DeviceGroupRole.ADMIN)) {
            throw new AccessDeniedException("You don't have admin access to this device group");
        }

        deviceGroup.setName(form.getName());
        deviceGroupRepository.save(deviceGroup);

        return ResponseEntity.ok(new DeviceGroupDTO(deviceGroup, deviceGroup.getUserRole(user)));
    }

    /**
     * Delete DeviceGroup
     * @param principal User-Details
     * @param deviceGroupId Internal ID of the DeviceGroup
     */
    @DeleteMapping("/{device_group_id}")
    @Transactional
    @ResponseStatus(OK)
    public void deleteDeviceGroup(@AuthenticationPrincipal Principal principal, @PathVariable("device_group_id") long deviceGroupId) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        DeviceGroup deviceGroup = deviceGroupRepository.findById(deviceGroupId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, MISSING_DEVICE_GROUP + deviceGroupId));

        if (!deviceGroup.userHasPermission(user, DeviceGroupRole.ADMIN)) {
            throw new AccessDeniedException("You don't have admin access to this device group");
        }

        deviceGroupRepository.delete(deviceGroup);
    }

}
