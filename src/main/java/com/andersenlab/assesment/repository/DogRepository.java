package com.andersenlab.assesment.repository;

import com.andersenlab.assesment.entity.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DogRepository extends JpaRepository<Dog, Integer>, JpaSpecificationExecutor<Dog> {

    List<Dog> findAllByBreedIgnoreCaseIn(List<String> breed);

    boolean existsByBreedIgnoreCase(String breed);
}
