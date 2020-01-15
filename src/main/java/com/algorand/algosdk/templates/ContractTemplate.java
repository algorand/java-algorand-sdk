/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.algorand.algosdk.templates;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.logic.Logic;
import com.algorand.algosdk.transaction.Lease;
import com.algorand.algosdk.util.Encoder;

public class ContractTemplate {

	/**
	 * Values are appended to a program at specific offsets. Depending on the type the source template has placeholders
	 * of differing sizes. The values are used to generate a byte array appropriate for the type it represents, for
	 * example some values may include a length in addition to the data. In addition the value knows how large the
	 * placeholder value is, this placeholder size may differ between different Algorand SDK implementations.
     *
	 * Placeholder sizes are the following:
	 * 		BASE64 : 2 bytes
	 * 		ADDRESS: 32 bytes
	 * 		INT    : 1 byte
	 */
	abstract static class ParameterValue {
        private final byte[] value;

        protected ParameterValue(byte[] value) {
        	this.value = value;
		}

		public byte[] toBytes() {
			return value;
		}
		abstract public int placeholderSize();
	}

	public static class IntParameterValue extends ParameterValue {
		public IntParameterValue(int value) {
			super(Logic.putUVarint(value));
		}

		public int placeholderSize() {
			return 1;
		}
	}

	public static class AddressParameterValue extends ParameterValue {
		public AddressParameterValue(String value) throws NoSuchAlgorithmException {
			super(new Address(value).getBytes());
		}

		public AddressParameterValue(Address address) throws NoSuchAlgorithmException {
			super(address.getBytes());
		}

		public AddressParameterValue(byte[] value) throws NoSuchAlgorithmException {
			super(value);
        }

		public int placeholderSize() {
			return 32;
		}
	}

	public static class BytesParameterValue extends ParameterValue {
		public BytesParameterValue(String value) {
			this(Encoder.decodeFromBase64(value));
		}

		public BytesParameterValue(byte[] value) {
			super(convertToBytes(value));
		}

		public BytesParameterValue(Lease value) {
			this(value.getBytes());
		}

		private static byte[] convertToBytes(byte[] value) {
			byte[] len = Logic.putUVarint(value.length);
			byte[] result = new byte[len.length + value.length];
			System.arraycopy(len, 0, result, 0, len.length);
			System.arraycopy(value, 0, result, len.length, value.length);
			return result;
		}

		public int placeholderSize() {
			return 2;
		}
	}

	public Address address;
	public byte[] program;


	/**
	 * Initialize a contract template.
	 *
	 * @param prog base64 encoded program.
	 */
	public ContractTemplate(String prog) throws NoSuchAlgorithmException {
		this(Encoder.decodeFromBase64(prog));
	}

	/**
	 * Initialize a contract template.
	 *
	 * @param prog bytes of program.
	 */
	public ContractTemplate(byte[] prog) throws NoSuchAlgorithmException {
		this(new LogicsigSignature(prog));
	}

    /**
     * Initialize a contract template.
     *
     * @param lsig the contract's LogicsigSignature.
     */
    public ContractTemplate(LogicsigSignature lsig) throws NoSuchAlgorithmException {
        address = lsig.toAddress();
        program = lsig.logic;
    }

	/**
	 * @param program is compiled TEAL program
	 * @param offsets are the position in the program bytecode of the program parameters
	 * @param values of the program parameters to inject to the program bytecode
	 * @return ContractTemplate with the address and the program with the given parameter values
	 * @throws NoSuchAlgorithmException
	 */
	protected static ContractTemplate inject(byte [] program, int [] offsets, ParameterValue[] values) throws NoSuchAlgorithmException {
		if (offsets.length != values.length) {
			throw new RuntimeException("offsets and values should have the same number of elements");
		}
		
		int paramIdx = 0;
		ArrayList<Byte> updatedProgram = new ArrayList<Byte>();

		for (int progIdx = 0; progIdx < program.length; ) {
			if (paramIdx < offsets.length && offsets[paramIdx] == progIdx) {
				ParameterValue value = values[paramIdx];
				++paramIdx;
				for (byte b : value.toBytes()) {
					updatedProgram.add(b);
				}
				progIdx += value.placeholderSize();
			} else {
				updatedProgram.add(program[progIdx]);
				progIdx += 1;
			}
		}

		byte [] updatedProgramByteArray = new byte[updatedProgram.size()];
		for (int x = 0; x < updatedProgram.size(); ++x) {
			updatedProgramByteArray[x] = updatedProgram.get(x);			
		}

		return new ContractTemplate(updatedProgramByteArray);
	}
}
