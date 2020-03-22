package com.algorand.algosdk.integration;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.Ed25519PublicKey;
import com.algorand.algosdk.crypto.MultisigAddress;
import com.algorand.algosdk.crypto.MultisigSignature;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.AlgoConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.algorand.algosdk.auction.Bid;
import com.algorand.algosdk.auction.SignedBid;
import com.algorand.algosdk.algod.client.AlgodClient;
import com.algorand.algosdk.algod.client.api.AlgodApi;
import com.algorand.algosdk.algod.client.ApiException;
import com.algorand.algosdk.algod.client.model.*;
import com.algorand.algosdk.kmd.client.KmdClient;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.kmd.client.model.*;
import com.algorand.algosdk.mnemonic.Mnemonic;
import com.algorand.algosdk.crypto.ParticipationPublicKey;
import com.algorand.algosdk.crypto.VRFPublicKey;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.threeten.bp.LocalDate;

import java.util.List;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


public class StepdefsIT {
    public StepdefsIT() {
        System.out.println("Created stepdefs....");
        System.out.println("Created stepdefs....");
        System.out.println("Created stepdefs....");
        System.out.println("Created stepdefs....");
        System.out.println("Created stepdefs....");
        System.out.println("Created stepdefs....");
        System.out.println("Created stepdefs....");
        System.out.println("Created stepdefs....");
    }
    TransactionParams params;
    SignedTransaction stx;
    SignedTransaction[] stxs;
    byte[] stxBytes;
    Transaction txn;
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
    MultisigSignature msigsig;
    String walletName;
    String walletPswd;
    String walletID;
    AlgodApi acl;
    AlgodClient algodClient;
    KmdApi kcl;
    KmdClient kmdClient;
    String handle;
    List<String> versions;
    NodeStatus status;
    NodeStatus statusAfter;
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
    BigInteger paramsFee;
    ParticipationPublicKey votepk;
    VRFPublicKey vrfpk;
    BigInteger votefst;
    BigInteger votelst;
    BigInteger votekd;
    String num;

    /* Assets */
    String creator = "";
    BigInteger assetID = BigInteger.valueOf(1);
    String assetName = "testcoin";
    String assetUnitName = "coins";
    Transaction.AssetParams expectedParams = null;
    AssetParams queriedParams = new AssetParams();

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
            params = acl.transactionParams();
            lastRound = params.getLastRound();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convenience method to export a key and initialize an account to use for signing.
     */
    public void exportKeyAndSetAccount(Address addr) throws com.algorand.algosdk.kmd.client.ApiException, NoSuchAlgorithmException {
        ExportKeyRequest req = new ExportKeyRequest();
        req.setAddress(addr.toString());
        req.setWalletHandleToken(handle);
        req.setWalletPassword(walletPswd);
        sk = kcl.exportKey(req).getPrivateKey();
        account = new Account(Arrays.copyOfRange(sk, 0, 32));
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
            if (w.getName().equals(walletName)){
                exists = true;
            }
        }
        System.out.println("Does this work at all...");
        assertThat(exists).isTrue().withFailMessage("walletExist");
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
        System.out.println("Does this work at all...");
        assertThat(mdk.length).isGreaterThan(0).withFailMessage("derivation key");
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
        System.out.println("Does this work at all...");
        assertThat(name).isEqualTo(walletName).withFailMessage("getWalletInfo");
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
    public void tryHandle() throws com.algorand.algosdk.kmd.client.ApiException{
        RenewWalletHandleTokenRequest req = new RenewWalletHandleTokenRequest();
        req.setWalletHandleToken(handle);
        err = false;
        try{
            kcl.renewWalletHandleToken(req);
        } catch(Exception e){
            err = true;
        }
        System.out.println("Does this work at all...");
        assertThat(err).isTrue().withFailMessage("tryHandle");
    }

