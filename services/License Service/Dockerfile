##### Base dockerfile config #####

## Start with a base image containing Java Runtime
#FROM amazoncorretto:22
#
## Add maintainer info
#LABEL maintainer="Allan Elong <venomcocytus-github-io.pages.dev>"
#
## The application Jar file
#ARG JAR_FILE
#
## Add the application 's jar file to the containter
#COPY ${JAR_FILE} app.jar
#
## Execute the application
#ENTRYPOINT ["java", "-jar", "/app.jar"]

##### Multistage config #####

# Stage 1
# Start with a base image containing Java runtime
FROM amazoncorretto:22-alpine-jdk AS build

# Install necessary packages
RUN apk add --no-cache shadow

# Create a non-root user for building (optional but recommended for security)
RUN addgroup -S appgroup && adduser -S -G appgroup appuser

# Add Maintainer Info
LABEL maintainer="Allan Elong <venomcocytus-github-io.pages.dev>"

# The application's jar file
ARG JAR_FILE

# Add the application's jar to the container
COPY ${JAR_FILE} app.jar

# Unpackage jar file
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf /app.jar)

# Stage 2
# Same Java runtime
FROM amazoncorretto:22-alpine-jdk

# Create a non-root user for runtime (recommended)
RUN addgroup -S appgroup && adduser -S -G appgroup appuser \
    && mkdir /app \
    && chown appuser:appgroup /app \
    && chmod 755 /app

USER appuser

# Add volume pointing to /tmp
VOLUME /tmp

# Copy unpackaged application to new container
ARG DEPENDENCY=/target/dependency
COPY --from=build --chown=appuser:appgroup ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build --chown=appuser:appgroup ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build --chown=appuser:appgroup ${DEPENDENCY}/BOOT-INF/classes /app

#execute the application
ENTRYPOINT ["java","-cp","app:app/lib/*","com.optimagrowth.license.LicenseServiceApplication"]