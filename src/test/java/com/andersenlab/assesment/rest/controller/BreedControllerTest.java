package com.andersenlab.assesment.rest.controller;

import com.andersenlab.assesment.dto.breed.BreedDto;
import com.andersenlab.assesment.dto.breed.BreedRequestDto;
import com.andersenlab.assesment.exception.model.ErrorCode;
import com.andersenlab.assesment.service.BreedService;
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

import static com.andersenlab.assesment.data.BreedTestBuilder.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BreedController.class)
class BreedControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BreedService breedService;
    @Autowired
    private ObjectMapper objectMapper;
    private BreedRequestDto breedRequestDto;
    private BreedDto breedDto;
    private JwtRequestPostProcessor jwtRequestPostProcessor;

    @BeforeEach
    void init() {
        breedRequestDto = aBreedTest().buildBreedRequestDto();
        breedDto = aBreedTest().buildBreedDto();
        jwtRequestPostProcessor = jwt().jwt(builder -> builder.tokenValue("value")
                .claim("realm_access", Map.of("roles", List.of("USER", "ADMIN")))
                .claim("email", "hard@gmail.com")
                .header("Authorization", "Bearer value"));
    }

    @Test
    void whenCreateBreed_thenCreated() throws Exception {
        //Given
        when(breedService.createBreed(breedRequestDto))
                .thenReturn(breedDto);

        //When
        mockMvc.perform(post(BreedController.BREEDS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor)
                        .content(objectMapper.writeValueAsString(breedRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.breedName").value(breedDto.getBreedName()))
                .andExpect(jsonPath("$.averageLifeExpectancy").value(breedDto.getAverageLifeExpectancy()))
                .andExpect(jsonPath("$.originCountry").value(breedDto.getOriginCountry()))
                .andExpect(jsonPath("$.easyToTrain").value(breedDto.getEasyToTrain()))
                .andExpect(jsonPath("$.id").value(breedDto.getId()));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRequestBody")
    void whenCreateBreedWithInvalidRequestBody_thenBadRequest(BreedRequestDto breedRequestDto, ErrorCode errorCode) throws Exception {
        //When
        mockMvc.perform(post(BreedController.BREEDS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor)
                        .content(objectMapper.writeValueAsString(breedRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.name()));
    }

    @Test
    void whenGetBreedById_thenReturnBreedDto() throws Exception {
        //Given
        when(breedService.getBreed(breedDto.getId()))
                .thenReturn(breedDto);

        //When
        mockMvc.perform(get(BreedController.BREEDS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.breedName").value(breedDto.getBreedName()))
                .andExpect(jsonPath("$.averageLifeExpectancy").value(breedDto.getAverageLifeExpectancy()))
                .andExpect(jsonPath("$.originCountry").value(breedDto.getOriginCountry()))
                .andExpect(jsonPath("$.easyToTrain").value(breedDto.getEasyToTrain()))
                .andExpect(jsonPath("$.id").value(breedDto.getId()));

        //Then
        verify(breedService, never()).createBreed(any());
    }

    @Test
    void whenGetAllBreeds_thenReturnPage() throws Exception {
        //Given
        Pageable pageable = Pageable.ofSize(2);
        Page<BreedDto> breedDtoPage = new PageImpl<>(List.of(breedDto, breedDto));
        when(breedService.getAllBreeds(pageable))
                .thenReturn(breedDtoPage);
        //When
        mockMvc.perform(get(BreedController.BREEDS_URL)
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(breedDtoPage.getTotalElements()));
    }

    @Test
    void whenSearchBreedsByCriteria_thenReturnBreedList() throws Exception {
        //Given
        when(breedService.searchBreedsByCriteria(any()))
                .thenReturn(List.of(breedDto));

        //When
        mockMvc.perform(get(BreedController.BREEDS_URL + "/search?originCountry=England")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(equalTo(1))))
                .andExpect(jsonPath("$[0].breedName").value(breedDto.getBreedName()))
                .andExpect(jsonPath("$[0].originCountry").value(breedDto.getOriginCountry()))
                .andExpect(jsonPath("$[0].averageLifeExpectancy").value(breedDto.getAverageLifeExpectancy()))
                .andExpect(jsonPath("$[0].easyToTrain").value(breedDto.getEasyToTrain()));
    }

    @Test
    void whenUpdateBreed_thenUpdateBreed() throws Exception {
        //Given
        when(breedService.updateBreed(1, breedRequestDto))
                .thenReturn(breedDto);

        //When
        mockMvc.perform(patch(BreedController.BREEDS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(breedRequestDto))
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.breedName").value(breedDto.getBreedName()))
                .andExpect(jsonPath("$.averageLifeExpectancy").value(breedDto.getAverageLifeExpectancy()))
                .andExpect(jsonPath("$.originCountry").value(breedDto.getOriginCountry()))
                .andExpect(jsonPath("$.easyToTrain").value(breedDto.getEasyToTrain()))
                .andExpect(jsonPath("$.id").value(breedDto.getId()));
    }

    @Test
    void whenDeleteBreed_thenNoContent() throws Exception {
        //When
        mockMvc.perform(delete(BreedController.BREEDS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isNoContent());

        //Then
        verify(breedService).deleteBreed(1);
    }

    private static Stream<Arguments> provideInvalidRequestBody() {
        return Stream.of(
                Arguments.of(aBreedTest().withBreedName(" ").buildBreedRequestDto(), ErrorCode.ERR002),
                Arguments.of(aBreedTest().withAverageLifeExpectancy(null).buildBreedRequestDto(), ErrorCode.ERR002),
                Arguments.of(aBreedTest().withAverageLifeExpectancy(-12).buildBreedRequestDto(), ErrorCode.ERR003),
                Arguments.of(aBreedTest().withOriginCountry("").buildBreedRequestDto(), ErrorCode.ERR002),
                Arguments.of(aBreedTest().withOriginCountry("EnglandEnglandEnglandEnglandEnglandEnglandEnglandEnglandEnglandEnglandEngland").buildBreedRequestDto(), ErrorCode.ERR001),
                Arguments.of(aBreedTest().withEasyToTrain(null).buildBreedRequestDto(), ErrorCode.ERR002)
        );
    }
}
