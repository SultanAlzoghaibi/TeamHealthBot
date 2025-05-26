# Step 1: Base image with Java 24
FROM eclipse-temurin:24-jdk AS builder

# Set environment variables
ENV MAVEN_VERSION=3.9.6
ENV MAVEN_HOME=/opt/maven
ENV PATH=${MAVEN_HOME}/bin:${PATH}

# Install dependencies and Maven
RUN apt-get update && apt-get install -y curl unzip && \
    curl -fsSL https://downloads.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.zip -o maven.zip && \
    unzip maven.zip && mv apache-maven-${MAVEN_VERSION} ${MAVEN_HOME} && \
    rm maven.zip && apt-get clean

WORKDIR /app
COPY . .

# Build the project using Maven inside the container
RUN mvn clean package -DskipTests

# Step 2: Runtime container
FROM eclipse-temurin:24-jdk

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8001
ENTRYPOINT ["java", "-jar", "app.jar"]