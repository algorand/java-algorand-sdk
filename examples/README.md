
# Java AlgoSDK Example Project

## Quickstart
```
cd examples
mvn clean install exec:java -Dexec.mainClass="com.algorand.algosdk.example.Main"
```
Output (for instance):
```
Total Algorand Supply: 10011480163524162
Online Algorand Supply: 9911265198066763
Suggested Fee: 9875
Current Round: 275938
Signed transaction with txid: AWHALXA3D4BV5URKY332X6U5W7HIHQZVDSNBRZZDRA2EGIK3IB4A
Exception when calling algod#rawTransaction: TransactionPool.Remember: Insufficient funds - The total pending transactions from the address require 9975 Algos but the account only has 0 Algos
```

## Notes
This example depends transitively on Bouncy Castle through the SDK's direct dependency.
For best practice, it is worth making this an explicit dependency.
