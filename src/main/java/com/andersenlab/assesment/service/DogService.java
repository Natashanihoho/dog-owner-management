package com.andersenlab.assesment.service;

import com.andersenlab.assesment.dto.dog.CreateDogDto;
import com.andersenlab.assesment.dto.dog.DogDto;
import com.andersenlab.assesment.dto.dog.DogFilter;
import com.andersenlab.assesment.dto.dog.PatchDogDto;
import com.andersenlab.assesment.entity.Dog;
import com.andersenlab.assesment.entity.Dog_;
import com.andersenlab.assesment.entity.Owner;
import com.andersenlab.assesment.entity.Owner_;
import com.andersenlab.assesment.exception.ResourceNotFoundException;
import com.andersenlab.assesment.exception.model.ErrorCode;
import com.andersenlab.assesment.mapper.DogMapper;
import com.andersenlab.assesment.repository.BreedRepository;
import com.andersenlab.assesment.repository.DogRepository;
import com.andersenlab.assesment.repository.specification.ConditionSpecification;
import com.andersenlab.assesment.repository.specification.JoinSpecification;
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
    private final BreedRepository breedRepository;
    private final DogMapper dogMapper;

    @Transactional
    public DogDto createDog(CreateDogDto createDogDto, Owner owner) {
        Dog dog = dogRepository.save(dogMapper.mapToDog(createDogDto));
        dog.setOwner(owner);
        breedRepository.findByBreedName(createDogDto.getBreed())
                .ifPresentOrElse(
                        dog::setBreed,
                        () -> { throw new ResourceNotFoundException(
                                ErrorCode.ERR004, "Breed [" + createDogDto.getBreed() + "] not found", HttpStatus.NOT_FOUND
                        );}
                );
        return dogMapper.mapToDogDto(dog);
    }

    public DogDto getDog(Integer dogId) {
        return dogMapper.mapToDogDto(getDogById(dogId));
    }

    public Page<DogDto> getAllDogs(Pageable pageable) {
        return dogRepository.findAll(pageable)
                .map(dogMapper::mapToDogDto);
    }

    public Page<DogDto> getAllDogsForOwner(String email, Pageable pageable) {
        return dogRepository.findAllByOwner_Email(email, pageable)
                .map(dogMapper::mapToDogDto);
    }

    public List<DogDto> searchDogsByCriteria(DogFilter dogFilter) {
        return dogRepository.findAll(
                        new ConditionSpecification<>(
                                dogFilter.name(), StringUtils::isNotBlank, Dog_.name
                        ).and(
                                new ConditionSpecification<>(dogFilter.dateOfBirth(), Objects::nonNull, Dog_.dateOfBirth)
                        ).and(
                                new JoinSpecification<>(dogFilter.ownerId(), Dog_.owner, Owner_.id)
                        )
                ).stream()
                .map(dogMapper::mapToDogDto)
                .toList();
    }

    @Transactional
    public DogDto updateDog(Integer dogId, PatchDogDto patchDogDto) {
        Dog updatedDog = dogMapper.updateDog(getDogById(dogId), patchDogDto);
        return dogMapper.mapToDogDto(updatedDog);
    }

    @Transactional
    public void deleteDog(Integer dogId) {
        dogRepository.delete(getDogById(dogId));
    }

    Dog getDogById(Integer dogId) {
        return dogRepository.findById(dogId)
                .orElseThrow(() -> new ResourceNotFoundException(
                                ErrorCode.ERR004, "Dog with id [" + dogId + "] not found", HttpStatus.NOT_FOUND
                        )
                );
    }
}
