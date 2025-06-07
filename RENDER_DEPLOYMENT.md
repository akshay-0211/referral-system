# Deploying to Render

This guide explains how to deploy the User Referral System to Render.

## Prerequisites

1. **Render Account**

   - Sign up at [render.com](https://render.com)
   - Verify your email address

2. **MongoDB Atlas**
   - Create a MongoDB Atlas account if you don't have one
   - Create a new cluster
   - Get your connection string

## Deployment Steps

1. **Push Code to GitHub**

   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git remote add origin <your-github-repo-url>
   git push -u origin main
   ```

2. **Create New Web Service on Render**

   - Go to [render.com/dashboard](https://render.com/dashboard)
   - Click "New +" and select "Web Service"
   - Connect your GitHub repository
   - Select the repository

3. **Configure Service**

   - Name: `referral-system`
   - Environment: `Java`
   - Build Command: `./mvnw clean package -DskipTests`
   - Start Command: `java -jar target/*.jar`

4. **Set Environment Variables**
   Click "Environment" and add:

   ```
   SPRING_DATA_MONGODB_URI=your_mongodb_uri
   ADMIN_USERNAME=your_admin_username
   ADMIN_PASSWORD=your_admin_password
   SPRING_PROFILES_ACTIVE=prod
   ```

5. **Deploy**
   - Click "Create Web Service"
   - Render will automatically deploy your application
   - Wait for the deployment to complete

## Verifying Deployment

1. **Check Health**

   - Visit `https://your-app-name.onrender.com/api/users`
   - Should return a 200 OK response

2. **Test API Endpoints**
   Use the curl commands from `curl-commands.md`, replacing `localhost:8080` with your Render URL:
   ```bash
   curl -X POST https://your-app-name.onrender.com/api/users/signup \
     -H "Content-Type: application/json" \
     -d '{
       "name": "Test User",
       "email": "test@example.com",
       "password": "password123"
     }'
   ```

## Monitoring

1. **Logs**

   - Go to your service on Render dashboard
   - Click "Logs" to view application logs

2. **Metrics**
   - Click "Metrics" to view:
     - CPU usage
     - Memory usage
     - Response times

## Troubleshooting

1. **Deployment Fails**

   - Check build logs
   - Verify environment variables
   - Ensure MongoDB URI is correct

2. **Application Crashes**

   - Check application logs
   - Verify MongoDB connection
   - Check memory usage

3. **Slow Response Times**
   - Check MongoDB performance
   - Monitor resource usage
   - Consider upgrading plan

## Updating Application

1. **Automatic Updates**

   - Push to GitHub
   - Render automatically redeploys

2. **Manual Updates**
   - Go to Render dashboard
   - Click "Manual Deploy"
   - Select "Deploy latest commit"

## Security Considerations

1. **Environment Variables**

   - Never commit sensitive data
   - Use Render's environment variables
   - Rotate credentials regularly

2. **MongoDB Security**
   - Use strong passwords
   - Enable IP whitelist
   - Regular security audits

## Cost Management

1. **Free Tier Limits**

   - 750 hours/month
   - 512 MB RAM
   - Shared CPU

2. **Upgrading**
   - Monitor usage
   - Upgrade when needed
   - Consider reserved instances

## Support

- Render Documentation: [docs.render.com](https://docs.render.com)
- MongoDB Atlas: [docs.atlas.mongodb.com](https://docs.atlas.mongodb.com)
- GitHub Issues: [github.com/your-repo/issues](https://github.com/your-repo/issues)
