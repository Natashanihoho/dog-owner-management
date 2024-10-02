package com.andersenlab.assesment.service;

import com.andersenlab.assesment.dto.dog.DogDto;
import com.andersenlab.assesment.dto.dog.DogFilter;
import com.andersenlab.assesment.dto.dog.PatchDogDto;
import com.andersenlab.assesment.entity.Dog;
import com.andersenlab.assesment.exception.ResourceNotFoundException;
import com.andersenlab.assesment.mapper.DogMapper;
import com.andersenlab.assesment.repository.DogRepository;
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

import static com.andersenlab.assesment.data.DogTestBuilder.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogServiceTest {

    @Mock
    private DogRepository dogRepository;
    @Spy
    private DogMapper DogMapper = Mappers.getMapper(DogMapper.class);
    @InjectMocks
    private DogService dogService;
    private Dog dog;
    private PatchDogDto patchDogDto;

    @BeforeEach
    void init() {
        dog = aDogTest().buildDogEntity();
        patchDogDto = aDogTest().buildPatchDogDto();
    }

    @Test
    void whenGetDogById_thenReturnDogDto() {
        //Given
        when(dogRepository.findById(1))
                .thenReturn(Optional.of(dog));

        //When
        DogDto actual = dogService.getDog(1);

        //Then
        assertEquals(dog.getName(), actual.getName());
        assertEquals(dog.getDateOfBirth(), actual.getDateOfBirth());
        assertEquals(dog.getId(), actual.getId());
    }

    @Test
    void whenGetDogByNonExistentId_thenThrowNotFoundException() {
        //Given
        when(dogRepository.findById(1))
                .thenReturn(Optional.empty());

        //When
        assertThrows(ResourceNotFoundException.class, () -> dogService.getDog(1));
    }

    @Test
    void whenGetAllDogs_thenReturnDogsPage() {
        //Given
        Pageable pageable = Pageable.ofSize(2);
        Page<Dog> DogPage = new PageImpl<>(List.of(dog, dog));
        when(dogRepository.findAll(pageable))
                .thenReturn(DogPage);

        //When
        Page<DogDto> actual = dogService.getAllDogs(pageable);

        //Then
        assertEquals(2, actual.getTotalElements());
    }

    @Test
    void whenSearchDogsByCriteria_thenReturnList() {
        //Given
        when(dogRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(dog));

        //When
        List<DogDto> actual = dogService.searchDogsByCriteria(new DogFilter("Alice", null, null));

        //Then
        assertEquals(1, actual.size());
    }

    @Test
    void whenUpdateDog_thenUpdateAndReturnDogDto() {
        //Given
        when(dogRepository.findById(1))
                .thenReturn(Optional.of(dog));

        //When
        DogDto actual = dogService.updateDog(1, patchDogDto);

        //Then
        assertEquals(patchDogDto.getName(), actual.getName());
        assertEquals(patchDogDto.getDateOfBirth(), actual.getDateOfBirth());
        assertEquals(1, actual.getId());
    }

    @Test
    void whenUpdateNonExistentDog_thenThrowNotFoundException() {
        //Given
        when(dogRepository.findById(1))
                .thenReturn(Optional.empty());

        //When
        assertThrows(ResourceNotFoundException.class, () -> dogService.updateDog(1, patchDogDto));

        //Then
        verify(DogMapper, never()).updateDog(any(), any());
    }

    @Test
    void whenDeleteDog_thenDeleteDog() {
        //Given
        when(dogRepository.findById(1))
                .thenReturn(Optional.of(dog));

        //When
        dogService.deleteDog(1);

        //Then
        verify(dogRepository).delete(dog);
    }

    @Test
    void whenDeleteNonExistentDog_thenThrowNotFoundException() {
        //Given
        when(dogRepository.findById(1))
                .thenReturn(Optional.empty());

        //When
        assertThrows(ResourceNotFoundException.class, () -> dogService.deleteDog(1));

        //Then
        verify(dogRepository, never()).delete(any(Dog.class));
    }
}
