package com.youthlin.example.reflect;

import java.io.Serializable;

/**
 * @author : youthlin.chen @ 2019-11-02 15:22
 */
public class MyClass {
    public interface A {
        CharSequence get();
    }

    public interface B {
        Serializable get();
    }

    private static class ClassInner implements A, B {
        @Override
        public String get() {
            return "ClassInner";
        }
    }

    public void sayHello() {
        System.out.println("Hello!");
    }

    public A getALambda() {
        return () -> "Lambda";
    }

    public A getAMethodClass() {
        class MethodInner implements A {
            @Override
            public String get() {
                return "MethodInner";
            }
        }
        return new MethodInner();
    }

    public A getAClassInner() {
        return new ClassInner();
    }
}
