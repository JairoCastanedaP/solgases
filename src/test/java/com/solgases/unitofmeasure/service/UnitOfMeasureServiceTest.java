package com.solgases.unitofmeasure.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.solgases.exception.ResourceConflictException;
import com.solgases.exception.ResourceNotFoundException;
import com.solgases.unitofmeasure.dto.UnitOfMeasureRequest;
import com.solgases.unitofmeasure.dto.UnitOfMeasureResponse;
import com.solgases.unitofmeasure.entity.UnitOfMeasure;
import com.solgases.unitofmeasure.repository.UnitOfMeasureRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UnitOfMeasureServiceTest {

    @Mock
    private UnitOfMeasureRepository unitOfMeasureRepository;

    @InjectMocks
    private UnitOfMeasureService unitOfMeasureService;

    private static UnitOfMeasure unit(Long id, String code, String name, boolean active) {
        UnitOfMeasure unit = new UnitOfMeasure(code, name);
        ReflectionTestUtils.setField(unit, "id", id);
        if (!active) {
            unit.deactivate();
        }
        return unit;
    }

    @Test
    void createSavesNewActiveUnit() {
        when(unitOfMeasureRepository.existsByCode("UN")).thenReturn(false);
        when(unitOfMeasureRepository.saveAndFlush(any(UnitOfMeasure.class))).thenAnswer(invocation -> {
            UnitOfMeasure toSave = invocation.getArgument(0);
            ReflectionTestUtils.setField(toSave, "id", 1L);
            return toSave;
        });

        UnitOfMeasureResponse response = unitOfMeasureService.create(new UnitOfMeasureRequest("UN", "Unidad"));

        assertThat(response).isEqualTo(new UnitOfMeasureResponse(1L, "UN", "Unidad", true));
    }

    @Test
    void createWithExistingCodeThrowsConflictWithoutSaving() {
        when(unitOfMeasureRepository.existsByCode("UN")).thenReturn(true);

        assertThatThrownBy(() -> unitOfMeasureService.create(new UnitOfMeasureRequest("UN", "Unidad")))
                .isInstanceOf(ResourceConflictException.class);

        verify(unitOfMeasureRepository, never()).saveAndFlush(any());
    }

    @Test
    void createTranslatesDatabaseUniqueViolationIntoConflict() {
        when(unitOfMeasureRepository.existsByCode("UN")).thenReturn(false);
        when(unitOfMeasureRepository.saveAndFlush(any(UnitOfMeasure.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThatThrownBy(() -> unitOfMeasureService.create(new UnitOfMeasureRequest("UN", "Unidad")))
                .isInstanceOf(ResourceConflictException.class);
    }

    @Test
    void findAllReturnsActiveAndInactiveUnits() {
        when(unitOfMeasureRepository.findAll()).thenReturn(List.of(
                unit(1L, "UN", "Unidad", true),
                unit(2L, "KG", "Kilogramo", false)));

        assertThat(unitOfMeasureService.findAll()).containsExactly(
                new UnitOfMeasureResponse(1L, "UN", "Unidad", true),
                new UnitOfMeasureResponse(2L, "KG", "Kilogramo", false));
    }

    @Test
    void findByIdReturnsExistingUnit() {
        when(unitOfMeasureRepository.findById(1L)).thenReturn(Optional.of(unit(1L, "UN", "Unidad", true)));

        assertThat(unitOfMeasureService.findById(1L)).isEqualTo(new UnitOfMeasureResponse(1L, "UN", "Unidad", true));
    }

    @Test
    void findByIdThrowsNotFoundWhenMissing() {
        when(unitOfMeasureRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> unitOfMeasureService.findById(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateChangesCodeAndNameAndKeepsActiveFlag() {
        UnitOfMeasure inactive = unit(1L, "UN", "Unidad", false);
        when(unitOfMeasureRepository.findById(1L)).thenReturn(Optional.of(inactive));
        when(unitOfMeasureRepository.existsByCodeAndIdNot("KG", 1L)).thenReturn(false);
        when(unitOfMeasureRepository.saveAndFlush(inactive)).thenReturn(inactive);

        UnitOfMeasureResponse response = unitOfMeasureService.update(1L, new UnitOfMeasureRequest("KG", "Kilogramo"));

        assertThat(response).isEqualTo(new UnitOfMeasureResponse(1L, "KG", "Kilogramo", false));
    }

    @Test
    void updateThrowsNotFoundWhenMissing() {
        when(unitOfMeasureRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> unitOfMeasureService.update(99L, new UnitOfMeasureRequest("UN", "Unidad")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateWithCodeUsedByAnotherUnitThrowsConflict() {
        when(unitOfMeasureRepository.findById(1L)).thenReturn(Optional.of(unit(1L, "UN", "Unidad", true)));
        when(unitOfMeasureRepository.existsByCodeAndIdNot("KG", 1L)).thenReturn(true);

        assertThatThrownBy(() -> unitOfMeasureService.update(1L, new UnitOfMeasureRequest("KG", "Kilogramo")))
                .isInstanceOf(ResourceConflictException.class);
    }

    @Test
    void activateIsIdempotentWhenAlreadyActive() {
        UnitOfMeasure active = unit(1L, "UN", "Unidad", true);
        when(unitOfMeasureRepository.findById(1L)).thenReturn(Optional.of(active));
        when(unitOfMeasureRepository.save(active)).thenReturn(active);

        assertThat(unitOfMeasureService.activate(1L).active()).isTrue();
        assertThat(unitOfMeasureService.activate(1L).active()).isTrue();
    }

    @Test
    void deactivateIsIdempotentWhenAlreadyInactive() {
        UnitOfMeasure inactive = unit(1L, "UN", "Unidad", false);
        when(unitOfMeasureRepository.findById(1L)).thenReturn(Optional.of(inactive));
        when(unitOfMeasureRepository.save(inactive)).thenReturn(inactive);

        assertThat(unitOfMeasureService.deactivate(1L).active()).isFalse();
        assertThat(unitOfMeasureService.deactivate(1L).active()).isFalse();
    }

    @Test
    void activateAndDeactivateThrowNotFoundWhenMissing() {
        when(unitOfMeasureRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> unitOfMeasureService.activate(99L)).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> unitOfMeasureService.deactivate(99L)).isInstanceOf(ResourceNotFoundException.class);
    }
}
