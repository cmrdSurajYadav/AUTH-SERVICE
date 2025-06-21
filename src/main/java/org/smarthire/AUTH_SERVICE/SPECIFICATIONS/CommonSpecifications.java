package org.smarthire.AUTH_SERVICE.SPECIFICATIONS;


import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@Component
public class CommonSpecifications {

    // Equals
    public static <T> Specification<T> fieldEquals(String fieldName, Object value) {
        return (root, query, cb) -> cb.equal(root.get(fieldName), value);
    }

    // Not Equals
    public static <T> Specification<T> fieldNotEquals(String fieldName, Object value) {
        return (root, query, cb) -> cb.notEqual(root.get(fieldName), value);
    }

    // Like (case-insensitive)
    public static <T> Specification<T> likeIgnoreCase(String fieldName, String value) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get(fieldName)), "%" + value.toLowerCase() + "%");
    }

    // Starts with
    public static <T> Specification<T> startsWithIgnoreCase(String fieldName, String value) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get(fieldName)), value.toLowerCase() + "%");
    }

    // Ends with
    public static <T> Specification<T> endsWithIgnoreCase(String fieldName, String value) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get(fieldName)), "%" + value.toLowerCase());
    }

    // In collection
    public static <T> Specification<T> inCollection(String fieldName, Collection<?> values) {
        return (root, query, cb) -> root.get(fieldName).in(values);
    }

    // Is null
    public static <T> Specification<T> isNull(String fieldName) {
        return (root, query, cb) -> cb.isNull(root.get(fieldName));
    }

    // Is not null
    public static <T> Specification<T> isNotNull(String fieldName) {
        return (root, query, cb) -> cb.isNotNull(root.get(fieldName));
    }

    // Greater than (for Comparable types)
// Greater than
    public static <T, Y extends Comparable<? super Y>> Specification<T> greaterThan(String fieldName, Y value) {
        return (root, query, cb) -> cb.greaterThan(root.get(fieldName), value);
    }

    // Less than
    public static <T, Y extends Comparable<? super Y>> Specification<T> lessThan(String fieldName, Y value) {
        return (root, query, cb) -> cb.lessThan(root.get(fieldName), value);
    }

    // Greater than or equal to
    public static <T, Y extends Comparable<? super Y>> Specification<T> greaterThanOrEqualTo(String fieldName, Y value) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(fieldName), value);
    }

    // Less than or equal to
    public static <T, Y extends Comparable<? super Y>> Specification<T> lessThanOrEqualTo(String fieldName, Y value) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(fieldName), value);
    }

    // Between (e.g., for dates or numbers)
    public static <T, Y extends Comparable<? super Y>> Specification<T> between(String fieldName, Y start, Y end) {
        return (root, query, cb) -> cb.between(root.get(fieldName), start, end);
    }

    // Exact date (for LocalDate/LocalDateTime)
    public static <T> Specification<T> dateEquals(String fieldName, LocalDate date) {
        return (root, query, cb) -> cb.equal(root.get(fieldName), date);
    }

    // Order by ascending (usage in service layer via Sort)
    public static Sort sortByAsc(String fieldName) {
        return Sort.by(Sort.Direction.ASC, fieldName);
    }

    // Order by descending
    public static Sort sortByDesc(String fieldName) {
        return Sort.by(Sort.Direction.DESC, fieldName);
    }

}

// Between (e.g., for dates or numbers)


