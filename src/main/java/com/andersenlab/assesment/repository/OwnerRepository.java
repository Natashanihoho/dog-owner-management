package com.andersenlab.assesment.repository;

import com.andersenlab.assesment.entity.Owner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OwnerRepository extends JpaRepository<Owner, Integer>, JpaSpecificationExecutor<Owner> {

    Page<Owner> findAll(Pageable pageable);

    boolean existsByEmail(String email);


}
