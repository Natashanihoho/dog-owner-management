package com.andersenlab.assesment.service;

import com.andersenlab.assesment.dto.DogDto;
import com.andersenlab.assesment.dto.DogFilter;
import com.andersenlab.assesment.dto.DogRequestDto;
import com.andersenlab.assesment.entity.Dog;
import com.andersenlab.assesment.exception.ResourceAlreadyExistsException;
import com.andersenlab.assesment.exception.ResourceNotFoundException;
import com.andersenlab.assesment.mapper.DogMapper;
import com.andersenlab.assesment.repository.DogRepository;
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

    @Test
    void whenCreateDog_thenSaveAndReturnDogDto() {
        //Given
        DogRequestDto dogRequestDto = new DogRequestDto("Corgi", 15, "England", true);
        Dog dog = new Dog(1, "Corgi", 15, true, "England", Collections.emptyList());
        when(dogRepository.existsByBreedIgnoreCase(dogRequestDto.getBreed()))
                .thenReturn(false);
        when(dogRepository.save(dog))
                .thenReturn(dog);

        //When
        DogDto actual = dogService.createDog(dogRequestDto);

        //Then
        assertEquals(dogRequestDto.getBreed(), actual.getBreed());
        assertEquals(dogRequestDto.getAverageLifeExpectancy(), actual.getAverageLifeExpectancy());
        assertEquals(dogRequestDto.getOriginCountry(), actual.getOriginCountry());
        assertEquals(dogRequestDto.getEasyToTrain(), actual.getEasyToTrain());
        assertEquals(dog.getId(), actual.getId());
    }

    @Test
    void whenCreateDogIfAlreadyExists_thenThrowException() {
        //Given
        DogRequestDto dogRequestDto = new DogRequestDto("Corgi", 15, "England", true);
        when(dogRepository.existsByBreedIgnoreCase(any()))
                .thenReturn(true);

        //When
        assertThrows(ResourceAlreadyExistsException.class, () -> dogService.createDog(dogRequestDto));

        //Then
        verify(dogRepository, never()).save(any());
    }

    @Test
    void whenGetDogById_thenReturnDogDto() {
        //Given
        Dog dog = new Dog(1, "Corgi", 15, true, "England", Collections.emptyList());
        when(dogRepository.findById(1))
                .thenReturn(Optional.of(dog));

        //When
        DogDto actual = dogService.getDog(1);

        //Then
        assertEquals(dog.getBreed(), actual.getBreed());
        assertEquals(dog.getOriginCountry(), actual.getOriginCountry());
        assertEquals(dog.getAverageLifeExpectancy(), actual.getAverageLifeExpectancy());
        assertEquals(dog.getEasyToTrain(), actual.getEasyToTrain());
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
        Dog dog = new Dog(1, "Corgi", 15, true, "England", Collections.emptyList());
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
        Dog dog = new Dog(1, "Corgi", 15, true, "England", Collections.emptyList());
        when(dogRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(dog));

        //When
        List<DogDto> actual = dogService.searchDogsByCriteria(new DogFilter("Alice", null, null, true));

        //Then
        assertEquals(1, actual.size());
    }

    @Test
    void whenUpdateDog_thenUpdateAndReturnDogDto() {
        //Given
        DogRequestDto dogRequestDto = new DogRequestDto("Welsh Corgi Pembroke", 12, "England", true);
        Dog dog = new Dog(1, "Corgi", 15, true, "England", Collections.emptyList());
        when(dogRepository.findById(1))
                .thenReturn(Optional.of(dog));

        //When
        DogDto actual = dogService.updateDog(1, dogRequestDto);

        //Then
        assertEquals(dogRequestDto.getBreed(), actual.getBreed());
        assertEquals(dogRequestDto.getEasyToTrain(), actual.getEasyToTrain());
        assertEquals(dogRequestDto.getAverageLifeExpectancy(), actual.getAverageLifeExpectancy());
        assertEquals(dogRequestDto.getOriginCountry(), actual.getOriginCountry());
        assertEquals(1, actual.getId());
    }

    @Test
    void whenUpdateNonExistentDog_thenThrowNotFoundException() {
        //Given
        DogRequestDto dogRequestDto = new DogRequestDto("Corgi", 15, "England", true);
        when(dogRepository.findById(1))
                .thenReturn(Optional.empty());

        //When
        assertThrows(ResourceNotFoundException.class, () -> dogService.updateDog(1, dogRequestDto));

        //Then
        verify(DogMapper, never()).updateDog(any(), any());
    }

    @Test
    void whenDeleteDog_thenDeleteDog() {
        //Given
        Dog dog = new Dog(1, "Corgi", 15, true, "England", Collections.emptyList());
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
        verify(dogRepository, never()).delete(any());
    }
}