    @Given("payment transaction parameters {int} {int} {int} {string} {string} {string} {int} {string} {string}")
    public void transactionParameters(int fee, int fv, int lv, String gh, String to, String close, int amt, String gen, String note)  throws GeneralSecurityException, NoSuchAlgorithmException{
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

    @Given("key registration transaction parameters {int} {int} {int} {string} {string} {string} {int} {int} {int} {string} {string}")
    public void keyregTxnParameters(int fee, int fv, int lv, String gh, String votepk, String vrfpk, int votefst, int votelst, int votekd, String gen, String note)  throws GeneralSecurityException, NoSuchAlgorithmException{
        this.fee = BigInteger.valueOf(fee);
        this.fv = BigInteger.valueOf(fv);
        this.lv = BigInteger.valueOf(lv);
        this.gh = new Digest(Encoder.decodeFromBase64(gh));
        this.votepk = new ParticipationPublicKey(Encoder.decodeFromBase64(votepk));
        this.vrfpk = new VRFPublicKey(Encoder.decodeFromBase64(vrfpk));
        this.votefst = BigInteger.valueOf(votefst);
        this.votelst = BigInteger.valueOf(votelst);
        this.votekd = BigInteger.valueOf(votekd);
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

    @When("I create the payment transaction")
    public void createPaytxn() throws NoSuchAlgorithmException, JsonProcessingException, IOException{
        txn = Transaction.PaymentTransactionBuilder()
                .sender(pk)
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

    @When("I create the key registration transaction")
    public void createKeyregTxn() throws NoSuchAlgorithmException, JsonProcessingException, IOException{
        txn = Transaction.KeyRegistrationTransactionBuilder()
                .sender(pk)
                .fee(fee)
                .firstValid(fv)
                .lastValid(lv)
                .note(note)
                .genesisID(gen)
                .genesisHash(gh)
                .participationPublicKey(votepk)
                .selectionPublicKey(vrfpk)
                .voteFirst(votefst)
                .voteLast(votelst)
                .voteKeyDilution(votekd)
                .build();
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
        System.out.println("Does this work at all...");
        assertThat(Encoder.encodeToBase64(signedTxBytes)).isEqualTo(golden).withFailMessage("equalGolden");

    }

    @Then("the multisig transaction should equal the golden {string}")
    public void equalMsigGolden(String golden) throws JsonProcessingException{
        byte[] signedTxBytes = Encoder.encodeToMsgPack(stx);
        System.out.println("Does this work at all...");
        assertThat(Encoder.encodeToBase64(signedTxBytes)).isEqualTo(golden).withFailMessage("equalMsigGolden");
    }


    @Then("the multisig address should equal the golden {string}")
    public void equalMsigAddrGolden(String golden){
        System.out.println("Does this work at all...");
        assertThat(msig.toString()).isEqualTo(golden).withFailMessage("equalMsigAddrGolden");
    }

    @When("I get versions with algod")
    public void aclV() throws ApiException{
        versions = acl.getVersion().getVersions();
    }

    @Then("v1 should be in the versions")
    public void v1InVersions(){
        System.out.println("Does this work at all...");
        assertThat(versions).contains("v1").withFailMessage("v1InVersions");
    }

    @When("I get versions with kmd")
    public void kclV() throws com.algorand.algosdk.kmd.client.ApiException{
        versions = kcl.getVersion().getVersions();
    }

    @When("I get the status")
    public void status() throws ApiException{
        status = acl.getStatus();
    }

    @When("I get status after this block")
    public void statusBlock() throws ApiException, InterruptedException {
        Thread.sleep(4000);
        statusAfter = acl.waitForBlock(status.getLastRound());
    }

    @Then("I can get the block info")
    public void block() throws ApiException{
        acl.getBlock(status.getLastRound().add(BigInteger.valueOf(1)));
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
        System.out.println("Does this work at all...");
        assertThat(exists).isTrue().withFailMessage("msigInWallet");
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
        System.out.println("Does this work at all...");
        assertThat(eq).isTrue().withFailMessage("msigEq");
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
        System.out.println("Does this work at all...");
        assertThat(exists).isFalse().withFailMessage("msigNotInWallet");
    }

    @When("I generate a key using kmd")
    public void genKeyKmd() throws com.algorand.algosdk.kmd.client.ApiException, NoSuchAlgorithmException{
        GenerateKeyRequest req = new GenerateKeyRequest();
        req.setDisplayMnemonic(false);
        req.setWalletHandleToken(handle);
        pk = new Address(kcl.generateKey(req).getAddress());
    }

    @Then("the key should be in the wallet")
    public void keyInWallet() throws com.algorand.algosdk.kmd.client.ApiException {
        ListKeysRequest req = new ListKeysRequest();
        req.setWalletHandleToken(handle);
        List<String> keys = kcl.listKeysInWallet(req).getAddresses();
        boolean exists = false;
        for (String k : keys){
            if (k.equals(pk.toString())){
                exists = true;
            }
        }
        System.out.println("Does this work at all...");
        assertThat(exists).isFalse().withFailMessage("keyInWallet");
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
        System.out.println("Does this work at all...");
        assertThat(exists).isFalse().withFailMessage("keyNotInWallet");
    }

    @When("I generate a key")
    public void genKey()throws NoSuchAlgorithmException, GeneralSecurityException{
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
        System.out.println("Does this work at all...");
        assertThat(Encoder.encodeToBase64(sk)).isEqualTo(Encoder.encodeToBase64(exported)).withFailMessage("expSkEq");
        DeleteKeyRequest deleteReq = new DeleteKeyRequest();
        deleteReq.setAddress(pk.toString());
        deleteReq.setWalletHandleToken(handle);
        deleteReq.setWalletPassword(walletPswd);
        kcl.deleteKey(deleteReq);
    }

    @Given("a kmd client")
    public void kClient() throws FileNotFoundException, IOException, NoSuchAlgorithmException{
        String data_dir_path = System.getenv("NODE_DIR") + "/";
        data_dir_path += System.getenv("KMD_DIR") + "/";
        BufferedReader reader = new BufferedReader(new FileReader(data_dir_path + "kmd.token"));
        String kmdToken = reader.readLine();
        reader.close();
        reader = new BufferedReader(new FileReader(data_dir_path + "kmd.net"));
        String kmdAddress = reader.readLine();
        kmdClient = new KmdClient();
        kmdClient.setConnectTimeout(30000);
        kmdClient.setReadTimeout(30000);
        kmdClient.setWriteTimeout(30000);
        kmdClient.setApiKey(kmdToken);
        kmdClient.setBasePath("http://" + kmdAddress);
        kcl = new KmdApi(kmdClient);

    }

    @Given("an algod client")
    public void aClient() throws FileNotFoundException, IOException{
        String data_dir_path = System.getenv("NODE_DIR") + "/";
        BufferedReader reader = new BufferedReader(new FileReader(data_dir_path + "algod.token"));
        String algodToken = reader.readLine();
        reader.close();
        reader = new BufferedReader(new FileReader(data_dir_path + "algod.net"));
        String algodAddress = reader.readLine();
        algodClient = new AlgodClient();
        algodClient.setConnectTimeout(30000);
        algodClient.setReadTimeout(30000);
        algodClient.setWriteTimeout(30000);
        algodClient.setApiKey(algodToken);
        algodClient.setBasePath("http://" + algodAddress);
        acl = new AlgodApi(algodClient);

    }

    @Given("wallet information")
    public void walletInfo() throws com.algorand.algosdk.kmd.client.ApiException, NoSuchAlgorithmException{
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
    public void defaultTxn(int amt, String note) throws ApiException, NoSuchAlgorithmException{
        getParams();
        if (note.equals("none")){
            this.note = null;
        } else{
            this.note = Encoder.decodeFromBase64(note);
        }
        txn = Transaction.PaymentTransactionBuilder()
                .sender(getAddress(0))
                .suggestedParams(params)
                .note(this.note)
                .amount(amt)
                .receiver(getAddress(1))
                .build();
        pk = getAddress(0);
    }

    @Given("default multisig transaction with parameters {int} {string}")
    public void defaultMsigTxn(int amt, String note) throws ApiException, NoSuchAlgorithmException{
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
    public void sendTxn() throws JsonProcessingException, ApiException{
        txid = acl.rawTransaction(Encoder.encodeToMsgPack(stx)).getTxId();
    }

    @When("I send the multisig transaction")
    public void sendMsigTxn() throws JsonProcessingException, ApiException{
        try{
            acl.rawTransaction(Encoder.encodeToMsgPack(stx));
        } catch(Exception e) {
            err = true;
        }
    }

    @Then("the transaction should go through")
    public void checkTxn() throws ApiException, InterruptedException{
        String ans = acl.pendingTransactionInformation(txid).getFrom();
        System.out.println("Does this work at all...");
        assertThat(pk.toString()).isEqualTo(ans).withFailMessage("checkTxn1");
        acl.waitForBlock(lastRound.add(BigInteger.valueOf(2)));
        System.out.println("Does this work at all...");
        assertThat(acl.transactionInformation(pk.toString(), txid).getFrom()).isEqualTo(pk.toString()).withFailMessage("checkTxn2");
        System.out.println("Does this work at all...");
        assertThat(acl.transaction(txid).getFrom()).isEqualTo(pk.toString()).withFailMessage("checkTxn3");
    }

    @Then("I can get the transaction by ID")
    public void txnbyID() throws ApiException, InterruptedException{
        acl.waitForBlock(lastRound.add(BigInteger.valueOf(2)));
        System.out.println("Does this work at all...");
        assertThat(acl.transaction(txid).getFrom()).isEqualTo(pk.toString()).withFailMessage("txnbyID");
    }

    @Then("the transaction should not go through")
    public void txnFail(){
        System.out.println("Does this work at all...");
        assertThat(err).isTrue().withFailMessage("txnFail");
    }

    @When("I sign the transaction with kmd")
    public void signKmd() throws JsonProcessingException, com.algorand.algosdk.kmd.client.ApiException, NoSuchAlgorithmException{
        SignTransactionRequest req = new SignTransactionRequest();
        req.setTransaction(Encoder.encodeToMsgPack(txn));
        req.setWalletHandleToken(handle);
        req.setWalletPassword(walletPswd);
        stxBytes = kcl.signTransaction(req).getSignedTransaction();
    }
    @Then("the signed transaction should equal the kmd signed transaction")
    public void signBothEqual() throws JsonProcessingException {
        System.out.println("Does this work at all...");
        assertThat(Encoder.encodeToBase64(stxBytes)).isEqualTo(Encoder.encodeToBase64(Encoder.encodeToMsgPack(stx)))
            .withFailMessage("signBothEqual");
    }

    @When("I sign the multisig transaction with kmd")
    public void signMsigKmd() throws JsonProcessingException, com.algorand.algosdk.kmd.client.ApiException, IOException{
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
        System.out.println("Does this work at all...");
        assertThat(Encoder.encodeToBase64(stxBytes)).isEqualTo(Encoder.encodeToBase64(Encoder.encodeToMsgPack(stx.mSig)))
            .withFailMessage("signMsigBothEqual");
        DeleteMultisigRequest req = new DeleteMultisigRequest();
        req.setAddress(msig.toString());
        req.setWalletHandleToken(handle);
        req.setWalletPassword(walletPswd);
        kcl.deleteMultisig(req);
    }

    @When("I read a transaction {string} from file {string}")
    public void readTxn(String encodedTxn, String num) throws IOException {
        String path = System.getProperty("user.dir");
        Path p = Paths.get(path);
        this.num = num;
        path = p.getParent() + "/temp/raw" + this.num + ".tx";
        FileInputStream inputStream = new FileInputStream(path);
        File file = new File(path);
        byte[] data = new byte[(int) file.length()];
        inputStream.read(data);
        stx = Encoder.decodeFromMsgPack(data, SignedTransaction.class);
        inputStream.close();
    }

    @When("I write the transaction to file")
    public void writeTxn() throws JsonProcessingException, IOException{
        String path = System.getProperty("user.dir");
        Path p = Paths.get(path);
        path = p.getParent() + "/temp/raw" + this.num + ".tx";
        byte[] data = Encoder.encodeToMsgPack(stx);
        FileOutputStream out = new FileOutputStream(path);
        out.write(data);
        out.close();
    }

    @Then("the transaction should still be the same")
    public void checkEnc() throws IOException{
        String path = System.getProperty("user.dir");
        Path p = Paths.get(path);
        path = p.getParent() + "/temp/raw" + this.num + ".tx";
        FileInputStream inputStream = new FileInputStream(path);
        File file = new File(path);
        byte[] data = new byte[(int) file.length()];
        inputStream.read(data);
        SignedTransaction stxnew = Encoder.decodeFromMsgPack(data, SignedTransaction.class);
        inputStream.close();

        path = p.getParent() + "/temp/old" + this.num + ".tx";
        inputStream = new FileInputStream(path);
        file = new File(path);
        data = new byte[(int) file.length()];
        inputStream.read(data);
        SignedTransaction stxold = Encoder.decodeFromMsgPack(data, SignedTransaction.class);
        inputStream.close();
        System.out.println("Does this work at all...");
        assertThat(stxnew).isEqualTo(stxold).withFailMessage("checkEnc");
    }

    @Then("I do my part")
    public void signSaveTxn() throws IOException, JsonProcessingException, NoSuchAlgorithmException, com.algorand.algosdk.kmd.client.ApiException, Exception{
        String path = System.getProperty("user.dir");
        Path p = Paths.get(path);
        path = p.getParent() + "/temp/txn.tx";
        FileInputStream inputStream = new FileInputStream(path);
        File file = new File(path);
        byte[] data = new byte[(int) file.length()];
        inputStream.read(data);
        inputStream.close();

        txn = Encoder.decodeFromMsgPack(data, Transaction.class);
        exportKeyAndSetAccount(txn.sender);

        stx = account.signTransaction(txn);
        data = Encoder.encodeToMsgPack(stx);
        FileOutputStream out = new FileOutputStream(path);
        out.write(data);
        out.close();
    }

    @Then("the node should be healthy")
    public void nodeHealth() throws ApiException{
        acl.healthCheck();
    }

    @Then("I get the ledger supply")
    public void getLedger() throws ApiException{
        acl.getSupply();
    }

    @Then("I get transactions by address and round")
    public void txnsByAddrRound() throws ApiException{
        System.out.println("Does this work at all...");
        assertThat(acl.transactions(addresses.get(0), BigInteger.valueOf(1), acl.getStatus().getLastRound(), null, null, BigInteger.valueOf(10)).getTransactions())
                .isInstanceOf(List.class)
                .withFailMessage("txnsByAddrRound");
        //Assert.assertTrue(acl.transactions(addresses.get(0), BigInteger.valueOf(1), acl.getStatus().getLastRound(), null, null, BigInteger.valueOf(10)).getTransactions() instanceof List<?>);
    }

    @Then("I get transactions by address only")
    public void txnsByAddrOnly() throws ApiException{
        System.out.println("Does this work at all...");
        assertThat(acl.transactions(addresses.get(0), null, null, null, null, BigInteger.valueOf(10)).getTransactions())
                .isInstanceOf(List.class)
                .withFailMessage("txnsByAddrOnly");
        //Assert.assertTrue(acl.transactions(addresses.get(0), null, null, null, null, BigInteger.valueOf(10)).getTransactions() instanceof List<?>);
    }

    @Then("I get transactions by address and date")
    public void txnsByAddrDate() throws ApiException{
        System.out.println("Does this work at all...");
        assertThat(acl.transactions(addresses.get(0), null, null, LocalDate.now(), LocalDate.now(), BigInteger.valueOf(10)).getTransactions())
                .isInstanceOf(List.class)
                .withFailMessage("txnsByAddrDate");
        //Assert.assertTrue(acl.transactions(addresses.get(0), null, null, LocalDate.now(), LocalDate.now(), BigInteger.valueOf(10)).getTransactions() instanceof List<?>);
    }

    @Then("I get pending transactions")
    public void pendingTxns() throws ApiException{
        System.out.println("Does this work at all...");
        assertThat(acl.getPendingTransactions(BigInteger.valueOf(10)).getTruncatedTxns())
            .isInstanceOf(TransactionList.class)
            .withFailMessage("pendingTxns");
        //Assert.assertTrue(acl.getPendingTransactions(BigInteger.valueOf(10)).getTruncatedTxns() instanceof TransactionList);
    }

    @When("I get the suggested params")
    public void suggestedParams() throws ApiException{
        paramsFee = acl.transactionParams().getFee();
    }

    @When("I get the suggested fee")
    public void suggestedFee() throws ApiException {
        fee = acl.suggestedFee().getFee();
    }

    @Then("the fee in the suggested params should equal the suggested fee")
    public void checkSuggested() {
        System.out.println("Does this work at all...");
        assertThat(paramsFee).isEqualTo(fee).withFailMessage("checkSuggested");
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
        System.out.println("Does this work at all...");
        assertThat(sbid).isEqualTo(oldBid).withFailMessage("checkBid");
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
        System.out.println("Does this work at all...");
        assertThat(address).isEqualTo(oldAddr).withFailMessage("checkAddr");
    }

    @When("I convert the private key back to a mnemonic")
    public void skToMn() {
        mnemonic = account.toMnemonic();
    }

    @Then("the mnemonic should still be the same as {string}")
    public void checkMn(String mn) {
        System.out.println("Does this work at all...");
        assertThat(mnemonic).isEqualTo(mn).withFailMessage("checkMn");
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
    public void createPaytxnFlat() throws NoSuchAlgorithmException{
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
        System.out.println("Does this work at all...");
        assertThat(microalgos).isEqualTo(BigInteger.valueOf(ma)).withFailMessage("checkMicro");
    }

    @Then("I get account information")
    public void accInfo() throws ApiException {
        acl.accountInformation(addresses.get(0));
    }

    @Then("I can get account information")
    public void newAccInfo() throws ApiException, NoSuchAlgorithmException, com.algorand.algosdk.kmd.client.ApiException {
        acl.accountInformation(pk.encodeAsString());
        DeleteKeyRequest req = new DeleteKeyRequest();
        req.setAddress(pk.encodeAsString());
        req.setWalletHandleToken(handle);
        req.setWalletPassword(walletPswd);
        kcl.deleteKey(req);
    }

    @When("I get recent transactions, limited by {int} transactions")
    public void i_get_recent_transactions_limited_by_count(int cnt) throws ApiException {
        System.out.println("Does this work at all...");
        assertThat(acl.transactions(addresses.get(0), null, null, null, null, BigInteger.valueOf(cnt)).getTransactions())
                .isInstanceOf(List.class);
        //Assert.assertTrue(acl.transactions(addresses.get(0), null, null, null, null, BigInteger.valueOf(cnt)).getTransactions() instanceof List<?>);
    }

    @Given("asset test fixture")
    public void asset_test_fixture() {
        // Implemented by the construction of Stepdefs;
    }

    @Given("default asset creation transaction with total issuance {int}")
    public void default_asset_creation_transaction_with_total_issuance(Integer assetTotal) throws NoSuchAlgorithmException, ApiException, InvalidKeySpecException {
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
    public void i_get_the_asset_info() throws ApiException {
        this.queriedParams = acl.assetInformation(this.assetID);
    }

    @Then("the asset info should match the expected asset info")
    public void the_asset_info_should_match_the_expected_asset_info() throws JsonProcessingException, NoSuchAlgorithmException {
        System.out.println("Does this work at all...");
        System.out.println("Does this work at all...");
        System.out.println("Does this work at all...");
        System.out.println("Does this work at all...");
        assertThat(this.expectedParams.assetManager).isEqualTo(this.queriedParams.getManagerkey()).withFailMessage("asset info - manager");
        assertThat(this.expectedParams.assetReserve).isEqualTo(this.queriedParams.getReserveaddr()).withFailMessage("asset info - reserve");;
        assertThat(this.expectedParams.assetFreeze).isEqualTo(this.queriedParams.getFreezeaddr()).withFailMessage("asset info - freeze");;
        assertThat(this.expectedParams.assetClawback).isEqualTo(this.queriedParams.getClawbackaddr()).withFailMessage("asset info - clawback");;
        //Assert.assertTrue(this.expectedParams.assetManager.compareTo(this.queriedParams.getManagerkey()));
        //Assert.assertTrue(this.expectedParams.assetReserve.compareTo(this.queriedParams.getReserveaddr()));
        //Assert.assertTrue(this.expectedParams.assetFreeze.compareTo(this.queriedParams.getFreezeaddr()));
        //Assert.assertTrue(this.expectedParams.assetClawback.compareTo(this.queriedParams.getClawbackaddr()));
    }

    @When("I create a no-managers asset reconfigure transaction")
    public void i_create_a_no_managers_asset_reconfigure_transaction() throws NoSuchAlgorithmException, ApiException, InvalidKeySpecException {
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
    public void i_create_an_asset_destroy_transaction() throws NoSuchAlgorithmException, ApiException, InvalidKeySpecException {
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
    public void i_should_be_unable_to_get_the_asset_info() {
        boolean exists = true;
        try {
            this.i_get_the_asset_info();
        } catch (ApiException e) {
            exists = false;
        }
        System.out.println("Does this work at all...");
        assertThat(exists).isFalse();
    }

    @When("I create a transaction transferring {int} assets from creator to a second account")
    public void i_create_a_transaction_transferring_assets_from_creator_to_a_second_account(Integer int1) throws NoSuchAlgorithmException, ApiException, InvalidKeySpecException {
        getParams();

        Transaction tx = Transaction.AssetTransferTransactionBuilder()
                .sender(this.creator)
                .assetReceiver(getAddress(1))
                .assetAmount(int1)
                .suggestedParams(this.params)
                .note(this.note)
                .assetIndex(this.assetID)
                .build();
        this.txn = tx;
        this.pk = getAddress(0);
    }

    @Then("the creator should have {int} assets remaining")
    public void the_creator_should_have_assets_remaining(Integer expectedBal) throws ApiException {
        com.algorand.algosdk.algod.client.model.Account accountResp =
                this.acl.accountInformation(this.creator);
        AssetHolding holding = accountResp.getHolding(this.assetID);
        System.out.println("Does this work at all...");
        assertThat(holding.getAmount()).isEqualTo(BigInteger.valueOf(expectedBal)).withFailMessage("creator remaining");
    }

    @Then("I update the asset index")
    public void i_update_the_asset_index() throws ApiException {
        com.algorand.algosdk.algod.client.model.Account accountResp = acl.accountInformation(this.creator);
        Set<java.math.BigInteger> keys = accountResp.getThisassettotal().keySet();
        this.assetID = Collections.max(keys);
    }

    @When("I send the bogus kmd-signed transaction")
    public void i_send_the_bogus_kmd_signed_transaction() {
        try {
            txid = acl.rawTransaction(this.stxBytes).getTxId();
        } catch (ApiException e) {
            this.err = true;
        }
    }

    @Then("I create a transaction for a second account, signalling asset acceptance")
    public void i_create_a_transaction_for_a_second_account_signalling_asset_acceptance() throws ApiException, NoSuchAlgorithmException {
        getParams();

        Transaction tx = Transaction.AssetAcceptTransactionBuilder()
                .acceptingAccount(getAddress(1))
                .suggestedParams(this.params)
                .note(this.note)
                .assetIndex(this.assetID)
                .build();
        this.txn = tx;
        this.pk = getAddress(1);
    }

    @Then("I send the kmd-signed transaction")
    public void i_send_the_kmd_signed_transaction() throws ApiException {
        txid = acl.rawTransaction(this.stxBytes).getTxId();
    }

    @When("I create a freeze transaction targeting the second account")
    public void i_create_a_freeze_transaction_targeting_the_second_account() throws NoSuchAlgorithmException, ApiException, com.algorand.algosdk.kmd.client.ApiException {
        this.renewHandle(); // to avoid handle expired error
        getParams();

        Transaction tx = Transaction.AssetFreezeTransactionBuilder()
                .sender(getAddress(0))
                .freezeTarget(getAddress(1))
                .freezeState(true)
                .assetIndex(this.assetID)
                .note(this.note)
                .suggestedParams(this.params)
                .build();
        this.txn = tx;
        this.pk = getAddress(0);
    }

    @When("I create a transaction transferring {int} assets from a second account to creator")
    public void i_create_a_transaction_transferring_assets_from_a_second_account_to_creator(Integer int1) throws ApiException, NoSuchAlgorithmException {
        getParams();

        Transaction tx = Transaction.AssetTransferTransactionBuilder()
                .sender(getAddress(1))
                .assetReceiver(this.creator)
                .assetAmount(int1)
                .note(this.note)
                .assetIndex(this.assetID)
                .suggestedParams(this.params)
                .build();
        this.txn = tx;
        this.pk = getAddress(1);
    }

    @When("I create an un-freeze transaction targeting the second account")
    public void i_create_an_un_freeze_transaction_targeting_the_second_account() throws ApiException, NoSuchAlgorithmException, com.algorand.algosdk.kmd.client.ApiException  {
        this.renewHandle(); // to avoid handle expired error
        getParams();

        Transaction tx = Transaction.AssetFreezeTransactionBuilder()
                .sender(getAddress(0))
                .freezeTarget(getAddress(1))
                .freezeState(false)
                .assetIndex(this.assetID)
                .note(this.note)
                .suggestedParams(this.params)
                .build();
        this.txn = tx;
        this.pk = getAddress(0);
    }

    @Given("default-frozen asset creation transaction with total issuance {int}")
    public void default_frozen_asset_creation_transaction_with_total_issuance(Integer int1) throws ApiException, NoSuchAlgorithmException {
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
    public void i_create_a_transaction_revoking_assets_from_a_second_account_to_creator(Integer int1) throws ApiException, NoSuchAlgorithmException {
        getParams();

        Transaction tx = Transaction.AssetClawbackTransactionBuilder()
                .sender(getAddress(0))
                .assetClawbackFrom(getAddress(1))
                .assetReceiver(getAddress(0))
                .assetAmount(int1)
                .assetIndex(this.assetID)
                .note(this.note)
                .suggestedParams(this.params)
                .build();
        this.txn = tx;
        this.pk = getAddress(0);
    }
}
