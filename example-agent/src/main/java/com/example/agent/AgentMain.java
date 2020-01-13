package com.example.agent;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author youthlin.chen
 * @date 2020-01-13 11:00
 */
public class AgentMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentMain.class);

    public static void premain(String args, Instrumentation instrumentation) {
        LOGGER.info("...start premain...args={}", args);
        // VM 参数形式运行目标程序 -javaagent:本程序.jar
        instrumentation.addTransformer(new AgentTransformer(), true);
        LOGGER.info("...end premain...");
    }

    public static void main(String[] args) {
        int id;
        do {
            System.out.println("List of VM:");
            List<VirtualMachineDescriptor> list = VirtualMachine.list();
            for (VirtualMachineDescriptor vmd : list) {
                System.out.printf("%s\t%s\n", vmd.id(), vmd.displayName());
            }

            System.out.println("Input id(-1 to exit, 0 to flush list):");
            Scanner in = new Scanner(System.in);
            id = in.nextInt();
        } while (id == 0);
        if (id < 0) {
            System.out.println("Exit.");
            return;
        }
        try {
            File lib = Path.of("example-agent", "target", "example-agent-1.0-SNAPSHOT-jar-with-dependencies.jar").toFile();
            System.out.println("load: " + lib);
            VirtualMachine vm = VirtualMachine.attach(String.valueOf(id));
            vm.loadAgent(lib.getAbsolutePath());
            vm.detach();
        } catch (AttachNotSupportedException | IOException | AgentInitializationException | AgentLoadException e) {
            e.printStackTrace();
        }
    }

    public static void agentmain(String args, Instrumentation instrumentation) {
        // attach 形式
        LOGGER.info("...start agentmain...args={}", args);
        instrumentation.addTransformer(new AgentTransformer(), true);
        LOGGER.info("all loaded classes: {}", Arrays.asList(instrumentation.getAllLoadedClasses()));
        LOGGER.info("initiated classes: {}", Arrays.asList(instrumentation.getInitiatedClasses(Thread.currentThread().getContextClassLoader())));
        Arrays.stream(instrumentation.getAllLoadedClasses())
                .filter(clazz -> AgentTransformer.TARGET_CLASS_NAME_DOT.equals(clazz.getName()))
                .findAny()
                .ifPresent(clazz -> {
                    try {
                        LOGGER.info("retransformClass: {}", clazz);
                        instrumentation.retransformClasses(clazz);
                    } catch (Throwable e) {
                        LOGGER.warn("", e);
                    }
                });
        LOGGER.info("...end agentmain...");
    }


}
