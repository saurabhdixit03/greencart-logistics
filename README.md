# GreenCart Logistics - Delivery Simulation & KPI Dashboard

A comprehensive full-stack web application for simulating delivery operations and calculating KPIs based on custom business rules for GreenCart Logistics, an eco-friendly delivery company.

## 📋 Project Overview

GreenCart Logistics is a fictional eco-friendly delivery company operating in urban areas. This application serves as an internal tool that allows managers to:

- Simulate delivery operations with different staffing levels
- Calculate Key Performance Indicators (KPIs) based on custom company rules
- Manage drivers, routes, and orders through CRUD interfaces
- Visualize performance metrics through interactive dashboards
- Experiment with staffing, delivery schedules, and route allocations

## 🏗️ Architecture & Tech Stack

### Backend
- **Framework**: Java Spring Boot 3.1.3
- **Database**: MongoDB (Cloud-hosted)
- **Authentication**: JWT with Spring Security
- **Documentation**: OpenAPI 3.0 (Swagger)
- **Testing**: JUnit 5, Mockito
- **Build Tool**: Maven

### Frontend
- **Framework**: React 18 with Hooks
- **Routing**: React Router DOM v6
- **State Management**: Context API
- **UI Framework**: Tailwind CSS
- **Charts**: Recharts
- **HTTP Client**: Axios
- **Forms**: React Hook Form
- **Notifications**: React Hot Toast

### Development & Deployment
- **Version Control**: Git
- **Backend Deployment**: Railway/Render
- **Frontend Deployment**: Vercel/Netlify
- **Database**: MongoDB Atlas

## 🚀 Features

### 1. Authentication System
- JWT-based authentication with secure password hashing
- Role-based access control (Manager/Admin)
- Session management with token validation

### 2. Dashboard
- Real-time KPI displays (Total Profit, Efficiency Score, Delivery Performance)
- Interactive charts showing on-time vs late deliveries
- Fuel cost breakdown by traffic level
- Simulation history trends

### 3. Delivery Simulation Engine
- Custom business rules implementation:
  - **Late Delivery Penalty**: ₹50 if delivery > (base time + 10 minutes)
  - **Driver Fatigue Rule**: 30% slower delivery if worked >8 hours previous day
  - **High-Value Bonus**: 10% bonus for orders >₹1000 delivered on-time
  - **Fuel Cost Calculation**: ₹5/km base + ₹2/km surcharge for high traffic
  - **Profit Formula**: Order value + bonuses - penalties - fuel costs
  - **Efficiency Score**: (On-time deliveries / Total deliveries) × 100

### 4. Management Interfaces
- **Drivers Management**: CRUD operations for driver profiles
- **Routes Management**: Route configuration with traffic levels
- **Orders Management**: Order tracking and status updates

### 5. Data Visualization
- Responsive charts and graphs
- Real-time updates after simulations
- Historical performance tracking
- Mobile-friendly design

## 🛠️ Installation & Setup

### Prerequisites
- Java 17+
- Node.js 16+
- MongoDB Atlas account (or local MongoDB)
- Maven 3.6+

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd LogisticAppExperiment/backend
   ```

2. **Configure environment variables**
   Create `application-local.yml` in `src/main/resources/`:
   ```yaml
   spring:
     data:
       mongodb:
         uri: mongodb+srv://username:password@cluster.mongodb.net/greencart_logistics
   
   app:
     jwt:
       secret: yourJWTSecretKeyHere
       expiration: 86400000
   ```

3. **Install dependencies and run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Initialize default users**
   ```bash
   curl -X POST http://localhost:8080/api/auth/initialize
   ```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd ../frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Configure environment variables**
   Create `.env` file:
   ```env
   REACT_APP_API_URL=http://localhost:8080/api
   ```

4. **Start development server**
   ```bash
   npm start
   ```

The frontend will start on `http://localhost:3000`

## 🔐 Default Credentials

The application comes with pre-configured test users:

- **Manager**: `manager` / `manager123`
- **Admin**: `admin` / `admin123`

## 📊 API Documentation

### Interactive API Documentation
Once the backend is running, visit:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

### Key Endpoints

#### Authentication
```
POST /api/auth/signin          - User login
POST /api/auth/initialize      - Create default users
GET  /api/auth/validate        - Validate JWT token
```

#### Simulation
```
POST /api/simulation/run       - Run delivery simulation
GET  /api/simulation/history   - Get simulation history
GET  /api/simulation/latest    - Get latest simulation result
```

#### Management
```
GET    /api/drivers            - Get all drivers
POST   /api/drivers            - Create new driver
PUT    /api/drivers/{id}       - Update driver
DELETE /api/drivers/{id}       - Delete driver

GET    /api/routes             - Get all routes
POST   /api/routes             - Create new route
PUT    /api/routes/{id}        - Update route
DELETE /api/routes/{id}        - Delete route

GET    /api/orders             - Get all orders
POST   /api/orders             - Create new order
PUT    /api/orders/{id}        - Update order
DELETE /api/orders/{id}        - Delete order
```

## 🧪 Testing

### Backend Tests
```bash
cd backend
mvn test
```

