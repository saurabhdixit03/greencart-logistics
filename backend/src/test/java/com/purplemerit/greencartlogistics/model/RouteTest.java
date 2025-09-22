package com.purplemerit.greencartlogistics.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Route model and business logic
 */
class RouteTest {

    @Test
    void testCalculateFuelCost_LowTraffic() {
        // Given
        Route route = new Route("R001", 10.0, "Low", 30);

        // When
        Double fuelCost = route.calculateFuelCost();

        // Then
        assertEquals(50.0, fuelCost); // 10km * ₹5/km = ₹50
    }

    @Test
    void testCalculateFuelCost_MediumTraffic() {
        // Given
        Route route = new Route("R002", 15.0, "Medium", 45);

        // When
        Double fuelCost = route.calculateFuelCost();

        // Then
        assertEquals(75.0, fuelCost); // 15km * ₹5/km = ₹75 (no surcharge for medium)
    }

    @Test
    void testCalculateFuelCost_HighTraffic() {
        // Given
        Route route = new Route("R003", 12.0, "High", 50);

        // When
        Double fuelCost = route.calculateFuelCost();

        // Then
        assertEquals(84.0, fuelCost); // 12km * (₹5 + ₹2)/km = ₹84
    }

    @Test
    void testRouteCreation() {
        // Given
        String routeId = "R001";
        Double distanceKm = 10.5;
        String trafficLevel = "Medium";
        Integer baseTimeMinutes = 35;

        // When
        Route route = new Route(routeId, distanceKm, trafficLevel, baseTimeMinutes);

        // Then
        assertEquals(routeId, route.getRouteId());
        assertEquals(distanceKm, route.getDistanceKm());
        assertEquals(trafficLevel, route.getTrafficLevel());
        assertEquals(baseTimeMinutes, route.getBaseTimeMinutes());
        assertTrue(route.isActive());
        assertNotNull(route.getCreatedAt());
        assertNotNull(route.getUpdatedAt());
    }

    @Test
    void testRouteToString() {
        // Given
        Route route = new Route("R001", 10.0, "Low", 30);
        route.setId("123");
        route.setStartLocation("Warehouse A");
        route.setEndLocation("Customer Location");

        // When
        String result = route.toString();

        // Then
        assertTrue(result.contains("R001"));
        assertTrue(result.contains("10.0"));
        assertTrue(result.contains("Low"));
        assertTrue(result.contains("30"));
    }
}
