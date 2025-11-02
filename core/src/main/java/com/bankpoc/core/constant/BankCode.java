package com.bankpoc.core.constant;

import java.util.Locale;

public enum BankCode {
    // Internal / Demo
    BANKPOC("Bank POC"),

    // 🇸🇬 Singapore Banks
    DBS("DBS Bank"),
    OCBC("Oversea-Chinese Banking Corporation"),
    UOB("United Overseas Bank"),
    CITIBANK_SG("Citibank Singapore"),
    HSBC_SG("HSBC Singapore"),
    STANDARD_CHARTERED_SG("Standard Chartered Bank Singapore"),
    MAYBANK_SG("Maybank Singapore"),
    ICBC_SG("Industrial and Commercial Bank of China Singapore"),
    BANK_OF_CHINA_SG("Bank of China Singapore"),
    BNP_PARIBAS_SG("BNP Paribas Singapore"),
    MUFG_SG("MUFG Bank Singapore"),
    SMBC_SG("Sumitomo Mitsui Banking Corporation Singapore"),

    // 🇮🇩 Indonesia Banks
    BCA("Bank Central Asia"),
    MANDIRI("Bank Mandiri"),
    BNI("Bank Negara Indonesia"),
    BRI("Bank Rakyat Indonesia"),
    CIMB_NIAGA("CIMB Niaga"),
    PERMATA("Bank Permata"),
    DANAMON("Bank Danamon"),
    PANIN("Bank Panin"),
    MEGA("Bank Mega"),
    BTN("Bank Tabungan Negara"),
    OCBC_NISP("OCBC NISP"),
    MAYBANK_ID("Maybank Indonesia"),
    COMMONWEALTH_ID("Commonwealth Bank Indonesia"),
    HSBC_ID("HSBC Indonesia");

    private final String fullName;

    BankCode(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }
    public static BankCode fromString(String value) {
        if (value == null || value.isBlank()) {
            return BANKPOC;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        try {
            return BankCode.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            // fallback default if unknown code
            return BANKPOC;
        }
    }
}
