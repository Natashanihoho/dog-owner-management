package com.andersenlab.assesment.dto.breed;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BreedDto extends BreedRequestDto {

    private Integer id;

    public BreedDto(String breedName, Integer averageLifeExpectancy, String originCountry, Boolean easyToTrain, Integer id) {
        super(breedName, averageLifeExpectancy, originCountry, easyToTrain);
        this.id = id;
    }
}
