package com.TradeFinance.accountservice.lookUp;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/country")
@RequiredArgsConstructor
public class CountryLookUpController {
    private CountryLookUpService countryLookUpService;

    @GetMapping("/{countryCode}")
    public ResponseEntity<?> getCountryNameByCountryCode(@PathVariable String countryCode) {
        try {
            String countryName = countryLookUpService.getCountryNameByCountryCode(countryCode);
            return ResponseEntity.ok(countryName);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

