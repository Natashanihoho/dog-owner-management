package com.andersenlab.assesment.service;

import com.andersenlab.assesment.dto.OwnerDogLinkDto;
import com.andersenlab.assesment.dto.OwnerDto;
import com.andersenlab.assesment.dto.OwnerFilter;
import com.andersenlab.assesment.dto.OwnerRequestDto;
import com.andersenlab.assesment.entity.Dog;
import com.andersenlab.assesment.entity.Owner;
import com.andersenlab.assesment.entity.Owner_;
import com.andersenlab.assesment.exception.model.ErrorCode;
import com.andersenlab.assesment.exception.ResourceNotFoundException;
import com.andersenlab.assesment.mapper.OwnerMapper;
import com.andersenlab.assesment.repository.DogRepository;
import com.andersenlab.assesment.repository.OwnerRepository;
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
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final DogRepository dogRepository;
    private final OwnerMapper ownerMapper;

    @Transactional
    public OwnerDto createOwner(OwnerRequestDto ownerRequestDto) {
        Owner owner = ownerMapper.mapToOwner(ownerRequestDto);
        Owner savedOwner = ownerRepository.save(owner);
        return ownerMapper.mapToOwnerDto(savedOwner);
    }

    @Transactional(readOnly = true)
    public OwnerDto getOwner(Integer ownerId) {
        return ownerMapper.mapToOwnerDto(getOwnerById(ownerId));
    }

    @Transactional(readOnly = true)
    public Page<OwnerDto> getAllOwners(Pageable pageable) {
        return ownerRepository.findAll(pageable)
                .map(ownerMapper::mapToOwnerDto);
    }

    @Transactional(readOnly = true)
    public List<OwnerDto> searchOwnersByCriteria(OwnerFilter ownerFilter) {
        return ownerRepository.findAll(
                        new ConditionSpecification<>(
                                ownerFilter.age(), Objects::nonNull, Owner_.age
                        ).and(
                                new ConditionSpecification<>(ownerFilter.firstName(), StringUtils::isNotBlank, Owner_.firstName)
                        ).and(
                                new ConditionSpecification<>(ownerFilter.lastName(), StringUtils::isNotBlank, Owner_.lastName)
                        ).and(
                                new ConditionSpecification<>(ownerFilter.city(), StringUtils::isNotBlank, Owner_.city)
                        )
                ).stream()
                .map(ownerMapper::mapToOwnerDto)
                .toList();
    }

    @Transactional
    public OwnerDto updateOwner(Integer ownerId, OwnerRequestDto ownerRequestDto) {
        Owner updatedOwner = ownerMapper.updateOwner(getOwnerById(ownerId), ownerRequestDto);
        return ownerMapper.mapToOwnerDto(updatedOwner);
    }

    @Transactional
    public void deleteOwner(Integer ownerId) {
        ownerRepository.delete(getOwnerById(ownerId));
    }

    @Transactional
    public OwnerDto addDogsToOwner(Integer ownerId, OwnerDogLinkDto ownerDogLinkDto) {
        List<Dog> dogs = getDogsIfListIsValid(ownerDogLinkDto.dogs());
        Owner owner = getOwnerById(ownerId);
        owner.addDogs(dogs);
        return ownerMapper.mapToOwnerDto(owner);
    }

    @Transactional
    public void removeDogsFromOwner(Integer ownerId, List<String> breeds) {
        List<Dog> dogs = getDogsIfListIsValid(breeds);
        Owner owner = getOwnerById(ownerId);
        owner.removeDogs(dogs);
    }

    private Owner getOwnerById(Integer ownerId) {
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                                ErrorCode.ERR004, "Owner with id [" + ownerId + "] not found", HttpStatus.NOT_FOUND
                        )
                );
    }

    private List<Dog> getDogsIfListIsValid(List<String> breedNames) {
        List<Dog> dogs = dogRepository.findAllByBreedIgnoreCaseIn(breedNames);
        List<String> validBreedNames = dogs.stream()
                .map(Dog::getBreed)
                .toList();
        List<String> invalidBreeds = breedNames.stream()
                .filter(breed -> !validBreedNames.contains(breed))
                .toList();
        if (!invalidBreeds.isEmpty()) {
            throw new ResourceNotFoundException(
                    ErrorCode.ERR004, "Invalid breeds: " + String.join(", ", invalidBreeds), HttpStatus.BAD_REQUEST
            );
        }
        return dogs;
    }
}
