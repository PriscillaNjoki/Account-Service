package com.TradeFinance.accountservice.applicant;

import com.TradeFinance.accountservice.account.AccountRepository;
import com.TradeFinance.accountservice.applicant.impl.ApplicantService;
import com.TradeFinance.accountservice.dto.*;
import com.TradeFinance.accountservice.emailAlerts.impl.EmailService;
import com.TradeFinance.accountservice.transaction.impl.TransactionService;;
import com.TradeFinance.accountservice.util.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;


@Service
@AllArgsConstructor
public class ApplicantServiceImpl implements ApplicantService {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ApplicantRepository applicantRepository;
        @Autowired
        EmailService emailService;
    @Autowired
    TransactionService transactionService;

    @Override
    public BankResponse createAccount(ApplicantDto applicantDto) {
        /**
         * Creating an account - saving a new applicant into the db
         * Check if user already has an account
         */
        if (applicantRepository.existsByEmail(applicantDto.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        Applicant newApplicant = Applicant.builder()
                .accountName(applicantDto.getAccountName())
                .nationalId(applicantDto.getNationalId())
                .address(applicantDto.getAddress())
                .postalCode(applicantDto.getPostalCode())
                .city(applicantDto.getCity())
                .formattedAddress(applicantDto.getFormattedAddress())
                .currency(applicantDto.getCurrency())
                .email(applicantDto.getEmail())
                .phoneNumber(applicantDto.getPhoneNumber())
                .alternativePhoneNumber(applicantDto.getAlternativePhoneNumber())
                .countryCode(applicantDto.getCountryCode())
                .country(applicantDto.getCountry())
                .cifId(AccountUtils.generateCifId())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .status("ACTIVE")
                .build();



        Applicant savedApplicant = applicantRepository.save(newApplicant);
        //send email alert
        EmailDto emailDto = EmailDto.builder()
                .recipient(savedApplicant.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations, Your account has been successfully created. \n Your Account Details: \n" +
                        "Account Name : "+ savedApplicant.getAccountName() +"\n Account Number: "+ savedApplicant.getAccountNumber())
                .build();


        emailService.sendEmailAlert(emailDto);
        return BankResponse.builder()

                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedApplicant.getAccountBalance())
                        .accountNumber(savedApplicant.getAccountNumber())
                        .accountName(savedApplicant.getAccountName())
                        .build())
                .build();


    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        //check if the provided account number exists in the bd
        Boolean isAccountExist = applicantRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
            Applicant foundApplicant = applicantRepository.findByAccountNumber(request.getAccountNumber());
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(foundApplicant.getAccountBalance())
                            .accountNumber(request.getAccountNumber())
                            .accountName(foundApplicant.getAccountName())
                            .build())
                    .build();
        }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist = applicantRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        Applicant foundApplicant = applicantRepository.findByAccountNumber(request.getAccountNumber());
        return foundApplicant.getAccountName();
    }



    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        //checking if the account exists
        boolean isAccountExist = applicantRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        Applicant applicantToCredit = applicantRepository.findByAccountNumber(request.getAccountNumber());
        applicantToCredit.setAccountBalance(applicantToCredit.getAccountBalance().add(request.getAmount()));
        applicantRepository.save(applicantToCredit);

        //save the transaction
        TransactionDto transactionDto= TransactionDto.builder()
                .accountNumber(applicantToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(applicantToCredit.getAccountName())
                        .accountBalance(applicantToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        boolean isAccountExist = applicantRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        Applicant applicantToDebit = applicantRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance =applicantToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
        if ( availableBalance.intValue() < debitAmount.intValue()){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        else {
            applicantToDebit.setAccountBalance(applicantToDebit.getAccountBalance().subtract(request.getAmount()));
            applicantRepository.save(applicantToDebit);
            TransactionDto transactionDto= TransactionDto.builder()
                    .accountNumber(applicantToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(request.getAmount())
                    .build();
            transactionService.saveTransaction(transactionDto);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(request.getAccountNumber())
                            .accountName(applicantToDebit.getAccountName())
                            .accountBalance(applicantToDebit.getAccountBalance())
                            .build())
                    .build();
        }
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        Boolean isDestinationAccountExist = applicantRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!isDestinationAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        Applicant sourceAccountApplicant = applicantRepository.findByAccountNumber(request.getSourceAccountNumber());
        if (request.getAmount().compareTo(sourceAccountApplicant.getAccountBalance()) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        sourceAccountApplicant.setAccountBalance(sourceAccountApplicant.getAccountBalance().subtract(request.getAmount()));
        String sourceUsername = sourceAccountApplicant.getAccountName();
        applicantRepository.save(sourceAccountApplicant);
        EmailDto debitAlert = EmailDto.builder()
                .subject("DEBIT ALERT")
                .recipient(sourceAccountApplicant.getEmail())
                .messageBody("The sum of " + request.getAmount()+ "has been deducted from your account")
                .build();
        emailService.sendEmailAlert(debitAlert);


        Applicant destinationAccountApplicant = applicantRepository.findByAccountNumber(request.getDestinationAccountNumber());
        destinationAccountApplicant.setAccountBalance(destinationAccountApplicant.getAccountBalance().add(request.getAmount()));
//        String recipientUsername = destinationAccountApplicant.getAccountName();
        applicantRepository.save(destinationAccountApplicant);
        EmailDto creditAlert = EmailDto.builder()
                .subject("CREDIT ALERT")
                .recipient(sourceAccountApplicant.getEmail())
                .messageBody("The sum of " + request.getAmount()+ "has been sent to your account from " + sourceUsername)
                .build();
        emailService.sendEmailAlert(creditAlert);
//save transaction
        TransactionDto transactionDto= TransactionDto.builder()
                .accountNumber(destinationAccountApplicant.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();

    }
}




        //        emailService.sendEmailAlert(emailDto);
//        return BankResponse.builder()
//                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
//                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
//                .accountInfo(AccountInfo.builder()
//                        .accountBalance(savedApplicant.getAccountBalance())
//                        .accountNumber(savedApplicant.getAccountNumber())
//                        .accountName(savedApplicant.getAccountName())
//                        .build())
//                .build();
//
//        return BankResponse.builder()
//                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
//                     .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
//                .accountInfo(AccountInfo.builder()
//                        .accountBalance(savedApplicant.getAccountBalance())
//                        .accountNumber(savedApplicant.getAccountNumber())
//                        .accountName(savedApplicant.getAccountName())
//                        .build())
//                .build();
//    }
//
//

//
//    @Override
//    public BankResponse debitAccount(CreditDebitRequest request) {
//        boolean isAccountExist = applicantRepository.existsByAccountNumber(request.getAccountNumber());
//        if (!isAccountExist){
//            return BankResponse.builder()
//                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
//                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
//                    .accountInfo(null)
//                    .build();
//        }
//
//        Applicant applicantToDebit = applicantRepository.findByAccountNumber(request.getAccountNumber());
//        BigInteger availableBalance =applicantToDebit.getAccountBalance().toBigInteger();
//        BigInteger debitAmount = request.getAmount().toBigInteger();
//        if ( availableBalance.intValue() < debitAmount.intValue()){
//            return BankResponse.builder()
//                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
//                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
//                    .accountInfo(null)
//                    .build();
//        }
//        else {
//            applicantToDebit.setAccountBalance(applicantToDebit.getAccountBalance().subtract(request.getAmount()));
//            applicantRepository.save(applicantToDebit);
//            TransactionDto transactionDto= TransactionDto.builder()
//                    .accountNumber(applicantToDebit.getAccountNumber())
//                    .transactionType("DEBIT")
//                    .amount(request.getAmount())
//                    .build();
//            transactionService.saveTransaction(transactionDto);
//            return BankResponse.builder()
//                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
//                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
//                    .accountInfo(AccountInfo.builder()
//                            .accountNumber(request.getAccountNumber())
//                            .accountName(applicantToDebit.getAccountName())
//                            .accountBalance(applicantToDebit.getAccountBalance())
//                            .build())
//                    .build();
//        }
//    }
//
//    @Override
//    public BankResponse transfer(TransferRequest request) {
//        Boolean isDestinationAccountExist = applicantRepository.existsByAccountNumber(request.getDestinationAccountNumber());
//        if (!isDestinationAccountExist) {
//            return BankResponse.builder()
//                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
//                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
//                    .accountInfo(null)
//                    .build();
//        }
//        Applicant sourceAccountApplicant = applicantRepository.findByAccountNumber(request.getSourceAccountNumber());
//        if (request.getAmount().compareTo(sourceAccountApplicant.getAccountBalance()) > 0) {
//            return BankResponse.builder()
//                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
//                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
//                    .accountInfo(null)
//                    .build();
//        }
//        sourceAccountApplicant.setAccountBalance(sourceAccountApplicant.getAccountBalance().subtract(request.getAmount()));
//        String sourceUsername = sourceAccountApplicant.getAccountName();
//        applicantRepository.save(sourceAccountApplicant);
//        EmailDto debitAlert = EmailDto.builder()
//                .subject("DEBIT ALERT")
//                .recipient(sourceAccountApplicant.getEmail())
//                .messageBody("The sum of " + request.getAmount()+ "has been deducted from your account")
//                .build();
//        emailService.sendEmailAlert(debitAlert);
//
//
//        Applicant destinationAccountApplicant = applicantRepository.findByAccountNumber(request.getDestinationAccountNumber());
//        destinationAccountApplicant.setAccountBalance(destinationAccountApplicant.getAccountBalance().add(request.getAmount()));
////        String recipientUsername = destinationAccountApplicant.getAccountName();
//        applicantRepository.save(destinationAccountApplicant);
//        EmailDto creditAlert = EmailDto.builder()
//                .subject("CREDIT ALERT")
//                .recipient(sourceAccountApplicant.getEmail())
//                .messageBody("The sum of " + request.getAmount()+ "has been sent to your account from " + sourceUsername)
//                .build();
//        emailService.sendEmailAlert(creditAlert);
////save transaction
//        TransactionDto transactionDto= TransactionDto.builder()
//                .accountNumber(destinationAccountApplicant.getAccountNumber())
//                .transactionType("CREDIT")
//                .amount(request.getAmount())
//                .build();
//        transactionService.saveTransaction(transactionDto);
//
//        return BankResponse.builder()
//                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
//                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
//                .accountInfo(null)
//                .build();
//
//    }
//}







