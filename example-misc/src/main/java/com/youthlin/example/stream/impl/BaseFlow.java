package com.youthlin.example.stream.impl;

import com.youthlin.example.stream.Flow;
import com.youthlin.example.stream.Stage;
import com.youthlin.example.stream.TerminalAction;
import com.youthlin.example.stream.Visitor;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author youthlin.chen
 * @date 2019-07-18 20:49
 */
public abstract class BaseFlow<IN, OUT> implements Flow<OUT> {
    private BaseFlow source;
    private BaseFlow prev;
    private BaseFlow next;
    private Visitor<?> visitor;

    BaseFlow(Visitor<?> in) {
        source = this;
        visitor = in;
    }

    BaseFlow(BaseFlow prev) {
        this.source = prev.source;
        this.prev = prev;
        prev.next = this;
    }

    //region 无状态操作

    @Override
    public Flow<OUT> filter(Predicate<? super OUT> predicate) {
        Objects.requireNonNull(predicate);
        return new BaseFlow<OUT, OUT>(this) {
            @Override
            Stage<OUT> onWrapStage(Stage<OUT> stage) {
                return new Stage.AbstractChainedStage<OUT, OUT>(stage) {
                    @Override
                    public void accept(OUT o) {
                        if (predicate.test(o)) {
                            downstream.accept(o);
                        }
                    }
                };
            }
        };
    }

    //endregion 无状态操作

    //region 有状态操作


    //endregion 有状态操作

    //region 终止操作

    @Override
    public void forEach(Consumer<? super OUT> action) {
        terminal(new TerminalAction<OUT, Void>() {
            @Override
            public void accept(OUT in) {
                action.accept(in);
            }

            @Override
            public <I> Void finish(Visitor<I> in) {
                Stage<Object> stage = wrapStage(this);
                stage.begin(in.getSizeIfKnown());
                in.forEachRemaining(stage);
                stage.end();
                return null;
            }
        });
    }

    //endregion 终止操作

    @SuppressWarnings("unchecked")
    final <P_IN> Stage<P_IN> wrapStage(Stage<OUT> stage) {
        for (BaseFlow f = this; f.prev != null; f = f.prev) {
            stage = f.onWrapStage(stage);
        }
        return (Stage<P_IN>) stage;
    }

    abstract Stage<IN> onWrapStage(Stage<OUT> stage);

    @SuppressWarnings("unchecked")
    private <O> O terminal(TerminalAction<OUT, O> terminalAction) {
        return (O) terminalAction.finish(source.visitor);
    }

    public static class Head<E_IN, E_OUT> extends BaseFlow<E_IN, E_OUT> {

        public Head(Visitor<?> in) {
            super(in);
        }

        @Override
        Stage<E_IN> onWrapStage(Stage<E_OUT> stage) {
            throw new UnsupportedOperationException();
        }
    }

}
