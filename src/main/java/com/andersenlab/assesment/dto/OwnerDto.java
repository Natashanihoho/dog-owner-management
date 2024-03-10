package com.andersenlab.assesment.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OwnerDto extends OwnerRequestDto {

    private Integer id;
    private List<String> dogs;

    public OwnerDto(String firstName, String lastName, Integer age, String city, Integer id, List<String> dogs) {
        super(firstName, lastName, age, city);
        this.id = id;
        this.dogs = dogs;
    }
}
