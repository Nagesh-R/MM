# Weather Component

In this component the weather is fetched via https://openweathermap.org/

## Service

This component has a OSGI service named WeatherService (core/services) and it consists of the following

- Method to make a post call and fetch the WeatherData from the openweathermap with the app id.
- openweather related configurations are made available as OSGI configurations
- Guava cache has been used as in-memory cache to save data and avoid multiple calls to the external api
- openweather gives 60 free calls per day so the default cache expiry is set to 2 hours and post that there is an API 
    call made to openweather to get the latest data.

    ###### configurable properties in OSGI
    
       1. API URL
       2. API Key (no encryption is used currently but granite crypto support can be used to enctypt the key)
       3. Metric (there are few metric options provided by the API)
       4. Cache size
       5. Cache Expiry (by default it is set to 120 minutes)
       

## Servlet 

This component uses a servlet (WeatherServlet under /core/servlets)

- Ajax call is made to this resource based servlet and then using the service reference the data is fetched and passed to frontend

## Dialog

The author can configure 
 - title
 - sub title
 - image 
 - city 
 
city is defaulted to Amsterdam if the Authors fails to configure

## JAVA Files related to the component

    1. src/main/java/com/monks/core/services/WeatherService.java
    2. src/main/java/com/monks/core/services/impl/WeatherServiceImpl.java
    3. src/main/java/com/monks/core/servlets/WeatherServlet.java


 