package com.solgases.infrastructure.config;

import com.solgases.application.port.out.UnitOfMeasurePersistencePort;
import com.solgases.application.usecase.ActivateUnitOfMeasureService;
import com.solgases.application.usecase.CreateUnitOfMeasureService;
import com.solgases.application.usecase.DeactivateUnitOfMeasureService;
import com.solgases.application.usecase.GetUnitOfMeasureByIdService;
import com.solgases.application.usecase.ListUnitOfMeasuresService;
import com.solgases.application.usecase.UpdateUnitOfMeasureService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UnitOfMeasureUseCaseConfiguration {

    @Bean
    CreateUnitOfMeasureService createUnitOfMeasureService(UnitOfMeasurePersistencePort port) {
        return new CreateUnitOfMeasureService(port);
    }

    @Bean
    ListUnitOfMeasuresService listUnitOfMeasuresService(UnitOfMeasurePersistencePort port) {
        return new ListUnitOfMeasuresService(port);
    }

    @Bean
    GetUnitOfMeasureByIdService getUnitOfMeasureByIdService(UnitOfMeasurePersistencePort port) {
        return new GetUnitOfMeasureByIdService(port);
    }

    @Bean
    UpdateUnitOfMeasureService updateUnitOfMeasureService(UnitOfMeasurePersistencePort port) {
        return new UpdateUnitOfMeasureService(port);
    }

    @Bean
    ActivateUnitOfMeasureService activateUnitOfMeasureService(UnitOfMeasurePersistencePort port) {
        return new ActivateUnitOfMeasureService(port);
    }

    @Bean
    DeactivateUnitOfMeasureService deactivateUnitOfMeasureService(UnitOfMeasurePersistencePort port) {
        return new DeactivateUnitOfMeasureService(port);
    }
}
