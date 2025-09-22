package com.purplemerit.greencartlogistics.repository;

import com.purplemerit.greencartlogistics.model.SimulationResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for SimulationResult entity
 */
@Repository
public interface SimulationResultRepository extends MongoRepository<SimulationResult, String> {
    
    List<SimulationResult> findBySimulatedByOrderBySimulationTimestampDesc(String simulatedBy);
    
    List<SimulationResult> findAllByOrderBySimulationTimestampDesc();
    
    List<SimulationResult> findBySimulationTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    SimulationResult findTopByOrderBySimulationTimestampDesc();
}
