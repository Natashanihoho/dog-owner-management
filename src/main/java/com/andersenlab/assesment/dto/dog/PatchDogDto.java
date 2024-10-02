package com.andersenlab.assesment.dto.dog;

import com.andersenlab.assesment.exception.model.ErrorMessage;
import com.andersenlab.assesment.rest.validator.group.CreateValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatchDogDto {

    @NotBlank(message = ErrorMessage.ERR002_MESSAGE, groups = CreateValidation.class)
    @Length(max = 128, message = ErrorMessage.ERR001_MESSAGE)
    private String name;
    @PastOrPresent(message = ErrorMessage.ERR006_MESSAGE)
    private LocalDate dateOfBirth;
}
