package com.purplemerit.greencartlogistics.service;

import com.purplemerit.greencartlogistics.dto.SimulationRequest;
import com.purplemerit.greencartlogistics.model.*;
import com.purplemerit.greencartlogistics.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for running delivery simulations with custom business rules
 */
@Service
public class SimulationService {
    
    private static final Logger logger = LoggerFactory.getLogger(SimulationService.class);
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Autowired
    private RouteRepository routeRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private SimulationResultRepository simulationResultRepository;
    
    /**
     * Run simulation based on the provided parameters
     * Implements custom company rules for GreenCart Logistics
     */
    public SimulationResult runSimulation(SimulationRequest request, String userId) {
        logger.info("Starting simulation with {} drivers, start time: {}, max hours: {}", 
                   request.getNumberOfDrivers(), request.getRouteStartTime(), request.getMaxHoursPerDriver());
        
        // Get available drivers and routes
        List<Driver> availableDrivers = driverRepository.findByIsActiveTrueOrderByCurrentShiftHours()
                .stream()
                .limit(request.getNumberOfDrivers())
                .collect(Collectors.toList());
        
        List<Route> activeRoutes = routeRepository.findByIsActiveTrue();
        List<Order> pendingOrders = orderRepository.findByStatus(Order.OrderStatus.PENDING);
        
        if (availableDrivers.size() < request.getNumberOfDrivers()) {
            throw new RuntimeException("Not enough active drivers available. Available: " + 
                                     availableDrivers.size() + ", Required: " + request.getNumberOfDrivers());
        }
        
        if (activeRoutes.isEmpty()) {
            throw new RuntimeException("No active routes available for simulation");
        }
        
        if (pendingOrders.isEmpty()) {
            throw new RuntimeException("No pending orders available for simulation");
        }
        
        // Create simulation result object
        SimulationResult result = new SimulationResult(
                request.getNumberOfDrivers(),
                request.getRouteStartTime(),
                request.getMaxHoursPerDriver()
        );
        result.setSimulatedBy(userId);
        result.setNotes(request.getNotes());
        
        // Simulate order allocation and delivery
        simulateOrderAllocation(availableDrivers, activeRoutes, pendingOrders, request, result);
        
        // Calculate KPIs
        calculateKPIs(result);
        
        // Save simulation result
        SimulationResult savedResult = simulationResultRepository.save(result);
        
        logger.info("Simulation completed. Total profit: ₹{}, Efficiency: {}%", 
                   result.getTotalProfit(), result.getEfficiencyScore());
        
        return savedResult;
    }
    
    /**
     * Simulate order allocation to drivers based on availability and capacity
     */
    private void simulateOrderAllocation(List<Driver> drivers, List<Route> routes, 
                                       List<Order> orders, SimulationRequest request, 
                                       SimulationResult result) {
        
        Map<String, Double> driverWorkingHours = new HashMap<>();
        Map<String, Route> routeMap = routes.stream()
                .collect(Collectors.toMap(Route::getRouteId, r -> r));
        
        List<String> processedOrderIds = new ArrayList<>();
        Map<String, Double> fuelCostBreakdown = new HashMap<>();
        
        int driverIndex = 0;
        
        for (Order order : orders) {
            // Find route for this order
            Route route = routeMap.get(order.getAssignedRouteId());
            if (route == null) {
                logger.warn("Route {} not found for order {}", order.getAssignedRouteId(), order.getOrderId());
                continue;
            }
            
            // Select driver in round-robin fashion
            Driver selectedDriver = drivers.get(driverIndex % drivers.size());
            driverIndex++;
            
            // Check if driver can handle this order within max hours
            double currentHours = driverWorkingHours.getOrDefault(selectedDriver.getId(), 0.0);
            double estimatedTimeHours = route.getBaseTimeMinutes() / 60.0;
            
            // Apply fatigue penalty if driver worked >8 hours yesterday
            if (selectedDriver.isHasFatiguePenalty()) {
                estimatedTimeHours *= 1.3; // 30% slower due to fatigue
            }
            
            if (currentHours + estimatedTimeHours <= request.getMaxHoursPerDriver()) {
                // Assign order to driver
                order.setAssignedDriverId(selectedDriver.getId());
                order.setStatus(Order.OrderStatus.ASSIGNED);
                
                // Simulate delivery
                simulateDelivery(order, route, request.getRouteStartTime().plusMinutes((int)(currentHours * 60)));
                
                // Update driver working hours
                driverWorkingHours.put(selectedDriver.getId(), currentHours + estimatedTimeHours);
                
                // Track fuel cost
                Double fuelCost = route.calculateFuelCost();
                order.setFuelCost(fuelCost);
                fuelCostBreakdown.merge(route.getTrafficLevel(), fuelCost, Double::sum);
                
                processedOrderIds.add(order.getOrderId());
                
                // Save updated order
                orderRepository.save(order);
            }
        }
        
        result.setProcessedOrderIds(processedOrderIds);
        result.setFuelCostBreakdown(fuelCostBreakdown);
    }
    
