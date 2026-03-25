# Step 1: Build JAR using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .

# ✅ mvnw ko executable permission dena
RUN chmod +x mvnw

# ✅ build jar
RUN mvn clean package -DskipTests

# Step 2: Run JAR
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/target/community-problem-voting.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]