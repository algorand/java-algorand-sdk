package com.algorand.algosdk.logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.algorand.algosdk.crypto.Address;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Logic class provides static checkProgram function
 * that can be used for client-side program validation for size and execution cost.
 */
public class Logic {

    @Deprecated
    private static final int MAX_COST = 20000;
    @Deprecated
    private static final int MAX_LENGTH = 1000;

    @Deprecated
    private static final int INTCBLOCK_OPCODE = 32;
    @Deprecated
    private static final int BYTECBLOCK_OPCODE = 38;
    @Deprecated
    private static final int PUSHBYTES_OPCODE = 128;
    @Deprecated
    private static final int PUSHINT_OPCODE = 129;

    @Deprecated
    private class LangSpec {
        public int EvalMaxVersion;
        public int LogicSigVersion;
        public Operation[] Ops;
    }

    @Deprecated
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

    /**
     * Metadata related to a teal program.
     */
    @Deprecated
    public static class ProgramData {
        public final boolean good;
        public final List<Integer> intBlock;
        public final List<byte[]> byteBlock;

        private ProgramData(final boolean good, final List<Integer> intBlock, final List<byte[]> byteBlock) {
            this.good = good;
            this.intBlock = intBlock;
            this.byteBlock = byteBlock;
        }
    }

    /**
     * Metadata related to a varint parsed from teal program data.
     */
    @Deprecated
    public static class VarintResult {
        final public int value;
        final public int length;

        private VarintResult(int value, int length) {
            this.value = value;
            this.length = length;
        }

        private VarintResult() {
            this.value = 0;
            this.length = 0;
        }
    }

    @Deprecated
    protected static class IntConstBlock {
        public final int size;
        public final List<Integer> results;

        IntConstBlock(final int size, final List<Integer> results) {
            this.size = size;
            this.results = results;
        }
    }

    @Deprecated
    protected static class ByteConstBlock {
        public final int size;
        public final List<byte[]> results;

        ByteConstBlock(int size, List<byte[]> results) {
            this.size = size;
            this.results = results;
        }
    }

