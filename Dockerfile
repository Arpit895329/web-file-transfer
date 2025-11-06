# Use a base image with Java 17
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory
WORKDIR /app

# Copy the entire project
COPY . .

# Make the Maven wrapper executable
RUN chmod +x mvnw

# Build the application
# We skip tests to make the build faster
RUN ./mvnw clean package -DskipTests

# Command to run the app.
# It will use the $PORT variable from Render
CMD ["java", "-jar", "target/filetransfer-0.0.1-SNAPSHOT.jar"]