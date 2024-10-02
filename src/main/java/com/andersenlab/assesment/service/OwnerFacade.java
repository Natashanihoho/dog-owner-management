package com.andersenlab.assesment.service;

import com.andersenlab.assesment.dto.dog.CreateDogDto;
import com.andersenlab.assesment.dto.dog.DogDto;
import com.andersenlab.assesment.dto.owner.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OwnerFacade {

    private final OwnerService ownerService;
    private final KeycloakService keycloakService;
    private final DogService dogService;

    public OwnerDto createOwner(CreateOwnerDto createOwnerDto) {
        ownerService.verifyThatOwnerDoesNotExist(createOwnerDto.getEmail());
        keycloakService.registerOwner(createOwnerDto);
        return ownerService.createOwner(createOwnerDto);
    }

    public OwnerDto getOwner(Integer ownerId, UserInfoDto userInfoDto) {
        verifyPermission(ownerId, userInfoDto);
       return ownerService.getOwner(ownerId);
    }

    public Page<OwnerDto> getAllOwners(Pageable pageable) {
        return ownerService.getAllOwners(pageable);
    }

    public List<OwnerDto> searchOwnersByCriteria(OwnerFilter ownerFilter) {
        return ownerService.searchOwnersByCriteria(ownerFilter);
    }

    public OwnerDto updateOwner(Integer ownerId, PatchOwnerDto patchOwnerDto, UserInfoDto userInfoDto) {
        verifyPermission(ownerId, userInfoDto);
        return ownerService.updateOwner(ownerId, patchOwnerDto);
    }

    public void updateOwnerRoles(PatchRoleDto patchRoleDto) {
        patchRoleDto.operationType().getKeycloakConsumer().accept(keycloakService, patchRoleDto);
    }

    public void deleteOwner(Integer ownerId, UserInfoDto userInfoDto) {
        verifyPermission(ownerId, userInfoDto);
        ownerService.deleteOwner(ownerId);
    }

    public DogDto addDogToOwner(Integer ownerId, CreateDogDto createDogDto, UserInfoDto userInfoDto) {
        verifyPermission(ownerId, userInfoDto);
        return ownerService.getOwnerById(ownerId)
                .map(owner -> dogService.createDog(createDogDto, owner))
                .orElseThrow(() -> ownerService.resourceNotFoundException(ownerId));
    }

    private void verifyPermission(Integer ownerId, UserInfoDto userInfoDto) {
        if(userInfoDto.role() != Role.ADMIN) {
            ownerService.verifyOwnerConsistency(ownerId, userInfoDto.email());
        }
    }
}
