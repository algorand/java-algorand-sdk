FROM adoptopenjdk/maven-openjdk11

RUN apt-get update && apt-get install -y make

# Copy SDK code into the container
RUN mkdir -p $HOME/java-algorand-sdk
COPY . $HOME/java-algorand-sdk
WORKDIR $HOME/java-algorand-sdk

# Run integration tests
CMD ["/bin/bash", "-c", "make unit && make integration && make smoke-test-examples"]
