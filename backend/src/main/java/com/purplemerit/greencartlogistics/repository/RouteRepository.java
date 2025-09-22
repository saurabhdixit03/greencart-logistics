package com.purplemerit.greencartlogistics.repository;

import com.purplemerit.greencartlogistics.model.Route;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Route entity
 */
@Repository
public interface RouteRepository extends MongoRepository<Route, String> {
    
    List<Route> findByIsActiveTrue();
    
    Optional<Route> findByRouteId(String routeId);
    
    List<Route> findByTrafficLevel(String trafficLevel);
    
    List<Route> findByIsActiveTrueOrderByDistanceKm();
}
