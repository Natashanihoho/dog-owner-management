package com.andersenlab.assesment.dto;

public record DogFilter(String breed,
                        Integer averageLifeExpectancy,
                        String originCountry,
                        Boolean easyToTrain) {
}
