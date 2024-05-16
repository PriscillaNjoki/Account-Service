package com.TradeFinance.accountservice.lookUp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankBicMappingRepository extends JpaRepository<BankBicMapping, String> {
    BankBicMapping findByBic(String bic);
}
