package com.andersenlab.assesment.service;

import com.andersenlab.assesment.dto.breed.BreedDto;
import com.andersenlab.assesment.dto.breed.BreedRequestDto;
import com.andersenlab.assesment.dto.breed.BreedFilter;
import com.andersenlab.assesment.entity.Breed;
import com.andersenlab.assesment.exception.ResourceAlreadyExistsException;
import com.andersenlab.assesment.exception.ResourceNotFoundException;
import com.andersenlab.assesment.mapper.BreedMapper;
import com.andersenlab.assesment.repository.BreedRepository;
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

import static com.andersenlab.assesment.data.BreedTestBuilder.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BreedServiceTest {

    @Mock
    private BreedRepository breedRepository;
    @Spy
    private BreedMapper BreedMapper = Mappers.getMapper(BreedMapper.class);
    @InjectMocks
    private BreedService breedService;
    private BreedRequestDto breedRequestDto;
    private Breed breed;

    @BeforeEach
    void init() {
        breedRequestDto = aBreedTest().buildBreedRequestDto();
        breed = aBreedTest().buildBreedEntity();
    }

    @Test
    void whenCreateBreed_thenSaveAndReturnBreedDto() {
        //Given
        when(breedRepository.existsByBreedNameIgnoreCase(breedRequestDto.getBreedName()))
                .thenReturn(false);
        when(breedRepository.save(any(Breed.class)))
                .thenReturn(breed);

        //When
        BreedDto actual = breedService.createBreed(breedRequestDto);

        //Then
        assertEquals(breedRequestDto.getBreedName(), actual.getBreedName());
        assertEquals(breedRequestDto.getAverageLifeExpectancy(), actual.getAverageLifeExpectancy());
        assertEquals(breedRequestDto.getOriginCountry(), actual.getOriginCountry());
        assertEquals(breedRequestDto.getEasyToTrain(), actual.getEasyToTrain());
        assertEquals(breed.getId(), actual.getId());
    }

    @Test
    void whenCreateBreedIfAlreadyExists_thenThrowException() {
        //Given
        when(breedRepository.existsByBreedNameIgnoreCase(any()))
                .thenReturn(true);

        //When
        assertThrows(ResourceAlreadyExistsException.class, () -> breedService.createBreed(breedRequestDto));

        //Then
        verify(breedRepository, never()).save(any());
    }

    @Test
    void whenGetBreedById_thenReturnBreedDto() {
        //Given
        when(breedRepository.findById(1))
                .thenReturn(Optional.of(breed));

        //When
        BreedDto actual = breedService.getBreed(1);

        //Then
        assertEquals(breed.getBreedName(), actual.getBreedName());
        assertEquals(breed.getOriginCountry(), actual.getOriginCountry());
        assertEquals(breed.getAverageLifeExpectancy(), actual.getAverageLifeExpectancy());
        assertEquals(breed.getEasyToTrain(), actual.getEasyToTrain());
        assertEquals(breed.getId(), actual.getId());
    }

    @Test
    void whenGetBreedByNonExistentId_thenThrowNotFoundException() {
        //Given
        when(breedRepository.findById(1))
                .thenReturn(Optional.empty());

        //When
        assertThrows(ResourceNotFoundException.class, () -> breedService.getBreed(1));
    }

    @Test
    void whenGetAllBreeds_thenReturnBreedsPage() {
        //Given
        Pageable pageable = Pageable.ofSize(2);
        Page<Breed> BreedPage = new PageImpl<>(List.of(breed, breed));
        when(breedRepository.findAll(pageable))
                .thenReturn(BreedPage);

        //When
        Page<BreedDto> actual = breedService.getAllBreeds(pageable);

        //Then
        assertEquals(2, actual.getTotalElements());
    }

    @Test
    void whenSearchBreedsByCriteria_thenReturnList() {
        //Given
        when(breedRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(breed));

        //When
        List<BreedDto> actual = breedService.searchBreedsByCriteria(new BreedFilter("Alice", null, null, true));

        //Then
        assertEquals(1, actual.size());
    }

    @Test
    void whenUpdateBreed_thenUpdateAndReturnBreedDto() {
        //Given
        when(breedRepository.findById(1))
                .thenReturn(Optional.of(breed));

        //When
        BreedDto actual = breedService.updateBreed(1, breedRequestDto);

        //Then
        assertEquals(breedRequestDto.getBreedName(), actual.getBreedName());
        assertEquals(breedRequestDto.getEasyToTrain(), actual.getEasyToTrain());
        assertEquals(breedRequestDto.getAverageLifeExpectancy(), actual.getAverageLifeExpectancy());
        assertEquals(breedRequestDto.getOriginCountry(), actual.getOriginCountry());
        assertEquals(1, actual.getId());
    }

    @Test
    void whenUpdateNonExistentBreed_thenThrowNotFoundException() {
        //Given
        when(breedRepository.findById(1))
                .thenReturn(Optional.empty());

        //When
        assertThrows(ResourceNotFoundException.class, () -> breedService.updateBreed(1, breedRequestDto));

        //Then
        verify(BreedMapper, never()).updateBreed(any(), any());
    }

    @Test
    void whenDeleteBreed_thenDeleteBreed() {
        //Given
        when(breedRepository.findById(1))
                .thenReturn(Optional.of(breed));

        //When
        breedService.deleteBreed(1);

        //Then
        verify(breedRepository).delete(breed);
    }

    @Test
    void whenDeleteNonExistentBreed_thenThrowNotFoundException() {
        //Given
        when(breedRepository.findById(1))
                .thenReturn(Optional.empty());

        //When
        assertThrows(ResourceNotFoundException.class, () -> breedService.deleteBreed(1));

        //Then
        verify(breedRepository, never()).delete(any(Breed.class));
    }
}
