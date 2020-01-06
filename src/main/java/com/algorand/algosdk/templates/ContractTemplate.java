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

	public enum DataType {
		BASE64, ADDRESS, INT;
	}

	abstract static class ParameterValue {
		abstract public DataType getType();

		public int getIntValue() {
		    throw new IllegalArgumentException();
		}

		public byte[] getByteValue() {
			throw new IllegalArgumentException();
		}
	}

	public static class IntParameterValue extends ParameterValue {
		private int value;

		public IntParameterValue(int value) {
			this.value = value;
		}

		public int getIntValue() {
			return this.value;
		}

		public DataType getType() {
			return DataType.INT;
		}
	}

	abstract public static class ByteParameterValue extends ParameterValue {
		private byte[] value;

		public ByteParameterValue(byte[] value) throws NoSuchAlgorithmException {
			this.value = value;
		}

		public byte[] getByteValue() {
			return this.value;
		}
	}

	public static class AddressParameterValue extends ByteParameterValue {

		public AddressParameterValue(String value) throws NoSuchAlgorithmException {
			super(new Address(value).getBytes());
		}

		public DataType getType() {
			return DataType.ADDRESS;
		}
	}

	public static class Base64ParameterValue extends ByteParameterValue {

		public Base64ParameterValue(String value) throws NoSuchAlgorithmException {
			super(Encoder.decodeFromBase64(value));
		}

		public DataType getType() {
			return DataType.BASE64;
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
	public static byte[] putUVarint(int value) {
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
	public static int[] getUVarint(byte [] buffer, int bufferOffset) { 
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

		/**
		 * offsets are the positions of the program arguments in the bytecode.
		 * Each argument in the program has a place-holder in the bytecode of varying sizes based on the argument type  
		 * 		BASE64 : placeholder  2 bytes 
		 * 		ADDRESS: placeholder 32 bytes 
 		 * 		INT    : placeholder  1 byte 
		 */
		if (offsets.length != values.length) {
			throw new RuntimeException("offsets and values should have the same number of elements");
		}
		
		int paramIdx = 0;
		ArrayList<Byte> updatedProgram = new ArrayList<Byte>();

		for (int progIdx = 0; progIdx < program.length; ) {
			if (paramIdx < offsets.length && offsets[paramIdx] == progIdx) {
				ParameterValue value = values[paramIdx];
				++paramIdx;
				switch (value.getType()) {
				case BASE64:
					byte[] byteValueString = value.getByteValue();
					byte[] byteValueLength = putUVarint(byteValueString.length);
					for (byte b : byteValueLength) {
						updatedProgram.add(b);	
					}
					for (byte b : byteValueString) {
						updatedProgram.add(b);	
					}
					progIdx += 2; // skip the parameter placeholder bytes in program bytecode
					break;

				case ADDRESS:
					for (byte b : value.getByteValue()) {
						updatedProgram.add(b);
					}
					progIdx += 32; // skip the parameter placeholder bytes in program bytecode
					break;

				case INT:
					byte[] byteValue = putUVarint(value.getIntValue());
					for (byte b : byteValue) {
						updatedProgram.add(b);	
					}
					progIdx += 1; // skip the parameter placeholder bytes in program bytecode
					break;

				default:
					throw new RuntimeException("Unrecognized program parameter datatype!");
				}
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
