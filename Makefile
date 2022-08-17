UNITS = "@unit.offline or @unit.algod or @unit.indexer or @unit.rekey or @unit.indexer.rekey or @unit.transactions or @unit.transactions.keyreg or @unit.responses or @unit.applications or @unit.dryrun or @unit.tealsign or @unit.responses.messagepack or @unit.responses.231 or @unit.responses.messagepack.231 or @unit.feetest or @unit.indexer.logs or @unit.abijson or @unit.abijson.byname or @unit.atomic_transaction_composer or @unit.transactions.payment or @unit.responses.unlimited_assets or @unit.algod.ledger_refactoring or @unit.indexer.ledger_refactoring or @unit.dryrun.trace.application or @unit.sourcemap"
unit:
	mvn test -Dcucumber.filter.tags=$(UNITS)

INTEGRATIONS = "@algod or @assets or @auction or @kmd or @send or @send.keyregtxn or @indexer or @rekey_v1 or @applications.verified or @applications or @compile or @dryrun or @indexer.applications or @indexer.231 or @abi or @c2c or @compile.sourcemap"
integration:
	mvn test -Dtest=com.algorand.algosdk.integration.RunCucumberIntegrationTest -Dcucumber.filter.tags=$(INTEGRATIONS)

harness:
	./test-harness.sh

docker-javasdk-build:
	# Build SDK testing environment
	docker build -t java-sdk-testing .

docker-javasdk-run:
	# Launch SDK testing
	docker run -it --network host java-sdk-testing:latest

docker-test: harness docker-javasdk-build docker-javasdk-run
