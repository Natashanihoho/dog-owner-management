package com.andersenlab.assesment.rest.controller;

import com.andersenlab.assesment.dto.DogDto;
import com.andersenlab.assesment.dto.DogFilter;
import com.andersenlab.assesment.dto.DogRequestDto;
import com.andersenlab.assesment.service.DogService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DogController.class)
class DogControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DogService dogService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenCreateDog_thenCreated() throws Exception {
        //Given
        DogRequestDto dogRequestDto = new DogRequestDto("Corgi", 15, "England", true);
        DogDto DogDto = new DogDto("Corgi", 15, "England", true, 1);
        when(dogService.createDog(dogRequestDto))
                .thenReturn(DogDto);

        //When
        mockMvc.perform(post(DogController.DOGS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dogRequestDto)))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRequestBody")
    void whenCreateDogWithInvalidRequestBody_thenBadRequest(String breed, Integer averageLifeExpectancy, String originCountry, Boolean easyToTrain) throws Exception {
        //Given
        DogRequestDto dogRequestDto = new DogRequestDto(breed, averageLifeExpectancy, originCountry, easyToTrain);

        //When
        mockMvc.perform(post(DogController.DOGS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dogRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetDogById_thenReturnDogDto() throws Exception {
        //Given
        DogDto DogDto = new DogDto("Corgi", 15, "England", true, 1);
        when(dogService.getDog(DogDto.getId()))
                .thenReturn(DogDto);

        //When
        MvcResult mvcResult = mockMvc.perform(get(DogController.DOGS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        DogDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), DogDto.class);
        assertEquals(DogDto, actual);
    }

    @Test
    void whenGetAllDogs_thenReturnPage() throws Exception {
        //Given
        DogDto DogDto = new DogDto("Corgi", 15, "England", true, 1);
        Pageable pageable = Pageable.ofSize(2);
        Page<DogDto> DogDtoPage = new PageImpl<>(List.of(DogDto, DogDto));
        when(dogService.getAllDogs(pageable))
                .thenReturn(DogDtoPage);
        //When
        MvcResult result = mockMvc.perform(get(DogController.DOGS_URL)
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    void whenSearchDogsByCriteria_thenReturnDogList() throws Exception {
        //Given
        DogDto DogDto = new DogDto("Corgi", 15, "England", true, 1);
        when(dogService.searchDogsByCriteria(new DogFilter(null, 15, null, true)))
                .thenReturn(List.of(DogDto));

        //When
        MvcResult mvcResult = mockMvc.perform(get(DogController.DOGS_URL + "/search?averageLifeExpectancy=15&easyToTrain=true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        List<DogDto> actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<DogDto>>() {
        });
        assertEquals(1, actual.size());
        assertEquals(15, actual.get(0).getAverageLifeExpectancy());
        assertEquals(true, actual.get(0).getEasyToTrain());
    }

    @Test
    void whenUpdateDog_thenReturnDogDto() throws Exception {
        //Given
        DogDto DogDto = new DogDto("Corgi", 15, "England", true, 1);
        DogRequestDto dogRequestDto = new DogRequestDto("Corgi", 15, "England", true);
        when(dogService.updateDog(1, dogRequestDto))
                .thenReturn(DogDto);

        //When
        MvcResult mvcResult = mockMvc.perform(patch(DogController.DOGS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dogRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        DogDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), DogDto.class);
        assertEquals(DogDto, actual);
    }

    @Test
    void whenDeleteDog_thenNoContent() throws Exception {
        //When
        mockMvc.perform(delete(DogController.DOGS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //Then
        verify(dogService).deleteDog(1);
    }

    private static Stream<Arguments> provideInvalidRequestBody() {
        return Stream.of(
                Arguments.of(" ", 15, "England", true),
                Arguments.of(null, 15, "England", true),
                Arguments.of("Corgi", -15, "England", true),
                Arguments.of("Corgi", null, "England", true),
                Arguments.of("Corgi", 15, "EnglandEnglandEnglandEnglandEnglandEnglandEnglandEnglandEnglandEngland", true),
                Arguments.of("Corgi", 15, "", true)
        );
    }
}
