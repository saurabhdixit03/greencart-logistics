package com.purplemerit.greencartlogistics.controller;

import com.purplemerit.greencartlogistics.model.Driver;
import com.purplemerit.greencartlogistics.repository.DriverRepository;
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
 * Controller for Driver CRUD operations
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/drivers")
@Tag(name = "Drivers", description = "Driver management APIs")
@SecurityRequirement(name = "bearerAuth")
public class DriverController {
    
    @Autowired
    private DriverRepository driverRepository;
    
    @GetMapping
    @Operation(summary = "Get all drivers", description = "Retrieve all drivers in the system")
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active drivers", description = "Retrieve all active drivers")
    public List<Driver> getActiveDrivers() {
        return driverRepository.findByIsActiveTrue();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get driver by ID", description = "Retrieve a specific driver by ID")
    public ResponseEntity<Driver> getDriverById(@PathVariable String id) {
        Optional<Driver> driver = driverRepository.findById(id);
        return driver.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Create driver", description = "Create a new driver")
    public Driver createDriver(@Valid @RequestBody Driver driver) {
        return driverRepository.save(driver);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update driver", description = "Update an existing driver")
    public ResponseEntity<Driver> updateDriver(@PathVariable String id, @Valid @RequestBody Driver driverDetails) {
        Optional<Driver> optionalDriver = driverRepository.findById(id);
        
        if (optionalDriver.isPresent()) {
            Driver driver = optionalDriver.get();
            driver.setName(driverDetails.getName());
            driver.setCurrentShiftHours(driverDetails.getCurrentShiftHours());
            driver.setPast7DayWorkHours(driverDetails.getPast7DayWorkHours());
            driver.setActive(driverDetails.isActive());
            driver.setHasFatiguePenalty(driverDetails.isHasFatiguePenalty());
            
            return ResponseEntity.ok(driverRepository.save(driver));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete driver", description = "Delete a driver (soft delete by setting inactive)")
    public ResponseEntity<?> deleteDriver(@PathVariable String id) {
        Optional<Driver> optionalDriver = driverRepository.findById(id);
        
        if (optionalDriver.isPresent()) {
            Driver driver = optionalDriver.get();
            driver.setActive(false); // Soft delete
            driverRepository.save(driver);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/count")
    @Operation(summary = "Get driver count", description = "Get count of active drivers")
    public ResponseEntity<Long> getActiveDriverCount() {
        long count = driverRepository.countByIsActiveTrue();
        return ResponseEntity.ok(count);
    }
}
