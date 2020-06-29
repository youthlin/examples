package com.youthlin.example.future;

import java.util.concurrent.CompletableFuture;

/**
 * @author youthlin.chen @ 2020-06-29 10:49:11
 */
public class FutureTest {
    public static void main(String[] args) {
        // 实际开发一般都是一条链走到底，这里为了 Debug 好对比哪个实例是哪个 故分开写变量
        CompletableFuture<String> hello = new CompletableFuture<>();
        CompletableFuture<Void> print = hello.thenAccept(System.out::println);
        CompletableFuture<String> upper = hello.thenApply(String::toUpperCase);
        CompletableFuture<Void> v1 = upper.thenAccept(System.out::println);
        CompletableFuture<Void> v2 = print.thenCombine(upper, (aVoid, s) -> s.toCharArray())
                .thenCompose(chars -> CompletableFuture.completedFuture(chars.length))
                .thenAccept(System.out::println);
        hello.complete("Hello");
    }

    private static void create() {
        // 新建一个已经完成的
        CompletableFuture.completedFuture("");
        CompletableFuture.failedFuture(new Exception());

        // 类似 SettableFuture.create() 新建一个壳子
        CompletableFuture<String> hello = new CompletableFuture<>();
        // 然后在合适的时机使它完成
        hello.complete("");
        hello.completeExceptionally(new Exception());

        // 异步运行
        CompletableFuture.runAsync(() -> System.out.println("CompletableFuture<Void>"));
        // 异步计算
        CompletableFuture.supplyAsync(() -> "CompletableFuture<String>");
    }

    private static void then() {
        CompletableFuture.completedFuture("")
                // 完成后执行一个 Function 进行数据转换 类似 Stream.map
                .thenApply(String::length)
                // 联合另一个 CompletableFuture，都完成后执行一个 BiFunction 进行数据转换
                .thenCombine(CompletableFuture.completedFuture(1), (length, num) -> "len+num=" + length + num)
                // 当两个都完成后执行一个动作 返回新的 CompletableFuture<Void>
                .thenAcceptBoth(CompletableFuture.completedFuture(2), (s, num) -> System.out.printf("%s,%d\n", s, num))
                .thenApply(v -> "s")
                // 将完成后的结果转换为另一个 CompletableFuture 类似 Stream.flatMap
                .thenCompose(s -> CompletableFuture.completedStage("compose:" + s))
                // 当出现异常时 处理异常并返回异常时的值
                .exceptionally(e -> "之前是什么类型这里就需要返回什么类型")
                // 完成后执行的动作 返回新的 CompletableFuture<Void>
                .thenAccept(System.out::println)
                // 类似 Accept 完成后执行的动作，返回新的 CompletableFuture<Void>
                .whenComplete((result, ex) -> {
                })
                // 类似 Apply 完成后执行的动作，返回新的 CompletableFuture<U>
                .handle((result, ex) -> "new")
        ;
    }

}
