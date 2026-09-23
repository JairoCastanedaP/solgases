package com.solgases.unitofmeasure.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class UnitOfMeasureRequestValidationTest {

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
    void validRequestHasNoViolations() {
        assertThat(validator.validate(new UnitOfMeasureRequest("UN", "Unidad"))).isEmpty();
    }

    @Test
    void codeWithMaxLengthIsValid() {
        assertThat(validator.validate(new UnitOfMeasureRequest("a".repeat(20), "Unidad"))).isEmpty();
    }

    @Test
    void nameWithMaxLengthIsValid() {
        assertThat(validator.validate(new UnitOfMeasureRequest("UN", "a".repeat(100)))).isEmpty();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void nullEmptyOrBlankCodeIsRejected(String code) {
        Set<ConstraintViolation<UnitOfMeasureRequest>> violations =
                validator.validate(new UnitOfMeasureRequest(code, "Unidad"));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("code");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void nullEmptyOrBlankNameIsRejected(String name) {
        Set<ConstraintViolation<UnitOfMeasureRequest>> violations =
                validator.validate(new UnitOfMeasureRequest("UN", name));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("name");
    }

    @Test
    void codeLongerThanMaxLengthIsRejected() {
        assertThat(validator.validate(new UnitOfMeasureRequest("a".repeat(21), "Unidad"))).hasSize(1);
    }

    @Test
    void nameLongerThanMaxLengthIsRejected() {
        assertThat(validator.validate(new UnitOfMeasureRequest("UN", "a".repeat(101)))).hasSize(1);
    }
}
