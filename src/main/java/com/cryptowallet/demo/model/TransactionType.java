package com.cryptowallet.demo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionType {
    DIRECT_TRANSFER_TO_WALLET("Direct_Transfer_To_Wallet"),
    QR_TRANSFER("QR_Transfer"),
    TRANSFER_REQUEST("Transfer_Request"),
    BANK_CARD_TRANSFER("Bank_Card_Transfer")
    ;

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TransactionType fromValue(String value) {
        for (TransactionType type : TransactionType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }
}
