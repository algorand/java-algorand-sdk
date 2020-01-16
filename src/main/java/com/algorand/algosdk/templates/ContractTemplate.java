/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.algorand.algosdk.templates;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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
	 *   BASE64 : 2 bytes
	 *   ADDRESS: 32 bytes
	 *   INT    : 1 byte
	 */
	abstract static class ParameterValue {
		private final int offset;
		private final byte[] value;

		protected ParameterValue(int offset, byte[] value) {
			this.value = value;
			this.offset = offset;
		}

		public byte[] toBytes() {
			return value;
		}

		public int getOffset() {
			return offset;
		}

		abstract public int placeholderSize();
	}

	public static class IntParameterValue extends ParameterValue {
		public IntParameterValue(int offset, int value) {
			super(offset, Logic.putUVarint(value));
		}

		public int placeholderSize() {
			return 1;
		}
	}

	public static class AddressParameterValue extends ParameterValue {
		public AddressParameterValue(int offset, String value) throws NoSuchAlgorithmException {
			super(offset, new Address(value).getBytes());
		}

		public AddressParameterValue(int offset, Address address) throws NoSuchAlgorithmException {
			super(offset, address.getBytes());
		}

		public AddressParameterValue(int offset, byte[] value) throws NoSuchAlgorithmException {
			super(offset, value);
		}

		public int placeholderSize() {
			return 32;
		}
	}

	public static class BytesParameterValue extends ParameterValue {
		public BytesParameterValue(int offset, String value) {
			this(offset, Encoder.decodeFromBase64(value));
		}

		public BytesParameterValue(int offset, byte[] value) {
			super(offset, convertToBytes(value));
		}

		public BytesParameterValue(int offset, Lease value) {
			this(offset, value.getBytes());
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
	 * @param values of the program parameters to inject to the program bytecode
	 * @return ContractTemplate with the address and the program with the given parameter values
	 * @throws NoSuchAlgorithmException
	 */
	protected static ContractTemplate inject(byte [] program, List<ParameterValue> values) throws NoSuchAlgorithmException {
		ArrayList<Byte> updatedProgram = new ArrayList<Byte>();

		int progIdx = 0;
		for (ParameterValue value : values) {
			while (progIdx < value.getOffset()) {
				updatedProgram.add(program[progIdx++]);
			}

			for (byte b : value.toBytes()) {
				updatedProgram.add(b);
			}
			progIdx += value.placeholderSize();
		}
		// append remainder of program.
		while (progIdx < program.length) {
			updatedProgram.add(program[progIdx++]);
		}

		byte [] updatedProgramByteArray = new byte[updatedProgram.size()];
		for (int x = 0; x < updatedProgram.size(); ++x) {
			updatedProgramByteArray[x] = updatedProgram.get(x);			
		}

		return new ContractTemplate(updatedProgramByteArray);
	}
}
