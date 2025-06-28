package com.rpg.exceptions;

@FunctionalInterface
public interface ShieldedOperation {
    void execute() throws Exception;
}
