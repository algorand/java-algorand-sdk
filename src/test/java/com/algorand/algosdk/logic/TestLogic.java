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
    public void testParsePushIntOp() throws Exception {
        byte[] data = {
            (byte)0x81, (byte)0x80, (byte)0x80, 0x04
        };

        IntConstBlock results = readPushIntOp(data, 0);
        assertThat(results.size).isEqualTo(data.length);
        assertThat(results.results)
                .containsExactlyElementsOf(ImmutableList.of(65536));
    }

    @Test
    public void testParsePushBytesOp() throws Exception {
        byte[] data = {
            (byte)0x80, 0x0b, 0x68, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x77, 0x6f, 0x72, 0x6c, 0x64
        };
        List<byte[]> values = ImmutableList.of(
                new byte[]{ 'h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd' }
        );

        Logic.ByteConstBlock results = readPushByteOp(data, 0);
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
        assertThat(programData.intBlock)
                .containsExactlyElementsOf(ImmutableList.of(1));
        assertThat(programData.byteBlock).isEmpty();

        // No argument
        ArrayList<byte[]> args = new ArrayList<>();
        programData = readProgram(program, args);
        assertThat(programData.good).isTrue();
        assertThat(programData.intBlock)
                .containsExactlyElementsOf(ImmutableList.of(1));
        assertThat(programData.byteBlock).isEmpty();

        // Unused argument
        byte[] arg = new byte[10];
        Arrays.fill(arg, (byte)0x31);
        args.add(arg);

        programData = readProgram(program, args);
        assertThat(programData.good).isTrue();
        assertThat(programData.intBlock)
                .containsExactlyElementsOf(ImmutableList.of(1));
        assertThat(programData.byteBlock).isEmpty();

        // Repeated int constants parsing
        byte[] int1 = new byte[10];
        Arrays.fill(int1, (byte)0x22);
        byte[] program2 = new byte[program.length + int1.length];
        System.arraycopy(program, 0, program2, 0, program.length);
        System.arraycopy(int1, 0, program2, program.length, int1.length);
        programData = readProgram(program2, args);
        assertThat(programData.good).isTrue();
        assertThat(programData.intBlock)
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
            0x01, 0x20, 0x01, 0x01, (byte)0xFF
        };
        ArrayList<byte[]> args = new ArrayList<>();

        assertThatThrownBy(() -> checkProgram(program, args))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("invalid instruction: 255");
    }

    @Test
    public void testCheckProgramTealV2() throws Exception {
        assertThat(getEvalMaxVersion()).isGreaterThanOrEqualTo(2);
        assertThat(getLogicSigVersion()).isGreaterThanOrEqualTo(2);

        {
            // balance
            byte[] program = {
                0x02, 0x20, 0x01, 0x00, 0x22, 0x60  // int 0; balance
            };
            boolean valid = checkProgram(program, null);
            assertThat(valid).isTrue();
        }

        // app_opted_in
        {
            byte[] program = {
                0x02, 0x20, 0x01, 0x00, 0x22, 0x22, 0x61  // int 0; int 0; app_opted_in
            };
            boolean valid = checkProgram(program, null);
            assertThat(valid).isTrue();
        }

        {
            // asset_holding_get
            byte[] program = {
                0x02, 0x20, 0x01, 0x00, 0x22, 0x70, 0x00  // int 0; int 0; asset_holding_get Balance
            };
            boolean valid = checkProgram(program, null);
            assertThat(valid).isTrue();
        }
    }

    @Test
    public void testCheckProgramTealV3() throws Exception {
        assertThat(getEvalMaxVersion()).isGreaterThanOrEqualTo(3);
        assertThat(getLogicSigVersion()).isGreaterThanOrEqualTo(3);

        {
            // min_balance
            byte[] program = {
                0x03, 0x20, 0x01, 0x00, 0x22, 0x78  // int 0; min_balance
            };
            boolean valid = checkProgram(program, null);
            assertThat(valid).isTrue();
        }

        // pushbytes
        {
            byte[] program = {
                0x03, 0x20, 0x01, 0x00, 0x22, (byte)0x80, 0x02, 0x68, 0x69, 0x48  // int 0; pushbytes "hi"; pop
            };
            boolean valid = checkProgram(program, null);
            assertThat(valid).isTrue();
        }

        {
            // pushint
            byte[] program = {
                0x03, 0x20, 0x01, 0x00, 0x22, (byte)0x81, 0x01, 0x48  // int 0; pushint 1; pop
            };
            boolean valid = checkProgram(program, null);
            assertThat(valid).isTrue();
        }

        {
            // swap
            byte[] program = {
                0x03, 0x20, 0x02, 0x00, 0x01, 0x22, 0x23, 0x4c, 0x48  // int 0; int 1; swap; pop
            };
            boolean valid = checkProgram(program, null);
            assertThat(valid).isTrue();
        }
    }
}
