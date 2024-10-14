package com.andersenlab.assesment.service;

import com.andersenlab.assesment.dto.breed.BreedDto;
import com.andersenlab.assesment.dto.breed.BreedFilter;
import com.andersenlab.assesment.dto.breed.BreedRequestDto;
import com.andersenlab.assesment.entity.Breed;
import com.andersenlab.assesment.entity.Breed_;
import com.andersenlab.assesment.exception.model.ErrorCode;
import com.andersenlab.assesment.exception.ResourceAlreadyExistsException;
import com.andersenlab.assesment.exception.ResourceNotFoundException;
import com.andersenlab.assesment.mapper.BreedMapper;
import com.andersenlab.assesment.repository.BreedRepository;
import com.andersenlab.assesment.repository.specification.ConditionSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BreedService {

    private final BreedRepository breedRepository;
    private final BreedMapper breedMapper;

    @Transactional
    public BreedDto createBreed(BreedRequestDto breedRequestDto) {
        checkIfBreedAlreadyExists(breedRequestDto.getBreedName());
        Breed breed = breedMapper.mapToBreed(breedRequestDto);
        Breed savedBreed = breedRepository.save(breed);
        return breedMapper.mapToBreedDto(savedBreed);
    }

    @Transactional(readOnly = true)
    public BreedDto getBreed(Integer breedId) {
        return breedMapper.mapToBreedDto(getBreedById(breedId));
    }

    @Transactional(readOnly = true)
    public Page<BreedDto> getAllBreeds(Pageable pageable) {
        return breedRepository.findAll(pageable)
                .map(breedMapper::mapToBreedDto);
    }

    @Transactional(readOnly = true)
    public List<BreedDto> searchBreedsByCriteria(BreedFilter breedFilter) {
        return breedRepository.findAll(
                        new ConditionSpecification<>(
                                breedFilter.breedName(), StringUtils::isNotBlank, Breed_.breedName
                        ).and(
                                new ConditionSpecification<>(breedFilter.averageLifeExpectancy(), Objects::nonNull, Breed_.averageLifeExpectancy)
                        ).and(
                                new ConditionSpecification<>(breedFilter.originCountry(), StringUtils::isNotBlank, Breed_.originCountry)
                        ).and(
                                new ConditionSpecification<>(breedFilter.easyToTrain(), Objects::nonNull, Breed_.easyToTrain)
                        )
                ).stream()
                .map(breedMapper::mapToBreedDto)
                .toList();
    }

    @Transactional
    public BreedDto updateBreed(Integer breedId, BreedRequestDto breedRequestDto) {
        Breed updatedBreed = breedMapper.updateBreed(getBreedById(breedId), breedRequestDto);
        return breedMapper.mapToBreedDto(updatedBreed);
    }

    @Transactional
    public void deleteBreed(Integer breedId) {
        breedRepository.delete(getBreedById(breedId));
    }

    private Breed getBreedById(Integer breedId) {
        return breedRepository.findById(breedId)
                .orElseThrow(() -> new ResourceNotFoundException(
                                ErrorCode.ERR004, "Breed with id [" + breedId + "] not found", HttpStatus.NOT_FOUND
                        )
                );
    }

    private void checkIfBreedAlreadyExists(String breedName) {
        if (breedRepository.existsByBreedNameIgnoreCase(breedName)) {
            throw new ResourceAlreadyExistsException(
                    ErrorCode.ERR005, "Breed [" + breedName + "] already exists", HttpStatus.BAD_REQUEST
            );
        }
    }
}
