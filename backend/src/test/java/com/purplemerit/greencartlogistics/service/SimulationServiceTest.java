package com.purplemerit.greencartlogistics.service;

import com.purplemerit.greencartlogistics.dto.SimulationRequest;
import com.purplemerit.greencartlogistics.model.*;
import com.purplemerit.greencartlogistics.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SimulationService
 */
@ExtendWith(MockitoExtension.class)
class SimulationServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SimulationResultRepository simulationResultRepository;

    @InjectMocks
    private SimulationService simulationService;

    private List<Driver> mockDrivers;
    private List<Route> mockRoutes;
    private List<Order> mockOrders;

    @BeforeEach
    void setUp() {
        // Setup mock drivers
        mockDrivers = Arrays.asList(
                createDriver("1", "Driver 1", 4.0, 35.0, false),
                createDriver("2", "Driver 2", 6.0, 40.0, false),
                createDriver("3", "Driver 3", 8.5, 48.0, true) // Has fatigue penalty
        );

        // Setup mock routes
        mockRoutes = Arrays.asList(
                createRoute("1", "R001", 10.0, "Low", 30),
                createRoute("2", "R002", 15.0, "High", 45),
                createRoute("3", "R003", 8.0, "Medium", 25)
        );

        // Setup mock orders
        mockOrders = Arrays.asList(
                createOrder("1", "ORD001", 1200.0, "R001"), // High value order
                createOrder("2", "ORD002", 800.0, "R002"),
                createOrder("3", "ORD003", 1500.0, "R003")  // High value order
        );
    }

    @Test
    void testRunSimulation_Success() {
        // Given
        SimulationRequest request = new SimulationRequest(2, LocalTime.of(9, 0), 8);
        String userId = "user123";

        when(driverRepository.findByIsActiveTrueOrderByCurrentShiftHours())
                .thenReturn(mockDrivers);
        when(routeRepository.findByIsActiveTrue())
                .thenReturn(mockRoutes);
        when(orderRepository.findByStatus(Order.OrderStatus.PENDING))
                .thenReturn(mockOrders);
        when(orderRepository.findByStatus(Order.OrderStatus.DELIVERED))
                .thenReturn(Arrays.asList());
        when(simulationResultRepository.save(any(SimulationResult.class)))
                .thenAnswer(invocation -> {
                    SimulationResult result = invocation.getArgument(0);
                    result.setId("sim123");
                    return result;
                });

        // When
        SimulationResult result = simulationService.runSimulation(request, userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getSimulatedBy());
        assertEquals(2, result.getNumberOfDrivers());
        assertEquals(LocalTime.of(9, 0), result.getRouteStartTime());
        assertEquals(8, result.getMaxHoursPerDriver());

        verify(simulationResultRepository, times(1)).save(any(SimulationResult.class));
    }

    @Test
    void testRunSimulation_InsufficientDrivers() {
        // Given
        SimulationRequest request = new SimulationRequest(5, LocalTime.of(9, 0), 8);
        String userId = "user123";

        when(driverRepository.findByIsActiveTrueOrderByCurrentShiftHours())
                .thenReturn(mockDrivers); // Only 3 drivers available

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                simulationService.runSimulation(request, userId)
        );

        assertTrue(exception.getMessage().contains("Not enough active drivers available"));
    }

    @Test
    void testRunSimulation_NoActiveRoutes() {
        // Given
        SimulationRequest request = new SimulationRequest(2, LocalTime.of(9, 0), 8);
        String userId = "user123";

        when(driverRepository.findByIsActiveTrueOrderByCurrentShiftHours())
                .thenReturn(mockDrivers);
        when(routeRepository.findByIsActiveTrue())
                .thenReturn(Arrays.asList()); // No active routes

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                simulationService.runSimulation(request, userId)
        );

        assertTrue(exception.getMessage().contains("No active routes available"));
    }

    @Test
    void testRunSimulation_NoPendingOrders() {
        // Given
        SimulationRequest request = new SimulationRequest(2, LocalTime.of(9, 0), 8);
        String userId = "user123";

        when(driverRepository.findByIsActiveTrueOrderByCurrentShiftHours())
                .thenReturn(mockDrivers);
        when(routeRepository.findByIsActiveTrue())
                .thenReturn(mockRoutes);
        when(orderRepository.findByStatus(Order.OrderStatus.PENDING))
                .thenReturn(Arrays.asList()); // No pending orders

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                simulationService.runSimulation(request, userId)
        );

        assertTrue(exception.getMessage().contains("No pending orders available"));
    }

    @Test
    void testGetSimulationHistory() {
        // Given
        List<SimulationResult> mockHistory = Arrays.asList(
                createSimulationResult("1", "user123"),
                createSimulationResult("2", "user123")
        );

        when(simulationResultRepository.findAllByOrderBySimulationTimestampDesc())
                .thenReturn(mockHistory);

        // When
        List<SimulationResult> result = simulationService.getSimulationHistory();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(simulationResultRepository, times(1)).findAllByOrderBySimulationTimestampDesc();
    }

    // Helper methods to create mock objects
    private Driver createDriver(String id, String name, Double currentShiftHours, 
                              Double past7DayWorkHours, boolean hasFatiguePenalty) {
        Driver driver = new Driver(name, currentShiftHours, past7DayWorkHours);
        driver.setId(id);
        driver.setHasFatiguePenalty(hasFatiguePenalty);
        return driver;
    }

    private Route createRoute(String id, String routeId, Double distanceKm, 
                            String trafficLevel, Integer baseTimeMinutes) {
        Route route = new Route(routeId, distanceKm, trafficLevel, baseTimeMinutes);
        route.setId(id);
        return route;
    }

    private Order createOrder(String id, String orderId, Double valueRs, String assignedRouteId) {
        Order order = new Order(orderId, valueRs, assignedRouteId, LocalDateTime.now().plusHours(2));
        order.setId(id);
        return order;
    }

    private SimulationResult createSimulationResult(String id, String simulatedBy) {
        SimulationResult result = new SimulationResult(3, LocalTime.of(9, 0), 8);
        result.setId(id);
        result.setSimulatedBy(simulatedBy);
        result.setTotalProfit(5000.0);
        result.setEfficiencyScore(85.0);
        return result;
    }
}
