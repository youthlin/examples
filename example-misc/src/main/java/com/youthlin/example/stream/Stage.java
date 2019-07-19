package com.youthlin.example.stream;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author youthlin.chen
 * @date 2019-07-18 21:36
 */
public interface Stage<T> extends Consumer<T> {

    default void begin(long size) {
    }

    default void end() {
    }

    default boolean canFinish() {
        return false;
    }

    abstract class AbstractChainedStage<T, E_OUT> implements Stage<T> {
        protected final Stage<? super E_OUT> downstream;

        public AbstractChainedStage(Stage<? super E_OUT> downstream) {
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

}
