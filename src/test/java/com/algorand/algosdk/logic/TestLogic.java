package com.algorand.algosdk.logic;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class TestLogic {

    @Test
    public void testParseUvarint1() throws Exception {
        byte[] data = {0x01};
        VarintResult result = Uvarint.parse(data);
        Assert.assertEquals(1, result.length);
        Assert.assertEquals(1, result.value);
    }

    @Test
    public void testParseUvarint2() throws Exception {
        byte[] data = {0x02};
        VarintResult result = Uvarint.parse(data);
        Assert.assertEquals(1, result.length);
        Assert.assertEquals(2, result.value);
    }

    @Test
    public void testParseUvarint3() throws Exception {
        byte[] data = {0x7b};
        VarintResult result = Uvarint.parse(data);
        Assert.assertEquals(1, result.length);
        Assert.assertEquals(123, result.value);
    }

    @Test
    public void testParseUvarint4() throws Exception {
        byte[] data = {(byte)0xc8, 0x03};
        VarintResult result = Uvarint.parse(data);
        Assert.assertEquals(2, result.length);
        Assert.assertEquals(456, result.value);
    }

    @Test
    public void testParseIntcBlock() throws Exception {
        byte[] data = {
            0x20, 0x05, 0x00, 0x01, (byte)0xc8, 0x03, 0x7b, 0x02
        };

        IntConstBlock results = Logic.readIntConstBlock(data, 0);
        assertThat(results.size).isEqualTo(data.length);
        assertThat(results.results)
                .extracting("value")
                .containsExactlyElementsOf(ImmutableList.of(0, 1, 456, 123, 2));
    }

    @Test
    public void testParseBytecBlock() throws Exception {
        byte[] data = {
            0x026, 0x02, 0x0d, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32, 0x33, 0x02, 0x01, 0x02
        };
        List<byte[]> values = ImmutableList.of(
                new byte[]{ 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32, 0x33 },
                new byte[]{ 0x1, 0x2 });

        ByteConstBlock results = Logic.readByteConstBlock(data, 0);
        assertThat(results.size).isEqualTo(data.length);
        assertThat(results.results)
                .extracting("value")
                .containsExactlyElementsOf(values);
    }

    @Test
    public void testCheckProgramValid() throws Exception {
        byte[] program = {
            0x01, 0x20, 0x01, 0x01, 0x22  // int 1
        };

        // Null argument
        Logic.ProgramData programData = Logic.readProgram(program, null);
        assertThat(programData.good).isTrue();
        assertThat(programData.intBlock.results).extracting("value")
                .containsExactlyElementsOf(ImmutableList.of(1));
        assertThat(programData.byteBlock).isNull();

        // No argument
        ArrayList<byte[]> args = new ArrayList<>();
        programData = Logic.readProgram(program, args);
        assertThat(programData.good).isTrue();
        assertThat(programData.intBlock.results).extracting("value")
                .containsExactlyElementsOf(ImmutableList.of(1));
        assertThat(programData.byteBlock).isNull();

        // Unused argument
        byte[] arg = new byte[10];
        Arrays.fill(arg, (byte)0x31);
        args.add(arg);

        programData = Logic.readProgram(program, args);
        assertThat(programData.good).isTrue();
        assertThat(programData.intBlock.results).extracting("value")
                .containsExactlyElementsOf(ImmutableList.of(1));
        assertThat(programData.byteBlock).isNull();

        // ???
        byte[] int1 = new byte[10];
        Arrays.fill(int1, (byte)0x22);
        byte[] program2 = new byte[program.length + int1.length];
        System.arraycopy(program, 0, program2, 0, program.length);
        System.arraycopy(int1, 0, program2, program.length, int1.length);
        programData = Logic.readProgram(program2, args);
        Assert.assertTrue(programData.good);
        assertThat(programData.intBlock.results).extracting("value")
                .containsExactlyElementsOf(ImmutableList.of(1));
        assertThat(programData.byteBlock).isNull();
   }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckProgramLongArgs() throws Exception {
        byte[] program = {
            0x01, 0x20, 0x01, 0x01, 0x22  // int 1
        };
        ArrayList<byte[]> args = new ArrayList<byte[]>();

        byte[] arg = new byte[1000];
        Arrays.fill(arg, (byte)0x31);
        args.add(arg);

        Logic.ProgramData programData = Logic.readProgram(program, args);
        assertThat(programData.good).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckProgramLong() throws Exception {
        byte[] program = {
            0x01, 0x20, 0x01, 0x01, 0x22  // int 1
        };
        byte[] int1 = new byte[1000];
        byte[] program2 = new byte[program.length + int1.length];
        ArrayList<byte[]> args = new ArrayList<byte[]>();

        System.arraycopy(program, 0, program2, 0, program.length);
        System.arraycopy(int1, 0, program2, program.length, int1.length);
        boolean valid = Logic.checkProgram(program2, args);
        Assert.assertFalse(valid);
   }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckProgramInvalidOpcode() throws Exception {
        byte[] program = {
            0x01, 0x20, 0x01, 0x01, (byte)0x81
        };
        ArrayList<byte[]> args = new ArrayList<byte[]>();

        boolean valid = Logic.checkProgram(program, args);
        Assert.assertFalse(valid);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckProgramCostly() throws Exception {
        byte[] program = {
            0x01, 0x26, 0x01, 0x01, 0x01, 0x01, 0x28, 0x02  // byte 0x01 + keccak256
        };
        ArrayList<byte[]> args = new ArrayList<byte[]>();

        boolean valid = Logic.checkProgram(program, args);
        Assert.assertTrue(valid);

        byte[] keccak25610 = new byte[10];
        Arrays.fill(keccak25610, (byte)0x02);

        byte[] program2 = new byte[program.length + keccak25610.length];
        System.arraycopy(program, 0, program2, 0, program.length);
        System.arraycopy(keccak25610, 0, program2, program.length, keccak25610.length);
        valid = Logic.checkProgram(program2, args);
        Assert.assertTrue(valid);

        byte[] keccak256800 = new byte[800];
        Arrays.fill(keccak256800, (byte)0x02);
        byte[] program3 = new byte[program.length + keccak256800.length];
        System.arraycopy(program, 0, program3, 0, program.length);
        System.arraycopy(keccak256800, 0, program3, program.length, keccak256800.length);
        valid = Logic.checkProgram(program3, args);
        Assert.assertFalse(valid);
    }
}
