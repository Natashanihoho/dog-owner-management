package com.andersenlab.assesment.data;

import com.andersenlab.assesment.dto.dog.DogDto;
import com.andersenlab.assesment.dto.owner.CreateOwnerDto;
import com.andersenlab.assesment.dto.owner.OwnerDto;
import com.andersenlab.assesment.dto.owner.PatchOwnerDto;
import com.andersenlab.assesment.entity.Owner;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "aOwnerTest")
public class OwnerTestBuilder {

    private String firstName = "Natallia";
    private String lastName = "Hard";
    private Integer age = 31;
    private String city = "Gdansk";
    private String email = "hard@gmail.com";
    private String password = "12345678";
    private Integer id = 1;
    private List<DogDto> dogs = List.of();

    public PatchOwnerDto buildPatchOwnerDto() {
        return new PatchOwnerDto(firstName, lastName, age, city);
    }

    public CreateOwnerDto buildCreateOwnerDto() {
        return new CreateOwnerDto(firstName, lastName, age, city, email, password);
    }

    public String buildJsonCreateOwnerDto() {
        return String.format("""
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "age": %d,
                  "city": "%s",
                  "email": "%s",
                  "password": "%s"
                }
                """, firstName, lastName, age, city, email, password);
    }

    public OwnerDto buildOwnerDto() {
        return new OwnerDto(firstName, lastName, age, city, email, password, id, dogs);
    }

    public Owner buildOwnerEntity() {
        return new Owner(id, firstName, lastName, age, city, email, List.of());
    }
}
