package com.lightning.northstar.util;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

// Can this even be qualified as a registry?
public class SimpleRegistry<K, V> {

    private final Map<K, V> values = new ConcurrentHashMap<>();

    public V get(K key) {
        return values.get(key);
    }

    public V getOrThrow(K key) {
        V v = values.get(key);
        if (v == null)
            throw new NullPointerException("No value with key " + key);
        return v;
    }

    public Function<K, V> lookup(String errorMessage) {
        return key -> Objects.requireNonNull(get(key), "Couldn't find " + errorMessage + " \"" + key + "\"");
    }

    public void register(K key, V value) {
        V previous = values.put(key, value);
        if (previous != null) {
            values.put(key, previous);
            throw new IllegalStateException("Tried to register duplicate key " + key);
        }
    }

}
