package com.algorand.algosdk.kmd;

import com.algorand.algosdk.kmd.model.*;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Client is the primary interface for accessing the KMD REST API. Use ClientFactory to synthesize a Client.
 * Under the hood, Client is a retrofit2 client, generated in part by swagger-codegen.
 * @see com.algorand.algosdk.kmd.ClientFactory#create(String, String)
 */
public interface Client {
    /**
     * Create a wallet
     * Create a new wallet (collection of keys) with the given parameters.
     * @param body  (required)
     * @return Call&lt;APIV1POSTWalletResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @POST("v1/wallet")
    Call<APIV1POSTWalletResponse> createWallet(
            @retrofit2.http.Body CreateWalletRequest body
    );

    /**
     * Delete a key
     * Deletes the key with the passed public key from the wallet.
     * @param body  (required)
     * @return Call&lt;APIV1DELETEKeyResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @DELETE("v1/key")
    Call<APIV1DELETEKeyResponse> deleteKey(
            @retrofit2.http.Body DeleteKeyRequest body
    );

    /**
     * Delete a multisig
     * Deletes multisig preimage information for the passed address from the wallet.
     * @param body  (required)
     * @return Call&lt;APIV1DELETEMultisigResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @DELETE("v1/multisig")
    Call<APIV1DELETEMultisigResponse> deleteMultisig(
            @retrofit2.http.Body DeleteMultisigRequest body
    );

    /**
     * Export a key
     * Export the secret key associated with the passed public key.
     * @param body  (required)
     * @return Call&lt;APIV1POSTKeyExportResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @POST("v1/key/export")
    Call<APIV1POSTKeyExportResponse> exportKey(
            @retrofit2.http.Body ExportKeyRequest body
    );

    /**
     * Export the master derivation key from a wallet
     * Export the master derivation key from the wallet. This key is a master \&quot;backup\&quot; key for the underlying wallet. With it, you can regenerate all of the wallets that have been generated with this wallet&#x27;s &#x60;POST /v1/key&#x60; endpoint. This key will not allow you to recover keys imported from other wallets, however.
     * @param body  (required)
     * @return Call&lt;APIV1POSTMasterKeyExportResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @POST("v1/master-key/export")
    Call<APIV1POSTMasterKeyExportResponse> exportMasterKey(
            @retrofit2.http.Body ExportMasterKeyRequest body
    );

    /**
     * Export multisig address metadata
     * Given a multisig address whose preimage this wallet stores, returns the information used to generate the address, including public keys, threshold, and multisig version.
     * @param body  (required)
     * @return Call&lt;APIV1POSTMultisigExportResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @POST("v1/multisig/export")
    Call<APIV1POSTMultisigExportResponse> exportMultisig(
            @retrofit2.http.Body ExportMultisigRequest body
    );

    /**
     * Generate a key
     * Generates the next key in the deterministic key sequence (as determined by the master derivation key) and adds it to the wallet, returning the public key.
     * @param body  (required)
     * @return Call&lt;APIV1POSTKeyResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @POST("v1/key")
    Call<APIV1POSTKeyResponse> generateKey(
            @retrofit2.http.Body GenerateKeyRequest body
    );

    /**
     *
     * Retrieves the current version
     * @return Call&lt;VersionsResponse&gt;
     */
    @GET("versions")
    Call<VersionsResponse> getVersion();


