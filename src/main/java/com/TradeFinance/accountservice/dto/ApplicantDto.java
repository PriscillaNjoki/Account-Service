package com.TradeFinance.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantDto {
    private String cifId;
    private String nationalId;
    private String accountName;
    private String accountNumber;
    private String address;
    private String postalCode;
    private String city;
    private String formattedAddress;
    private String currency;
    private String email;
    private String phoneNumber;
    private String alternativePhoneNumber;
    private String countryCode;
    private String country;
    private String businessName;
    private String gender;
    private String kra;

    private AccountDto accountDto;

}

