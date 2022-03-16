package de.thkoeln.syp.team17.backend.controllers;

import de.thkoeln.syp.team17.backend.dto.DeviceGroupUserDTO;
import de.thkoeln.syp.team17.backend.entities.DeviceGroup;
import de.thkoeln.syp.team17.backend.entities.DeviceGroupUser;
import de.thkoeln.syp.team17.backend.entities.User;
import de.thkoeln.syp.team17.backend.entities.enums.DeviceGroupRole;
import de.thkoeln.syp.team17.backend.exceptions.ValidationException;
import de.thkoeln.syp.team17.backend.forms.CreateDeviceGroupUserForm;
import de.thkoeln.syp.team17.backend.forms.PatchDeviceGroupUserForm;
import de.thkoeln.syp.team17.backend.repositories.DeviceGroupRepository;
import de.thkoeln.syp.team17.backend.repositories.DeviceGroupUserRepository;
import de.thkoeln.syp.team17.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(value = "/device-groups/{device_group_id}/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceGroupUserController {

    private static final String MISSING_ADMIN_PERMISSION = "You don't have admin permissions to this device group.";

    private static final String USERNAME_FIELD = "username";

    @Autowired
    private DeviceGroupRepository deviceGroupRepository;

    @Autowired
    private DeviceGroupUserRepository deviceGroupUserRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Gets Permissions for a DeviceGroup
     * @param principal User-Details
     * @param deviceGroupId Associated DeviceGroup of the Permissions
     * @return List of permissions
     */
    @GetMapping
    public ResponseEntity<List<DeviceGroupUserDTO>> getDeviceGroupUsers(@AuthenticationPrincipal Principal principal, @PathVariable("device_group_id") long deviceGroupId) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        DeviceGroup deviceGroup = deviceGroupRepository.findById(deviceGroupId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find DeviceGroup with id:" + deviceGroupId));

        if (!deviceGroup.userHasPermission(user, DeviceGroupRole.ADMIN)) {
            throw new AccessDeniedException(MISSING_ADMIN_PERMISSION);
        }

        List<DeviceGroupUser> users = deviceGroupUserRepository.findDeviceGroupUsersByIdDeviceGroupId(deviceGroupId);
        return ResponseEntity.ok(users.stream().map(DeviceGroupUserDTO::new).collect(Collectors.toList()));
    }

    /**
     * Create Permissions
     * @param principal User-Details
     * @param deviceGroupId Associated DeviceGroup of the Permission
     * @param form PermissionsForm
     * @param bindingResult validationErrors
     * @return Created Permission
     */
    @PostMapping
    @Transactional
    public ResponseEntity<DeviceGroupUserDTO> createDeviceGroupUser(@AuthenticationPrincipal Principal principal, @PathVariable("device_group_id") long deviceGroupId, @RequestBody @Valid CreateDeviceGroupUserForm form, BindingResult bindingResult) {
        User authenticatedUser = userRepository.findByUsername(principal.getName()).orElseThrow();
        DeviceGroup deviceGroup = deviceGroupRepository.findById(deviceGroupId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find DeviceGroup with id:" + deviceGroupId));

        if (!deviceGroup.userHasPermission(authenticatedUser, DeviceGroupRole.ADMIN)) {
            throw new AccessDeniedException(MISSING_ADMIN_PERMISSION);
        }

        User userToAdd = null;
        DeviceGroupRole role = null;

        List<ObjectError> validationErrors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            validationErrors.addAll(bindingResult.getAllErrors());
        }
        // Validate and find user.
        if (!bindingResult.hasFieldErrors(USERNAME_FIELD)) {
            Optional<User> user = userRepository.findByUsername(form.getUsername());
            if (user.isEmpty()) {
                validationErrors.add(new FieldError(form.getClass().getName(), USERNAME_FIELD, "User was not found"));
            } else if (deviceGroup.userHasPermission(user.get())) {
                validationErrors.add(new FieldError(form.getClass().getName(), USERNAME_FIELD, "User already has permission"));
            } else {
                userToAdd = user.get();
            }
        }
        // Validate and get role.
        if (!bindingResult.hasFieldErrors("role")) {
            switch (form.getRole()) {
                case "ADMIN":
                    role = DeviceGroupRole.ADMIN;
                    break;
                case "OBSERVER":
                    role = DeviceGroupRole.OBSERVER;
                    break;
                default:
                    validationErrors.add(new FieldError(form.getClass().getName(), "role", "Unknown role"));
            }
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

        DeviceGroupUser deviceGroupUser = new DeviceGroupUser();
        deviceGroupUser.setDeviceGroup(deviceGroup);
        deviceGroupUser.setUser(userToAdd);
        deviceGroupUser.setRole(role);
        deviceGroupUserRepository.save(deviceGroupUser);

        return ResponseEntity.status(CREATED).body(new DeviceGroupUserDTO(deviceGroupUser));
    }

    /**
     * Edit Permissions
     * @param principal User-Details
     * @param deviceGroupId Associated DeviceGroup of the Permission
     * @param userId Associated User of the Permission
     * @param form changes for the Permission
     * @param bindingResult validationErrors
     * @return
     */
    @PatchMapping("/{user_id}")
    @Transactional
    public ResponseEntity<DeviceGroupUserDTO> updateDeviceGroupUser(@AuthenticationPrincipal Principal principal, @PathVariable("device_group_id") long deviceGroupId, @PathVariable("user_id") long userId, @RequestBody @Valid PatchDeviceGroupUserForm form, BindingResult bindingResult) {
        User authenticatedUser = userRepository.findByUsername(principal.getName()).orElseThrow();
        DeviceGroupUser deviceGroupUser = deviceGroupUserRepository.findDeviceGroupUserByIdDeviceGroupIdAndIdUserId(deviceGroupId, userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find DeviceGroupUser"));

        if (!deviceGroupUser.getDeviceGroup().userHasPermission(authenticatedUser, DeviceGroupRole.ADMIN)) {
            throw new AccessDeniedException(MISSING_ADMIN_PERMISSION);
        }

        DeviceGroupRole role = null;

        List<ObjectError> validationErrors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            validationErrors.addAll(bindingResult.getAllErrors());
        }
        // Validate and get role.
        if (!bindingResult.hasFieldErrors("role")) {
            switch (form.getRole()) {
                case "ADMIN":
                    role = DeviceGroupRole.ADMIN;
                    break;
                case "OBSERVER":
                    role = DeviceGroupRole.OBSERVER;
                    break;
                default:
                    validationErrors.add(new FieldError(form.getClass().getName(), "role", "Unknown role"));
            }
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

        deviceGroupUser.setRole(role);
        deviceGroupUserRepository.save(deviceGroupUser);

        return ResponseEntity.ok(new DeviceGroupUserDTO(deviceGroupUser));
    }

    /**
     * Delete Permission
     * @param principal User-Details
     * @param deviceGroupId Associated DeviceGroup of the Permission
     * @param userId Associated User of the Permission
     */
    @DeleteMapping("/{user_id}")
    @Transactional
    @ResponseStatus(OK)
    public void deleteDeviceGroupUser(@AuthenticationPrincipal Principal principal, @PathVariable("device_group_id") long deviceGroupId, @PathVariable("user_id") long userId) {
        User authenticatedUser = userRepository.findByUsername(principal.getName()).orElseThrow();
        DeviceGroupUser deviceGroupUser = deviceGroupUserRepository.findDeviceGroupUserByIdDeviceGroupIdAndIdUserId(deviceGroupId, userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find DeviceGroupUser"));

        if (!deviceGroupUser.getDeviceGroup().userHasPermission(authenticatedUser, DeviceGroupRole.ADMIN)) {
            throw new AccessDeniedException(MISSING_ADMIN_PERMISSION);
        }

        deviceGroupUserRepository.delete(deviceGroupUser);

    }

}