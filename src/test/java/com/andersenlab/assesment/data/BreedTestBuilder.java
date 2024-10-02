package com.andersenlab.assesment.data;

import com.andersenlab.assesment.dto.breed.BreedDto;
import com.andersenlab.assesment.dto.breed.BreedRequestDto;
import com.andersenlab.assesment.entity.Breed;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "aBreedTest")
public class BreedTestBuilder {

    private Integer id = 1;
    private String breedName = "Corgi";
    private Integer averageLifeExpectancy = 15;
    private String originCountry = "England";
    private Boolean easyToTrain = true;

    public BreedRequestDto buildBreedRequestDto() {
        return new BreedRequestDto(breedName, averageLifeExpectancy, originCountry, easyToTrain);
    }

    public BreedDto buildBreedDto() {
        return new BreedDto(breedName, averageLifeExpectancy, originCountry, easyToTrain, id);
    }

    public Breed buildBreedEntity() {
        return new Breed(id, breedName, averageLifeExpectancy, easyToTrain, originCountry);
    }
}
