package com.solgases.product.repository;

import com.solgases.product.entity.Product;
import jakarta.persistence.criteria.JoinType;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

/**
 * Builds the combinable, optional filters accepted by {@code GET /api/products}.
 * Each filter is applied only when its value is present.
 */
public final class ProductSpecifications {

    private static final char LIKE_ESCAPE_CHAR = '\\';

    private ProductSpecifications() {
    }

    /**
     * Partial, case-insensitive match on the product name. Case-insensitivity is guaranteed by
     * the query itself (both sides are lower-cased), independent of the database collation, and
     * any {@code %}, {@code _} or {@code \} in the given value is escaped so it is matched as a
     * literal character instead of a SQL wildcard.
     */
    public static Specification<Product> nameContains(String name) {
        return (root, query, builder) -> name == null
                ? null
                : builder.like(builder.lower(root.get("name")), likePattern(name), LIKE_ESCAPE_CHAR);
    }

    public static Specification<Product> hasCategoryId(Long categoryId) {
        return (root, query, builder) -> categoryId == null
                ? null
                : builder.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> hasActive(Boolean active) {
        return (root, query, builder) -> active == null
                ? null
                : builder.equal(root.get("active"), active);
    }

    /**
     * Eagerly loads {@code category} and {@code unitOfMeasure} in the same query, avoiding an N+1
     * round trip per product when mapping the list to {@code ProductResponse}. Both associations
     * are mandatory (NOT NULL foreign keys), so a left join does not change which rows match.
     * Skipped for count queries, where a fetch join is not applicable.
     */
    public static Specification<Product> fetchCategoryAndUnitOfMeasure() {
        return (root, query, builder) -> {
            if (!Long.class.equals(query.getResultType())) {
                root.fetch("category", JoinType.LEFT);
                root.fetch("unitOfMeasure", JoinType.LEFT);
            }
            return null;
        };
    }

    /**
     * Builds a case-insensitive "contains" LIKE pattern for {@code value}, escaping {@code %},
     * {@code _} and {@code \} so they are matched literally instead of as SQL wildcards.
     */
    static String likePattern(String value) {
        String escaped = value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        return "%" + escaped.toLowerCase(Locale.ROOT) + "%";
    }
}
