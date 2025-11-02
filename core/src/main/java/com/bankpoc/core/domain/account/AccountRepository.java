package com.bankpoc.core.domain.account;

import com.bankpoc.core.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    public boolean existsByAccountNumber(String accountNumber);
    public Optional<Account> findAccountByUser(User user);
}
