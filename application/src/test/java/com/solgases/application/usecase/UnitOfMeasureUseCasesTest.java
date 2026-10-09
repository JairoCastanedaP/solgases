package com.solgases.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.solgases.application.dto.UnitOfMeasureCommand;
import com.solgases.application.dto.UnitOfMeasureResult;
import com.solgases.application.exception.DuplicateUnitOfMeasureCodeException;
import com.solgases.application.exception.UnitOfMeasureNotFoundException;
import com.solgases.application.port.out.UnitOfMeasurePersistencePort;
import com.solgases.domain.model.UnitOfMeasure;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnitOfMeasureUseCasesTest {

    @Mock private UnitOfMeasurePersistencePort port;
    @InjectMocks private CreateUnitOfMeasureService create;
    @InjectMocks private ListUnitOfMeasuresService list;
    @InjectMocks private GetUnitOfMeasureByIdService get;
    @InjectMocks private UpdateUnitOfMeasureService update;
    @InjectMocks private ActivateUnitOfMeasureService activate;
    @InjectMocks private DeactivateUnitOfMeasureService deactivate;

    @Test
    void createKeepsTheProvidedValues() {
        when(port.saveNew("UN", "Unidad")).thenReturn(new UnitOfMeasure(1L, "UN", "Unidad", true));
        assertThat(create.execute(new UnitOfMeasureCommand("UN", "Unidad")))
                .isEqualTo(new UnitOfMeasureResult(1L, "UN", "Unidad", true));
    }

    @Test
    void createRejectsDuplicateCode() {
        when(port.existsByCode("UN")).thenReturn(true);
        assertThatThrownBy(() -> create.execute(new UnitOfMeasureCommand("UN", "Unidad")))
                .isInstanceOf(DuplicateUnitOfMeasureCodeException.class);
        verify(port, never()).saveNew("UN", "Unidad");
    }

    @Test
    void listReturnsAllActiveStates() {
        when(port.findAll()).thenReturn(List.of(new UnitOfMeasure(1L, "UN", "Unidad", true),
                new UnitOfMeasure(2L, "KG", "Kilogramo", false)));
        assertThat(list.execute()).containsExactly(new UnitOfMeasureResult(1L, "UN", "Unidad", true),
                new UnitOfMeasureResult(2L, "KG", "Kilogramo", false));
    }

    @Test
    void getThrowsNotFoundWhenMissing() {
        when(port.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> get.execute(99L)).isInstanceOf(UnitOfMeasureNotFoundException.class);
    }

    @Test
    void updateReplacesCodeAndName() {
        UnitOfMeasure existing = new UnitOfMeasure(1L, "UN", "Unidad", false);
        when(port.findById(1L)).thenReturn(Optional.of(existing));
        when(port.saveChanges(existing.update("UND", "Unidades")))
                .thenReturn(new UnitOfMeasure(1L, "UND", "Unidades", false));
        assertThat(update.execute(1L, new UnitOfMeasureCommand("UND", "Unidades")))
                .isEqualTo(new UnitOfMeasureResult(1L, "UND", "Unidades", false));
    }

    @Test
    void updateRejectsCodeUsedByAnotherUnit() {
        when(port.findById(1L)).thenReturn(Optional.of(new UnitOfMeasure(1L, "UN", "Unidad", true)));
        when(port.existsByCodeAndIdNot("KG", 1L)).thenReturn(true);
        assertThatThrownBy(() -> update.execute(1L, new UnitOfMeasureCommand("KG", "Kilogramo")))
                .isInstanceOf(DuplicateUnitOfMeasureCodeException.class);
        verify(port, never()).saveChanges(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void activateAndDeactivateChangeOnlyTheActiveFlag() {
        UnitOfMeasure inactive = new UnitOfMeasure(1L, "UN", "Unidad", false);
        UnitOfMeasure active = inactive.activate();
        when(port.findById(1L)).thenReturn(Optional.of(inactive));
        when(port.saveChanges(active)).thenReturn(active);
        assertThat(activate.execute(1L).active()).isTrue();

        when(port.findById(2L)).thenReturn(Optional.of(new UnitOfMeasure(2L, "KG", "Kilogramo", true)));
        when(port.saveChanges(new UnitOfMeasure(2L, "KG", "Kilogramo", false)))
                .thenReturn(new UnitOfMeasure(2L, "KG", "Kilogramo", false));
        assertThat(deactivate.execute(2L).active()).isFalse();
    }
}
