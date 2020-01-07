package com.algorand.algosdk.logic;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Logic class provides static checkProgram function
 * that can be used for client-side program validation for size and execution cost.
 */
public class Logic {

    private static final int MAX_COST = 20000;
    private static final int MAX_LENGTH = 1000;

    private static final int INTCBLOCK_OPCODE = 32;
    private static final int BYTECBLOCK_OPCODE = 38;

    private class LangSpec {
        public int EvalMaxVersion;
        public int LogicSigVersion;
        public Operation[] Ops;
    }

    private class Operation {
        int Opcode;
        String Name;
        int Cost;
        int Size;
        String Returns;
        String[] ArgEnum;
        String ArgEnumTypes;
        String Doc;
        String ImmediateNote;
        String[] Group;
    }

    public static class ProgramData {
        public final boolean good;
        public final IntConstBlock intBlock;
        public final ByteConstBlock byteBlock;

        public ProgramData(final boolean good, final IntConstBlock intBlock, final ByteConstBlock byteBlock) {
            this.good = good;
            this.intBlock = intBlock;
            this.byteBlock = byteBlock;
        }
    }


    private static LangSpec langSpec;
    private static Operation[] opcodes;

    public static boolean checkProgram(byte[] program, ArrayList<byte[]> args) throws IOException {
        return readProgram(program, args).good;
    }

    /**
     * Performs basic program validation: instruction count and program cost
     * @param program Program to validate
     * @param args Program arguments to validate
     * @return boolean
     * @throws IOException
     */
    public static ProgramData readProgram(byte[] program, ArrayList<byte[]> args) throws IOException {
        IntConstBlock intBlock = null;
        ByteConstBlock byteBlock = null;

        if (langSpec == null) {
            Reader reader;
            try {
                reader = new InputStreamReader(
                    Logic.class.getResourceAsStream("/langspec.json"),
                    "UTF-8"
                );
            } catch (UnsupportedEncodingException ex) {
                throw new IllegalStateException("langspec opening error");
            }

            Gson g = new GsonBuilder().create();

            langSpec = g.fromJson(reader, LangSpec.class);
            reader.close();
        }

        VarintResult result = Uvarint.parse(program);
        int vlen = result.length;
        if (vlen <= 0) {
            throw new IllegalArgumentException("version parsing error");
        }

        int version = result.value;
        if (version > langSpec.EvalMaxVersion) {
            throw new IllegalArgumentException("unsupported version");
        }

        if (args == null) {
            args = new ArrayList<byte[]>();
        }

        int cost = 0;
        int length = program.length;
        for (int i = 0; i < args.size(); i++) {
            length += args.get(i).length;
        }

        if (length > MAX_LENGTH) {
            throw new IllegalArgumentException("program too long");
        }

        if (opcodes == null) {
            opcodes = new Operation[256];
            for (int i = 0; i < langSpec.Ops.length; i++) {
                Operation op = langSpec.Ops[i];
                opcodes[op.Opcode] = op;
            }
        }

        int pc = vlen;
        while (pc < program.length) {
            int opcode = program[pc] & 0xFF;
            Operation op = opcodes[opcode];
            if (op == null) {
                throw new IllegalArgumentException("invalid instruction");
            }

            cost += op.Cost;
            int size = op.Size;
            if (size == 0) {
                switch (op.Opcode) {
                    case INTCBLOCK_OPCODE:
                        intBlock = readIntConstBlock(program, pc);
                        size += intBlock.size;
                        break;
                    case BYTECBLOCK_OPCODE:
                        byteBlock = readByteConstBlock(program, pc);
                        size += byteBlock.size;
                        break;
                    default:
                        throw new IllegalArgumentException("invalid instruction");
                }
            }
            pc += size;
        }

        if (cost > MAX_COST) {
            throw new IllegalArgumentException("program too costly to run");
        }

        return new ProgramData(true, intBlock, byteBlock);
    }

    static IntConstBlock readIntConstBlock(byte[] program, int pc) {
        ArrayList<VarintResult> results = new ArrayList<>();

        int size = 1;
        VarintResult result = Uvarint.parse(Arrays.copyOfRange(program, pc + size, program.length));
        if (result.length <= 0) {
            throw new IllegalArgumentException(
                String.format("could not decode int const block at pc=%d", pc)
            );
        }
        size += result.length;
        int numInts = result.value;
        for (int i = 0; i < numInts; i++) {
            if (pc + size >= program.length) {
                throw new IllegalArgumentException("int const block exceeds program length");
            }
            result = Uvarint.parse(Arrays.copyOfRange(program, pc + size, program.length));
            if (result.length <= 0) {
                throw new IllegalArgumentException(
                    String.format("could not decode int const[%d] block at pc=%d", i, pc + size)
                );
            }
            size += result.length;
            results.add(result);
        }
        return new IntConstBlock(size, results);
    }

    static ByteConstBlock readByteConstBlock(byte[] program, int pc) {
        ArrayList<ByteConstResult> results = new ArrayList<>();
        int size = 1;
        VarintResult result = Uvarint.parse(Arrays.copyOfRange(program, pc + size, program.length));
        if (result.length <= 0) {
            throw new IllegalArgumentException(
                String.format("could not decode byte[] const block at pc=%d", pc)
            );
        }
        size += result.length;
        int numInts = result.value;
        for (int i = 0; i < numInts; i++) {
            if (pc + size >= program.length) {
                throw new IllegalArgumentException("byte[] const block exceeds program length");
            }
            result = Uvarint.parse(Arrays.copyOfRange(program, pc + size, program.length));
            if (result.length <= 0) {
                throw new IllegalArgumentException(
                    String.format("could not decode byte[] const[%d] block at pc=%d", i, pc + size)
                );
            }
            size += result.length;
            if (pc + size >= program.length) {
                throw new IllegalArgumentException("byte[] const block exceeds program length");
            }
            byte[] buff = new byte[result.value];
            System.arraycopy(program, pc + size, buff, 0, result.value);
            results.add(new ByteConstResult(buff, result.length + result.value));
            size += result.value;
        }
        return new ByteConstBlock(size, results);
    }
}

class Uvarint {
    public static VarintResult parse(byte[] data) {
        int x = 0;
        int s = 0;
        for (int i = 0; i < data.length; i++) {
            int b = data[i] & 0xff;
            if (b < 0x80) {
                if (i > 9 || i == 9 && b > 1) {
                    return new VarintResult(0, -(i + 1));
                }
                return new VarintResult(x | (b & 0xff) << s, i + 1);
            }
            x |= ((b & 0x7f) & 0xff) << s;
            s += 7;
        }
        return new VarintResult();
    }
}

class VarintResult {
    final public int value;
    final public int length;

    public VarintResult(int value, int length) {
        this.value = value;
        this.length = length;
    }

    public VarintResult() {
        this.value = 0;
        this.length = 0;
    }
}

class IntConstBlock {
    public final int size;
    public final ArrayList<VarintResult> results;

    IntConstBlock(final int size, final ArrayList<VarintResult> results) {
        this.size = size;
        this.results = results;
    }
}

class ByteConstBlock {
    public final int size;
    public final ArrayList<ByteConstResult> results;

    ByteConstBlock(int size, ArrayList<ByteConstResult> results) {
        this.size = size;
        this.results = results;
    }
}

class ByteConstResult {
    final public byte[] value;
    final public int length;

    public ByteConstResult(byte[] value, int length) {
        this.value = value;
        this.length = length;
    }

    public ByteConstResult() {
        this.value = new byte[]{};
        this.length = 0;
    }
}
