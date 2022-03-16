package de.thkoeln.syp.team17.backend.controllers;

import de.thkoeln.syp.team17.backend.dto.*;
import de.thkoeln.syp.team17.backend.entities.Device;
import de.thkoeln.syp.team17.backend.entities.DeviceGroup;
import de.thkoeln.syp.team17.backend.entities.User;
import de.thkoeln.syp.team17.backend.entities.enums.DeviceGroupRole;
import de.thkoeln.syp.team17.backend.entities.enums.UserRole;
import de.thkoeln.syp.team17.backend.exceptions.ValidationException;
import de.thkoeln.syp.team17.backend.forms.CreateDeviceForm;
import de.thkoeln.syp.team17.backend.forms.PatchDeviceForm;
import de.thkoeln.syp.team17.backend.repositories.DeviceGroupRepository;
import de.thkoeln.syp.team17.backend.repositories.DeviceRepository;
import de.thkoeln.syp.team17.backend.repositories.UserRepository;
import de.thkoeln.syp.team17.backend.services.DeviceMetricsService;
import de.thkoeln.syp.team17.backend.services.DeviceMultiQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceController {

    private static final String ERROR_RESPONSE_DEVICE = "Unable to find Device with id:";

    private static final String ERROR_MISSING_ACCESS_DEVICE = "You don't have access to this device.";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceGroupRepository deviceGroupRepository;

    @Autowired
    private DeviceMetricsService deviceMetricsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get a list of devices
     * @param principal User-details
     * @param deviceGroupId The ID of the DeviceGroup that owns devices, if omitted all devices will be returned
     * @param page Determines which part of devices you receive if the amount exceeds 25, when omitted a value of 1 is assumed
     * @return A list of Devices split into pages of size of 25 per page
     */
    @GetMapping
    public ResponseEntity<DeviceListDTO> getDevices(@AuthenticationPrincipal Principal principal, @RequestParam(required = false) Long deviceGroupId,  @RequestParam(required = false) Integer page) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        if (deviceGroupId == null) {
            if (user.getEffectiveRole() != UserRole.SYSADMIN) {
                throw new AccessDeniedException("You are not allowed to view all devices.");
            }
            if (page == null) page = 1;

            Pageable pageable = PageRequest.of(page - 1, 25);
            Page<Device> devicePage = deviceRepository.findAll(pageable);
            return ResponseEntity.ok(new DeviceListDTO(devicePage));
        }

        DeviceGroup deviceGroup = deviceGroupRepository.findById(deviceGroupId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find DeviceGroup with id:" + deviceGroupId));

        if (!deviceGroup.userHasPermission(user)) {
            throw new AccessDeniedException("You don't have access to this device group");
        }

        if (page == null) page = 1;

        Pageable pageable = PageRequest.of(page - 1, 25);
        Page<Device> devicePage = new PageImpl<>(deviceGroup.getDevices(), pageable , deviceGroup.getDevices().size());

        return ResponseEntity.ok(new DeviceListDTO(devicePage));
    }

    /**
     * Create Device / Add Device to DeviceGroup
     * @param principal User-details
     * @param form Details about the Device
     * @param bindingResult validationErrors
     * @return Created Device with a token which will be only persisted in a hashed state in the database
     */

    @PostMapping
    @Transactional
    public ResponseEntity<DeviceWithTokenDTO> createDevice(@AuthenticationPrincipal Principal principal, @RequestBody @Valid CreateDeviceForm form, BindingResult bindingResult) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Optional<DeviceGroup> deviceGroup = Optional.empty();

        List<ObjectError> validationErrors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            validationErrors.addAll(bindingResult.getAllErrors());
        }
        // Fetch device group
        if (!bindingResult.hasFieldErrors("deviceGroupId")) {
            deviceGroup = deviceGroupRepository.findById(form.getDeviceGroupId());
        }
        if (deviceGroup.isEmpty()) {
            validationErrors.add(new FieldError(form.getClass().getName(), "deviceGroupId", "does not exist"));
        }
        // Check if identifier address is already in use
        Optional<Device> existingDevice = deviceRepository.findByIdentifier(form.getIdentifier());
        if (existingDevice.isPresent()) {
            validationErrors.add(new FieldError(form.getClass().getName(), "identifier", "already exists"));
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }
        if (!deviceGroup.orElseThrow().userHasPermission(user, DeviceGroupRole.ADMIN)) {
            throw new AccessDeniedException("You don't have admin access to this device group.");
        }

        String token = UUID.randomUUID().toString();

        // Create device
        Device device = new Device();
        device.setDeviceGroup(deviceGroup.orElseThrow());
        device.setName(form.getName());
        device.setIdentifier(form.getIdentifier());
        device.setToken(passwordEncoder.encode(token));
        deviceRepository.save(device);

        return ResponseEntity.status(CREATED).body(new DeviceWithTokenDTO(device, deviceGroup.orElseThrow().getUserRole(user), token));
    }

    /**
     * Get Device
     * @param principal User-details
     * @param deviceId Internal ID of the Device
     * @return Device
     */
    @GetMapping("/{device_id}")
    public ResponseEntity<DeviceWithGroupDTO> getDevice(@AuthenticationPrincipal Principal principal, @PathVariable("device_id") long deviceId) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, ERROR_RESPONSE_DEVICE + deviceId));

        if (!device.getDeviceGroup().userHasPermission(user)) {
            throw new AccessDeniedException(ERROR_MISSING_ACCESS_DEVICE);
        }

        return ResponseEntity.ok(new DeviceWithGroupDTO(device, device.getDeviceGroup().getUserRole(user)));
    }

    /**
     * Edit Device
     * @param principal User-details
     * @param form Changes for the Device
     * @param deviceId Internal ID of the Device
     * @param bindingResult validationErrors
     * @return Updated Device
     */
    @PatchMapping("/{device_id}")
    @Transactional
    public ResponseEntity<DeviceWithGroupDTO> patchDevice(@AuthenticationPrincipal Principal principal, @RequestBody @Valid PatchDeviceForm form, @PathVariable("device_id") long deviceId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors());
        }

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, ERROR_RESPONSE_DEVICE + deviceId));

        if (!device.getDeviceGroup().userHasPermission(user, DeviceGroupRole.ADMIN)) {
            throw new AccessDeniedException("You don't have admin access to this device.");
        }

        device.setName(form.getName());
        deviceRepository.save(device);

        return ResponseEntity.ok(new DeviceWithGroupDTO(device, device.getDeviceGroup().getUserRole(user)));
    }

    /**
     * Delete Device / Remove from DeviceGroup
     * @param principal User-details
     * @param deviceId Internal ID of the Device
     */
    @DeleteMapping("/{device_id}")
    @Transactional
    @ResponseStatus(OK)
    public void deleteDevice(@AuthenticationPrincipal Principal principal, @PathVariable("device_id") long deviceId) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, ERROR_RESPONSE_DEVICE + deviceId));

        if (!device.getDeviceGroup().userHasPermission(user, DeviceGroupRole.ADMIN)) {
            throw new AccessDeniedException("You don't have admin access to this device.");
        }

        deviceRepository.delete(device);
    }

    /**
     * Get names/fields of metrics
     * @param principal User-details
     * @param deviceId Internal ID of the Device
     * @param startTime Beginning of the Timeframe to search in
     * @param stopTime Ending of the Timeframe to search in, if omitted current Time is taken
     * @return List of MetricNames
     */
    @GetMapping("/{device_id}/metrics-fields")
    public ResponseEntity<List<String>> getDeviceMetricFields(@AuthenticationPrincipal Principal principal, @PathVariable("device_id") long deviceId,
                                                              @RequestParam(name = "start") String startTime,
                                                              @RequestParam(name = "stop", required = false) String stopTime) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, ERROR_RESPONSE_DEVICE + deviceId));

        if (!device.getDeviceGroup().userHasPermission(user)) {
            throw new AccessDeniedException(ERROR_MISSING_ACCESS_DEVICE);
        }

        DeviceMultiQuery deviceMultiQuery = DeviceMultiQuery.builder()
                .start(startTime)
                .stop(stopTime)
                .identifier(device.getIdentifier())
                .build();

        List<String> metricFields = deviceMetricsService.getFieldsOf(deviceMultiQuery);

        return ResponseEntity.ok(metricFields);
    }

    /**
     * Get values from Metrics
     * @param principal User-details
     * @param deviceId Internal ID of the Device
     * @param fields Names of the desired Metrics
     * @param startTime Beginning of the Timeframe to search in
     * @param stopTime Ending of the Timeframe to search in, if omitted current Time is taken
     * @param interval Interval of the aggregation of metrics, if omitted a value of 1m will be used
     * @param aggregation Function for aggregation, i.e. mean;median;sum;
     * @return A list with metrics containing the metrics value and a Timestamp
     */
    @GetMapping("/{device_id}/metrics")
    public ResponseEntity<List<MultiValueDTO>> getDeviceMetrics(@AuthenticationPrincipal Principal principal, @PathVariable("device_id") long deviceId,
                                        @RequestParam(name = "field") List<String> fields,
                                        @RequestParam(name = "start") String startTime,
                                        @RequestParam(name = "stop", required = false) String stopTime,
                                        @RequestParam(name = "interval", required = false) String interval,
                                        @RequestParam(name = "aggregation", required = false) String aggregation) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, ERROR_RESPONSE_DEVICE + deviceId));

        if (!device.getDeviceGroup().userHasPermission(user)) {
            throw new AccessDeniedException(ERROR_MISSING_ACCESS_DEVICE);
        }

        DeviceMultiQuery deviceMultiQuery = DeviceMultiQuery.builder()
                .start(startTime)
                .stop(stopTime)
                .identifier(device.getIdentifier())
                .fields(fields)
                .interval(interval)
                .aggregationMethod(aggregation)
                .build();

        List<MultiValueDTO> metrics = deviceMetricsService.getMetricsOf(deviceMultiQuery);

        return ResponseEntity.ok(metrics);
    }

    /**
     * Authentication for Mosquitto Go Auth (to authenticate DaemonDevices)
     * @param authData Authentication details
     */
    @PostMapping("/authenticate")
    @ResponseStatus(OK)
    public void authenticateDevice(@RequestBody DeviceAuthenticationDTO authData) {
        Device device = deviceRepository.findByIdentifier(authData.getUsername())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Unable to find Device with identifier:" + authData.getUsername()));

        if (!passwordEncoder.matches(authData.getPassword(), device.getToken())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid password");
        }
    }

    /**
     * AccessControlList for Mosquitto Go Auth
     * @param aclData ACL details
     */
    @PostMapping("/acl-check")
    @ResponseStatus(OK)
    public void checkACL(@RequestBody DeviceACLCheckDTO aclData) {
        if (!(aclData.getAcc() == 2 && aclData.getTopic().equals("daemon/metrics")))
            throw new AccessDeniedException("You are only allowed to write to daemon/metrics");
    }

}
