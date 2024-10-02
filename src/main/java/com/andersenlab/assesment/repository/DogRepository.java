package com.andersenlab.assesment.repository;

import com.andersenlab.assesment.entity.Dog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DogRepository extends JpaRepository<Dog, Integer>, JpaSpecificationExecutor<Dog> {

    Page<Dog> findAllByOwner_Email(String email, Pageable pageable);
}
