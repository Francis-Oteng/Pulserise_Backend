# GROQ API Configuration Guide

## Overview
The PulseRise backend now supports separate GROQ API keys for Chat and Workout Recommendation services. This allows for better service isolation and monitoring.

## API Keys Provided
- **Chat Service API Key**: `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`
- **Workout Recommendation API Key**: `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`

## Configuration Methods

### Method 1: Environment Variables (Recommended for Production)
Set these environment variables in your deployment platform (Railway, Heroku, etc.):

```bash
CHAT_API_KEY=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
WORKOUT_RECOMMENDATION_API_KEY=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
```

### Method 2: Application Properties (Development)
The keys are already configured in `application.properties` with default values:

```properties
# Chat Service API Key
groq.chat.api.key=${CHAT_API_KEY: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}

# Workout Recommendation Service API Key
groq.recommendation.api.key=${WORKOUT_RECOMMENDATION_API_KEY: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}
```

## Railway Deployment Configuration

### Step 1: Set Environment Variables in Railway
1. Go to your Railway project dashboard
2. Navigate to the "Variables" tab
3. Add the following environment variables:
   - `CHAT_API_KEY` = `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`
   - `WORKOUT_RECOMMENDATION_API_KEY` = `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`

### Step 2: Redeploy the Application
1. Trigger a new deployment in Railway
2. The application will automatically pick up the new API keys
3. Both chat and recommendation services should now work properly

## Verification

### Test Chat Service
```bash
curl -X POST https://pulserisebackend-production.up.railway.app/api/chat/message \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"message": "Hello, can you help me with a workout plan?"}'
```

### Test Recommendation Service
```bash
curl -X POST https://pulserisebackend-production.up.railway.app/api/recommendations/generate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "userId": 1,
    "fitnessLevel": "intermediate",
    "fitnessGoals": ["muscle gain"],
    "workoutDaysPerWeek": 4,
    "workoutDurationMinutes": 60,
    "availableEquipment": ["gym"]
  }'
```

## Service Status Endpoints
Check if the services are properly configured:

- **Chat Status**: `GET /api/chat/status`
- **Recommendation Status**: `GET /api/recommendations/status`

Both should return `{"available": true, "message": "Service is available"}` when properly configured.

## Configuration Features

### Separate Service Configuration
- **Chat Service**: Optimized for conversational responses (temperature: 0.7, max_tokens: 1000)
- **Recommendation Service**: Optimized for structured workout plans (temperature: 0.8, max_tokens: 1500)

### Backward Compatibility
The system maintains backward compatibility with the legacy `GROQ_API_KEY` environment variable.

### Fallback Mechanism
If specific service keys are not configured, the system falls back to the legacy configuration.

## Troubleshooting

### Issue: Services Still Not Working
1. Verify environment variables are set correctly in Railway
2. Check the application logs for API key validation errors
3. Ensure the API keys are valid and have sufficient quota

### Issue: Only One Service Working
1. Check that both environment variables are set
2. Verify there are no typos in the environment variable names
3. Restart the application after setting new variables

### Issue: API Key Quota Exceeded
1. Monitor GROQ API usage in your GROQ dashboard
2. Consider upgrading your GROQ plan if needed
3. The system will gracefully fall back to mock responses if quota is exceeded

## Security Notes
- Never commit API keys to version control
- Use environment variables for production deployments
- Rotate API keys regularly for security
- Monitor API usage to prevent unexpected charges

## Next Steps
1. Set the environment variables in Railway
2. Redeploy the application
3. Test both chat and recommendation services
4. Monitor the application logs for any issues
5. The frontend fallback systems will automatically switch to real AI responses once configured