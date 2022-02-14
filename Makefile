unit:
	mvn test -Dcucumber.filter.tags="@unit.offline or @unit.algod or @unit.indexer or @unit.rekey or @unit.indexer.rekey or @unit.transactions or @unit.transactions.keyreg or @unit.responses or @unit.applications or @unit.dryrun or @unit.tealsign or @unit.responses.messagepack or @unit.responses.231 or @unit.responses.messagepack.231 or @unit.feetest or @unit.indexer.logs or @unit.abijson or @unit.atomic_transaction_composer or @unit.transactions.payment"

integration:
	mvn test -Dcucumber.filter.tags="@algod or @assets or @auction or @kmd or @send or @send.keyregtxn or @indexer or @rekey or @applications.verified or @applications or @compile or @dryrun or @indexer.applications or @indexer.231 or @abi or @c2c"

docker-test:
	./run_integration_tests.sh
