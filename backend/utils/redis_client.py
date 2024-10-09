import os
from redis import Redis

# Get the Redis host and port from environment variables
redis_host = os.getenv('REDIS_HOST', 'redis')
redis_port = os.getenv('REDIS_PORT', 6379)
# Create and configure the Redis client
# Use localhost and default port 6379 for the Redis server
redis_client = Redis(host=redis_host, port=redis_port, decode_responses=True)

print("Redis client created successfully!")
