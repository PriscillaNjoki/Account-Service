package com.TradeFinance.accountservice.lookUp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountryLookUpService {
    @Autowired
    private CountryCodeMappingRepository countryCodeMappingRepository;
    public String getCountryNameByCountryCode(String countryCode) {
        if (countryCode == null || countryCode.isEmpty()) {
            throw new IllegalArgumentException("CountryCode is required");
        }
        CountryCodeMapping mapping = countryCodeMappingRepository.findById(countryCode).orElse(null);
        return mapping != null ? mapping.getCountryName() : "Unknown Country";
    }
}
