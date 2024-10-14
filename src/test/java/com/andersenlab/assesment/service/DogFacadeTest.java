package com.andersenlab.assesment.service;

import com.andersenlab.assesment.dto.dog.DogDto;
import com.andersenlab.assesment.dto.dog.DogFilter;
import com.andersenlab.assesment.dto.dog.PatchDogDto;
import com.andersenlab.assesment.dto.owner.Role;
import com.andersenlab.assesment.dto.owner.UserInfoDto;
import com.andersenlab.assesment.entity.Dog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.andersenlab.assesment.data.DogTestBuilder.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogFacadeTest {

    @Mock
    private DogService dogService;
    @Mock
    private OwnerService ownerService;
    @InjectMocks
    private DogFacade dogFacade;
    private DogDto dogDto;
    private PatchDogDto patchDogDto;
    private UserInfoDto userInfoDto;

    @BeforeEach
    void init() {
        dogDto = aDogTest().buildDogDto();
        patchDogDto = aDogTest().buildPatchDogDto();
        userInfoDto = new UserInfoDto("email", Role.ADMIN);
    }

    @Test
    void whenGetDog_thenReturnDogDto() {
        //Given
        when(dogService.getDog(1))
                .thenReturn(dogDto);

        //When
        DogDto actual = dogFacade.getDog(1, userInfoDto);

        //Then
        assertEquals(dogDto.getName(), actual.getName());
        assertEquals(dogDto.getDateOfBirth(), actual.getDateOfBirth());
        assertEquals(dogDto.getId(), actual.getId());
    }

    @Test
    void wheGetAllDogs_thenReturnPage() {
        //Given
        Pageable pageable = Pageable.ofSize(2);
        Page<DogDto> dogPage = new PageImpl<>(List.of(dogDto, dogDto));
        when(dogService.getAllDogs(pageable))
                .thenReturn(dogPage);

        //When
        Page<DogDto> actual = dogFacade.getAllDogs(pageable, userInfoDto);

        //Then
        assertEquals(2, actual.getTotalElements());
    }

    @Test
    void whenSearchDogsByCriteria_thenReturnList() {
        //Given
        DogFilter dogFilter = new DogFilter("Muffin", null, null);
        when(dogService.searchDogsByCriteria(dogFilter))
                .thenReturn(List.of(dogDto));

        //When
        List<DogDto> actual = dogFacade.searchDogsByCriteria(dogFilter);

        //Then
        assertEquals(1, actual.size());
    }

    @Test
    void whenUpdateDog_thenUpdateAndReturnDogDto() {
        //Given
        Dog dog = aDogTest().buildDogEntity();
        when(dogService.getDogById(1))
                .thenReturn(dog);
        when(dogService.updateDog(1, patchDogDto))
                .thenReturn(dogDto);

        //When
        DogDto actual = dogFacade.updateDog(1, patchDogDto, userInfoDto);

        //Then
        assertEquals(patchDogDto.getName(), actual.getName());
        assertEquals(patchDogDto.getDateOfBirth(), actual.getDateOfBirth());
        assertEquals(1, actual.getId());
    }

    @Test
    void whenDeleteDog_thenDeleteDog() {
        //Given
        Dog dog = aDogTest().buildDogEntity();
        when(dogService.getDogById(1))
                .thenReturn(dog);

        //When
        dogFacade.deleteDog(1, userInfoDto);

        //Then
        verify(dogService).deleteDog(1);
    }
}