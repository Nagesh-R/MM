package com.monks.core.services;

public interface WeatherService {
    String constructApiURI(String city);
    String fetchWeatherData(String uri);
    String getWeatherDataFromCache(String apiURL);
}
