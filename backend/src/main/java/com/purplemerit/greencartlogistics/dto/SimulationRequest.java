package com.purplemerit.greencartlogistics.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalTime;

/**
 * DTO for simulation request
 */
public class SimulationRequest {
    
    @NotNull(message = "Number of drivers is required")
    @Min(value = 1, message = "Number of drivers must be at least 1")
    private Integer numberOfDrivers;
    
    @NotNull(message = "Route start time is required")
    private LocalTime routeStartTime;
    
    @NotNull(message = "Max hours per driver is required")
    @Min(value = 1, message = "Max hours per driver must be at least 1")
    private Integer maxHoursPerDriver;
    
    private String notes;
    
    public SimulationRequest() {}
    
    public SimulationRequest(Integer numberOfDrivers, LocalTime routeStartTime, Integer maxHoursPerDriver) {
        this.numberOfDrivers = numberOfDrivers;
        this.routeStartTime = routeStartTime;
        this.maxHoursPerDriver = maxHoursPerDriver;
    }
    
    public Integer getNumberOfDrivers() {
        return numberOfDrivers;
    }
    
    public void setNumberOfDrivers(Integer numberOfDrivers) {
        this.numberOfDrivers = numberOfDrivers;
    }
    
    public LocalTime getRouteStartTime() {
        return routeStartTime;
    }
    
    public void setRouteStartTime(LocalTime routeStartTime) {
        this.routeStartTime = routeStartTime;
    }
    
    public Integer getMaxHoursPerDriver() {
        return maxHoursPerDriver;
    }
    
    public void setMaxHoursPerDriver(Integer maxHoursPerDriver) {
        this.maxHoursPerDriver = maxHoursPerDriver;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
