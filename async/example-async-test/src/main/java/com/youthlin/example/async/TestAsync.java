package com.youthlin.example.async;

import com.youthlin.example.annotation.async;
import com.youthlin.example.annotation.await;

import java.util.concurrent.CompletableFuture;

/**
 * @author youthlin.chen
 * @date 2020-07-14 14:10:38
 */
public class TestAsync {
    public static void main(String[] args) throws InterruptedException {
        test(10);
        Thread.sleep(1000);
    }

    @async
    private static void test(int x) {
        @await int doubleNum = doubleNum(x);
        @await int halfNum = halfNum(x);
        System.out.println(doubleNum);
        System.out.println(halfNum);
        System.out.println(hello());
    }

    // test => testAsync

    private static CompletableFuture<Void> testAsync(int x) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        try {
            doubleNumAsync(x).whenComplete((doubleNum, e1) -> {
                if (e1 != null) {
                    result.completeExceptionally(e1);
                    return;
                }
                halfNumAsync(x).whenComplete(((halfNum, e2) -> {
                    if (e2 != null) {
                        result.completeExceptionally(e2);
                        return;
                    }
                    System.out.println(doubleNum);
                    System.out.println(halfNum);
                    System.out.println(hello());
                }));
            });
            result.complete(null);
        } catch (Throwable t) {
            result.completeExceptionally(t);
        }
        return result;
    }

    @async
    private static Integer doubleNum(int input) {
        return input * 2;
    }

    private static CompletableFuture<Integer> doubleNumAsync(int input) {
        CompletableFuture<Integer> result = new CompletableFuture<>();
        try {
            result.complete(input * 2);
        } catch (Throwable t) {
            result.completeExceptionally(t);
        }
        return result;
    }

    @async
    private static int halfNum(int input) {
        return input / 2;
    }

    private static CompletableFuture<Integer> halfNumAsync(int input) {
        CompletableFuture<Integer> result = new CompletableFuture<>();
        try {
            result.complete(input / 2);
        } catch (Throwable t) {
            result.completeExceptionally(t);
        }
        return result;
    }

    @async
    private static String hello() {
        return "Hello";
    }

    private static CompletableFuture<String> helloAsync() {
        CompletableFuture<String> result = new CompletableFuture<>();
        try {
            result.complete("Hello");
        } catch (Throwable t) {
            result.completeExceptionally(t);
        }
        return result;
    }

}
