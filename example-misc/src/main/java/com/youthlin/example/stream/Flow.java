package com.youthlin.example.stream;

import com.google.common.collect.Iterators;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;

/**
 * 自定义流 对应 {@link java.util.stream.Stream}
 *
 * @param <T> 流的元素类型
 * @author youthlin.chen
 * @date 2019-07-18 20:39
 */
public interface Flow<T> {
    //region 无状态操作

    /**
     * 过滤 无状态操作
     *
     * @param predicate 过滤条件
     * @return 过滤掉不符合条件元素的流
     */
    Flow<T> filter(Predicate<? super T> predicate);

    <R> Flow<R> map(Function<? super T, ? extends R> mapper);

    <R> Flow<R> flatMap(Function<? super T, ? extends Flow<? extends R>> mapper);

    Flow<T> peek(Consumer<? super T> action);

    //endregion 无状态操作

    //region 有状态操作

    Flow<T> distinct();

    Flow<T> sorted();

    Flow<T> sorted(Comparator<T> comparator);

    Flow<T> limit(long maxSize);

    Flow<T> skip(long n);

    //endregion 有状态操作

    //region 终止操作

    /**
     * 遍历当前流的元素 终止操作
     *
     * @param action 对每个元素执行的操作
     */
    void forEach(Consumer<? super T> action);

    /**
     * 遍历当前流的元素 终止操作
     * 按流中元素的顺序遍历
     *
     * @param action 对每个元素执行的操作
     */
    void forEachOrdered(Consumer<? super T> action);

    Object[] toArray();

    T[] toArray(IntFunction<T[]> generator);

    T reduce(T identity, BinaryOperator<T> accumulator);

    Optional<T> reduce(BinaryOperator<T> accumulator);

    <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner);

    <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner);

    <R, A> R collect(Collector<? super T, A, R> collector);

    Optional<T> min(Comparator<? super T> comparator);

    Optional<T> max(Comparator<? super T> comparator);

    long count();

    boolean anyMatch(Predicate<? super T> predicate);

    boolean allMatch(Predicate<? super T> predicate);

    boolean noneMatch(Predicate<? super T> predicate);

    Optional<T> findFirst();

    Optional<T> findAny();

    //endregion 终止操作

    //region 静态方法

    /**
     * 静态帮助方法
     * 将集合转为自定义流
     *
     * @param collection 源集合
     * @return collection 对应的自定义流
     */
    static <E> Flow<E> of(Collection<E> collection) {
        return new BaseFlow.Head<>(VisitorImpl.of(collection));
    }

    /**
     * 静态帮助方法
     * 将自定义迭代器转为自定义流
     *
     * @param visitor 自定义迭代器
     * @return 迭代器对应的自定义流
     */
    static <E> Flow<E> of(Visitor<E> visitor) {
        return new BaseFlow.Head<>(visitor);
    }

    /**
     * 静态帮助方法
     * 将数组转为自定义流
     *
     * @param elements 输入数组
     * @return 迭代器对应的自定义流
     */
    @SafeVarargs
    static <E> Flow<E> of(E... elements) {
        return new BaseFlow.Head<>(new VisitorImpl<>(Iterators.forArray(elements)));
    }

    static <E> Flow<E> of(E element) {
        return new BaseFlow.Head<>(VisitorImpl.of(Collections.singleton(element)));
    }

    static <E> Flow<E> empty() {
        return of(Collections.emptyList());
    }

    static <E> Flow<E> iterate(final E seed, final UnaryOperator<E> f) {
        Visitor<E> visitor = new Visitor<E>() {
            private boolean first = true;
            private E element;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public E next() {
                if (first) {
                    first = false;
                    element = seed;
                } else {
                    element = f.apply(element);
                }
                return element;
            }
        };
        return new BaseFlow.Head<>(visitor);
    }

    static <E> Flow<E> generate(Supplier<E> s) {
        return new BaseFlow.Head<>(new Visitor<E>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public E next() {
                return s.get();
            }
        });
    }

    //endregion 静态方法

}
