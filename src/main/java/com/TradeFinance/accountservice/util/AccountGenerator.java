//package com.TradeFinance.accountservice.util;
//
//import java.util.Random;
//import java.util.UUID;
//
//public class AccountGenerator {
//
//    public static String generateAccountNumber() {
//        Random random = new Random();
//        // Generate a random number between 0 and 999999999999 (13 digits)
//        long randomNumber = random.nextLong() % 1000000000000L;
//        if (randomNumber < 0) {
//            // Ensure the number is positive
//            randomNumber *= -1;
//        }
//        // Concatenate "777" with the random number
//        return "777" + String.format("%010d", randomNumber);
//    }
//    public static String generateCifId() {
//        Random random = new Random();
//        // Generate a random number between 0 and 99999 (5 digits)
//        int randomNumber = random.nextInt(100000);
//        // Concatenate "54" with the random number
//        String cifId = "54" + String.format("%05d", randomNumber);
//        // Convert the concatenated string to a UUID
//        return cifId;
//    }
//
//
//    public static void main(String[] args) {
//        // Example usage
//        String accountNumber = generateAccountNumber();
//        String cifId = generateCifId();
//        System.out.println("Generated Account Number: " + accountNumber);
//        System.out.println("Generated CIFID: " + cifId);
//    }
//
//}
