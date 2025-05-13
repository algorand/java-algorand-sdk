package com.algorand.algosdk.unit;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.crypto.Signature;
import com.algorand.algosdk.util.Encoder;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

public class TealSign {
    private Rekeying shared;
    private byte[] data;
    private Address contractAddress;
    private Signature sig;

    public TealSign(Rekeying shared) {
        this.shared = shared;
    }

    @Given("base64 encoded data to sign {string}")
    public void data_to_sign(String data) {
        this.data = Encoder.decodeFromBase64(data);
    }

    @Given("base64 encoded private key {string}")
    public void seed_for_private_key(String key) throws GeneralSecurityException {
        byte[] seed = Encoder.decodeFromBase64(key);
        shared.account = new Account(seed);
    }

    @Given("base64 encoded program {string}")
    public void encoded_program(String program) throws GeneralSecurityException {
        byte[] prog = Encoder.decodeFromBase64(program);
        LogicsigSignature lsig = new LogicsigSignature(prog);
        contractAddress = lsig.toAddress();
    }

    @Given("program hash {string}")
    public void program_hash(String hash) throws GeneralSecurityException {
        contractAddress = new Address(hash);
    }

    @When("I perform tealsign")
    public void perform_teal_sign() throws NoSuchAlgorithmException, IOException {
        sig = shared.account.tealSign(data, contractAddress);
    }


    @Then("the signature should be equal to {string}")
    public void the_signature_should_equal_the_golden(String golden) throws IOException {
        byte[] expected = Encoder.decodeFromBase64(golden);
        assertThat(sig.getBytes()).isEqualTo(expected);
    }
}
