package com.algorand.algosdk.crypto;

import com.algorand.algosdk.util.TestUtil;

import java.util.ArrayList;
import java.util.Arrays;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.util.Encoder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class TestLogicsigSignature {
    @Test
    public void testLogicsigEmptyCreation() throws Exception {
        assertThatThrownBy(() -> new LogicsigSignature(null, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testLogicsigCreation() throws Exception {
        byte[] program = {
            0x01, 0x20, 0x01, 0x01, 0x22  // int 1
        };
        ArrayList<byte[]> args = new ArrayList<byte[]>();
        String programHash = "6Z3C3LDVWGMX23BMSYMANACQOSINPFIRF77H7N3AWJZYV6OH6GWTJKVMXY";
        Address sender = new Address(programHash);

        LogicsigSignature lsig = new LogicsigSignature(program);
        assertThat(lsig.logic).isEqualTo(program);
        assertThat(lsig.args).isNull();
        assertThat(lsig.sig).isNull();
        assertThat(lsig.msig).isNull();
        boolean verified = lsig.verify(sender);
        assertThat(verified).isTrue();
        assertThat(lsig.toAddress()).isEqualTo(sender);

        byte[] arg1 = {1, 2, 3};
        byte[] arg2 = {4, 5, 6};
        args.add(arg1);
        args.add(arg2);

        lsig = new LogicsigSignature(program, args);
        assertThat(lsig.logic).isEqualTo(program);
        assertThat(lsig.args).isEqualTo(args);
        assertThat(lsig.sig).isNull();
        assertThat(lsig.msig).isNull();
        verified = lsig.verify(sender);
        assertThat(verified).isTrue();
        assertThat(lsig.toAddress()).isEqualTo(sender);

        // check serialization
        byte[] outBytes = Encoder.encodeToMsgPack(lsig);
        LogicsigSignature lsig1 = Encoder.decodeFromMsgPack(outBytes, LogicsigSignature.class);
        assertThat(lsig).isEqualTo(lsig1);

        // check serialization with null args
        lsig = new LogicsigSignature(program);
        outBytes = Encoder.encodeToMsgPack(lsig);
        lsig1 = Encoder.decodeFromMsgPack(outBytes, LogicsigSignature.class);
        assertThat(lsig).isEqualTo(lsig1);

        // check modified program fails on verification
        program[3] = 2;
        lsig = new LogicsigSignature(program);
        verified = lsig.verify(sender);
        assertThat(verified).isFalse();
        TestUtil.serializeDeserializeCheck(lsig);
    }

    @Test
    public void testLogicsigInvalidProgramCreation() throws Exception {
        byte[] program = {
            0x7F, 0x20, 0x01, 0x01, 0x22
        };
        assertThatThrownBy(() -> new LogicsigSignature(program))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("unsupported version");
    }

    @Test
    public void testLogicsigSignature() throws Exception {
        byte[] program = {
            0x01, 0x20, 0x01, 0x01, 0x22  // int 1
        };

        LogicsigSignature lsig = new LogicsigSignature(program);
        Account account = new Account();
        lsig = account.signLogicsig(lsig);
        assertThat(lsig.logic).isEqualTo(program);
        assertThat(lsig.args).isNull();
        assertThat(lsig.sig)
                .isNotEqualTo(new Signature())
                .isNotNull();
        assertThat(lsig.msig).isNull();
        boolean verified = lsig.verify(account.getAddress());
        assertThat(verified).isTrue();

        // check serialization
        byte[] outBytes = Encoder.encodeToMsgPack(lsig);
        LogicsigSignature lsig1 = Encoder.decodeFromMsgPack(outBytes, LogicsigSignature.class);
        assertThat(lsig1).isEqualTo(lsig);
        TestUtil.serializeDeserializeCheck(lsig);
    }

    @Test
    public void testLogicsigMultisigSignature() throws Exception {
        byte[] program = {
            0x01, 0x20, 0x01, 0x01, 0x22  // int 1
        };

        Address one = new Address("DN7MBMCL5JQ3PFUQS7TMX5AH4EEKOBJVDUF4TCV6WERATKFLQF4MQUPZTA");
        Address two = new Address("BFRTECKTOOE7A5LHCF3TTEOH2A7BW46IYT2SX5VP6ANKEXHZYJY77SJTVM");
        Address three = new Address("47YPQTIGQEO7T4Y4RWDYWEKV6RTR2UNBQXBABEEGM72ESWDQNCQ52OPASU");
        MultisigAddress ma = new MultisigAddress(
            1, 2, Arrays.asList(
                new Ed25519PublicKey(one.getBytes()),
                new Ed25519PublicKey(two.getBytes()),
                new Ed25519PublicKey(three.getBytes())
            )
        );

        String mn1 = "auction inquiry lava second expand liberty glass involve ginger illness length room item discover ahead table doctor term tackle cement bonus profit right above catch";
        String mn2 = "since during average anxiety protect cherry club long lawsuit loan expand embark forum theory winter park twenty ball kangaroo cram burst board host ability left";
        Account acc1 = new Account(mn1);
        Account acc2 = new Account(mn2);
        Account account = new Account();

        LogicsigSignature lsig = new LogicsigSignature(program);
        lsig = acc1.signLogicsig(lsig, ma);
        assertThat(lsig.logic).isEqualTo(program);
        assertThat(lsig.args).isNull();
        assertThat(lsig.sig).isNull();
        assertThat(lsig.msig)
                .isNotEqualTo(new MultisigSignature())
                .isNotNull();
        boolean verified = lsig.verify(ma.toAddress());
        assertThat(verified).isFalse();

        LogicsigSignature lsigLambda = lsig;
        assertThatThrownBy(() -> account.appendToLogicsig(lsigLambda))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Multisig account does not contain this secret key");

        lsig = acc2.appendToLogicsig(lsig);
        verified = lsig.verify(ma.toAddress());
        assertThat(verified).isTrue();

        // Add a single signature and ensure it fails
        LogicsigSignature lsig1 = new LogicsigSignature(program);
        lsig1 = account.signLogicsig(lsig1);
        lsig.sig = lsig1.sig;
        verified = lsig.verify(ma.toAddress());
        assertThat(verified).isFalse();
        verified = lsig.verify(account.getAddress());
        assertThat(verified).isFalse();

        // Remove and ensure it still works
        lsig.sig = null;
        verified = lsig.verify(ma.toAddress());
        assertThat(verified).isTrue();

        // check serialization
        byte[] outBytes = Encoder.encodeToMsgPack(lsig);
        LogicsigSignature lsig2 = Encoder.decodeFromMsgPack(outBytes, LogicsigSignature.class);
        assertThat(lsig2).isEqualTo(lsig);
        verified = lsig2.verify(ma.toAddress());
        assertThat(verified).isTrue();
        TestUtil.serializeDeserializeCheck(lsig2);
    }
}
