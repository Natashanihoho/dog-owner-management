package com.andersenlab.assesment.rest.controller;

import com.andersenlab.assesment.dto.dog.DogDto;
import com.andersenlab.assesment.dto.dog.PatchDogDto;
import com.andersenlab.assesment.dto.owner.Role;
import com.andersenlab.assesment.dto.owner.UserInfoDto;
import com.andersenlab.assesment.service.DogFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static com.andersenlab.assesment.data.DogTestBuilder.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DogController.class)
class DogControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DogFacade dogFacade;
    @Autowired
    private ObjectMapper objectMapper;
    private PatchDogDto patchDogDto;
    private DogDto dogDto;
    private UserInfoDto userInfoDto;
    private JwtRequestPostProcessor jwtRequestPostProcessor;

    @BeforeEach
    void init() {
        patchDogDto = aDogTest().buildPatchDogDto();
        dogDto = aDogTest().buildDogDto();
        userInfoDto = new UserInfoDto("hard@gmail.com", Role.ADMIN);
        jwtRequestPostProcessor = jwt().jwt(builder -> builder.tokenValue("value")
                .claim("realm_access", Map.of("roles", List.of("USER", "ADMIN")))
                .claim("email", "hard@gmail.com")
                .header("Authorization", "Bearer value"));
    }

    @Test
    void whenGetDogById_thenReturnDogDto() throws Exception {
        //Given
        when(dogFacade.getDog(dogDto.getId(), userInfoDto))
                .thenReturn(dogDto);

        //When
        mockMvc.perform(get(DogController.DOGS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(dogDto.getName()))
                .andExpect(jsonPath("$.dateOfBirth").value(String.valueOf(dogDto.getDateOfBirth())))
                .andExpect(jsonPath("$.breed").value(dogDto.getBreed()))
                .andExpect(jsonPath("$.id").value(dogDto.getId()));
    }

    @Test
    void whenGetAllDogs_thenReturnPage() throws Exception {
        //Given
        Pageable pageable = Pageable.ofSize(2);
        Page<DogDto> dogDtoPage = new PageImpl<>(List.of(dogDto, dogDto));
        when(dogFacade.getAllDogs(pageable, userInfoDto))
                .thenReturn(dogDtoPage);

        //When
        mockMvc.perform(get(DogController.DOGS_URL)
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(dogDtoPage.getTotalElements()));
    }

    @Test
    void whenSearchDogsByCriteria_thenReturnDogList() throws Exception {
        //Given
        when(dogFacade.searchDogsByCriteria(any()))
                .thenReturn(List.of(dogDto));

        //When
        mockMvc.perform(get(DogController.DOGS_URL + "/search?name=Muffin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(equalTo(1))))
                .andExpect(jsonPath("$[0].name").value(dogDto.getName()))
                .andExpect(jsonPath("$[0].dateOfBirth").value(String.valueOf(dogDto.getDateOfBirth())))
                .andExpect(jsonPath("$[0].breed").value(dogDto.getBreed()))
                .andExpect(jsonPath("$[0].id").value(dogDto.getId()));
    }

    @Test
    void whenUpdateDog_thenUpdateDog() throws Exception {
        //Given
        when(dogFacade.updateDog(1, patchDogDto, userInfoDto))
                .thenReturn(dogDto);

        //When
        mockMvc.perform(patch(DogController.DOGS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor)
                        .content(objectMapper.writeValueAsString(patchDogDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(dogDto.getName()))
                .andExpect(jsonPath("$.dateOfBirth").value(String.valueOf(dogDto.getDateOfBirth())))
                .andExpect(jsonPath("$.breed").value(dogDto.getBreed()))
                .andExpect(jsonPath("$.id").value(dogDto.getId()));
    }

    @Test
    void whenDeleteDog_thenNoContent() throws Exception {
        //When
        mockMvc.perform(delete(DogController.DOGS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isNoContent());

        //Then
        verify(dogFacade).deleteDog(1, userInfoDto);
    }
}
