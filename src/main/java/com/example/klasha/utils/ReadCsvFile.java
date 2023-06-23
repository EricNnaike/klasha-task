package com.example.klasha.utils;

import com.example.klasha.Exception.CustomNotFoundException;
import com.example.klasha.constants.BaseConstants;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.Objects;

public class ReadCsvFile {
    public static double getExchangeRate(String sourceCurrency,String targetCurrency) {
        String csvFile = "exchange_rate.csv";
        double exchangeRate = 0;
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            String[] currencyData;
            boolean ishead = true;
            while ((currencyData = reader.readNext()) != null) {
                if(ishead){
                    ishead = false;
                    continue;
                }
                for (int i = 0; i < currencyData.length; i++) {
                    if(Objects.equals(currencyData[1],sourceCurrency) && Objects.equals(currencyData[0],targetCurrency)){
                        return Double.valueOf(currencyData[2]);
                }
                System.out.println();
            }
        }} catch (Exception e) {
            e.printStackTrace();
        }
        //todo: throw exception
    throw new CustomNotFoundException("No exchange rate found for the selected currency");
    }
}

