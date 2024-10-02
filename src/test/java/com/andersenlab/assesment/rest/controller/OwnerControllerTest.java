package com.andersenlab.assesment.rest.controller;

import com.andersenlab.assesment.data.DogTestBuilder;
import com.andersenlab.assesment.dto.dog.CreateDogDto;
import com.andersenlab.assesment.dto.dog.DogDto;
import com.andersenlab.assesment.dto.owner.*;
import com.andersenlab.assesment.exception.model.ErrorCode;
import com.andersenlab.assesment.service.OwnerFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.andersenlab.assesment.data.OwnerTestBuilder.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OwnerFacade ownerFacade;
    @Autowired
    private ObjectMapper objectMapper;
    private OwnerDto ownerDto;
    private String createOwnerDto;
    private PatchOwnerDto patchOwnerDto;
    private JwtRequestPostProcessor jwtRequestPostProcessor;
    private UserInfoDto userInfoDto;

    @BeforeEach
    void init() {
        patchOwnerDto = aOwnerTest().buildPatchOwnerDto();
        ownerDto = aOwnerTest().buildOwnerDto();
        createOwnerDto = aOwnerTest().buildJsonCreateOwnerDto();
        userInfoDto = new UserInfoDto("hard@gmail.com", Role.ADMIN);
        jwtRequestPostProcessor = jwt().jwt(builder -> builder.tokenValue("value")
                .claim("realm_access", Map.of("roles", List.of("USER", "ADMIN")))
                .claim("email", "hard@gmail.com")
                .header("Authorization", "Bearer value"));
    }

    @Test
    void whenCreateOwner_thenCreated() throws Exception {
        //Given
        when(ownerFacade.createOwner(any()))
                .thenReturn(ownerDto);

        //When
        mockMvc.perform(post(OwnerController.OWNERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor)
                        .content(createOwnerDto))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(ownerDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(ownerDto.getLastName()))
                .andExpect(jsonPath("$.city").value(ownerDto.getCity()))
                .andExpect(jsonPath("$.age").value(ownerDto.getAge()))
                .andExpect(jsonPath("$.id").value(ownerDto.getId()));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRequestBody")
    void whenCreateOwnerWithInvalidRequestBody_thenBadRequest(String createOwnerDto, ErrorCode errorCode) throws Exception {
        //When
        mockMvc.perform(post(OwnerController.OWNERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor)
                        .content(createOwnerDto))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.name()));
    }

    @Test
    void whenGetOwnerById_thenReturnOwnerDto() throws Exception {
        //Given
        when(ownerFacade.getOwner(ownerDto.getId(), userInfoDto))
                .thenReturn(ownerDto);

        //When
        mockMvc.perform(get(OwnerController.OWNERS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(ownerDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(ownerDto.getLastName()))
                .andExpect(jsonPath("$.city").value(ownerDto.getCity()))
                .andExpect(jsonPath("$.age").value(ownerDto.getAge()))
                .andExpect(jsonPath("$.id").value(ownerDto.getId()));
    }

    @Test
    void whenGetAllOwners_thenReturnPage() throws Exception {
        //Given
        Pageable pageable = Pageable.ofSize(2);
        Page<OwnerDto> ownerDtoPage = new PageImpl<>(List.of(ownerDto, ownerDto));
        when(ownerFacade.getAllOwners(pageable))
                .thenReturn(ownerDtoPage);
        //When
        mockMvc.perform(get(OwnerController.OWNERS_URL)
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(ownerDtoPage.getTotalElements()));
    }

    @Test
    void whenSearchOwnersByCriteria_thenReturnOwnerList() throws Exception {
        //Given
        when(ownerFacade.searchOwnersByCriteria(any()))
                .thenReturn(List.of(ownerDto));

        //When
        mockMvc.perform(get(OwnerController.OWNERS_URL + "/search?age=30&city=Gdansk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(equalTo(1))))
                .andExpect(jsonPath("$[0].firstName").value(ownerDto.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(ownerDto.getLastName()))
                .andExpect(jsonPath("$[0].city").value(ownerDto.getCity()))
                .andExpect(jsonPath("$[0].age").value(ownerDto.getAge()))
                .andExpect(jsonPath("$[0].id").value(ownerDto.getId()));
    }

    @Test
    void whenUpdateOwner_thenReturnOwnerDto() throws Exception {
        //Given
        when(ownerFacade.updateOwner(1, patchOwnerDto, userInfoDto))
                .thenReturn(ownerDto);

        //When
        mockMvc.perform(patch(OwnerController.OWNERS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchOwnerDto))
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(ownerDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(ownerDto.getLastName()))
                .andExpect(jsonPath("$.city").value(ownerDto.getCity()))
                .andExpect(jsonPath("$.age").value(ownerDto.getAge()))
                .andExpect(jsonPath("$.id").value(ownerDto.getId()));
    }

    @Test
    void whenAddDogToOwner_thenCreated() throws Exception {
        //Given
        CreateDogDto createDogDto = DogTestBuilder.aDogTest().buildCreateDogDto();
        DogDto dogDto = DogTestBuilder.aDogTest().buildDogDto();
        when(ownerFacade.addDogToOwner(1, createDogDto, userInfoDto))
                .thenReturn(dogDto);

        //When
        mockMvc.perform(post(OwnerController.OWNERS_URL + "/1/dogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor)
                        .content(objectMapper.writeValueAsString(createDogDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(dogDto.getName()))
                .andExpect(jsonPath("$.dateOfBirth").value(String.valueOf(dogDto.getDateOfBirth())))
                .andExpect(jsonPath("$.breed").value(dogDto.getBreed()));
    }

    @Test
    void whenDeleteOwner_thenNoContent() throws Exception {
        //When
        mockMvc.perform(delete(OwnerController.OWNERS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isNoContent());

        //Then
        verify(ownerFacade).deleteOwner(1, userInfoDto);
    }

    private static Stream<Arguments> provideInvalidRequestBody() {
        return Stream.of(
                Arguments.of(aOwnerTest().withFirstName("InvalidSizeOfFirstNameMoreThan128CharactersProvideddddddddddddddddd").buildJsonCreateOwnerDto(), ErrorCode.ERR001),
                Arguments.of(aOwnerTest().withFirstName("").buildJsonCreateOwnerDto(), ErrorCode.ERR002),
                Arguments.of(aOwnerTest().withLastName("InvalidSizeOfFirstNameMoreThan128CharactersProvideddddddddddddddddd").buildJsonCreateOwnerDto(), ErrorCode.ERR001),
                Arguments.of(aOwnerTest().withLastName("").buildJsonCreateOwnerDto(), ErrorCode.ERR002),
                Arguments.of(aOwnerTest().withAge(-43).buildJsonCreateOwnerDto(), ErrorCode.ERR003),
                Arguments.of(aOwnerTest().withCity("GdanskGdanskGdanskGdanskGdanskGdanskGdanskGdanskGdanskGdanskGdansk").buildJsonCreateOwnerDto(), ErrorCode.ERR001),
                Arguments.of(aOwnerTest().withCity("").buildJsonCreateOwnerDto(), ErrorCode.ERR002)
        );
    }
}
