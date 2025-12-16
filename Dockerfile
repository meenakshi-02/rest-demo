# === STAGE 1: The BUILDER Stage ===
# 1. Use a standard, well-maintained JDK image (Eclipse Temurin is highly recommended).
# 2. RENAME the stage to 'builder' (best practice) or ensure it's unique.
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set the working directory
WORKDIR /app

# 1. COPY the necessary files for dependency resolution AND the Maven Wrapper
# This leverages Docker's build cache: if pom.xml doesn't change, dependencies aren't downloaded again.
# The '.' copies mvnw, .mvn folder, and pom.xml
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# 2. Download dependencies (this often takes the longest time)
# We use 'dependency:resolve' which only downloads dependencies without running the main build.
# We include 'RUN mvn install --settings .m2/settings.xml -DskipTests' if you have custom settings
# RUN mvn dependency:go-offline -B
# We skip the 'go-offline' step because using 'mvnw package' handles it fine,
# and we need to copy the source code first to use the wrapper.

# 3. Copy the source code *after* the dependencies are resolved (must be done before package)
COPY src src

# 4. Run the actual package build
# RUN mvn package -DskipTests
# Change the RUN command to use the wrapper: ./mvnw
RUN ./mvnw package -DskipTests


# === STAGE 2: The FINAL (RUNTIME) Stage ===
# 5. Use a lightweight JRE image (even smaller than openjdk:21-jdk-slim).
FROM eclipse-temurin:21-jre-alpine

# Set the working directory
WORKDIR /app

# 6. Copy the built JAR from the 'builder' stage
# Ensure the path matches your project structure
COPY --from=builder /app/target/*.jar app.jar

# 7. Expose the port (default for Spring Boot)
EXPOSE 8080

# 8. Define the command to run the application (using the smaller image entry point)
ENTRYPOINT ["java", "-jar", "app.jar"]