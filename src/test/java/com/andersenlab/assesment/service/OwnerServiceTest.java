package com.andersenlab.assesment.service;

import com.andersenlab.assesment.dto.OwnerDogLinkDto;
import com.andersenlab.assesment.dto.OwnerDto;
import com.andersenlab.assesment.dto.OwnerFilter;
import com.andersenlab.assesment.dto.OwnerRequestDto;
import com.andersenlab.assesment.entity.Owner;
import com.andersenlab.assesment.exception.ResourceNotFoundException;
import com.andersenlab.assesment.mapper.OwnerMapper;
import com.andersenlab.assesment.repository.DogRepository;
import com.andersenlab.assesment.repository.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;
    @Mock
    private DogRepository dogRepository;
    @Spy
    private OwnerMapper ownerMapper = Mappers.getMapper(OwnerMapper.class);
    @InjectMocks
    private OwnerService ownerService;

    @Test
    void whenCreateOwner_thenSaveAndReturnOwnerDto() {
        //Given
        OwnerRequestDto ownerRequestDto = new OwnerRequestDto("Alice", "Smith", 30, "Gdansk");
        Owner owner = new Owner(1, "Alice", "Smith", 30, "Gdansk", Collections.emptyList());
        when(ownerRepository.save(owner))
                .thenReturn(owner);

        //When
        OwnerDto actual = ownerService.createOwner(ownerRequestDto);

        //Then
        assertEquals(ownerRequestDto.getFirstName(), actual.getFirstName());
        assertEquals(ownerRequestDto.getLastName(), actual.getLastName());
        assertEquals(ownerRequestDto.getAge(), actual.getAge());
        assertEquals(ownerRequestDto.getCity(), actual.getCity());
        assertEquals(owner.getId(), actual.getId());
    }

    @Test
    void whenGetOwnerById_thenReturnOwnerDto() {
        //Given
        Owner owner = new Owner(1, "Alice", "Smith", 30, "Gdansk", Collections.emptyList());
        when(ownerRepository.findById(1))
                .thenReturn(Optional.of(owner));

        //When
        OwnerDto actual = ownerService.getOwner(1);

        //Then
        assertEquals(owner.getFirstName(), actual.getFirstName());
        assertEquals(owner.getLastName(), actual.getLastName());
        assertEquals(owner.getAge(), actual.getAge());
        assertEquals(owner.getCity(), actual.getCity());
        assertEquals(owner.getId(), actual.getId());
    }

    @Test
    void whenGetOwnerByNonExistentId_thenThrowNotFoundException() {
        //Given
        when(ownerRepository.findById(1))
                .thenReturn(Optional.empty());

        //When
        assertThrows(ResourceNotFoundException.class, () -> ownerService.getOwner(1));
    }

    @Test
    void whenGetAllOwners_thenReturnOwnersPage() {
        //Given
        Owner owner = new Owner(1, "Alice", "Smith", 30, "Gdansk", Collections.emptyList());
        Pageable pageable = Pageable.ofSize(2);
        Page<Owner> ownerPage = new PageImpl<>(List.of(owner, owner));
        when(ownerRepository.findAll(pageable))
                .thenReturn(ownerPage);

        //When
        Page<OwnerDto> actual = ownerService.getAllOwners(pageable);

        //Then
        assertEquals(2, actual.getTotalElements());
    }

    @Test
    void whenSearchOwnersByCriteria_thenReturnList() {
        //Given
        Owner owner = new Owner(1, "Alice", "Smith", 30, "Gdansk", Collections.emptyList());
        when(ownerRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(owner));

        //When
        List<OwnerDto> actual = ownerService.searchOwnersByCriteria(new OwnerFilter("Alice", null, null, "Gdansk"));

        //Then
        assertEquals(1, actual.size());
    }

    @Test
    void whenUpdateOwner_thenUpdateAndReturnOwnerDto() {
        //Given
        OwnerRequestDto ownerRequestDto = new OwnerRequestDto("Alice", "Smith", 31, "Warsaw");
        Owner owner = new Owner(1, "Alice", "Smith", 30, "Gdansk", Collections.emptyList());
        when(ownerRepository.findById(1))
                .thenReturn(Optional.of(owner));

        //When
        OwnerDto actual = ownerService.updateOwner(1, ownerRequestDto);

        //Then
        assertEquals(ownerRequestDto.getFirstName(), actual.getFirstName());
        assertEquals(ownerRequestDto.getLastName(), actual.getLastName());
        assertEquals(ownerRequestDto.getAge(), actual.getAge());
        assertEquals(ownerRequestDto.getCity(), actual.getCity());
        assertEquals(1, actual.getId());
    }

    @Test
    void whenUpdateNonExistentOwner_thenThrowNotFoundException() {
        //Given
        OwnerRequestDto ownerRequestDto = new OwnerRequestDto("Alice", "Smith", 31, "Warsaw");
        when(ownerRepository.findById(1))
                .thenReturn(Optional.empty());

        //When
        assertThrows(ResourceNotFoundException.class, () -> ownerService.updateOwner(1, ownerRequestDto));

        //Then
        verify(ownerMapper, never()).updateOwner(any(), any());
    }

    @Test
    void whenDeleteOwner_thenDeleteOwner() {
        //Given
        Owner owner = new Owner(1, "Alice", "Smith", 30, "Gdansk", Collections.emptyList());
        when(ownerRepository.findById(1))
                .thenReturn(Optional.of(owner));

        //When
        ownerService.deleteOwner(1);

        //Then
        verify(ownerRepository).delete(owner);
    }

    @Test
    void whenDeleteNonExistentOwner_thenThrowNotFoundException() {
        //Given
        when(ownerRepository.findById(1))
                .thenReturn(Optional.empty());

        //When
        assertThrows(ResourceNotFoundException.class, () -> ownerService.deleteOwner(1));

        //Then
        verify(ownerRepository, never()).delete(any());
    }

    @Test
    void whenAddInvalidDogsToOwner_thenLinkDogToOwner() {
        //Then
        OwnerDogLinkDto ownerDogLinkDto = new OwnerDogLinkDto(List.of("Cor"));
        when(dogRepository.findAllByBreedIgnoreCaseIn(ownerDogLinkDto.dogs()))
                .thenThrow(ResourceNotFoundException.class);

        //When
        assertThrows(ResourceNotFoundException.class, () -> ownerService.addDogsToOwner(1, ownerDogLinkDto));
    }
}
