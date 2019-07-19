package com.youthlin.example.stream;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author youthlin.chen
 * @date 2019-07-19 09:58
 */
public class StreamTest {
    public static void main(String[] args) {
        ArrayList<Integer> list = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 9, 9, 9, 1, 2, 3, 2, 3, 4);
        String[] strings = Flow.of(list)
                .filter(x -> x % 2 == 0)
                .map(x -> x + "s")
                .limit(30)
                .skip(1)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toArray(String[]::new);
        System.out.println(Arrays.toString(strings));
        System.out.println("-------");
        ArrayList<ArrayList<Integer>> tow = Lists.newArrayList(list, list);
        Flow.of(tow)
                .flatMap(in -> Flow.of(in).skip(8).map(x -> x + "a").limit(5))
                .forEach(System.out::println);
        System.out.println("-------");
        Integer sum = Flow.of('a', 'b', 'c', 'e', 'd')
                .sorted()
                .map(x -> x - 'a')
                .peek(System.out::println)
                .reduce(0, Integer::sum);
        System.out.println(sum);
        System.out.println("-------");
        System.out.println(Flow.of(1, 2, 3).reduce(Integer::sum));
        System.out.println("-------");
        System.out.println(Flow.<Integer>of().reduce(Integer::sum));
        System.out.println("-------");
        ArrayList<Integer> reduce = Flow.of(1, 2, 3, 4, 5)
                .sorted(Comparator.reverseOrder())
                .reduce(Lists.newArrayList(),
                        (acc, e) -> {
                            acc.add(e);
                            return acc;
                        },
                        (acc, other) -> {
                            acc.addAll(other);
                            return acc;
                        }
                );
        System.out.println(reduce);
        System.out.println("-------");
        List<Integer> collect = Flow.of(4, 3, 2, 1)
                .sorted()
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        System.out.println(collect);
        System.out.println("-------");
        collect = Flow.of(4, 3, 2, 1)
                .sorted()
                .collect(Collectors.toList());
        System.out.println(collect);
        System.out.println("-------");

        Object[] array = Flow.of(4, 3, 2, 1)
                .filter(x -> x > 1)
                .map(x -> x + "element")
                //.sorted()
                .toArray();
        System.out.println(Arrays.toString(array));

        System.out.println("-------fibonacci-------");
        Flow.generate(new Supplier<Long>() {
            long a = 0;
            long b = 1;

            @Override
            public Long get() {
                long x = a + b;
                a = b;
                b = x;
                return a;
            }
        })
        //.limit(100)
        //.forEach(System.out::println)
        //.toArray()
        ;
        Flow<Object> flow = Flow.of();
        Object[] x = flow.toArray();
        System.out.println(Arrays.toString(x));
        Flow<Integer> of = Flow.of(1, 2, 3, 4);
        // of.skip(1);
        of.limit(2).forEach(System.out::println);
    }

}
