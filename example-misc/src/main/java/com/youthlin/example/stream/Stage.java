package com.youthlin.example.stream;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 一个操作对应的一个阶段
 *
 * @param <T> 当前阶段的元素类型
 * @author youthlin.chen
 * @date 2019-07-18 21:36
 */
interface Stage<T> extends Consumer<T> {
    /**
     * 操作开始
     *
     * @param size 元素个数 未知时是 {@link Visitor#UNKNOWN_SIZE}
     */
    default void begin(long size) {
    }

    /**
     * 操作结束
     */
    default void end() {
    }

    /**
     * 是否可以停止操作而无需遍历剩余元素
     *
     * @return true if can finish
     */
    default boolean canFinish() {
        return false;
    }

    /**
     * 操作链
     */
    abstract class AbstractChainedStage<T, E_OUT> implements Stage<T> {
        final Stage<? super E_OUT> downstream;

        AbstractChainedStage(Stage<? super E_OUT> downstream) {
            this.downstream = Objects.requireNonNull(downstream);
        }

        @Override
        public void begin(long size) {
            downstream.begin(size);
        }

        @Override
        public void end() {
            downstream.end();
        }

        @Override
        public boolean canFinish() {
            return downstream.canFinish();
        }
    }

    /**
     * 终止操作
     *
     * @param <E_IN> 当前阶段的元素类型
     * @param <OUT>  该终止操作将会产生的类型
     * @author youthlin.chen
     * @date 2019-07-19 09:51
     */
    interface TerminalAction<E_IN, OUT> extends Stage<E_IN> {

        /**
         * 开始对串联的流进行每个阶段的操作并返回最终结果
         *
         * @param in     源迭代器
         * @param <S_IN> 源迭代器元素的类型
         * @return 该终止操作返回的结果
         */
        <S_IN> OUT startAndGet(Visitor<S_IN> in);
    }

}
