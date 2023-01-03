# How to run server side

## Environment setup

1. **Java**: The server side is developed in Java language (JDK 1.8), please make sure you have the Java environment properly configured on your computer first.
2. **VSCode**: The system is developed using VSCode as IDE. Please make sure that it has been installed correctly.
3. **Extension Pack for Java**: This extension is necessary because it can easily provide Java IntelliSense, debugging, Maven support, project management and more.

## Import server-side project
Open folder *mip-wps-service* with VSCode and it will automatically recognize the programming language and import the server-side project.

## Run/Debug server-side project
When the project is successfully imported, it will appear in the Java Projects panel in VSCode. Click on the triangle button in the upper right corner to start the project in debug mode.


![](README_md_files/2190df70-8b15-11ed-8a86-07580e728085.jpeg?v=1&type=image)


You can also open the code [BMPOrdersOptApplication.java](https://github.com/lreis2415/WatershedPlanningSystem/blob/main/mip-wps-service/src/main/java/com/egc/mipwps/BMPOrdersOptApplication.java), where the main function located, and select Run or Debug mode to start the project.


![](README_md_files/5b43e130-8b16-11ed-8a86-07580e728085.jpeg?v=1&type=image)


The server side uses port 8081 by default, which can be modified in [application.properties](https://github.com/lreis2415/WatershedPlanningSystem/blob/main/mip-wps-service/src/main/resources/application.properties).  When successfully started, it can be accessed through the link (e.g., http://localhost:8081).

## Deploy in Apache Tomcat
The project can be packaged using the Maven command provided in VSCode to get the war package, which can then be deployed in Apache Tomcat.


![](README_md_files/20258840-8b18-11ed-8a86-07580e728085.jpeg?v=1&type=image)



## Additional configurations (optional)
The system provides an online optimization function, which requires configuring the [SEIMS](https://github.com/lreis2415/SEIMS/tree/feature/bmps-order-opt) environment and starting the services in SEIMS. The configuration of the SEIMS environment can be found in this [manual](https://github.com/lreis2415/SEIMS/blob/feature/bmps-order-opt/SEIMS-UserManual.pdf). The code for SEIMS microservices are located in [services.py](https://github.com/lreis2415/SEIMS/blob/feature/bmps-order-opt/seims/services.py). The microservices are developed based on the Flask web framework. Please make sure it is properly installed before using it. The services can be started using the following command. 
```
python services.py
```

