package com.solgases.product.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.solgases.product.entity.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;

class ProductSpecificationsTest {

    // --- likePattern: escaping of SQL wildcards and case normalization (Hallazgos 2 y 4) ---
    // These are pure functions, so they are verified directly without involving the Criteria API.

    @Test
    void likePatternLowercasesPlainText() {
        assertThat(ProductSpecifications.likePattern("Casco")).isEqualTo("%casco%");
    }

    @Test
    void likePatternNormalizesAnyCasingToTheSamePattern() {
        assertThat(ProductSpecifications.likePattern("CASCO")).isEqualTo("%casco%");
        assertThat(ProductSpecifications.likePattern("casco")).isEqualTo("%casco%");
        assertThat(ProductSpecifications.likePattern("CaScO")).isEqualTo("%casco%");
    }

    @Test
    void likePatternEscapesPercent() {
        assertThat(ProductSpecifications.likePattern("50%")).isEqualTo("%50\\%%");
    }

    @Test
    void likePatternEscapesUnderscore() {
        assertThat(ProductSpecifications.likePattern("Guante_M")).isEqualTo("%guante\\_m%");
    }

    @Test
    void likePatternEscapesBackslash() {
        assertThat(ProductSpecifications.likePattern("a\\b")).isEqualTo("%a\\\\b%");
    }

    // --- nameContains: absent filter short-circuits without touching the Criteria API ---

    @Test
    void nameContainsReturnsNullPredicateWhenNameIsAbsent() {
        Root<Product> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder builder = mock(CriteriaBuilder.class);

        assertThat(ProductSpecifications.nameContains(null).toPredicate(root, query, builder)).isNull();
    }

    @Test
    void hasCategoryIdReturnsNullPredicateWhenAbsent() {
        Root<Product> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder builder = mock(CriteriaBuilder.class);

        assertThat(ProductSpecifications.hasCategoryId(null).toPredicate(root, query, builder)).isNull();
    }

    @Test
    void hasActiveReturnsNullPredicateWhenAbsent() {
        Root<Product> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder builder = mock(CriteriaBuilder.class);

        assertThat(ProductSpecifications.hasActive(null).toPredicate(root, query, builder)).isNull();
    }

    // --- fetchCategoryAndUnitOfMeasure: N+1 fix (Hallazgo 3) ---

    @Test
    void fetchCategoryAndUnitOfMeasureFetchesBothAssociationsForAnEntityQuery() {
        Root<Product> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder builder = mock(CriteriaBuilder.class);
        when(query.getResultType()).thenReturn((Class) Product.class);

        Predicate result = ProductSpecifications.fetchCategoryAndUnitOfMeasure().toPredicate(root, query, builder);

        verify(root).fetch("category", JoinType.LEFT);
        verify(root).fetch("unitOfMeasure", JoinType.LEFT);
        assertThat(result).isNull();
    }

    @Test
    void fetchCategoryAndUnitOfMeasureSkipsFetchingForCountQueries() {
        Root<Product> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder builder = mock(CriteriaBuilder.class);
        when(query.getResultType()).thenReturn((Class) Long.class);

        ProductSpecifications.fetchCategoryAndUnitOfMeasure().toPredicate(root, query, builder);

        verify(root, never()).fetch(anyString(), any(JoinType.class));
    }
}