Test coverage includes:
- Unit tests for business logic (SimulationService, Models)
- Controller tests for API endpoints
- Security tests for JWT utilities
- Integration tests for database operations

### Frontend Tests
```bash
cd frontend
npm test
```

## 🚀 Deployment

### Backend Deployment (Railway/Render)

1. **Prepare for deployment**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Deploy to Railway**
   - Connect your GitHub repository
   - Set environment variables in Railway dashboard
   - Deploy automatically on push

3. **Environment Variables for Production**
   ```
   MONGODB_URI=mongodb+srv://...
   JWT_SECRET=your-production-secret
   PORT=8080
   ```

### Frontend Deployment (Vercel/Netlify)

1. **Build the project**
   ```bash
   npm run build
   ```

2. **Deploy to Vercel**
   ```bash
   npm install -g vercel
   vercel --prod
   ```

3. **Environment Variables for Production**
   ```
   REACT_APP_API_URL=https://your-backend-url.com/api
   ```

## 📱 Usage Guide

### Running a Simulation

1. **Login** with manager/admin credentials
2. **Navigate** to the Simulation page
3. **Configure parameters**:
   - Number of available drivers (1-8)
   - Route start time (HH:MM format)
   - Maximum hours per driver per day (1-24)
   - Optional notes
4. **Click "Run Simulation"** to execute
5. **View results** showing:
   - Total profit calculated
   - Efficiency score percentage
   - On-time vs late delivery breakdown
   - Fuel cost analysis
   - Financial breakdown (bonuses, penalties, costs)

### Managing Data

1. **Drivers**: Add/edit driver information including work hours and fatigue status
2. **Routes**: Configure routes with distance, traffic levels, and base delivery times
3. **Orders**: Create and track orders with values and assigned routes

### Viewing Analytics

1. **Dashboard**: Real-time KPIs and performance metrics
2. **Charts**: Visual representation of delivery performance and costs
3. **History**: Track simulation results over time

## 🏢 Business Rules Implementation

The application implements the following custom business rules:

### 1. Late Delivery Penalty
- **Rule**: If actual delivery time > (base route time + 10 minutes)
- **Penalty**: ₹50 deducted from order profit
- **Implementation**: Applied during simulation in `SimulationService`

### 2. Driver Fatigue System
- **Rule**: If driver worked >8 hours previous day
- **Effect**: 30% increase in delivery time (slower speed)
- **Implementation**: Tracked via `Driver.hasFatiguePenalty` flag

### 3. High-Value Order Bonus
- **Rule**: Order value >₹1000 AND delivered on-time
- **Bonus**: 10% of order value added to profit
- **Implementation**: Calculated in `simulateDelivery()` method

### 4. Dynamic Fuel Cost Calculation
- **Base Cost**: ₹5 per kilometer
- **High Traffic Surcharge**: Additional ₹2 per kilometer
- **Implementation**: `Route.calculateFuelCost()` method

### 5. Profit Calculation
- **Formula**: Order Value + Bonuses - Penalties - Fuel Costs
- **Implementation**: `Order.calculateProfit()` method

### 6. Efficiency Score
- **Formula**: (On-time Deliveries ÷ Total Deliveries) × 100
- **Implementation**: Calculated in simulation results

## 🔧 Configuration

### Database Schema
- **Users**: Authentication and authorization
- **Drivers**: Driver profiles and work history
- **Routes**: Delivery routes with traffic data
- **Orders**: Customer orders and delivery status
- **SimulationResults**: Historical simulation data

### Security Configuration
- JWT token expiration: 24 hours
- Password encryption: BCrypt
- CORS enabled for frontend integration
- Role-based endpoint protection

## 🐛 Troubleshooting

### Common Issues

1. **MongoDB Connection Error**
   - Verify MongoDB URI in configuration
   - Check network connectivity
   - Ensure database user has proper permissions

2. **JWT Token Issues**
   - Verify JWT secret configuration
   - Check token expiration settings
   - Ensure proper Authorization header format

3. **CORS Errors**
   - Verify frontend URL in CORS configuration
   - Check environment variables
   - Ensure proper API base URL

4. **Simulation Failures**
   - Verify sufficient active drivers
   - Check for active routes in database
   - Ensure pending orders exist

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Contributors

- **Developer**: Purple Merit Technologies Assessment Project
- **Contact**: career@purplemerit.com

## 🎯 Assessment Criteria Met

✅ **Backend**: Java Spring Boot with MongoDB  
✅ **Frontend**: React with Hooks  
✅ **Authentication**: JWT-based with password hashing  
✅ **CRUD Operations**: Complete management interfaces  
✅ **Custom Business Logic**: All 6 company rules implemented  
✅ **Charts**: Interactive visualizations with Recharts  
✅ **Responsive Design**: Mobile and desktop optimized  
✅ **API Documentation**: OpenAPI/Swagger integration  
✅ **Testing**: Unit and integration tests  
✅ **Deployment**: Production-ready configuration  
✅ **Data Persistence**: Simulation history with timestamps  
✅ **Error Handling**: Comprehensive validation and error responses  

---

**Built with ❤️ for Purple Merit Technologies Full-Stack Developer Assessment**
