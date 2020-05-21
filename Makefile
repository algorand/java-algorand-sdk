unit:
	mvn test -Dcucumber.filter.tags="@unit.offline or @unit.algod or @unit.indexer"

integration:
	mvn test -Dskip.integration.tests=false -Dcucumber.filter.tags="@algod or @assets or @auction or @kmd or @send or @template or @indexer"

docker-test:
	./run_integration_tests.sh
