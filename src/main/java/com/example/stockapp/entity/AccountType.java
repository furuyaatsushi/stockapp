package com.example.stockapp.entity;

public enum AccountType {
    TAXABLE("特定口座"),
    NISA("NISA");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
