package com.purplemerit.greencartlogistics.controller;

import com.purplemerit.greencartlogistics.dto.MessageResponse;
import com.purplemerit.greencartlogistics.dto.SimulationRequest;
import com.purplemerit.greencartlogistics.model.SimulationResult;
import com.purplemerit.greencartlogistics.security.UserPrincipal;
import com.purplemerit.greencartlogistics.service.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for simulation operations
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/simulation")
@Tag(name = "Simulation", description = "Delivery simulation APIs")
@SecurityRequirement(name = "bearerAuth")
public class SimulationController {
    
    private static final Logger logger = LoggerFactory.getLogger(SimulationController.class);
    
    @Autowired
    private SimulationService simulationService;
    
    @PostMapping("/run")
    @Operation(summary = "Run simulation", description = "Run delivery simulation with specified parameters")
    public ResponseEntity<?> runSimulation(@Valid @RequestBody SimulationRequest request, 
                                         Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            logger.info("Running simulation requested by user: {}", userPrincipal.getUsername());
            
            // Validate input parameters
            if (request.getNumberOfDrivers() <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Number of drivers must be greater than 0"));
            }
            
            if (request.getMaxHoursPerDriver() <= 0 || request.getMaxHoursPerDriver() > 24) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Max hours per driver must be between 1 and 24"));
            }
            
            SimulationResult result = simulationService.runSimulation(request, userPrincipal.getId());
            
            logger.info("Simulation completed successfully with ID: {}", result.getId());
            
            return ResponseEntity.ok(result);
            
        } catch (RuntimeException e) {
            logger.error("Simulation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Simulation failed: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during simulation: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Internal server error occurred during simulation"));
        }
    }
    
    @GetMapping("/history")
    @Operation(summary = "Get simulation history", description = "Retrieve all simulation results")
    public ResponseEntity<List<SimulationResult>> getSimulationHistory() {
        try {
            List<SimulationResult> history = simulationService.getSimulationHistory();
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.error("Error retrieving simulation history: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/history/my")
    @Operation(summary = "Get my simulation history", description = "Retrieve simulation results for current user")
    public ResponseEntity<List<SimulationResult>> getMySimulationHistory(Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            List<SimulationResult> history = simulationService.getSimulationHistoryByUser(userPrincipal.getId());
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.error("Error retrieving user simulation history: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/latest")
    @Operation(summary = "Get latest simulation", description = "Retrieve the most recent simulation result")
    public ResponseEntity<SimulationResult> getLatestSimulation() {
        try {
            SimulationResult latest = simulationService.getLatestSimulation();
            if (latest != null) {
                return ResponseEntity.ok(latest);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error retrieving latest simulation: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get simulation by ID", description = "Retrieve a specific simulation result by ID")
    public ResponseEntity<SimulationResult> getSimulationById(@PathVariable String id) {
        try {
            // This would require adding a findById method to SimulationService
            // For now, we'll return a not implemented response
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error retrieving simulation by ID: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
