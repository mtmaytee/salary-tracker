FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests
CMD ["java", "-jar", "target/salarytracker-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]