package com.purplemerit.greencartlogistics.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Swagger documentation
 */
@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Local Development Server"),
                new Server().url("https://your-app.herokuapp.com").description("Production Server")
            ))
            .info(new Info()
                .title("GreenCart Logistics API")
                .description("Delivery Simulation & KPI Dashboard Backend API\n\n" +
                           "This API provides endpoints for managing drivers, routes, orders, and running delivery simulations " +
                           "with custom business rules for GreenCart Logistics.\n\n" +
                           "**Authentication:** Use JWT Bearer token in Authorization header\n\n" +
                           "**Default Credentials:**\n" +
                           "- Manager: `manager` / `manager123`\n" +
                           "- Admin: `admin` / `admin123`\n\n" +
                           "**Company Rules Implemented:**\n" +
                           "1. Late Delivery Penalty: ₹50 if delivery > (base time + 10 min)\n" +
                           "2. Driver Fatigue: 30% slower if worked >8 hours previous day\n" +
                           "3. High-Value Bonus: 10% bonus if order >₹1000 AND on-time\n" +
                           "4. Fuel Cost: ₹5/km base + ₹2/km surcharge for high traffic\n" +
                           "5. Profit: order value + bonus - penalties - fuel cost\n" +
                           "6. Efficiency: (on-time deliveries / total deliveries) × 100")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Purple Merit Technologies")
                    .email("career@purplemerit.com")
                    .url("https://purplemerit.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")));
    }
}
