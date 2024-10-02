package com.andersenlab.assesment.dto.owner;

import com.andersenlab.assesment.exception.model.ErrorMessage;
import com.andersenlab.assesment.rest.validator.group.CreateValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatchOwnerDto {

    @NotBlank(message = ErrorMessage.ERR002_MESSAGE, groups = CreateValidation.class)
    @Length(max = 64, message = ErrorMessage.ERR001_MESSAGE)
    private String firstName;

    @NotBlank(message = ErrorMessage.ERR002_MESSAGE, groups = CreateValidation.class)
    @Length(max = 64, message = ErrorMessage.ERR001_MESSAGE)
    private String lastName;

    @NotNull(message = ErrorMessage.ERR002_MESSAGE, groups = CreateValidation.class)
    @Positive(message = ErrorMessage.ERR003_MESSAGE)
    private Integer age;

    @NotBlank(message = ErrorMessage.ERR002_MESSAGE, groups = CreateValidation.class)
    @Length(max = 64, message = ErrorMessage.ERR001_MESSAGE)
    private String city;
}
