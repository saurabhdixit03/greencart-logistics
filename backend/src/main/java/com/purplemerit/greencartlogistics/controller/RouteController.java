package com.purplemerit.greencartlogistics.controller;

import com.purplemerit.greencartlogistics.model.Route;
import com.purplemerit.greencartlogistics.repository.RouteRepository;
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
 * Controller for Route CRUD operations
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/routes")
@Tag(name = "Routes", description = "Route management APIs")
@SecurityRequirement(name = "bearerAuth")
public class RouteController {
    
    @Autowired
    private RouteRepository routeRepository;
    
    @GetMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get all routes", description = "Retrieve all routes in the system")
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get active routes", description = "Retrieve all active routes")
    public List<Route> getActiveRoutes() {
        return routeRepository.findByIsActiveTrue();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get route by ID", description = "Retrieve a specific route by ID")
    public ResponseEntity<Route> getRouteById(@PathVariable String id) {
        Optional<Route> route = routeRepository.findById(id);
        return route.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/route/{routeId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get route by route ID", description = "Retrieve a specific route by route ID")
    public ResponseEntity<Route> getRouteByRouteId(@PathVariable String routeId) {
        Optional<Route> route = routeRepository.findByRouteId(routeId);
        return route.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Create route", description = "Create a new route")
    public Route createRoute(@Valid @RequestBody Route route) {
        return routeRepository.save(route);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Update route", description = "Update an existing route")
    public ResponseEntity<Route> updateRoute(@PathVariable String id, @Valid @RequestBody Route routeDetails) {
        Optional<Route> optionalRoute = routeRepository.findById(id);
        
        if (optionalRoute.isPresent()) {
            Route route = optionalRoute.get();
            route.setRouteId(routeDetails.getRouteId());
            route.setDistanceKm(routeDetails.getDistanceKm());
            route.setTrafficLevel(routeDetails.getTrafficLevel());
            route.setBaseTimeMinutes(routeDetails.getBaseTimeMinutes());
            route.setStartLocation(routeDetails.getStartLocation());
            route.setEndLocation(routeDetails.getEndLocation());
            route.setActive(routeDetails.isActive());
            
            return ResponseEntity.ok(routeRepository.save(route));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Delete route", description = "Delete a route (soft delete by setting inactive)")
    public ResponseEntity<?> deleteRoute(@PathVariable String id) {
        Optional<Route> optionalRoute = routeRepository.findById(id);
        
        if (optionalRoute.isPresent()) {
            Route route = optionalRoute.get();
            route.setActive(false); // Soft delete
            routeRepository.save(route);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/traffic/{level}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get routes by traffic level", description = "Retrieve routes by traffic level")
    public List<Route> getRoutesByTrafficLevel(@PathVariable String level) {
        return routeRepository.findByTrafficLevel(level);
    }
}
