# ==========================================
# Stage 1: Build & Automated Test Gate
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace

# Copy Gradle wrapper and configuration
COPY gradlew settings.gradle gradle.properties build.gradle ./
COPY gradle ./gradle

# Grant execution rights to the Gradle wrapper
RUN chmod +x ./gradlew

# Copy application source code
COPY src ./src

# Automated Test Gate: Run test suite and package executable Spring Boot JAR
RUN ./gradlew test bootJar --no-daemon -Dorg.gradle.jvmargs="-Xmx384m"

# ==========================================
# Stage 2: Minimal Production Runtime Image
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Run as non-root user for cloud security best practices
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy executable jar from builder stage
COPY --from=builder /workspace/build/libs/*.jar app.jar

# Render & cloud platforms inject PORT dynamically
ENV PORT=8080
EXPOSE 8080

# Clean signal handling and memory bounds
ENTRYPOINT ["sh", "-c", "exec java -Xmx384m -XX:+UseG1GC -jar app.jar"]

