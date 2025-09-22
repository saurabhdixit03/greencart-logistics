package com.purplemerit.greencartlogistics.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Order model and business logic
 */
class OrderTest {

    @Test
    void testCalculateProfit_BasicOrder() {
        // Given
        Order order = new Order("ORD001", 1000.0, "R001", LocalDateTime.now().plusHours(2));
        order.setBonus(0.0);
        order.setPenalty(0.0);
        order.setFuelCost(50.0);

        // When
        order.calculateProfit();

        // Then
        assertEquals(950.0, order.getProfit()); // 1000 + 0 - 0 - 50 = 950
    }

    @Test
    void testCalculateProfit_WithBonus() {
        // Given
        Order order = new Order("ORD001", 1200.0, "R001", LocalDateTime.now().plusHours(2));
        order.setBonus(120.0); // 10% bonus for high-value on-time delivery
        order.setPenalty(0.0);
        order.setFuelCost(60.0);

        // When
        order.calculateProfit();

        // Then
        assertEquals(1260.0, order.getProfit()); // 1200 + 120 - 0 - 60 = 1260
    }

    @Test
    void testCalculateProfit_WithPenalty() {
        // Given
        Order order = new Order("ORD001", 800.0, "R001", LocalDateTime.now().plusHours(2));
        order.setBonus(0.0);
        order.setPenalty(50.0); // Late delivery penalty
        order.setFuelCost(40.0);

        // When
        order.calculateProfit();

        // Then
        assertEquals(710.0, order.getProfit()); // 800 + 0 - 50 - 40 = 710
    }

    @Test
    void testCalculateProfit_WithBonusAndPenalty() {
        // Given
        Order order = new Order("ORD001", 1500.0, "R001", LocalDateTime.now().plusHours(2));
        order.setBonus(150.0);
        order.setPenalty(50.0);
        order.setFuelCost(75.0);

        // When
        order.calculateProfit();

        // Then
        assertEquals(1525.0, order.getProfit()); // 1500 + 150 - 50 - 75 = 1525
    }

    @Test
    void testOrderCreation() {
        // Given
        String orderId = "ORD001";
        Double valueRs = 1200.0;
        String assignedRouteId = "R001";
        LocalDateTime deliveryTimestamp = LocalDateTime.now().plusHours(2);

        // When
        Order order = new Order(orderId, valueRs, assignedRouteId, deliveryTimestamp);

        // Then
        assertEquals(orderId, order.getOrderId());
        assertEquals(valueRs, order.getValueRs());
        assertEquals(assignedRouteId, order.getAssignedRouteId());
        assertEquals(deliveryTimestamp, order.getDeliveryTimestamp());
        assertEquals(Order.OrderStatus.PENDING, order.getStatus());
        assertEquals(0.0, order.getPenalty());
        assertEquals(0.0, order.getBonus());
        assertEquals(0.0, order.getFuelCost());
        assertEquals(0.0, order.getProfit());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    void testOrderStatusEnum() {
        // Test all order status values
        Order.OrderStatus[] statuses = Order.OrderStatus.values();
        
        assertEquals(5, statuses.length);
        assertTrue(java.util.Arrays.asList(statuses).contains(Order.OrderStatus.PENDING));
        assertTrue(java.util.Arrays.asList(statuses).contains(Order.OrderStatus.ASSIGNED));
        assertTrue(java.util.Arrays.asList(statuses).contains(Order.OrderStatus.IN_TRANSIT));
        assertTrue(java.util.Arrays.asList(statuses).contains(Order.OrderStatus.DELIVERED));
        assertTrue(java.util.Arrays.asList(statuses).contains(Order.OrderStatus.CANCELLED));
    }

    @Test
    void testOrderToString() {
        // Given
        Order order = new Order("ORD001", 1000.0, "R001", LocalDateTime.now());
        order.setId("123");
        order.setAssignedDriverId("D001");
        order.setStatus(Order.OrderStatus.DELIVERED);

        // When
        String result = order.toString();

        // Then
        assertTrue(result.contains("ORD001"));
        assertTrue(result.contains("1000.0"));
        assertTrue(result.contains("R001"));
        assertTrue(result.contains("D001"));
        assertTrue(result.contains("DELIVERED"));
    }
}
