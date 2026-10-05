# Mini-Redis Gateway

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Redis Protocol](https://img.shields.io/badge/Redis-RESP%20Compatible-red.svg)](https://redis.io/)

A high-performance, lightweight **HTTP REST API Gateway** for Redis-compatible databases (specifically paired with [`mini-redis`](../mini-redis)). 

---

## 📌 Purpose & Architecture

Web applications, browsers, serverless functions, and HTTP clients cannot speak raw TCP/RESP (REdis Serialization Protocol) directly. **Mini-Redis Gateway** bridges this gap:

```
+--------------------------+       HTTP / JSON       +-------------------------+       Raw TCP / RESP       +-------------------------+
|   Client Application     |  -------------------->  |   Mini-Redis Gateway    |  ----------------------->  |       Mini-Redis        |
|  (React, Game, Webhooks) |  <--------------------  |   (Spring Boot 4.1.1)   |  <-----------------------  |   (Port 6380 / 6379)    |
+--------------------------+                         +-------------------------+                            +-------------------------+
```

- **Translates REST to TCP**: Accepts clean HTTP JSON requests, executes commands over raw TCP sockets via Jedis & `StringRedisTemplate`, and returns formatted JSON.
- **Unified Redis-Style Envelope**: All data endpoints return a standardized `{"result": ...}` envelope matching official Redis return types (`OK`, integer counts, strings, or `null`).
- **Resource-Oriented Design**: Cleanly separated controllers following strict REST hierarchies (`KeyController`, `TTLController`, `IncrController`, `HealthController`).
- **Low Memory Footprint**: Tuned with JVM flags and G1GC to run comfortably on resource-constrained environments (capped to 256 MB heap).

---

## 🚀 API Reference

All requests and responses use `application/json`.

### 1. Health & Status
Used by uptime monitoring and cloud container health probes (Render, Railway, Fly.io).

| Method | Endpoint | Description | Response Example |
| :--- | :--- | :--- | :--- |
| `GET` | `/health`<br>`/api/health`<br>`/` | Pings the Redis TCP server | `{"status": "Up", "redis": "PONG"}` |

```bash
curl http://localhost:8080/health
```

---

### 2. Key Lifecycle & Strings (`/api/keys`)

Handles standard key storage, retrieval, existence checks, and deletion.

| Method | Endpoint | Description | Request Body | Response Body |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/keys/{key}` | Retrieve string value (`GET`) | *None* | `{"result": "Alice"}` or `{"result": null}` |
| `POST` | `/api/keys` | Set key value (`SET`) | `{"key": "user:1", "value": "Alice"}` | `{"result": "OK"}` |
| `DELETE` | `/api/keys/{key}` | Delete key (`DEL`) | *None* | `{"result": 1}` *(0 if not found)* |
| `GET` | `/api/keys/{key}/exists` | Check if key exists (`EXISTS`) | *None* | `{"result": 1}` *(0 if not exists)* |

#### Examples:
```bash
# Set a key
curl -X POST http://localhost:8080/api/keys \
  -H "Content-Type: application/json" \
  -d '{"key": "player:101", "value": "Alice"}'

# Get a key
curl http://localhost:8080/api/keys/player:101

# Check key existence
curl http://localhost:8080/api/keys/player:101/exists

# Delete a key
curl -X DELETE http://localhost:8080/api/keys/player:101
```

---

### 3. Expiration & TTL (`/api/keys/...`)

Manages key expiration, remaining time-to-live, and persistence.

| Method | Endpoint | Description | Request Body | Response Body |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/keys/{key}/ttl` | Get remaining TTL in seconds (`TTL` / `PTTL`) | *None* | `{"result": 60}`<br>`-1` *(no expiry)*<br>`-2` *(key not found)* |
| `POST` | `/api/keys/ttl` | Set expiration in seconds (`EXPIRE` / `PEXPIRE`) | `{"key": "session:abc", "ttl": 300}` | `{"result": 1}` *(0 if failed)* |
| `POST` | `/api/keys/{key}/persist` | Remove expiration (`PERSIST`) | *None* | `{"result": 1}` *(0 if no TTL)* |

#### Examples:
```bash
# Set TTL (300 seconds)
curl -X POST http://localhost:8080/api/keys/ttl \
  -H "Content-Type: application/json" \
  -d '{"key": "session:abc", "ttl": 300}'

# Check remaining TTL
curl http://localhost:8080/api/keys/session:abc/ttl

# Make key persistent (remove TTL)
curl -X POST http://localhost:8080/api/keys/session:abc/persist
```

---

### 4. Atomic Counters (`/api/keys/{key}/incr`)

Executes atomic numerical operations for scoreboards, rate limiters, and game state.

| Method | Endpoint | Description | Query Parameters | Response Body |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/keys/{key}/incr` | Increment by 1 (`INCR`) | *None* | `{"result": 1}` |
| `POST` | `/api/keys/{key}/incrby` | Increment by delta (`INCRBY`) | `?delta=<number>` | `{"result": 15}` |

#### Examples:
```bash
# Increment counter by 1
curl -X POST http://localhost:8080/api/keys/room:1:score/incr

# Increment counter by custom delta (e.g. +50 points)
curl -X POST "http://localhost:8080/api/keys/room:1:score/incrby?delta=50"
```

---

## ⚙️ Configuration & Environment Variables

Configure the gateway via environment variables or Spring configuration:

| Variable | Default Value | Description |
| :--- | :--- | :--- |
| `REDIS_HOST` | `localhost` | Hostname / IP address of the target Redis server |
| `REDIS_PORT` | `6380` | Port of the target Redis server (default for `mini-redis`) |
| `PORT` | `8080` | HTTP port for this Spring Boot Gateway service |

---

## 🛠️ Local Development & Running

### Prerequisites
- **Java 21** or higher
- Running instance of **`mini-redis`** (listening on port `6380`) or standard Redis (`6379`)

### Run Application
```bash
# Linux / macOS / WSL
./gradlew bootRun

# Windows PowerShell / CMD
.\gradlew.bat bootRun
```

### Build Executable JAR
```bash
./gradlew bootJar
```
The compiled fat JAR will be generated at: `build/libs/mini-redis-gateway-0.0.1-SNAPSHOT.jar`.

### Run Built JAR with Custom Port / Host:
```bash
java -Xmx256m -DREDIS_HOST=127.0.0.1 -DREDIS_PORT=6380 -jar build/libs/mini-redis-gateway-0.0.1-SNAPSHOT.jar
```

---

## 🌐 Production Deployment

### Deploying on Render / Railway
1. **Target Mini-Redis**: Deploy `mini-redis` to a TCP-supporting host (Railway, Fly.io, or VPS) to obtain an external TCP address (e.g. `roundhouse.proxy.rlwy.net:12345`).
2. **Deploy Gateway**: Deploy `mini-redis-gateway` on Render as a **Web Service**:
   - Build Command: `./gradlew bootJar`
   - Start Command: `java -jar build/libs/mini-redis-gateway-0.0.1-SNAPSHOT.jar`
   - Set Environment Variables:
     - `REDIS_HOST` = `<your-tcp-host>`
     - `REDIS_PORT` = `<your-tcp-port>`

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
