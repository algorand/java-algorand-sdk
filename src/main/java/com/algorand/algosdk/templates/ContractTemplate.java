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
import com.algorand.algosdk.util.Encoder;

public class ContractTemplate {

	/**
	 * Values are appended to a program at specific offsets. Depending on the type the source template has placeholders
	 * of differing sizes, this offset is returned by the "updateProgram" method and is used to traverse the template.
	 *
	 * Note: Placeholder sizes may differ between SDK implementations.
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
			super(putUVarint(value));
		}

		public int placeholderSize() {
			return 1;
		}
	}

	public static class AddressParameterValue extends ParameterValue {
		public AddressParameterValue(String value) throws NoSuchAlgorithmException {
			super(new Address(value).getBytes());
		}

		public int placeholderSize() {
			return 32;
		}
	}

	public static class Base64ParameterValue extends ParameterValue {
		public Base64ParameterValue(String value) {
			this(Encoder.decodeFromBase64(value));
		}

		public Base64ParameterValue(byte[] value) {
			super(convertToBytes(value));
		}

		private static byte[] convertToBytes(byte[] value) {
			byte[] len = putUVarint(value.length);
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

	ContractTemplate(Address addr, byte[] prog) {
		address = addr;
		program = prog;
	}

	/**
	 * Varints are a method of serializing integers using one or more bytes. 
	 * Smaller numbers take a smaller number of bytes.
	 * Each byte in a varint, except the last byte, has the most significant 
	 * bit (msb) set – this indicates that there are further bytes to come. 
	 * The lower 7 bits of each byte are used to store the two's complement 
	 * representation of the number in groups of 7 bits, least significant 
	 * group first.
	 * https://developers.google.com/protocol-buffers/docs/encoding
	 * @param value being serialized
	 * @return byte array holding the serialized bits
	 */
	protected static byte[] putUVarint(int value) {
		assert value >= 0 : "putUVarint expects non-negative values.";
		ArrayList<Byte> buffer = new ArrayList<Byte>();
		while (value >= 0x80) {
			buffer.add((byte)((value & 0xFF) | 0x80 ));
			value >>= 7;
		}
		buffer.add((byte)(value & 0xFF));
		byte [] out = new byte[buffer.size()];
		for (int x = 0; x < buffer.size(); ++x) {
			out[x] = buffer.get(x);
		}
		return out;
	}

	/**
	 * Given a varint, get the integer value
	 * @param buffer serialized varint
	 * @param bufferOffset position in the buffer to start reading from
	 * @return pair of values in in array: value, read size 
	 */
	protected static int[] getUVarint(byte [] buffer, int bufferOffset) {
		int x = 0;
		int s = 0;
		for (int i = 0; i < buffer.length; i++) {
			int b = buffer[bufferOffset+i] & 0xff;
			if (b < 0x80) {
				if (i > 9 || i == 9 && b > 1) {
					return new int[] {0, -(i + 1)};
				}
				return new int[] {x | (b & 0xff) << s, i + 1};
			}
			x |= ((b & 0x7f) & 0xff) << s;
			s += 7;
		}
		return new int[] {0,0};
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
				//progIdx += value.appendToProgram(updatedProgram);
			} else {
				updatedProgram.add(program[progIdx]);
				progIdx += 1;
			}
		}

		byte [] updatedProgramByteArray = new byte[updatedProgram.size()];
		for (int x = 0; x < updatedProgram.size(); ++x) {
			updatedProgramByteArray[x] = updatedProgram.get(x);			
		}
		//Address
		LogicsigSignature ls = new LogicsigSignature(updatedProgramByteArray, new ArrayList<byte[]>());
		return new ContractTemplate(ls.toAddress(), updatedProgramByteArray);
	}
}
