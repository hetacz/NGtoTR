package com.hetacz.ngtotr.listeners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestRailID {

    int value() default 0; // can be case of task ID, by default case
    String[] defects() default {};
    int testerID() default 0;
    int taskID() default 0; // in case this is used value is case ID.
}
