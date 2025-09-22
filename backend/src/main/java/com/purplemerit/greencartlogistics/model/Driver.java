package com.purplemerit.greencartlogistics.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Driver entity representing delivery drivers in the system
 */
@Document(collection = "drivers")
public class Driver {
    
    @Id
    private String id;
    
    @NotBlank(message = "Driver name is required")
    private String name;
    
    @Min(value = 0, message = "Current shift hours cannot be negative")
    @Max(value = 24, message = "Current shift hours cannot exceed 24")
    private Double currentShiftHours;
    
    @Min(value = 0, message = "Past 7-day work hours cannot be negative")
    private Double past7DayWorkHours;
    
    private boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // List to track daily work hours for fatigue calculation
    private List<Double> dailyWorkHours;
    
    // Flag to track if driver has fatigue penalty
    private boolean hasFatiguePenalty;
    
    // Constructors
    public Driver() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.hasFatiguePenalty = false;
    }
    
    public Driver(String name, Double currentShiftHours, Double past7DayWorkHours) {
        this();
        this.name = name;
        this.currentShiftHours = currentShiftHours;
        this.past7DayWorkHours = past7DayWorkHours;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Double getCurrentShiftHours() {
        return currentShiftHours;
    }
    
    public void setCurrentShiftHours(Double currentShiftHours) {
        this.currentShiftHours = currentShiftHours;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Double getPast7DayWorkHours() {
        return past7DayWorkHours;
    }
    
    public void setPast7DayWorkHours(Double past7DayWorkHours) {
        this.past7DayWorkHours = past7DayWorkHours;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
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
    
    public List<Double> getDailyWorkHours() {
        return dailyWorkHours;
    }
    
    public void setDailyWorkHours(List<Double> dailyWorkHours) {
        this.dailyWorkHours = dailyWorkHours;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isHasFatiguePenalty() {
        return hasFatiguePenalty;
    }
    
    public void setHasFatiguePenalty(boolean hasFatiguePenalty) {
        this.hasFatiguePenalty = hasFatiguePenalty;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Driver{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", currentShiftHours=" + currentShiftHours +
                ", past7DayWorkHours=" + past7DayWorkHours +
                ", isActive=" + isActive +
                ", hasFatiguePenalty=" + hasFatiguePenalty +
                '}';
    }
}
