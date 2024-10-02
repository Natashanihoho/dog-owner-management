package com.andersenlab.assesment.dto.dog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
public class DogDto extends CreateDogDto {

    private Integer id;
    private Integer ownerId;

    public DogDto(String name, LocalDate dateOfBirth, String breed, Integer id, Integer ownerId) {
        super(name, dateOfBirth, breed);
        this.id = id;
        this.ownerId = ownerId;
    }
}
