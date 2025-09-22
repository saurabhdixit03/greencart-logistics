package com.purplemerit.greencartlogistics.repository;

import com.purplemerit.greencartlogistics.model.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Driver entity
 */
@Repository
public interface DriverRepository extends MongoRepository<Driver, String> {
    
    List<Driver> findByIsActiveTrue();
    
    List<Driver> findByIsActiveTrueOrderByCurrentShiftHours();
    
    long countByIsActiveTrue();
}
