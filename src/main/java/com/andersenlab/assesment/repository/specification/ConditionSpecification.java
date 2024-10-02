package com.andersenlab.assesment.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.Specification;

import java.util.function.Predicate;

public record ConditionSpecification<T, V>(V value, Predicate<V> condition, SingularAttribute<T, V> attribute) implements Specification<T> {
    @Override
    public jakarta.persistence.criteria.Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return condition.test(value)
                ? criteriaBuilder.equal(root.get(attribute), value)
                : null;
    }
}
