package com.example.klasha.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

public interface ApiService {
    List<Map<String,?>> getTopCities(int count);
    Object getCountryInfo(String country);
    Object getCountryCityAndState(String country);
    Object convertCurrency(String country, String amount, String targetCurrency);
    JSONObject postRequest(String url, String value);
    String getCurrency(String country, String currencyUrl);
    JSONObject getIso(String isoUrl,String country);
    JSONObject getLocation(String locationUrl, String country);
    String getPopulation(String populationUrl,String country);
}

