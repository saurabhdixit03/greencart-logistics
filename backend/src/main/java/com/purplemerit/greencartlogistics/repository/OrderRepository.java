package com.purplemerit.greencartlogistics.repository;

import com.purplemerit.greencartlogistics.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entity
 */
@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    Optional<Order> findByOrderId(String orderId);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    List<Order> findByAssignedDriverId(String driverId);
    
    List<Order> findByAssignedRouteId(String routeId);
    
    List<Order> findByIsDeliveredOnTime(boolean onTime);
    
    long countByStatus(Order.OrderStatus status);
    
    long countByIsDeliveredOnTime(boolean onTime);
}
