/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.algorand.algosdk.templates;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.util.Encoder;

public class ContractTemplate {

	public enum DataType {
		BASE64, ADDRESS, INT;
	}

	public static class ParameterValue {
		private DataType type;
		private byte[] byteValue;
		private int intValue;
		
		public ParameterValue(DataType dataType, String value) throws NoSuchAlgorithmException {
			type = dataType;
			
			if (type == DataType.ADDRESS) {
				Address addr = new Address(value);
				byteValue = addr.getBytes();
			} else {
				byteValue = value.getBytes();
			}
		}
		
		public ParameterValue(DataType dataType, int value) {
			type = dataType;
			intValue = value;
			if (type != DataType.INT) {
				throw new RuntimeException("Expecting int type with int value for parameter value.");
			}
		}
		
		public DataType getType() {
			return type;
		}
		
		public int getIntValue() {
			return intValue;
		}
		
		public byte[] getByteValue() {
			return byteValue;
		}
	}

	public String address;
	public String program;

	ContractTemplate(String addr, String prog) {
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
	public static byte[] putVarint(int value) {
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
	public static int[] getVarint(byte [] buffer, int bufferOffset) { 
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
	 * 
	 * @param program is compiled TEAL program
	 * @param offsets are the position in the program bytecode of the program parameters
	 * @param values of the program parameters to inject to the program bytecode
	 * @return ContractTemplate with the address and the program with the given parameter values
	 * @throws NoSuchAlgorithmException
	 */
	protected static ContractTemplate inject(String program, int [] offsets, ParameterValue[] values) throws NoSuchAlgorithmException {
		
		int paramIdx = 0;
		if (offsets.length != values.length) {
			throw new RuntimeException("offsets and values should have the same number of elements");
		}
		byte[] progBytes = Encoder.decodeFromBase64(program);
		
		ArrayList<Byte> updatedProgram = new ArrayList<Byte>();
		
		for (int progIdx = 0; progIdx < progBytes.length; ++progIdx) {
			if (paramIdx < offsets.length && offsets[paramIdx] == progIdx) {
				ParameterValue value = values[paramIdx];
				++paramIdx;
				switch (value.getType()) {
				case BASE64:
					byte[] byteValueString = value.getByteValue();
					byte[] byteValueLength = putVarint(byteValueString.length);
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
					byte[] byteValue = putVarint(value.getIntValue());
					for (byte b : byteValue) {
						updatedProgram.add(b);	
					}
					progIdx += 1; // skip the parameter placeholder bytes in program bytecode
					break;
					
				default:
					throw new RuntimeException("Unrecognized program parameter datatype!");
				}
			} else {
				updatedProgram.add(progBytes[progIdx]);
			}
		}

		byte [] updatedProgramByteArray = new byte[updatedProgram.size()];
		for (int x = 0; x < updatedProgram.size(); ++x) {
			updatedProgramByteArray[x] = updatedProgram.get(x);			
		}
		//Address
		LogicsigSignature ls = new LogicsigSignature(updatedProgramByteArray, new ArrayList<byte[]>());
		return new ContractTemplate(ls.toAddress().toString(), Encoder.encodeToBase64(updatedProgramByteArray));
	}
}
