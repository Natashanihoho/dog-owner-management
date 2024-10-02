package com.andersenlab.assesment.dto.owner;

import com.andersenlab.assesment.exception.model.ErrorMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
public class CreateOwnerDto extends PatchOwnerDto {

    @NotBlank(message = ErrorMessage.ERR002_MESSAGE)
    @Length(max = 128, message = ErrorMessage.ERR001_MESSAGE)
    @Email(message = ErrorMessage.ERR008_MESSAGE)
    private String email;

    @NotBlank(message = ErrorMessage.ERR002_MESSAGE)
    @Length(min = 8, message = ErrorMessage.ERR001_MESSAGE)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public CreateOwnerDto(String firstName, String lastName, Integer age, String city, String email, String password) {
        super(firstName, lastName, age, city);
        this.email = email;
        this.password = password;
    }
}
