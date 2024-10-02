package com.andersenlab.assesment.rest.controller;

import com.andersenlab.assesment.dto.dog.CreateDogDto;
import com.andersenlab.assesment.dto.dog.DogDto;
import com.andersenlab.assesment.dto.owner.*;
import com.andersenlab.assesment.rest.validator.group.CreateValidation;
import com.andersenlab.assesment.service.OwnerFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(OwnerController.OWNERS_URL)
@RequiredArgsConstructor
@Validated
@Slf4j
public class OwnerController {

    static final String OWNERS_URL = "/v1/owners";
    private final OwnerFacade ownerFacade;

    @PostMapping
    @Validated(CreateValidation.class)
    public ResponseEntity<OwnerDto> createOwner(@RequestBody @Valid CreateOwnerDto createOwnerDto) {
        log.info("Create owner request [{}]", createOwnerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ownerFacade.createOwner(createOwnerDto));
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<OwnerDto> getOwner(@PathVariable Integer ownerId, @AuthenticationPrincipal Jwt jwt) {
        log.info("Get owner request with owner id [{}]", ownerId);
        return ResponseEntity.ok(ownerFacade.getOwner(ownerId, getUserInfoDto(jwt)));
    }

    @GetMapping
    public ResponseEntity<Page<OwnerDto>> getAllOwners(@PageableDefault Pageable pageable) {
        log.info("Get all owners request with pageable [{}]", pageable);
        return ResponseEntity.ok(ownerFacade.getAllOwners(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<List<OwnerDto>> searchOwnersByCriteria(OwnerFilter ownerFilter) {
        log.info("Search owners by criteria request with owner filter[{}]", ownerFilter);
        return ResponseEntity.ok(ownerFacade.searchOwnersByCriteria(ownerFilter));
    }

    @PatchMapping("/{ownerId}")
    public ResponseEntity<OwnerDto> updateOwner(@PathVariable Integer ownerId,
                                                @RequestBody @Valid PatchOwnerDto patchOwnerDto,
                                                @AuthenticationPrincipal Jwt jwt) {
        log.info("Update owner request with owner id [{}] and fields to update [{}]", ownerId, patchOwnerDto);
        return ResponseEntity.ok(ownerFacade.updateOwner(ownerId, patchOwnerDto, getUserInfoDto(jwt)));
    }

    @PatchMapping("/roles")
    public ResponseEntity<Void> updateOwnerRoles(@RequestBody @Valid PatchRoleDto patchOwnerDto) {
        log.info("Update owner roles request with role [{}]", patchOwnerDto.role());
        ownerFacade.updateOwnerRoles(patchOwnerDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{ownerId}")
    public ResponseEntity<Void> deleteOwner(@PathVariable Integer ownerId, @AuthenticationPrincipal Jwt jwt) {
        log.info("Delete owner request with owner id [{}]", ownerId);
        ownerFacade.deleteOwner(ownerId, getUserInfoDto(jwt));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{ownerId}/dogs")
    @Validated
    public ResponseEntity<DogDto> addDogToOwner(@PathVariable Integer ownerId,
                                                @RequestBody @Valid CreateDogDto createDogDto,
                                                @AuthenticationPrincipal Jwt jwt) {
        log.info("Add dog to owner with id [{}] request [{}]", ownerId, createDogDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ownerFacade.addDogToOwner(ownerId, createDogDto, getUserInfoDto(jwt)));
    }

    @SuppressWarnings("unchecked")
    private UserInfoDto getUserInfoDto(Jwt jwt) {
        String email = jwt.getClaim("email");
        List<String> roles = (List<String>) jwt.getClaimAsMap("realm_access").get("roles");
        return new UserInfoDto(email, roles.contains(Role.ADMIN.name()) ? Role.ADMIN : Role.USER);
    }
}
