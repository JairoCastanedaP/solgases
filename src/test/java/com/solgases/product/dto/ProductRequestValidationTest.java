package com.solgases.product.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProductRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    private static ProductRequest validRequest() {
        return new ProductRequest("EPP-001", "Casco", "Casco de seguridad", "3M", "H-700",
                new BigDecimal("85000.00"), 1L, 1L);
    }

    @Test
    void validRequestHasNoViolations() {
        assertThat(validator.validate(validRequest())).isEmpty();
    }

    @Test
    void optionalFieldsMayBeNull() {
        ProductRequest request = new ProductRequest("EPP-001", "Casco", null, null, null,
                new BigDecimal("1"), 1L, 1L);

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void blankSkuIsRejected() {
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(
                new ProductRequest("", "Casco", null, null, null, new BigDecimal("1"), 1L, 1L));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("sku");
    }

    @Test
    void skuLongerThan50CharactersIsRejected() {
        ProductRequest request = new ProductRequest("a".repeat(51), "Casco", null, null, null,
                new BigDecimal("1"), 1L, 1L);

        assertThat(validator.validate(request)).hasSize(1);
    }

    @Test
    void blankNameIsRejected() {
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(
                new ProductRequest("EPP-001", "  ", null, null, null, new BigDecimal("1"), 1L, 1L));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("name");
    }

    @Test
    void nameLongerThan100CharactersIsRejected() {
        ProductRequest request = new ProductRequest("EPP-001", "a".repeat(101), null, null, null,
                new BigDecimal("1"), 1L, 1L);

        assertThat(validator.validate(request)).hasSize(1);
    }

    @Test
    void descriptionBrandAndReferenceLongerThan255AreRejected() {
        String tooLong = "a".repeat(256);
        assertThat(validator.validate(
                new ProductRequest("EPP-001", "Casco", tooLong, null, null, new BigDecimal("1"), 1L, 1L)))
                .hasSize(1);
        assertThat(validator.validate(
                new ProductRequest("EPP-001", "Casco", null, tooLong, null, new BigDecimal("1"), 1L, 1L)))
                .hasSize(1);
        assertThat(validator.validate(
                new ProductRequest("EPP-001", "Casco", null, null, tooLong, new BigDecimal("1"), 1L, 1L)))
                .hasSize(1);
    }

    @Test
    void nullPriceIsRejected() {
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(
                new ProductRequest("EPP-001", "Casco", null, null, null, null, 1L, 1L));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("price");
    }

    @Test
    void zeroOrNegativePriceIsRejected() {
        assertThat(validator.validate(
                new ProductRequest("EPP-001", "Casco", null, null, null, BigDecimal.ZERO, 1L, 1L)))
                .hasSize(1);
        assertThat(validator.validate(
                new ProductRequest("EPP-001", "Casco", null, null, null, new BigDecimal("-1"), 1L, 1L)))
                .hasSize(1);
    }

    @Test
    void priceWithTwoDecimalsIsValid() {
        assertThat(validator.validate(
                new ProductRequest("EPP-001", "Casco", null, null, null, new BigDecimal("85000.00"), 1L, 1L)))
                .isEmpty();
    }

    @Test
    void integerPriceIsValid() {
        assertThat(validator.validate(
                new ProductRequest("EPP-001", "Casco", null, null, null, new BigDecimal("85000"), 1L, 1L)))
                .isEmpty();
    }

    @Test
    void priceWithMoreThanTwoDecimalsIsRejected() {
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(
                new ProductRequest("EPP-001", "Casco", null, null, null, new BigDecimal("85000.999"), 1L, 1L));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("price");
    }

    @Test
    void nullCategoryIdIsRejected() {
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(
                new ProductRequest("EPP-001", "Casco", null, null, null, new BigDecimal("1"), null, 1L));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("categoryId");
    }

    @Test
    void nullUnitOfMeasureIdIsRejected() {
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(
                new ProductRequest("EPP-001", "Casco", null, null, null, new BigDecimal("1"), 1L, null));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("unitOfMeasureId");
    }
}
