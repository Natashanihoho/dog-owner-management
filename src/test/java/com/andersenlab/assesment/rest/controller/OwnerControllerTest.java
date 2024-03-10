package com.andersenlab.assesment.rest.controller;

import com.andersenlab.assesment.dto.OwnerDogLinkDto;
import com.andersenlab.assesment.dto.OwnerDto;
import com.andersenlab.assesment.dto.OwnerFilter;
import com.andersenlab.assesment.dto.OwnerRequestDto;
import com.andersenlab.assesment.service.OwnerService;
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OwnerService ownerService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenCreateOwner_thenCreated() throws Exception {
        //Given
        OwnerRequestDto ownerRequestDto = new OwnerRequestDto("Alice", "Smith", 30, "Gdansk");
        OwnerDto ownerDto = new OwnerDto("Alice", "Smith", 30, "Gdansk", 1, Collections.emptyList());
        when(ownerService.createOwner(ownerRequestDto))
                .thenReturn(ownerDto);

        //When
        mockMvc.perform(post(OwnerController.OWNERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerRequestDto)))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRequestBody")
    void whenCreateOwnerWithInvalidRequestBody_thenBadRequest(String firstName, String lastName, Integer age, String city) throws Exception {
        //Given
        OwnerRequestDto ownerRequestDto = new OwnerRequestDto(firstName, lastName, age, city);

        //When
        mockMvc.perform(post(OwnerController.OWNERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetOwnerById_thenReturnOwnerDto() throws Exception {
        //Given
        OwnerDto ownerDto = new OwnerDto("Alice", "Smith", 30, "Gdansk", 1, Collections.emptyList());
        when(ownerService.getOwner(ownerDto.getId()))
                .thenReturn(ownerDto);

        //When
        MvcResult mvcResult = mockMvc.perform(get(OwnerController.OWNERS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        OwnerDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), OwnerDto.class);
        assertEquals(ownerDto, actual);
    }

    @Test
    void whenGetAllOwners_thenReturnPage() throws Exception {
        //Given
        OwnerDto ownerDto = new OwnerDto("Alice", "Smith", 30, "Gdansk", 1, Collections.emptyList());
        Pageable pageable = Pageable.ofSize(2);
        Page<OwnerDto> ownerDtoPage = new PageImpl<>(List.of(ownerDto, ownerDto));
        when(ownerService.getAllOwners(pageable))
                .thenReturn(ownerDtoPage);
        //When
        MvcResult result = mockMvc.perform(get(OwnerController.OWNERS_URL)
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    void whenSearchOwnersByCriteria_thenReturnOwnerList() throws Exception {
        //Given
        OwnerDto ownerDto = new OwnerDto("Alice", "Smith", 30, "Gdansk", 1, Collections.emptyList());
        when(ownerService.searchOwnersByCriteria(new OwnerFilter(null, null, 30, "Gdansk")))
                .thenReturn(List.of(ownerDto));

        //When
        MvcResult mvcResult = mockMvc.perform(get(OwnerController.OWNERS_URL + "/search?age=30&city=Gdansk")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        List<OwnerDto> actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<OwnerDto>>() {
        });
        assertEquals(1, actual.size());
        assertEquals("Gdansk", actual.get(0).getCity());
        assertEquals(30, actual.get(0).getAge());
    }

    @Test
    void whenUpdateOwner_thenReturnOwnerDto() throws Exception {
        //Given
        OwnerDto ownerDto = new OwnerDto("Alice", "Smith", 30, "Gdansk", 1, Collections.emptyList());
        OwnerRequestDto ownerRequestDto = new OwnerRequestDto("Alice", "Smith", 30, "Gdansk");
        when(ownerService.updateOwner(1, ownerRequestDto))
                .thenReturn(ownerDto);

        //When
        MvcResult mvcResult = mockMvc.perform(patch(OwnerController.OWNERS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        OwnerDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), OwnerDto.class);
        assertEquals(ownerDto, actual);
    }

    @Test
    void whenUpdateOwnerWithDogs_thenReturnOwnerDto() throws Exception {
        //Given
        OwnerDto ownerDto = new OwnerDto("Alice", "Smith", 30, "Gdansk", 1, List.of("Corgi", "Beagle"));
        OwnerDogLinkDto ownerDogLinkDto = new OwnerDogLinkDto(List.of("Corgi", "Beagle"));
        when(ownerService.addDogsToOwner(1, ownerDogLinkDto))
                .thenReturn(ownerDto);

        //When
        MvcResult mvcResult = mockMvc.perform(patch(OwnerController.OWNERS_URL + "/1/dogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerDogLinkDto)))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        OwnerDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), OwnerDto.class);
        assertEquals(ownerDto, actual);
    }

    @Test
    void whenDeleteOwner_thenNoContent() throws Exception {
        //When
        mockMvc.perform(delete(OwnerController.OWNERS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //Then
        verify(ownerService).deleteOwner(1);
    }

    @Test
    void whenRemoveDogsFromOwner_thenNoContent() throws Exception {
        //When
        mockMvc.perform(delete(OwnerController.OWNERS_URL + "/1/dogs")
                        .param("breeds", "Corgi,Beagle")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //Then
        verify(ownerService).removeDogsFromOwner(1, List.of("Corgi", "Beagle"));
    }

    private static Stream<Arguments> provideInvalidRequestBody() {
        return Stream.of(
                Arguments.of("InvalidSizeOfFirstNameMoreThan128CharactersProvideddddddddddddddddd", "Smith", 30, "Gdansk"),
                Arguments.of(null, "Smith", 30, "Gdansk"),
                Arguments.of("Alice", "InvalidSizeOfLastNameMoreThan128CharactersProvideddddddddddddddddd", 30, "Gdansk"),
                Arguments.of("Alice", " ", 30, "Gdansk"),
                Arguments.of("Alice", "Smith", -30, "Gdansk"),
                Arguments.of("Alice", "Smith", null, "Gdansk"),
                Arguments.of("Alice", "Smith", 30, "GdanskGdanskGdanskGdanskGdanskGdanskGdanskGdanskGdanskGdanskGdansk"),
                Arguments.of("Alice", "Smith", 30, "")
        );
    }
}
