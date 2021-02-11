package com.monks.core.servlets;

import com.monks.core.services.WaterHeightsDashboardService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(
        resourceTypes = "monks/components/waterheightsconfiguration",
        methods = {HttpConstants.METHOD_GET, HttpConstants.METHOD_POST},
        selectors = {"add","fetch","delete"},
        extensions = "json")
@ServiceDescription("Weather Dashboard API")
public class CanalWaterDashboardServlet extends SlingAllMethodsServlet {

    @Reference
    WaterHeightsDashboardService dashboardService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        String selector = request.getRequestPathInfo().getSelectors().length > 0 ?  request.getRequestPathInfo().getSelectors()[0] : StringUtils.EMPTY;
        if(StringUtils.isNotEmpty(selector) && selector.equals("fetch")){
            response.setContentType("application/json");
            response.getWriter().print(dashboardService.fetchData(request.getResourceResolver()));
        }
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        String selector = request.getRequestPathInfo().getSelectors().length > 0 ?  request.getRequestPathInfo().getSelectors()[0] : StringUtils.EMPTY;
        ResourceResolver resourceResolver = request.getResourceResolver();
        response.setContentType("application/json");
        String data = "{}";
        switch(selector) {
            case "add" :
                if(StringUtils.isNotEmpty(request.getParameter("date"))
                        && StringUtils.isNotEmpty(request.getParameter("height"))
                        && StringUtils.isNotEmpty(request.getParameter("id"))){
                     data = dashboardService.addData(resourceResolver, request.getParameter("date"),
                            NumberUtils.toLong(request.getParameter("height")),
                            NumberUtils.toLong(request.getParameter("id")));
                }
                break;
        }
        response.getWriter().print(data);
    }
}
