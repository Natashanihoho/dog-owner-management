package com.andersenlab.assesment.mapper;

import com.andersenlab.assesment.dto.breed.BreedDto;
import com.andersenlab.assesment.dto.breed.BreedRequestDto;
import com.andersenlab.assesment.entity.Breed;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface BreedMapper {

    BreedDto mapToBreedDto(Breed breed);

    @Mapping(target = "id", ignore = true)
    Breed mapToBreed(BreedRequestDto breedRequestDto);

    @Mapping(target = "id", ignore = true)
    Breed updateBreed(@MappingTarget Breed breed, BreedRequestDto breedRequestDto);
}
