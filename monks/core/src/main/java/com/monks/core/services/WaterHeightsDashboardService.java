package com.monks.core.services;

import org.apache.sling.api.resource.ResourceResolver;

public interface WaterHeightsDashboardService {
    String fetchData(ResourceResolver resourceResolver);
    String fetchCachedData(ResourceResolver resourceResolver);
    String addData(ResourceResolver resourceResolver, String date, long height, long id);
    void flushCacheData();
}
