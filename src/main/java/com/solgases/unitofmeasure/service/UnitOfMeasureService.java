package com.solgases.unitofmeasure.service;

import com.solgases.exception.ResourceConflictException;
import com.solgases.exception.ResourceNotFoundException;
import com.solgases.unitofmeasure.dto.UnitOfMeasureRequest;
import com.solgases.unitofmeasure.dto.UnitOfMeasureResponse;
import com.solgases.unitofmeasure.entity.UnitOfMeasure;
import com.solgases.unitofmeasure.repository.UnitOfMeasureRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UnitOfMeasureService {

    private static final Logger log = LoggerFactory.getLogger(UnitOfMeasureService.class);

    private final UnitOfMeasureRepository unitOfMeasureRepository;

    public UnitOfMeasureService(UnitOfMeasureRepository unitOfMeasureRepository) {
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @Transactional
    public UnitOfMeasureResponse create(UnitOfMeasureRequest request) {
        if (unitOfMeasureRepository.existsByCode(request.code())) {
            throw duplicateCode(request.code());
        }
        UnitOfMeasure unitOfMeasure = new UnitOfMeasure(request.code(), request.name());
        UnitOfMeasure saved = saveOrConflict(unitOfMeasure);
        log.info("Unit of measure created with id {}", saved.getId());
        return UnitOfMeasureResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<UnitOfMeasureResponse> findAll() {
        return unitOfMeasureRepository.findAll().stream()
                .map(UnitOfMeasureResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UnitOfMeasureResponse findById(Long id) {
        return UnitOfMeasureResponse.from(getExisting(id));
    }

    @Transactional
    public UnitOfMeasureResponse update(Long id, UnitOfMeasureRequest request) {
        UnitOfMeasure unitOfMeasure = getExisting(id);
        if (unitOfMeasureRepository.existsByCodeAndIdNot(request.code(), id)) {
            throw duplicateCode(request.code());
        }
        unitOfMeasure.update(request.code(), request.name());
        UnitOfMeasure saved = saveOrConflict(unitOfMeasure);
        log.info("Unit of measure {} updated", id);
        return UnitOfMeasureResponse.from(saved);
    }

    @Transactional
    public UnitOfMeasureResponse activate(Long id) {
        UnitOfMeasure unitOfMeasure = getExisting(id);
        unitOfMeasure.activate();
        log.info("Unit of measure {} activated", id);
        return UnitOfMeasureResponse.from(unitOfMeasureRepository.save(unitOfMeasure));
    }

    @Transactional
    public UnitOfMeasureResponse deactivate(Long id) {
        UnitOfMeasure unitOfMeasure = getExisting(id);
        unitOfMeasure.deactivate();
        log.info("Unit of measure {} deactivated", id);
        return UnitOfMeasureResponse.from(unitOfMeasureRepository.save(unitOfMeasure));
    }

    private UnitOfMeasure getExisting(Long id) {
        return unitOfMeasureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit of measure not found with id " + id));
    }

    private UnitOfMeasure saveOrConflict(UnitOfMeasure unitOfMeasure) {
        try {
            return unitOfMeasureRepository.saveAndFlush(unitOfMeasure);
        } catch (DataIntegrityViolationException ex) {
            throw duplicateCode(unitOfMeasure.getCode());
        }
    }

    private ResourceConflictException duplicateCode(String code) {
        return new ResourceConflictException("A unit of measure with the code '" + code + "' already exists");
    }
}
