package com.example.klasha.service.impl;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.klasha.Exception.BadRequestException;
import com.example.klasha.Exception.CustomNotFoundException;
import com.example.klasha.constants.BaseConstants;
import com.example.klasha.dto.BaseResponse;
import com.example.klasha.service.ApiService;
import com.example.klasha.utils.ReadCsvFile;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

@Component
@AllArgsConstructor
public class ApiServiceImpl implements ApiService {
    @Autowired
    private WebClient webClient;
    @Autowired
    DefaultUriBuilderFactory factory;

    @Override
    public List<Map<String,?>> getTopCities(int count) {

        String url = BaseConstants.TOP_CITES_URL;
        String[] countries = {"new zealand","Italy","Ghana"};
        List<Map<String,?>> cities = new ArrayList<>();
    try {
        for (int i = 0; i < countries.length; i++) {
        String country = countries[i];
        Map<String, Object> map = new HashMap<>();
        map.put("limit", count);
        map.put("order", "dsc");
        map.put("orderBy", "value");
        map.put("country", country);

        BaseResponse city = webClient.method(HttpMethod.POST)
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(map))
                .exchangeToMono(response -> {
                    if (response.statusCode().is3xxRedirection()) {
                        String redirectUrl = "https://countriesnow.space" + response.headers().header(HttpHeaders.LOCATION).get(0);
                        String newUrl = redirectUrl.substring(0, redirectUrl.indexOf("q") + 1);
                        String query = country;

                        URI uri = factory.uriString(newUrl)
                                .queryParam("country", "{query}")
                                .queryParam("limit", count)
                                .queryParam("order", "dsc")
                                .queryParam("orderBy", "value")
                                .build(query);
                        return webClient.method(HttpMethod.GET)
                                .uri(uri)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .exchangeToMono(res -> res.bodyToMono(BaseResponse.class));
                    } else {
                        return response.bodyToMono(BaseResponse.class);
                    }
                }).block();

        List<Map<String, ?>> response = city.getData();
        JSONObject jsonObject1 = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String newCountry = "";
        for (int j = 0; j < response.size(); j++) {
            jsonArray.add(response.get(j).get("city"));
            newCountry = response.get(j).get("country").toString();
        }
        jsonObject1.put(newCountry, jsonArray);
            cities.add(jsonObject1);
        }
    }
    catch (Exception e){
        throw new CustomNotFoundException(e.getMessage());
    }
        return cities;
    }

    @Override
    public Object getCountryInfo(String country) {
        if(Objects.isNull(country) || Objects.equals(country,"")){
            throw new BadRequestException("Country cannot be empty");
        }
        String populationUrl = BaseConstants.POPULATION_URL;

        Map<String,Object> result = new HashMap<>();
        // Get population
        String newPopulation = getPopulation(populationUrl,country);
        result.put("population",newPopulation);

        //Get Capital City
        String cityUrl = BaseConstants.CITY_CAPITAL_URL;
        JSONObject city = postRequest(cityUrl,country);
        if(city.getBoolean("error")) return  city;
        String capital = city.getJSONObject("data").get("capital").toString();
        result.put("capital",capital);

        //Get Location
        String locationUrl = BaseConstants.LOCATION_URL;
        JSONObject locationObject = getLocation(locationUrl,country);
        result.put("location", locationObject);

        //Get iso2&3
        String isoUrl = BaseConstants.ISO_URL;
        JSONObject isoObject = getIso(isoUrl,country);
        result.put("Iso2&3",isoObject);

        //Get Currency
        String currencyUrl = BaseConstants.CURRENCY_URL;
        String currency = getCurrency(country,currencyUrl);
        result.put("currency",currency);

        return result;
    }

    @Override
    public String getPopulation(String populationUrl,String country){
        JSONObject population = postRequest(populationUrl,country);
        if(population.getBoolean("error")){
            throw new CustomNotFoundException(population.getString("msg"));
        }
        JSONArray jsonArray =  population.getJSONObject("data").getJSONArray("populationCounts");
        String newPopulation = jsonArray.getJSONObject(jsonArray.size()-1).get("value").toString();

        return newPopulation;
    }

    @Override
    public JSONObject getLocation(String locationUrl, String country){

        JSONObject location = postRequest(locationUrl,country);
        if(location.getBoolean("error")){
            throw new CustomNotFoundException(location.getString("msg"));
        }
        if(location.getBoolean("error")) return  location;
        String longitude = location.getJSONObject("data").get("long").toString();
        String latitude = location.getJSONObject("data").get("lat").toString();
        JSONObject locationObject = new JSONObject();
        locationObject.put("long",longitude);
        locationObject.put("lat",latitude);

        return locationObject;
    }

    @Override
    public JSONObject getIso(String isoUrl,String country){

        JSONObject iso = postRequest(isoUrl,country);
        if(iso.getBoolean("error")){
            throw new CustomNotFoundException(iso.getString("msg"));
        }
        if(iso.getBoolean("error")) return  iso;
        String iso2 = iso.getJSONObject("data").get("Iso2").toString();
        String iso3 = iso.getJSONObject("data").get("Iso3").toString();
        JSONObject isoObject = new JSONObject();
        isoObject.put("Iso2",iso2);
        isoObject.put("Iso3",iso3);

        return isoObject;
    }

    @Override
    public String getCurrency(String country, String currencyUrl){
        JSONObject currency = postRequest(currencyUrl,country);
        if(currency.getBoolean("error")){
            throw new CustomNotFoundException(currency.getString("msg"));
        }
        String newCurrency = currency.getJSONObject("data").get("currency").toString();

        return newCurrency;
    }
    @Override
    public Object getCountryCityAndState(String country) {
        String stateUrl = BaseConstants.STATE_URL;

        JSONArray result = new JSONArray();
        JSONObject states = postRequest(stateUrl,country);
        if(states.getBoolean("error")){
            throw new CustomNotFoundException(states.getString("msg"));
        }
        JSONArray allStates = states.getJSONObject("data").getJSONArray("states");

        for(int i = 0; i < allStates.size(); i++){
           JSONObject jsonObject = allStates.getJSONObject(i);
           String state =  jsonObject.get("name").toString();

            Map<String, Object> map = new HashMap<>();
            map.put("country", country);
            map.put("state",state);

        String cityUrl = BaseConstants.CITY_URL;
          JSONObject object =  webClient.method(HttpMethod.POST)
                    .uri(cityUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromValue(map))
                    .exchangeToMono(response -> {
                        if (response.statusCode().is3xxRedirection()) {
                            String redirectUrl = "https://countriesnow.space" + response.headers().header(HttpHeaders.LOCATION).get(0);
                            String newUrl = redirectUrl.substring(0,redirectUrl.indexOf("q")+1);
                            String country1 = country;
                            String state1 = state;

                            URI uri = factory.uriString(newUrl)
                                    .queryParam("country", "{country}")
                                    .queryParam("state","{state1}")
                                    .build(country1,state1);
                            return webClient.method(HttpMethod.GET)
                                    .uri(uri)
                                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .exchangeToMono(res -> res.bodyToMono(JSONObject.class));
                        } else {
                            return response.bodyToMono(JSONObject.class);
                        }
                    }).block();
            JSONObject jsonObject1 = new JSONObject();
            JSONArray jsonArray = object.getJSONArray("data");
            jsonObject1.put("state",state);
            jsonObject1.put("cities",jsonArray);

            result.add(jsonObject1);
        }
        return result;
    }

    @Override
    public Object convertCurrency(String country, String amount, String targetCurrency) {
        if(Objects.isNull(country) || Objects.isNull(amount) || Objects.isNull(targetCurrency)){
            throw new BadRequestException("all the parameters must be supplied");
        }
       String currencyUrl = BaseConstants.CURRENCY_URL;
       String sourceCurrency = getCurrency(country,currencyUrl);
       double exchangeRate = ReadCsvFile.getExchangeRate(sourceCurrency,targetCurrency);
        BigDecimal newAmount = new BigDecimal(amount);
        BigDecimal result = newAmount.multiply(BigDecimal.valueOf(exchangeRate));
        Map<String, Object> response = new HashMap<>();

        response.put("currency",sourceCurrency);
        response.put("amount",result);

        return response;
    }

    @Override
    public JSONObject postRequest(String url, String value){
        Map<String, Object> map = new HashMap<>();
        map.put("country", value);

       return webClient.method(HttpMethod.POST)
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(map))
                .exchangeToMono(response -> {
                    if (response.statusCode().is3xxRedirection()) {
                        String redirectUrl = "https://countriesnow.space" + response.headers().header(HttpHeaders.LOCATION).get(0);
                        String newUrl = redirectUrl.substring(0,redirectUrl.indexOf("q")+1);
                        String country1 = value;
                        URI uri = factory.uriString(newUrl)
                                .queryParam("country", "{country}")
                                .build(country1);
                        return webClient.method(HttpMethod.GET)
                                .uri(uri)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .exchangeToMono(res -> res.bodyToMono(JSONObject.class));
                    } else {
                        return response.bodyToMono(JSONObject.class);
                    }
                }).block();
    }
}
