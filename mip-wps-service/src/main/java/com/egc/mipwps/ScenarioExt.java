package com.egc.mipwps;

import com.alibaba.fastjson.annotation.JSONField;

public class ScenarioExt {
    @JSONField
    private int generation;
    @JSONField
    private int scenarioID;
    @JSONField
    private float economy;
    @JSONField
    private float economyCost;
    @JSONField
    private float environment;
    @JSONField
    private Float[] geneValues;
    @JSONField
    private float sedSum;
    @JSONField
    private Float[] sedPerPeriod;
    @JSONField
    private Float[] netcostPerPeriod;
    @JSONField
    private Float[] costPerPeriod;
    @JSONField
    private Float[] incomePerPeriod;

    public ScenarioExt() {
    }

    public ScenarioExt(int generation, int scenarioID, float economy, float economyCost, float environment, float sedSum,
                       Float[] sedPerPeriod, Float[] netcostPerPeriod, Float[] costPerPeriod, Float[] incomePerPeriod,
                       Float[] geneValues) {
        this.generation = generation;
        this.scenarioID = scenarioID;
        this.economy = economy;
        this.economyCost = economyCost;
        this.environment = environment;
        this.geneValues = geneValues;
        this.sedSum = sedSum;
        this.sedPerPeriod = sedPerPeriod;
        this.netcostPerPeriod = netcostPerPeriod;
        this.costPerPeriod = costPerPeriod;
        this.incomePerPeriod = incomePerPeriod;
    }

    // compatible with PlotApplication
    public ScenarioExt(int generation, int scenarioID, float economy, float environment, float sedSum,
                       Float[] sedPerPeriod, Float[] netcostPerPeriod, Float[] costPerPeriod, Float[] incomePerPeriod,
                       Float[] geneValues) {
        this.generation = generation;
        this.scenarioID = scenarioID;
        this.economy = economy;
        this.environment = environment;
        this.geneValues = geneValues;
        this.sedSum = sedSum;
        this.sedPerPeriod = sedPerPeriod;
        this.netcostPerPeriod = netcostPerPeriod;
        this.costPerPeriod = costPerPeriod;
        this.incomePerPeriod = incomePerPeriod;
    }

    public Float[] getSedPerPeriod() {
        return sedPerPeriod;
    }

    public void setSedPerPeriod(Float[] sedPerPeriod) {
        this.sedPerPeriod = sedPerPeriod;
    }


    public Float[] getNetcostPerPeriod() {
        return netcostPerPeriod;
    }

    public void setNetcostPerPeriod(Float[] netcostPerPeriod) {
        this.netcostPerPeriod = netcostPerPeriod;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public int getScenarioID() {
        return scenarioID;
    }

    public void setScenarioID(int scenarioID) {
        this.scenarioID = scenarioID;
    }

    public float getEconomy() {
        return economy;
    }

    public void setEconomy(float economy) {
        this.economy = economy;
    }

    public float getEnvironment() {
        return environment;
    }

    public void setEnvironment(float environment) {
        this.environment = environment;
    }

    public Float[] getGeneValues() {
        return geneValues;
    }

    public void setGeneValues(Float[] geneValues) {
        this.geneValues = geneValues;
    }

    public float getSedSum() {
        return sedSum;
    }

    public void setSedSum(float sedSum) {
        this.sedSum = sedSum;
    }

    public Float[] getCostPerPeriod() {
        return costPerPeriod;
    }

    public void setCostPerPeriod(Float[] costPerPeriod) {
        this.costPerPeriod = costPerPeriod;
    }

    public Float[] getIncomePerPeriod() {
        return incomePerPeriod;
    }

    public void setIncomePerPeriod(Float[] incomePerPeriod) {
        this.incomePerPeriod = incomePerPeriod;
    }

    public float getEconomyCost() {
        return economyCost;
    }

    public void setEconomyCost(float economyCost) {
        this.economyCost = economyCost;
    }

}
