package com.egc.mipwps;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class RoadmapsCompare {
    @JSONField
    private int groupId;
    @JSONField
    private boolean reachConsensus;

    public boolean isReachConsensus() {
        return reachConsensus;
    }

    public void setReachConsensus(boolean reachConsensus) {
        this.reachConsensus = reachConsensus;
    }

    public String getConsensusRoadmap() {
        return consensusRoadmap;
    }

    public void setConsensusRoadmap(String consensusRoadmap) {
        this.consensusRoadmap = consensusRoadmap;
    }

    @JSONField
    private String consensusRoadmap;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public List<RoadmapStage> getDataCompare() {
        return dataCompare;
    }

    public void setDataCompare(List<RoadmapStage> dataCompare) {
        this.dataCompare = dataCompare;
    }

    public RoadmapsCompare() {
    }

    @JSONField
    private List<RoadmapStage> dataCompare;
}
