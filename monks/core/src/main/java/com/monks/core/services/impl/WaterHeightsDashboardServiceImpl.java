package com.monks.core.services.impl;

import com.day.crx.JcrConstants;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.monks.core.services.WaterHeightsDashboardService;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Component(service = WaterHeightsDashboardService.class, immediate = true)
@Designate(ocd = WaterHeightsDashboardServiceImpl.Config.class)
public class WaterHeightsDashboardServiceImpl implements WaterHeightsDashboardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaterHeightsDashboardService.class);
    private static final String CANAL_WATER_DATA_KEY = "canalWaterData";
    public static final String DATA = "data";
    public static final String DEFAULT_VALUE = "[]";
    private String dataStorePath;
    private int cacheSize;
    private Cache<String, String> cachedCanalData;

    @ObjectClassDefinition(name = "Monks Water Heights API Configuration")
    public @interface Config {

        @AttributeDefinition(name = "Configure the Data Store Path")
        String dataStorePath() default "/etc/waterHeightsData";

        @AttributeDefinition(name = "Maximum Cache Size", description = "Maximum number of records that can be cached")
        int cacheSize() default 5;
    }

    @Activate
    public final void activate(final WaterHeightsDashboardServiceImpl.Config config){
        dataStorePath = config.dataStorePath();
        cacheSize = config.cacheSize();
        cachedCanalData = CacheBuilder
                .newBuilder()
                .maximumSize(cacheSize)
                .build();
    }

    @Override
    public String fetchData(ResourceResolver resourceResolver) {
        String data = DEFAULT_VALUE;
        JsonArray resultJsonArray = new JsonArray();
        try {
            Resource dataStoreResource = ResourceUtil.getOrCreateResource(resourceResolver, dataStorePath, JcrConstants.NT_UNSTRUCTURED, null, true);
            JsonObject canalDataJson = new Gson().fromJson(String.valueOf(dataStoreResource.getValueMap().getOrDefault(CANAL_WATER_DATA_KEY, DEFAULT_VALUE)), JsonObject.class);
            resultJsonArray = canalDataJson.has(DATA) ? canalDataJson.getAsJsonArray(DATA): new JsonArray();
        } catch (PersistenceException e) {
            LOGGER.error("***** WaterHeightsDashboardServiceImpl :: fetchData :: Persistence Exception Caught  *****", e);
        }
        data = resultJsonArray.toString();
        return data;
    }

    @Override
    public String fetchCachedData(ResourceResolver resourceResolver) {
        String data = DEFAULT_VALUE;
        try {
            data = cachedCanalData.get("canalData", new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return fetchData(resourceResolver);
                }
            });
        } catch (ExecutionException e) {
            LOGGER.error("***** WaterHeightsDashboardServiceImpl :: fetchCachedData :: Execution Exception Caught  *****", e);
        }
        return data;
    }

    @Override
    public String addData(ResourceResolver resourceResolver, String date, long height, long id) {
        Gson gson = new Gson();
        JsonObject newCanalData = new JsonObject();
        try {
            if (StringUtils.isNotEmpty(date) && height > 0) {
                Resource dataResource = resourceResolver.getResource(dataStorePath);
                if (dataResource != null) {
                    ModifiableValueMap dataModifiableValueMap = dataResource.adaptTo(ModifiableValueMap.class);
                    JsonObject canalJsonObject = gson.fromJson(String.valueOf(dataModifiableValueMap.getOrDefault(CANAL_WATER_DATA_KEY, "{}")), JsonObject.class);
                    if (canalJsonObject.has(DATA)) {
                        newCanalData.addProperty("date", date);
                        newCanalData.addProperty("height", height);
                        newCanalData.addProperty("createdDate", LocalDate.now().toString());
                        newCanalData.addProperty("id", id);
                        JsonArray canalDataArray = canalJsonObject.getAsJsonArray(DATA);
                        canalDataArray.add(newCanalData);
                        canalJsonObject.add(DATA, canalDataArray);
                        dataModifiableValueMap.put(CANAL_WATER_DATA_KEY, canalJsonObject.toString());
                    } else {
                        JsonObject canalData = new JsonObject();
                        newCanalData.addProperty("date", date);
                        newCanalData.addProperty("height", height);
                        newCanalData.addProperty("createdDate", LocalDate.now().toString());
                        newCanalData.addProperty("id", id);
                        JsonArray canalDataArray = new JsonArray();
                        canalDataArray.add(newCanalData);
                        canalData.add(DATA, canalDataArray);
                        dataModifiableValueMap.put(CANAL_WATER_DATA_KEY, canalData.toString());
                    }
                    dataResource.getResourceResolver().commit();
                    flushCacheData();
                }
            }
        } catch (PersistenceException e) {
            LOGGER.error("***** WaterHeightsDashboardServiceImpl :: addData :: Persistence Exception Caught  *****", e);
        }
        return newCanalData.toString();
    }

    @Override
    public void flushCacheData() {
        cachedCanalData.invalidateAll();
    }
}
