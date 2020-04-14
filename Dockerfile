FROM adoptopenjdk/maven-openjdk11

# Copy SDK code into the container
RUN mkdir -p $HOME/java-algorand-sdk
COPY . $HOME/java-algorand-sdk
WORKDIR $HOME/java-algorand-sdk

# Run integration tests
CMD ["/bin/bash", "-c", "mvn test -Dskip.integration.tests=false -e"]
