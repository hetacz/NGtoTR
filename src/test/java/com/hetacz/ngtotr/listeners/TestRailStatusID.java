package com.hetacz.ngtotr.listeners;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TestRailStatusID {

    PASS(1, "PASSED"),
    BLOCKED(2, "BLOCKED"),
    UNTESTED(3, "UNTESTED"),
    RETEST(4, "SKIPPED"),
    FAIL(5, "FAILED");

    @Getter
    private final int value;
    @Getter
    private final String msg;
}
