package com.andersenlab.assesment.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "breed")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Breed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String breedName;
    private Integer averageLifeExpectancy;
    private Boolean easyToTrain;
    private String originCountry;
}
