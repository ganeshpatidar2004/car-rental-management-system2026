package com.carrental.specification;

import com.carrental.entity.Car;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specifications for dynamic searching and filtering of Car entities.
 * Executes filtering directly within the database query avoiding in-memory overhead.
 */
public final class CarSpecification {

    private CarSpecification() {
        // Prevent instantiation of utility class
    }

    /**
     * Builds a composite Specification combining all supplied search criteria.
     *
     * @param keyword search term for brand or model
     * @param brand specific brand filter
     * @param minPrice minimum pricePerDay
     * @param maxPrice maximum pricePerDay
     * @param available vehicle availability status
     * @return JPA Specification for Car
     */
    public static Specification<Car> filter(
            String keyword,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean available) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate brandMatch = cb.like(cb.lower(root.get("brand")), pattern);
                Predicate modelMatch = cb.like(cb.lower(root.get("model")), pattern);
                predicates.add(cb.or(brandMatch, modelMatch));
            }

            if (brand != null && !brand.trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("brand")), brand.trim().toLowerCase()));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("pricePerDay"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("pricePerDay"), maxPrice));
            }

            if (available != null) {
                predicates.add(cb.equal(root.get("available"), available));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
