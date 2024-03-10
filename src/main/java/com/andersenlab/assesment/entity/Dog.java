package com.andersenlab.assesment.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dog")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "breed", "averageLifeExpectancy", "originCountry", "easyToTrain"})
@ToString(exclude = "owners")
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String breed;

    private Integer averageLifeExpectancy;

    private Boolean easyToTrain;

    private String originCountry;

    @ManyToMany(mappedBy = "dogs")
    private List<Owner> owners = new ArrayList<>();
}
