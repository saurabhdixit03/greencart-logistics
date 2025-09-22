package com.purplemerit.greencartlogistics.controller;

import com.purplemerit.greencartlogistics.model.Order;
import com.purplemerit.greencartlogistics.repository.OrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for Order CRUD operations
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management APIs")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieve all orders in the system")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieve a specific order by ID")
    public ResponseEntity<Order> getOrderById(@PathVariable String id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get order by order ID", description = "Retrieve a specific order by order ID")
    public ResponseEntity<Order> getOrderByOrderId(@PathVariable String orderId) {
        Optional<Order> order = orderRepository.findByOrderId(orderId);
        return order.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Create order", description = "Create a new order")
    public Order createOrder(@Valid @RequestBody Order order) {
        return orderRepository.save(order);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update order", description = "Update an existing order")
    public ResponseEntity<Order> updateOrder(@PathVariable String id, @Valid @RequestBody Order orderDetails) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setOrderId(orderDetails.getOrderId());
            order.setValueRs(orderDetails.getValueRs());
            order.setAssignedRouteId(orderDetails.getAssignedRouteId());
            order.setDeliveryTimestamp(orderDetails.getDeliveryTimestamp());
            order.setAssignedDriverId(orderDetails.getAssignedDriverId());
            order.setStatus(orderDetails.getStatus());
            order.setDeliveredOnTime(orderDetails.isDeliveredOnTime());
            order.setPenalty(orderDetails.getPenalty());
            order.setBonus(orderDetails.getBonus());
            order.setFuelCost(orderDetails.getFuelCost());
            
            // Recalculate profit
            order.calculateProfit();
            
            return ResponseEntity.ok(orderRepository.save(order));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order", description = "Delete an order")
    public ResponseEntity<?> deleteOrder(@PathVariable String id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        
        if (optionalOrder.isPresent()) {
            orderRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status", description = "Retrieve orders by status")
    public List<Order> getOrdersByStatus(@PathVariable Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    @GetMapping("/driver/{driverId}")
    @Operation(summary = "Get orders by driver", description = "Retrieve orders assigned to a specific driver")
    public List<Order> getOrdersByDriver(@PathVariable String driverId) {
        return orderRepository.findByAssignedDriverId(driverId);
    }
    
    @GetMapping("/route/{routeId}")
    @Operation(summary = "Get orders by route", description = "Retrieve orders assigned to a specific route")
    public List<Order> getOrdersByRoute(@PathVariable String routeId) {
        return orderRepository.findByAssignedRouteId(routeId);
    }
    
    @GetMapping("/stats/delivery-performance")
    @Operation(summary = "Get delivery performance stats", description = "Get on-time vs late delivery statistics")
    public ResponseEntity<?> getDeliveryPerformanceStats() {
        long onTimeDeliveries = orderRepository.countByIsDeliveredOnTime(true);
        long lateDeliveries = orderRepository.countByIsDeliveredOnTime(false);
        
        return ResponseEntity.ok(new DeliveryStats(onTimeDeliveries, lateDeliveries));
    }
    
    // Inner class for delivery stats response
    public static class DeliveryStats {
        private long onTimeDeliveries;
        private long lateDeliveries;
        private long totalDeliveries;
        private double efficiencyScore;
        
        public DeliveryStats(long onTimeDeliveries, long lateDeliveries) {
            this.onTimeDeliveries = onTimeDeliveries;
            this.lateDeliveries = lateDeliveries;
            this.totalDeliveries = onTimeDeliveries + lateDeliveries;
            this.efficiencyScore = totalDeliveries > 0 ? (double) onTimeDeliveries / totalDeliveries * 100 : 0;
        }
        
        // Getters
        public long getOnTimeDeliveries() { return onTimeDeliveries; }
        public long getLateDeliveries() { return lateDeliveries; }
        public long getTotalDeliveries() { return totalDeliveries; }
        public double getEfficiencyScore() { return efficiencyScore; }
    }
}
