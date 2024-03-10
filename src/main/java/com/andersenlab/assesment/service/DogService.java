package com.andersenlab.assesment.service;

import com.andersenlab.assesment.dto.*;
import com.andersenlab.assesment.entity.Dog;
import com.andersenlab.assesment.entity.Dog_;
import com.andersenlab.assesment.exception.model.ErrorCode;
import com.andersenlab.assesment.exception.ResourceAlreadyExistsException;
import com.andersenlab.assesment.exception.ResourceNotFoundException;
import com.andersenlab.assesment.mapper.DogMapper;
import com.andersenlab.assesment.repository.DogRepository;
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
public class DogService {

    private final DogRepository dogRepository;
    private final DogMapper dogMapper;

    @Transactional
    public DogDto createDog(DogRequestDto dogRequestDto) {
        checkIfDogAlreadyExists(dogRequestDto.getBreed());
        Dog dog = dogMapper.mapToDog(dogRequestDto);
        Dog savedDog = dogRepository.save(dog);
        return dogMapper.mapToDogDto(savedDog);
    }

    @Transactional(readOnly = true)
    public DogDto getDog(Integer dogId) {
        return dogMapper.mapToDogDto(getDogById(dogId));
    }

    @Transactional(readOnly = true)
    public Page<DogDto> getAllDogs(Pageable pageable) {
        return dogRepository.findAll(pageable)
                .map(dogMapper::mapToDogDto);
    }

    @Transactional(readOnly = true)
    public List<DogDto> searchDogsByCriteria(DogFilter dogFilter) {
        return dogRepository.findAll(
                        new ConditionSpecification<>(
                                dogFilter.breed(), StringUtils::isNotBlank, Dog_.breed
                        ).and(
                                new ConditionSpecification<>(dogFilter.averageLifeExpectancy(), Objects::nonNull, Dog_.averageLifeExpectancy)
                        ).and(
                                new ConditionSpecification<>(dogFilter.originCountry(), StringUtils::isNotBlank, Dog_.originCountry)
                        ).and(
                                new ConditionSpecification<>(dogFilter.easyToTrain(), Objects::nonNull, Dog_.easyToTrain)
                        )
                ).stream()
                .map(dogMapper::mapToDogDto)
                .toList();
    }

    @Transactional
    public DogDto updateDog(Integer dogId, DogRequestDto dogRequestDto) {
        Dog updatedDog = dogMapper.updateDog(getDogById(dogId), dogRequestDto);
        return dogMapper.mapToDogDto(updatedDog);
    }

    @Transactional
    public void deleteDog(Integer dogId) {
        dogRepository.delete(getDogById(dogId));
    }

    private Dog getDogById(Integer dogId) {
        return dogRepository.findById(dogId)
                .orElseThrow(() -> new ResourceNotFoundException(
                                ErrorCode.ERR004, "Dog with id [" + dogId + "] not found", HttpStatus.NOT_FOUND
                        )
                );
    }

    private void checkIfDogAlreadyExists(String breed) {
        if (dogRepository.existsByBreedIgnoreCase(breed)) {
            throw new ResourceAlreadyExistsException(
                    ErrorCode.ERR005, "Dog with breed [" + breed + "] already exists", HttpStatus.BAD_REQUEST
            );
        }
    }
}
