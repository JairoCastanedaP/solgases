package com.solgases.application.usecase;

import com.solgases.application.dto.InventoryMovementCommand;
import com.solgases.application.dto.InventoryMovementResult;
import com.solgases.application.port.in.RegisterInventoryEntryUseCase;
import com.solgases.application.port.out.InventoryPersistencePort;
import com.solgases.domain.model.MovementDirection;
import com.solgases.domain.model.MovementType;
import org.springframework.transaction.annotation.Transactional;

public class RegisterInventoryEntryService implements RegisterInventoryEntryUseCase {
    private final InventoryMovementRegistration registration;
    public RegisterInventoryEntryService(InventoryPersistencePort persistence) { registration = new InventoryMovementRegistration(persistence); }
    @Override @Transactional
    public InventoryMovementResult execute(Long productId, InventoryMovementCommand command) {
        return registration.execute(productId, MovementType.ENTRY, MovementDirection.INCREASE,
                command.quantity(), command.reason(), command.responsibleUser());
    }
}
