rem docker run --name my-redis -p 6379:6379  -d redis
rem docker run -p 16379:6379 -d redis:6.0 redis-server --requirepass "mypass"
start docker exec -it my-redis /bin/bash
# redis-cli
# redis-cli HGETALL connection_info
# redis-cli --scan | head -10
# redis-cli PUBSUB CHANNELS
# redis-cli MONITOR
