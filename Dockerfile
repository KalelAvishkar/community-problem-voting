# Use Java 17
FROM eclipse-temurin:17-jdk

# App directory
WORKDIR /app

# Copy project files
COPY . .

# Build jar
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Run app
CMD ["java", "-jar", "target/community-problem-voting.jar"]