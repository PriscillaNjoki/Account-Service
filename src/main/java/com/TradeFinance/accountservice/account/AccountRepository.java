package com.TradeFinance.accountservice.account;

import com.TradeFinance.accountservice.applicant.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Applicant> findByCifId(String cifId);
}
