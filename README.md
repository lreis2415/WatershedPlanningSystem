# Watershed planning system for multistage implementation plans (MIP WPS)

This repo is used to share the code of the paper From scenario to roadmap: **A web-based participatory watershed planning system for optimizing multistage implementation plans of management practice scenario under stepwise investment**. The web-based watershed planning system in the study is designed and developed with a multi-tier architecture with front and back-end separation. 

The server side is mainly used to assist diverse stakeholders in iterative optimization of roadmaps, analysis of results, and reaching a consensus. The server side integrates an optimization method for BMP implementation plans. The code is located in the folder *mip-wps-service*. See [README.md](https://github.com/lreis2415/WatershedPlanningSystem/blob/main/mip-wps-service/README.md) for detailed instructions.

The client side constructed a user-friendly interface and an iterative workflow for participatory analysis, including setting investment constraints and optimization parameters, visualizing and analyzing spatiotemporal results from multiple perspectives, and ultimately reaching a consensus. The code is located in the folder *mip-wps-web*. See [README.md](https://github.com/lreis2415/WatershedPlanningSystem/blob/main/mip-wps-web/README.md) for detailed instructions.

The system was demonstrated to optimize BMPs for soil erosion reduction in an agricultural watershed in Southeastern China to validate its iterative optimization process and the rationality and diversity of optimized roadmaps.
