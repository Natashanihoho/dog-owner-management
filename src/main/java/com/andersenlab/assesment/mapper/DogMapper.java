package com.andersenlab.assesment.mapper;

import com.andersenlab.assesment.dto.dog.DogDto;
import com.andersenlab.assesment.dto.dog.CreateDogDto;
import com.andersenlab.assesment.dto.dog.PatchDogDto;
import com.andersenlab.assesment.entity.Dog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface DogMapper {

    @Mapping(target = "breed", source = "breed.breedName")
    @Mapping(target = "ownerId", source = "owner.id")
    DogDto mapToDogDto(Dog dog);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "breed", ignore = true)
    @Mapping(target = "id", ignore = true)
    Dog mapToDog(CreateDogDto createDogDto);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "breed", ignore = true)
    @Mapping(target = "id", ignore = true)
    Dog updateDog(@MappingTarget Dog dog, PatchDogDto patchDogDto);
}
