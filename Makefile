unit:
	mvn test -Dcucumber.filter.tags="@unit.offline or @unit.algod or @unit.indexer or @unit.rekey or @unit.indexer.rekey or @unit.transactions or @unit.responses or @unit.applications or @unit.dryrun or @unit.tealsign or @unit.responses.messagepack"

integration:
	mvn test -Dcucumber.filter.tags="@algod or @assets or @auction or @kmd or @send or @template or @indexer or @rekey or @applications.verified or @applications or @compile or @dryrun or @indexer.applications or @applications.evaldelta"
docker-test:
	./run_integration_tests.sh
