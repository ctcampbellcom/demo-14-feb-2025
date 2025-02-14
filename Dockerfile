# The version number tag for the base container
ARG FROM_VERSION=11-jre-alpine
FROM eclipse-temurin:${FROM_VERSION}

RUN apk update \
  && apk upgrade  \
  && rm -rf /var/cache/apk/*

ARG VERSION=1.0.0-SNAPSHOT

# Default the server port if not specified
ARG SERVER_PORT
ENV SERVER_PORT=${SERVER_PORT:-8080}
EXPOSE ${SERVER_PORT}

ARG install_dir=/opt/app
ARG username=github

# Create a user and directory to install and run the application
RUN adduser --disabled-password --home ${install_dir} --uid 1000 ${username}
USER ${username}
WORKDIR ${install_dir}

# Copy the self contained jar file to the container
COPY target/bookstore-security-${VERSION}.jar bookstore.jar

ENTRYPOINT ["/opt/java/openjdk/bin/java", "-jar", "bookstore.jar"]

LABEL org.opencontainers.image.authors="GitHub Solutions Engineering" \
  org.opencontainers.image.vendor="GitHub" \
  org.opencontainers.image.licenses="MIT" \
  org.opencontainers.image.title="GitHub Solutions Engineering Java Bookstore" \
  org.opencontainers.image.description="GitHub Demo Bookstore written in Java"
