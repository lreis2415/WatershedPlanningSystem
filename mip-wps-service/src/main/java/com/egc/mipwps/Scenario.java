package com.egc.mipwps;
import com.alibaba.fastjson.annotation.JSONField;

public class Scenario {
    @JSONField
    private int generation;
    @JSONField
    private int scenarioID;
    @JSONField
    private float economy;
    @JSONField
    private float environment;
    @JSONField
    private Float[] geneValues;

    public Scenario(int generation, int scenarioID, float economy, float environment, Float[] geneValues){

        this.setGeneration(generation);
        this.setScenarioID(scenarioID);
        this.setEconomy(economy);
        this.setEnvironment(environment);
        this.setGeneValues(geneValues);
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
}
