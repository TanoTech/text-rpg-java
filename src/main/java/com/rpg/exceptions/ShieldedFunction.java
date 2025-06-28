package com.rpg.exceptions;

@FunctionalInterface
public interface ShieldedFunction<T> {
    T execute() throws Exception;
}