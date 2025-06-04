FROM adoptopenjdk/maven-openjdk11

RUN apt-get update && apt-get install -y make

# Copy SDK code into the container
RUN mkdir -p /app/java-algorand-sdk
COPY . /app/java-algorand-sdk
WORKDIR /app/java-algorand-sdk

# Run integration tests
CMD ["/bin/bash", "-c", "make unit && make integration"]
