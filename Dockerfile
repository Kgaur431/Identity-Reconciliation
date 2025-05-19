# Use Eclipse Temurin JDK 17 for building
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Download dependencies (this step is cached unless build files change)
RUN ./gradlew dependencies --no-daemon

# Copy the rest of the source code
COPY . .

# Build the project, skipping tests for faster build (you can remove -x test if you want tests to run)
RUN ./gradlew build -x test --no-daemon

# Use a smaller JRE image for running the app
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR from the build image
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# Pass the dev profile (can override with Render env vars if needed)
ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java", "-jar", "app.jar"]
