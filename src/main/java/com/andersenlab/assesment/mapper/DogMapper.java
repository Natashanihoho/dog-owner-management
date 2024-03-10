package com.andersenlab.assesment.mapper;

import com.andersenlab.assesment.dto.DogDto;
import com.andersenlab.assesment.dto.DogRequestDto;
import com.andersenlab.assesment.entity.Dog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface DogMapper {

    DogDto mapToDogDto(Dog og);

    @Mapping(target = "id", ignore = true)
    Dog mapToDog(DogRequestDto dogRequestDto);

    @Mapping(target = "id", ignore = true)
    Dog updateDog(@MappingTarget Dog dog, DogRequestDto dogRequestDto);
}
