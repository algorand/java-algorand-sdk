/**
 * Callback-based signers.
 * <p>
 * These let a custom key backend (HD wallet, hardware device, KMS, an external
 * post-quantum implementation, ...) plug into Algorand signing operations by
 * supplying a single low-level {@link com.algorand.algosdk.signer.RawSigner}
 * callback that signs exact bytes, instead of exposing a raw secret key.
 * <p>
 * {@link com.algorand.algosdk.signer.Ed25519AlgorandSigner} and the
 * post-quantum {@link com.algorand.algosdk.signer.Falcon1024AlgorandSigner}
 * are both {@link com.algorand.algosdk.transaction.TxnSigner}s (so they plug
 * straight into an {@link com.algorand.algosdk.transaction.AtomicTransactionComposer})
 * and delegate a logic signature in place via {@code signLogicsig}.
 * {@code Ed25519AlgorandSigner} additionally signs messages ("MX") and
 * program data; {@link com.algorand.algosdk.signer.Ed25519MultisigAlgorandSigner}
 * fills a multisig from several ed25519 callback signers.
 * <p>
 * For signing a single transaction, pair any of these with
 * {@link com.algorand.algosdk.transaction.TxnSigner#signTransaction}.
 */
package com.algorand.algosdk.signer;
