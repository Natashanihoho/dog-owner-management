package com.andersenlab.assesment.dto.dog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CreateDogDto extends PatchDogDto {

    private String breed;

    public CreateDogDto(String name, LocalDate dateOfBirth, String breed) {
        super(name, dateOfBirth);
        this.breed= breed;
    }
}