    /**
     * Simulate delivery for an order and apply company rules
     */
    private void simulateDelivery(Order order, Route route, LocalTime startTime) {
        // Calculate actual delivery time
        int baseTimeMinutes = route.getBaseTimeMinutes();
        
        // Add some randomness for simulation (±5 minutes)
        Random random = new Random();
        int actualTimeMinutes = baseTimeMinutes + random.nextInt(11) - 5; // -5 to +5 minutes
        
        LocalDateTime deliveryTime = LocalDateTime.now()
                .with(startTime)
                .plusMinutes(actualTimeMinutes);
        
        order.setDeliveryTimestamp(deliveryTime);
        order.setStatus(Order.OrderStatus.DELIVERED);
        
        // Company Rule 1: Late Delivery Penalty
        // If delivery time > (base route time + 10 minutes), apply ₹50 penalty
        boolean isOnTime = actualTimeMinutes <= (baseTimeMinutes + 10);
        order.setDeliveredOnTime(isOnTime);
        
        if (!isOnTime) {
            order.setPenalty(50.0);
        } else {
            order.setPenalty(0.0);
        }
        
        // Company Rule 3: High-Value Bonus
        // If order value > ₹1000 AND delivered on time → add 10% bonus
        if (order.getValueRs() > 1000 && isOnTime) {
            order.setBonus(order.getValueRs() * 0.1); // 10% bonus
        } else {
            order.setBonus(0.0);
        }
        
        // Calculate profit for this order
        order.calculateProfit();
        
        logger.debug("Order {} delivered. Value: ₹{}, On time: {}, Penalty: ₹{}, Bonus: ₹{}, Profit: ₹{}",
                    order.getOrderId(), order.getValueRs(), isOnTime, order.getPenalty(), 
                    order.getBonus(), order.getProfit());
    }
    
    /**
     * Calculate overall KPIs based on simulation results
     */
    private void calculateKPIs(SimulationResult result) {
        List<Order> deliveredOrders = orderRepository.findByStatus(Order.OrderStatus.DELIVERED);
        
        if (deliveredOrders.isEmpty()) {
            result.setTotalProfit(0.0);
            result.setEfficiencyScore(0.0);
            result.setOnTimeDeliveries(0);
            result.setLateDeliveries(0);
            result.setTotalDeliveries(0);
            return;
        }
        
        // Filter orders processed in this simulation
        List<Order> simulationOrders = deliveredOrders.stream()
                .filter(order -> result.getProcessedOrderIds().contains(order.getOrderId()))
                .collect(Collectors.toList());
        
        // Company Rule 5: Overall Profit calculation
        // Sum of (order value + bonus – penalties – fuel cost)
        double totalProfit = simulationOrders.stream()
                .mapToDouble(Order::getProfit)
                .sum();
        
        double totalPenalties = simulationOrders.stream()
                .mapToDouble(Order::getPenalty)
                .sum();
        
        double totalBonuses = simulationOrders.stream()
                .mapToDouble(Order::getBonus)
                .sum();
        
        double totalFuelCost = simulationOrders.stream()
                .mapToDouble(Order::getFuelCost)
                .sum();
        
        // Company Rule 6: Efficiency Score
        // Formula: Efficiency = (OnTime Deliveries / Total Deliveries) × 100
        long onTimeDeliveries = simulationOrders.stream()
                .mapToLong(order -> order.isDeliveredOnTime() ? 1 : 0)
                .sum();
        
        long lateDeliveries = simulationOrders.size() - onTimeDeliveries;
        double efficiencyScore = simulationOrders.size() > 0 ? 
                (double) onTimeDeliveries / simulationOrders.size() * 100 : 0;
        
        // Set results
        result.setTotalProfit(totalProfit);
        result.setTotalPenalties(totalPenalties);
        result.setTotalBonuses(totalBonuses);
        result.setTotalFuelCost(totalFuelCost);
        result.setEfficiencyScore(efficiencyScore);
        result.setOnTimeDeliveries((int) onTimeDeliveries);
        result.setLateDeliveries((int) lateDeliveries);
        result.setTotalDeliveries(simulationOrders.size());
    }
    
    /**
     * Get simulation history
     */
    public List<SimulationResult> getSimulationHistory() {
        return simulationResultRepository.findAllByOrderBySimulationTimestampDesc();
    }
    
    /**
     * Get simulation history for a specific user
     */
    public List<SimulationResult> getSimulationHistoryByUser(String userId) {
        return simulationResultRepository.findBySimulatedByOrderBySimulationTimestampDesc(userId);
    }
    
    /**
     * Get latest simulation result
     */
    public SimulationResult getLatestSimulation() {
        return simulationResultRepository.findTopByOrderBySimulationTimestampDesc();
    }
}
