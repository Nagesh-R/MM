package com.monks.core.services.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.monks.core.services.RestClientFactory;
import com.monks.core.services.WeatherService;
import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component(service = WeatherService.class, immediate = true)
@Designate(ocd = WeatherServiceImpl.Config.class)
public class WeatherServiceImpl implements WeatherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherServiceImpl.class);
    private String apiURL;
    private String apiKey;
    private String units;
    private int cacheSize;
    private int cacheExpiry;
    private Cache<String, String> cachedWeatherData;

    @Reference
    RestClientFactory restClientFactory;

    @ObjectClassDefinition(name = "Monks Weather API Configuration")
    public @interface Config {

        @AttributeDefinition(name = "Configure the API URL")
        String apiURL() default "http://api.openweathermap.org/data/2.5/weather";

        @AttributeDefinition(name = "Configure API Key")
        String apiKey() default "6e67c06cb79afffd3b3137da5ee0ff0c";

        @AttributeDefinition(name = "Configure Metric")
        String units() default "metric";

        @AttributeDefinition(name = "Maximum Cache Size", description = "Maximum number of records that can be cached")
        int cacheSize() default 10;

        @AttributeDefinition( name = "Cache Expiry Time (In minutes)",
                description = "Number of minutes after which the cache will expire")
        int cacheExpiry() default 120;

    }

    @Activate
    public final void activate(final Config config){
        apiURL = config.apiURL();
        apiKey = config.apiKey();
        units = config.units();
        cacheSize = config.cacheSize();
        cacheExpiry = config.cacheExpiry();
        cachedWeatherData = CacheBuilder
                .newBuilder()
                .maximumSize(cacheSize)
                .expireAfterWrite(cacheExpiry, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public String constructApiURI(String city) {
        String endpoint = StringUtils.EMPTY;
        if(StringUtils.isNotEmpty(apiURL) && StringUtils.isNotEmpty(apiKey) && StringUtils.isNotEmpty(city)
                && StringUtils.isNotEmpty(units)) {
            endpoint = endpoint.concat(apiURL)
                    .concat("?q=")
                    .concat(city)
                    .concat("&appid=")
                    .concat(apiKey)
                    .concat("&units=")
                    .concat(units);
        } else {
            LOGGER.error("***** WeatherImpl :: constructApiURI :: Mandatory parameters missing *****");
        }
        return endpoint;
    }

    @Override
    public String fetchWeatherData(String uri) {
        String weatherData = StringUtils.EMPTY;
        if(StringUtils.isNotEmpty(uri)) {
            weatherData = restClientFactory.sendGetRequest(uri);
        }
        return weatherData;
    }

    @Override
    public String getWeatherDataFromCache(String apiURL) {
        String todaysDate = LocalDate.now().toString();
        String weatherData = StringUtils.EMPTY;
        try {
            if(cachedWeatherData.asMap().containsKey(todaysDate)) {
                weatherData = cachedWeatherData.getIfPresent(todaysDate);
            } else {
                cachedWeatherData.invalidateAll();
                weatherData = cachedWeatherData.get(todaysDate, new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return fetchWeatherData(apiURL);
                    }
                });
            }
        } catch (ExecutionException e) {
            LOGGER.error("****** WeatherImpl :: getWeatherDataFromCache :: caught Execution Exception ******", e);
        }
        return weatherData;
    }
}
