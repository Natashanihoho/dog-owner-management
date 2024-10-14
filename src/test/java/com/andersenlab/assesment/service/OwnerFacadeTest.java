package com.andersenlab.assesment.service;

import com.andersenlab.assesment.data.DogTestBuilder;
import com.andersenlab.assesment.dto.dog.CreateDogDto;
import com.andersenlab.assesment.dto.dog.DogDto;
import com.andersenlab.assesment.dto.owner.*;
import com.andersenlab.assesment.entity.Owner;
import com.andersenlab.assesment.exception.ResourceNotFoundException;
import com.andersenlab.assesment.exception.model.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static com.andersenlab.assesment.data.OwnerTestBuilder.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerFacadeTest {

    @Mock
    private OwnerService ownerService;
    @Mock
    private DogService dogService;
    @Mock
    private KeycloakService keycloakService;
    @InjectMocks
    private OwnerFacade ownerFacade;
    private UserInfoDto userInfoDto;
    private OwnerDto ownerDto;
    private CreateOwnerDto createOwnerDto;
    private PatchOwnerDto patchOwnerDto;

    @BeforeEach
    void init() {
        userInfoDto = new UserInfoDto("hard@gmail.com", Role.ADMIN);
        createOwnerDto = aOwnerTest().buildCreateOwnerDto();
        ownerDto = aOwnerTest().buildOwnerDto();
        patchOwnerDto = aOwnerTest().buildPatchOwnerDto();
    }

    @Test
    void whenCreateOwner_thenCallOwnerCreation() {
        //Given
        when(ownerService.createOwner(createOwnerDto))
                .thenReturn(ownerDto);

        //When
        OwnerDto actual = ownerFacade.createOwner(createOwnerDto);

        //Then
        verify(keycloakService).registerOwner(createOwnerDto);
        assertEquals(ownerDto.getFirstName(), actual.getFirstName());
        assertEquals(ownerDto.getLastName(), actual.getLastName());
        assertEquals(ownerDto.getAge(), actual.getAge());
        assertEquals(ownerDto.getCity(), actual.getCity());
        assertEquals(ownerDto.getId(), actual.getId());
    }

    @Test
    void whenGetOwnerById_thenReturnOwnerDto() {
        //Given
        when(ownerService.getOwner(1))
                .thenReturn(ownerDto);

        //When
        OwnerDto actual = ownerFacade.getOwner(1, userInfoDto);

        //Then
        assertEquals(ownerDto.getFirstName(), actual.getFirstName());
        assertEquals(ownerDto.getLastName(), actual.getLastName());
        assertEquals(ownerDto.getAge(), actual.getAge());
        assertEquals(ownerDto.getCity(), actual.getCity());
        assertEquals(ownerDto.getId(), actual.getId());
    }

    @Test
    void whenGetAllOwners_thenReturnOwnersPage() {
        //Given
        Pageable pageable = Pageable.ofSize(2);
        Page<OwnerDto> ownerPage = new PageImpl<>(List.of(ownerDto, ownerDto));
        when(ownerService.getAllOwners(pageable))
                .thenReturn(ownerPage);

        //When
        Page<OwnerDto> actual = ownerFacade.getAllOwners(pageable);

        //Then
        assertEquals(2, actual.getTotalElements());
    }

    @Test
    void whenSearchOwnersByCriteria_thenReturnList() {
        //Given
        OwnerFilter ownerFilter = new OwnerFilter(null, null, null, "Gdansk");
        when(ownerService.searchOwnersByCriteria(ownerFilter))
                .thenReturn(List.of(ownerDto));

        //When
        List<OwnerDto> actual = ownerFacade.searchOwnersByCriteria(ownerFilter);

        //Then
        assertEquals(1, actual.size());
        assertEquals(ownerFilter.city(), actual.getFirst().getCity());
    }

    @Test
    void whenUpdateOwner_thenCallUpdateAndReturnOwnerDto() {
        //Given
        when(ownerService.updateOwner(1, patchOwnerDto))
                .thenReturn(ownerDto);

        //When
        OwnerDto actual = ownerFacade.updateOwner(1, patchOwnerDto, userInfoDto);

        //Then
        assertEquals(patchOwnerDto.getFirstName(), actual.getFirstName());
        assertEquals(patchOwnerDto.getLastName(), actual.getLastName());
        assertEquals(patchOwnerDto.getAge(), actual.getAge());
        assertEquals(patchOwnerDto.getCity(), actual.getCity());
        assertEquals(1, actual.getId());
    }

    @Test
    void whenDeleteOwner_thenDeleteOwner() {
        //When
        ownerFacade.deleteOwner(1, userInfoDto);

        //Then
        verify(ownerService).deleteOwner(1);
        verify(keycloakService).deleteUser(userInfoDto.email());
    }

    @Test
    void whenAddDogToOwner_thenCallDogCreation() {
        //Given
        CreateDogDto createDogDto = DogTestBuilder.aDogTest().buildCreateDogDto();
        DogDto dogDto = DogTestBuilder.aDogTest().buildDogDto();
        Owner owner = aOwnerTest().buildOwnerEntity();
        when(ownerService.getOwnerById(1))
                .thenReturn(Optional.of(owner));
        when(dogService.createDog(createDogDto, owner))
                .thenReturn(dogDto);

        //When

        DogDto actual = ownerFacade.addDogToOwner(1, createDogDto, userInfoDto);

        //Then
        assertEquals(createDogDto.getName(), actual.getName());
        assertEquals(createDogDto.getBreed(), actual.getBreed());
        assertEquals(createDogDto.getDateOfBirth(), actual.getDateOfBirth());
    }

    @Test
    void whenAddDogToAbsentOwner_thenThrowException() {
        //Given
        CreateDogDto createDogDto = DogTestBuilder.aDogTest().buildCreateDogDto();
        when(ownerService.getOwnerById(1))
                .thenReturn(Optional.empty());
        when(ownerService.resourceNotFoundException(any()))
                .thenReturn(new ResourceNotFoundException(ErrorCode.ERR004, "message", HttpStatus.NOT_FOUND));

        //When
        assertThrows(ResourceNotFoundException.class, () -> ownerFacade.addDogToOwner(1, createDogDto, userInfoDto));

        //Then
        verify(dogService, never()).createDog(any(), any());
    }

    @Test
    void whenAddRoleToOwner_thenAssignRoleOnKeycloak() {
        //Given
        PatchRoleDto patchRoleDto = new PatchRoleDto("email", Role.ADMIN, KeycloakRoleOperationType.ADD);

        //When
        ownerFacade.updateOwnerRoles(patchRoleDto);

        //Then
        verify(keycloakService).assignRole(patchRoleDto.email(), patchRoleDto.role().name());
    }

    @Test
    void whenDeleteRoleFromOwner_thenDeleteRoleOnKeycloak() {
        //Given
        PatchRoleDto patchRoleDto = new PatchRoleDto("email", Role.ADMIN, KeycloakRoleOperationType.DELETE);

        //When
        ownerFacade.updateOwnerRoles(patchRoleDto);

        //Then
        verify(keycloakService).deleteRole(patchRoleDto.email(), patchRoleDto.role().name());
    }
}
