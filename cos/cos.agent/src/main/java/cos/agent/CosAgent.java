package cos.agent;

import java.lang.instrument.Instrumentation;

public class CosAgent {

    private final static long start = System.nanoTime();

    public static void premain(String agentArgument, Instrumentation instrumentation) {
        System.out.println("Agent Counter");
        instrumentation.addTransformer(new ClassTransformer());
    }
}
