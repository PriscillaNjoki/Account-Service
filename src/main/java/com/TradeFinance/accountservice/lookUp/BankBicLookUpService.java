package com.TradeFinance.accountservice.lookUp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankBicLookUpService {
    @Autowired
    private BankBicMappingRepository bankBicMappingRepository;

    public String getBankNameAndCountryByBic(String bic) {
        if (bic == null || bic.isEmpty()) {
            throw new IllegalArgumentException("BIC is required");
        }
        BankBicMapping mapping = bankBicMappingRepository.findById(bic).orElse(null);
        if (mapping != null) {
            return mapping.getBankName() + " - " + mapping.getCountry();
        } else {
            return "Unknown Bank - Unknown Country";
        }
    }

}
