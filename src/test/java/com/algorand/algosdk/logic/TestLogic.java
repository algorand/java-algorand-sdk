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
   public void testCheckProgramCostly() throws Exception {

    byte[] old_versions = {
        0x1, 0x2, 0x3
    };

    byte[] versions = {
        0x4
    };

    byte[] program = {
        0x01, 0x26, 0x01, 0x01, 0x01, 0x28, 0x02 // byte 0x01 + keccak256
    };
    ArrayList<byte[]> args = new ArrayList<byte[]>();
    
    byte[] arg = "aaaaaaaaaa".getBytes();
    args.add(arg);

    ProgramData programData = readProgram(program, args);
    assertThat(programData.good).isTrue();

    byte[] keccakx800 = new byte[800];
    for (int i = 0; i < keccakx800.length; i++) {
        keccakx800[i] = 0x02;
    }

    byte[] program2 = new byte[program.length + keccakx800.length];
    System.arraycopy(program, 0, program2, 0, program.length);
    System.arraycopy(keccakx800, 0, program2, program.length, keccakx800.length);

    for (byte v : old_versions) {
        program2[0] = v;
        assertThatThrownBy(() -> readProgram(program2, args))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("program too costly for Teal version < 4. consider using v4.");
    }

    for (byte v : versions) {
        program2[0] = v;
        programData = readProgram(program2, args);
        assertThat(programData.good).isTrue();
    }
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

    @Test
    public void testCheckProgramTealV4() throws Exception {
        assertThat(getEvalMaxVersion()).isGreaterThanOrEqualTo(4);

        {
            // divmodw
            byte[] program = {
                0x04, 0x20, 0x03, 0x01, 0x00, 0x02, 0x22, (byte) 0x81, (byte) 0xd0, (byte) 0x0f, 0x23, 0x24, 0x1f  // int 1; pushint 2000; int 0; int 2; divmodw
            };
            boolean valid = checkProgram(program, null);
            assertThat(valid).isTrue();
        }

        // gloads i
        {
            byte[] program = {
                0x04, 0x20, 0x01, 0x00, 0x22, 0x3b, 0x00  // int 0; gloads 0
            };
            boolean valid = checkProgram(program, null);
            assertThat(valid).isTrue();
        }

        {
            // callsub
            byte[] program = {
                0x04, 0x20, 0x02, 0x01, 0x02, 0x22, (byte) 0x88, 0x00, 0x02, 0x23, 0x12, 0x49  // int 1; callsub double; int 2; ==; double: dup;
            };
            boolean valid = checkProgram(program, null);
            assertThat(valid).isTrue();
        }

        {
            // b>=
            byte[] program = {
                0x04, 0x26, 0x02, 0x01, 0x11, 0x01, 0x10, 0x28, 0x29, (byte) 0xa7  // byte 0x11; byte 0x10; b>=
            };
            boolean valid = checkProgram(program, null);
            assertThat(valid).isTrue();
        }

        {
            // b^
            byte[] program = {
                0x04, 0x26, 0x02, 0x01, 0x11, 0x01, 0x10, 0x28, 0x29, (byte) 0xa7  // byte 0x11; byte 0x10; b^; byte 0x01; ==
            };
            boolean valid = checkProgram(program, null);
            assertThat(valid).isTrue();
        }

        {
            // callsub, retsub.
            byte[] program = {
                0x04, 0x20, 0x02, 0x01, 0x02, 0x22, (byte) 0x88, 0x00, 0x03, 0x23, 0x12, 0x43, 0x49, 0x08, (byte) 0x89  // int 1; callsub double; int 2; ==; return; double: dup; +; retsub;
            };
            boolean valid = checkProgram(program, null);
            assertThat(valid).isTrue();
        }

        {
            // loop
            byte[] program = {
                0x04, 0x20, 0x04, 0x01, 0x02, 0x0a, 0x10, 0x22, 0x23, 0x0b, 0x49, 0x24, 0x0c, 0x40, (byte) 0xff, (byte) 0xf8, 0x25, 0x12  // int 1; loop: int 2; *; dup; int 10; <; bnz loop; int 16; ==
            };
            boolean valid = checkProgram(program, null);
            assertThat(valid).isTrue();
        }
    }

    @Test
    public void testCheckProgramTealV5() throws Exception {
        assertThat(getEvalMaxVersion()).isGreaterThanOrEqualTo(5);

        {
            // itxn ops
            byte[] program = {
                0x05, 0x20, 0x01, (byte) 0xc0, (byte) 0x84, 0x3d, (byte) 0xb1, (byte) 0x81, 0x01, (byte) 0xb2, 0x10, 0x22, (byte) 0xb2, 0x08, 0x31, 0x00, (byte) 0xb2, 0x07, (byte) 0xb3, (byte) 0xb4, 0x08, 0x22, 0x12
            };
            // itxn_begin; int pay; itxn_field TypeEnum; int 1000000; itxn_field Amount; txn Sender; itxn_field Receiver; itxn_submit; itxn Amount; int 1000000; ==
            boolean valid = checkProgram(program, null);
            assertThat(valid).isTrue();
        }

        {
            // ECDSA ops
            byte[] program = {
                0x05,(byte)0x80,0x08,0x74,0x65,0x73,0x74,0x64,0x61,0x74,0x61,0x03,(byte)0x80,0x20,0x79,(byte)0xbf,(byte)0xa8,0x24,0x5a,(byte)0xea,(byte)0xc0,(byte)0xe7,0x14,(byte)0xb7,(byte)0xbd,0x2b,0x32,0x52,(byte)0xd0,0x39,0x79,(byte)0xe5,(byte)0xe7,(byte)0xa4,0x3c,(byte)0xb0,0x39,0x71,0x5a,0x5f,(byte)0x81,0x09,(byte)0xa7,(byte)0xdd,(byte)0x9b,(byte)0xa1,(byte)0x80,0x20,0x07,0x53,(byte)0xd3,0x17,(byte)0xe5,0x43,0x50,(byte)0xd1,(byte)0xd1,0x02,0x28,(byte)0x9a,(byte)0xfb,(byte)0xde,0x30,0x02,(byte)0xad,(byte)0xd4,0x52,(byte)0x9f,0x10,(byte)0xb9,(byte)0xf7,(byte)0xd3,(byte)0xd2,0x23,(byte)0x84,0x39,(byte)0x85,(byte)0xde,0x62,(byte)0xe0,(byte)0x80,0x21,0x03,(byte)0xab,(byte)0xfb,0x5e,0x6e,0x33,0x1f,(byte)0xb8,0x71,(byte)0xe4,0x23,(byte)0xf3,0x54,(byte)0xe2,(byte)0xbd,0x78,(byte)0xa3,(byte)0x84,(byte)0xef,0x7c,(byte)0xb0,0x7a,(byte)0xc8,(byte)0xbb,(byte)0xf2,0x7d,0x2d,(byte)0xd1,(byte)0xec,(byte)0xa0,0x0e,0x73,(byte)0xc1,0x06,0x00,0x05,0x00
            };
            // byte "testdata"; sha512_256; byte 0x79bfa8245aeac0e714b7bd2b3252d03979e5e7a43cb039715a5f8109a7dd9ba1; byte 0x0753d317e54350d1d102289afbde3002add4529f10b9f7d3d223843985de62e0; byte 0x03abfb5e6e331fb871e423f354e2bd78a384ef7cb07ac8bbf27d2dd1eca00e73c1; ecdsa_pk_decompress Secp256k1; ecdsa_verify Secp256k1
            boolean valid = checkProgram(program, null);
            assertThat(valid).isTrue();
        }
    }
}
