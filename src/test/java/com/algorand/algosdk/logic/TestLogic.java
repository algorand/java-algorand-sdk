package com.algorand.algosdk.logic;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.algorand.algosdk.logic.Logic.*;
import static org.assertj.core.api.Assertions.*;

public class TestLogic {
    @Test
    public void testParseUvarint1() throws Exception {
        byte[] data = {0x01};
        VarintResult result = getUVarint(data, 0);
        assertThat(result.length).isEqualTo(1);
        assertThat(result.value).isEqualTo(1);
    }

    @Test
    public void testParseUvarint2() throws Exception {
        byte[] data = {0x02};
        VarintResult result = getUVarint(data, 0);
        assertThat(result.length).isEqualTo(1);
        assertThat(result.value).isEqualTo(2);
    }

    @Test
    public void testParseUvarint3() throws Exception {
        byte[] data = {0x7b};
        VarintResult result = getUVarint(data, 0);
        assertThat(result.length).isEqualTo(1);
        assertThat(result.value).isEqualTo(123);
    }

    @Test
    public void testParseUvarint4() throws Exception {
        byte[] data = {(byte)0xc8, 0x03};
        VarintResult result = getUVarint(data, 0);
        assertThat(result.length).isEqualTo(2);
        assertThat(result.value).isEqualTo(456);
    }

    @Test
    public void testParseUvarint4AtOffset() throws Exception {
        byte[] data = {0x0, 0x0, (byte)0xc8, 0x03};
        VarintResult result = getUVarint(data, 2);
        assertThat(result.length).isEqualTo(2);
        assertThat(result.value).isEqualTo(456);
    }

    @Test
    public void testParseIntcBlock() throws Exception {
        byte[] data = {
            0x20, 0x05, 0x00, 0x01, (byte)0xc8, 0x03, 0x7b, 0x02
        };

        IntConstBlock results = readIntConstBlock(data, 0);
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

        Logic.ByteConstBlock results = readByteConstBlock(data, 0);
        assertThat(results.size).isEqualTo(data.length);
        assertThat(results.results).containsExactlyElementsOf(values);
    }

    @Test
    public void testCheckProgramValid() throws Exception {
        byte[] program = {
            0x01, 0x20, 0x01, 0x01, 0x22  // int 1
        };

        // Null argument
        ProgramData programData = readProgram(program, null);
        assertThat(programData.good).isTrue();
        assertThat(programData.intBlock).extracting("value")
                .containsExactlyElementsOf(ImmutableList.of(1));
        assertThat(programData.byteBlock).isEmpty();

        // No argument
        ArrayList<byte[]> args = new ArrayList<>();
        programData = readProgram(program, args);
        assertThat(programData.good).isTrue();
        assertThat(programData.intBlock).extracting("value")
                .containsExactlyElementsOf(ImmutableList.of(1));
        assertThat(programData.byteBlock).isEmpty();

        // Unused argument
        byte[] arg = new byte[10];
        Arrays.fill(arg, (byte)0x31);
        args.add(arg);

        programData = readProgram(program, args);
        assertThat(programData.good).isTrue();
        assertThat(programData.intBlock).extracting("value")
                .containsExactlyElementsOf(ImmutableList.of(1));
        assertThat(programData.byteBlock).isEmpty();

        // ???
        byte[] int1 = new byte[10];
        Arrays.fill(int1, (byte)0x22);
        byte[] program2 = new byte[program.length + int1.length];
        System.arraycopy(program, 0, program2, 0, program.length);
        System.arraycopy(int1, 0, program2, program.length, int1.length);
        programData = readProgram(program2, args);
        assertThat(programData.good).isTrue();
        assertThat(programData.intBlock).extracting("value")
                .containsExactlyElementsOf(ImmutableList.of(1));
        assertThat(programData.byteBlock).isEmpty();
   }

    @Test
    public void testCheckProgramLongArgs() throws Exception {
        byte[] program = {
            0x01, 0x20, 0x01, 0x01, 0x22  // int 1
        };
        ArrayList<byte[]> args = new ArrayList<byte[]>();

        byte[] arg = new byte[1000];
        Arrays.fill(arg, (byte)0x31);
        args.add(arg);

        assertThatThrownBy(() -> readProgram(program, args))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("program too long");
    }

    @Test
    public void testCheckProgramLong() throws Exception {
        byte[] program = {
            0x01, 0x20, 0x01, 0x01, 0x22  // int 1
        };
        byte[] int1 = new byte[1000];
        byte[] program2 = new byte[program.length + int1.length];
        ArrayList<byte[]> args = new ArrayList<byte[]>();

        System.arraycopy(program, 0, program2, 0, program.length);
        System.arraycopy(int1, 0, program2, program.length, int1.length);
        assertThatThrownBy(() -> checkProgram(program2, args))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("program too long");
   }

    @Test
    public void testCheckProgramInvalidOpcode() throws Exception {
        byte[] program = {
            0x01, 0x20, 0x01, 0x01, (byte)0x81
        };
        ArrayList<byte[]> args = new ArrayList<>();

        assertThatThrownBy(() -> checkProgram(program, args))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("invalid instruction: 129");
    }

    @Test
    public void testCheckProgramCostly() throws Exception {
        byte[] program = {
            0x01, 0x26, 0x01, 0x01, 0x01, 0x01, 0x28, 0x02  // byte 0x01 + keccak256
        };
        ArrayList<byte[]> args = new ArrayList<byte[]>();

        boolean valid = checkProgram(program, args);
        assertThat(valid).isTrue();

        byte[] keccak25610 = new byte[10];
        Arrays.fill(keccak25610, (byte)0x02);

        byte[] program2 = new byte[program.length + keccak25610.length];
        System.arraycopy(program, 0, program2, 0, program.length);
        System.arraycopy(keccak25610, 0, program2, program.length, keccak25610.length);
        valid = checkProgram(program2, args);
        assertThat(valid).isTrue();

        byte[] keccak256800 = new byte[800];
        Arrays.fill(keccak256800, (byte)0x02);
        byte[] program3 = new byte[program.length + keccak256800.length];
        System.arraycopy(program, 0, program3, 0, program.length);
        System.arraycopy(keccak256800, 0, program3, program.length, keccak256800.length);
        assertThatThrownBy(() -> checkProgram(program3, args))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("program too costly to run");
    }
}
