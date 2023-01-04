# How to customize MIP WPS using the new study area data

The system design has high flexibility and is easy to implement. The watershed model and optimization tool in the optimization suite can be replaced with components with similar functionality. Probably the most common way to customize the system is to replace the study area data. This document will guide you through customizing your watershed planning system by replacing study area data.
The MIP WPS adopted the layered browser/server (B/S) architecture. Below we will go through the points that need to be modified in each layer.

## 1. Presentation layer

The presentation layer is the most direct contact with the user and is modified mainly from the following aspects. 

### Texts

The texts displayed on the user interface are mainly defined inside [main.js](https://github.com/lreis2415/WatershedPlanningSystem/blob/main/mip-wps-web/src/main.js), which also contains a multilingual version of the texts. This file can be modified as needed to customize the interface content.

### Maps 
Maps are closely related to the study area. So the rasters, vectors, and positions data contained in the maps need to be modified. The data related to the map object in these two views, [optimize](https://github.com/lreis2415/WatershedPlanningSystem/blob/main/mip-wps-web/src/views/optimize/index.vue) and [analyze](https://github.com/lreis2415/WatershedPlanningSystem/blob/main/mip-wps-web/src/views/analysis/index.vue), need to be modified. 

## 2. Software server layer

### Optimization suite

Collecting study area data can be one of the most important and complex things in customizing a system. The watershed data in the optimization suite needs to be prepared in focus. You can refer to the [user manual](https://github.com/lreis2415/SEIMS/blob/feature/bmps-order-opt/SEIMS-UserManual.pdf) of SEIMS to prepare the data required for its execution.

### Back-end business logic
This component needs to read the model execution results, which is highly related to the location of the data. So the relevant settings need to be modified (mainly in [BMPOrdersOptApplication.java](https://github.com/lreis2415/WatershedPlanningSystem/blob/main/mip-wps-service/src/main/java/com/egc/mipwps/BMPOrdersOptApplication.java)).

## 3. Other layers

The study area data is stored and transmitted in a standard format. Hence, the data layer and hardware server layer are independent of the study area data and do not need to be modified.













