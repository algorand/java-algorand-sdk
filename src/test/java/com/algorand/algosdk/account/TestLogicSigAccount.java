package com.algorand.algosdk.account;

import com.algorand.algosdk.crypto.*;
import com.algorand.algosdk.crypto.Signature;
import com.algorand.algosdk.mnemonic.Mnemonic;
import org.bouncycastle.util.test.FixedSecureRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;


public class TestLogicSigAccount {
    byte[] program;
    ArrayList<byte[]> args;
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
        assertThat(singleDelegateAccount.getEd25519PublicKey()).isEqualTo(lsaDelegated.sigKey);
        assertThat(lsaDelegated.lsig.sig).isEqualTo(new Signature(expectedSigBytes));

        // delegated multi logic-sig account
        LogicSigAccount lsaMultiDelegated = new LogicSigAccount(program, args, maKp0.getPrivate(), ma);
        assertThat(lsaMultiDelegated.sigKey).isNull();
        assertThat(lsaMultiDelegated.lsig.logic).isEqualTo(program);
        assertThat(lsaMultiDelegated.lsig.args).isEqualTo(args);
        lsaMultiDelegated.appendMultiSig(maKp1.getPrivate());

        byte[] sig0bytes = new byte[]{
                0x49, 0x13, (byte) 0xb8, 0x5, (byte) 0xd1, (byte) 0x9e, 0x7f, 0x2c, 0x10, (byte) 0x80, (byte) 0xf6,
                0x33, 0x7e, 0x18, 0x54, (byte) 0xa7, (byte) 0xce, (byte) 0xea, (byte) 0xee, 0x10, (byte) 0xdd, (byte) 0xbd,
                0x13, 0x65, (byte) 0x84, (byte) 0xbf, (byte) 0x93, (byte) 0xb7, 0x5f, 0x30, 0x63, 0x15, (byte) 0x91,
                (byte) 0xca, 0x23, 0xc, (byte) 0xed, (byte) 0xef, 0x23, (byte) 0xd1, 0x74, 0x1b, 0x52, (byte) 0x9d,
                (byte) 0xb0, (byte) 0xff, (byte) 0xef, 0x37, 0x54, (byte) 0xd6, 0x46, (byte) 0xf4, (byte) 0xb5, 0x61,
                (byte) 0xfc, (byte) 0x8b, (byte) 0xbc, 0x2d, 0x7b, 0x4e, 0x63, 0x5c, (byte) 0xbd, 0x2
        };
        Signature sig0Expected = new Signature(sig0bytes);
        assertThat(lsaMultiDelegated.lsig.msig.subsigs.get(0).sig).isEqualTo(sig0Expected);

        byte[] sig1bytes = new byte[]{
                0x64, (byte) 0xbc, 0x55, (byte) 0xdb, (byte) 0xed, (byte) 0x91, (byte) 0xa2, 0x41, (byte) 0xd4, 0x2a,
                (byte) 0xb6, 0x60, (byte) 0xf7, (byte) 0xe1, 0x4a, (byte) 0xb9, (byte) 0x99, (byte) 0x9a, 0x52,
                (byte) 0xb3, (byte) 0xb1, 0x71, 0x58, (byte) 0xce, (byte) 0xfc, 0x3f, 0x4f, (byte) 0xe7,
                (byte) 0xcb, 0x22, 0x41, 0x14, (byte) 0xad, (byte) 0xa9, 0x3d, 0x5e, (byte) 0x84, 0x5, 0x2, 0xa, 0x17,
                (byte) 0xa6, 0x69, (byte) 0x83, 0x3, 0x22, 0x4e, (byte) 0x86, (byte) 0xa3, (byte) 0x8b, 0x6a, 0x36,
                (byte) 0xc5, 0x54, (byte) 0xbe, 0x20, 0x50, (byte) 0xff, (byte) 0xd3, (byte) 0xee, (byte) 0xa8,
                (byte) 0xb3, 0x4, 0x9
        };
        Signature sig1Expected = new Signature(sig1bytes);
        assertThat(lsaMultiDelegated.lsig.msig.subsigs.get(1).sig).isEqualTo(sig1Expected);
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
}
