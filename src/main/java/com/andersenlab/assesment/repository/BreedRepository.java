package com.andersenlab.assesment.repository;

import com.andersenlab.assesment.entity.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BreedRepository extends JpaRepository<Breed, Integer>, JpaSpecificationExecutor<Breed> {

    Optional<Breed> findByBreedName(String breedName);

    boolean existsByBreedNameIgnoreCase(String breed);
}
