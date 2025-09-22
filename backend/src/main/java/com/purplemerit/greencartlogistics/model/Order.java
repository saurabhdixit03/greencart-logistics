package com.purplemerit.greencartlogistics.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * Order entity representing delivery orders in the system
 */
@Document(collection = "orders")
public class Order {
    
    @Id
    private String id;
    
    @NotBlank(message = "Order ID is required")
    private String orderId;
    
    @Min(value = 0, message = "Order value must be positive")
    private Double valueRs;
    
    @NotBlank(message = "Assigned route is required")
    private String assignedRouteId;
    
    private LocalDateTime deliveryTimestamp;
    
    private String assignedDriverId;
    
    private OrderStatus status;
    
    private boolean isDeliveredOnTime;
    
    private Double penalty;
    
    private Double bonus;
    
    private Double fuelCost;
    
    private Double profit;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Enums
    public enum OrderStatus {
        PENDING, ASSIGNED, IN_TRANSIT, DELIVERED, CANCELLED
    }
    
    // Constructors
    public Order() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.penalty = 0.0;
        this.bonus = 0.0;
        this.fuelCost = 0.0;
        this.profit = 0.0;
    }
    
    public Order(String orderId, Double valueRs, String assignedRouteId, LocalDateTime deliveryTimestamp) {
        this();
        this.orderId = orderId;
        this.valueRs = valueRs;
        this.assignedRouteId = assignedRouteId;
        this.deliveryTimestamp = deliveryTimestamp;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Double getValueRs() {
        return valueRs;
    }
    
    public void setValueRs(Double valueRs) {
        this.valueRs = valueRs;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getAssignedRouteId() {
        return assignedRouteId;
    }
    
    public void setAssignedRouteId(String assignedRouteId) {
        this.assignedRouteId = assignedRouteId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getDeliveryTimestamp() {
        return deliveryTimestamp;
    }
    
    public void setDeliveryTimestamp(LocalDateTime deliveryTimestamp) {
        this.deliveryTimestamp = deliveryTimestamp;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getAssignedDriverId() {
        return assignedDriverId;
    }
    
    public void setAssignedDriverId(String assignedDriverId) {
        this.assignedDriverId = assignedDriverId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isDeliveredOnTime() {
        return isDeliveredOnTime;
    }
    
    public void setDeliveredOnTime(boolean deliveredOnTime) {
        isDeliveredOnTime = deliveredOnTime;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Double getPenalty() {
        return penalty;
    }
    
    public void setPenalty(Double penalty) {
        this.penalty = penalty;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Double getBonus() {
        return bonus;
    }
    
    public void setBonus(Double bonus) {
        this.bonus = bonus;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Double getFuelCost() {
        return fuelCost;
    }
    
    public void setFuelCost(Double fuelCost) {
        this.fuelCost = fuelCost;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Double getProfit() {
        return profit;
    }
    
    public void setProfit(Double profit) {
        this.profit = profit;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Calculate profit based on company rules
     * Overall Profit: Sum of (order value + bonus – penalties – fuel cost)
     */
    public void calculateProfit() {
        this.profit = this.valueRs + this.bonus - this.penalty - this.fuelCost;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", orderId='" + orderId + '\'' +
                ", valueRs=" + valueRs +
                ", assignedRouteId='" + assignedRouteId + '\'' +
                ", assignedDriverId='" + assignedDriverId + '\'' +
                ", status=" + status +
                ", isDeliveredOnTime=" + isDeliveredOnTime +
                ", penalty=" + penalty +
                ", bonus=" + bonus +
                ", fuelCost=" + fuelCost +
                ", profit=" + profit +
                '}';
    }
}
