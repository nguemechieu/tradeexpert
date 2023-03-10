package org.tradeexpert.tradeexpert;

public enum NewsEventImpact {
    HIGH,
    MEDIUM, LOW, UNKNOWN;


    public static NewsEventImpact getEnum(String value) {

        if (value == null) {
            return null;
        }

        try {
            return NewsEventImpact.valueOf(value.toUpperCase());

        } catch (Exception e) {
            return UNKNOWN;
        }

    }
}
