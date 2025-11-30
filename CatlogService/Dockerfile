# ---- build stage ----
FROM maven:3.9.2-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -e -DskipTests package

# ---- runtime stage ----
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# create non-root user
RUN addgroup -S app && adduser -S -G app app
USER app

COPY --from=builder /workspace/target/*.jar app.jar

ENV JAVA_OPTS="-Xms128m -Xmx384m -XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0"

EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
