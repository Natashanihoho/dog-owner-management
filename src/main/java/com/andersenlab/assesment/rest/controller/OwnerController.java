package com.andersenlab.assesment.rest.controller;

import com.andersenlab.assesment.dto.OwnerDogLinkDto;
import com.andersenlab.assesment.dto.OwnerDto;
import com.andersenlab.assesment.dto.OwnerFilter;
import com.andersenlab.assesment.dto.OwnerRequestDto;
import com.andersenlab.assesment.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(OwnerController.OWNERS_URL)
@RequiredArgsConstructor
@Validated
public class OwnerController {

    static final String OWNERS_URL = "/v1/owners";

    private final OwnerService ownerService;

    @PostMapping
    public ResponseEntity<OwnerDto> createOwner(@RequestBody @Valid OwnerRequestDto ownerRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ownerService.createOwner(ownerRequestDto));
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<OwnerDto> getOwner(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(ownerService.getOwner(ownerId));
    }

    @GetMapping
    public ResponseEntity<Page<OwnerDto>> findAllOwners(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(ownerService.getAllOwners(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<List<OwnerDto>> searchOwnersByCriteria(OwnerFilter ownerFilter) {
        return ResponseEntity.ok(ownerService.searchOwnersByCriteria(ownerFilter));
    }

    @PatchMapping("/{ownerId}")
    public ResponseEntity<OwnerDto> updateOwner(@PathVariable Integer ownerId,
                                                @RequestBody @Valid OwnerRequestDto ownerRequestDto) {
        return ResponseEntity.ok(ownerService.updateOwner(ownerId, ownerRequestDto));
    }

    @PatchMapping("/{ownerId}/dogs")
    public ResponseEntity<OwnerDto> addDogsToOwner(@PathVariable Integer ownerId,
                                                   @RequestBody OwnerDogLinkDto ownerDogLinkDto) {
        return ResponseEntity.ok(ownerService.addDogsToOwner(ownerId, ownerDogLinkDto));
    }

    @DeleteMapping("/{ownerId}")
    public ResponseEntity<Void> deleteOwner(@PathVariable Integer ownerId) {
        ownerService.deleteOwner(ownerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{ownerId}/dogs")
    public ResponseEntity<Void> removeDogsFromOwner(@PathVariable Integer ownerId,
                                                    @RequestParam List<String> breeds) {
        ownerService.removeDogsFromOwner(ownerId, breeds);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
