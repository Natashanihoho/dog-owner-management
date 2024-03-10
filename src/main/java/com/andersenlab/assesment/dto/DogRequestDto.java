package com.andersenlab.assesment.dto;

import com.andersenlab.assesment.exception.model.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DogRequestDto {

    @NotBlank(message = ErrorMessage.ERR002_MESSAGE)
    @Length(max = 128, message = ErrorMessage.ERR001_MESSAGE)
    private String breed;

    @NotNull(message = ErrorMessage.ERR002_MESSAGE)
    @Positive(message = ErrorMessage.ERR003_MESSAGE)
    private Integer averageLifeExpectancy;

    @NotBlank(message = ErrorMessage.ERR002_MESSAGE)
    @Length(max = 64, message = ErrorMessage.ERR001_MESSAGE)
    private String originCountry;

    @NotNull(message = ErrorMessage.ERR002_MESSAGE)
    private Boolean easyToTrain;
}
