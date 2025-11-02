package com.bankpoc.core.domain.beneficiary;

import com.bankpoc.core.constant.BankCode;
import com.bankpoc.core.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, UUID> {
    List<Beneficiary> findByUserEmail(String email);
    List<Beneficiary> findByUserEmailAndBankCode(String email, BankCode bankCode);
}
