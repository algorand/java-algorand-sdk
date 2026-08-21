/**
 * Callback-based signers.
 * <p>
 * These let a custom key backend (HD wallet, hardware device, KMS, an external
 * post-quantum implementation, ...) plug into Algorand signing operations by
 * supplying a single low-level {@link com.algorand.algosdk.signer.RawSigner}
 * callback that signs exact bytes, instead of exposing a raw secret key.
 * <p>
 * {@link com.algorand.algosdk.signer.Falcon1024AlgorandSigner} is a
 * {@link com.algorand.algosdk.transaction.TxnSigner} (so it plugs straight
 * into an {@link com.algorand.algosdk.transaction.AtomicTransactionComposer})
 * and delegates a logic signature in place via {@code signLogicsig}.
 * <p>
 * For signing a single transaction, pair it with
 * {@link com.algorand.algosdk.transaction.TxnSigner#signTransaction}.
 */
package com.algorand.algosdk.signer;
