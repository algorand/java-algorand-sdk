package com.algorand.algosdk.v2.client.model;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Contains ledger deltas for transaction groups in a round
 */
@JsonFormat(shape=JsonFormat.Shape.ARRAY)
public class TransactionGroupLedgerStateDeltaForRoundResponse extends LedgerStateDeltaForTransactionGroup {
}
