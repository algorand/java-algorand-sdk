unit:
	mvn test -Dcucumber.filter.tags="@unit.offline or @unit.algod or @unit.indexer or @unit.indexer.rekey or @unit.transactions or @unit.responses or @unit.applications"

integration:
	mvn test -Dcucumber.filter.tags="@algod or @assets or @auction or @kmd or @send or @template or @indexer or @rekey or @applications"
docker-test:
	./run_integration_tests.sh
