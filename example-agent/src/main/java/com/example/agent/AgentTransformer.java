package com.example.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.ProtectionDomain;
import java.util.Objects;

/**
 * @author youthlin.chen
 * @date 2020-01-13 11:06
 */
public class AgentTransformer implements ClassFileTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentTransformer.class);

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classFileBuffer) throws IllegalClassFormatException {
        className = className.replace("/", ".");
        if (Objects.equals("com.youthlin.example.boot.App", className)) {
            LOGGER.info("transform... {}", className);
            try {
                CtClass ctClass = ClassPool.getDefault().get(className);
                LOGGER.info("ctClass: {}", ctClass);
                CtMethod sayHello = ctClass.getDeclaredMethod("sayHello",
                        new CtClass[]{ClassPool.getDefault().get("java.lang.String")});

                CtMethod originalMethod = CtNewMethod.copy(sayHello, "sayHello", ctClass, null);
                originalMethod.setName("sayHello$gen");
                ctClass.addMethod(originalMethod);

                String sb = "{" +
                        "System.out.println(\"Enter Method:" + sayHello.getLongName() + "\");\n" +
                        "long start = System.currentTimeMillis();\n" +
                        "String ret = sayHello$gen($$);\n" +
                        "System.out.println(\"Exit Method. cost=\"+(System.currentTimeMillis()-start));\n" +
                        "return ret;" +
                        "}";
                sayHello.setBody(sb);


                byte[] bytes = ctClass.toBytecode();
                Path path = Path.of(".", "App.class");
                LOGGER.info("Write to {}", path.toAbsolutePath());
                Files.write(path, bytes, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                return bytes;
            } catch (Throwable e) {
                LOGGER.warn("error", e);
            }
        }
        return null;
    }

}
