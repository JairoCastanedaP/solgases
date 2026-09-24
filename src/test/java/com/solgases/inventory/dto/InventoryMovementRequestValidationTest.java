package com.solgases.inventory.dto;

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

class InventoryMovementRequestValidationTest {

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

    private static InventoryMovementRequest request(BigDecimal quantity, String reason, String responsibleUser) {
        return new InventoryMovementRequest(quantity, reason, responsibleUser);
    }

    @Test
    void validRequestHasNoViolations() {
        assertThat(validator.validate(request(new BigDecimal("10.000"), "Compra a proveedor", "jcastaneda"))).isEmpty();
    }

    @Test
    void quantityWithThreeDecimalsIsValid() {
        assertThat(validator.validate(request(new BigDecimal("1.001"), "Reason", "user"))).isEmpty();
    }

    @Test
    void nullQuantityIsRejected() {
        Set<ConstraintViolation<InventoryMovementRequest>> violations =
                validator.validate(request(null, "Reason", "user"));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("quantity");
    }

    @Test
    void zeroQuantityIsRejected() {
        assertThat(validator.validate(request(BigDecimal.ZERO, "Reason", "user"))).hasSize(1);
    }

    @Test
    void negativeQuantityIsRejected() {
        assertThat(validator.validate(request(new BigDecimal("-1"), "Reason", "user"))).hasSize(1);
    }

    @Test
    void quantityWithMoreThanThreeDecimalsIsRejected() {
        Set<ConstraintViolation<InventoryMovementRequest>> violations =
                validator.validate(request(new BigDecimal("1.0001"), "Reason", "user"));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("quantity");
    }

    @Test
    void quantityWithMoreThanTwelveIntegerDigitsIsRejected() {
        assertThat(validator.validate(request(new BigDecimal("1234567890123"), "Reason", "user"))).hasSize(1);
    }

    @Test
    void blankReasonIsRejected() {
        Set<ConstraintViolation<InventoryMovementRequest>> violations =
                validator.validate(request(BigDecimal.ONE, "   ", "user"));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("reason");
    }

    @Test
    void reasonLongerThan255CharactersIsRejected() {
        assertThat(validator.validate(request(BigDecimal.ONE, "a".repeat(256), "user"))).hasSize(1);
    }

    @Test
    void blankResponsibleUserIsRejected() {
        Set<ConstraintViolation<InventoryMovementRequest>> violations =
                validator.validate(request(BigDecimal.ONE, "Reason", ""));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("responsibleUser");
    }

    @Test
    void responsibleUserLongerThan255CharactersIsRejected() {
        assertThat(validator.validate(request(BigDecimal.ONE, "Reason", "a".repeat(256)))).hasSize(1);
    }
}
