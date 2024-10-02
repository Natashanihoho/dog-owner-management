package com.andersenlab.assesment.mapper;

import com.andersenlab.assesment.dto.owner.OwnerDto;
import com.andersenlab.assesment.dto.owner.CreateOwnerDto;
import com.andersenlab.assesment.dto.owner.PatchOwnerDto;
import com.andersenlab.assesment.entity.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(uses = DogMapper.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OwnerMapper {

    @Mapping(target = "password", ignore = true)
    OwnerDto mapToOwnerDto(Owner owner);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dogs", ignore = true)
    Owner mapToOwner(CreateOwnerDto createOwnerDto);

    @Mapping(target = "email", ignore = true)
    @Mapping(target = "dogs", ignore = true)
    @Mapping(target = "id", ignore = true)
    Owner updateOwner(@MappingTarget Owner owner, PatchOwnerDto patchOwnerDto);
}
