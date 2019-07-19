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
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface Flow<T> {
    //region 无状态操作

    /**
     * 过滤 无状态操作
     *
     * @param predicate 过滤条件
     * @return 过滤掉不符合条件元素的流
     */
    Flow<T> filter(Predicate<? super T> predicate);

    /**
     * 转换
     *
     * @param mapper 转换方法
     * @return 转换元素后的流
     */
    <R> Flow<R> map(Function<? super T, ? extends R> mapper);

    /**
     * 转换元素为流
     *
     * @param mapper 将每个元素转换为流
     * @return 以‘每个元素转换后的流’为元素的流
     */
    <R> Flow<R> flatMap(Function<? super T, ? extends Flow<? extends R>> mapper);

    /**
     * 访问每个元素
     *
     * @param action 访问时执行的动作
     * @return 包含同样元素的流
     */
    Flow<T> peek(Consumer<? super T> action);

    //endregion 无状态操作

    //region 有状态操作

    /**
     * 去重
     *
     * @return 去重后的流
     */
    Flow<T> distinct();

    /**
     * 排序 按元素的自然顺序
     *
     * @return 排序后的流
     * @see Comparator#naturalOrder()
     */
    Flow<T> sorted();

    /**
     * 按指定比较器排序
     *
     * @param comparator 比较器
     * @return 排序后的流
     */
    Flow<T> sorted(Comparator<T> comparator);

    /**
     * 限制元素个数
     *
     * @param maxSize 最大个数
     * @return 限制个数后的流
     */
    Flow<T> limit(long maxSize);

    /**
     * 跳过指定数目个元素
     *
     * @param n 指定个数
     * @return 跳过指定个数后的流
     */
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

    /**
     * 转为数组
     *
     * @return Object[] 数组 包含流中的元素
     * @throws IllegalStateException 当元素个数太大
     */
    Object[] toArray();

    /**
     * 转为数组
     *
     * @param generator 指定大小数组的生成器
     * @return T[] 数组 包含流中的元素
     * @throws IllegalStateException 当元素个数太大
     */
    T[] toArray(IntFunction<T[]> generator);

    /**
     * 消费流中元素
     * 以指定单位值为初始值，将每个元素通过累加器加到初始值上生成结果值
     *
     * @param identity    指定的初始单位值
     * @param accumulator 累加器
     * @return 结果
     */
    T reduce(T identity, BinaryOperator<T> accumulator);

    /**
     * 消费流中元素
     * 将每个元素通过累加器累加
     *
     * @param accumulator 累加器
     * @return 如果流中有元素则返回结果值否则返回 {@link Optional#empty()}
     */
    Optional<T> reduce(BinaryOperator<T> accumulator);

    /**
     * 消费每个元素
     *
     * @param <U>         结果类型
     * @param identity    初始值
     * @param accumulator 累加器
     * @param combiner    合并器 可能会并行累加 用合并器合并累加的结果
     * @return 结果
     */
    <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner);

    /**
     * 收集元素
     *
     * @param <R>         结果类型
     * @param supplier    生成 R 的初始值
     * @param accumulator 累加器 将每个元素累加到初始值上
     * @param combiner    合并器 合并每个累加后的容器 当并行执行时可能用到
     * @return 收集内容
     */
    <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner);

    /**
     * 收集元素
     *
     * @param collector 收集器
     * @return 收集到的内容
     */
    <R, A> R collect(Collector<? super T, A, R> collector);

    /**
     * 取最小值
     *
     * @param comparator 比较器
     * @return 最小值如果有
     */
    Optional<T> min(Comparator<? super T> comparator);

    /**
     * 取最大值
     *
     * @param comparator 比较器
     * @return 最大值如果有
     */
    Optional<T> max(Comparator<? super T> comparator);

    /**
     * 返回元素个数
     *
     * @return 个数
     */
    long count();

    /**
     * 任意一个元素满足条件
     * 空的流返回 false
     *
     * @param predicate 测试条件
     * @return 任意一个元素满足条件
     */
    boolean anyMatch(Predicate<? super T> predicate);

    /**
     * 所有元素满足条件
     * 空的流返回 true
     *
     * @param predicate 测试条件
     * @return 所有元素满足条件
     */
    boolean allMatch(Predicate<? super T> predicate);

    /**
     * 没有元素满足条件
     * 空的流返回 true
     *
     * @param predicate 测试条件
     * @return 没有元素满足条件
     */
    boolean noneMatch(Predicate<? super T> predicate);

    /**
     * 取第一个
     *
     * @return 第一个元素如果有
     */
    Optional<T> findFirst();

    /**
     * 取任意一个
     *
     * @return 任意一个元素如果有
     */
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

    /**
     * 生成指定元素构成的流
     *
     * @param element 指定的元素
     * @return 只包含指定元素的流
     */
    static <E> Flow<E> of(E element) {
        return new BaseFlow.Head<>(VisitorImpl.of(Collections.singleton(element)));
    }

    /**
     * 生成空的流
     *
     * @return 空的流
     */
    static <E> Flow<E> empty() {
        return of(Collections.emptyList());
    }

    /**
     * 生成无限流
     *
     * @param seed 种子元素
     * @param f    以种子元素作为操作对象的一元操作符 每次调用生成一个元素 并将该元素作为下次调用的入参
     * @return 生成的无限流
     */
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

    /**
     * 生成无限流
     *
     * @param s 每次生成一个元素
     * @return 生成的无限流
     */
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
