package com.youthlin.example.stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @param <IN>  流入元素类型
 * @param <OUT> 流出元素类型
 * @author youthlin.chen
 * @date 2019-07-18 20:49
 */
abstract class BaseFlow<IN, OUT> implements Flow<OUT> {
    static final long MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    /**
     * 最初迭代器
     */
    private Visitor<?> source;
    /**
     * 前一个流
     */
    private BaseFlow prev;
    /**
     * 该流是否被消费过
     */
    private boolean linkedOrConsumed;

    BaseFlow(Visitor<?> in) {
        source = Objects.requireNonNull(in);
    }

    BaseFlow(BaseFlow prev) {
        Objects.requireNonNull(prev);
        Preconditions.checkArgument(!prev.linkedOrConsumed);
        prev.linkedOrConsumed = true;
        this.source = prev.source;
        this.prev = prev;
    }

    //region 无状态操作

    @Override
    public Flow<OUT> filter(Predicate<? super OUT> predicate) {
        Objects.requireNonNull(predicate);
        return new BaseFlow<OUT, OUT>(this) {
            @Override
            Stage<OUT> wrapDownstream(Stage<OUT> nextStage) {
                return new Stage.AbstractChainedStage<OUT, OUT>(nextStage) {
                    @Override
                    public void begin(long size) {
                        /*因为过滤后数量可能改变 所以是不确定个数*/
                        downstream.begin(Visitor.UNKNOWN_SIZE);
                    }

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

    /**
     * @param <R> 转换之后的元素类型
     */
    @Override
    public <R> Flow<R> map(Function<? super OUT, ? extends R> mapper) {
        Objects.requireNonNull(mapper);
        //返回一个流 输入是本流的输出 输出是转换后的类型
        return new BaseFlow<OUT, R>(this) {
            @Override
            Stage<OUT> wrapDownstream(Stage<R> nextStage) {
                return new Stage.AbstractChainedStage<OUT, R>(nextStage) {
                    @Override
                    public void accept(OUT prevOut) {
                        downstream.accept(mapper.apply(prevOut));
                    }
                };
            }
        };
    }

    @Override
    public <R> Flow<R> flatMap(Function<? super OUT, ? extends Flow<? extends R>> mapper) {
        Objects.requireNonNull(mapper);
        return new BaseFlow<OUT, R>(this) {
            @Override
            Stage<OUT> wrapDownstream(Stage<R> nextStage) {
                return new Stage.AbstractChainedStage<OUT, R>(nextStage) {
                    @Override
                    public void begin(long size) {
                        downstream.begin(Visitor.UNKNOWN_SIZE);
                    }

                    @Override
                    public void accept(OUT prevOut) {
                        Flow<? extends R> result = mapper.apply(prevOut);
                        result.forEach(downstream);
                    }
                };
            }
        };
    }
    //endregion 无状态操作

    @Override
    public Flow<OUT> peek(Consumer<? super OUT> action) {
        return new BaseFlow<OUT, OUT>(this) {
            @Override
            Stage<OUT> wrapDownstream(Stage<OUT> nextStage) {
                return new Stage.AbstractChainedStage<OUT, OUT>(nextStage) {
                    @Override
                    public void accept(OUT out) {
                        action.accept(out);
                        downstream.accept(out);
                    }
                };
            }
        };
    }

    //region 有状态操作

    @Override
    public Flow<OUT> distinct() {
        return new BaseFlow<OUT, OUT>(this) {
            @Override
            Stage<OUT> wrapDownstream(Stage<OUT> nextStage) {
                return new Stage.AbstractChainedStage<OUT, OUT>(nextStage) {
                    private Set<OUT> set;

                    @Override
                    public void begin(long size) {
                        set = new HashSet<>();
                        downstream.begin(Visitor.UNKNOWN_SIZE);
                    }

                    @Override
                    public void accept(OUT out) {
                        if (!set.contains(out)) {
                            set.add(out);
                            downstream.accept(out);
                        }
                    }

                    @Override
                    public void end() {
                        set = null;
                        downstream.end();
                    }
                };
            }
        };
    }

    @Override
    @SuppressWarnings({"RedundantCast", "unchecked"})
    public Flow<OUT> sorted() {
        return sorted((Comparator<OUT>) Comparator.naturalOrder());
    }

    @Override
    public Flow<OUT> sorted(Comparator<OUT> comparator) {
        return new BaseFlow<OUT, OUT>(this) {
            @Override
            Stage<OUT> wrapDownstream(Stage<OUT> nextStage) {
                return new Stage.AbstractChainedStage<OUT, OUT>(nextStage) {
                    private List<OUT> list;

                    @Override
                    public void begin(long size) {
                        Preconditions.checkArgument(size < MAX_ARRAY_SIZE,
                                "max array size exceed: %s", MAX_ARRAY_SIZE);
                        if (size > 0) {
                            list = Lists.newArrayListWithExpectedSize((int) size);
                        } else {
                            list = Lists.newArrayList();
                        }
                    }

                    @Override
                    public void accept(OUT out) {
                        Preconditions.checkArgument(list.size() < MAX_ARRAY_SIZE,
                                "max array size exceed: %s", MAX_ARRAY_SIZE);
                        list.add(out);
                    }

                    @Override
                    public void end() {
                        list.sort(comparator);
                        downstream.begin(list.size());
                        VisitorImpl<OUT> visitor = VisitorImpl.of(list);
                        while (visitor.hasNext() && !canFinish()) {
                            downstream.accept(visitor.next());
                        }
                        list = null;
                        downstream.end();
                    }
                };
            }
        };
    }

    @Override
    public Flow<OUT> limit(long maxSize) {
        Preconditions.checkArgument(maxSize >= 0);
        return new BaseFlow<OUT, OUT>(this) {
            @Override
            Stage<OUT> wrapDownstream(Stage<OUT> nextStage) {
                return new Stage.AbstractChainedStage<OUT, OUT>(nextStage) {
                    private int count = 0;

                    @Override
                    public void begin(long size) {
                        downstream.begin(Visitor.UNKNOWN_SIZE);
                    }

                    @Override
                    public void accept(OUT out) {
                        downstream.accept(out);
                        count++;
                    }

                    @Override
                    public boolean canFinish() {
                        return count == maxSize;
                    }
                };
            }
        };
    }

    @Override
    public Flow<OUT> skip(long n) {
        Preconditions.checkArgument(n >= 0);
        return new BaseFlow<OUT, OUT>(this) {
            @Override
            Stage<OUT> wrapDownstream(Stage<OUT> nextStage) {
                return new Stage.AbstractChainedStage<OUT, OUT>(nextStage) {
                    private int count = 0;

                    @Override
                    public void begin(long size) {
                        downstream.begin(Visitor.UNKNOWN_SIZE);
                    }

                    @Override
                    public void accept(OUT out) {
                        if (count++ >= n) {
                            downstream.accept(out);
                        }
                    }
                };
            }
        };
    }


    //endregion 有状态操作

    //region 终止操作帮助方法

    /**
     * 终止操作的基类
     *
     * @param <R> 该终止操作的返回类型 通过 {@link Supplier#get()} 返回结果
     * @see Supplier#get()
     */
    static abstract class BaseTerminal<IN, OUT, R> implements Stage.TerminalAction<OUT, R>, Supplier<R> {
        private BaseFlow<IN, OUT> baseFlow;

        BaseTerminal(BaseFlow<IN, OUT> baseFlow) {
            this.baseFlow = baseFlow;
        }

        @Override
        public <S_IN> R startAndGet(Visitor<S_IN> in) {
            baseFlow.start(in, this);
            return get();
        }

    }

    /**
     * 终止方法
     *
     * @param <O>            返回类型
     * @param terminalAction 终止操作
     */
    private <O> O terminal(Stage.TerminalAction<OUT, O> terminalAction) {
        return terminalAction.startAndGet(source);
    }

    /**
     * 开始执行整串流各阶段的操作
     */
    private <I> void start(Visitor<I> in, Stage<OUT> lastStage) {
        Stage<I> stage = wrapStage(lastStage);
        stage.begin(in.getSizeIfKnown());
        while (in.hasNext() && !stage.canFinish()) {
            stage.accept(in.next());
        }
        stage.end();
    }

    /**
     * 将每个阶段的操作串起来 真正开始处理流时调用
     *
     * @param stage  最后一个操作
     * @param <S_IN> 源迭代器的元素类型
     * @return 将整个串的每个操作串起来作为一个操作
     */
    @SuppressWarnings("unchecked")
    private <S_IN> Stage<S_IN> wrapStage(Stage<OUT> stage) {
        for (BaseFlow flow = this; flow.prev != null; flow = flow.prev) {
            /* flow.prev != null 即头节点不参与*/
            stage = flow.wrapDownstream(stage);
        }
        return (Stage<S_IN>) stage;
    }

    /**
     * 各个阶段自定义如何将本阶段串入整个流串
     * 在终止操作时调用 {@link #wrapStage(Stage)} 该方法调用本方法
     *
     * @param nextStage 下游操作 处理的元素是本阶段流的产出元素
     * @return 将下游操作包裹的一个操作 该操作处理的元素类型是 &lt;IN&gt; 类型
     */
    abstract Stage<IN> wrapDownstream(Stage<OUT> nextStage);

    //endregion 终止操作帮助方法

    //region 终止操作

    @Override
    public void forEach(Consumer<? super OUT> action) {
        // 遍历不需要返回内容所以是 Void
        terminal(new BaseTerminal<IN, OUT, Void>(this) {
            @Override
            public Void get() {
                return null;
            }

            @Override
            public void accept(OUT out) {
                action.accept(out);
            }
        });
    }

    /**
     * Java 8 的 {@link java.util.stream.Stream#forEach(Consumer)}
     * 不保证遍历顺序就是流中元素的顺序 因为可能是并行访问的
     * 要按流中的顺序遍历则应使用
     * {@link java.util.stream.Stream#forEachOrdered(Consumer)}
     * 这里我们实现的更简单，{@link #forEach(Consumer)} 已经是按序的
     */
    @Override
    public void forEachOrdered(Consumer<? super OUT> action) {
        forEach(action);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object[] toArray() {
        return toArray(size -> (OUT[]) new Object[size]);
    }

    @Override
    public OUT[] toArray(IntFunction<OUT[]> generator) {
        return terminal(new BaseTerminal<IN, OUT, OUT[]>(this) {
            private List<OUT> list;
            private OUT[] array;
            private int idx;

            @Override
            public OUT[] get() {
                OUT[] result = this.array;
                array = null;
                return result;
            }

            @Override
            public void begin(long size) {
                if (size >= 0) {
                    array = generator.apply((int) size);
                } else {
                    list = Lists.newArrayList();
                }
            }

            @Override
            public void accept(OUT out) {
                if (array != null) {
                    array[idx++] = out;
                } else {
                    Preconditions.checkArgument(list.size() < MAX_ARRAY_SIZE,
                            "max array size exceed: %s", MAX_ARRAY_SIZE);
                    list.add(out);
                }
            }

            @Override
            public void end() {
                if (array != null) {
                    Preconditions.checkArgument(idx == array.length);
                } else {
                    array = generator.apply(list.size());
                    list.toArray(array);
                    list = null;
                }
            }
        });

    }

    @Override
    public OUT reduce(OUT identity, BinaryOperator<OUT> accumulator) {
        Objects.requireNonNull(accumulator);
        return terminal(new BaseTerminal<IN, OUT, OUT>(this) {
            private OUT result;

            @Override
            public OUT get() {
                OUT ret = this.result;
                result = null;
                return ret;
            }

            @Override
            public void begin(long size) {
                result = identity;
            }

            @Override
            public void accept(OUT out) {
                result = accumulator.apply(result, out);
            }
        });
    }

    @Override
    public Optional<OUT> reduce(BinaryOperator<OUT> accumulator) {
        Objects.requireNonNull(accumulator);
        return terminal(new BaseTerminal<IN, OUT, Optional<OUT>>(this) {
            private OUT result;
            private boolean hasElement;

            @Override
            public Optional<OUT> get() {
                OUT ret = this.result;
                result = null;
                return hasElement ? Optional.of(ret) : Optional.empty();
            }

            @Override
            public void accept(OUT out) {
                if (hasElement) {
                    result = accumulator.apply(result, out);
                } else {
                    result = out;
                    hasElement = true;
                }
            }
        });
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super OUT, U> accumulator, BinaryOperator<U> combiner) {
        return terminal(new BaseTerminal<IN, OUT, U>(this) {
            private U result;

            @Override
            public U get() {
                U ret = this.result;
                result = null;
                return ret;
            }

            @Override
            public void begin(long size) {
                result = identity;
            }

            @Override
            public void accept(OUT out) {
                result = accumulator.apply(result, out);
            }
        });
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super OUT> accumulator, BiConsumer<R, R> combiner) {
        return terminal(new BaseTerminal<IN, OUT, R>(this) {
            private R result;

            @Override
            public R get() {
                R ret = this.result;
                result = null;
                return ret;
            }

            @Override
            public void begin(long size) {
                result = supplier.get();
            }

            @Override
            public void accept(OUT out) {
                accumulator.accept(result, out);
            }
        });
    }

    @Override
    public <R, A> R collect(Collector<? super OUT, A, R> collector) {
        return terminal(new BaseTerminal<IN, OUT, R>(this) {
            private A tmp;

            @Override
            public R get() {
                A result = tmp;
                tmp = null;
                return collector.finisher().apply(result);
            }

            @Override
            public void begin(long size) {
                tmp = collector.supplier().get();
            }

            @Override
            public void accept(OUT out) {
                collector.accumulator().accept(tmp, out);
            }
        });
    }

    @Override
    public Optional<OUT> min(Comparator<? super OUT> comparator) {
        return reduce(BinaryOperator.minBy(comparator));
    }

    @Override
    public Optional<OUT> max(Comparator<? super OUT> comparator) {
        return reduce(BinaryOperator.maxBy(comparator));
    }

    @Override
    public long count() {
        return map(x -> 1L).reduce(0L, Long::sum);
    }

    /**
     * 任意一个元素满足条件
     * 空的流返回 false
     */
    @Override
    public boolean anyMatch(Predicate<? super OUT> predicate) {
        return terminal(new BaseTerminal<IN, OUT, Boolean>(this) {
            private boolean result;
            private boolean canFinish;

            @Override
            public Boolean get() {
                return result;
            }

            @Override
            public void accept(OUT out) {
                if (!canFinish() && predicate.test(out)) {
                    result = true;
                    canFinish = true;
                }
            }

            @Override
            public boolean canFinish() {
                return canFinish;
            }
        });
    }

    /**
     * 所有元素满足条件
     * 空的流返回 true
     */
    @Override
    public boolean allMatch(Predicate<? super OUT> predicate) {
        return terminal(new BaseTerminal<IN, OUT, Boolean>(this) {
            private boolean result = true;

            @Override
            public Boolean get() {
                return result;
            }

            @Override
            public void accept(OUT out) {
                if (!predicate.test(out)) {
                    result = false;
                }
            }

        });
    }

    /**
     * 没有元素满足条件
     * 空的流返回 true
     */
    @Override
    public boolean noneMatch(Predicate<? super OUT> predicate) {
        return terminal(new BaseTerminal<IN, OUT, Boolean>(this) {
            private boolean result = true;

            @Override
            public Boolean get() {
                return result;
            }

            @Override
            public void accept(OUT out) {
                if (predicate.test(out)) {
                    result = false;
                }
            }

        });
    }

    @Override
    public Optional<OUT> findFirst() {
        return terminal(new BaseTerminal<IN, OUT, Optional<OUT>>(this) {
            private OUT result;
            private boolean find;

            @Override
            public Optional<OUT> get() {
                OUT ret = result;
                result = null;
                return find ? Optional.of(ret) : Optional.empty();
            }

            @Override
            public void accept(OUT out) {
                if (!canFinish()) {
                    result = out;
                    find = true;
                }
            }

            @Override
            public boolean canFinish() {
                return find;
            }
        });
    }

    @Override
    public Optional<OUT> findAny() {
        return findFirst();
    }

    //endregion 终止操作

    /**
     * 头节点
     */
    static class Head<E_IN, E_OUT> extends BaseFlow<E_IN, E_OUT> {
        Head(Visitor<?> in) {
            super(in);
        }

        @Override
        Stage<E_IN> wrapDownstream(Stage<E_OUT> nextStage) {
            throw new AssertionError("should not go to here.");
        }
    }

}
