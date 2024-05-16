package com.TradeFinance.accountservice.lookUp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryCodeMappingRepository extends JpaRepository<CountryCodeMapping, String> {
    CountryCodeMapping findByCountryCode(String countryCode);
}
