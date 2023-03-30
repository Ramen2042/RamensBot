package com.ramen.texttojava;

public class ReturnedValue<T> {
    private final T value;

    public ReturnedValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
