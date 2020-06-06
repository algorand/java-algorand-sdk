unit:
	mvn test -Dcucumber.filter.tags="@unit.offline or @unit.algod or @unit.indexer or @unit.rekey or @unit.transactions"

integration:
	mvn test -Dcucumber.filter.tags="@algod or @assets or @auction or @kmd or @send or @template or @indexer or @rekey"
docker-test:
	./run_integration_tests.sh
