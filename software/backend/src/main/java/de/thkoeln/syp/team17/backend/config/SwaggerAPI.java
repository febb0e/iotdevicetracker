package de.thkoeln.syp.team17.backend.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerAPI {

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("Authentication")
                .pathsToMatch("/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi daemonApi() {
        return GroupedOpenApi.builder()
                .group("Daemon")
                .pathsToMatch("/devices/authenticate", "/devices/acl-check")
                .build();
    }

    @Bean
    public GroupedOpenApi deviceApi() {
        return GroupedOpenApi.builder()
                .group("Device")
                .pathsToMatch("/devices/**")
                .pathsToExclude("/devices/authenticate", "/devices/acl-check")
                .build();
    }

    @Bean
    public GroupedOpenApi deviceGroupApi() {
        return GroupedOpenApi.builder()
                .group("DeviceGroup")
                .pathsToMatch("/device-groups/**")
                .pathsToExclude("/device-groups/{device_group_id}/users/**")
                .build();
    }

    @Bean
    public GroupedOpenApi deviceGroupPermissionApi() {
        return GroupedOpenApi.builder()
                .group("DeviceGroupPermission")
                .pathsToMatch("/device-groups/{device_group_id}/users/**")
                .build();
    }

    @Bean
    public GroupedOpenApi totpApi() {
        return GroupedOpenApi.builder()
                .group("Totp")
                .pathsToMatch("/totp/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("User")
                .pathsToMatch("/users/**")
                .build();
    }

}