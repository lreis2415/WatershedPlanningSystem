package com.egc.mipwps;

import com.alibaba.fastjson.annotation.JSONField;

public class Hypervolume {
    @JSONField
    private int gen;

    @JSONField
    private int newModelRuns;
    @JSONField
    private float executeTimespan;
    @JSONField
    private float sumOfModelRunTimespan;
    @JSONField
    private float hypervolume;

    public Hypervolume(int gen, int newModelRuns, float executeTimespan, float sumOfModelRunTimespan, float hypervolume) {
        this.gen = gen;
        this.newModelRuns = newModelRuns;
        this.executeTimespan = executeTimespan;
        this.sumOfModelRunTimespan = sumOfModelRunTimespan;
        this.hypervolume = hypervolume;
    }

    public int getGen() {
        return gen;
    }

    public void setGen(int gen) {
        this.gen = gen;
    }

    public int getNewModelRuns() {
        return newModelRuns;
    }

    public void setNewModelRuns(int newModelRuns) {
        this.newModelRuns = newModelRuns;
    }

    public float getExecuteTimespan() {
        return executeTimespan;
    }

    public void setExecuteTimespan(float executeTimespan) {
        this.executeTimespan = executeTimespan;
    }

    public float getSumOfModelRunTimespan() {
        return sumOfModelRunTimespan;
    }

    public void setSumOfModelRunTimespan(float sumOfModelRunTimespan) {
        this.sumOfModelRunTimespan = sumOfModelRunTimespan;
    }

    public float getHypervolume() {
        return hypervolume;
    }

    public void setHypervolume(float hypervolume) {
        this.hypervolume = hypervolume;
    }
}
