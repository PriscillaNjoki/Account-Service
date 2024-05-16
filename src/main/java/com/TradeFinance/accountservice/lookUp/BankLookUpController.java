package com.TradeFinance.accountservice.lookUp;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bank")
@RequiredArgsConstructor
public class BankLookUpController {
    private BankBicLookUpService bankBicLookUpService;
    @GetMapping
    public ResponseEntity<?>getBankNameAndCountryByBic(@PathVariable String bic){
        try {
            String bankInfo = bankBicLookUpService.getBankNameAndCountryByBic(bic);
            return ResponseEntity.ok(bankInfo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    }


