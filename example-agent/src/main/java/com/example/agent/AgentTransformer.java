package com.example.agent;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
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
    public static final String TARGET_CLASS_NAME_SLASH = "com/youthlin/example/boot/App";
    public static final String TARGET_CLASS_NAME_DOT = "com.youthlin.example.boot.App";

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classFileBuffer) throws IllegalClassFormatException {
        if (Objects.equals(TARGET_CLASS_NAME_SLASH, className)) {
            className = className.replace("/", ".");
            LOGGER.info("transform... {}", className);
            try {
                ClassPool pool = ClassPool.getDefault();
                if (classBeingRedefined != null) {
                    // https://www.jianshu.com/p/43424242846b
                    pool.insertClassPath(new ClassClassPath(classBeingRedefined));
                }
                CtClass ctClass = pool.get(className);
                CtMethod method = ctClass.getDeclaredMethod("sayHello",
                        new CtClass[]{pool.get("java.lang.String")});

                // https://www.jianshu.com/p/1e2d970e3661
                method.addLocalVariable("$_start", CtClass.longType);
                method.insertBefore("$_start = System.currentTimeMillis();");
                method.insertAfter("System.out.println(\"cost:\"+(System.currentTimeMillis()-$_start));");

                byte[] bytes = ctClass.toBytecode();
                ctClass.detach();

                Path path = Path.of("App.class");
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
