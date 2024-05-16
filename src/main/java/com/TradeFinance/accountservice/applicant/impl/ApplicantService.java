package com.TradeFinance.accountservice.applicant.impl;

import com.TradeFinance.accountservice.dto.*;

public interface ApplicantService {
    BankResponse createAccount(ApplicantDto applicantDto);
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
    BankResponse transfer(TransferRequest request);

}
