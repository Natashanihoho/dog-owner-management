package com.andersenlab.assesment.rest.controller;

import com.andersenlab.assesment.dto.DogDto;
import com.andersenlab.assesment.dto.DogFilter;
import com.andersenlab.assesment.dto.DogRequestDto;
import com.andersenlab.assesment.service.DogService;
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
@RequestMapping(DogController.DOGS_URL)
@RequiredArgsConstructor
@Validated
public class DogController {

    static final String DOGS_URL = "/v1/dogs";

    private final DogService dogService;

    @PostMapping
    public ResponseEntity<DogDto> createDog(@RequestBody @Valid DogRequestDto dogRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dogService.createDog(dogRequestDto));
    }

    @GetMapping("/{dogId}")
    public ResponseEntity<DogDto> getDog(@PathVariable Integer dogId) {
        return ResponseEntity.ok(dogService.getDog(dogId));
    }

    @GetMapping
    public ResponseEntity<Page<DogDto>> findAllDogs(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(dogService.getAllDogs(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DogDto>> searchDogsByCriteria(DogFilter dogFilter) {
        return ResponseEntity.ok(dogService.searchDogsByCriteria(dogFilter));
    }

    @PatchMapping("/{dogId}")
    public ResponseEntity<DogDto> updateDog(@PathVariable Integer dogId,
                                            @RequestBody DogRequestDto dogRequestDto) {
        return ResponseEntity.ok(dogService.updateDog(dogId, dogRequestDto));
    }

    @DeleteMapping("/{dogId}")
    public ResponseEntity<Void> deleteDog(@PathVariable Integer dogId) {
        dogService.deleteDog(dogId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
