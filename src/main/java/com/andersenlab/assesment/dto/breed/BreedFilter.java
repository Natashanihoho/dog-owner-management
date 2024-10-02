package com.andersenlab.assesment.dto.breed;

public record BreedFilter(String breedName,
                          Integer averageLifeExpectancy,
                          String originCountry,
                          Boolean easyToTrain) {
}
