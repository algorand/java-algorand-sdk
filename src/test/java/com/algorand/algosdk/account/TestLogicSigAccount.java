package com.algorand.algosdk.account;

import com.algorand.algosdk.crypto.*;
import com.algorand.algosdk.crypto.Signature;
import com.algorand.algosdk.mnemonic.Mnemonic;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import org.bouncycastle.util.test.FixedSecureRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

import static org.assertj.core.api.Assertions.*;


public class TestLogicSigAccount {
    byte[] program;
    ArrayList<byte[]> args;
    String otherAddrStr, programHash;
    byte[] note;
    byte[] testVectorGenesisHash;
    final String SK_MNEMONIC = "olympic cricket tower model share zone grid twist sponsor avoid eight apology patient party success claim famous rapid donor pledge bomb mystery security ability often";
    Account singleDelegateAccount;
    KeyPair kp;

    MultisigAddress ma;
    KeyPair maKp0, maKp1, maKp2;

    private KeyPair mnemonicToKeyPair(String mnemonic) throws GeneralSecurityException {
        byte[] seed = Mnemonic.toKey(mnemonic);
        KeyPairGenerator gen = KeyPairGenerator.getInstance("Ed25519");
        gen.initialize(256, new FixedSecureRandom(seed));
        return gen.generateKeyPair();
    }

    @BeforeEach
    void setUp() throws GeneralSecurityException {
        program = new byte[]{0x01, 0x20, 0x01, 0x01, 0x22};
        args = new ArrayList<>();
        byte[] arg0 = {0x01};
        byte[] arg1 = {0x02, 0x03};
        args.add(arg0);
        args.add(arg1);
        note = new byte[]{(byte) 180, 81, 121, 57, (byte) 252, (byte) 250, (byte) 210, 113};
        otherAddrStr = "WTDCE2FEYM2VB5MKNXKLRSRDTSPR2EFTIGVH4GRW4PHGD6747GFJTBGT2A";
        programHash = "6Z3C3LDVWGMX23BMSYMANACQOSINPFIRF77H7N3AWJZYV6OH6GWTJKVMXY";
        testVectorGenesisHash = Base64.getDecoder().decode("JgsgCaCTqIaLeVhyL6XlRu3n7Rfk2FxMeK+wRSaQ7dI=");

        byte[] seed = Mnemonic.toKey(SK_MNEMONIC);
        singleDelegateAccount = new Account(seed);
        kp = mnemonicToKeyPair(SK_MNEMONIC);

        Address one = new Address("DN7MBMCL5JQ3PFUQS7TMX5AH4EEKOBJVDUF4TCV6WERATKFLQF4MQUPZTA");
        Address two = new Address("BFRTECKTOOE7A5LHCF3TTEOH2A7BW46IYT2SX5VP6ANKEXHZYJY77SJTVM");
        Address three = new Address("47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU");
        ma = new MultisigAddress(1, 2, Arrays.asList(
                new Ed25519PublicKey(one.getBytes()),
                new Ed25519PublicKey(two.getBytes()),
                new Ed25519PublicKey(three.getBytes())
        ));
        maKp0 = mnemonicToKeyPair("auction inquiry lava second expand liberty glass involve ginger illness length room item discover ahead table doctor term tackle cement bonus profit right above catch");
        maKp1 = mnemonicToKeyPair("since during average anxiety protect cherry club long lawsuit loan expand embark forum theory winter park twenty ball kangaroo cram burst board host ability left");
        maKp2 = mnemonicToKeyPair("advice pudding treat near rule blouse same whisper inner electric quit surface sunny dismiss leader blood seat clown cost exist hospital century reform able sponsor");
    }

