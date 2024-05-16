package com.TradeFinance.accountservice.transaction.impl;

import com.TradeFinance.accountservice.dto.TransactionDto;
//import com.TradeFinance.accountservice.transaction.Transaction;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
