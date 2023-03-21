package com.algorand.algosdk.integration;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.auction.Bid;
import com.algorand.algosdk.auction.SignedBid;
import com.algorand.algosdk.builder.transaction.TransactionBuilder;
import com.algorand.algosdk.crypto.*;
import com.algorand.algosdk.kmd.client.KmdClient;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.kmd.client.model.*;
import com.algorand.algosdk.mnemonic.Mnemonic;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.AlgoConverter;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.ResourceUtils;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.IndexerClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.*;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.algorand.algosdk.util.ResourceUtils.loadResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class Stepdefs {
    public static String token = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    public static Integer algodPort = 60000;
    public static Integer kmdPort = 60001;

    TransactionParametersResponse params;
    SignedTransaction stx;
    SignedTransaction[] stxs;
    byte[] stxBytes;
    Transaction txn;
    TransactionBuilder txnBuilder;
    String txid;
    Account account;
    Address pk;
    String address;
    byte[] sk;
    BigInteger fee;
    BigInteger fv;
    BigInteger lv;
    Digest gh;
    Address to;
    Address close;
    BigInteger amt;
    String gen;
    byte[] note;
    MultisigAddress msig;
    String walletName;
    String walletPswd;
    String walletID;
    KmdApi kcl;
    KmdClient kmdClient;
    com.algorand.algosdk.v2.client.common.AlgodClient aclv2;
    IndexerClient v2IndexerClient;
    String handle;
    List<String> versions;
    List<byte[]> pks;
    List<String> addresses;
    BigInteger lastRound;
    boolean err;
    BigInteger microalgos;
    String mnemonic;
    byte[] mdk;
    String oldAddr;
    Bid bid;
    SignedBid oldBid;
    SignedBid sbid;
    ParticipationPublicKey votepk;
    VRFPublicKey vrfpk;
    String sprfpk;
    BigInteger votefst;
    BigInteger votelst;
    BigInteger votekd;

    /* Assets */
    String creator = "";
    BigInteger assetID = BigInteger.valueOf(1);
    String assetName = "testcoin";
    String assetUnitName = "coins";
    com.algorand.algosdk.transaction.AssetParams expectedParams = null;
    Asset queriedParams = new Asset();

    /* Compile / Dryrun */
    Response<CompileResponse> compileResponse;
    Response<DryrunResponse> dryrunResponse;

    protected Address getAddress(int i) {
        if (addresses == null) {
            throw new RuntimeException("Addresses not initialized, must use given 'wallet information'");
        }
        if (addresses.size() < i || addresses.size() == 0) {
            throw new RuntimeException("Not enough addresses, you may need to update the network template.");
        }
        try {
            return new Address(addresses.get(i));
        } catch (Exception e) {
            // Lets not bother recovering from this one 🔥
            throw new RuntimeException(e);
        }
    }

    /**
     * Convenience method to prepare the parameter object.
     */
    protected void getParams() {
        try {
            params = aclv2.TransactionParams().execute().body();
            lastRound = BigInteger.valueOf(params.lastRound);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convenience method to lookup a secret key and sign a transaction with the key.
     */
    public SignedTransaction signWithAddress(Transaction tx, Address addr) throws com.algorand.algosdk.kmd.client.ApiException, NoSuchAlgorithmException {
        ExportKeyRequest req = new ExportKeyRequest();
        req.setAddress(addr.toString());
        req.setWalletHandleToken(handle);
        req.setWalletPassword(walletPswd);
        byte[] secretKey = kcl.exportKey(req).getPrivateKey();
        Account acct = new Account(Arrays.copyOfRange(secretKey, 0, 32));
        return acct.signTransaction(tx);
    }

    @When("I create a wallet")
    public void createWallet() throws com.algorand.algosdk.kmd.client.ApiException {
        walletName = "Walletjava";
        walletPswd = "";
        CreateWalletRequest req = new CreateWalletRequest();
        req.setWalletName(walletName);
        req.setWalletPassword(walletPswd);
        req.setWalletDriverName("sqlite");
        walletID = kcl.createWallet(req).getWallet().getId();
    }

    @Then("the wallet should exist")
    public void walletExist() throws com.algorand.algosdk.kmd.client.ApiException{
        boolean exists = false;
        APIV1GETWalletsResponse resp = kcl.listWallets();
        for (APIV1Wallet w : resp.getWallets()){
            if (w.getName().equals(walletName)) {
                exists = true;
                break;
            }
        }
        assertThat(exists).isTrue();
    }

    @When("I get the wallet handle")
    public void getHandle() throws com.algorand.algosdk.kmd.client.ApiException{
        InitWalletHandleTokenRequest req = new InitWalletHandleTokenRequest();
        req.setWalletId(walletID);
        req.setWalletPassword(walletPswd);
        handle = kcl.initWalletHandleToken(req).getWalletHandleToken();

    }

    @Then("I can get the master derivation key")
    public void getMdk() throws com.algorand.algosdk.kmd.client.ApiException{
        ExportMasterKeyRequest req = new ExportMasterKeyRequest();
        req.setWalletHandleToken(handle);
        req.setWalletPassword(walletPswd);
        byte[] mdk = kcl.exportMasterKey(req).getMasterDerivationKey();
        assertThat(mdk.length).isGreaterThan(0);
    }

    @When("I rename the wallet")
    public void renameWallet() throws com.algorand.algosdk.kmd.client.ApiException{
        RenameWalletRequest req = new RenameWalletRequest();
        walletName = "Walletjava_new";
        req.setWalletId(walletID);
        req.setWalletPassword(walletPswd);
        req.setWalletName(walletName);
        kcl.renameWallet(req);
    }

    @Then("I can still get the wallet information with the same handle")
    public void getWalletInfo() throws com.algorand.algosdk.kmd.client.ApiException{
        WalletInfoRequest req = new WalletInfoRequest();
        req.setWalletHandleToken(handle);
        String name = kcl.getWalletInfo(req).getWalletHandle().getWallet().getName();
        assertThat(name).isEqualTo(walletName);
    }

    @When("I renew the wallet handle")
    public void renewHandle() throws com.algorand.algosdk.kmd.client.ApiException{
        RenewWalletHandleTokenRequest req = new RenewWalletHandleTokenRequest();
        req.setWalletHandleToken(handle);
        kcl.renewWalletHandleToken(req);
    }

    @When("I release the wallet handle")
    public void releaseHandle() throws com.algorand.algosdk.kmd.client.ApiException{
        ReleaseWalletHandleTokenRequest req = new ReleaseWalletHandleTokenRequest();
        req.setWalletHandleToken(handle);
        kcl.releaseWalletHandleToken(req);
    }

    @Then("the wallet handle should not work")
    public void tryHandle() {
        RenewWalletHandleTokenRequest req = new RenewWalletHandleTokenRequest();
        req.setWalletHandleToken(handle);
        err = false;
        try{
            kcl.renewWalletHandleToken(req);
        } catch(Exception e){
            err = true;
        }
        assertThat(err).isTrue();
    }

    @Given("payment transaction parameters {int} {int} {int} {string} {string} {string} {int} {string} {string}")
    public void transactionParameters(int fee, int fv, int lv, String gh, String to, String close, int amt, String gen, String note)  throws GeneralSecurityException {
        this.fee = BigInteger.valueOf(fee);
        this.fv = BigInteger.valueOf(fv);
        this.lv = BigInteger.valueOf(lv);
        this.gh = new Digest(Encoder.decodeFromBase64(gh));
        this.to = new Address(to);
        if (!close.equals("none")){
            this.close = new Address(close);
        }
        this.amt = BigInteger.valueOf(amt);
        if (!gen.equals("none")) {
            this.gen = gen;
        }
        if (!note.equals("none")) {
            this.note = Encoder.decodeFromBase64(note);
        }
    }

    @Given("mnemonic for private key {string}")
    public void mn_for_sk(String mn) throws GeneralSecurityException{
        account = new Account(mn);
        pk = account.getAddress();
    }

    @Given("default V2 key registration transaction {string}")
    public void default_v2_key_registration_transaction(String type) {
        getParams();
        votepk=new ParticipationPublicKey(Encoder.decodeFromBase64("9mr13Ri8rFepxN3ghIUrZNui6LqqM5hEzB45Rri5lkU="));
        vrfpk = new VRFPublicKey(Encoder.decodeFromBase64("dx717L3uOIIb/jr9OIyls1l5Ei00NFgRa380w7TnPr4="));
        votefst = BigInteger.valueOf(0);
        votelst = BigInteger.valueOf(2000);
        votekd = BigInteger.valueOf(10);
        pk = getAddress(0);

        if(type.equals("online")){
            sprfpk = "mYR0GVEObMTSNdsKM6RwYywHYPqVDqg3E4JFzxZOreH9NU8B+tKzUanyY8AQ144hETgSMX7fXWwjBdHz6AWk9w==";
            txn = Transaction.KeyRegistrationTransactionBuilder()
                    .sender(pk)
                    .participationPublicKey(votepk)
                    .selectionPublicKey(vrfpk)
                    .voteFirst(votefst)
                    .voteLast(votelst)
                    .voteKeyDilution(votekd)
                    .stateProofKeyBase64(sprfpk)
                    .suggestedParams(this.params)
                    .build();
        }else if(type.equals("offline")){
            txn = Transaction.KeyRegistrationTransactionBuilder()
                    .sender(pk)
                    .suggestedParams(this.params)
                    .build();
        }else if(type.equals("nonparticipation")){
            txn = Transaction.KeyRegistrationTransactionBuilder()
                    .sender(pk)
                    .suggestedParams(this.params)
                    .nonparticipation(true)
                    .build();
        }
    }

    @Given("multisig addresses {string}")
    public void msig_addresses(String addresses) throws NoSuchAlgorithmException {
        String[] addrs = addresses.split(" ");
        Ed25519PublicKey[] addrlist = new Ed25519PublicKey[addrs.length];
        for(int x = 0; x < addrs.length; x++){
            addrlist[x] = new Ed25519PublicKey((new Address(addrs[x])).getBytes());
        }
        msig = new MultisigAddress(1, 2, Arrays.asList(addrlist));
        pk = new Address(msig.toString());
    }

    @When("I create the multisig payment transaction")
    public void createMsigTxn() throws NoSuchAlgorithmException{
        txn = Transaction.PaymentTransactionBuilder()
                .sender(msig.toString())
                .fee(fee)
                .firstValid(fv)
                .lastValid(lv)
                .note(note)
                .genesisID(gen)
                .genesisHash(gh)
                .amount(amt)
                .receiver(to)
                .closeRemainderTo(close)
                .build();
    }

    @When("I sign the multisig transaction with the private key")
    public void signMsigTxn() throws NoSuchAlgorithmException{
        stx = account.signMultisigTransaction(msig, txn);
    }

    @When("I sign the transaction with the private key")
    public void signTxn() throws NoSuchAlgorithmException{
        stx = account.signTransaction(txn);
    }

    @Then("the signed transaction should equal the golden {string}")
    public void equalGolden(String golden) throws JsonProcessingException{
        byte[] signedTxBytes = Encoder.encodeToMsgPack(stx);
        assertThat(Encoder.encodeToBase64(signedTxBytes)).isEqualTo(golden);

    }

    @Then("the multisig transaction should equal the golden {string}")
    public void equalMsigGolden(String golden) throws JsonProcessingException{
        byte[] signedTxBytes = Encoder.encodeToMsgPack(stx);
        assertThat(Encoder.encodeToBase64(signedTxBytes)).isEqualTo(golden);
    }


    @Then("the multisig address should equal the golden {string}")
    public void equalMsigAddrGolden(String golden){
        assertThat(msig.toString()).isEqualTo(golden);
    }

    @When("I get versions with algod")
    public void aclV() throws Exception {
        versions = aclv2.GetVersion().execute().body().versions;
    }

    @Then("v1 should be in the versions")
    public void v1InVersions(){
        assertThat(versions).contains("v1");
    }

    @Then("v2 should be in the versions")
    public void v2InVersions(){
        assertThat(versions).contains("v2");
    }

    @When("I get versions with kmd")
    public void kclV() throws com.algorand.algosdk.kmd.client.ApiException{
        versions = kcl.getVersion().getVersions();
    }

    @When("I import the multisig")
    public void importMsig() throws com.algorand.algosdk.kmd.client.ApiException{
        ImportMultisigRequest req = new ImportMultisigRequest();
        req.setMultisigVersion(msig.version);
        req.setThreshold(msig.threshold);
        req.setWalletHandleToken(handle);
        req.setPks(msig.publicKeys);
        kcl.importMultisig(req);
    }

    @Then("the multisig should be in the wallet")
    public void msigInWallet() throws com.algorand.algosdk.kmd.client.ApiException{
        ListMultisigRequest req = new ListMultisigRequest();
        req.setWalletHandleToken(handle);
        List<String> msigs = kcl.listMultisig(req).getAddresses();
        boolean exists = false;
        for (String m : msigs){
            if (m.equals(msig.toString())){
                exists = true;
            }
        }
        assertThat(exists).isTrue();
    }

    @When("I export the multisig")
    public void expMsig() throws com.algorand.algosdk.kmd.client.ApiException{
        ExportMultisigRequest req = new ExportMultisigRequest();
        req.setAddress(msig.toString());
        req.setWalletHandleToken(handle);
        pks = kcl.exportMultisig(req).getPks();

    }

    @Then("the multisig should equal the exported multisig")
    public void msigEq(){
        boolean eq = true;
        for (int x = 0; x < msig.publicKeys.size(); x++){
            if (!Encoder.encodeToBase64(msig.publicKeys.get(x).getBytes()).equals(Encoder.encodeToBase64(pks.get(x)))){
                eq = false;
            }
        }
        assertThat(eq).isTrue();
    }
    @When("I delete the multisig")
    public void deleteMsig() throws com.algorand.algosdk.kmd.client.ApiException{
        DeleteMultisigRequest req = new DeleteMultisigRequest();
        req.setAddress(msig.toString());
        req.setWalletHandleToken(handle);
        req.setWalletPassword(walletPswd);
        kcl.deleteMultisig(req);
    }

    @Then("the multisig should not be in the wallet")
    public void msigNotInWallet()throws com.algorand.algosdk.kmd.client.ApiException{
        ListMultisigRequest req = new ListMultisigRequest();
        req.setWalletHandleToken(handle);
        List<String> msigs = kcl.listMultisig(req).getAddresses();
        boolean exists = false;
        if (msigs != null) {
            for (String m : msigs){
                if (m.equals(msig.toString())){
                    exists = true;
                }
            }
        }
        assertThat(exists).isFalse();
    }

    @When("I generate a key using kmd")
    public void genKeyKmd() throws com.algorand.algosdk.kmd.client.ApiException, NoSuchAlgorithmException {
        GenerateKeyRequest req = new GenerateKeyRequest();
        req.setDisplayMnemonic(false);
        req.setWalletHandleToken(handle);
        pk = new Address(kcl.generateKey(req).getAddress());
    }

    @When("I generate a key using kmd for rekeying and fund it")
    public void genKeyKmdRekey() throws com.algorand.algosdk.kmd.client.ApiException, NoSuchAlgorithmException {
        GenerateKeyRequest req = new GenerateKeyRequest();
        req.setDisplayMnemonic(false);
        req.setWalletHandleToken(handle);
        rekey = new Address(kcl.generateKey(req).getAddress());

        // Fund rekey address
        try {
            getParams();
            Address sender = getAddress(0);
            Transaction tx =
                    Transaction.PaymentTransactionBuilder()
                            .sender(sender)
                            .suggestedParams(params)
                            .amount(100_000_000)
                            .receiver(rekey)
                            .build();
            SignedTransaction st = signWithAddress(tx, sender);
            aclv2.RawTransaction().rawtxn(Encoder.encodeToMsgPack(st)).execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Address rekey;

    @Then("the key should be in the wallet")
    public void keyInWallet() throws com.algorand.algosdk.kmd.client.ApiException {
        ListKeysRequest req = new ListKeysRequest();
        req.setWalletHandleToken(handle);
        List<String> keys = kcl.listKeysInWallet(req).getAddresses();
        boolean exists = false;
        for (String k : keys) {
            if (k.equals(pk.toString())) {
                exists = true;
            }
        }
        assertThat(exists).isTrue();
    }

    @When("I delete the key")
    public void deleteKey() throws com.algorand.algosdk.kmd.client.ApiException{
        DeleteKeyRequest req = new DeleteKeyRequest();
        req.setAddress(pk.toString());
        req.setWalletHandleToken(handle);
        req.setWalletPassword(walletPswd);
        kcl.deleteKey(req);
    }

    @Then("the key should not be in the wallet")
    public void keyNotInWallet() throws com.algorand.algosdk.kmd.client.ApiException{
        ListKeysRequest req = new ListKeysRequest();
        req.setWalletHandleToken(handle);
        List<String> keys = kcl.listKeysInWallet(req).getAddresses();
        boolean exists = false;
        for (String k : keys){
            if (k.equals(pk.toString())){
                exists = true;
            }
        }
        assertThat(exists).isFalse();
    }

    @When("I generate a key")
    public void genKey()throws GeneralSecurityException{
        account = new Account();
        pk = account.getAddress();
        address = pk.toString();
        sk = Mnemonic.toKey(account.toMnemonic());
    }

    @When("I import the key")
    public void importKey() throws com.algorand.algosdk.kmd.client.ApiException{
        ImportKeyRequest req = new ImportKeyRequest();
        req.setWalletHandleToken(handle);
        req.setPrivateKey(sk);
        kcl.importKey(req);
    }

    @When("I get the private key")
    public void getSk() throws com.algorand.algosdk.kmd.client.ApiException, GeneralSecurityException{
        ExportKeyRequest req = new ExportKeyRequest();
        req.setAddress(pk.toString());
        req.setWalletHandleToken(handle);
        req.setWalletPassword(walletPswd);
        sk = kcl.exportKey(req).getPrivateKey();
        account = new Account(Arrays.copyOfRange(sk, 0, 32));
    }

    @Then("the private key should be equal to the exported private key")
    public void expSkEq() throws com.algorand.algosdk.kmd.client.ApiException {
        ExportKeyRequest req = new ExportKeyRequest();
        req.setAddress(pk.toString());
        req.setWalletHandleToken(handle);
        req.setWalletPassword(walletPswd);
        byte[] exported = Arrays.copyOfRange(kcl.exportKey(req).getPrivateKey(), 0, 32);
        assertThat(Encoder.encodeToBase64(sk)).isEqualTo(Encoder.encodeToBase64(exported));
        DeleteKeyRequest deleteReq = new DeleteKeyRequest();
        deleteReq.setAddress(pk.toString());
        deleteReq.setWalletHandleToken(handle);
        deleteReq.setWalletPassword(walletPswd);
        kcl.deleteKey(deleteReq);
    }

    @Given("a kmd client")
    public void kClient() {
        kmdClient = new KmdClient();
        kmdClient.setConnectTimeout(30000);
        kmdClient.setReadTimeout(30000);
        kmdClient.setWriteTimeout(30000);
        kmdClient.setApiKey(token);
        kmdClient.setBasePath("http://localhost:" + kmdPort);
        kcl = new KmdApi(kmdClient);
    }

    @Given("an algod v2 client")
    public void aClientv2() {
        aclv2 = new com.algorand.algosdk.v2.client.common.AlgodClient(
            "http://localhost", algodPort, token
        );
    }

    @Given("an algod v2 client connected to {string} port {int} with token {string}")
    public void an_algod_v2_client_connected_to_port_with_token(String host, Integer port, String token) {
        aclv2 = new AlgodClient(host, port, token);
    }

    @Given("an indexer v2 client")
    public void indexer_v2_client() {
        v2IndexerClient = new IndexerClient("localhost", 59999);
    }

    @Given("wallet information")
    public void walletInfo() throws com.algorand.algosdk.kmd.client.ApiException {
        walletName = "unencrypted-default-wallet";
        walletPswd = "";
        List<APIV1Wallet> wallets = kcl.listWallets().getWallets();
        for (APIV1Wallet w: wallets){
            if (w.getName().equals(walletName)){
                walletID = w.getId();
            }
        }
        InitWalletHandleTokenRequest tokenreq = new InitWalletHandleTokenRequest();
        tokenreq.setWalletId(walletID);
        tokenreq.setWalletPassword(walletPswd);
        handle = kcl.initWalletHandleToken(tokenreq).getWalletHandleToken();
        ListKeysRequest req = new ListKeysRequest();
        req.setWalletHandleToken(handle);
        addresses = kcl.listKeysInWallet(req).getAddresses();
        pk = getAddress(0);
    }

    @Given("default transaction with parameters {int} {string}")
    public void defaultTxn(int amt, String note) {
        defaultTxnWithAddress(amt, note, getAddress(0));
    }

    @Given("default transaction with parameters {int} {string} and rekeying key")
    public void defaultTxnForRekeying(int amt, String note) {
        defaultTxnWithAddress(amt, note, rekey);
    }

    private void defaultTxnWithAddress(int amt, String note, Address sender) {
        getParams();
        if (note.equals("none")) {
            this.note = null;
        } else {
            this.note = Encoder.decodeFromBase64(note);
        }
        txnBuilder = Transaction.PaymentTransactionBuilder()
                .sender(sender)
                .suggestedParams(params)
                .note(this.note)
                .amount(amt)
                .receiver(getAddress(1));
        txn = txnBuilder.build();
        pk = sender;
    }

    @Given("default multisig transaction with parameters {int} {string}")
    public void defaultMsigTxn(int amt, String note) {
        getParams();
        if (note.equals("none")){
            this.note = null;
        } else{
            this.note = Encoder.decodeFromBase64(note);
        }
        Ed25519PublicKey[] addrlist = new Ed25519PublicKey[addresses.size()];
        for(int x = 0; x < addresses.size(); x++){
            addrlist[x] = new Ed25519PublicKey((getAddress(x)).getBytes());
        }
        msig = new MultisigAddress(1, 1, Arrays.asList(addrlist));
        txn = Transaction.PaymentTransactionBuilder()
                .sender(msig.toString())
                .suggestedParams(params)
                .note(this.note)
                .amount(amt)
                .receiver(getAddress(1))
                .build();
        pk = getAddress(0);
    }

    @When("I send the transaction")
    public void sendTxn() throws Exception {
        txid = aclv2.RawTransaction().rawtxn(Encoder.encodeToMsgPack(stx)).execute().body().txId;
    }

    @When("I send the multisig transaction")
    public void sendMsigTxn() throws Exception {
        err = !aclv2.RawTransaction().rawtxn(Encoder.encodeToMsgPack(stx)).execute().isSuccessful();
    }

    @Then("the transaction should not go through")
    public void txnFail() {
        assertThat(err).isTrue();
    }

    @When("I sign the transaction with kmd")
    public void signKmd() throws JsonProcessingException, com.algorand.algosdk.kmd.client.ApiException {
        SignTransactionRequest req = new SignTransactionRequest();
        req.setTransaction(Encoder.encodeToMsgPack(txn));
        req.setWalletHandleToken(handle);
        req.setWalletPassword(walletPswd);
        stxBytes = kcl.signTransaction(req).getSignedTransaction();
    }
    @Then("the signed transaction should equal the kmd signed transaction")
    public void signBothEqual() throws JsonProcessingException {
        assertThat(Encoder.encodeToBase64(stxBytes)).isEqualTo(Encoder.encodeToBase64(Encoder.encodeToMsgPack(stx)));
    }

    @When("I sign the multisig transaction with kmd")
    public void signMsigKmd() throws com.algorand.algosdk.kmd.client.ApiException, IOException{
        ImportMultisigRequest importReq = new ImportMultisigRequest();
        importReq.setMultisigVersion(msig.version);
        importReq.setThreshold(msig.threshold);
        importReq.setWalletHandleToken(handle);
        importReq.setPks(msig.publicKeys);
        kcl.importMultisig(importReq);

        SignMultisigRequest req = new SignMultisigRequest();
        req.setTransaction(Encoder.encodeToMsgPack(txn));
        req.setWalletHandleToken(handle);
        req.setWalletPassword(walletPswd);
        req.setPublicKey(pk.getBytes());
        stxBytes = kcl.signMultisigTransaction(req).getMultisig();
    }

    @Then("the multisig transaction should equal the kmd signed multisig transaction")
    public void signMsigBothEqual() throws JsonProcessingException, com.algorand.algosdk.kmd.client.ApiException {
        assertThat(Encoder.encodeToBase64(stxBytes)).isEqualTo(Encoder.encodeToBase64(Encoder.encodeToMsgPack(stx.mSig)));
        DeleteMultisigRequest req = new DeleteMultisigRequest();
        req.setAddress(msig.toString());
        req.setWalletHandleToken(handle);
        req.setWalletPassword(walletPswd);
        kcl.deleteMultisig(req);
    }

    @Then("the node should be healthy")
    public void nodeHealth() throws Exception {
        aclv2.HealthCheck().execute();
    }

    @Then("I get the ledger supply")
    public void getLedger() throws Exception {
        aclv2.GetSupply().execute();
    }

    @When("I create a bid")
    public void createBid() throws NoSuchAlgorithmException {
        account = new Account();
        pk = account.getAddress();
        address = pk.toString();
        bid = new Bid(pk, pk, BigInteger.valueOf(1L), BigInteger.valueOf(2L), BigInteger.valueOf(3L), BigInteger.valueOf(4L));
    }

    @When("I encode and decode the bid")
    public void encDecBid() throws JsonProcessingException, IOException{
        sbid = Encoder.decodeFromMsgPack(Encoder.encodeToMsgPack(sbid), SignedBid.class);
    }

    @When("I sign the bid")
    public void signBid() throws NoSuchAlgorithmException{
        sbid = account.signBid(bid);
        oldBid = account.signBid(bid);
    }

    @Then("the bid should still be the same")
    public void checkBid() {
        assertThat(sbid).isEqualTo(oldBid);
    }

    @When("I decode the address")
    public void decAddr() throws NoSuchAlgorithmException{
        pk = new Address(address);
        oldAddr = address;
    }

    @When("I encode the address")
    public void encAddr() {
        address = pk.toString();
    }

    @Then("the address should still be the same")
    public void checkAddr() {
        assertThat(address).isEqualTo(oldAddr);
    }

    @When("I convert the private key back to a mnemonic")
    public void skToMn() {
        mnemonic = account.toMnemonic();
    }

    @Then("the mnemonic should still be the same as {string}")
    public void checkMn(String mn) {
        assertThat(mnemonic).isEqualTo(mn);
    }

    @Given("mnemonic for master derivation key {string}")
    public void mnforMdk(String mn) throws GeneralSecurityException {
        mdk = Mnemonic.toKey(mn);
    }

    @When("I convert the master derivation key back to a mnemonic")
    public void mdkToMn() {
        mnemonic = Mnemonic.fromKey(mdk);
    }

    @When("I create the flat fee payment transaction")
    public void createPaytxnFlat() {
        txn = Transaction.PaymentTransactionBuilder()
                .sender(pk)
                .flatFee(fee)
                .firstValid(fv)
                .lastValid(lv)
                .note(note)
                .genesisID(gen)
                .genesisHash(gh)
                .amount(amt)
                .receiver(to)
                .closeRemainderTo(close)
                .build();
    }

    @Given("encoded multisig transaction {string}")
    public void encMsigTxn(String encTxn) throws IOException {
        stx = Encoder.decodeFromMsgPack(Encoder.decodeFromBase64(encTxn), SignedTransaction.class);
        Ed25519PublicKey[] addrlist = new Ed25519PublicKey[stx.mSig.subsigs.size()];
        for(int x = 0; x < addrlist.length; x++){
            addrlist[x] = stx.mSig.subsigs.get(x).key;
        }
        msig = new MultisigAddress(stx.mSig.version, stx.mSig.threshold, Arrays.asList(addrlist));
    }

    @When("I append a signature to the multisig transaction")
    public void appendMsig() throws NoSuchAlgorithmException {
        stx = account.appendMultisigTransaction(msig, stx);
    }

    @Given("encoded multisig transactions {string}")
    public void encMsigTxns(String encTxns) throws IOException {
        String[] txnArray = encTxns.split(" ");
        stxs = new SignedTransaction[txnArray.length];
        for (int t = 0; t < txnArray.length; t++){
            stxs[t] = Encoder.decodeFromMsgPack(Encoder.decodeFromBase64(txnArray[t]), SignedTransaction.class);
        }
    }

    @When("I merge the multisig transactions")
    public void mergeMsig() {
        stx = Account.mergeMultisigTransactions(stxs);
    }

    @When("I convert {long} microalgos to algos and back")
    public void microToAlgo(long ma) {
        microalgos = BigInteger.valueOf(ma);
        BigDecimal algos = AlgoConverter.toAlgos(microalgos);
        microalgos = AlgoConverter.toMicroAlgos(algos);
    }

    @Then("it should still be the same amount of microalgos {long}")
    public void checkMicro(long ma) {
        assertThat(microalgos).isEqualTo(BigInteger.valueOf(ma));
    }

    @Then("I can get account information")
    public void newAccInfo() throws Exception {
        aclv2.AccountInformation(pk).execute();
        DeleteKeyRequest req = new DeleteKeyRequest();
        req.setAddress(pk.encodeAsString());
        req.setWalletHandleToken(handle);
        req.setWalletPassword(walletPswd);
        kcl.deleteKey(req);
    }

    @Given("asset test fixture")
    public void asset_test_fixture() {
        // Implemented by the construction of Stepdefs;
    }

    @Given("default asset creation transaction with total issuance {int}")
    public void default_asset_creation_transaction_with_total_issuance(Integer assetTotal) {
        getParams();

        Transaction tx = Transaction.AssetCreateTransactionBuilder()
                .sender(getAddress(0))
                .suggestedParams(params)
                .note(this.note)
                .assetTotal(assetTotal)
                .assetDecimals(0)
                .assetName(this.assetName)
                .assetUnitName(this.assetUnitName)
                .manager(getAddress(0))
                .reserve(getAddress(0))
                .clawback(getAddress(0))
                .freeze(getAddress(0))
                .build();

        this.creator = addresses.get(0);
        this.txn = tx;
        this.expectedParams = tx.assetParams;
    }

    @When("I get the asset info")
    public void i_get_the_asset_info() throws Exception {
        this.queriedParams = aclv2.GetAssetByID(this.assetID.longValue()).execute().body();
    }

    static private String null_to_empty(String str) {
        return str == null ? "" : str;
    }

    @Then("the asset info should match the expected asset info")
    public void the_asset_info_should_match_the_expected_asset_info() {
        // Can't use a regular assertj call because 'compareTo' isn't a regular comparator.
        assertThat(this.expectedParams.assetManager.compareTo(null_to_empty(this.queriedParams.params.manager))).isTrue();
        assertThat(this.expectedParams.assetReserve.compareTo(null_to_empty(this.queriedParams.params.reserve))).isTrue();
        assertThat(this.expectedParams.assetFreeze.compareTo(null_to_empty(this.queriedParams.params.freeze))).isTrue();
        assertThat(this.expectedParams.assetClawback.compareTo(null_to_empty(this.queriedParams.params.clawback))).isTrue();
    }

    @When("I create a no-managers asset reconfigure transaction")
    public void i_create_a_no_managers_asset_reconfigure_transaction() {
        getParams();

        Transaction tx = Transaction.AssetConfigureTransactionBuilder()
                .sender(this.creator)
                .suggestedParams(params)
                .note(this.note)
                .assetIndex(this.assetID)
                .manager(this.creator)
                .strictEmptyAddressChecking(false)
                .build();
        this.txn = tx;
        this.expectedParams = tx.assetParams;
    }

    @When("I create an asset destroy transaction")
    public void i_create_an_asset_destroy_transaction() {
        getParams();

        Transaction tx = Transaction.AssetDestroyTransactionBuilder()
                .sender(this.creator)
                .suggestedParams(this.params)
                .note(this.note)
                .assetIndex(this.assetID)
                .build();
        this.txn = tx;
        this.expectedParams = tx.assetParams;
    }

    @Then("I should be unable to get the asset info")
    public void i_should_be_unable_to_get_the_asset_info() throws Exception {
        this.i_get_the_asset_info();
        assertThat(queriedParams).isNull();
    }

    @When("I create a transaction transferring {int} assets from creator to a second account")
    public void i_create_a_transaction_transferring_assets_from_creator_to_a_second_account(Integer int1) {
        getParams();

        this.txn = Transaction.AssetTransferTransactionBuilder()
                .sender(this.creator)
                .assetReceiver(getAddress(1))
                .assetAmount(int1)
                .suggestedParams(this.params)
                .note(this.note)
                .assetIndex(this.assetID)
                .build();
        this.pk = getAddress(0);
    }

    @Then("the creator should have {int} assets remaining")
    public void the_creator_should_have_assets_remaining(Integer expectedBal) throws Exception {
        AccountAssetResponse holding = aclv2.AccountAssetInformation(new Address(this.creator), this.assetID.longValue()).execute().body();
        assertThat(holding.assetHolding.amount).isEqualTo(BigInteger.valueOf(expectedBal));
    }

    @Then("I update the asset index")
    public void i_update_the_asset_index() throws Exception {
        List<Asset> assets = aclv2.AccountInformation(new Address(this.creator)).execute().body().createdAssets;
        List<BigInteger> assetIDs = new ArrayList<>();
        for (Asset a: assets) {
            assetIDs.add(BigInteger.valueOf(a.index));
        }
        this.assetID = Collections.max(assetIDs);
    }

    @When("I send the bogus kmd-signed transaction")
    public void i_send_the_bogus_kmd_signed_transaction() {
        try {
            txid = aclv2.RawTransaction().rawtxn(this.stxBytes).execute().body().txId;
        } catch (Exception e) {
            this.err = true;
        }
    }

    @Then("I create a transaction for a second account, signalling asset acceptance")
    public void i_create_a_transaction_for_a_second_account_signalling_asset_acceptance() {
        getParams();

        this.txn = Transaction.AssetAcceptTransactionBuilder()
                .acceptingAccount(getAddress(1))
                .suggestedParams(this.params)
                .note(this.note)
                .assetIndex(this.assetID)
                .build();
        this.pk = getAddress(1);
    }

    @Then("I send the kmd-signed transaction")
    public void i_send_the_kmd_signed_transaction() throws Exception {
        txid = aclv2.RawTransaction().rawtxn(this.stxBytes).execute().body().txId;
    }

    @When("I create a freeze transaction targeting the second account")
    public void i_create_a_freeze_transaction_targeting_the_second_account() throws com.algorand.algosdk.kmd.client.ApiException {
        this.renewHandle(); // to avoid handle expired error
        getParams();

        this.txn = Transaction.AssetFreezeTransactionBuilder()
                .sender(getAddress(0))
                .freezeTarget(getAddress(1))
                .freezeState(true)
                .assetIndex(this.assetID)
                .note(this.note)
                .suggestedParams(this.params)
                .build();
        this.pk = getAddress(0);
    }

    @When("I create a transaction transferring {int} assets from a second account to creator")
    public void i_create_a_transaction_transferring_assets_from_a_second_account_to_creator(Integer int1) {
        getParams();

        this.txn = Transaction.AssetTransferTransactionBuilder()
                .sender(getAddress(1))
                .assetReceiver(this.creator)
                .assetAmount(int1)
                .note(this.note)
                .assetIndex(this.assetID)
                .suggestedParams(this.params)
                .build();
        this.pk = getAddress(1);
    }

    @When("I create an un-freeze transaction targeting the second account")
    public void i_create_an_un_freeze_transaction_targeting_the_second_account() throws com.algorand.algosdk.kmd.client.ApiException  {
        this.renewHandle(); // to avoid handle expired error
        getParams();

        this.txn = Transaction.AssetFreezeTransactionBuilder()
                .sender(getAddress(0))
                .freezeTarget(getAddress(1))
                .freezeState(false)
                .assetIndex(this.assetID)
                .note(this.note)
                .suggestedParams(this.params)
                .build();
        this.pk = getAddress(0);
    }

    @Given("default-frozen asset creation transaction with total issuance {int}")
    public void default_frozen_asset_creation_transaction_with_total_issuance(Integer int1) throws NoSuchAlgorithmException {
        getParams();

        Transaction tx = Transaction.AssetCreateTransactionBuilder()
                .sender(getAddress(0))
                .suggestedParams(this.params)
                .note(this.note)
                .assetTotal(int1)
                .assetDecimals(0)
                .defaultFrozen(true)
                .assetName(this.assetName)
                .assetUnitName(this.assetUnitName)
                .manager(getAddress(0))
                .reserve(getAddress(0))
                .freeze(getAddress(0))
                .clawback(getAddress(0))
                .build();
        Account.setFeeByFeePerByte(tx, tx.fee);
        this.creator = addresses.get(0);
        this.txn = tx;
        this.expectedParams = tx.assetParams;
    }

    @When("I create a transaction revoking {int} assets from a second account to creator")
    public void i_create_a_transaction_revoking_assets_from_a_second_account_to_creator(Integer int1) {
        getParams();

        this.txn = Transaction.AssetClawbackTransactionBuilder()
                .sender(getAddress(0))
                .assetClawbackFrom(getAddress(1))
                .assetReceiver(getAddress(0))
                .assetAmount(int1)
                .assetIndex(this.assetID)
                .note(this.note)
                .suggestedParams(this.params)
                .build();
        this.pk = getAddress(0);
    }

    @When("I add a rekeyTo field with address {string}")
    public void i_add_a_rekeyTo_field_with_address(String string) {
        txnBuilder.rekey(string);
        txn = txnBuilder.build();
    }

    @When("I add a rekeyTo field with the private key algorand address")
    public void i_add_a_rekeyTo_field_with_the_private_key_algorand_address() {
        txnBuilder.rekey(this.pk.toString());
        txn = txnBuilder.build();
    }

    @When("I compile a teal program {string}")
    public void i_compile_teal_program(String path) throws Exception {
        byte[] source = loadResource(path);
        compileResponse = aclv2.TealCompile().source(source).execute();
    }

    @Then("base64 decoding the response is the same as the binary {string}")
    public void base64_decoding_the_response_is_the_same_as_the_binary(String path) {
        byte[] source = loadResource(path);
        String b64EncodedSrc = Encoder.encodeToBase64(source);
        assertThat(b64EncodedSrc).isEqualTo(compileResponse.body().result);
    }

    @Then("it is compiled with {int} and {string} and {string}")
    public void it_is_compiled_with(Integer status, String result, String hash) {
        assertThat(compileResponse.code()).isEqualTo(status);
        CompileResponse body = compileResponse.body();
        if (body != null) {
            assertThat(compileResponse.isSuccessful()).isTrue();
            assertThat(body.result).isEqualTo(result);
            assertThat(body.hash).isEqualTo(hash);
        } else {
            assertThat(compileResponse.isSuccessful()).isFalse();
        }
    }

    @When("I dryrun a {string} program {string}")
    public void i_dryrun_a_program(String kind, String path) throws Exception {
        byte[] data = loadResource(path);
        List<DryrunSource> sources = new ArrayList<DryrunSource>();
        List<SignedTransaction> stxns = new ArrayList<SignedTransaction>();
        Account account = new Account();
        Address pk = account.getAddress();
        Digest gh = new Digest(Encoder.decodeFromBase64("ZIkPs8pTDxbRJsFB1yJ7gvnpDu0Q85FRkl2NCkEAQLU="));
        Transaction txn = Transaction.PaymentTransactionBuilder()
            .sender(pk)
            .fee(1000)
            .firstValid(1)
            .lastValid(100)
            .amount(1000)
            .genesisHash(gh)
            .receiver(pk)
            .build();

        if (kind.equals("compiled")) {
            LogicsigSignature lsig = new LogicsigSignature(data);
            SignedTransaction stxn = new SignedTransaction(txn, lsig);
            stxns.add(stxn);
        } else if (kind.equals("source")) {
            DryrunSource drs = new DryrunSource();
            drs.fieldName = "lsig";
            drs.source = new String(data);
            drs.txnIndex = 0L;
            sources.add(drs);
            SignedTransaction stxn = new SignedTransaction(txn, new Signature());
            stxns.add(stxn);
        } else {
            fail("kind " + kind + " not in (compiled, source)");
        }

        DryrunRequest dr = new DryrunRequest();
        dr.txns = stxns;
        dr.sources = sources;
        dryrunResponse = aclv2.TealDryrun().request(dr).execute();
    }

    @When("I get execution result {string}")
    public void i_get_execution_result(String result) {
        DryrunResponse ddr = dryrunResponse.body();
        assertThat(ddr).isNotNull();
        assertThat(ddr.txns).isNotNull();
        assertThat(ddr.txns.size()).isGreaterThan(0);
        List<String> msgs = new ArrayList<String>();
        if (ddr.txns.get(0).appCallMessages.size() > 0) {
            msgs = ddr.txns.get(0).appCallMessages;
        } else if (ddr.txns.get(0).logicSigMessages.size() > 0) {
            msgs = ddr.txns.get(0).logicSigMessages;
        }
        assertThat(msgs.size()).isGreaterThan(0);
        assertThat(msgs.get(msgs.size() - 1)).isEqualTo(result);
    }

    @When("I compile a teal program {string} with mapping enabled")
    public void i_compile_a_teal_program_with_mapping_enabled(String tealPath) throws Exception {
        byte[] tealProgram = ResourceUtils.loadResource(tealPath);
        this.compileResponse = aclv2.TealCompile().source(tealProgram).sourcemap(true).execute();
    }

    @Then("the resulting source map is the same as the json {string}")
    public void the_resulting_source_map_is_the_same_as_the_json(String jsonPath) throws Exception {
        String[] fields = {"version", "sources", "names", "mappings"};
        String srcMapStr = new String(ResourceUtils.readResource(jsonPath), StandardCharsets.UTF_8);

        HashMap<String, Object> expectedMap = new HashMap<>(Encoder.decodeFromJson(srcMapStr, Map.class));
        HashMap<String, Object> actualMap = this.compileResponse.body().sourcemap;

        assertThat(expectedMap.size()).isEqualTo(actualMap.size());

        for(String field: fields){
            assertThat(actualMap.get(field)).isEqualTo(expectedMap.get(field));
        }
    }

    @Then("disassembly of {string} matches {string}")
    public void disassemblyMatches(String bytecodeFilename, String sourceFilename) throws Exception {
        String disassembledSource = aclv2.TealDisassemble().source(loadResource(bytecodeFilename)).execute().body().result;
        String expectedSource = new String(ResourceUtils.readResource(sourceFilename), StandardCharsets.UTF_8);

        assertThat(disassembledSource).isEqualTo(expectedSource);
    }
}
