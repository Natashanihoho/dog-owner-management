package com.andersenlab.assesment.service;

import com.andersenlab.assesment.dto.owner.OwnerDto;
import com.andersenlab.assesment.dto.owner.OwnerFilter;
import com.andersenlab.assesment.dto.owner.CreateOwnerDto;
import com.andersenlab.assesment.dto.owner.PatchOwnerDto;
import com.andersenlab.assesment.entity.Owner;
import com.andersenlab.assesment.entity.Owner_;
import com.andersenlab.assesment.exception.ActionNotAllowedException;
import com.andersenlab.assesment.exception.ResourceAlreadyExistsException;
import com.andersenlab.assesment.exception.model.ErrorCode;
import com.andersenlab.assesment.exception.ResourceNotFoundException;
import com.andersenlab.assesment.mapper.OwnerMapper;
import com.andersenlab.assesment.repository.OwnerRepository;
import com.andersenlab.assesment.repository.specification.ConditionSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;

    @Transactional
    public OwnerDto createOwner(CreateOwnerDto createOwnerDto) {
        Owner owner = ownerMapper.mapToOwner(createOwnerDto);
        Owner savedOwner = ownerRepository.save(owner);
        return ownerMapper.mapToOwnerDto(savedOwner);
    }

    @Transactional(readOnly = true)
    public OwnerDto getOwner(Integer ownerId) {
        return getOwnerById(ownerId)
                .map(ownerMapper::mapToOwnerDto)
                .orElseThrow(() -> resourceNotFoundException(ownerId));
    }

    @Transactional(readOnly = true)
    public Page<OwnerDto> getAllOwners(Pageable pageable) {
        return ownerRepository.findAll(pageable)
                .map(ownerMapper::mapToOwnerDto);
    }

    @Transactional(readOnly = true)
    public List<OwnerDto> searchOwnersByCriteria(OwnerFilter ownerFilter) {
        return ownerRepository.findAll(
                        new ConditionSpecification<>(
                                ownerFilter.age(), Objects::nonNull, Owner_.age
                        ).and(
                                new ConditionSpecification<>(ownerFilter.firstName(), StringUtils::isNotBlank, Owner_.firstName)
                        ).and(
                                new ConditionSpecification<>(ownerFilter.lastName(), StringUtils::isNotBlank, Owner_.lastName)
                        ).and(
                                new ConditionSpecification<>(ownerFilter.city(), StringUtils::isNotBlank, Owner_.city)
                        )
                ).stream()
                .map(ownerMapper::mapToOwnerDto)
                .toList();
    }

    @Transactional
    public OwnerDto updateOwner(Integer ownerId, PatchOwnerDto patchOwnerDto) {
        return getOwnerById(ownerId)
                .map(owner -> ownerMapper.updateOwner(owner, patchOwnerDto))
                .map(ownerMapper::mapToOwnerDto)
                .orElseThrow(() -> resourceNotFoundException(ownerId));
    }

    @Transactional
    public void deleteOwner(Integer ownerId) {
        getOwnerById(ownerId)
                .ifPresentOrElse(
                        ownerRepository::delete,
                        () -> { throw resourceNotFoundException(ownerId); }
                );
    }

    @Transactional(readOnly = true)
    public void verifyOwnerConsistency(Integer ownerId, String email) {
        getOwnerById(ownerId)
                .map(Owner::getEmail)
                .filter(mail -> mail.equals(email))
                .ifPresentOrElse(id -> log.debug("Owner consistency is verified"),
                        () -> { throw new ActionNotAllowedException(ErrorCode.ERR009, ErrorCode.ERR009.getMessage()); }
                );
    }

    Optional<Owner> getOwnerById(Integer ownerId) {
        return ownerRepository.findById(ownerId);
    }

    ResourceNotFoundException resourceNotFoundException(Integer ownerId) {
        return new ResourceNotFoundException(ErrorCode.ERR004, "Owner with id [" + ownerId + "] not found", HttpStatus.NOT_FOUND);
    }

    void verifyThatOwnerDoesNotExist(String email) {
        if(ownerRepository.existsByEmail(email)) {
            throw new ResourceAlreadyExistsException(
                    ErrorCode.ERR005, "Owner [" + email + "] already exists", HttpStatus.BAD_REQUEST
            );
        }
    }
}
