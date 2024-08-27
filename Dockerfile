FROM maven:3.5-jdk-8-alpine AS Builder


# Copy local code to the container image
WORKDIR /app
COPY pom.xml .
COPY src ./src


# Build a release artufact

RUN mvn package -DskipTests

# Make sure any scripts have execute permissions
RUN chmod +x /app/target/usercenter-0.0.1-SNAPSHOT.jar

#Run the web service on container startup
CMD ["java","-jar","/app/target/usercenter-0.0.1-SNAPSHOT.jar","--spring.profile.active=prod"]