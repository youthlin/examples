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
 * 为什么需要两个类型:
 * map 操作会转换类型，为了区分转换前后的类型，用两个类型符号表示
 *
 * @param <S> 流入元素类型
 * @param <T> 流出元素类型
 * @author youthlin.chen
 * @date 2019-07-18 20:49
 */
abstract class AbstractFlow<S, T> implements Flow<T> {
    static final long MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    /**
     * 最初迭代器
     */
    private final Visitor<?> source;
    /**
     * 前一个流
     */
    private final AbstractFlow<?, S> prev;
    /**
     * 该流是否被消费过
     */
    private boolean linkedOrConsumed;

    AbstractFlow(Visitor<?> in) {
        source = Objects.requireNonNull(in);
        prev = null;
    }

    AbstractFlow(AbstractFlow<?, S> prev) {
        Objects.requireNonNull(prev);
        Preconditions.checkState(!prev.linkedOrConsumed, "already linked");
        prev.linkedOrConsumed = true;
        this.source = prev.source;
        this.prev = prev;
    }

    //region 无状态操作

    @Override
    public Flow<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return new AbstractFlow<T, T>(this) {
            @Override
            Stage<T> wrapDownstream(Stage<T> nextStage) {
                return new Stage.AbstractChainedStage<T, T>(nextStage) {
                    @Override
                    public void begin(long size) {
                        /*因为过滤后数量可能改变 所以是不确定个数*/
                        downstream.begin(Visitor.UNKNOWN_SIZE);
                    }

                    @Override
                    public void accept(T element) {
                        if (predicate.test(element)) {
                            downstream.accept(element);
                        }
                    }

                    @Override
                    public String toString() {
                        return "Stage.filter";
                    }
                };
            }

            @Override
            public String toString() {
                return "Flow.filter";
            }
        };
    }

    /**
     * @param <R> 转换之后的元素类型
     */
    @Override
    public <R> Flow<R> map(Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper);
        //返回一个流 输入是本流的输出 输出是转换后的类型
        return new AbstractFlow<T, R>(this) {
            @Override
            Stage<T> wrapDownstream(Stage<R> nextStage) {
                return new Stage.AbstractChainedStage<T, R>(nextStage) {
                    @Override
                    public void accept(T element) {
                        downstream.accept(mapper.apply(element));
                    }

                    @Override
                    public String toString() {
                        return "Stage.map";
                    }
                };
            }

            @Override
            public String toString() {
                return "Flow.map";
            }
        };
    }

    @Override
    public <R> Flow<R> flatMap(Function<? super T, ? extends Flow<? extends R>> mapper) {
        Objects.requireNonNull(mapper);
        return new AbstractFlow<T, R>(this) {
            @Override
            Stage<T> wrapDownstream(Stage<R> nextStage) {
                return new Stage.AbstractChainedStage<T, R>(nextStage) {
                    @Override
                    public void begin(long size) {
                        downstream.begin(Visitor.UNKNOWN_SIZE);
                    }

                    @Override
                    public void accept(T element) {
                        Flow<? extends R> result = mapper.apply(element);
                        result.forEach(downstream);
                    }

                    @Override
                    public String toString() {
                        return "Stage.flatMap";
                    }
                };
            }

            @Override
            public String toString() {
                return "Flow.flatMap";
            }
        };
    }

    //endregion 无状态操作

    @Override
    public Flow<T> peek(Consumer<? super T> action) {
        return new AbstractFlow<T, T>(this) {
            @Override
            Stage<T> wrapDownstream(Stage<T> nextStage) {
                return new Stage.AbstractChainedStage<T, T>(nextStage) {
                    @Override
                    public void accept(T element) {
                        action.accept(element);
                        downstream.accept(element);
                    }

                    @Override
                    public String toString() {
                        return "Stage.peek";
                    }
                };
            }

            @Override
            public String toString() {
                return "Flow.peek";
            }
        };
    }

    //region 有状态操作

    @Override
    public Flow<T> distinct() {
        return new AbstractFlow<T, T>(this) {
            @Override
            Stage<T> wrapDownstream(Stage<T> nextStage) {
                return new Stage.AbstractChainedStage<T, T>(nextStage) {
                    private Set<T> set;

                    @Override
                    public void begin(long size) {
                        set = new HashSet<>();
                        downstream.begin(Visitor.UNKNOWN_SIZE);
                    }

                    @Override
                    public void accept(T element) {
                        if (!set.contains(element)) {
                            set.add(element);
                            downstream.accept(element);
                        }
                    }

                    @Override
                    public void end() {
                        set = null;
                        downstream.end();
                    }

                    @Override
                    public String toString() {
                        return "Stage.distinct";
                    }
                };
            }

            @Override
            public String toString() {
                return "Flow.distinct";
            }
        };
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Flow<T> sorted() {
        return sorted((Comparator<T>) Comparator.naturalOrder());
    }

    @Override
    public Flow<T> sorted(Comparator<T> comparator) {
        return new AbstractFlow<T, T>(this) {
            @Override
            Stage<T> wrapDownstream(Stage<T> nextStage) {
                return new Stage.AbstractChainedStage<T, T>(nextStage) {
                    private List<T> list;

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
                    public void accept(T element) {
                        Preconditions.checkArgument(list.size() < MAX_ARRAY_SIZE,
                                "max array size exceed: %s", MAX_ARRAY_SIZE);
                        list.add(element);
                    }

                    @Override
                    public void end() {
                        list.sort(comparator);
                        downstream.begin(list.size());
                        VisitorImpl<T> visitor = VisitorImpl.of(list);
                        while (visitor.hasNext() && !canFinish()) {
                            downstream.accept(visitor.next());
                        }
                        list = null;
                        downstream.end();
                    }

                    @Override
                    public String toString() {
                        return "Stage.sorted";
                    }
                };
            }

            @Override
            public String toString() {
                return "Flow.sorted";
            }
        };
    }

    @Override
    public Flow<T> limit(long maxSize) {
        Preconditions.checkArgument(maxSize >= 0);
        return new AbstractFlow<T, T>(this) {
            @Override
            Stage<T> wrapDownstream(Stage<T> nextStage) {
                return new Stage.AbstractChainedStage<T, T>(nextStage) {
                    private long count = 0;

                    @Override
                    public void begin(long size) {
                        if (size >= 0) {
                            size = Math.min(size, maxSize);
                        } else {
                            size = Visitor.UNKNOWN_SIZE;
                        }
                        downstream.begin(size);
                    }

                    @Override
                    public void accept(T element) {
                        downstream.accept(element);
                        count++;
                    }

                    @Override
                    public boolean canFinish() {
                        return count == maxSize;
                    }

                    @Override
                    public String toString() {
                        return "Stage.limit";
                    }
                };
            }

            @Override
            public String toString() {
                return "Flow.limit";
            }
        };
    }

    @Override
    public Flow<T> skip(long n) {
        Preconditions.checkArgument(n >= 0);
        return new AbstractFlow<T, T>(this) {
            @Override
            Stage<T> wrapDownstream(Stage<T> nextStage) {
                return new Stage.AbstractChainedStage<T, T>(nextStage) {
                    private long count = 0;

                    @Override
                    public void begin(long size) {
                        if (size >= 0) {
                            size = Math.min(size - n, Visitor.UNKNOWN_SIZE);
                        } else {
                            size = Visitor.UNKNOWN_SIZE;
                        }
                        downstream.begin(size);
                    }

                    @Override
                    public void accept(T element) {
                        if (count++ >= n) {
                            downstream.accept(element);
                        }
                    }

                    @Override
                    public String toString() {
                        return "Stage.skip";
                    }
                };
            }

            @Override
            public String toString() {
                return "Flow.skip";
            }
        };
    }

    //endregion 有状态操作

    //region 终止操作帮助方法

    /**
     * 终止方法
     *
     * @param <R>           返回类型
     * @param terminalStage 终止操作
     */
    private <R> R terminal(Stage.TerminalStage<T, R> terminalStage) {
        return terminalStage.startAndGet(source);
    }

    /**
     * 终止操作的基类
     *
     * @param <T> 流中元素类型
     * @param <R> 该终止操作的返回类型 通过 {@link Supplier#get()} 返回结果
     * @see Supplier#get()
     */
    static abstract class AbstractTerminal<T, R> implements Stage.TerminalStage<T, R>, Supplier<R> {
        private final AbstractFlow<?, T> lastFlow;

        AbstractTerminal(AbstractFlow<?, T> lastFlow) {
            Preconditions.checkArgument(!lastFlow.linkedOrConsumed, "already consumed");
            lastFlow.linkedOrConsumed = true;
            this.lastFlow = lastFlow;
        }

        @Override
        public R startAndGet(Visitor<?> in) {
            lastFlow.start(in, this);
            return get();
        }

        @Override
        public abstract R get();
    }

    /**
     * 开始执行整串流各阶段的操作
     *
     * @param <S_IN> 源迭代器的元素类型
     */
    private <S_IN> void start(Visitor<S_IN> in, Stage.TerminalStage<T, ?> terminalStage) {
        Stage<S_IN> stage = wrapStage(terminalStage);
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    private <S_IN> Stage<S_IN> wrapStage(Stage<T> stage) {
        for (AbstractFlow flow = this; flow.prev != null; flow = flow.prev) {
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
     * @return 将下游操作包裹的当前阶段操作 该操作处理的元素类型是当前流的流入类型
     */
    abstract Stage<S> wrapDownstream(Stage<T> nextStage);

    //endregion 终止操作帮助方法

    //region 终止操作

    @Override
    public void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        // 遍历不需要返回内容所以是 Void
        terminal(new AbstractTerminal<T, Void>(this) {
            @Override
            public Void get() {
                return null;
            }

            @Override
            public void accept(T element) {
                action.accept(element);
            }

            @Override
            public String toString() {
                return "Stage.TerminalStage.forEach";
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
    public void forEachOrdered(Consumer<? super T> action) {
        forEach(action);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object[] toArray() {
        return toArray(size -> (T[]) new Object[size]);
    }

    @Override
    public T[] toArray(IntFunction<T[]> generator) {
        Objects.requireNonNull(generator);
        return terminal(new AbstractTerminal<T, T[]>(this) {
            private List<T> list;
            private T[] array;
            private int idx;

            @Override
            public T[] get() {
                T[] result = this.array;
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
            public void accept(T element) {
                if (array != null) {
                    array[idx++] = element;
                } else {
                    Preconditions.checkState(list.size() < MAX_ARRAY_SIZE,
                            "max array size exceed: %s", MAX_ARRAY_SIZE);
                    list.add(element);
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

            @Override
            public String toString() {
                return "Stage.TerminalStage.toArray";
            }
        });

    }

    @Override
    public T reduce(T identity, BinaryOperator<T> accumulator) {
        Objects.requireNonNull(accumulator);
        return terminal(new AbstractTerminal<T, T>(this) {
            private T result;

            @Override
            public T get() {
                T ret = this.result;
                result = null;
                return ret;
            }

            @Override
            public void begin(long size) {
                result = identity;
            }

            @Override
            public void accept(T element) {
                result = accumulator.apply(result, element);
            }

            @Override
            public String toString() {
                return "Stage.TerminalStage.reduce";
            }
        });
    }

    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        Objects.requireNonNull(accumulator);
        return terminal(new AbstractTerminal<T, Optional<T>>(this) {
            private T result;
            private boolean hasElement;

            @Override
            public Optional<T> get() {
                T ret = this.result;
                result = null;
                return hasElement ? Optional.of(ret) : Optional.empty();
            }

            @Override
            public void accept(T element) {
                if (hasElement) {
                    result = accumulator.apply(result, element);
                } else {
                    result = element;
                    hasElement = true;
                }
            }

            @Override
            public String toString() {
                return "Stage.TerminalStage.reduce";
            }
        });
    }

    @Override
    public <R> R reduce(R identity, BiFunction<R, ? super T, R> accumulator, BinaryOperator<R> combiner) {
        Objects.requireNonNull(accumulator);
        Objects.requireNonNull(combiner);
        return terminal(new AbstractTerminal<T, R>(this) {
            private R result;

            @Override
            public R get() {
                R ret = this.result;
                result = null;
                return ret;
            }

            @Override
            public void begin(long size) {
                result = identity;
            }

            @Override
            public void accept(T element) {
                result = accumulator.apply(result, element);
            }

            @Override
            public String toString() {
                return "Stage.TerminalStage.reduce";
            }
        });
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
        Objects.requireNonNull(accumulator);
        Objects.requireNonNull(combiner);
        return terminal(new AbstractTerminal<T, R>(this) {
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
            public void accept(T element) {
                accumulator.accept(result, element);
            }

            @Override
            public String toString() {
                return "Stage.TerminalStage.collect";
            }
        });
    }

    /**
     * @param <A> 初始结果类型
     * @param <R> 最终结果类型 由 {@link Collector#finisher()} 将 {@link A} 转为 {@link R}
     */
    @Override
    public <R, A> R collect(Collector<? super T, A, R> collector) {
        Objects.requireNonNull(collector);
        return terminal(new AbstractTerminal<T, R>(this) {
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
            public void accept(T element) {
                collector.accumulator().accept(tmp, element);
            }

            @Override
            public String toString() {
                return "Stage.TerminalStage.collect";
            }
        });
    }

    @Override
    public Optional<T> min(Comparator<? super T> comparator) {
        return reduce(BinaryOperator.minBy(comparator));
    }

    @Override
    public Optional<T> max(Comparator<? super T> comparator) {
        return reduce(BinaryOperator.maxBy(comparator));
    }

    @Override
    public long count() {
        return map(x -> 1L).reduce(0L, Long::sum);
    }

    @Override
    public boolean anyMatch(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return terminal(new AbstractTerminal<T, Boolean>(this) {
            private boolean result;
            private boolean canFinish;

            @Override
            public Boolean get() {
                return result;
            }

            @Override
            public void accept(T element) {
                if (!canFinish() && predicate.test(element)) {
                    result = true;
                    canFinish = true;
                }
            }

            @Override
            public boolean canFinish() {
                return canFinish;
            }

            @Override
            public String toString() {
                return "Stage.TerminalStage.anyMatch";
            }
        });
    }

    @Override
    public boolean allMatch(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return terminal(new AbstractTerminal<T, Boolean>(this) {
            private boolean result = true;

            @Override
            public Boolean get() {
                return result;
            }

            @Override
            public void accept(T element) {
                if (!predicate.test(element)) {
                    result = false;
                }
            }

            @Override
            public String toString() {
                return "Stage.TerminalStage.allMatch";
            }
        });
    }

    @Override
    public boolean noneMatch(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return terminal(new AbstractTerminal<T, Boolean>(this) {
            private boolean result = true;

            @Override
            public Boolean get() {
                return result;
            }

            @Override
            public void accept(T element) {
                if (predicate.test(element)) {
                    result = false;
                }
            }

            @Override
            public String toString() {
                return "Stage.TerminalStage.noneMatch";
            }
        });
    }

    @Override
    public Optional<T> findFirst() {
        return terminal(new AbstractTerminal<T, Optional<T>>(this) {
            private T result;
            private boolean find;

            @Override
            public Optional<T> get() {
                T ret = result;
                result = null;
                return find ? Optional.of(ret) : Optional.empty();
            }

            @Override
            public void accept(T element) {
                if (!canFinish()) {
                    result = element;
                    find = true;
                }
            }

            @Override
            public boolean canFinish() {
                return find;
            }

            @Override
            public String toString() {
                return "Stage.TerminalStage.findFirst";
            }
        });
    }

    @Override
    public Optional<T> findAny() {
        return findFirst();
    }

    //endregion 终止操作

    /**
     * 头节点
     */
    static class Head<S, T> extends AbstractFlow<S, T> {
        Head(Visitor<S> in) {
            super(in);
        }

        @Override
        Stage<S> wrapDownstream(Stage<T> nextStage) {
            throw new AssertionError("should not go to here.");
        }

        @Override
        public String toString() {
            return "Flow.Head";
        }
    }

}
