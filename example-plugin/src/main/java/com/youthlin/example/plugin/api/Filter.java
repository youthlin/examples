package com.youthlin.example.plugin.api;

public interface Filter<T>  {
    T applyFilter(T input, Object... args);
}
