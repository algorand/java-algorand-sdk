package com.algorand.algosdk.unit;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.PaymentTransactionBuilder;
import com.algorand.algosdk.builder.transaction.TransactionBuilder;
import com.algorand.algosdk.transaction.SignedTransaction;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Rekeying {
	
	private TransactionBuilder transactionBuilder;
	private Account account;
	private SignedTransaction signedTransaction;
	
	@Given("payment transaction parameters {int} {int} {int} {string} {string} {string} {int} {string} {string}")	
	public void payment_transaction_parameters(
			Integer fee, Integer fv, Integer lv, String gh, 
			String to, String close, Integer amt, String gen, String note) {
		
		transactionBuilder = PaymentTransactionBuilder.Builder()
				.fee(fee)
				.firstValid(fv)
				.lastValid(lv)
				.genesisHashB64(gh)
				.receiver(to)
				.closeRemainderTo(close)
				.amount(amt)
				.genesisID(gen)
				.noteUTF8(note);
	}

	@Given("mnemonic for private key {string}")
	public void mnemonic_for_private_key(String mn) {
		try {
			account = new Account(mn);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}

	@When("I create the flat fee payment transaction")
	public void i_create_the_flat_fee_payment_transaction() {
		transactionBuilder.sender(account.getAddress());
	}

	@When("I add a rekeyTo field with address {string}")
	public void i_add_a_rekeyTo_field_with_address(String rekeyTo) {
		transactionBuilder.rekey(rekeyTo);
	}

	@When("I sign the transaction with the private key")
	public void i_sign_the_transaction_with_the_private_key() {
		try {
			signedTransaction = account.signTransaction(transactionBuilder.build());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	@Then("the signed transaction should equal the golden {string}")
	public void the_signed_transaction_should_equal_the_golden(String golden) {
		signedTransaction.toString().equals(golden);
	}

	@When("I set the from address to {string}")
	public void i_set_the_from_address_to(String fromAddress) {
		transactionBuilder.sender(fromAddress);
	}
}
