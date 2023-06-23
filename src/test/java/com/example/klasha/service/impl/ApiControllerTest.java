package com.example.klasha.service.impl;

import com.example.klasha.controller.ApiController;
import com.example.klasha.service.ApiService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ApiController.class)
class ApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiService apiService;

    @Test
    public void testGetCountryInformation() throws Exception{
        // Mock the service response
        String capital = "Abuja";
        Mockito.when(apiService.getCountryInfo(Mockito.anyString())).thenReturn(capital);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/countryinfo")
                        .param("country", "Nigeria"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(capital));

        // Verify that the service method was called with the correct parameter
        Mockito.verify(apiService, Mockito.times(1)).getCountryInfo("Nigeria");
    }

    @Test
    public void getCountryCityAndState() throws Exception{
        // Mock the service response
        String countryDetails = "Country: Nigeria, City: Apapa, State: Lagos";
        Mockito.when(apiService.getCountryCityAndState(Mockito.anyString())).thenReturn(countryDetails);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cityAndState")
                        .param("country", "Nigeria"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(countryDetails));

        // Verify that the service method was called with the correct parameter
        Mockito.verify(apiService, Mockito.times(1)).getCountryCityAndState("Nigeria");
    }

    @Test
    public void convertCurrency() throws Exception{
        // Mock the service response
        double convertedAmount = 2500.0;
        Mockito.when(apiService.convertCurrency(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(convertedAmount);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/currencyConverter")
                        .param("country", "Nigeria")
                        .param("amount", "5000")
                        .param("targetCurrency", "USD"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(String.valueOf(convertedAmount)));

        // Verify that the service method was called with the correct parameters
        Mockito.verify(apiService, Mockito.times(1)).convertCurrency("Nigeria", "5000", "USD");
    }

    @Test
    public void testGetTopCities() throws Exception {


    }


}