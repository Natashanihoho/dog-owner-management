package com.andersenlab.assesment.dto.owner;

import com.andersenlab.assesment.exception.model.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PatchRoleDto(@NotBlank(message = ErrorMessage.ERR002_MESSAGE) String email,
                           @NotNull(message = ErrorMessage.ERR002_MESSAGE) Role role,
                           @NotNull(message = ErrorMessage.ERR002_MESSAGE) KeycloakRoleOperationType operationType) {
}
