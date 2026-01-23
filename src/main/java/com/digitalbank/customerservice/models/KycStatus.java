package com.digitalbank.customerservice.models;

import java.util.Arrays;

public enum KycStatus {
    PENDING,
    VERIFIED,
    REJECTED;

    public static boolean contains(String value) {
        return Arrays.stream(KycStatus.values())
                .anyMatch(r -> r.name().equalsIgnoreCase(value));
    }

}
