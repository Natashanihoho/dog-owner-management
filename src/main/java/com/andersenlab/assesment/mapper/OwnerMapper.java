package com.andersenlab.assesment.mapper;

import com.andersenlab.assesment.dto.OwnerDto;
import com.andersenlab.assesment.dto.OwnerRequestDto;
import com.andersenlab.assesment.entity.Dog;
import com.andersenlab.assesment.entity.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OwnerMapper {

    OwnerDto mapToOwnerDto(Owner owner);

    @Mapping(target = "id", ignore = true)
    Owner mapToOwner(OwnerRequestDto ownerRequestDto);

    @Mapping(target = "id", ignore = true)
    Owner updateOwner(@MappingTarget Owner owner, OwnerRequestDto ownerRequestDto);

    default List<String> mapDogs(List<Dog> dogs) {
        return dogs.stream()
                .map(Dog::getBreed)
                .toList();
    }
}
