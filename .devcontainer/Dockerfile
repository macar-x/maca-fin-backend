FROM mcr.microsoft.com/devcontainers/base:ubuntu-24.04

# Install Java 21 and Maven for Quarkus development
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends openjdk-21-jdk maven \
    && apt-get clean && rm -rf /var/lib/apt/lists/*
