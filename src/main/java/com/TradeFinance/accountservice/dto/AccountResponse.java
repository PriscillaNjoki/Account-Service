package com.TradeFinance.accountservice.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    private String cifId;
    private String passportNumber;
    private boolean isBusiness;
    private String firstName;
    private String middleName;
    private String lastName;
    private String businessName;
    private String gender;
    private String kra;
    private String accountNumber;
    private String nationalId;
    private String accountName;
    private String currency;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String postalCode;
    private String countryCode;
    private String country;
//    @JsonIgnore
//    private Bank bank;
    @JsonIgnore
    private String formattedAddress;
}
