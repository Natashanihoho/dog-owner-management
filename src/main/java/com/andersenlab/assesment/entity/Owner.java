package com.andersenlab.assesment.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "owner")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "firstName", "lastName", "age", "city"})
@ToString(exclude = "dogs")
@NamedEntityGraph(name = "owner-graph", attributeNodes = {@NamedAttributeNode("dogs")})
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstName;

    private String lastName;

    private Integer age;

    private String city;

    @ManyToMany
    @JoinTable(
            name = "dog_owner",
            joinColumns = @JoinColumn(name = "owner_id"),
            inverseJoinColumns = @JoinColumn(name = "dog_id")
    )
    private List<Dog> dogs = new ArrayList<>();

    public void addDogs(List<Dog> dogs) {
        this.dogs.addAll(dogs);
        dogs.forEach(dog -> dog.getOwners().add(this));
    }

    public void removeDogs(List<Dog> dogs) {
        this.dogs.removeAll(dogs);
        dogs.forEach(dog -> dog.getOwners().remove(this));
    }
}
