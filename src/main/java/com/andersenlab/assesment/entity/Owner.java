package com.andersenlab.assesment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "owner")
@Data
@NoArgsConstructor
@AllArgsConstructor
//@NamedEntityGraph(name = "owner-graph", attributeNodes = {@NamedAttributeNode("dogs")})
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Exclude
    private Integer id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String city;
    private String email;
    @OneToMany(mappedBy = "owner", cascade = {CascadeType.REMOVE})
    @ToString.Exclude
    private List<Dog> dogs = new ArrayList<>();
}
