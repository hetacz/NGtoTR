package com.hetacz.ngtotr;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TestRailStatusID {

    PASS(1, "PASSED"),
    BLOCKED(2, "BLOCKED"),
    UNTESTED(3, "UNTESTED"),
    RETEST(4, "SKIPPED"),
    FAIL(5, "FAILED");

    private final int value;
    private final String msg;
}
