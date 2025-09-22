# 🚀 Deployment Guide - GreenCart Logistics

This guide provides step-by-step instructions for deploying the GreenCart Logistics application to production environments.

## 📋 Prerequisites

- MongoDB Atlas account
- GitHub repository
- Railway/Render account (for backend)
- Vercel/Netlify account (for frontend)

## 🗄️ Database Setup (MongoDB Atlas)

1. **Create MongoDB Atlas Cluster**
   - Go to [MongoDB Atlas](https://www.mongodb.com/atlas)
   - Create a new cluster (free tier available)
   - Choose your preferred cloud provider and region

2. **Configure Database Access**
   - Create a database user with read/write permissions
   - Add your IP address to the IP Access List (or use 0.0.0.0/0 for all IPs)

3. **Get Connection String**
   - Click "Connect" on your cluster
   - Choose "Connect your application"
   - Copy the connection string
   - Replace `<password>` with your database user password
   - Replace `<dbname>` with `greencart_logistics`

## 🔧 Backend Deployment (Railway)

### Option 1: Deploy to Railway

1. **Prepare Repository**
   ```bash
   git add .
   git commit -m "Prepare for deployment"
   git push origin main
   ```

2. **Deploy to Railway**
   - Visit [Railway](https://railway.app/)
   - Sign up/Login with GitHub
   - Click "New Project" → "Deploy from GitHub repo"
   - Select your repository

3. **Configure Environment Variables**
   In Railway dashboard, add these variables:
   ```
   MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/greencart_logistics
   JWT_SECRET=your-super-secret-jwt-key-here
   PORT=8080
   SPRING_PROFILES_ACTIVE=prod
   ```

4. **Deploy**
   - Railway will automatically build and deploy your Spring Boot application
   - Note the generated URL (e.g., `https://your-app.railway.app`)

### Option 2: Deploy to Render

1. **Create Render Account**
   - Visit [Render](https://render.com/)
   - Sign up with GitHub

2. **Create Web Service**
   - Click "New" → "Web Service"
   - Connect your GitHub repository
   - Configure build settings:
     - **Build Command**: `cd backend && mvn clean package -DskipTests`
     - **Start Command**: `cd backend && java -jar target/greencart-logistics-0.0.1-SNAPSHOT.jar`

3. **Set Environment Variables**
   ```
   MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/greencart_logistics
   JWT_SECRET=your-super-secret-jwt-key-here
   PORT=8080
   SPRING_PROFILES_ACTIVE=prod
   ```

## 🌐 Frontend Deployment (Vercel)

### Option 1: Deploy to Vercel

1. **Install Vercel CLI**
   ```bash
   npm install -g vercel
   ```

2. **Build and Deploy**
   ```bash
   cd frontend
   npm run build
   vercel --prod
   ```

3. **Configure Environment Variables**
   In Vercel dashboard:
   ```
   REACT_APP_API_URL=https://your-backend-url.railway.app/api
   ```

4. **Redeploy**
   ```bash
   vercel --prod
   ```

### Option 2: Deploy to Netlify

1. **Build the Project**
   ```bash
   cd frontend
   npm run build
   ```

2. **Deploy to Netlify**
   - Visit [Netlify](https://www.netlify.com/)
   - Drag and drop the `build` folder to deploy
   - Or connect your GitHub repository for automatic deployments

3. **Configure Environment Variables**
   In Netlify dashboard → Site settings → Environment variables:
   ```
   REACT_APP_API_URL=https://your-backend-url.railway.app/api
   ```

## 🔒 Security Considerations

### Backend Security
- Use strong JWT secrets (at least 32 characters)
- Enable HTTPS in production
- Configure CORS properly
- Use environment variables for sensitive data

### Database Security
- Use strong database passwords
- Limit IP access to your application servers
- Enable MongoDB authentication
- Regular security updates

## 🧪 Testing Deployment

### 1. Test Backend API
```bash
# Test health endpoint
curl https://your-backend-url.railway.app/actuator/health

# Test authentication
curl -X POST https://your-backend-url.railway.app/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"manager","password":"manager123"}'
```

### 2. Test Frontend
- Visit your frontend URL
- Test login functionality
- Run a simulation
- Check dashboard displays

## 📊 Monitoring & Logs

### Railway Monitoring
- Check deployment logs in Railway dashboard
- Monitor resource usage
- Set up alerts for downtime

### Application Logs
- Spring Boot logs are available in Railway/Render dashboard
- Monitor for errors and performance issues
- Set up log aggregation if needed

## 🔄 CI/CD Pipeline (Optional)

### GitHub Actions Example
Create `.github/workflows/deploy.yml`:

```yaml
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  deploy-backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Setup Java
        uses: actions/setup-java@v2
        with:
          java-version: '17'
          distribution: 'adopt'
      - name: Build with Maven
        run: cd backend && mvn clean package -DskipTests
      - name: Deploy to Railway
        # Add Railway deployment steps

  deploy-frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Setup Node.js
        uses: actions/setup-node@v2
        with:
          node-version: '16'
      - name: Install dependencies
        run: cd frontend && npm install
      - name: Build
        run: cd frontend && npm run build
      - name: Deploy to Vercel
        # Add Vercel deployment steps
```

## 🚨 Troubleshooting

### Common Issues

1. **Backend won't start**
   - Check MongoDB connection string
   - Verify environment variables
   - Check Java version compatibility

2. **Frontend can't connect to backend**
   - Verify REACT_APP_API_URL
   - Check CORS configuration
   - Ensure backend is running

3. **Database connection issues**
   - Check MongoDB Atlas IP whitelist
   - Verify connection string format
   - Test connection from local environment

4. **Authentication not working**
   - Check JWT secret configuration
   - Verify password encoding
   - Test with default credentials

### Debugging Steps

1. **Check Logs**
   ```bash
   # Railway logs
   railway logs

   # Local testing
   cd backend && mvn spring-boot:run
   cd frontend && npm start
   ```

2. **Test API Endpoints**
   - Use Postman or curl to test API
   - Check Swagger documentation: `https://your-backend-url/swagger-ui.html`

3. **Database Verification**
   - Connect to MongoDB Atlas directly
   - Verify collections and data

## 📝 Post-Deployment Checklist

- [ ] Backend API is accessible
- [ ] Frontend loads without errors
- [ ] Authentication works
- [ ] Database connection established
- [ ] Sample data is loaded
- [ ] Simulations can be run
- [ ] CRUD operations work
- [ ] Charts display correctly
- [ ] Mobile responsiveness tested
- [ ] SSL certificates configured
- [ ] Domain names configured (optional)
- [ ] Monitoring set up
- [ ] Backup strategy in place

## 🔗 Useful Links

- [Railway Documentation](https://docs.railway.app/)
- [Render Documentation](https://render.com/docs)
- [Vercel Documentation](https://vercel.com/docs)
- [Netlify Documentation](https://docs.netlify.com/)
- [MongoDB Atlas Documentation](https://docs.atlas.mongodb.com/)
- [Spring Boot Deployment Guide](https://spring.io/guides/gs/spring-boot-docker/)

## 📞 Support

If you encounter issues during deployment:

1. Check the troubleshooting section above
2. Review platform-specific documentation
3. Check GitHub repository issues
4. Contact: career@purplemerit.com

---

**🎉 Congratulations! Your GreenCart Logistics application is now live in production!**
