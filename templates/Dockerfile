FROM maven:3.8.1-openjdk-11 as builder

COPY . /repo
WORKDIR /repo

RUN mvn clean package -DskipTests

FROM algorand-generator

ENV NO_BUILD="true"
COPY --from builder /repo /repo
