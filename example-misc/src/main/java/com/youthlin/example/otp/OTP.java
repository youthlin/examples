package com.youthlin.example.otp;

import com.google.common.io.Files;
import kotlin.text.Charsets;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * @author youthlin.chen
 * @date 2019-08-12 15:22
 */
@SuppressWarnings("UnstableApiUsage")
public class OTP {
    private static final String DEFAULT = "default";

    public static void main(String[] args) {
        try {
            Entry entry;
            switch (args.length) {
                case 0:
                    entry = fromFile(DEFAULT);
                    break;
                case 1:
                    String arg = args[0];
                    try {
                        entry = Entry.of(URI.create(arg).toString());
                        saveToFile(DEFAULT, entry.getUrl());
                    } catch (Exception e) {
                        entry = fromFile(args[0]);
                    }
                    break;
                case 2:
                    entry = Entry.of(args[1]);
                    saveToFile(args[0], entry.getUrl());
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            entry.updateToken();
            System.out.println(entry.getCurrentToken());
        } catch (Exception e) {
            help(e);
        }
    }

    private static File dir() {
        String home = System.getenv("user.home");
        if (home == null) {
            home = System.getenv("USERPROFILE");
        }
        File dir = new File(home, ".otp");
        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        return dir;
    }

    private static File file(String name) {
        return new File(dir(), name);
    }

    private static void saveToFile(String name, String url) {
        try {
            File file = file(name);
            Files.write(url.getBytes(Charsets.UTF_8), file);
        } catch (IOException e) {
            throw new RuntimeException("Save file error:" + name, e);
        }
    }

    private static Entry fromFile(String name) {
        try {
            File file = file(name);
            String read = Files.asCharSource(file, Charsets.UTF_8).read();
            return Entry.of(read);
        } catch (IOException e) {
            throw new RuntimeException("You May run `"
                    + OTP.class.getSimpleName() + " url` first to store your otp url.", e);
        }
    }

    private static void help(Throwable e) {
        System.err.println(e.getMessage());
        System.out.println(OTP.class.getSimpleName());
        System.out.println("\tUse default otp, and prints the current code.");
        System.out.println("\tDefault otp is store at: " + file(DEFAULT).getAbsolutePath());
        System.out.println(OTP.class.getSimpleName() + " nameOrUrl");
        System.out.println("\tUse the specified otp, and prints the current code.");
        System.out.println("\tWhen nameOrUrl is a name, use stored file: " + file("`name`"));
        System.out.println("\tWhen nameOrUrl is a url, use and store it as default: " + file(DEFAULT).getAbsolutePath());
        System.out.println(OTP.class.getSimpleName() + " name url");
        System.out.println("\tUse the specified otp. and prints the current code.");
        System.out.println("\tAnd store it by specified name on:" + dir().getAbsolutePath());
    }

}
