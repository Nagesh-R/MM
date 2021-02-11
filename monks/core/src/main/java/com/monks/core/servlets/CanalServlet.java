package com.monks.core.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.monks.core.services.WaterHeightsDashboardService;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(
        resourceTypes = "monks/components/canaltable",
        methods = HttpConstants.METHOD_GET,
        selectors = "api",
        extensions = "json")
@ServiceDescription("Canal Table API")
public class CanalServlet extends SlingSafeMethodsServlet {

    @Reference
    WaterHeightsDashboardService dashboardService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        long minWaterLimit = NumberUtils.toLong((String) request.getResource().getValueMap().getOrDefault("minWaterHeight", "30"));
        String canalData = dashboardService.fetchCachedData(request.getResourceResolver());
        Gson gson = new Gson();
        JsonArray canalDataJsonArray = gson.fromJson(canalData, JsonArray.class);
        JsonArray resultJsonArray = new JsonArray();
        response.setContentType("application/json");
        int size = canalDataJsonArray.size() < 20 ? canalDataJsonArray.size() : 20;
        for(int index = 1; index <= size; index ++ ){
            if(canalDataJsonArray.get(canalDataJsonArray.size()-index).getAsJsonObject().get("height").getAsDouble() > minWaterLimit) {
                resultJsonArray.add(canalDataJsonArray.get(canalDataJsonArray.size()-index));
            }
        }
        response.getWriter().print(resultJsonArray.toString());
    }
}
