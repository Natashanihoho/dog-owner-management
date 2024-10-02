package com.andersenlab.assesment.dto.owner;

import com.andersenlab.assesment.dto.dog.DogDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDto extends CreateOwnerDto {

    private Integer id;
    private List<DogDto> dogs;

    public OwnerDto(String firstName, String lastName, Integer age, String city, String email,
                    String password, Integer id, List<DogDto> dogs) {
        super(firstName, lastName, age, city, email, password);
        this.id = id;
        this.dogs = dogs;
    }
}
