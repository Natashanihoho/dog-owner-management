package com.andersenlab.assesment.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DogDto extends DogRequestDto {

    private Integer id;

    public DogDto(String breed, Integer averageLifeExpectancy, String originCountry, Boolean easyToTrain, Integer id) {
        super(breed, averageLifeExpectancy, originCountry, easyToTrain);
        this.id = id;
    }
}
