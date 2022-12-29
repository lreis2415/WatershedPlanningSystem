package com.egc.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OptimizationRepository extends JpaRepository<Optimization, Integer> {
    Optimization findOptimizationByUserGroupIdAndName(int userId, String name);
    Optimization findOptimizationByUserGroupIdAndId(int userId, int id);
    Optimization findOptimizationByUserGroupIdAndIdAndMode(int userId, int id, String mode);
    List<Optimization> findOptimizationsByUserGroupId(int userId);
    List<Optimization> findOptimizationsByUserGroupIdAndProgress(int userId, int progress);
    List<Optimization> findOptimizationsByUserGroupIdAndProgressAndMode(int userId, int progress,String mode);
    void deleteOptimizationById(int optId);
}
