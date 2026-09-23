package com.solgases.category.dto;

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

class CategoryRequestValidationTest {

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
    void validNameHasNoViolations() {
        assertThat(validator.validate(new CategoryRequest("EPP"))).isEmpty();
    }

    @Test
    void nameWithSurroundingSpacesIsAcceptedAsIs() {
        CategoryRequest request = new CategoryRequest(" Gases industriales ");

        assertThat(validator.validate(request)).isEmpty();
        assertThat(request.name()).isEqualTo(" Gases industriales ");
    }

    @Test
    void nameWithExactlyMaxLengthIsValid() {
        assertThat(validator.validate(new CategoryRequest("a".repeat(100)))).isEmpty();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "     "})
    void nullEmptyOrBlankNameIsRejected(String name) {
        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(new CategoryRequest(name));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("name");
    }

    @Test
    void nameLongerThanMaxLengthIsRejected() {
        Set<ConstraintViolation<CategoryRequest>> violations =
                validator.validate(new CategoryRequest("a".repeat(101)));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("name");
    }
}
