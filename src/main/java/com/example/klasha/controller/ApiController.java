package com.example.klasha.controller;

import com.example.klasha.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("api/v1")
@RestController
@RequiredArgsConstructor
public class ApiController {

    private final ApiService apiService;

    @GetMapping("/topcities")
    private ResponseEntity<?> getTopCities(@RequestParam("count") int count){
        return ResponseEntity.ok(apiService.getTopCities(count));
    }
    @GetMapping("/countryinfo")
    private ResponseEntity<?> getCountryInfo(@RequestParam("country") String country){
        return ResponseEntity.ok(apiService.getCountryInfo(country));
    }

    @GetMapping("/cityAndState")
    private ResponseEntity<?> getCountryCityAndState(@RequestParam("country") String country){
        return ResponseEntity.ok(apiService.getCountryCityAndState(country));
    }

    @GetMapping("/currencyConverter")
    private ResponseEntity<?> convertCurrency(@RequestParam("country") String country,
                                              @RequestParam("amount") String amount,
                                              @RequestParam("targetCurrency") String targetCurrency
    ){
        return ResponseEntity.ok(apiService.convertCurrency(country,amount,targetCurrency));
    }

}
