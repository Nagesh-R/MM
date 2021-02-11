# Water Heights Configuration

In this component the Canal Water heights can be configured by the Author in the Dashboard. This is under component group "hidden"

## Service

This component has a OSGI service named WaterHeightsDashboardService (core/services) and it consists of the following

- Methods to fetch and save data under "/etc/waterHeightsData"
- Service related configurations are made available as OSGI configurations
- Guava cache has been used as in-memory cache to save data and avoid multiple node reads

    ###### configurable properties in OSGI
    
       1. Path to save Canal Data
       2. Cache size (cache is invalidated every time a new entry is added)
       

## Servlet 

This component uses a servlet (CanalWaterDashboardServlet under /core/servlets)

- Ajax call is made to this resource based servlet and then using the service reference the data is fetched and passed to frontend

## JAVA Files related to the component

    1. src/main/java/com/monks/core/services/WaterHeightsDashboardService.java
    2. src/main/java/com/monks/core/services/impl/WaterHeightsDashboardServiceImpl.java
    3. src/main/java/com/monks/core/servlets/CanalWaterDashboardServlet.java

#### Options that author would still require

- Delete/Edit record option
- publish option to publish the data node 

 