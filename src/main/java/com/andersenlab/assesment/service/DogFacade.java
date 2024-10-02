package com.andersenlab.assesment.service;

import com.andersenlab.assesment.dto.dog.DogDto;
import com.andersenlab.assesment.dto.dog.DogFilter;
import com.andersenlab.assesment.dto.dog.PatchDogDto;
import com.andersenlab.assesment.dto.owner.Role;
import com.andersenlab.assesment.dto.owner.UserInfoDto;
import com.andersenlab.assesment.entity.Dog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DogFacade {

    private final DogService dogService;
    private final OwnerService ownerService;

    public DogDto getDog(Integer dogId, UserInfoDto userInfoDto) {
        verifyPermission(dogId, userInfoDto);
        return dogService.getDog(dogId);
    }

    public Page<DogDto> getAllDogs(Pageable pageable, UserInfoDto userInfoDto) {
        return Role.ADMIN == userInfoDto.role()
                ? dogService.getAllDogs(pageable)
                : dogService.getAllDogsForOwner(userInfoDto.email(), pageable);
    }

    public List<DogDto> searchDogsByCriteria(DogFilter dogFilter) {
        return dogService.searchDogsByCriteria(dogFilter);
    }

    @Transactional
    public DogDto updateDog(Integer dogId, PatchDogDto patchDogDto, UserInfoDto userInfoDto) {
        Dog dog = dogService.getDogById(dogId);
        verifyPermission(dog.getOwner().getId(), userInfoDto);
        return dogService.updateDog(dogId, patchDogDto);
    }

    @Transactional
    public void deleteDog(Integer dogId, UserInfoDto userInfoDto) {
        Dog dog = dogService.getDogById(dogId);
        verifyPermission(dog.getOwner().getId(), userInfoDto);
        dogService.deleteDog(dogId);
    }

    private void verifyPermission(Integer dogId, UserInfoDto userInfoDto) {
        Dog dog = dogService.getDogById(dogId);
        if(userInfoDto.role() != Role.ADMIN) {
            ownerService.verifyOwnerConsistency(dog.getOwner().getId(), userInfoDto.email());
        }
    }
}
