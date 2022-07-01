package com.algorand.algosdk.transaction;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.*;
import com.algorand.algosdk.mnemonic.Mnemonic;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.util.TestUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestTransaction {
    private static Account DEFAULT_ACCOUNT = initializeDefaultAccount();

    private static Account initializeDefaultAccount() {
        try {
            String mnemonic = "awful drop leaf tennis indoor begin mandate discover uncle seven only coil atom any hospital uncover make any climb actor armed measure need above hundred";
            return new Account(mnemonic);
        } catch (Exception e) {
            fail("Failed to initialize static default account.");
        }
        return null;
    }

    private void assertEqual(Transaction actual, Transaction expected) {
        assertThat(actual).isEqualTo(expected);
        assertThat(actual.sender).isEqualTo(expected.sender);
        assertThat(actual.receiver).isEqualTo(expected.receiver);
        assertThat(actual.amount).isEqualTo(expected.amount);
        assertThat(actual.lastValid).isEqualTo(expected.lastValid);
        assertThat(actual.genesisHash).isEqualTo(expected.genesisHash);
    }

    @Test
    public void testSerialization() throws Exception {
        Address from = new Address("VKM6KSCTDHEM6KGEAMSYCNEGIPFJMHDSEMIRAQLK76CJDIRMMDHKAIRMFQ");
        Address to = new Address("CQW2QBBUW5AGFDXMURQBRJN2AM3OHHQWXXI4PEJXRCVTEJ3E5VBTNRTEAE");
        Transaction tx = Transaction.PaymentTransactionBuilder()
                .sender(from)
                .receiver(to)
                .amount(100)
                .firstValid(301)
                .lastValid(1300)
                .genesisHash(new Digest())
                .build();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        out = new ObjectOutputStream(bos);
        out.writeObject(tx);
        out.flush();
        byte[] outBytes = bos.toByteArray();
        bos.close();
        ByteArrayInputStream bis = new ByteArrayInputStream(outBytes);
        ObjectInput in = null;
        in = new ObjectInputStream(bis);
        Transaction o = (Transaction) in.readObject();
        in.close();
        assertEqual(o, tx);
    }

    @Test
    public void testPaymentTransactionJsonSerialization() throws Exception {
        Address from = new Address("VKM6KSCTDHEM6KGEAMSYCNEGIPFJMHDSEMIRAQLK76CJDIRMMDHKAIRMFQ");
        Address to = new Address("CQW2QBBUW5AGFDXMURQBRJN2AM3OHHQWXXI4PEJXRCVTEJ3E5VBTNRTEAE");
        Transaction tx = Transaction.PaymentTransactionBuilder()
                .sender(from)
                .receiver(to)
                .amount(100)
                .firstValid(301)
                .lastValid(1300)
                .genesisHash(new Digest())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String transactionJson = objectMapper.writeValueAsString(tx);
        final Transaction transaction = objectMapper.readValue(transactionJson, Transaction.class);
        assertEqual(tx, transaction);
        String transactionJson1 = objectMapper.writeValueAsString(transaction);
        assertThat(transactionJson).isEqualTo(transactionJson1);
    }

    @Test
    public void testApplicationTransactionJsonSerialization() throws Exception {
        Address from = new Address("VKM6KSCTDHEM6KGEAMSYCNEGIPFJMHDSEMIRAQLK76CJDIRMMDHKAIRMFQ");
        Transaction tx = Transaction.ApplicationUpdateTransactionBuilder()
                .sender(from)
                .applicationId(100000L)
                .firstValid(301)
                .lastValid(1300)
                .genesisHash(new Digest())
                .foreignApps(Arrays.asList(10L))
                .boxReferences(Arrays.asList(new BoxReference(10L, "name".getBytes())))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String transactionJson = objectMapper.writeValueAsString(tx);
        final Transaction transaction = objectMapper.readValue(transactionJson, Transaction.class);
        assertEqual(tx, transaction);
        String transactionJson1 = objectMapper.writeValueAsString(transaction);
        assertThat(transactionJson).isEqualTo(transactionJson1);
    }

    @Test
    public void testApplicationTransactionWithBoxes() throws Exception {
        Address from = new Address("VKM6KSCTDHEM6KGEAMSYCNEGIPFJMHDSEMIRAQLK76CJDIRMMDHKAIRMFQ");
        Transaction tx = Transaction.ApplicationUpdateTransactionBuilder()
                .sender(from)
                .applicationId(100000L)
                .firstValid(301)
                .lastValid(1300)
                .genesisHash(new Digest())
                .foreignApps(Arrays.asList(10L, 100000L))
                .boxReferences(Arrays.asList(
                    new BoxReference(10L, "name".getBytes()),
                    new BoxReference(100000L, "name2".getBytes()),
                    new BoxReference(0L, "name3".getBytes())))
                .build();

        assert(tx.boxReferences.size() == 3);
        assert(tx.boxReferences.get(0).appIdx == 1);
        assert(tx.boxReferences.get(1).appIdx == 2);
        assert(tx.boxReferences.get(2).appIdx == 0);
    }

    @Test
    public void testSerializationMsgpack() throws Exception {
        Address from = new Address("VKM6KSCTDHEM6KGEAMSYCNEGIPFJMHDSEMIRAQLK76CJDIRMMDHKAIRMFQ");
        Address to = new Address("CQW2QBBUW5AGFDXMURQBRJN2AM3OHHQWXXI4PEJXRCVTEJ3E5VBTNRTEAE");
        Transaction tx = Transaction.PaymentTransactionBuilder()
                .sender(from)
                .receiver(to)
                .amount(100)
                .firstValid(301)
                .lastValid(1300)
                .genesisHash(new Digest())
                .build();

        byte[] outBytes = Encoder.encodeToMsgPack(tx);
        Transaction o = Encoder.decodeFromMsgPack(outBytes, Transaction.class);

        assertEqual(o, tx);
    }

    @Test
    public void testMetadaHashBuilderMethods() throws Exception {
        // Test that the following 3 builder methods returns the same transaction
        //    metadataHash, metadataHashUTF8, and metadataHashB64
        // when given as input the same metadata hash
        // and that it is different when the input is different

        String metadataHashUTF8          = "Hello! This is the metadata hash";
        String metadataHashUTF8Different = "Hi! I am another metadata hash..";
        byte[] metadataHashBytes = metadataHashUTF8.getBytes(StandardCharsets.UTF_8);
        // The value below is the base64 of metadataHashUTF8
        String metadataHashB64 = "SGVsbG8hIFRoaXMgaXMgdGhlIG1ldGFkYXRhIGhhc2g=";

        Address addr = new Address("BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4");
        byte[] gh = Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=");
        Address sender = addr;
        Address manager = addr;
        Address reserve = addr;
        Address freeze = addr;
        Address clawback = addr;

        Transaction txBytes = Transaction.AssetCreateTransactionBuilder()
                .sender(sender)
                .fee(10)
                .firstValid(322575)
                .lastValid(323575)
                .genesisHash(gh)
                .assetTotal(100)
                .assetDecimals(5)
                .assetUnitName("tst")
                .assetName("testcoin")
                .url("https://example.com")
                .metadataHash(metadataHashBytes)
                .manager(manager)
                .reserve(reserve)
                .freeze(freeze)
                .clawback(clawback)
                .build();

        Transaction txUTF8 = Transaction.AssetCreateTransactionBuilder()
                .sender(sender)
                .fee(10)
                .firstValid(322575)
                .lastValid(323575)
                .genesisHash(gh)
                .assetTotal(100)
                .assetDecimals(5)
                .assetUnitName("tst")
                .assetName("testcoin")
                .url("https://example.com")
                .metadataHashUTF8(metadataHashUTF8)
                .manager(manager)
                .reserve(reserve)
                .freeze(freeze)
                .clawback(clawback)
                .build();

        Transaction txUTF8Different = Transaction.AssetCreateTransactionBuilder()
                .sender(sender)
                .fee(10)
                .firstValid(322575)
                .lastValid(323575)
                .genesisHash(gh)
                .assetTotal(100)
                .assetDecimals(5)
                .assetUnitName("tst")
                .assetName("testcoin")
                .url("https://example.com")
                .metadataHashUTF8(metadataHashUTF8Different)
                .manager(manager)
                .reserve(reserve)
                .freeze(freeze)
                .clawback(clawback)
                .build();

        Transaction txB64 = Transaction.AssetCreateTransactionBuilder()
                .sender(sender)
                .fee(10)
                .firstValid(322575)
                .lastValid(323575)
                .genesisHash(gh)
                .assetTotal(100)
                .assetDecimals(5)
                .assetUnitName("tst")
                .assetName("testcoin")
                .url("https://example.com")
                .metadataHashB64(metadataHashB64)
                .manager(manager)
                .reserve(reserve)
                .freeze(freeze)
                .clawback(clawback)
                .build();

        assertThat(txUTF8).isEqualTo(txBytes);
        assertThat(txUTF8).isEqualTo(txB64);
        assertThat(txUTF8).isNotEqualTo(txUTF8Different);
    }

    private void createAssetTest(int numDecimal, String goldenString) throws Exception {
        Address addr = new Address("BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4");
        byte[] gh = Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=");
        Address sender = addr;
        Address manager = addr;
        Address reserve = addr;
        Address freeze = addr;
        Address clawback = addr;
        byte[] metadataHash = Base64.getDecoder().decode("ZkFDUE80blJnTzU1ajFuZEFLM1c2U2djNEFQa2N5Rmg=");
        String url_96 = new String(new char[96]).replace("\0", "w");

        Transaction tx = Transaction.AssetCreateTransactionBuilder()
                .sender(sender)
                .fee(10)
                .firstValid(322575)
                .lastValid(323575)
                .genesisHash(gh)
                .assetTotal(100)
                .assetDecimals(numDecimal)
                .assetUnitName("tst")
                .assetName("testcoin")
                .url(url_96)
                .metadataHash(metadataHash)
                .manager(manager)
                .reserve(reserve)
                .freeze(freeze)
                .clawback(clawback)
                .build();

        AssetParams expectedParams = new AssetParams(
                BigInteger.valueOf(100),
                numDecimal,
                false,
                "tst",
                "testcoin",
                url_96,
                metadataHash,
                manager,
                reserve,
                freeze,
                clawback
        );
        assertThat(expectedParams).isEqualToComparingFieldByField(tx.assetParams);

        SignedTransaction stx = DEFAULT_ACCOUNT.signTransaction(tx);

        String encodedOut = Encoder.encodeToBase64(Encoder.encodeToMsgPack(stx));

        SignedTransaction decodedOut = Encoder.decodeFromMsgPack(encodedOut, SignedTransaction.class);

        assertThat(decodedOut).isEqualTo(stx);
        assertThat(encodedOut).isEqualTo(goldenString);
        assertThat(decodedOut).isEqualTo(stx);
        TestUtil.serializeDeserializeCheck(stx);
    }

    @Test
    public void testAssetParamsValidation() throws Exception {
        Address addr = new Address("BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4");
        Address manager = addr;
        Address reserve = addr;
        Address freeze = addr;
        Address clawback = addr;
        byte[] tooShortMetadataHash = "fACPO4nRgO55j1ndAK3W6Sgc4APkcyF".getBytes(StandardCharsets.UTF_8);
        byte[] tooLongMetadataHash = "fACPO4nRgO55j1ndAK3W6Sgc4APkcyFhfACPO4nRgO55j1ndAK3W6Sgc4APkcyFh".getBytes(StandardCharsets.UTF_8);
        byte[] normalMetadataHash = Base64.getDecoder().decode("IsHwwbvsnx9X5HMW1U468AXzbvRWk8VffLw0NQrmEq0=");
        String url_100 = new String(new char[100]).replace("\0", "w");

        assertThatThrownBy(() -> new AssetParams(
                BigInteger.valueOf(100),
                3,
                false,
                "tst",
                "testcoin",
                url_100,
                normalMetadataHash,
                manager,
                reserve,
                freeze,
                clawback))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("url length must be between 0 and 96 characters (inclusive).");

        assertThatThrownBy(() -> new AssetParams(
                BigInteger.valueOf(100),
                3,
                false,
                "tst",
                "testcoin",
                "website",
                tooShortMetadataHash,
                manager,
                reserve,
                freeze,
                clawback))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("asset metadataHash must have 32 bytes");

        assertThatThrownBy(() -> new AssetParams(
                BigInteger.valueOf(100),
                3,
                false,
                "tst",
                "testcoin",
                "website",
                tooLongMetadataHash,
                manager,
                reserve,
                freeze,
                clawback))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("asset metadataHash must have 32 bytes");
    }

    @Test
    public void testMakeAssetCreateTxn() throws Exception {
        createAssetTest(0, "gqNzaWfEQF2hf4SoXzT2Wyp5p3CYbMoX2xmrRrKfxxqSa8uhSXv+qDpAIdvFVlQhkNXpz8j7m7M/9xjPBSXSUxnYuzbgvQijdHhuh6RhcGFyiaJhbcQgZkFDUE80blJnTzU1ajFuZEFLM1c2U2djNEFQa2N5RmiiYW6odGVzdGNvaW6iYXXZYHd3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d6FjxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFmxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFtxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFyxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aF0ZKJ1bqN0c3SjZmVlzRM4omZ2zgAE7A+iZ2jEIEhjtRiks8hOyBDyLU8QgcsPcfBZp6wg3sYvf3DlCToiomx2zgAE7/ejc25kxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aR0eXBlpGFjZmc=");
    }

    @Test
    public void testMakeAssetCreateTxnWithDecimals() throws Exception {
        createAssetTest(1, "gqNzaWfEQGMrl8xmewPhzZL2aLc7Wt+ZI8Ff1HXxA+xO11kbChe/tPIC5scCHv6M+cgTLl1nG9Z0406ScpoeNoIDpcLPXgujdHhuh6RhcGFyiqJhbcQgZkFDUE80blJnTzU1ajFuZEFLM1c2U2djNEFQa2N5RmiiYW6odGVzdGNvaW6iYXXZYHd3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d3d6FjxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aJkYwGhZsQgCfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f2hbcQgCfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f2hcsQgCfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f2hdGSidW6jdHN0o2ZlZc0TYKJmds4ABOwPomdoxCBIY7UYpLPITsgQ8i1PEIHLD3HwWaesIN7GL39w5Qk6IqJsds4ABO/3o3NuZMQgCfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f2kdHlwZaRhY2Zn");
    }

    @Test
    public void testSerializationAssetConfig() throws Exception {
        Address addr = new Address("BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4");
        byte[] gh = Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=");
        Address sender = addr;
        Address manager = addr;
        Address reserve = addr;
        Address freeze = addr;
        Address clawback = addr;

        Transaction tx = Transaction.AssetConfigureTransactionBuilder()
                .sender(sender)
                .fee(10)
                .firstValid(322575)
                .lastValid(323575)
                .genesisHash(gh)
                .assetIndex(1234)
                .manager(manager)
                .reserve(reserve)
                .freeze(freeze)
                .clawback(clawback)
                .build();

        SignedTransaction stx = DEFAULT_ACCOUNT.signTransaction(tx);

        String encodedOutBytes = Encoder.encodeToBase64(Encoder.encodeToMsgPack(stx));
        String goldenString = "gqNzaWfEQBBkfw5n6UevuIMDo2lHyU4dS80JCCQ/vTRUcTx5m0ivX68zTKyuVRrHaTbxbRRc3YpJ4zeVEnC9Fiw3Wf4REwejdHhuiKRhcGFyhKFjxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFmxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFtxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aFyxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aRjYWlkzQTSo2ZlZc0NSKJmds4ABOwPomdoxCBIY7UYpLPITsgQ8i1PEIHLD3HwWaesIN7GL39w5Qk6IqJsds4ABO/3o3NuZMQgCfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f2kdHlwZaRhY2Zn";

        SignedTransaction o = Encoder.decodeFromMsgPack(encodedOutBytes, SignedTransaction.class);

        assertThat(encodedOutBytes).isEqualTo(goldenString);
        assertThat(o).isEqualTo(stx);
        TestUtil.serializeDeserializeCheck(stx);
    }

    @Test
    public void testAssetConfigStrictEmptyAddressChecking() throws NoSuchAlgorithmException {
        Address addr = new Address("BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4");
        byte[] gh = Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=");
        Address sender = addr;
        Address manager = addr;
        Address reserve = addr;
        Address freeze = addr;

        assertThatThrownBy(() -> Transaction.AssetConfigureTransactionBuilder()
                .sender(sender)
                .fee(10)
                .firstValid(322575)
                .lastValid(323575)
                .genesisHash(gh)
                .assetIndex(1234)
                .manager(manager)
                .reserve(reserve)
                .freeze(freeze)
                .clawback(new Address())
                .build())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("strict empty address checking requested but empty or default address supplied to one or more manager addresses");

        assertThatThrownBy(() -> Transaction.AssetConfigureTransactionBuilder()
                .sender(sender)
                .fee(10)
                .firstValid(322575)
                .lastValid(323575)
                .genesisHash(gh)
                .assetIndex(1234)
                .manager(manager)
                .reserve(reserve)
                .freeze(freeze)
                .clawback(new Address())
                .build())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("strict empty address checking requested but empty or default address supplied to one or more manager addresses");
    }

    @Test
    public void testSerializationAssetFreeze() throws Exception {
        Address addr = new Address("BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4");
        byte[] gh = Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=");
        Address sender = addr;
        Address target = addr;
        BigInteger assetFreezeID = BigInteger.valueOf(1);
        boolean freezeState = true;
        Transaction tx = Transaction.AssetFreezeTransactionBuilder()
                .sender(sender)
                .freezeTarget(target)
                .freezeState(freezeState)
                .fee(10)
                .firstValid(322575)
                .lastValid(323576)
                .genesisHash(gh)
                .assetIndex(assetFreezeID)
                .build();
        SignedTransaction stx = DEFAULT_ACCOUNT.signTransaction(tx);
        String encodedOutBytes = Encoder.encodeToBase64(Encoder.encodeToMsgPack(stx));
        SignedTransaction o = Encoder.decodeFromMsgPack(encodedOutBytes, SignedTransaction.class);
        String goldenString = "gqNzaWfEQAhru5V2Xvr19s4pGnI0aslqwY4lA2skzpYtDTAN9DKSH5+qsfQQhm4oq+9VHVj7e1rQC49S28vQZmzDTVnYDQGjdHhuiaRhZnJ6w6RmYWRkxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aRmYWlkAaNmZWXNCRqiZnbOAATsD6JnaMQgSGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiKibHbOAATv+KNzbmTEIAn70nYsCPhsWua/bdenqQHeZnXXUOB+jFx2mGR9tuH9pHR5cGWkYWZyeg==";

        assertThat(encodedOutBytes).isEqualTo(goldenString);
        assertThat(o).isEqualTo(stx);
        TestUtil.serializeDeserializeCheck(stx);
    }

    @Test
    public void testPaymentTransaction() throws Exception {
        final String FROM_SK = "advice pudding treat near rule blouse same whisper inner electric quit surface sunny dismiss leader blood seat clown cost exist hospital century reform able sponsor";
        byte[] seed = Mnemonic.toKey(FROM_SK);
        Account account = new Account(seed);

        Address fromAddr = new Address("47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU");
        Address toAddr = new Address("PNWOET7LLOWMBMLE4KOCELCX6X3D3Q4H2Q4QJASYIEOF7YIPPQBG3YQ5YI");
        Address closeTo = new Address("IDUTJEUIEVSMXTU4LGTJWZ2UE2E6TIODUKU6UW3FU3UKIQQ77RLUBBBFLA");
        String goldenString = "gqNzaWfEQPhUAZ3xkDDcc8FvOVo6UinzmKBCqs0woYSfodlmBMfQvGbeUx3Srxy3dyJDzv7rLm26BRv9FnL2/AuT7NYfiAWjdHhui6NhbXTNA+ilY2xvc2XEIEDpNJKIJWTLzpxZpptnVCaJ6aHDoqnqW2Wm6KRCH/xXo2ZlZc0EmKJmds0wsqNnZW6sZGV2bmV0LXYzMy4womdoxCAmCyAJoJOohot5WHIvpeVG7eftF+TYXEx4r7BFJpDt0qJsds00mqRub3RlxAjqABVHQ2y/lqNyY3bEIHts4k/rW6zAsWTinCIsV/X2PcOH1DkEglhBHF/hD3wCo3NuZMQg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGkdHlwZaNwYXk=";

        BigInteger firstValidRound = BigInteger.valueOf(12466);
        BigInteger lastValidRound = BigInteger.valueOf(13466);
        BigInteger amountToSend = BigInteger.valueOf(1000);
        byte[] note = Encoder.decodeFromBase64("6gAVR0Nsv5Y=");
        String genesisID = "devnet-v33.0";
        Digest genesisHash = new Digest(Encoder.decodeFromBase64("JgsgCaCTqIaLeVhyL6XlRu3n7Rfk2FxMeK+wRSaQ7dI="));

        Transaction tx = Transaction.PaymentTransactionBuilder()
                .sender(fromAddr)
                .fee(4)
                .firstValid(firstValidRound)
                .lastValid(lastValidRound)
                .note(note)
                .genesisID(genesisID)
                .genesisHash(genesisHash)
                .amount(amountToSend)
                .receiver(toAddr)
                .closeRemainderTo(closeTo)
                .build();

        byte[] outBytes = Encoder.encodeToMsgPack(tx);
        Transaction o = Encoder.decodeFromMsgPack(outBytes, Transaction.class);
        assertThat(o).isEqualTo(tx);

        SignedTransaction stx = account.signTransaction(tx);
        String encodedOutBytes = Encoder.encodeToBase64(Encoder.encodeToMsgPack(stx));

        SignedTransaction stxDecoded = Encoder.decodeFromMsgPack(encodedOutBytes, SignedTransaction.class);
        assertThat(stxDecoded).isEqualTo(stx);
        assertThat(encodedOutBytes).isEqualTo(goldenString);
        TestUtil.serializeDeserializeCheck(stx);
    }

    @Test
    public void testTransactionGroupLimit() throws IOException, NoSuchAlgorithmException {

        Transaction[] txs = new Transaction[TxGroup.MAX_TX_GROUP_SIZE + 1];

        boolean gotExpectedException = false;
        Digest gid = null;
        try {
            gid = TxGroup.computeGroupID(txs);
        } catch (IllegalArgumentException e) {
            gotExpectedException = true;
            assertThat(e.getMessage()).isEqualTo("max group size is " + TxGroup.MAX_TX_GROUP_SIZE);
        } catch (IOException e) {
            throw e;
        }
        assertThat(gotExpectedException).isEqualTo(true);
        assertThat(gid == null);

    }

    @Test
    public void testTransactionGroup() throws Exception {
        Address from = new Address("UPYAFLHSIPMJOHVXU2MPLQ46GXJKSDCEMZ6RLCQ7GWB5PRDKJUWKKXECXI");
        Address to = new Address("UPYAFLHSIPMJOHVXU2MPLQ46GXJKSDCEMZ6RLCQ7GWB5PRDKJUWKKXECXI");
        BigInteger fee = BigInteger.valueOf(1000);
        BigInteger amount = BigInteger.valueOf(2000);
        String genesisID = "devnet-v1.0";
        Digest genesisHash = new Digest(Encoder.decodeFromBase64("sC3P7e2SdbqKJK0tbiCdK9tdSpbe6XeCGKdoNzmlj0E"));
        BigInteger firstRound1 = BigInteger.valueOf(710399);
        byte[] note1 = Encoder.decodeFromBase64("wRKw5cJ0CMo=");

        Transaction tx1 = Transaction.PaymentTransactionBuilder()
                .sender(from)
                .flatFee(fee)
                .firstValid(firstRound1)
                .lastValid(firstRound1.longValue() + 1000)
                .note(note1)
                .genesisID(genesisID)
                .genesisHash(genesisHash)
                .amount(amount)
                .receiver(to)
                .build();

        BigInteger firstRound2 = BigInteger.valueOf(710515);
        byte[] note2 = Encoder.decodeFromBase64("dBlHI6BdrIg=");

        Transaction tx2 = Transaction.PaymentTransactionBuilder()
                .sender(from)
                .flatFee(fee)
                .firstValid(firstRound2)
                .lastValid(firstRound2.longValue() + 1000)
                .note(note2)
                .genesisID(genesisID)
                .genesisHash(genesisHash)
                .amount(amount)
                .receiver(to)
                .build();

        // check serialization/deserialization without group field
        assertThat(Encoder.decodeFromMsgPack(Encoder.encodeToMsgPack(tx1), Transaction.class)).isEqualTo(tx1);
        assertThat(Encoder.decodeFromMsgPack(Encoder.encodeToMsgPack(tx2), Transaction.class)).isEqualTo(tx2);

        String goldenTx1 = "gaN0eG6Ko2FtdM0H0KNmZWXNA+iiZnbOAArW/6NnZW6rZGV2bmV0LXYxLjCiZ2jEILAtz+3tknW6iiStLW4gnSvbXUqW3ul3ghinaDc5pY9Bomx2zgAK2uekbm90ZcQIwRKw5cJ0CMqjcmN2xCCj8AKs8kPYlx63ppj1w5410qkMRGZ9FYofNYPXxGpNLKNzbmTEIKPwAqzyQ9iXHremmPXDnjXSqQxEZn0Vih81g9fEak0spHR5cGWjcGF5";
        String goldenTx2 = "gaN0eG6Ko2FtdM0H0KNmZWXNA+iiZnbOAArXc6NnZW6rZGV2bmV0LXYxLjCiZ2jEILAtz+3tknW6iiStLW4gnSvbXUqW3ul3ghinaDc5pY9Bomx2zgAK21ukbm90ZcQIdBlHI6BdrIijcmN2xCCj8AKs8kPYlx63ppj1w5410qkMRGZ9FYofNYPXxGpNLKNzbmTEIKPwAqzyQ9iXHremmPXDnjXSqQxEZn0Vih81g9fEak0spHR5cGWjcGF5";

        // goal clerk send dumps unsigned transaction as signed with empty signature in order to save tx type
        SignedTransaction stx1 = new SignedTransaction(tx1, new Signature(), new MultisigSignature(), new LogicsigSignature(), tx1.txID());
        SignedTransaction stx2 = new SignedTransaction(tx2, new Signature(), new MultisigSignature(), new LogicsigSignature(), tx2.txID());

        assertThat(Encoder.encodeToBase64(Encoder.encodeToMsgPack(stx1))).isEqualTo(goldenTx1);
        assertThat(Encoder.encodeToBase64(Encoder.encodeToMsgPack(stx2))).isEqualTo(goldenTx2);
        TestUtil.serializeDeserializeCheck(stx1);
        TestUtil.serializeDeserializeCheck(stx2);


        Digest gid = TxGroup.computeGroupID(tx1, tx2);
        tx1.assignGroupID(gid);
        tx2.assignGroupID(gid);

        // check serialization/deserialization with group field set
        assertThat(Encoder.decodeFromMsgPack(Encoder.encodeToMsgPack(tx1), Transaction.class)).isEqualTo(tx1);
        assertThat(Encoder.decodeFromMsgPack(Encoder.encodeToMsgPack(tx2), Transaction.class)).isEqualTo(tx2);

        // goal clerk group sets Group to every transaction and concatenate them in output file
        // simulating that behavior here
        String goldenTxg = "gaN0eG6Lo2FtdM0H0KNmZWXNA+iiZnbOAArW/6NnZW6rZGV2bmV0LXYxLjCiZ2jEILAtz+3tknW6iiStLW4gnSvbXUqW3ul3ghinaDc5pY9Bo2dycMQgLiQ9OBup9H/bZLSfQUH2S6iHUM6FQ3PLuv9FNKyt09SibHbOAAra56Rub3RlxAjBErDlwnQIyqNyY3bEIKPwAqzyQ9iXHremmPXDnjXSqQxEZn0Vih81g9fEak0so3NuZMQgo/ACrPJD2Jcet6aY9cOeNdKpDERmfRWKHzWD18RqTSykdHlwZaNwYXmBo3R4boujYW10zQfQo2ZlZc0D6KJmds4ACtdzo2dlbqtkZXZuZXQtdjEuMKJnaMQgsC3P7e2SdbqKJK0tbiCdK9tdSpbe6XeCGKdoNzmlj0GjZ3JwxCAuJD04G6n0f9tktJ9BQfZLqIdQzoVDc8u6/0U0rK3T1KJsds4ACttbpG5vdGXECHQZRyOgXayIo3JjdsQgo/ACrPJD2Jcet6aY9cOeNdKpDERmfRWKHzWD18RqTSyjc25kxCCj8AKs8kPYlx63ppj1w5410qkMRGZ9FYofNYPXxGpNLKR0eXBlo3BheQ==";
        stx1 = new SignedTransaction(tx1, new Signature(), new MultisigSignature(), new LogicsigSignature(), tx1.txID());
        stx2 = new SignedTransaction(tx2, new Signature(), new MultisigSignature(), new LogicsigSignature(), tx2.txID());
        byte[] stx1Enc = Encoder.encodeToMsgPack(stx1);
        byte[] stx2Enc = Encoder.encodeToMsgPack(stx2);
        byte[] concat = Arrays.copyOf(stx1Enc, stx1Enc.length + stx2Enc.length);
        System.arraycopy(stx2Enc, 0, concat, stx1Enc.length, stx2Enc.length);

        assertThat(Encoder.encodeToBase64(concat)).isEqualTo(goldenTxg);

        // check assignGroupID
        Transaction[] result = TxGroup.assignGroupID(tx1, tx2);
        assertThat(result).hasSize(2);

        result = TxGroup.assignGroupID(from, tx1, tx2);
        assertThat(result).hasSize(2);


        result = TxGroup.assignGroupID(to, tx1, tx2);
        assertThat(result).hasSize(0);
    }

    @Test
    public void testTransactionGroupEmpty() throws IOException {
        assertThatThrownBy(() -> TxGroup.computeGroupID())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("empty transaction list");
    }

    @Test
    public void testTransactionGroupNull() throws IOException {
        assertThatThrownBy(() -> TxGroup.computeGroupID())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("empty transaction list");
    }

    @Test
    public void testMakeAssetAcceptanceTxn() throws Exception {

        Address addr = new Address("BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4");
        byte[] gh = Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=");
        Address recipient = addr;

        BigInteger assetIndex = BigInteger.valueOf(1);
        BigInteger firstValidRound = BigInteger.valueOf(322575);
        BigInteger lastValidRound = BigInteger.valueOf(323575);

        Transaction tx = Transaction.AssetAcceptTransactionBuilder()
                .acceptingAccount(recipient)
                .fee(10)
                .firstValid(firstValidRound)
                .lastValid(lastValidRound)
                .genesisHash(gh)
                .assetIndex(assetIndex)
                .build();
        byte[] outBytes = Encoder.encodeToMsgPack(tx);
        Transaction o = Encoder.decodeFromMsgPack(outBytes, Transaction.class);
        assertThat(o).isEqualTo(tx);

        /*  Example from: go-algorand-sdk/transaction/transaction_test.go
        {
            "sig:b64": "nuras5PxJv/AHQXzuV37XMymvFWViptRt76jPRYzrcVy0iL4r15gVKpPbpcFnhGvf5VMmkET4ksqzydy2X2GCA==",
            "txn": {
              "arcv:b64": "CfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f0=",
              "fee": 2280,
              "fv": 322575,
              "gh:b64": "SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=",
              "lv": 323575,
              "snd:b64": "CfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f0=",
              "type": "axfer",
              "xaid": 1
            }
          }
         */
        SignedTransaction stx = DEFAULT_ACCOUNT.signTransaction(tx);
        String encodedOutBytes = Encoder.encodeToBase64(Encoder.encodeToMsgPack(stx));
        String goldenString = "gqNzaWfEQJ7q2rOT8Sb/wB0F87ld+1zMprxVlYqbUbe+oz0WM63FctIi+K9eYFSqT26XBZ4Rr3+VTJpBE+JLKs8nctl9hgijdHhuiKRhcmN2xCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aNmZWXNCOiiZnbOAATsD6JnaMQgSGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiKibHbOAATv96NzbmTEIAn70nYsCPhsWua/bdenqQHeZnXXUOB+jFx2mGR9tuH9pHR5cGWlYXhmZXKkeGFpZAE=";

        SignedTransaction stxDecoded = Encoder.decodeFromMsgPack(encodedOutBytes, SignedTransaction.class);
        assertThat(stxDecoded).isEqualTo(stx);
        assertThat(encodedOutBytes).isEqualTo(goldenString);
        TestUtil.serializeDeserializeCheck(stx);
    }


    @Test
    public void testMakeAssetTransferTxn() throws Exception {

        Address addr = new Address("BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4");
        byte[] gh = Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=");
        Address sender = addr;
        Address recipient = addr;
        Address closeAssetsTo = addr;

        BigInteger assetIndex = BigInteger.valueOf(1);
        BigInteger firstValidRound = BigInteger.valueOf(322575);
        BigInteger lastValidRound = BigInteger.valueOf(323576);
        BigInteger amountToSend = BigInteger.valueOf(1);

        Transaction tx = Transaction.AssetTransferTransactionBuilder()
                .sender(sender)
                .assetReceiver(recipient)
                .assetCloseTo(closeAssetsTo)
                .assetAmount(amountToSend)
                .flatFee(10)
                .firstValid(firstValidRound)
                .lastValid(lastValidRound)
                .genesisHash(gh)
                .assetIndex(assetIndex)
                .build();


        Account.setFeeByFeePerByte(tx, BigInteger.valueOf(10));
        byte[] outBytes = Encoder.encodeToMsgPack(tx);
        Transaction o = Encoder.decodeFromMsgPack(outBytes, Transaction.class);
        assertThat(o).isEqualTo(tx);

        /*
         * Golden from: go-algorand-sdk/transaction/transaction_test.go
            {
              "sig:b64": "2QSzdZ18WrohAol0XWfT+FtX3Bouy+iPL2kzVPh+/B8w12MZAPL4t56y5BR9BVOd4kPhV8w/vMrHg5SUi1uvBA==",
              "txn": {
                "aamt": 1,
                "aclose:b64": "CfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f0=",
                "arcv:b64": "CfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f0=",
                "fee": 2750,
                "fv": 322575,
                "gh:b64": "SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=",
                "lv": 323576,
                "snd:b64": "CfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f0=",
                "type": "axfer",
                "xaid": 1
              }
            }
         */
        SignedTransaction stx = DEFAULT_ACCOUNT.signTransaction(tx);
        String encodedOutBytes = Encoder.encodeToBase64(Encoder.encodeToMsgPack(stx));
        String goldenString = "gqNzaWfEQNkEs3WdfFq6IQKJdF1n0/hbV9waLsvojy9pM1T4fvwfMNdjGQDy+LeesuQUfQVTneJD4VfMP7zKx4OUlItbrwSjdHhuiqRhYW10AaZhY2xvc2XEIAn70nYsCPhsWua/bdenqQHeZnXXUOB+jFx2mGR9tuH9pGFyY3bEIAn70nYsCPhsWua/bdenqQHeZnXXUOB+jFx2mGR9tuH9o2ZlZc0KvqJmds4ABOwPomdoxCBIY7UYpLPITsgQ8i1PEIHLD3HwWaesIN7GL39w5Qk6IqJsds4ABO/4o3NuZMQgCfvSdiwI+Gxa5r9t16epAd5mdddQ4H6MXHaYZH224f2kdHlwZaVheGZlcqR4YWlkAQ==";

        SignedTransaction stxDecoded = Encoder.decodeFromMsgPack(encodedOutBytes, SignedTransaction.class);

        assertThat(stxDecoded).isEqualTo(stx);
        assertThat(encodedOutBytes).isEqualTo(goldenString);
        TestUtil.serializeDeserializeCheck(stx);
    }

    @Test
    public void testMakeAssetRevocationTransaction() throws Exception {

        Address addr = new Address("BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4");
        byte[] gh = Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=");
        Address revoker = addr;
        Address revokeFrom = addr;
        Address receiver = addr;

        BigInteger assetIndex = BigInteger.valueOf(1);
        BigInteger firstValidRound = BigInteger.valueOf(322575);
        BigInteger lastValidRound = BigInteger.valueOf(323575);
        BigInteger amountToSend = BigInteger.valueOf(1);

        Transaction tx = Transaction.AssetClawbackTransactionBuilder()
                .sender(revoker)
                .assetClawbackFrom(revokeFrom)
                .assetReceiver(receiver)
                .assetAmount(amountToSend)
                .flatFee(10)
                .firstValid(firstValidRound)
                .lastValid(lastValidRound)
                .genesisHash(gh)
                .assetIndex(assetIndex)
                .build();


        Account.setFeeByFeePerByte(tx, BigInteger.valueOf(10));
        byte[] outBytes = Encoder.encodeToMsgPack(tx);
        Transaction o = Encoder.decodeFromMsgPack(outBytes, Transaction.class);
        assertThat(o).isEqualTo(tx);

        SignedTransaction stx = DEFAULT_ACCOUNT.signTransaction(tx);
        String encodedOutBytes = Encoder.encodeToBase64(Encoder.encodeToMsgPack(stx));
        String goldenString = "gqNzaWfEQHsgfEAmEHUxLLLR9s+Y/yq5WeoGo/jAArCbany+7ZYwExMySzAhmV7M7S8+LBtJalB4EhzEUMKmt3kNKk6+vAWjdHhuiqRhYW10AaRhcmN2xCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aRhc25kxCAJ+9J2LAj4bFrmv23Xp6kB3mZ111Dgfoxcdphkfbbh/aNmZWXNCqqiZnbOAATsD6JnaMQgSGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiKibHbOAATv96NzbmTEIAn70nYsCPhsWua/bdenqQHeZnXXUOB+jFx2mGR9tuH9pHR5cGWlYXhmZXKkeGFpZAE=";
        SignedTransaction stxDecoded = Encoder.decodeFromMsgPack(encodedOutBytes, SignedTransaction.class);

        assertThat(stxDecoded).isEqualTo(stx);
        assertThat(encodedOutBytes).isEqualTo(goldenString);
        TestUtil.serializeDeserializeCheck(stx);
    }

    @Test
    public void testEncoding() throws Exception {
        Address addr1 = new Address("726KBOYUJJNE5J5UHCSGQGWIBZWKCBN4WYD7YVSTEXEVNFPWUIJ7TAEOPM");
        Address addr2 = new Address("42NJMHTPFVPXVSDGA6JGKUV6TARV5UZTMPFIREMLXHETRKIVW34QFSDFRE");
        Account account1 = new Account(Encoder.decodeFromBase64("cv8E0Ln24FSkwDgGeuXKStOTGcze5u8yldpXxgrBxumFPYdMJymqcGoxdDeyuM8t6Kxixfq0PJCyJP71uhYT7w=="));

        String lease = "f4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGk=";
        Transaction txn = Transaction.PaymentTransactionBuilder()
                .sender(account1.getAddress())
                .fee(Account.MIN_TX_FEE_UALGOS.longValue() * 10)
                .firstValid(12345)
                .lastValid(12346)
                .genesisHashB64("f4OxZX/x/FO5LcGBSKHWXfwtSx+j1ncoSt3SABJtkGk=")
                .amount(5000)
                .receiver(addr1)
                .closeRemainderTo(addr2)
                .leaseB64(lease)
                .build();

        byte[] packed = Encoder.encodeToMsgPack(txn);
        Transaction txnDecoded = Encoder.decodeFromMsgPack(packed, Transaction.class);
        assertThat(txnDecoded.lease).isEqualTo(txn.lease);
        assertThat(txnDecoded.lease).isEqualTo(Encoder.decodeFromBase64(lease));
        assertThat(txnDecoded).isEqualTo(txn);
    }

    @Test
    public void testTransactionWithLease() throws Exception {

        final String FROM_SK = "advice pudding treat near rule blouse same whisper inner electric quit surface sunny dismiss leader blood seat clown cost exist hospital century reform able sponsor";
        byte[] seed = Mnemonic.toKey(FROM_SK);
        Account account = new Account(seed);

        Address fromAddr = new Address("47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU");
        Address toAddr = new Address("PNWOET7LLOWMBMLE4KOCELCX6X3D3Q4H2Q4QJASYIEOF7YIPPQBG3YQ5YI");
        Address closeTo = new Address("IDUTJEUIEVSMXTU4LGTJWZ2UE2E6TIODUKU6UW3FU3UKIQQ77RLUBBBFLA");
        String goldenString = "gqNzaWfEQOMmFSIKsZvpW0txwzhmbgQjxv6IyN7BbV5sZ2aNgFbVcrWUnqPpQQxfPhV/wdu9jzEPUU1jAujYtcNCxJ7ONgejdHhujKNhbXTNA+ilY2xvc2XEIEDpNJKIJWTLzpxZpptnVCaJ6aHDoqnqW2Wm6KRCH/xXo2ZlZc0FLKJmds0wsqNnZW6sZGV2bmV0LXYzMy4womdoxCAmCyAJoJOohot5WHIvpeVG7eftF+TYXEx4r7BFJpDt0qJsds00mqJseMQgAQIDBAECAwQBAgMEAQIDBAECAwQBAgMEAQIDBAECAwSkbm90ZcQI6gAVR0Nsv5ajcmN2xCB7bOJP61uswLFk4pwiLFf19j3Dh9Q5BIJYQRxf4Q98AqNzbmTEIOfw+E0GgR358xyNh4sRVfRnHVGhhcIAkIZn9ElYcGihpHR5cGWjcGF5";

        BigInteger firstValidRound = BigInteger.valueOf(12466);
        BigInteger lastValidRound = BigInteger.valueOf(13466);
        BigInteger amountToSend = BigInteger.valueOf(1000);
        byte[] note = Encoder.decodeFromBase64("6gAVR0Nsv5Y=");
        String genesisID = "devnet-v33.0";
        Digest genesisHash = new Digest(Encoder.decodeFromBase64("JgsgCaCTqIaLeVhyL6XlRu3n7Rfk2FxMeK+wRSaQ7dI="));
        byte[] lease = {1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4};

        Transaction tx = Transaction.PaymentTransactionBuilder()
                .sender(fromAddr)
                .fee(4)
                .firstValid(firstValidRound)
                .lastValid(lastValidRound)
                .note(note)
                .genesisID(genesisID)
                .genesisHash(genesisHash)
                .amount(amountToSend)
                .receiver(toAddr)
                .closeRemainderTo(closeTo)
                .lease(lease)
                .build();
        byte[] outBytes = Encoder.encodeToMsgPack(tx);
        Transaction o = Encoder.decodeFromMsgPack(outBytes, Transaction.class);
        assertThat(o).isEqualTo(tx);

        SignedTransaction stx = account.signTransaction(tx);
        String encodedOutBytes = Encoder.encodeToBase64(Encoder.encodeToMsgPack(stx));
        SignedTransaction stxDecoded = Encoder.decodeFromMsgPack(encodedOutBytes, SignedTransaction.class);

        assertThat(stxDecoded).isEqualTo(stx);
        assertThat(encodedOutBytes).isEqualTo(goldenString);
        TestUtil.serializeDeserializeCheck(stx);
    }

    @Test
    public void EmptyByteArraysShouldBeRejected() throws Exception {
        Address fromAddr = new Address("47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU");
        Address toAddr = new Address("PNWOET7LLOWMBMLE4KOCELCX6X3D3Q4H2Q4QJASYIEOF7YIPPQBG3YQ5YI");

        Transaction tx = Transaction.PaymentTransactionBuilder()
                .sender(fromAddr)
                .fee(4)
                .firstValid(1)
                .lastValid(10)
                .amount(1)
                .genesisHash(new Digest())
                .receiver(toAddr)
                .note(new byte[]{})
                .lease(new byte[]{})
                .build();

        assertThat(tx.note).isNull();
        assertThat(tx.lease).isNull();
    }

    @Nested
    class TestFees {
        @Test
        public void TxnCannotHaveBothFeeAndFlatFee() throws Exception {
            Address fromAddr = new Address("47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU");
            Address toAddr = new Address("PNWOET7LLOWMBMLE4KOCELCX6X3D3Q4H2Q4QJASYIEOF7YIPPQBG3YQ5YI");

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                Transaction.PaymentTransactionBuilder()
                        .sender(fromAddr)
                        .fee(4)
                        .flatFee(4)
                        .firstValid(1)
                        .lastValid(10)
                        .amount(1)
                        .genesisHash(new Digest())
                        .receiver(toAddr)
                        .note(new byte[]{})
                        .lease(new byte[]{})
                        .build();
            });

            String expected = "Cannot set both fee and flatFee.";
            String actual = exception.getMessage();

            assertThat(actual.contains(expected));
        }

        @Test
        public void TxnUsingFlatFee() throws Exception {
            Address fromAddr = new Address("47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU");
            Address toAddr = new Address("PNWOET7LLOWMBMLE4KOCELCX6X3D3Q4H2Q4QJASYIEOF7YIPPQBG3YQ5YI");

            Transaction tx = Transaction.PaymentTransactionBuilder()
                    .sender(fromAddr)
                    .flatFee(1)
                    .firstValid(1)
                    .lastValid(10)
                    .amount(1)
                    .genesisHash(new Digest())
                    .receiver(toAddr)
                    .note(new byte[]{})
                    .lease(new byte[]{})
                    .build();

            assertThat(tx.fee).isEqualTo(1);
        }

        @Test
        public void TxnZeroFlatFeeNotOverriddenWithMinFee() throws Exception {
            Address fromAddr = new Address("47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU");
            Address toAddr = new Address("PNWOET7LLOWMBMLE4KOCELCX6X3D3Q4H2Q4QJASYIEOF7YIPPQBG3YQ5YI");

            Transaction tx = Transaction.PaymentTransactionBuilder()
                    .sender(fromAddr)
                    .flatFee(0)
                    .firstValid(1)
                    .lastValid(10)
                    .amount(1)
                    .genesisHash(new Digest())
                    .receiver(toAddr)
                    .note(new byte[]{})
                    .lease(new byte[]{})
                    .build();

            assertThat(tx.fee).isEqualTo(0);
        }

        @Test
        public void TxnEstimatedFeeOverridenWithMinFee() throws Exception {
            Address addr = new Address("BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4");
            byte[] gh = Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=");
            Address sender = addr;
            Address recipient = addr;
            Address closeAssetsTo = addr;

            BigInteger assetIndex = BigInteger.valueOf(1);
            BigInteger firstValidRound = BigInteger.valueOf(322575);
            BigInteger lastValidRound = BigInteger.valueOf(323576);
            BigInteger amountToSend = BigInteger.valueOf(1);

            Transaction tx = Transaction.AssetTransferTransactionBuilder()
                    .sender(sender)
                    .assetReceiver(recipient)
                    .assetCloseTo(closeAssetsTo)
                    .assetAmount(amountToSend)
                    .fee(0)
                    .firstValid(firstValidRound)
                    .lastValid(lastValidRound)
                    .genesisHash(gh)
                    .assetIndex(assetIndex)
                    .build();
            assertThat(tx.fee).isEqualTo(1000);
        }

        @Test
        public void TxnEstimatedDefaultFee() throws Exception {
            Address addr = new Address("BH55E5RMBD4GYWXGX5W5PJ5JAHPGM5OXKDQH5DC4O2MGI7NW4H6VOE4CP4");
            byte[] gh = Encoder.decodeFromBase64("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=");
            Address sender = addr;
            Address recipient = addr;
            Address closeAssetsTo = addr;

            BigInteger assetIndex = BigInteger.valueOf(1);
            BigInteger firstValidRound = BigInteger.valueOf(322575);
            BigInteger lastValidRound = BigInteger.valueOf(323576);
            BigInteger amountToSend = BigInteger.valueOf(1);

            Transaction tx = Transaction.AssetTransferTransactionBuilder()
                    .sender(sender)
                    .assetReceiver(recipient)
                    .assetCloseTo(closeAssetsTo)
                    .assetAmount(amountToSend)
                    .firstValid(firstValidRound)
                    .lastValid(lastValidRound)
                    .genesisHash(gh)
                    .assetIndex(assetIndex)
                    .build();
            assertThat(tx.fee).isEqualTo(1000);
        }
    }

}
