package com.andersenlab.assesment.dto.dog;

import java.time.LocalDate;

public record DogFilter(String name, LocalDate dateOfBirth, Integer ownerId) {
}