    @Test
    public void testLogicSigAccount() throws Exception {
        // escrow logic-sig account
        LogicSigAccount lsaEscrow = new LogicSigAccount(program, args);
        assertThat(lsaEscrow.lsig).isEqualTo(new LogicsigSignature(program, args));
        assertThat(lsaEscrow.sigKey).isNull();
        assertThat(lsaEscrow.lsig.sig).isNull();
        assertThat(lsaEscrow.lsig.msig).isNull();
        assertThat(lsaEscrow.lsig.lmsig).isNull();

        // delegated single logic-sig account
        LogicSigAccount lsaDelegated = new LogicSigAccount(program, args, kp.getPrivate());
        byte[] expectedSigBytes = new byte[]{
                0x3e, 0x5, 0x3d, 0x39, 0x4d, (byte) 0xfb, 0x12, (byte) 0xbc, 0x65, 0x79,
                (byte) 0x9f, (byte) 0xea, 0x31, (byte) 0x8a, 0x7b, (byte) 0x8e, (byte) 0xa2,
                (byte) 0x51, (byte) 0x8b, 0x55, 0x2c, (byte) 0x8a, (byte) 0xbe, 0x6c, (byte) 0xd7,
                (byte) 0xa7, 0x65, 0x2d, (byte) 0xd8, (byte) 0xb0, 0x18, 0x7e, 0x21, 0x5, 0x2d,
                (byte) 0xb9, 0x24, 0x62, (byte) 0x89, 0x16, (byte) 0xe5, 0x61, 0x74, (byte) 0xcd,
                0xf, 0x19, (byte) 0xac, (byte) 0xb9, 0x6c, 0x45, (byte) 0xa4, 0x29, (byte) 0x91,
                (byte) 0x99, 0x11, 0x1d, (byte) 0xe4, 0x7c, (byte) 0xe4, (byte) 0xfc, 0x12,
                (byte) 0xec, (byte) 0xce, 0x2
        };
        assertThat(lsaDelegated.sigKey).isEqualTo(singleDelegateAccount.getEd25519PublicKey());
        assertThat(lsaDelegated.lsig.msig).isNull();
        assertThat(lsaDelegated.lsig.lmsig).isNull();
        assertThat(singleDelegateAccount.getEd25519PublicKey()).isEqualTo(lsaDelegated.sigKey);
        assertThat(lsaDelegated.lsig.sig).isEqualTo(new Signature(expectedSigBytes));

        // delegated multi logic-sig account
        LogicSigAccount lsaMultiDelegated = new LogicSigAccount(program, args, maKp0.getPrivate(), ma);
        assertThat(lsaMultiDelegated.sigKey).isNull();
        assertThat(lsaMultiDelegated.lsig.logic).isEqualTo(program);
        assertThat(lsaMultiDelegated.lsig.args).isEqualTo(args);
        lsaMultiDelegated.appendMultiSig(maKp1.getPrivate());

        assertThat(lsaMultiDelegated.lsig.msig).isNull(); // Legacy
        assertThat(lsaMultiDelegated.lsig.lmsig).isNotNull();
        assertThat(lsaMultiDelegated.lsig.lmsig.subsigs).hasSize(3);
        assertThat(lsaMultiDelegated.lsig.lmsig.subsigs.get(0).sig).isNotNull();
        assertThat(lsaMultiDelegated.lsig.lmsig.subsigs.get(1).sig).isNotNull();
        assertThat(lsaMultiDelegated.lsig.lmsig.subsigs.get(2).sig).isEqualTo(new Signature());

        // from: jq '.lsig' tests/resources/msig_delegated.txn | msgpacktool -e | base64
        String expectedBase64 = "g6NhcmeSxAEBxAICA6FsxAUBIAEBIqVsbXNpZ4Omc3Vic2lnk4KicGvEIBt+wLBL6mG3lpCX5sv0B+EIpwU1HQvJir6xIgmoq4F4oXPEQIwzZcSx0RNw8j9w13dGn+HZR3m/TY1kgXZJNe94TMx2V2zA4O/pwUb6YHba+s5V7przG3aOvDK07BosjD3AZwaConBrxCAJYzIJU3OJ8HVnEXc5kcfQPhtzyMT1K/av8BqiXPnCcaFzxEBPVtR92cCxahX1iGTp50PVMQkf969ssoHfNA0VOiNupdkXc9n/l2WO9+pj8Ddozf4ovorGgnrzca3ZhKc46uUNgaJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGjdGhyAqF2AQ==";
        byte[] encoded = Encoder.encodeToMsgPack(lsaMultiDelegated.lsig);
        String actualBase64 = Base64.getEncoder().encodeToString(encoded);
        assertThat(actualBase64).isEqualTo(expectedBase64);
    }

    @Test
    public void testLogicSigAccountFromLogicSig() throws IOException, NoSuchAlgorithmException {
        // escrow logic-sig account
        LogicsigSignature lsigNoKey = new LogicsigSignature(program, args);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new LogicSigAccount(lsigNoKey, singleDelegateAccount.getEd25519PublicKey()));
        LogicSigAccount lsaNoKey = new LogicSigAccount(lsigNoKey, null);
        assertThat(lsaNoKey.lsig).isEqualTo(lsigNoKey);
        assertThat(lsaNoKey.sigKey).isNull();

