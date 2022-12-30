package com.egc.mipwps;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@RestController
public class BMPOrdersOptApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BMPOrdersOptApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(BMPOrdersOptApplication.class, args);
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setIncludeHeaders(true);
        return loggingFilter;
    }

    // @Bean
    // public WebMvcConfigurer corsConfigurer() {
    // return new WebMvcConfigurer() {
    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    // registry.addMapping("/**").allowedOrigins("http://localhost:8080");
    // }
    // };
    // }

    public final String SEIMS_ROOT = "D:/Programs/SEIMS/";
    public final String SEIMS_MODEL_PATH = SEIMS_ROOT + "data/youwuzhen/ss_youwuzhen10m_longterm_model/";
    public final String SEIMS_MODEL_PATH_30 = SEIMS_ROOT + "data/youwuzhen30/demo_youwuzhen30m_longterm_model/";
    // public final String SEIMS_SA_CONF_FILE =
    // SEIMS_ROOT+"data/youwuzhen/workspace/scenario_analysis_bmps_order.ini";
    // public final String SEIMS_WORKING_DIR = SEIMS_ROOT +
    // "seims/scenario_analysis/spatialunits/";
    // public final String SEIMS_SA_SCRIPT_FILE = SEIMS_ROOT +
    // "seims/scenario_analysis/spatialunits/bmps_order_nsga2.py";
    // public final String PYTHON_EXE = "D:/Anaconda3/envs/seims-py27/python.exe";
    // public final Float[] BASE_SEDIMENTS = {1347426f, 818153f, 1009664f, 3634481f,
    // 1632739f};
    // public final float BASE_SEDIMENT_AVG = 1688493f;
    // public final Float[] BASE_SEDIMENTS_CUMUL = cumulativeList(BASE_SEDIMENTS);

    @Autowired
    public StakeholderRepository userRepository;
    @Autowired
    public SthdGroupRepository groupRepository;
    @Autowired
    public OptimizationRepository optimizationRepository;
    @Autowired
    public JavaMailSender mailSender;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "Please Login.";
    }

    @RequestMapping(value = "/stakeholderGroups", method = RequestMethod.GET)
    public JSONObject getStakeholderGroups() {
        List<SthdGroup> groups = groupRepository.findAll();
        JSONObject obj = new JSONObject();
        if(groups.size() == 0){
            obj.put("code", 0);
            obj.put("data", groups);
        }else{            
            obj.put("code", 1);
            obj.put("data", groups);
        }
        return obj;
    }

    @RequestMapping(value = "/addGroup", method = RequestMethod.GET)
    public JSONObject addGroup(String name) {
        JSONObject obj = new JSONObject();
        SthdGroup group = groupRepository.findByName(name);
        if(group == null){
            SthdGroup newGroup = groupRepository.save(new SthdGroup(name));
            obj.put("code", 1);
            obj.put("data", newGroup);
        }else{
            obj.put("code", 0);
            obj.put("data", null);
        }
        return obj;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public JSONObject login(String email, String password) {
        Stakeholder user = userRepository.findUserByEmail(email);
        JSONObject obj = new JSONObject();
        if (user == null) {
            obj.put("code", 0);
            obj.put("message", "User doesn't exist!");
        } else {
            if (!password.equals(user.getPassword())) {
                obj.put("code", 0);
                obj.put("message", "Password doesn't match!");
            } else {
                obj.put("code", 1);
                obj.put("data", user);
                obj.put("message", "Login success!");
            }
        }
        return obj;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public JSONObject register(@RequestParam int groupId, @RequestParam String name, @RequestParam String password,
            @RequestParam String email, @RequestParam String role) {
        Stakeholder foundUser = userRepository.findUserByEmail(email);
        JSONObject obj = new JSONObject();
        if (foundUser != null) {
            obj.put("code", 0);
            obj.put("message", "Stakeholder exists. Please login.");
        } else {
            obj.put("code", 1);
            obj.put("message", "Register success.");
            Stakeholder added = userRepository.save(new Stakeholder(groupId, name, password, email, role));
            obj.put("data", added);
        }
        return obj;
    }

    @RequestMapping(value = "/validateOptName", method = RequestMethod.GET)
    public JSONObject validateOptName(int groupId, String optName) {
        Optimization optimizationObj = optimizationRepository.findOptimizationByUserGroupIdAndName(groupId, optName);
        JSONObject obj = new JSONObject();
        if (optimizationObj == null) {
            obj.put("code", 1);
            obj.put("message", "It's OK to use this name.");
        } else {
            obj.put("code", 0);
            obj.put("data", "Optimization Name has existed! Please set a unique name.");
        }
        return obj;
    }

    @RequestMapping(value = "/selectedScenario", method = RequestMethod.GET)
    public JSONObject getSelectedScenario() {
        return readJsonFile(SEIMS_MODEL_PATH + "Scenario_Selected.json");
    }

    @RequestMapping(value = "/submitBMPOrdersOptimizationOnline", method = RequestMethod.POST)
    public JSONObject submitBMPordersOptimizationOnline(@RequestBody Optimization optimization) {
        Optimization optimizationObj = optimizationRepository.save(optimization);
        String outputPath = "group" + optimizationObj.getUserGroupId() + "_opt" + optimizationObj.getId()
                + "/SA_NSGA2_SLPPOS_HILLSLP_Gen_" + optimizationObj.getGeneration() + "_Pop_"
                + optimizationObj.getPopulation() + "/";
        optimizationObj.setOutputPath(outputPath);
        optimizationObj.setProgress(1);
        optimizationRepository.save(optimizationObj);

        JSONObject obj = new JSONObject();
        int gen = optimization.getGeneration();
        int pop = optimization.getPopulation();
        // online mode
        // send request: start online optimization
        int groupID = optimizationObj.getUserGroupId();
        int optID = optimizationObj.getId();
        Mono<String> mono = WebClient.create("http://localhost:5000")
                .get().uri(uriBuilder -> uriBuilder.path("/onlineOptimization")
                        .queryParam("groupID", groupID)
                        .queryParam("optID", optID)
                        .build())
                .retrieve().bodyToMono(String.class);
        String result = mono.block();
        System.out.print(result);
        ServiceResponse resObj = JSON.parseObject(result, ServiceResponse.class);
        if (resObj.getResultCode() == 1) {
            obj.put("code", 1);
            obj.put("message", "Optimize success!");
        } else {// request failed
            obj.put("code", 0);
            obj.put("data", "Optimize fail!");
        }
        return obj;
    }

    @RequestMapping(value = "/submitBMPOrdersOptimizationOffline", method = RequestMethod.POST)
    public JSONObject submitBMPordersOptimizationOffline(@RequestBody Optimization optimization) {
        Optimization optimizationObj = optimizationRepository.save(optimization);
        String outputPath = "group" + optimizationObj.getUserGroupId() + "_opt" + optimizationObj.getId()
                + "/SA_NSGA2_SLPPOS_HILLSLP_Gen_" + optimizationObj.getGeneration() + "_Pop_"
                + optimizationObj.getPopulation() + "/";
        optimizationObj.setOutputPath(outputPath);
        optimizationRepository.save(optimizationObj);

        JSONObject obj = new JSONObject();
        // offline mode
        // send email to administrator
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("rubikshen@126.com");
        msg.setTo("shenshen@lreis.ac.cn");
        msg.setSubject("Youwuzhen project: User has submitted optimization task!");
        msg.setText("User group: " + optimizationObj.getUserGroupId()
                + "  has submitted optimization task! Please start manually!");
        mailSender.send(msg);
        obj.put("code", 1);
        obj.put("message", "The administrator has been notified! Please wait patiently for the results.");
    
        return obj;
    }

    @RequestMapping(value = "/hasHistoricalOptimizations", method = RequestMethod.GET)
    public JSONObject hasHistoricalOptimizations(int groupId) {
        List<Optimization> opts = optimizationRepository.findOptimizationsByUserGroupIdAndProgress(groupId,1);
        JSONObject obj = new JSONObject();
        if(opts.size()!=0){
            obj.put("code", 1);
            obj.put("message", "Exists historical optimizations!");
        }else{
            obj.put("code", 0);
            obj.put("message", "Do not have historical optimizations!");
        }
        return obj;
    }

    @RequestMapping(value = "/historicalOptimizations", method = RequestMethod.GET)
    public JSONObject getHistoricalOptimizations(int groupId, String mode) {
        List<Optimization> opts = optimizationRepository.findOptimizationsByUserGroupIdAndProgressAndMode(groupId,1, mode);
        //List<Optimization> opts = optimizationRepository.findOptimizationsByUserGroupIdAndProgress(groupId, 1);
        JSONObject obj = new JSONObject();
        obj.put("code", 1);
        obj.put("message", "Get historical optimizations successfully!");
        obj.put("data", opts);
        return obj;
    }

    @RequestMapping(value = "/deleteOptimization", method = RequestMethod.GET)
    public JSONObject deleteOptimization(int optId) {
        optimizationRepository.deleteOptimizationById(optId);
        JSONObject obj = new JSONObject();
        obj.put("code", 1);
        obj.put("message", "Delete optimization successfully!");
        return obj;
    }

    @RequestMapping(value = "/getParetoFront", method = RequestMethod.GET)
    public JSONObject getParetoFront(int groupId, String optIds, String mode) {
        List<JSONObject> scenariosAll = new ArrayList<>();
        String[] idsStrArr = optIds.split(",");
        for (int i = 0; i < idsStrArr.length; i++) {
            int optId = Integer.parseInt(idsStrArr[i]);
            //Optimization opt = optimizationRepository.findOptimizationByUserGroupIdAndId(groupId, optId);
            Optimization opt = optimizationRepository.findOptimizationByUserGroupIdAndIdAndMode(groupId, optId, mode);
            String logfilename;
            if(mode.equals("offline") ){
                logfilename = SEIMS_MODEL_PATH + opt.getOutputPath() + "runtime.log";
            }else{
                logfilename = SEIMS_MODEL_PATH_30 + opt.getOutputPath() + "runtime.log";
            }             
            List<ScenarioExt> scenarios = ReadParetoFrontInRuntimeLog(logfilename, opt.getGeneration(),
                    opt.getDiscountRate());
            int userId = opt.getUserId();
            Stakeholder sthd = userRepository.findById(userId);
            JSONObject obj = new JSONObject();
            obj.put("stakeholderName",sthd.getName());
            obj.put("stakeholderRole",sthd.getRole());
            obj.put("optName", opt.getName());
            obj.put("optId", opt.getId());
            obj.put("scenarios", scenarios);
            scenariosAll.add(obj);
        }
        JSONObject obj = new JSONObject();
        obj.put("code", 1);
        obj.put("data", scenariosAll);
        obj.put("message", "Get Pareto Fronts successfully!");
        return obj;
    }

    @RequestMapping(value = "/getScenarioByID", method = RequestMethod.GET)
    public JSONObject getScenarioByID(int groupId, int optId, int scenarioId, String mode) {
        String scenarioFilename;
        if(mode.equals("offline")){
            scenarioFilename = SEIMS_MODEL_PATH + "group" + groupId + "_opt" + optId + "/Scenarios/Scenario_"
                + scenarioId + ".json";
        }else{
            scenarioFilename = SEIMS_MODEL_PATH_30 + "group" + groupId + "_opt" + optId + "/Scenarios/Scenario_"
            + scenarioId + ".json";
        }
        JSONObject sceObj = readJsonFile(scenarioFilename);
        return sceObj;
    }

    @RequestMapping(value = "/getCompareData", method = RequestMethod.GET)
    public JSONObject getCompareData(int groupId) {
        String compareDataFile = SEIMS_MODEL_PATH + "group" + groupId + "_RoadmapCompare.json";
        Path path = Paths.get(compareDataFile);
        Stream<String> lines = null;
        try {
            lines = Files.lines(path);
            String data = lines.collect(Collectors.joining("\n"));
            lines.close();
            RoadmapsCompare roadmapsCompare = JSONObject.parseObject(data, RoadmapsCompare.class);
            JSONObject result = new JSONObject();
            result.put("code", 1);
            result.put("message", "Get data success!");
            result.put("data", roadmapsCompare);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            JSONObject result = new JSONObject();
            result.put("code", 0);
            result.put("message", "get data error!");
            return result;
        }
    }

    @RequestMapping(value = "/saveCompareData", method = RequestMethod.POST)
    public JSONObject saveCompareData(@RequestBody RoadmapsCompare roadmapsCompare) {
        String roadmapFilename = SEIMS_MODEL_PATH + "group" + roadmapsCompare.getGroupId() + "_RoadmapCompare.json";
        String jsonStr = JSON.toJSONString(roadmapsCompare);

        try {
            FileOutputStream outputStream = new FileOutputStream(roadmapFilename);
            byte[] strToBytes = jsonStr.getBytes();
            outputStream.write(strToBytes);
            outputStream.close();
            JSONObject result = new JSONObject();
            result.put("code", 1);
            result.put("message", "Submit success!");
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            JSONObject result = new JSONObject();
            result.put("code", 0);
            result.put("message", "Submit error!");
            return result;
        }
    }

    public JSONObject readJsonFile(String filePath) {
        BufferedReader reader = null;
        StringBuilder readJson = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                readJson.append(tempString);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        JSONObject jsonObject = JSONObject.parseObject(readJson.toString());
        return jsonObject;
    }

    public List<ScenarioExt> ReadParetoFrontInRuntimeLog(String filename, int gen, float discountRate) {
        BufferedReader br;
        String str;
        List<ScenarioExt> scenarios = new ArrayList<ScenarioExt>();
        try {
            br = new BufferedReader(new FileReader(filename));
            while ((str = br.readLine()) != null) {
                if (!str.startsWith("#"))
                    continue;
                if (!str.contains("Generation: " + gen))
                    continue;
                br.readLine(); // ignore column title
                while ((str = br.readLine()) != null) {
                    if (str.startsWith("#"))
                        break;
                    String[] items = str.split("\t");
                    int generation = Integer.parseInt(items[0]);
                    int scenarioID = Integer.parseInt(items[1]);
                    float economy = Float.parseFloat(items[2]);
                    float environment = Float.parseFloat(items[3]);
                    float sedSum = Float.parseFloat(items[4]);// it's sed avg, actually.

                    // sed per period
                    List<Float> sedList = new ArrayList<>();
                    // List<Float> sedCumulativeList = new ArrayList<>();
                    // List<Float> reductionRateList = new ArrayList<>();
                    // float cumulative = 0.0f;
                    String sedPP = items[5].substring(1, items[5].length() - 1);
                    String[] sedPPItems = sedPP.split(",");
                    for (int i = 0; i < sedPPItems.length; i++) {
                        float temp = Float.parseFloat(sedPPItems[i]);
                        sedList.add(temp);
                        // cumulative += temp;
                        // sedCumulativeList.add(cumulative);
                        // reductionRateList.add((BASE_SEDIMENTS_CUMUL[i] - cumulative) * 100 /
                        // BASE_SEDIMENTS_CUMUL[i]);
                    }

                    // net cost per period
                    List<Float> netcostList = new ArrayList<>();
                    // List<Float> netcostCumulativeList = new ArrayList<>();
                    // cumulative = 0.0f;
                    String netcostPP = items[6].substring(1, items[6].length() - 1);
                    for (String s : netcostPP.split(",")) {
                        float temp = Float.parseFloat(s);
                        netcostList.add(temp);
                        // cumulative += temp;
                        // netcostCumulativeList.add(cumulative);
                    }

                    // cost per period
                    List<Float> costList = new ArrayList<>();
                    String costPP = items[7].substring(1, items[7].length() - 1);
                    for (String s : costPP.split(",")) {
                        float temp = Float.parseFloat(s);
                        costList.add(temp);
                    }
                    float discountedCost = getDiscountedValue(costList, discountRate);

                    // income per period
                    List<Float> incomeList = new ArrayList<>();
                    String incomePP = items[8].substring(1, items[8].length() - 1);
                    for (String s : incomePP.split(",")) {
                        float temp = Float.parseFloat(s);
                        incomeList.add(temp);
                    }

                    // gene values
                    List<Float> geneValues = new ArrayList<Float>();
                    String genes = items[9].substring(12, items[9].length() - 2);
                    for (String s : genes.split(",")) {
                        geneValues.add(Float.parseFloat(s));
                    }
                    ScenarioExt scenario = new ScenarioExt(generation, scenarioID, economy, discountedCost, environment,
                            sedSum,
                            sedList.toArray(new Float[0]), netcostList.toArray(new Float[0]),
                            costList.toArray(new Float[0]),
                            incomeList.toArray(new Float[0]), geneValues.toArray(new Float[0]));
                    scenarios.add(scenario);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scenarios;
    }

    public Float[] cumulativeList(Float[] list) {
        Float[] results = new Float[list.length];
        for (int i = 0; i < list.length; i++) {
            if (i == 0) {
                results[i] = list[i];
            } else {
                results[i] = results[i - 1] + list[i];
            }
        }
        return results;
    }

    public float getDiscountedValue(List<Float> costList, float discountRate) {
        float discountedValue = 0.0f;
        for (int i = 0; i < costList.size(); i++) {
            discountedValue += costList.get(i) / Math.pow((1.0f + discountRate / 100.0f), (i + 1));
        }
        return discountedValue;
    }
}
