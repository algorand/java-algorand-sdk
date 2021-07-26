FROM algorand-generator as generator

FROM maven:3.8.1-openjdk-11 as builder

COPY . /repo
WORKDIR /repo

RUN mvn clean package -DskipTests

FROM generator as publisher

COPY --from=builder /repo /repo
