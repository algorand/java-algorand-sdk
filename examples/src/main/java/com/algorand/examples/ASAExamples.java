package com.algorand.examples;

import java.util.List;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.Utils;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.Asset;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;

public class ASAExamples {

    public static void main(String[] args) throws Exception {
        AlgodClient algodClient = ExampleUtils.getAlgodClient();
        List<Account> accts = ExampleUtils.getSandboxAccounts();
        Account acct1 = accts.get(0);
        Account acct2 = accts.get(1);

        Long asaId = createAsset(algodClient, acct1);

        // example: ASSET_INFO
        // Retrieve the asset info of the newly created asset
        Response<Asset> assetResp = algodClient.GetAssetByID(asaId).execute();
        Asset assetInfo = assetResp.body();
        System.out.printf("Asset Name: %s", assetInfo.params.name);
        // example: ASSET_INFO

        optInToAsset(algodClient, acct2, asaId);
        xferAsset(algodClient, acct1, acct2, asaId);
        freezeAsset(algodClient, acct1, acct2, asaId);
        clawbackAsset(algodClient, acct1, acct2, asaId);
        deleteAsset(algodClient, acct1, asaId);

    }

    public static Long createAsset(AlgodClient algodClient, Account acct) throws Exception {
        // example: ASSET_CREATE
        // Account 1 creates an asset called `rug` with a total supply
        // of 1000 units and sets itself to the freeze/clawback/manager/reserve roles
        Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();
        TransactionParametersResponse sp = rsp.body();

        // Under the covers, this is an AssetConfig with asset id set to 0
        Transaction createTxn = Transaction.AssetCreateTransactionBuilder().suggestedParams(sp)
                .sender(acct.getAddress())
                .assetTotal(1000)
                .assetDecimals(0)
                .defaultFrozen(false)
                .assetUnitName("rug")
                .assetName("Really Useful Gift")
                .url("https://path/to/my/asset/details")
                .manager(acct.getAddress())
                .reserve(acct.getAddress())
                .freeze(acct.getAddress())
                .clawback(acct.getAddress())
                .build();

        SignedTransaction signedCreateTxn = acct.signTransaction(createTxn);
        Response<PostTransactionsResponse> submitResult = algodClient.RawTransaction()
                .rawtxn(Encoder.encodeToMsgPack(signedCreateTxn)).execute();
        String txId = submitResult.body().txId;
        PendingTransactionResponse result = Utils.waitForConfirmation(algodClient, txId, 4);

        // Grab the asset id for the asset we just created
        Long asaId = result.assetIndex;
        System.out.printf("Created asset with id: %d\n", asaId);

        // example: ASSET_CREATE
        return asaId;
    }

    public static void optInToAsset(AlgodClient algodClient, Account acct, Long asaId) throws Exception {
        // example: ASSET_OPTIN
        Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();
        TransactionParametersResponse sp = rsp.body();
        // Under the covers, this is an AssetTransfer from me to me for amount 0
        // with asset id set to the asset we wish to start accepting
        Transaction optInTxn = Transaction.AssetAcceptTransactionBuilder().suggestedParams(sp)
                .sender(acct.getAddress())
                .assetIndex(asaId)
                .build();

        // example: ASSET_OPTIN
        SignedTransaction signedOptIn = acct.signTransaction(optInTxn);
        ExampleUtils.sendPrint(algodClient, signedOptIn, "opt in asset");
    }

    public static void xferAsset(AlgodClient algodClient, Account sender, Account receiver, Long asaId)
            throws Exception {
        // example: ASSET_XFER
        Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();
        TransactionParametersResponse sp = rsp.body();
        // Under the covers, this is an AssetTransfer from me to me for amount 0
        // with asset id set to the asset we wish to start accepting
        Transaction xferTxn = Transaction.AssetTransferTransactionBuilder().suggestedParams(sp)
                .sender(sender.getAddress())
                .assetReceiver(receiver.getAddress())
                .assetIndex(asaId)
                .assetAmount(1)
                .build();

        // example: ASSET_XFER
        SignedTransaction signedXfer = sender.signTransaction(xferTxn);
        ExampleUtils.sendPrint(algodClient, signedXfer, "xfer asset");
    }

    public static void freezeAsset(AlgodClient algodClient, Account sender, Account receiver, Long asaId)
            throws Exception {
        // example: ASSET_FREEZE
        Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();
        TransactionParametersResponse sp = rsp.body();
        // Set the freeze state on the account, only the account that is set to the
        // freeze role
        // on the asset may issue this transaction
        Transaction freezeTxn = Transaction.AssetFreezeTransactionBuilder().suggestedParams(sp)
                .sender(sender.getAddress())
                .freezeTarget(receiver.getAddress())
                .freezeState(true)
                .assetIndex(asaId)
                .build();

        // example: ASSET_FREEZE
        SignedTransaction signedFreeze = sender.signTransaction(freezeTxn);
        ExampleUtils.sendPrint(algodClient, signedFreeze, "freeze asset");
    }

    public static void clawbackAsset(AlgodClient algodClient, Account sender, Account receiver, Long asaId)
            throws Exception {
        // example: ASSET_CLAWBACK
        Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();
        TransactionParametersResponse sp = rsp.body();
        // revoke an asset from an account, only the account that is set to the clawback
        // role
        // on the asset may issue this transaction
        Transaction clawbackTxn = Transaction.AssetClawbackTransactionBuilder().suggestedParams(sp)
                .sender(sender.getAddress())
                .assetClawbackFrom(receiver.getAddress())
                .assetReceiver(sender.getAddress())
                .assetIndex(asaId)
                .assetAmount(1)
                .build();

        // example: ASSET_CLAWBACK
        SignedTransaction signedClawback = sender.signTransaction(clawbackTxn);
        ExampleUtils.sendPrint(algodClient, signedClawback, "clawback asset");
    }

    public static void deleteAsset(AlgodClient algodClient, Account acct, Long asaId) throws Exception {
        // example: ASSET_DELETE
        Response<TransactionParametersResponse> rsp = algodClient.TransactionParams().execute();
        TransactionParametersResponse sp = rsp.body();
        // Under the covers, an AssetDestroyTransaction is an AssetConfig with all of
        // its
        // configurable fields set to empty
        // All units of the asset _must_ be owned by the creator account and this
        // transaction _must_
        // be issued by the account set to the manager role on the asset
        Transaction destroyTxn = Transaction.AssetDestroyTransactionBuilder().suggestedParams(sp)
                .sender(acct.getAddress())
                .assetIndex(asaId)
                .build();

        // example: ASSET_DELETE
        SignedTransaction signedDestroy = acct.signTransaction(destroyTxn);
        ExampleUtils.sendPrint(algodClient, signedDestroy, "clawback asset");
    }
}
