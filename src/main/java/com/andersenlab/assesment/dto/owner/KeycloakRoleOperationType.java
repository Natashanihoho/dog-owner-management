package com.andersenlab.assesment.dto.owner;

import com.andersenlab.assesment.service.KeycloakService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiConsumer;

@Getter
@RequiredArgsConstructor
public enum KeycloakRoleOperationType {

    ADD((keycloak, dto) -> keycloak.assignRole(dto.email(), dto.role().name())),
    DELETE((keycloak, dto) -> keycloak.deleteRole(dto.email(), dto.role().name()));

    private final BiConsumer<KeycloakService, PatchRoleDto> keycloakConsumer;
}
