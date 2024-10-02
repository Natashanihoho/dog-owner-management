package com.andersenlab.assesment.service;

import com.andersenlab.assesment.dto.owner.CreateOwnerDto;
import com.andersenlab.assesment.dto.owner.OwnerDto;
import com.andersenlab.assesment.dto.owner.OwnerFilter;
import com.andersenlab.assesment.dto.owner.PatchOwnerDto;
import com.andersenlab.assesment.entity.Owner;
import com.andersenlab.assesment.exception.ResourceNotFoundException;
import com.andersenlab.assesment.mapper.DogMapper;
import com.andersenlab.assesment.mapper.OwnerMapper;
import com.andersenlab.assesment.mapper.OwnerMapperImpl;
import com.andersenlab.assesment.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;
import java.util.Optional;

import static com.andersenlab.assesment.data.OwnerTestBuilder.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;
    @Spy
    private DogMapper dogMapper = Mappers.getMapper(DogMapper.class);
    @Spy
    private OwnerMapper ownerMapper = new OwnerMapperImpl(dogMapper);
    @InjectMocks
    private OwnerService ownerService;
    private CreateOwnerDto createOwnerDto;
    private PatchOwnerDto patchOwnerDto;
    private Owner owner;

    @BeforeEach
    void init() {
        createOwnerDto = aOwnerTest().buildCreateOwnerDto();
        patchOwnerDto = aOwnerTest().buildPatchOwnerDto();
        owner = aOwnerTest().buildOwnerEntity();
    }

    @Test
    void whenCreateOwner_thenSaveAndReturnOwnerDto() {
        //Given
        when(ownerRepository.save(any()))
                .thenReturn(owner);

        //When
        OwnerDto actual = ownerService.createOwner(createOwnerDto);

        //Then
        assertEquals(createOwnerDto.getFirstName(), actual.getFirstName());
        assertEquals(createOwnerDto.getLastName(), actual.getLastName());
        assertEquals(createOwnerDto.getAge(), actual.getAge());
        assertEquals(createOwnerDto.getCity(), actual.getCity());
        assertEquals(owner.getId(), actual.getId());
    }

    @Test
    void whenGetOwnerById_thenReturnOwnerDto() {
        //Given
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
        when(ownerRepository.findById(1))
                .thenReturn(Optional.of(owner));

        //When
        OwnerDto actual = ownerService.updateOwner(1, patchOwnerDto);

        //Then
        assertEquals(patchOwnerDto.getFirstName(), actual.getFirstName());
        assertEquals(patchOwnerDto.getLastName(), actual.getLastName());
        assertEquals(patchOwnerDto.getAge(), actual.getAge());
        assertEquals(patchOwnerDto.getCity(), actual.getCity());
        assertEquals(1, actual.getId());
    }

    @Test
    void whenUpdateNonExistentOwner_thenThrowNotFoundException() {
        //Given
        when(ownerRepository.findById(1))
                .thenReturn(Optional.empty());

        //When
        assertThrows(ResourceNotFoundException.class, () -> ownerService.updateOwner(1, patchOwnerDto));

        //Then
        verify(ownerMapper, never()).updateOwner(any(), any());
    }

    @Test
    void whenDeleteOwner_thenDeleteOwner() {
        //Given
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
        verify(ownerRepository, never()).delete(any(Owner.class));
    }
}
