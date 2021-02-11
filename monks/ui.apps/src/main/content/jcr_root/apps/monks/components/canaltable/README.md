# Canal Table

In this component the Canal Water heights are displayed in a table form
       
## Servlet 

This component uses a servlet (CanalServlet under /core/servlets)

- Ajax call is made to this resource based servlet and then using the service reference the data is fetched and passed to frontend

    ###### Alternative
    
        - Instaed on ajax call which the component currrently does can be replaced by a Sling Model soo that the page is cached and 
        ther will be no delay in displaying the data to the user. When using a Model class a we should make sure that when ever 
        canal data is updated the page cache has to be invalidated.
    

## JAVA Files related to the component

    1. src/main/java/com/monks/core/servlets/CanalServlet.java
    
#### Considerations

- Since the data is not sensitive the data node under etc should be given read access so that the data is fetched and displayed in publisher
- In case of sensitive information a system user has to be created to access the nodes in publisher and fetch the data.


 