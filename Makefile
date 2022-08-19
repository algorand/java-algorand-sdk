UNIT_TAGS :=  "$(subst :, or ,$(shell awk '{print $2}' src/test/unit.tags | paste -s -d: -))"
INTEGRATION_TAGS := "$(subst :, or ,$(shell awk '{print $2}' src/test/integration.tags | paste -s -d: -))"

unit:
	mvn test -Dcucumber.filter.tags=$(UNIT_TAGS)

integration:
	mvn test -Dtest=com.algorand.algosdk.integration.RunCucumberIntegrationTest -Dcucumber.filter.tags=$(INTEGRATION_TAGS)

display-all-java-steps:
	find src/test/java/com/algorand/algosdk -name "*.java" | xargs grep "io.cucumber.java.en" 2>/dev/null | grep -v Binary | cut -d: -f1 | sort | uniq | xargs grep -E "@(Given|Then|When)"

harness:
	./test-harness.sh

docker-javasdk-build:
	# Build SDK testing environment
	docker build -t java-sdk-testing .

docker-javasdk-run:
	# Launch SDK testing
	docker run -it --network host java-sdk-testing:latest

docker-test: harness docker-javasdk-build docker-javasdk-run
