package cos.agent;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class ClassTransformer implements ClassFileTransformer {

    private static int count = 0;

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {

        System.out.println("load class: " + className.replaceAll("/", "."));
        System.out.println(String.format("loaded %s classes", ++count));
        return classfileBuffer;
    }
}
