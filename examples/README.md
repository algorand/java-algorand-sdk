
# Java AlgoSDK Example Project

## Quickstart

1. Find the algod network address from algod.net file within the data
   directory of your algod install. Replace 127.0.0.1:8080 in the mvn
   command with the address from the file.

2. Find the X-Algo-API-Token from algod.token file within the data
   directory of your algod install. Replace ***X-Algo-API-Token*** in the
   mvn command with the token from the file.

To run the example using maven:
```
cd examples
mvn clean install -Dexec.cleanupDaemonThreads=false exec:java -Dexec.mainClass="com.algorand.algosdk.example.Main" -Dexec.args="127.0.0.1:8080 ***X-Algo-API-Token***"
```
Output (for instance):
```
Total Algorand Supply: 10011480163524162
Online Algorand Supply: 9911265198066763
Suggested Fee: 9875
Current Round: 275938
Attempting an invalid transaction: overspending using randomly generated accounts.
Expecting overspend exception.
Signed transaction with txid: AWHALXA3D4BV5URKY332X6U5W7HIHQZVDSNBRZZDRA2EGIK3IB4A
Exception when calling algod#rawTransaction: TransactionPool.Remember: Insufficient funds - The total pending transactions from the address require 9975 Algos but the account only has 0 Algos
```

## Notes
This example depends transitively on Bouncy Castle through the SDK's direct dependency.
For best practice, it is worth making this an explicit dependency.
