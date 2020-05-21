unit:
	mvn test -Dcucumber.filter.tags="@unit.offline or @unit.algod or @unit.indexer"

integration:
	mvn test -Dcucumber.filter.tags="@unit or @algod or @assets or @auction or @kmd or @unit or @send or @template or @indexer"

docker-test:
	./run_integration_tests.sh
