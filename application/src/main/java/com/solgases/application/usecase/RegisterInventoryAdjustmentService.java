package com.solgases.application.usecase;

import com.solgases.application.dto.InventoryAdjustmentCommand;
import com.solgases.application.dto.InventoryMovementResult;
import com.solgases.application.port.in.RegisterInventoryAdjustmentUseCase;
import com.solgases.application.port.out.InventoryPersistencePort;
import com.solgases.domain.model.MovementType;
import org.springframework.transaction.annotation.Transactional;

public class RegisterInventoryAdjustmentService implements RegisterInventoryAdjustmentUseCase {
    private final InventoryMovementRegistration registration;
    public RegisterInventoryAdjustmentService(InventoryPersistencePort persistence) { registration = new InventoryMovementRegistration(persistence); }
    @Override @Transactional
    public InventoryMovementResult execute(Long productId, InventoryAdjustmentCommand command) {
        return registration.execute(productId, MovementType.ADJUSTMENT, command.direction(),
                command.quantity(), command.reason(), command.responsibleUser());
    }
}
