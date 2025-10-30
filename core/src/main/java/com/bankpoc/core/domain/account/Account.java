package com.bankpoc.core.domain.account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Builder
class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;




}
