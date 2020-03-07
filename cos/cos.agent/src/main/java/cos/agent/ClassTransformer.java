package cos.agent;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import static java.lang.System.out;

public class ClassTransformer implements ClassFileTransformer {

    private static int count = 0;

    @Override
    public byte[] transform(Module module,
                            ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {

        out.println("load class: " + className);
        out.println(String.format("loaded %s classes", ++count));
        return classfileBuffer;
    }
}
