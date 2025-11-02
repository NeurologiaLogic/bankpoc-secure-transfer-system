package com.bankpoc.core.dto.beneficiary;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BeneficiaryRequest {
    @NotBlank(message = "Account Number Must Not be Blank")
    private String accountNumber;
    @NotBlank(message = "Use BANKPOC")
    private String bankCode;
    @NotBlank(message = "Enter your personal PIN")
    private String pin;
}

