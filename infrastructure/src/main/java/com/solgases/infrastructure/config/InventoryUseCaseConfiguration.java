package com.solgases.infrastructure.config;

import com.solgases.application.port.out.InventoryPersistencePort;
import com.solgases.application.usecase.GetInventoryMovementService;
import com.solgases.application.usecase.GetInventoryService;
import com.solgases.application.usecase.ListInventoryMovementsService;
import com.solgases.application.usecase.RegisterInventoryAdjustmentService;
import com.solgases.application.usecase.RegisterInventoryEntryService;
import com.solgases.application.usecase.RegisterInventoryExitService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryUseCaseConfiguration {
    @Bean GetInventoryService getInventoryService(InventoryPersistencePort port) { return new GetInventoryService(port); }
    @Bean ListInventoryMovementsService listInventoryMovementsService(InventoryPersistencePort port) {
        return new ListInventoryMovementsService(port);
    }
    @Bean GetInventoryMovementService getInventoryMovementService(InventoryPersistencePort port) {
        return new GetInventoryMovementService(port);
    }
    @Bean RegisterInventoryEntryService registerInventoryEntryService(InventoryPersistencePort port) {
        return new RegisterInventoryEntryService(port);
    }
    @Bean RegisterInventoryExitService registerInventoryExitService(InventoryPersistencePort port) {
        return new RegisterInventoryExitService(port);
    }
    @Bean RegisterInventoryAdjustmentService registerInventoryAdjustmentService(InventoryPersistencePort port) {
        return new RegisterInventoryAdjustmentService(port);
    }
}
