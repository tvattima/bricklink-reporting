package com.vattima.bricklink.reporting.model;

import java.util.Arrays;

public enum Condition {
    NEW("N"),
    USED("U"),
    UNSPECIFIED("X");

    private final String conditionCode;

    Condition(String conditionCode) {
        this.conditionCode = conditionCode;
    }

    public String getConditionCode() {
        return conditionCode;
    }

    public static Condition of(String colorCode) {
        return Arrays.stream(Condition.values()).filter(c -> c.getConditionCode().equalsIgnoreCase(colorCode)).findFirst().orElse(null);
    }
}
