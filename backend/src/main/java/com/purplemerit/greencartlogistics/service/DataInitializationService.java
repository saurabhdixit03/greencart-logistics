package com.purplemerit.greencartlogistics.service;

import com.purplemerit.greencartlogistics.model.*;
import com.purplemerit.greencartlogistics.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Service to initialize sample data for testing and demonstration
 */
@Service
public class DataInitializationService implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Autowired
    private RouteRepository routeRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        initializeData();
    }
    
    public void initializeData() {
        logger.info("Initializing sample data...");
        
        // Initialize users
        initializeUsers();
        
        // Initialize drivers
        initializeDrivers();
        
        // Initialize routes
        initializeRoutes();
        
        // Initialize orders
        initializeOrders();
        
        logger.info("Sample data initialization completed.");
    }
    
    private void initializeUsers() {
        if (userRepository.count() == 0) {
            logger.info("Creating default users...");
            
            // Create manager user
            User manager = new User();
            manager.setUsername("manager");
            manager.setEmail("manager@greencart.com");
            manager.setPassword(passwordEncoder.encode("manager123"));
            Set<User.Role> managerRoles = new HashSet<>();
            managerRoles.add(User.Role.MANAGER);
            manager.setRoles(managerRoles);
            userRepository.save(manager);
            
            // Create admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@greencart.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            Set<User.Role> adminRoles = new HashSet<>();
            adminRoles.add(User.Role.ADMIN);
            admin.setRoles(adminRoles);
            userRepository.save(admin);
            
            logger.info("Created default users: manager/manager123, admin/admin123");
        }
    }
    
    private void initializeDrivers() {
        if (driverRepository.count() == 0) {
            logger.info("Creating sample drivers...");
            
            Driver[] drivers = {
                new Driver("Rajesh Kumar", 6.5, 42.0),
                new Driver("Priya Sharma", 4.2, 38.5),
                new Driver("Amit Singh", 7.8, 45.2),
                new Driver("Sunita Devi", 5.1, 35.8),
                new Driver("Vikram Patel", 8.2, 48.7),
                new Driver("Meera Gupta", 3.9, 32.4),
                new Driver("Ravi Yadav", 6.8, 41.6),
                new Driver("Kavita Joshi", 5.5, 39.2)
            };
            
            // Set some drivers with fatigue penalty for testing
            drivers[2].setHasFatiguePenalty(true); // Amit Singh worked >8 hours yesterday
            drivers[4].setHasFatiguePenalty(true); // Vikram Patel worked >8 hours yesterday
            
            driverRepository.saveAll(Arrays.asList(drivers));
            logger.info("Created {} sample drivers", drivers.length);
        }
    }
    
    private void initializeRoutes() {
        if (routeRepository.count() == 0) {
            logger.info("Creating sample routes...");
            
            Route[] routes = {
                new Route("R001", 12.5, "Low", 45),
                new Route("R002", 8.3, "Medium", 35),
                new Route("R003", 15.7, "High", 65),
                new Route("R004", 6.2, "Low", 25),
                new Route("R005", 18.9, "High", 75),
                new Route("R006", 10.1, "Medium", 40),
                new Route("R007", 22.3, "High", 85),
                new Route("R008", 7.8, "Low", 30),
                new Route("R009", 13.6, "Medium", 50),
                new Route("R010", 9.4, "Low", 35)
            };
            
            // Set locations for routes
            routes[0].setStartLocation("Warehouse A");
            routes[0].setEndLocation("Sector 15, Noida");
            
            routes[1].setStartLocation("Warehouse B");
            routes[1].setEndLocation("Connaught Place, Delhi");
            
            routes[2].setStartLocation("Warehouse A");
            routes[2].setEndLocation("Gurgaon Cyber City");
            
            routes[3].setStartLocation("Warehouse C");
            routes[3].setEndLocation("Lajpat Nagar, Delhi");
            
            routes[4].setStartLocation("Warehouse A");
            routes[4].setEndLocation("Faridabad Industrial Area");
            
            for (int i = 5; i < routes.length; i++) {
                routes[i].setStartLocation("Warehouse " + (char)('A' + (i % 3)));
                routes[i].setEndLocation("Location " + (i + 1));
            }
            
            routeRepository.saveAll(Arrays.asList(routes));
            logger.info("Created {} sample routes", routes.length);
        }
    }
    
    private void initializeOrders() {
        if (orderRepository.count() == 0) {
            logger.info("Creating sample orders...");
            
            // Get available routes
            var routes = routeRepository.findAll();
            if (routes.isEmpty()) {
                logger.warn("No routes available for creating orders");
                return;
            }
            
            Order[] orders = {
                new Order("ORD001", 1250.0, "R001", LocalDateTime.now().plusHours(2)),
                new Order("ORD002", 875.0, "R002", LocalDateTime.now().plusHours(1)),
                new Order("ORD003", 1580.0, "R003", LocalDateTime.now().plusHours(3)),
                new Order("ORD004", 650.0, "R004", LocalDateTime.now().plusMinutes(45)),
                new Order("ORD005", 2100.0, "R005", LocalDateTime.now().plusHours(4)),
                new Order("ORD006", 920.0, "R006", LocalDateTime.now().plusHours(2)),
                new Order("ORD007", 1750.0, "R007", LocalDateTime.now().plusHours(5)),
                new Order("ORD008", 480.0, "R008", LocalDateTime.now().plusMinutes(30)),
                new Order("ORD009", 1320.0, "R009", LocalDateTime.now().plusHours(3)),
                new Order("ORD010", 760.0, "R010", LocalDateTime.now().plusHours(2)),
                new Order("ORD011", 1890.0, "R001", LocalDateTime.now().plusHours(6)),
                new Order("ORD012", 540.0, "R002", LocalDateTime.now().plusHours(1)),
                new Order("ORD013", 2250.0, "R003", LocalDateTime.now().plusHours(7)),
                new Order("ORD014", 1100.0, "R004", LocalDateTime.now().plusHours(2)),
                new Order("ORD015", 890.0, "R005", LocalDateTime.now().plusHours(4))
            };
            
            orderRepository.saveAll(Arrays.asList(orders));
            logger.info("Created {} sample orders", orders.length);
        }
    }
}
