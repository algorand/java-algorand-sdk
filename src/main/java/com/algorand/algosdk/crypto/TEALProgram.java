package com.algorand.algosdk.crypto;

import com.algorand.algosdk.util.Encoder;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TEALProgram {
    private byte[] program = null;

    @JsonValue
    public byte[] getBytes() {
        if (program == null) return null;
        return Arrays.copyOf(program, program.length);
    }

    // default values for serializer to ignore
    public TEALProgram() {
    }

    /**
     * Initialize a TEALProgram based on the byte array. A runtime exception is thrown if the program is invalid.
     * @param program
     */
    @JsonCreator
    public TEALProgram(byte[] program) {
        if (program == null) return;
        // try {
        //     Logic.readProgram(program, null);
        // } catch (Exception e) {
        //     throw new RuntimeException(e);
        // }
        this.program = Arrays.copyOf(program, program.length);
    }

    /**
     * Initialize a TEALProgram based on the base64 encoding. A runtime exception is thrown if the program is invalid.
     * @param base64String
     */
    public TEALProgram(String base64String) {
        this(Encoder.decodeFromBase64(base64String));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TEALProgram that = (TEALProgram) o;
        return Arrays.equals(program, that.program);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(program);
    }
}