    /**
     * Varints are a method of serializing integers using one or more bytes.
     * Smaller numbers take a smaller number of bytes.
     * Each byte in a varint, except the last byte, has the most significant
     * bit (msb) set – this indicates that there are further bytes to come.
     * The lower 7 bits of each byte are used to store the two's complement
     * representation of the number in groups of 7 bits, the least significant
     * group first.
     * https://developers.google.com/protocol-buffers/docs/encoding
     * @param value being serialized
     * @return byte array holding the serialized bits
     */
    @Deprecated
    public static byte[] putUVarint(int value) {
        assert value >= 0 : "putUVarint expects non-negative values.";
        List<Byte> buffer = new ArrayList<>();
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
     * @return pair of values in an array: value, read size
     */
    @Deprecated
    public static VarintResult getUVarint(byte [] buffer, int bufferOffset) {
        int x = 0;
        int s = 0;
        for (int i = 0; i < buffer.length; i++) {
            int b = buffer[bufferOffset+i] & 0xff;
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

    /**
     * Performs heuristic program validation:
     * check if passed in bytes are Algorand address or is B64 encoded, rather than Teal bytes
     * @param program
     */
    public static void sanityCheckProgram(final byte[] program) {
        boolean isAddress = false;
        try {
            new Address(new String(program));
            isAddress = true;
        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            // if exception is IllegalArgException, it means bytes are not Algorand address
            if (e instanceof NoSuchAlgorithmException)
                throw new IllegalArgumentException("cannot check if program bytes are Algorand address" + e);
        }
        if (isAddress)
            throw new IllegalArgumentException("requesting program bytes, but get Algorand address");

        if (Base64.isBase64(program))
            throw new IllegalArgumentException("program should not be b64 encoded");
    }

    @Deprecated
    private static LangSpec langSpec;
    @Deprecated
    private static Operation[] opcodes;

    /**
     * Performs basic program validation: instruction count and program cost
     * @deprecated `langspec.json` can no longer correctly to depicting the cost model (as of 2022.08.22),
     * also to minimize the work in updating SDKs per AVM release,
     * we are deprecating`langspec.json` across all SDKs.
     * The behavior of method `checkProgram` relies on `langspec.json`.
     * Thus, this method is being deprecated.
     *
     * @param program
     * @param args
     * @return
     * @throws IOException
     */
    @Deprecated
    public static boolean checkProgram(byte[] program, List<byte[]> args) throws IOException {
        return readProgram(program, args).good;
    }

    /**
     * Performs basic program validation: instruction count and program cost
     * @deprecated `langspec.json` can no longer correctly to depicting the cost model (as of 2022.08.22),
     * also to minimize the work in updating SDKs per AVM release,
     * we are deprecating`langspec.json` across all SDKs.
     * The behavior of method `readProgram` relies on `langspec.json`.
     * Thus, this method is being deprecated.
     *
     * @param program Program to validate
     * @param args Program arguments to validate
     * @return boolean
     * @throws IOException
     */
    @Deprecated
    public static ProgramData readProgram(byte[] program, List<byte[]> args) throws IOException {
        List<Integer> ints = new ArrayList<>();
        List<byte[]> bytes = new ArrayList<>();

        if (langSpec == null) {
            loadLangSpec();
        }

        VarintResult result = getUVarint(program, 0);
        int vlen = result.length;
        if (vlen <= 0) {
            throw new IllegalArgumentException("version parsing error");
        }

        int version = result.value;
        if (version > langSpec.EvalMaxVersion) {
            throw new IllegalArgumentException("unsupported version");
        }

        if (args == null) {
            args = new ArrayList<>();
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
                throw new IllegalArgumentException("invalid instruction: " + opcode);
            }

            cost += op.Cost;
            int size = op.Size;
            if (size == 0) {
                switch (op.Opcode) {
                    case INTCBLOCK_OPCODE:
                        IntConstBlock intsBlock = readIntConstBlock(program, pc);
                        size += intsBlock.size;
                        ints.addAll(intsBlock.results);
                        break;
                    case BYTECBLOCK_OPCODE:
                        ByteConstBlock bytesBlock = readByteConstBlock(program, pc);
                        size += bytesBlock.size;
                        bytes.addAll(bytesBlock.results);
                        break;
                    case PUSHINT_OPCODE:
                        IntConstBlock pushInt = readPushIntOp(program, pc);
                        size += pushInt.size;
                        ints.addAll(pushInt.results);
                        break;
                    case PUSHBYTES_OPCODE:
                        ByteConstBlock pushBytes = readPushByteOp(program, pc);
                        size += pushBytes.size;
                        bytes.addAll(pushBytes.results);
                        break;
                    default:
                        throw new IllegalArgumentException("invalid instruction");
                }
            }
            pc += size;
        }
        // costs calculated dynamically starting in v4
        if (version < 4 && cost > MAX_COST) {
            throw new IllegalArgumentException("program too costly for version < 4. consider using v4.");
        }
        
        return new ProgramData(true, ints, bytes);
    }

    /**
     * Retrieves supported program version
     * @return int
     * @throws IOException
     */
    @Deprecated
    public static int getLogicSigVersion() throws IOException {
        if (langSpec == null) {
            loadLangSpec();
        }
        return langSpec.LogicSigVersion;
    }

    /**
     * Retrieves max supported program version of evaluator
     * @return int
     * @throws IOException
     */
    @Deprecated
    public static int getEvalMaxVersion() throws IOException {
        if (langSpec == null) {
            loadLangSpec();
        }
        return langSpec.EvalMaxVersion;
    }

    @Deprecated
    private static void loadLangSpec() throws IOException {
        if (langSpec != null) {
            return;
        }

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

    @Deprecated
    protected static IntConstBlock readIntConstBlock(byte[] program, int pc) {
        ArrayList<Integer> results = new ArrayList<>();

        int size = 1;
        VarintResult result = getUVarint(program, pc + size);
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
            result = getUVarint(program, pc + size);
            if (result.length <= 0) {
                throw new IllegalArgumentException(
                    String.format("could not decode int const[%d] block at pc=%d", i, pc + size)
                );
            }
            size += result.length;
            results.add(result.value);
        }
        return new IntConstBlock(size, results);
    }

    @Deprecated
    protected static ByteConstBlock readByteConstBlock(byte[] program, int pc) {
        ArrayList<byte[]> results = new ArrayList<>();
        int size = 1;
        VarintResult result = getUVarint(program, pc + size);
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
            result = getUVarint(program, pc + size);
            if (result.length <= 0) {
                throw new IllegalArgumentException(
                    String.format("could not decode byte[] const[%d] block at pc=%d", i, pc + size)
                );
            }
            size += result.length;
            if (pc + size + result.value > program.length) {
                throw new IllegalArgumentException("byte[] const block exceeds program length");
            }
            byte[] buff = new byte[result.value];
            System.arraycopy(program, pc + size, buff, 0, result.value);
            results.add(buff);
            size += result.value;
        }
        return new ByteConstBlock(size, results);
    }

    @Deprecated
    protected static IntConstBlock readPushIntOp(byte[] program, int pc) {
        int size = 1;
        VarintResult result = getUVarint(program, pc + size);
        if (result.length <= 0) {
            throw new IllegalArgumentException(
                String.format("could not decode push int const at pc=%d", pc)
            );
        }
        size += result.length;

        return new IntConstBlock(size, Collections.singletonList(result.value));
    }

    @Deprecated
    protected static ByteConstBlock readPushByteOp(byte[] program, int pc) {
        int size = 1;
        VarintResult result = getUVarint(program, pc + size);
        if (result.length <= 0) {
            throw new IllegalArgumentException(
                String.format("could not decode push []byte const size at pc=%d", pc)
            );
        }

        size += result.length;
        if (pc + size + result.value > program.length) {
            throw new IllegalArgumentException("pushbytes ran past end of program");
        }

        byte[] buff = new byte[result.value];
        System.arraycopy(program, pc + size, buff, 0, result.value);
        size += result.value;

        return new ByteConstBlock(size, Collections.singletonList(buff));
    }
}
