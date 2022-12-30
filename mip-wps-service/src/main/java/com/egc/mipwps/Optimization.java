package com.egc.mipwps;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "optimization")
public class Optimization {
    public Optimization() {
    }

    @Id
    @GeneratedValue
    public int id;
    private String name;
    private int userId;
    private int userGroupId;
    private String submitTime;
    private int investType;
    private String stepwiseInvestMin;
    private String stepwiseInvestMax;
    private int discountRate;
    private int bmpEffectType;
    private String objective1;
    private String objective2;
    private String algorithm;
    private int generation;
    private int population;
    private float crossoverRate;
    private float mutateRate;
    private float selectRate;
    private int progress;
    private String outputPath;
    private String notes;
    private String mode;

    public Optimization(String name, int userId, int userGroupId, String submitTime, int investType, String stepwiseInvestMin,
                        String stepwiseInvestMax, int discountRate, int bmpEffectType, String objective1, String objective2, String algorithm,
                        int generation, int population, float crossoverRate, float mutateRate, float selectRate, String notes, String mode) {
        this.name = name;
        this.userId = userId;
        this.userGroupId = userGroupId;
        this.submitTime = submitTime;
        this.investType = investType;
        this.stepwiseInvestMin = stepwiseInvestMin;
        this.stepwiseInvestMax = stepwiseInvestMax;
        this.discountRate = discountRate;
        this.bmpEffectType = bmpEffectType;
        this.objective1 = objective1;
        this.objective2 = objective2;
        this.algorithm = algorithm;
        this.generation = generation;
        this.population = population;
        this.crossoverRate = crossoverRate;
        this.mutateRate = mutateRate;
        this.selectRate = selectRate;
        this.notes = notes;
        this.mode = mode;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public int getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(int userGroupId) {
        this.userGroupId = userGroupId;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public int getInvestType() {
        return investType;
    }

    public void setInvestType(int investType) {
        this.investType = investType;
    }

    public String getStepwiseInvestMin() {
        return stepwiseInvestMin;
    }

    public void setStepwiseInvestMin(String stepwiseInvestMin) {
        this.stepwiseInvestMin = stepwiseInvestMin;
    }

    public String getStepwiseInvestMax() {
        return stepwiseInvestMax;
    }

    public void setStepwiseInvestMax(String stepwiseInvestMax) {
        this.stepwiseInvestMax = stepwiseInvestMax;
    }

    public int getBmpEffectType() {
        return bmpEffectType;
    }

    public void setBmpEffectType(int bmpEffectType) {
        this.bmpEffectType = bmpEffectType;
    }

    public String getObjective1() {
        return objective1;
    }

    public void setObjective1(String objective1) {
        this.objective1 = objective1;
    }

    public String getObjective2() {
        return objective2;
    }

    public void setObjective2(String objective2) {
        this.objective2 = objective2;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public float getCrossoverRate() {
        return crossoverRate;
    }

    public void setCrossoverRate(float crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    public float getMutateRate() {
        return mutateRate;
    }

    public void setMutateRate(float mutateRate) {
        this.mutateRate = mutateRate;
    }

    public float getSelectRate() {
        return selectRate;
    }

    public void setSelectRate(float selectRate) {
        this.selectRate = selectRate;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public int getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(int discountRate) {
        this.discountRate = discountRate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
