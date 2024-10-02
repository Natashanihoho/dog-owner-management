package com.andersenlab.assesment.rest.controller;

import com.andersenlab.assesment.dto.dog.DogDto;
import com.andersenlab.assesment.dto.dog.DogFilter;
import com.andersenlab.assesment.dto.dog.PatchDogDto;
import com.andersenlab.assesment.dto.owner.Role;
import com.andersenlab.assesment.dto.owner.UserInfoDto;
import com.andersenlab.assesment.rest.validator.group.CreateValidation;
import com.andersenlab.assesment.service.DogFacade;
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
@RequestMapping(DogController.DOGS_URL)
@RequiredArgsConstructor
@Validated
@Slf4j
public class DogController {

    static final String DOGS_URL = "/v1/dogs";

    private final DogFacade dogFacade;

    @GetMapping("/{dogId}")
    @Validated(CreateValidation.class)
    public ResponseEntity<DogDto> getDog(@PathVariable Integer dogId, @AuthenticationPrincipal Jwt jwt) {
        log.info("Get dog request with id [{}]", dogId);
        return ResponseEntity.ok(dogFacade.getDog(dogId, getUserInfoDto(jwt)));
    }

    @GetMapping
    public ResponseEntity<Page<DogDto>> getAllDogs(@PageableDefault Pageable pageable, @AuthenticationPrincipal Jwt jwt) {
        log.info("Get all dogs request with pageable [{}]", pageable);
        return ResponseEntity.ok(dogFacade.getAllDogs(pageable, getUserInfoDto(jwt)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DogDto>> searchDogsByCriteria(DogFilter dogFilter) {
        log.info("Search dogs by criteria request with dog filter[{}]", dogFilter);
        return ResponseEntity.ok(dogFacade.searchDogsByCriteria(dogFilter));
    }

    @PatchMapping("/{dogId}")
    public ResponseEntity<DogDto> updateDog(@PathVariable Integer dogId,
                                            @RequestBody PatchDogDto patchDogDto,
                                            @AuthenticationPrincipal Jwt jwt) {
        log.info("Update dog request with dog id [{}] and fields to update [{}]", dogId, patchDogDto);
        return ResponseEntity.ok(dogFacade.updateDog(dogId, patchDogDto, getUserInfoDto(jwt)));
    }

    @DeleteMapping("/{dogId}")
    public ResponseEntity<Void> deleteDog(@PathVariable Integer dogId, @AuthenticationPrincipal Jwt jwt) {
        dogFacade.deleteDog(dogId, getUserInfoDto(jwt));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @SuppressWarnings("unchecked")
    private UserInfoDto getUserInfoDto(Jwt jwt) {
        String email = jwt.getClaim("email");
        List<String> roles = (List<String>) jwt.getClaimAsMap("realm_access").get("roles");
        return new UserInfoDto(email, roles.contains(Role.ADMIN.name()) ? Role.ADMIN : Role.USER);
    }
}
