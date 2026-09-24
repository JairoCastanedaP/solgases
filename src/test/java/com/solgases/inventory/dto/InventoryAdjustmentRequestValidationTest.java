package com.solgases.inventory.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.solgases.inventory.entity.MovementDirection;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class InventoryAdjustmentRequestValidationTest {

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

    @Test
    void validIncreaseRequestHasNoViolations() {
        InventoryAdjustmentRequest request = new InventoryAdjustmentRequest(
                new BigDecimal("2.000"), MovementDirection.INCREASE, "Conteo físico", "jcastaneda");

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void validDecreaseRequestHasNoViolations() {
        InventoryAdjustmentRequest request = new InventoryAdjustmentRequest(
                new BigDecimal("2.000"), MovementDirection.DECREASE, "Conteo físico", "jcastaneda");

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void nullDirectionIsRejected() {
        InventoryAdjustmentRequest request = new InventoryAdjustmentRequest(
                BigDecimal.ONE, null, "Reason", "user");

        Set<ConstraintViolation<InventoryAdjustmentRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("direction");
    }

    @Test
    void zeroQuantityIsRejected() {
        InventoryAdjustmentRequest request = new InventoryAdjustmentRequest(
                BigDecimal.ZERO, MovementDirection.INCREASE, "Reason", "user");

        assertThat(validator.validate(request)).hasSize(1);
    }

    @Test
    void quantityWithMoreThanThreeDecimalsIsRejected() {
        InventoryAdjustmentRequest request = new InventoryAdjustmentRequest(
                new BigDecimal("1.0001"), MovementDirection.DECREASE, "Reason", "user");

        assertThat(validator.validate(request)).hasSize(1);
    }

    @Test
    void blankReasonIsRejected() {
        InventoryAdjustmentRequest request = new InventoryAdjustmentRequest(
                BigDecimal.ONE, MovementDirection.INCREASE, "", "user");

        assertThat(validator.validate(request)).hasSize(1);
    }

    @Test
    void blankResponsibleUserIsRejected() {
        InventoryAdjustmentRequest request = new InventoryAdjustmentRequest(
                BigDecimal.ONE, MovementDirection.INCREASE, "Reason", "   ");

        assertThat(validator.validate(request)).hasSize(1);
    }
}
