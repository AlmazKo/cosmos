package cos.ops.parser;

import cos.ops.Registry;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;


public final class OpParser {
    private static final MethodHandles.Lookup MHL = MethodHandles.publicLookup();
    private static final Record[] EMPTY = new Record[0];

    public static final class Builder {
        private static final MethodHandle[] readers = new MethodHandle[127];
        private static final HashMap<Class<? extends Record>, MethodHandle> writers = new HashMap<>();
        private static final HashMap<Class<? extends Record>, Byte> opcodes = new HashMap<>();

        public Builder register(int opCode, Class<? extends Record> klass) {
            if ((byte) opCode != opCode)
                throw new IllegalArgumentException("Wrong opCode, expected value is in range [0;127]");

            var methodType = MethodType.methodType(klass, ByteBuffer.class);
            MethodHandle readMethod = null;
            try {
                readMethod = MHL.findStatic(klass, "read", methodType);
            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException("Wrong op", e);
            }

            methodType = MethodType.methodType(void.class, ByteBuffer.class);
            MethodHandle writeMethod = null;
            try {
                writeMethod = MHL.findVirtual(klass, "write", methodType);
            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException("Wrong op", e);
            }
            readers[opCode] = readMethod;
            writers.put(klass, writeMethod);
            opcodes.put(klass, (byte) opCode);

            return this;
        }


        public OpParser build() {
            return new OpParser(
                    readers,
                    Map.copyOf(writers),
                    Map.copyOf(opcodes)
            );
        }
    }

    private final MethodHandle[] readers;
    private final Map<Class<? extends Record>, MethodHandle> writers;
    private final Map<Class<? extends Record>, Byte> opcodes;

    private OpParser(MethodHandle[] readers, Map<Class<? extends Record>, MethodHandle> writers, Map<Class<? extends Record>, Byte> opcodes) {
        this.readers = readers;
        this.writers = writers;
        this.opcodes = opcodes;
    }

    public byte toOpCode(Record op) {
        Byte opcode = opcodes.get(op.getClass());
        if (opcode == null) throw new RuntimeException("Unknown opcode " + op.getClass().getName());
        return opcode;
    }

    public void write(ByteBuffer buf, Record[] ops) {
        buf.putInt(ops.length);
        for (Record op : ops) {
            write(buf, op);
        }
    }

    public @NotNull Record readOnlyData(ByteBuffer buf) {
        byte code = buf.get();
        return read(buf, code);
    }

    public void writeOnlyData(ByteBuffer buf, Record op) {
        buf.put(toOpCode(op));
        write(buf, op);
    }

    public void write(ByteBuffer buf, Record op) {
        var writer = writers.get(op.getClass());
        if (writer == null) throw new IllegalArgumentException("Non registered op " + op);
        try {
            writer.invokeWithArguments(op, buf);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public @NotNull Object readMany(ByteBuffer buf, byte opCode) {
        if (opCode == Registry.NOPE_OP) {
            return EMPTY;
        }

        int len = buf.getInt();
        if (len == 0) return EMPTY;

        var reader = readers[opCode];
        if (reader == null) return Unknown.VALUE;

        var result = new Record[len];
        try {
            for (int i = 0; i < len; i++) {
                result[i] = (Record) reader.invokeWithArguments(buf);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return Unknown.VALUE;
        }

        return result;
    }


    public @NotNull Record read(ByteBuffer buf, byte opCode) {
        var reader = readers[opCode];
        if (reader == null) return Unknown.VALUE;

        try {
            return (Record) reader.invokeWithArguments(buf);
        } catch (Throwable e) {
            e.printStackTrace();
            return Unknown.VALUE;
        }
    }

}
