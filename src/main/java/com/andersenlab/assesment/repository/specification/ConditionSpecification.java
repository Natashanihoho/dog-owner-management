package com.andersenlab.assesment.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.function.Predicate;

public record ConditionSpecification<T, V>(V value, Predicate<V> condition, SingularAttribute<T, V> attribute) implements Specification<T> {
    @Override
    public javax.persistence.criteria.Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return condition.test(value)
                ? criteriaBuilder.equal(root.get(attribute), value)
                : null;
    }
}
