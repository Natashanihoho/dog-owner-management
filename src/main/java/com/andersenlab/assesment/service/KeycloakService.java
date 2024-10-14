package com.andersenlab.assesment.service;

import com.andersenlab.assesment.dto.owner.CreateOwnerDto;
import com.andersenlab.assesment.dto.owner.Role;
import com.andersenlab.assesment.exception.KeycloakOperationFailedException;
import com.andersenlab.assesment.exception.ResourceNotFoundException;
import com.andersenlab.assesment.exception.model.ErrorCode;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class KeycloakService {

    private final String realm;
    private final Keycloak keycloak;

    public KeycloakService(@Value("${app.keycloak.realm}") String realm, Keycloak keycloak) {
        this.realm = realm;
        this.keycloak = keycloak;
    }

    public void registerOwner(CreateOwnerDto createOwnerDto) {
        UsersResource usersResource = getUsersResource();
        try (Response response = usersResource.create(buildUserRepresentation(createOwnerDto))) {
            log.debug("Keycloak status code [{}]", response.getStatus());
            assignDefaultRole(response);
            log.debug("New user has been created");
        } catch (WebApplicationException e) {
            int status = e.getResponse().getStatus();
            log.error("Registration failed with status code [{}]", status);
            throw new KeycloakOperationFailedException(ErrorCode.ERR007, ErrorCode.ERR007.getMessage(), HttpStatus.valueOf(status));
        } catch (Exception e) {
            log.error("Registration failed due to a system error: [{}]", e.getMessage());
            throw new KeycloakOperationFailedException(ErrorCode.ERR007, ErrorCode.ERR007.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void assignRole(String email, String roleName) {
        RoleRepresentation roleToAdd = getRoleRepresentation(roleName);
        getUserByEmail(email).ifPresentOrElse(
                user -> getUserRoles(user.getId()).add(Collections.singletonList(roleToAdd)),
                () -> {
                    throw new ResourceNotFoundException(ErrorCode.ERR004, ErrorCode.ERR004.getMessage(), HttpStatus.NOT_FOUND);
                }
        );
    }

    public void deleteRole(String email, String roleName) {
        RoleRepresentation roleToRemove = getRoleRepresentation(roleName);
        getUserByEmail(email).ifPresentOrElse(
                user -> getUserRoles(user.getId()).remove(Collections.singletonList(roleToRemove)),
                () -> {
                    throw new ResourceNotFoundException(ErrorCode.ERR004, ErrorCode.ERR004.getMessage(), HttpStatus.NOT_FOUND);
                }
        );
    }

    public void deleteUser(String username) {
        UsersResource usersResource = getUsersResource();
        String userId = getUserId(username, usersResource);
        try (Response response = usersResource.delete(userId)) {
            log.debug("The user [{}] has been deleted. Response status [{}]", userId, response.getStatus());
        } catch (Exception e) {
            log.error("Deletion failed due to a system error: [{}]", e.getMessage());
            throw new KeycloakOperationFailedException(ErrorCode.ERR010, ErrorCode.ERR010.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private UserRepresentation buildUserRepresentation(CreateOwnerDto createOwnerDto) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setFirstName(createOwnerDto.getFirstName());
        userRepresentation.setLastName(createOwnerDto.getLastName());
        userRepresentation.setUsername(createOwnerDto.getEmail());
        userRepresentation.setEmail(createOwnerDto.getEmail());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setCredentials(buildCredentialRepresentation(createOwnerDto.getPassword()));
        return userRepresentation;
    }

    private List<CredentialRepresentation> buildCredentialRepresentation(String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(password);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        return List.of(credentialRepresentation);
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

    private RoleScopeResource getUserRoles(String userId) {
        return getUsersResource().get(userId).roles().realmLevel();
    }

    private RoleRepresentation getRoleRepresentation(String roleName) {
        return keycloak.realm(realm)
                .roles()
                .get(roleName)
                .toRepresentation();
    }

    private void assignDefaultRole(Response response) {
        String roleName = Role.USER.name();
        RoleRepresentation roleRepresentation = getRoleRepresentation(roleName);
        log.debug("Role representation [{}]", roleRepresentation);
        String userId = CreatedResponseUtil.getCreatedId(response);
        UserResource userResource = getUsersResource().get(userId);
        userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));
    }

    private String getUserId(String username, UsersResource usersResource) {
        List<UserRepresentation> users = usersResource.search(username, true);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException(ErrorCode.ERR004, ErrorCode.ERR004.getMessage(), HttpStatus.NOT_FOUND);
        }
        return users.getFirst().getId();
    }

    private Optional<UserRepresentation> getUserByEmail(String username) {
        return Optional.ofNullable(getUsersResource().search(username).getFirst());
    }
}
