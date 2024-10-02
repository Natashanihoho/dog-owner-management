package com.andersenlab.assesment.data;

import com.andersenlab.assesment.dto.dog.CreateDogDto;
import com.andersenlab.assesment.dto.dog.DogDto;
import com.andersenlab.assesment.dto.dog.DogFilter;
import com.andersenlab.assesment.dto.dog.PatchDogDto;
import com.andersenlab.assesment.entity.Breed;
import com.andersenlab.assesment.entity.Dog;
import com.andersenlab.assesment.entity.Owner;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDate;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "aDogTest")
public class DogTestBuilder {

    private String name = "Muffin";
    private LocalDate dateOfBirth = LocalDate.of(2020, 5, 4);
    private String breed = "Corgi";
    private Integer id = 1;
    private Integer ownerId = 1;

    public PatchDogDto buildPatchDogDto() {
        return new PatchDogDto(name, dateOfBirth);
    }

    public CreateDogDto buildCreateDogDto() {
        return new CreateDogDto(name, dateOfBirth, breed);
    }

    public DogDto buildDogDto() {
        return new DogDto(name, dateOfBirth, breed, id, ownerId);
    }

    public Dog buildDogEntity() {
        return new Dog(id, name, dateOfBirth, new Owner(), new Breed());
    }
}
