# Deployment Guide

This guide explains how to deploy the User Referral System to a production environment.

## Prerequisites

1. **MongoDB Atlas**

   - Create a MongoDB Atlas account
   - Create a new cluster
   - Get your connection string
   - Create a database user with appropriate permissions

2. **Cloud Provider Account**
   - AWS, Google Cloud, or Azure account
   - Basic knowledge of cloud services

## Deployment Options

### Option 1: AWS Elastic Beanstalk (Recommended)

1. **Install AWS CLI and EB CLI**

   ```bash
   pip install awsebcli
   ```

2. **Create Elastic Beanstalk Application**

   ```bash
   eb init -p java referral-system
   ```

3. **Create Environment**

   ```bash
   eb create referral-system-env
   ```

4. **Configure Environment Variables**

   ```bash
   eb setenv SPRING_DATA_MONGODB_URI="your_mongodb_uri"
   ```

5. **Deploy**
   ```bash
   eb deploy
   ```

### Option 2: Docker Deployment

1. **Build Docker Image**

   ```bash
   docker build -t referral-system .
   ```

2. **Run Container**
   ```bash
   docker run -p 8080:8080 \
     -e SPRING_DATA_MONGODB_URI="your_mongodb_uri" \
     referral-system
   ```

## Environment Configuration

### Production Properties

Create `application-prod.properties`:

```properties
# Server Configuration
server.port=8080

# MongoDB Configuration
spring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI}

# Security Configuration
spring.security.user.name=${ADMIN_USERNAME}
spring.security.user.password=${ADMIN_PASSWORD}

# Logging Configuration
logging.level.root=WARN
logging.level.com.referral=INFO
```

### Environment Variables

Required environment variables:

- `SPRING_DATA_MONGODB_URI`: MongoDB connection string
- `ADMIN_USERNAME`: Admin username
- `ADMIN_PASSWORD`: Admin password

## Monitoring

1. **Application Logs**

   - Use cloud provider's logging service
   - Configure log rotation
   - Set up log alerts

2. **Performance Monitoring**
   - Monitor API response times
   - Track database performance
   - Set up alerts for errors

## Security Considerations

1. **SSL/TLS**

   - Enable HTTPS
   - Configure SSL certificates
   - Force HTTPS redirect

2. **Network Security**

   - Configure firewall rules
   - Use VPC if applicable
   - Implement rate limiting

3. **Data Security**
   - Encrypt sensitive data
   - Regular security audits
   - Backup strategy

## Backup Strategy

1. **Database Backups**

   - Enable MongoDB Atlas backups
   - Configure backup schedule
   - Test restore process

2. **Application Backups**
   - Backup configuration files
   - Document deployment process
   - Version control all changes

## Scaling

1. **Horizontal Scaling**

   - Configure auto-scaling
   - Load balancer setup
   - Session management

2. **Vertical Scaling**
   - Monitor resource usage
   - Adjust instance size
   - Optimize performance

## Maintenance

1. **Updates**

   - Regular security patches
   - Dependency updates
   - Database maintenance

2. **Monitoring**
   - Health checks
   - Performance metrics
   - Error tracking

## Troubleshooting

Common issues and solutions:

1. **Connection Issues**

   - Check MongoDB URI
   - Verify network access
   - Check firewall rules

2. **Performance Issues**

   - Monitor resource usage
   - Check database indexes
   - Review application logs

3. **Deployment Issues**
   - Check build logs
   - Verify environment variables
   - Review deployment configuration