    /**
     * Get wallet info
     * Returns information about the wallet associated with the passed wallet handle token. Additionally returns expiration information about the token itself.
     * @param body  (required)
     * @return Call&lt;APIV1POSTWalletInfoResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @POST("v1/wallet/info")
    Call<APIV1POSTWalletInfoResponse> getWalletInfo(
            @retrofit2.http.Body WalletInfoRequest body
    );

    /**
     * Import a key
     * Import an externally generated key into the wallet. Note that if you wish to back up the imported key, you must do so by backing up the entire wallet database, because imported keys were not derived from the wallet&#x27;s master derivation key.
     * @param body  (required)
     * @return Call&lt;APIV1POSTKeyImportResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @POST("v1/key/import")
    Call<APIV1POSTKeyImportResponse> importKey(
            @retrofit2.http.Body ImportKeyRequest body
    );

    /**
     * Import a multisig account
     * Generates a multisig account from the passed public keys array and multisig metadata, and stores all of this in the wallet.
     * @param body  (required)
     * @return Call&lt;APIV1POSTMultisigImportResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @POST("v1/multisig/import")
    Call<APIV1POSTMultisigImportResponse> importMultisig(
            @retrofit2.http.Body ImportMultisigRequest body
    );

    /**
     * Initialize a wallet handle token
     * Unlock the wallet and return a wallet handle token that can be used for subsequent operations. These tokens expire periodically and must be renewed. You can &#x60;POST&#x60; the token to &#x60;/v1/wallet/info&#x60; to see how much time remains until expiration, and renew it with &#x60;/v1/wallet/renew&#x60;. When you&#x27;re done, you can invalidate the token with &#x60;/v1/wallet/release&#x60;.
     * @param body  (required)
     * @return Call&lt;APIV1POSTWalletInitResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @POST("v1/wallet/init")
    Call<APIV1POSTWalletInitResponse> initWalletHandleToken(
            @retrofit2.http.Body InitWalletHandleTokenRequest body
    );

    /**
     * List keys in wallet
     * Lists all of the public keys in this wallet. All of them have a stored private key.
     * @param body  (required)
     * @return Call&lt;APIV1POSTKeyListResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @POST("v1/key/list")
    Call<APIV1POSTKeyListResponse> listKeysInWallet(
            @retrofit2.http.Body ListKeysRequest body
    );

    /**
     * List multisig accounts
     * Lists all of the multisig accounts whose preimages this wallet stores
     * @param body  (required)
     * @return Call&lt;APIV1POSTMultisigListResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @POST("v1/multisig/list")
    Call<APIV1POSTMultisigListResponse> listMultisg(
            @retrofit2.http.Body ListMultisigRequest body
    );

    /**
     * List wallets
     * Lists all of the wallets that kmd is aware of.
     * @return Call&lt;APIV1GETWalletsResponse&gt;
     */
    @GET("v1/wallets")
    Call<APIV1GETWalletsResponse> listWallets();


    /**
     * Release a wallet handle token
     * Invalidate the passed wallet handle token, making it invalid for use in subsequent requests.
     * @param body  (required)
     * @return Call&lt;APIV1POSTWalletReleaseResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @POST("v1/wallet/release")
    Call<APIV1POSTWalletReleaseResponse> releaseWalletHandleToken(
            @retrofit2.http.Body ReleaseWalletHandleTokenRequest body
    );

    /**
     * Rename a wallet
     * Rename the underlying wallet to something else
     * @param body  (required)
     * @return Call&lt;APIV1POSTWalletRenameResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @POST("v1/wallet/rename")
    Call<APIV1POSTWalletRenameResponse> renameWallet(
            @retrofit2.http.Body RenameWalletRequest body
    );

    /**
     * Renew a wallet handle token
     * Renew a wallet handle token, increasing its expiration duration to its initial value
     * @param body  (required)
     * @return Call&lt;APIV1POSTWalletRenewResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @POST("v1/wallet/renew")
    Call<APIV1POSTWalletRenewResponse> renewWalletHandleToken(
            @retrofit2.http.Body RenewWalletHandleTokenRequest body
    );

    /**
     * Sign a multisig transaction
     * Start a multisig signature, or add a signature to a partially completed multisig signature object.
     * @param body  (required)
     * @return Call&lt;APIV1POSTMultisigTransactionSignResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @POST("v1/multisig/sign")
    Call<APIV1POSTMultisigTransactionSignResponse> signMultisigTransaction(
            @retrofit2.http.Body SignMultisigRequest body
    );

    /**
     * Sign a transaction
     * Signs the passed transaction with a key from the wallet, determined by the sender encoded in the transaction.
     * @param body  (required)
     * @return Call&lt;APIV1POSTTransactionSignResponse&gt;
     */
    @Headers({
            "Content-TxType:application/json"
    })
    @POST("v1/transaction/sign")
    Call<APIV1POSTTransactionSignResponse> signTransaction(
            @retrofit2.http.Body SignTransactionRequest body
    );

    /**
     * Gets the current swagger spec.
     * Returns the entire swagger spec in json.
     * @return Call&lt;String&gt;
     */
    @GET("swagger.json")
    Call<String> swaggerHandler();


}