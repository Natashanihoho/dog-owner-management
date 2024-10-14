package com.andersenlab.assesment.rest.controller;

import com.andersenlab.assesment.dto.breed.BreedDto;
import com.andersenlab.assesment.dto.breed.BreedFilter;
import com.andersenlab.assesment.dto.breed.BreedRequestDto;
import com.andersenlab.assesment.rest.validator.group.CreateValidation;
import com.andersenlab.assesment.service.BreedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(BreedController.BREEDS_URL)
@RequiredArgsConstructor
@Validated
@Slf4j
public class BreedController {

    static final String BREEDS_URL = "/v1/breeds";

    private final BreedService breedService;

    @PostMapping
    @Validated(CreateValidation.class)
    public ResponseEntity<BreedDto> createBreed(@RequestBody @Valid BreedRequestDto breedRequestDto) {
        log.info("Create breed request [{}]", breedRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(breedService.createBreed(breedRequestDto));
    }

    @GetMapping("/{breedId}")
    public ResponseEntity<BreedDto> getBreed(@PathVariable Integer breedId) {
        log.info("Get breed request with id [{}]", breedId);
        return ResponseEntity.ok(breedService.getBreed(breedId));
    }

    @GetMapping
    public ResponseEntity<Page<BreedDto>> getAllBreeds(@PageableDefault Pageable pageable) {
        log.info("Get all breeds request with pageable[{}]", pageable);
        return ResponseEntity.ok(breedService.getAllBreeds(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BreedDto>> searchBreedsByCriteria(BreedFilter breedFilter) {
        log.info("Search breeds by criteria request with breed filter[{}]", breedFilter);
        return ResponseEntity.ok(breedService.searchBreedsByCriteria(breedFilter));
    }

    @PatchMapping("/{breedId}")
    public ResponseEntity<BreedDto> updateBreed(@PathVariable Integer breedId,
                                                @RequestBody BreedRequestDto breedRequestDto) {
        log.info("Update breed request with breed id [{}] and fields to update [{}]", breedId, breedRequestDto);
        return ResponseEntity.ok(breedService.updateBreed(breedId, breedRequestDto));
    }

    @DeleteMapping("/{breedId}")
    public ResponseEntity<Void> deleteBreed(@PathVariable Integer breedId) {
        log.info("Delete breed request with id [{}]", breedId);
        breedService.deleteBreed(breedId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
