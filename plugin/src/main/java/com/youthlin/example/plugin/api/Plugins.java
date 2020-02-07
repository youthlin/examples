package com.youthlin.example.plugin.api;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Plugins {
    private static Map<String, QueueWarp> MAP = new ConcurrentHashMap<>();

    public enum State {
        READY,
        DOING,
        DONE;
    }

    private static class QueueWarp {
        State state = State.READY;
        PriorityQueue<Pluginable> queue = new PriorityQueue<>();
    }

    private static class Pluginable implements Comparable<Pluginable> {
        Object target;
        int priority;
        int acceptArgs;

        private Pluginable(Object target, int priority, int acceptArgs) {
            this.target = target;
            this.priority = priority;
            this.acceptArgs = acceptArgs;
        }

        @Override
        public int compareTo(Pluginable o) {
            return Integer.compare(priority, o.priority);
        }
    }

    private static void add(String tag, Object actionOrFilter, int priority, int acceptArgs) {
        QueueWarp warp = MAP.computeIfAbsent(tag, k -> new QueueWarp());
        if (warp.state == State.READY) {
            warp.queue.add(new Pluginable(actionOrFilter, priority, acceptArgs));
        } else {
            log("can not add %1$s on tag `%2$s` cause it's state is not READY, state: %3$s",
                    (actionOrFilter instanceof Action) ? "Action" : "Filter", tag, warp.state);
        }
    }

    private static void log(String format, Object... args) {
        System.err.println(String.format(format, args));
    }

    public static void addAction(String tag, Action action, int priority, int acceptArgs) {
        add(tag, action, priority, acceptArgs);
    }

    public static <T> void addFilter(String tag, Filter<T> filter, int priority, int acceptArgs) {
        add(tag, filter, priority, acceptArgs);
    }

    public static void doAction(String tag, Object... args) {
        QueueWarp wrap = MAP.get(tag);
        if (wrap != null) {
            wrap.state = State.DOING;
            for (Pluginable pluginable : wrap.queue) {
                if (pluginable.target instanceof Action) {
                    if (pluginable.acceptArgs <= args.length) {
                        ((Action) pluginable.target).doAction(sub(args, pluginable.acceptArgs));
                    } else {
                        log("tag `%1$s` args length: %2$d, action `%3$s` accept args length: %4$d",
                                tag, args.length, pluginable.target, pluginable.acceptArgs);
                    }
                }
            }
            wrap.state = State.DONE;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] sub(T[] input, int length) {
        assert length <= input.length;
        if (input.length == 0) {
            return input;
        }
        T[] array = (T[]) Array.newInstance(input[0].getClass(), length);
        System.arraycopy(input, 0, array, 0, length);
        return array;
    }

    @SuppressWarnings("unchecked")
    public static <T> T applyFilter(String tag, T input, Object... args) {
        QueueWarp wrap = MAP.get(tag);
        if (wrap != null) {
            wrap.state = State.DOING;
            for (Pluginable pluginable : wrap.queue) {
                if (pluginable.target instanceof Filter) {
                    if (pluginable.acceptArgs <= args.length) {
                        input = ((Filter<T>) pluginable.target).applyFilter(input, sub(args, pluginable.acceptArgs));
                    } else {
                        log("tag `%1$s` args length: %2$d, filter `%3$s` accept args length: %4$d",
                                tag, args.length, pluginable.target, pluginable.acceptArgs);
                    }
                }
            }
            wrap.state = State.DONE;
        }
        return input;
    }

}
