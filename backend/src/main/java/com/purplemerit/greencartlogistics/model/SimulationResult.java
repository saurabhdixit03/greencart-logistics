package com.purplemerit.greencartlogistics.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * SimulationResult entity to store simulation results with timestamp
 */
@Document(collection = "simulation_results")
public class SimulationResult {
    
    @Id
    private String id;
    
    private Integer numberOfDrivers;
    
    private LocalTime routeStartTime;
    
    private Integer maxHoursPerDriver;
    
    private Double totalProfit;
    
    private Double efficiencyScore;
    
    private Integer onTimeDeliveries;
    
    private Integer lateDeliveries;
    
    private Integer totalDeliveries;
    
    private Double totalFuelCost;
    
    private Double totalPenalties;
    
    private Double totalBonuses;
    
    private Map<String, Double> fuelCostBreakdown;
    
    private List<String> processedOrderIds;
    
    private String simulatedBy; // User ID who ran the simulation
    
    private LocalDateTime simulationTimestamp;
    
    private String notes;
    
    // Constructors
    public SimulationResult() {
        this.simulationTimestamp = LocalDateTime.now();
    }
    
    public SimulationResult(Integer numberOfDrivers, LocalTime routeStartTime, Integer maxHoursPerDriver) {
        this();
        this.numberOfDrivers = numberOfDrivers;
        this.routeStartTime = routeStartTime;
        this.maxHoursPerDriver = maxHoursPerDriver;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
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
    
    public Double getTotalProfit() {
        return totalProfit;
    }
    
    public void setTotalProfit(Double totalProfit) {
        this.totalProfit = totalProfit;
    }
    
    public Double getEfficiencyScore() {
        return efficiencyScore;
    }
    
    public void setEfficiencyScore(Double efficiencyScore) {
        this.efficiencyScore = efficiencyScore;
    }
    
    public Integer getOnTimeDeliveries() {
        return onTimeDeliveries;
    }
    
    public void setOnTimeDeliveries(Integer onTimeDeliveries) {
        this.onTimeDeliveries = onTimeDeliveries;
    }
    
    public Integer getLateDeliveries() {
        return lateDeliveries;
    }
    
    public void setLateDeliveries(Integer lateDeliveries) {
        this.lateDeliveries = lateDeliveries;
    }
    
    public Integer getTotalDeliveries() {
        return totalDeliveries;
    }
    
    public void setTotalDeliveries(Integer totalDeliveries) {
        this.totalDeliveries = totalDeliveries;
    }
    
    public Double getTotalFuelCost() {
        return totalFuelCost;
    }
    
    public void setTotalFuelCost(Double totalFuelCost) {
        this.totalFuelCost = totalFuelCost;
    }
    
    public Double getTotalPenalties() {
        return totalPenalties;
    }
    
    public void setTotalPenalties(Double totalPenalties) {
        this.totalPenalties = totalPenalties;
    }
    
    public Double getTotalBonuses() {
        return totalBonuses;
    }
    
    public void setTotalBonuses(Double totalBonuses) {
        this.totalBonuses = totalBonuses;
    }
    
    public Map<String, Double> getFuelCostBreakdown() {
        return fuelCostBreakdown;
    }
    
    public void setFuelCostBreakdown(Map<String, Double> fuelCostBreakdown) {
        this.fuelCostBreakdown = fuelCostBreakdown;
    }
    
    public List<String> getProcessedOrderIds() {
        return processedOrderIds;
    }
    
    public void setProcessedOrderIds(List<String> processedOrderIds) {
        this.processedOrderIds = processedOrderIds;
    }
    
    public String getSimulatedBy() {
        return simulatedBy;
    }
    
    public void setSimulatedBy(String simulatedBy) {
        this.simulatedBy = simulatedBy;
    }
    
    public LocalDateTime getSimulationTimestamp() {
        return simulationTimestamp;
    }
    
    public void setSimulationTimestamp(LocalDateTime simulationTimestamp) {
        this.simulationTimestamp = simulationTimestamp;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        return "SimulationResult{" +
                "id='" + id + '\'' +
                ", numberOfDrivers=" + numberOfDrivers +
                ", routeStartTime=" + routeStartTime +
                ", maxHoursPerDriver=" + maxHoursPerDriver +
                ", totalProfit=" + totalProfit +
                ", efficiencyScore=" + efficiencyScore +
                ", onTimeDeliveries=" + onTimeDeliveries +
                ", lateDeliveries=" + lateDeliveries +
                ", totalDeliveries=" + totalDeliveries +
                ", simulationTimestamp=" + simulationTimestamp +
                '}';
    }
}
