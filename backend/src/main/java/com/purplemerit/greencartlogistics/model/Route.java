package com.purplemerit.greencartlogistics.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * Route entity representing delivery routes in the system
 */
@Document(collection = "routes")
public class Route {
    
    @Id
    private String id;
    
    @NotBlank(message = "Route ID is required")
    private String routeId;
    
    @Min(value = 0, message = "Distance must be positive")
    private Double distanceKm;
    
    @Pattern(regexp = "Low|Medium|High", message = "Traffic level must be Low, Medium, or High")
    private String trafficLevel;
    
    @Min(value = 0, message = "Base time must be positive")
    private Integer baseTimeMinutes;
    
    private String startLocation;
    
    private String endLocation;
    
    private boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Constructors
    public Route() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    public Route(String routeId, Double distanceKm, String trafficLevel, Integer baseTimeMinutes) {
        this();
        this.routeId = routeId;
        this.distanceKm = distanceKm;
        this.trafficLevel = trafficLevel;
        this.baseTimeMinutes = baseTimeMinutes;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getRouteId() {
        return routeId;
    }
    
    public void setRouteId(String routeId) {
        this.routeId = routeId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Double getDistanceKm() {
        return distanceKm;
    }
    
    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getTrafficLevel() {
        return trafficLevel;
    }
    
    public void setTrafficLevel(String trafficLevel) {
        this.trafficLevel = trafficLevel;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Integer getBaseTimeMinutes() {
        return baseTimeMinutes;
    }
    
    public void setBaseTimeMinutes(Integer baseTimeMinutes) {
        this.baseTimeMinutes = baseTimeMinutes;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getStartLocation() {
        return startLocation;
    }
    
    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getEndLocation() {
        return endLocation;
    }
    
    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
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
    
    /**
     * Calculate fuel cost based on distance and traffic level
     * Base cost: ₹5/km per route
     * If traffic level is "High" → +₹2/km fuel surcharge
     */
    public Double calculateFuelCost() {
        double baseCost = distanceKm * 5.0; // ₹5/km
        if ("High".equals(trafficLevel)) {
            baseCost += distanceKm * 2.0; // +₹2/km surcharge
        }
        return baseCost;
    }
    
    @Override
    public String toString() {
        return "Route{" +
                "id='" + id + '\'' +
                ", routeId='" + routeId + '\'' +
                ", distanceKm=" + distanceKm +
                ", trafficLevel='" + trafficLevel + '\'' +
                ", baseTimeMinutes=" + baseTimeMinutes +
                ", startLocation='" + startLocation + '\'' +
                ", endLocation='" + endLocation + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