        // delegated single logic-sig account
        LogicsigSignature lsigDelegated = new LogicsigSignature(program, args);
        Account signerAccount = new Account(maKp0);
        signerAccount.signLogicsig(lsigDelegated);

        Assertions.assertThrows(IllegalArgumentException.class, () -> new LogicSigAccount(lsigDelegated, null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new LogicSigAccount(lsigDelegated, new Account(maKp2).getEd25519PublicKey()));
        LogicSigAccount lsaDelegated = new LogicSigAccount(lsigDelegated, signerAccount.getEd25519PublicKey());
        assertThat(lsaDelegated.lsig).isEqualTo(lsigDelegated);
        assertThat(lsaDelegated.sigKey).isEqualTo(signerAccount.getEd25519PublicKey());

        // delegated multi logic-sig account
        LogicsigSignature lsigMultiDelegated = new LogicsigSignature(program, args);
        Account signer0 = new Account(maKp0);
        signer0.signLogicsig(lsigMultiDelegated, ma);
        Account signer1 = new Account(maKp1);
        signer1.signLogicsig(lsigMultiDelegated, ma);

        Assertions.assertThrows(IllegalArgumentException.class, () -> new LogicSigAccount(lsigMultiDelegated, new Account(maKp2).getEd25519PublicKey()));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new LogicSigAccount(lsigMultiDelegated, new Account(maKp1).getEd25519PublicKey()));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new LogicSigAccount(lsigMultiDelegated, new Account(maKp0).getEd25519PublicKey()));
        LogicSigAccount lsaMultiDelegated = new LogicSigAccount(lsigMultiDelegated, null);
        assertThat(lsaMultiDelegated.lsig).isEqualTo(lsigMultiDelegated);
        assertThat(lsaMultiDelegated.sigKey).isNull();
    }

    @Test
    public void testLogicSigAccountAddress() throws GeneralSecurityException, IOException {
        // escrow logic-sig account
        LogicSigAccount lsaEscrow = new LogicSigAccount(program, args);
        Address expectedEscrowAddr = new Address("6Z3C3LDVWGMX23BMSYMANACQOSINPFIRF77H7N3AWJZYV6OH6GWTJKVMXY");
        assertThat(lsaEscrow.getAddress()).isEqualTo(expectedEscrowAddr);

        // delegated single logic-sig account
        LogicSigAccount lsaDelegated = new LogicSigAccount(program, args, kp.getPrivate());
        assertThat(lsaDelegated.getAddress()).isEqualTo(singleDelegateAccount.getAddress());

        // delegated multi logic-sig account'
        LogicSigAccount lsaMultiDelegated = new LogicSigAccount(program, args, maKp0.getPrivate(), ma);
        assertThat(lsaMultiDelegated.getAddress()).isEqualTo(ma.toAddress());
    }

    private void testSign(LogicSigAccount lsa, Address sender, String expectedTxid, Address expectedAuthAddr, byte[] expectedBytes)
            throws GeneralSecurityException, IOException {
        // older tests use empty genesis hash
        testSign(lsa, sender, expectedTxid, expectedAuthAddr, expectedBytes, null);
    }

    private void testSign(LogicSigAccount lsa, Address sender, String expectedTxid, Address expectedAuthAddr, byte[] expectedBytes, byte[] genesisHashBytes)
            throws GeneralSecurityException, IOException {
        Address to = new Address(otherAddrStr);

        BigInteger firstRound = BigInteger.valueOf(972508);

        Transaction tx = Transaction.PaymentTransactionBuilder()
                .sender(sender)
                .flatFee(BigInteger.valueOf(217000))
                .firstValid(firstRound)
                .lastValid(firstRound.longValue() + 1000)
                .note(note)
                .genesisID("testnet-v31.0")
                .genesisHash(new Digest(genesisHashBytes))
                .amount(BigInteger.valueOf(5000))
                .receiver(to)
                .build();

        SignedTransaction stx = lsa.signLogicSigTransaction(tx);
        byte[] stxBytes = Encoder.encodeToMsgPack(stx);
        assertThat(stxBytes).isEqualTo(expectedBytes);
        assertThat(stx.transactionID).isEqualTo(expectedTxid);
        assertThat(stx.authAddr).isEqualTo(expectedAuthAddr);
        assertThat(stx.lSig).isEqualTo(lsa.lsig);
        assertThat(stx.mSig).isEqualTo(new MultisigSignature());
        assertThat(stx.sig).isEqualTo(new Signature());
    }

    @Test
    public void testNoSigContractAddr() throws GeneralSecurityException, IOException {
        LogicSigAccount lsaNoSig = new LogicSigAccount(program, args);
        Address programAddr = new Address(programHash);
        String expectedTxID = "IL5UCKXGWBA2MQ4YYFQKYC3BFCWO2KHZSNZWVDIXOOZS3AWVIQDA";
        Address expectedAuthAddr = new Address();
        String expectedByteEncoded = "QKSGY43JM6BKGYLSM6JMIAIBYQBAEA5BNTCAKAJAAEASFI3UPBXITI3BNV2M2E4IUNTGKZOOAABU7KFCMZ3M4AAO23OKGZ3FN2WXIZLTORXGK5BNOYZTCLRQUJWHNTQAB3NMJJDON52GLRAIWRIXSOP47LJHDI3SMN3MIIFUYYRGRJGDGVIPLCTN2S4MUI44T4ORBM2BVJ7BUNXDZZQ7X7HZRKRXG3TEYQQPM5RNVR23DGL5NQWJMGAGQBIHJEGXSUIS77T7W5QLE44K7HD7DLNEOR4XAZNDOBQXS";
        byte[] expectedBytes = Encoder.decodeFromBase32StripPad(expectedByteEncoded);
        testSign(lsaNoSig, programAddr, expectedTxID, expectedAuthAddr, expectedBytes);
    }

    @Test
    public void testNoSigNotContractAddr() throws GeneralSecurityException, IOException {
        LogicSigAccount lsaNoSig = new LogicSigAccount(program, args);
        Address programAddr = new Address(programHash);
        String expectedTxID = "U4X24Q45MCZ6JSL343QNR3RC6AJO2NUDQXFCTNONIW3SU2AYQJOA";
        String expectedByteEncoded = "QOSGY43JM6BKGYLSM6JMIAIBYQBAEA5BNTCAKAJAAEASFJDTM5XHFRBA6Z3C3LDVWGMX23BMSYMANACQOSINPFIRF77H7N3AWJZYV6OH6GW2G5DYN2E2GYLNOTGRHCFDMZSWLTQAANH2RITGO3HAADWW3SRWOZLOVV2GK43UNZSXILLWGMYS4MFCNR3M4AAO3LCKI3TPORS4ICFUKF4TT7H22JY2G4TDO3CCBNGGEJUKJQZVKD2YU3OUXDFCHHE7DUILGQNKPYNDNY6OMH57Z6MKUNZW4ZGEEC2MMITIUTBTKUHVRJW5JOGKEOOJ6HIQWNA2U7Q2G3R44YP37T4YVJDUPFYGLI3QMF4Q";
        byte[] expectedBytes = Encoder.decodeFromBase32StripPad(expectedByteEncoded);
        testSign(lsaNoSig, new Address(otherAddrStr), expectedTxID, programAddr, expectedBytes);
    }

    @Test
    public void testSingleSigContractAddr() throws GeneralSecurityException, IOException {
        Account acc = new Account(kp.getPrivate());
        LogicSigAccount lsa = new LogicSigAccount(program, args, kp.getPrivate());
        String expectedTxID = "XPFTYDOV5RCL7K5EGTC32PSTRKFO3ITZIL64CRTYIJEPHXOTCCXA";
        String expectedByteEncoded = "QKSGY43JM6B2GYLSM6JMIAIBYQBAEA5BNTCAKAJAAEASFI3TNFT4IQB6AU6TSTP3CK6GK6M75IYYU64OUJIYWVJMRK7GZV5HMUW5RMAYPYQQKLNZERRISFXFMF2M2DYZVS4WYRNEFGIZSEI54R6OJ7AS5THAFI3UPBXITI3BNV2M2E4IUNTGKZOOAABU7KFCMZ3M4AAO23OKGZ3FN2WXIZLTORXGK5BNOYZTCLRQUJWHNTQAB3NMJJDON52GLRAIWRIXSOP47LJHDI3SMN3MIIFUYYRGRJGDGVIPLCTN2S4MUI44T4ORBM2BVJ7BUNXDZZQ7X7HZRKRXG3TEYQQF4Z2PDQFO53BXOGEY6YOHN725ESQZPE7CZEP2BBIWEY7DQVZ6UQVEOR4XAZNDOBQXS";
        byte[] expectedBytes = Encoder.decodeFromBase32StripPad(expectedByteEncoded);
        testSign(lsa, acc.getAddress(), expectedTxID, new Address(), expectedBytes);
    }

    @Test
    public void testSingleSigNotContractAddr() throws GeneralSecurityException, IOException {
        Account acc = new Account(kp.getPrivate());
        LogicSigAccount lsa = new LogicSigAccount(program, args, kp.getPrivate());
        String expectedTxID = "U4X24Q45MCZ6JSL343QNR3RC6AJO2NUDQXFCTNONIW3SU2AYQJOA";
        String expectedByteEncoded = "QOSGY43JM6B2GYLSM6JMIAIBYQBAEA5BNTCAKAJAAEASFI3TNFT4IQB6AU6TSTP3CK6GK6M75IYYU64OUJIYWVJMRK7GZV5HMUW5RMAYPYQQKLNZERRISFXFMF2M2DYZVS4WYRNEFGIZSEI54R6OJ7AS5THAFJDTM5XHFRBALZTU6HAK53WDO4MJR5Q4O37V2JFBS6J6FSI7UCCRMJR6HBLT5JBKG5DYN2E2GYLNOTGRHCFDMZSWLTQAANH2RITGO3HAADWW3SRWOZLOVV2GK43UNZSXILLWGMYS4MFCNR3M4AAO3LCKI3TPORS4ICFUKF4TT7H22JY2G4TDO3CCBNGGEJUKJQZVKD2YU3OUXDFCHHE7DUILGQNKPYNDNY6OMH57Z6MKUNZW4ZGEEC2MMITIUTBTKUHVRJW5JOGKEOOJ6HIQWNA2U7Q2G3R44YP37T4YVJDUPFYGLI3QMF4Q";
        byte[] expectedByte = Encoder.decodeFromBase32StripPad(expectedByteEncoded);
        Address expectedAuthAddr = acc.getAddress();
        testSign(lsa, new Address(otherAddrStr), expectedTxID, expectedAuthAddr, expectedByte);
    }

    @Test
    public void testMultiSigContractAddr() throws GeneralSecurityException, IOException {
        LogicSigAccount lsa = new LogicSigAccount(program, args, maKp0.getPrivate(), ma);
        lsa.appendMultiSig(maKp1.getPrivate());
        String expectedTxID = "UGGT5EZXG2OBPGWTEINC65UXIQ6UVAAOTNKRRCRAUCZH4FWJTVQQ";
        // from: msgpacktool -e < tests/resources/msig_delegated.txn | base64
        String expectedBase64 = "gqRsc2lng6NhcmeSxAEBxAICA6FsxAUBIAEBIqVsbXNpZ4Omc3Vic2lnk4KicGvEIBt+wLBL6mG3lpCX5sv0B+EIpwU1HQvJir6xIgmoq4F4oXPEQIwzZcSx0RNw8j9w13dGn+HZR3m/TY1kgXZJNe94TMx2V2zA4O/pwUb6YHba+s5V7przG3aOvDK07BosjD3AZwaConBrxCAJYzIJU3OJ8HVnEXc5kcfQPhtzyMT1K/av8BqiXPnCcaFzxEBPVtR92cCxahX1iGTp50PVMQkf969ssoHfNA0VOiNupdkXc9n/l2WO9+pj8Ddozf4ovorGgnrzca3ZhKc46uUNgaJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGjdGhyAqF2AaN0eG6Ko2FtdM0TiKNmZWXOAANPqKJmds4ADtbco2dlbq10ZXN0bmV0LXYzMS4womdoxCAmCyAJoJOohot5WHIvpeVG7eftF+TYXEx4r7BFJpDt0qJsds4ADtrEpG5vdGXECLRReTn8+tJxo3JjdsQgtMYiaKTDNVD1im3UuMojnJ8dELNBqn4aNuPOYfv8+Yqjc25kxCCNkrSJkAFzoE36Q1mjZmpq/OosQqBd2cH3PuulR4A36aR0eXBlo3BheQ==";
        byte[] expectedBytes = Base64.getDecoder().decode(expectedBase64);
        testSign(lsa, ma.toAddress(), expectedTxID, new Address(), expectedBytes, testVectorGenesisHash);

        SignedTransaction stx = Encoder.decodeFromMsgPack(expectedBytes, SignedTransaction.class);
        assertThat(stx.lSig).isNotNull();
        assertThat(stx.lSig.logic).isEqualTo(program);
        assertThat(stx.lSig.args).hasSize(2);
        assertThat(stx.lSig.args.get(0)).isEqualTo(new byte[] {0x01});
        assertThat(stx.lSig.args.get(1)).isEqualTo(new byte[] {0x02, 0x03});
        assertThat(stx.lSig.lmsig).isNotNull();
        assertThat(stx.lSig.lmsig.subsigs).hasSize(3);
        assertThat(stx.tx.sender).isEqualTo(ma.toAddress());

        assertThat(stx.lSig.verify(ma.toAddress())).isTrue();
    }

    @Test
    public void testMultiSigNotContractAddr() throws GeneralSecurityException, IOException {
        LogicSigAccount lsa = new LogicSigAccount(program, args, maKp0.getPrivate(), ma);
        lsa.appendMultiSig(maKp1.getPrivate());
        String expectedTxid = "DRBC5KBOYEUCL6L6H45GQSRKCCUTPNELUHUSQO4ZWCEODJEXQBBQ";
        // from: msgpacktool -e < tests/resources/msig_delegated_other.txn | base64
        String expectedBase64 = "g6Rsc2lng6NhcmeSxAEBxAICA6FsxAUBIAEBIqVsbXNpZ4Omc3Vic2lnk4KicGvEIBt+wLBL6mG3lpCX5sv0B+EIpwU1HQvJir6xIgmoq4F4oXPEQIwzZcSx0RNw8j9w13dGn+HZR3m/TY1kgXZJNe94TMx2V2zA4O/pwUb6YHba+s5V7przG3aOvDK07BosjD3AZwaConBrxCAJYzIJU3OJ8HVnEXc5kcfQPhtzyMT1K/av8BqiXPnCcaFzxEBPVtR92cCxahX1iGTp50PVMQkf969ssoHfNA0VOiNupdkXc9n/l2WO9+pj8Ddozf4ovorGgnrzca3ZhKc46uUNgaJwa8Qg5/D4TQaBHfnzHI2HixFV9GcdUaGFwgCQhmf0SVhwaKGjdGhyAqF2AaRzZ25yxCCNkrSJkAFzoE36Q1mjZmpq/OosQqBd2cH3PuulR4A36aN0eG6Ko2FtdM0TiKNmZWXOAANPqKJmds4ADtbco2dlbq10ZXN0bmV0LXYzMS4womdoxCAmCyAJoJOohot5WHIvpeVG7eftF+TYXEx4r7BFJpDt0qJsds4ADtrEpG5vdGXECLRReTn8+tJxo3JjdsQgtMYiaKTDNVD1im3UuMojnJ8dELNBqn4aNuPOYfv8+Yqjc25kxCC0xiJopMM1UPWKbdS4yiOcnx0Qs0Gqfho2485h+/z5iqR0eXBlo3BheQ==";
        byte[] expectedBytes = Base64.getDecoder().decode(expectedBase64);
        testSign(lsa, new Address(otherAddrStr), expectedTxid, ma.toAddress(), expectedBytes, testVectorGenesisHash);

        SignedTransaction stx = Encoder.decodeFromMsgPack(expectedBytes, SignedTransaction.class);
        assertThat(stx.lSig).isNotNull();
        assertThat(stx.lSig.logic).isEqualTo(program);
        assertThat(stx.lSig.args).hasSize(2);
        assertThat(stx.lSig.args.get(0)).isEqualTo(new byte[] {0x01});
        assertThat(stx.lSig.args.get(1)).isEqualTo(new byte[] {0x02, 0x03});
        assertThat(stx.lSig.lmsig).isNotNull();
        assertThat(stx.lSig.lmsig.subsigs).hasSize(3);
        assertThat(stx.authAddr).isEqualTo(ma.toAddress());
        assertThat(stx.tx.sender).isNotEqualTo(ma.toAddress());

        assertThat(stx.lSig.verify(ma.toAddress())).isTrue();
    }
}
