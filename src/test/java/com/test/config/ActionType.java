package com.test.config;

public enum ActionType {
    LOGIN,
    ACTION,
    LOGOUT;

    @Override
    public String toString() {
        return this.name();
    }
}